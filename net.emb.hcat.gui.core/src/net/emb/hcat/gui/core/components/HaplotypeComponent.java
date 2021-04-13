package net.emb.hcat.gui.core.components;

import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import net.emb.hcat.cli.haplotype.Haplotype;
import net.emb.hcat.cli.haplotype.HaplotypeTransformer;
import net.emb.hcat.cli.sequence.Difference;
import net.emb.hcat.cli.sequence.Sequence;
import net.emb.hcat.gui.core.messages.Messages;

/**
 * Component to compare haloptyoes to each other and show their differences.
 *
 * @author OT Piccolo
 */
public class HaplotypeComponent {

	private Control control;

	private ComboViewer sequencesComboViewer;
	private ComboViewer haplotypesComboViewer;
	private GridTableViewer tableViewer;

	private List<Haplotype> haploModel;
	private Haplotype masterHaplotype;

	/**
	 * Creates this component.
	 *
	 * @param parent
	 *            The parent composite
	 * @return The top control of this component.
	 */
	public Control createComposite(final Composite parent) {
		final Composite body = new Composite(parent, SWT.NONE);
		body.setLayout(new GridLayout(2, false));

		final Label label = new Label(body, SWT.NONE);
		label.setText(Messages.HaplotypeTableComponent_MasterSequenceLabel);
		label.setLayoutData(GridDataFactory.defaultsFor(label).create());

		haplotypesComboViewer = createHaplotypesCombo(body);
		haplotypesComboViewer.getCombo().setLayoutData(GridDataFactory.defaultsFor(haplotypesComboViewer.getCombo()).create());
		sequencesComboViewer = createSequencesCombo(body);
		sequencesComboViewer.getCombo().setLayoutData(GridDataFactory.defaultsFor(sequencesComboViewer.getCombo()).exclude(true).create());

		tableViewer = createViewer(body);
		tableViewer.getGrid().setLayoutData(GridDataFactory.defaultsFor(tableViewer.getGrid()).span(2, 1).create());

		control = body;
		return body;
	}

