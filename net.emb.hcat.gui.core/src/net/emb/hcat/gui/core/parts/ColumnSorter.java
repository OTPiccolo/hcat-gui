package net.emb.hcat.gui.core.parts;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

/**
 * This class enables a viewer to automatically sort by its columns. To be able
 * to do this, each column in a viewer has to use a {@link ColumnLabelProvider}.
 *
 * @author Heiko Mattes
 */
public class ColumnSorter {

	// Listener that visualizes the currently selected sorting.
	private class SortingSelectionListener extends SelectionAdapter {
		private final GridColumn column;

		public SortingSelectionListener(final GridColumn column) {
			this.column = column;
		}

		@Override
		public void widgetSelected(final SelectionEvent e) {
			switch (column.getSort()) {
			case SWT.NONE:
				column.setSort(SWT.UP);
				break;

			case SWT.DOWN:
				column.setSort(SWT.NONE);
				break;

			case SWT.UP:
				column.setSort(SWT.DOWN);
				break;

			default:
				// Should never happen.
				break;
			}

			// Deselect other columns sorting.
			for (final GridColumn col : column.getParent().getColumns()) {
				if (col != column) {
					col.setSort(SWT.NONE);
				}
			}

			setSortingColumn(column);
		}
	}

	private final GridTableViewer viewer;
	private GridColumn sortingColumn;
	private ColumnLabelProvider sortingLabelProvider;

	/**
	 * Constructor.
	 *
	 * @param viewer
	 *            The viewer to apply column sorting for.
	 */
	public ColumnSorter(final GridTableViewer viewer) {
		this.viewer = viewer;
	}

	/**
	 * Applies the sorting to the viewer. This will overwrite previously set
	 * comperators added to the viewer. It will apply a listener to each column
	 * currently used by the viewer. If columns are added later, no listener
	 * will be added to those.
	 */
	public void applySorting() {
		// Apply listener to each column to prepare sorting for it.
		for (final GridColumn col : viewer.getGrid().getColumns()) {
			col.addSelectionListener(new SortingSelectionListener(col));
		}

		// Comparator that queries the label provider of the currently used
		// column to sort.
		viewer.setComparator(new ViewerComparator() {
			@Override
			public int compare(final Viewer viewer, final Object e1, final Object e2) {
				if (sortingColumn == null) {
					// Don't sort.
					return -1;
				}

				switch (sortingColumn.getSort()) {
				case SWT.UP:
					return getComparator().compare(sortingLabelProvider.getText(e1), sortingLabelProvider.getText(e2));
				case SWT.DOWN:
					return getComparator().reversed().compare(sortingLabelProvider.getText(e1), sortingLabelProvider.getText(e2));
				default:
					// Don't sort.
					return -1;
				}
			}
		});
	}

	private void setSortingColumn(final GridColumn column) {
		sortingColumn = null;
		sortingLabelProvider = null;

		// Search the label provider for the given column.
		final GridColumn[] columns = viewer.getGrid().getColumns();
		for (int i = 0; i < columns.length; i++) {
			if (columns[i] == column) {
				sortingColumn = column;
				sortingLabelProvider = (ColumnLabelProvider) viewer.getLabelProvider(i);
			}
		}

		viewer.refresh(false);
	}

}