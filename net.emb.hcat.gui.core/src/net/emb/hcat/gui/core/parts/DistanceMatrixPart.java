package net.emb.hcat.gui.core.parts;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import net.emb.hcat.cli.Sequence;
import net.emb.hcat.cli.haplotype.DistanceMatrix;
import net.emb.hcat.cli.haplotype.Haplotype;

public class DistanceMatrixPart {

	private static final List<Sequence> loadSequences() {
		// Load your sequences.
		return null;
	}

	private GridTableViewer tableViewer;

	@Inject
	private MDirtyable dirty;

	private List<Haplotype> haploModel;

	@PostConstruct
	public void createComposite(final Composite parent) {
		parent.setLayout(new GridLayout(1, false));

		haploModel = createHaploDataModel();

		tableViewer = createViewer(parent);
		tableViewer.getGrid().setLayoutData(GridDataFactory.defaultsFor(tableViewer.getGrid()).create());

		tableViewer.setInput(haploModel);
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
					final List<Haplotype> haplotypes = (List<Haplotype>) newInput;
					final DistanceMatrix matrix = new DistanceMatrix(haplotypes);

					for (final Haplotype haplotype : haplotypes) {
						final GridViewerColumn column = new GridViewerColumn((GridTableViewer) viewer, SWT.NONE);
						column.getColumn().setText(haplotype.getFirstSequence().getName());
						column.getColumn().setWidth(40);
						column.setLabelProvider(new ColumnLabelProvider() {
							@Override
							public String getText(final Object element) {
								final Integer distance = matrix.getDistance((Haplotype) element, haplotype);
								return distance == null ? "0" : distance.toString();
							}
						});
					}
				}
			}

			@Override
			public Object[] getElements(final Object inputElement) {
				return ArrayContentProvider.getInstance().getElements(inputElement);
			}
		});

		final GridViewerColumn idColumn = new GridViewerColumn(viewer, SWT.NONE);
		idColumn.getColumn().setText("ID");
		idColumn.getColumn().setWidth(200);
		idColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(final Object element) {
				return ((Haplotype) element).getFirstSequence().getName();
			}
		});

		final Grid grid = viewer.getGrid();
		grid.setHeaderVisible(true);
		grid.setAutoHeight(true);

		return viewer;
	}

	@Focus
	public void setFocus() {
		tableViewer.getGrid().setFocus();
	}

	@Persist
	public void save() {
		dirty.setDirty(false);
	}

	private List<Haplotype> createHaploDataModel() {
		return Haplotype.createHaplotypes(loadSequences());
	}
}