	private ComboViewer createHaplotypesCombo(final Composite parent) {
		final ComboViewer viewer = new ComboViewer(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(final Object element) {
				return ((Haplotype) element).getName();
			}
		});
		viewer.setComparator(new ViewerComparator());
		viewer.addPostSelectionChangedListener(e -> masterHaplotypeSelected((Haplotype) haplotypesComboViewer.getStructuredSelection().getFirstElement()));
		return viewer;
	}

	private ComboViewer createSequencesCombo(final Composite parent) {
		final ComboViewer viewer = new ComboViewer(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(final Object element) {
				return ((Sequence) element).getName();
			}
		});
		viewer.setComparator(new ViewerComparator());
		viewer.addPostSelectionChangedListener(e -> masterSequenceSelected((Sequence) sequencesComboViewer.getStructuredSelection().getFirstElement()));
		return viewer;
	}

	private GridTableViewer createViewer(final Composite parent) {
		final GridTableViewer viewer = new GridTableViewer(parent);
		viewer.setContentProvider(new IStructuredContentProvider() {
			@Override
			public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
				final GridTableViewer gridViewer = (GridTableViewer) viewer;
				if (oldInput != null) {
					// Remove old columns. Skip ID and sequence column as always
					// present.
					final GridColumn[] columns = gridViewer.getGrid().getColumns();
					for (int i = 2; i < columns.length; i++) {
						columns[i].dispose();
					}
					columns[0].setFooterText(""); //$NON-NLS-1$
				}

				if (newInput != null) {
					// Add new columns. Skip ID column as always present.
					@SuppressWarnings("unchecked")
					final Map<Haplotype, Difference> map = (Map<Haplotype, Difference>) newInput;
					final SortedSet<Integer> positions = new TreeSet<>();
					map.values().stream().map(Difference::getDifferencePosition).forEach(positions::addAll);
					for (final Integer pos : positions) {
						final GridViewerColumn column = new GridViewerColumn(gridViewer, SWT.NONE);
						column.getColumn().setText(Integer.toString(pos.intValue() + 1));
						column.getColumn().setWidth(40);
						column.setLabelProvider(new ColumnLabelProvider() {
							@SuppressWarnings("unchecked")
							@Override
							public String getText(final Object element) {
								final Entry<Haplotype, Difference> entry = (Entry<Haplotype, Difference>) element;
								// Master haplotype should always show its
								// value, so it can be compared to the
								// differences.
								if (entry.getKey() == masterHaplotype) {
									return String.valueOf(masterHaplotype.getFirstSequence().getValue().charAt(pos));
								}
								return String.valueOf(entry.getValue().getDifference().charAt(pos));
							}
						});
					}
					gridViewer.getGrid().getColumn(0).setFooterText(MessageFormat.format(Messages.HaplotypeTableComponent_VariableSites, positions.size()));
				}
			}

			@Override
			public Object[] getElements(final Object inputElement) {
				return ((Map<?, ?>) inputElement).entrySet().toArray();
			}
		});

		final GridViewerColumn idColumn = new GridViewerColumn(viewer, SWT.NONE);
		idColumn.getColumn().setText(Messages.HaplotypeTableComponent_IdColumn);
		idColumn.getColumn().setWidth(150);
		idColumn.setLabelProvider(new ColumnLabelProvider() {
			@SuppressWarnings("unchecked")
			@Override
			public String getText(final Object element) {
				return ((Entry<Haplotype, ?>) element).getKey().getName();
			}
		});

		final GridViewerColumn seqNamesColumn = new GridViewerColumn(viewer, SWT.NONE);
		seqNamesColumn.getColumn().setText(Messages.HaplotypeTableComponent_SeqNamesColumn);
		seqNamesColumn.getColumn().setWidth(150);
		seqNamesColumn.setLabelProvider(new ColumnLabelProvider() {
			@SuppressWarnings("unchecked")
			@Override
			public String getText(final Object element) {
				final Haplotype haplotype = ((Entry<Haplotype, ?>) element).getKey();
				String text = haplotype.stream().map(seq -> seq.getName() == null ? Messages.HaplotypeTableComponent_NotAvailable : seq.getName()).reduce("", (a, b) -> a + "/" + b); //$NON-NLS-1$ //$NON-NLS-2$
				if (!text.isEmpty()) {
					text = text.substring(1);
				}
				return text;
			}
		});

		final Grid grid = viewer.getGrid();
		grid.setHeaderVisible(true);
		grid.setAutoHeight(true);
		grid.setFooterVisible(true);

		return viewer;
	}

	private void masterHaplotypeSelected(final Haplotype haplotype) {
		masterHaplotype = haplotype;
		tableViewer.setInput(createViewerInput(haplotype == null ? null : haplotype.getFirstSequence()));
	}

	private void masterSequenceSelected(final Sequence sequence) {
		masterHaplotype = findHaplotype(sequence);
		tableViewer.setInput(createViewerInput(sequence));
	}

	private Haplotype findHaplotype(final Sequence sequence) {
		if (sequence == null) {
			return null;
		}
		for (final Haplotype haplotype : haploModel) {
			if (haplotype.belongsToHaplotype(sequence)) {
				return haplotype;
			}
		}
		return null;
	}

	private Map<Haplotype, Difference> createViewerInput(final Sequence sequence) {
		if (sequence == null) {
			return null;
		}

		// Make sure that the haplotype that is used for comparison (master
		// haplotype) is always at first position.
		final Map<Haplotype, Difference> compared = new HaplotypeTransformer(haploModel).compareToMaster(sequence);
		final LinkedHashMap<Haplotype, Difference> input = new LinkedHashMap<>(compared.size());
		input.put(masterHaplotype, compared.get(masterHaplotype));
		input.putAll(compared);
		return input;
	}

	/**
	 * Gets the top control of this component.
	 *
	 * @return The control, or <code>null</code> if not yet created.
	 */
	public Control getControl() {
		return control;
	}

	/**
	 * Sets focus to this component.
	 */
	public void setFocus() {
		if (sequencesComboViewer != null && !sequencesComboViewer.getCombo().isDisposed()) {
			sequencesComboViewer.getCombo().setFocus();
		}
	}

	/**
	 * Displays the selection as haplotypes.
	 */
	public void selectWithHaplotypes() {
		if (!isViewerVisible(haplotypesComboViewer)) {
			final Haplotype selectedHaplo = getSelectedHaplo();
			final Sequence selectedSeq = getSelectedSeq();
			// Try to preserve selection, so that the corresponding haplotype of
			// the currently selected sequence is shown.
			if (selectedHaplo == null || !selectedHaplo.belongsToHaplotype(selectedSeq)) {
				setSelectedHaplotype(findHaplotype(selectedSeq));
			} else {
				showViewer(haplotypesComboViewer, sequencesComboViewer);
			}
		}
	}

	/**
	 * Displays the selection as sequences.
	 */
	public void selectWithSequences() {
		if (!isViewerVisible(sequencesComboViewer)) {
			final Haplotype selectedHaplo = getSelectedHaplo();
			final Sequence selectedSeq = getSelectedSeq();
			// Try to preserve selection, so that the corresponding sequence of
			// the currently selected haplotype is shown.
			if (selectedHaplo == null || selectedHaplo.belongsToHaplotype(selectedSeq)) {
				showViewer(sequencesComboViewer, haplotypesComboViewer);
			} else {
				setSelectedSequence(selectedHaplo.getFirstSequence());
			}
		}
	}

	/**
	 * Gets the selected haplotype.
	 *
	 * @return The selected haplotype, or <code>null</code> if no haplotype is
	 *         currently selected.
	 */
	public Haplotype getSelectedHaplotype() {
		if (isViewerVisible(haplotypesComboViewer)) {
			return getSelectedHaplo();
		}
		return null;
	}

	private Haplotype getSelectedHaplo() {
		final IStructuredSelection selection = (IStructuredSelection) haplotypesComboViewer.getSelection();
		return (Haplotype) selection.getFirstElement();
	}

	/**
	 * Selects the given haplotype.
	 *
	 * @param haplotype
	 *            The haplotype to select.
	 */
	public void setSelectedHaplotype(final Haplotype haplotype) {
		showViewer(haplotypesComboViewer, sequencesComboViewer);
		if (haplotype == null) {
			haplotypesComboViewer.setSelection(StructuredSelection.EMPTY, true);
		} else {
			haplotypesComboViewer.setSelection(new StructuredSelection(haplotype), true);
		}
	}

	/**
	 * Gets the selected sequence.
	 *
	 * @return The selected sequence, or <code>null</code> if no sequence is
	 *         currently selected.
	 */
	public Sequence getSelectedSequence() {
		if (isViewerVisible(sequencesComboViewer)) {
			return getSelectedSeq();
		}
		return null;
	}

	private Sequence getSelectedSeq() {
		final IStructuredSelection selection = (IStructuredSelection) sequencesComboViewer.getSelection();
		return (Sequence) selection.getFirstElement();
	}

	/**
	 * Select the given sequence.
	 *
	 * @param sequence
	 *            The sequence to select.
	 */
	public void setSelectedSequence(final Sequence sequence) {
		showViewer(sequencesComboViewer, haplotypesComboViewer);
		if (sequence == null) {
			sequencesComboViewer.setSelection(StructuredSelection.EMPTY, true);
		} else {
			sequencesComboViewer.setSelection(new StructuredSelection(sequence), true);
		}
	}

	// Switch which viewer is visible
	private void showViewer(final ComboViewer toShow, final ComboViewer toHide) {
		final Combo show = toShow.getCombo();
		final Combo hide = toHide.getCombo();
		final GridData data = (GridData) show.getLayoutData();
		if (data.exclude) {
			data.exclude = false;
			((GridData) hide.getLayoutData()).exclude = true;
			show.requestLayout();
			hide.requestLayout();
			show.setVisible(true);
			hide.setVisible(false);
		}
	}

	private boolean isViewerVisible(final ComboViewer viewer) {
		final Combo combo = viewer.getCombo();
		if (combo.isDisposed()) {
			return false;
		}
		final GridData data = (GridData) combo.getLayoutData();
		return !data.exclude;
	}

	/**
	 * Sets the haplotypes to be displayed in this component.
	 *
	 * @param haplotypes
	 *            The haplotypes to display.
	 */
	public void setModel(final List<Haplotype> haplotypes) {
		haploModel = haplotypes;
		haplotypesComboViewer.setInput(haplotypes);
		sequencesComboViewer.setInput(haplotypes == null ? null : haplotypes.stream().flatMap(h -> h.stream()).collect(Collectors.toList()).toArray());
		tableViewer.setInput(null);
	}

	/**
	 * Updates the given haplotype.
	 *
	 * @param haplotype
	 *            The haplotype that has been changed.
	 */
	public void updateHaplotype(final Haplotype haplotype) {
		if (haplotype != null && tableViewer != null && !tableViewer.getGrid().isDisposed()) {
			@SuppressWarnings("unchecked")
			final Map<Haplotype, Difference> input = (Map<Haplotype, Difference>) tableViewer.getInput();
			if (input != null) {
				for (final Entry<Haplotype, Difference> entry : input.entrySet()) {
					if (entry.getKey().equals(haplotype)) {
						tableViewer.update(entry, null);
						break;
					}
				}
			}

			haplotypesComboViewer.update(haplotype, null);
		}
	}

}
