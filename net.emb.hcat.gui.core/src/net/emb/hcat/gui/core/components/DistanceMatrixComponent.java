package net.emb.hcat.gui.core.components;

import java.util.List;

import org.eclipse.e4.ui.di.Focus;
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
import org.eclipse.swt.widgets.Control;

import net.emb.hcat.cli.haplotype.DistanceMatrix;
import net.emb.hcat.cli.haplotype.Haplotype;
import net.emb.hcat.gui.core.messages.Messages;

public class DistanceMatrixComponent {

	private Control control;
	private GridTableViewer tableViewer;

	private List<Haplotype> haploModel;

	public Control createComposite(final Composite parent) {
		final Composite body = new Composite(parent, SWT.NONE);
		body.setLayout(new GridLayout(1, false));

		tableViewer = createViewer(body);
		tableViewer.getGrid().setLayoutData(GridDataFactory.defaultsFor(tableViewer.getGrid()).create());

		control = body;
		return body;
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
						column.getColumn().setText(haplotype.getName());
						column.getColumn().setWidth(40);
						column.setLabelProvider(new ColumnLabelProvider() {
							@Override
							public String getText(final Object element) {
								Integer distance = matrix.getDistance((Haplotype) element, haplotype);
								if (distance == null) {
									distance = 0;
								}
								return distance.toString();
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
		idColumn.getColumn().setText(Messages.DistanceMatrixComponent_IdColumn);
		idColumn.getColumn().setWidth(200);
		idColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(final Object element) {
				return ((Haplotype) element).getName();
			}
		});

		final Grid grid = viewer.getGrid();
		grid.setHeaderVisible(true);
		grid.setAutoHeight(true);

		return viewer;
	}

	public Control getControl() {
		return control;
	}

	@Focus
	public void setFocus() {
		tableViewer.getGrid().setFocus();
	}

	public void setModel(final List<Haplotype> haplotypes) {
		haploModel = haplotypes;
		updateViewer();
	}

	private void updateViewer() {
		tableViewer.setInput(haploModel);
	}

}