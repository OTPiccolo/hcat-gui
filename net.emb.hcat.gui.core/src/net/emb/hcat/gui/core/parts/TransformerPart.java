package net.emb.hcat.gui.core.parts;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import net.emb.hcat.cli.Difference;
import net.emb.hcat.cli.Sequence;
import net.emb.hcat.cli.haplotype.Haplotype;
import net.emb.hcat.cli.haplotype.HaplotypeTransformer;

public class TransformerPart {

	private static final List<Sequence> loadSequences() {
		// Load your sequences.
		return null;
	}

	private ComboViewer comboViewer;
	private GridTableViewer tableViewer;

	@Inject
	private MDirtyable dirty;

	private List<Haplotype> haploModel;

	@PostConstruct
	public void createComposite(final Composite parent) {
		parent.setLayout(new GridLayout(2, false));

		haploModel = createHaploDataModel();

		final Label label = new Label(parent, SWT.NONE);
		label.setText("Master Sequenz:");
		label.setLayoutData(GridDataFactory.defaultsFor(label).create());

		comboViewer = createCombo(parent);
		comboViewer.getCombo().setLayoutData(GridDataFactory.defaultsFor(comboViewer.getCombo()).create());

		tableViewer = createViewer(parent);
		tableViewer.getGrid().setLayoutData(GridDataFactory.defaultsFor(tableViewer.getGrid()).span(2, 1).create());

		comboViewer.setInput(haploModel);
	}

	private ComboViewer createCombo(final Composite parent) {
		final ComboViewer viewer = new ComboViewer(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		viewer.setContentProvider(new IStructuredContentProvider() {

			@SuppressWarnings("unchecked")
			@Override
			public Object[] getElements(final Object inputElement) {
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
		viewer.addPostSelectionChangedListener(e -> tableViewer.setInput(new HaplotypeTransformer(haploModel).compareToMaster((Sequence) e.getStructuredSelection().getFirstElement())));
		return viewer;
	}

	private GridTableViewer createViewer(final Composite parent) {
		final GridTableViewer viewer = new GridTableViewer(parent);
		viewer.setContentProvider(new IStructuredContentProvider() {
			@Override
			public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
				if (oldInput != null) {
					// Remove old columns. Skip ID column as always present.
					final GridColumn[] columns = ((GridTableViewer) viewer).getGrid().getColumns();
					for (int i = 1; i < columns.length; i++) {
						columns[i].dispose();
					}
				}

				if (newInput != null) {
					// Add new columns. Skip ID column as always present.
					@SuppressWarnings("unchecked")
					final Map<Haplotype, Difference> map = (Map<Haplotype, Difference>) newInput;
					final SortedSet<Integer> positions = new TreeSet<>();
					map.values().stream().map(Difference::getDifferencePosition).forEach(positions::addAll);
					for (final Integer pos : positions) {
						final GridViewerColumn column = new GridViewerColumn((GridTableViewer) viewer, SWT.NONE);
						column.getColumn().setText(pos.toString());
						column.getColumn().setWidth(40);
						column.setLabelProvider(new ColumnLabelProvider() {
							@SuppressWarnings("unchecked")
							@Override
							public String getText(final Object element) {
								return String.valueOf(((Entry<?, Difference>) element).getValue().getDifference().charAt(pos));
							}
						});
					}
				}
			}

			@Override
			public Object[] getElements(final Object inputElement) {
				return ((Map<?, ?>) inputElement).entrySet().toArray();
			}
		});

		final GridViewerColumn idColumn = new GridViewerColumn(viewer, SWT.NONE);
		idColumn.getColumn().setText("ID");
		idColumn.getColumn().setWidth(200);
		idColumn.setLabelProvider(new ColumnLabelProvider() {
			@SuppressWarnings("unchecked")
			@Override
			public String getText(final Object element) {
				return ((Entry<Haplotype, ?>) element).getKey().getFirstSequence().getName();
			}
		});

		final Grid grid = viewer.getGrid();
		grid.setHeaderVisible(true);
		grid.setAutoHeight(true);

		return viewer;
	}

	@Focus
	public void setFocus() {
		comboViewer.getCombo().setFocus();
	}

	@Persist
	public void save() {
		dirty.setDirty(false);
	}

	private List<Haplotype> createHaploDataModel() {
		return Haplotype.createHaplotypes(loadSequences());
	}
}