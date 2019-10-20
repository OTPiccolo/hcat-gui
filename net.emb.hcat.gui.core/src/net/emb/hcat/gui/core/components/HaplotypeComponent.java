package net.emb.hcat.gui.core.components;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import net.emb.hcat.cli.haplotype.Haplotype;
import net.emb.hcat.cli.haplotype.HaplotypeTransformer;
import net.emb.hcat.cli.sequence.Difference;
import net.emb.hcat.cli.sequence.Sequence;
import net.emb.hcat.gui.core.EventTopics;
import net.emb.hcat.gui.core.messages.Messages;

/**
 * Component to compare haloptyoes to each other and show their differences.
 *
 * @author OT Piccolo
 */
public class HaplotypeComponent {

	private Control control;

	private ComboViewer comboViewer;
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
		label.setText(Messages.HaplotypeTablePart_MasterSequenceLabel);
		label.setLayoutData(GridDataFactory.defaultsFor(label).create());

		comboViewer = createCombo(body);
		comboViewer.getCombo().setLayoutData(GridDataFactory.defaultsFor(comboViewer.getCombo()).create());

		tableViewer = createViewer(body);
		tableViewer.getGrid().setLayoutData(GridDataFactory.defaultsFor(tableViewer.getGrid()).span(2, 1).create());

		control = body;
		return body;
	}

	private ComboViewer createCombo(final Composite parent) {
		final ComboViewer viewer = new ComboViewer(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		viewer.setContentProvider(new IStructuredContentProvider() {

			@SuppressWarnings("unchecked")
			@Override
			public Object[] getElements(final Object inputElement) {
				if (inputElement == null) {
					return new Object[0];
				}
				return ((Collection<Haplotype>) inputElement).stream().flatMap(h -> h.stream()).collect(Collectors.toList()).toArray();
			}
		});
		viewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(final Object element) {
				return ((Sequence) element).getName();
			}
		});
		viewer.setComparator(new ViewerComparator());
		viewer.addPostSelectionChangedListener(e -> masterSequenceSelected((Sequence) comboViewer.getStructuredSelection().getFirstElement()));
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
					final int maxDistance = map.isEmpty() ? 0 : map.values().stream().map(Difference::getDistance).max(Comparator.naturalOrder()).get();
					final SortedSet<Integer> positions = new TreeSet<>();
					map.values().stream().map(Difference::getDifferencePosition).forEach(positions::addAll);
					for (final Integer pos : positions) {
						final GridViewerColumn column = new GridViewerColumn(gridViewer, SWT.NONE);
						column.getColumn().setText(pos.toString());
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

					// Set max distance information.
					final String footer = MessageFormat.format(Messages.HaplotypeComponent_MaxFooter, maxDistance);
					gridViewer.getGrid().getColumn(0).setFooterText(footer);
				}
			}

			@Override
			public Object[] getElements(final Object inputElement) {
				return ((Map<?, ?>) inputElement).entrySet().toArray();
			}
		});

		final GridViewerColumn idColumn = new GridViewerColumn(viewer, SWT.NONE);
		idColumn.getColumn().setText(Messages.HaplotypeTablePart_IdColumn);
		idColumn.getColumn().setWidth(150);
		idColumn.setLabelProvider(new ColumnLabelProvider() {
			@SuppressWarnings("unchecked")
			@Override
			public String getText(final Object element) {
				return ((Entry<Haplotype, ?>) element).getKey().getName();
			}
		});

		final GridViewerColumn seqNamesColumn = new GridViewerColumn(viewer, SWT.NONE);
		seqNamesColumn.getColumn().setText(Messages.HaplotypeTablePart_SeqNamesColumn);
		seqNamesColumn.getColumn().setWidth(150);
		seqNamesColumn.setLabelProvider(new ColumnLabelProvider() {
			@SuppressWarnings("unchecked")
			@Override
			public String getText(final Object element) {
				final Haplotype haplotype = ((Entry<Haplotype, ?>) element).getKey();
				String text = haplotype.stream().map(seq -> seq.getName() == null ? Messages.HaplotypeTablePart_NotAvailable : seq.getName()).reduce("", (a, b) -> a + "/" + b); //$NON-NLS-1$ //$NON-NLS-2$
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
	 * @return
	 */
	public Control getControl() {
		return control;
	}

	@Focus
	public void setFocus() {
		comboViewer.getCombo().setFocus();
	}

	@Inject
	@Optional
	public void setSelectedHaplotype(@UIEventTopic(EventTopics.SELECTED_HAPLOTYPE) final Haplotype haplotype) {
		setSelectedSequence(haplotype == null ? null : haplotype.getFirstSequence());
	}

	@Inject
	@Optional
	public void setSelectedSequence(@UIEventTopic(EventTopics.SELECTED_SEQUENCE) final Sequence sequence) {
		if (sequence == null) {
			comboViewer.setSelection(StructuredSelection.EMPTY, true);
		} else {
			comboViewer.setSelection(new StructuredSelection(sequence), true);
		}
	}

	@Inject
	@Optional
	public void setActiveHaplotypes(@UIEventTopic(EventTopics.ACTIVE_HAPLOTYPES) final List<Haplotype> haplotypes) {
		haploModel = haplotypes;
		comboViewer.setInput(haplotypes);
		tableViewer.setInput(null);
	}

	@Inject
	@Optional
	public void updateHaplotype(@UIEventTopic(EventTopics.UPDATE_HAPLOTYPE) final Haplotype haplotype) {
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
		}
	}

}
