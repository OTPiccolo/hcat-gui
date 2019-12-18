package net.emb.hcat.gui.core.layout;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.swt.layout.GridLayout;

/**
 * Builder for {@link GridLayout} objects.
 *
 * @author OT Piccolo
 * @see GridLayout
 * @see GridDataBuilder
 */
@Creatable
public class GridLayoutBuilder {

	/**
	 * Creates a simple grid layout with the given number of columns.
	 *
	 * @param columns
	 *            The number of columns in this grid layout.
	 * @return The grid layout.
	 */
	public static final GridLayout simple(final int columns) {
		return new GridLayoutBuilder().columns(columns).create();
	}

	private int columns;
	private boolean equalSize;

	/**
	 * Creates the grid layout object. Always creates a new {@link GridLayout},
	 * and retains the settings in the builder, so calling create twice in a row
	 * will create two objects with the same settings.
	 *
	 * @return The grid layout.
	 */
	public GridLayout create() {
		final GridLayout layout = new GridLayout();
		layout.numColumns = columns;
		layout.makeColumnsEqualWidth = equalSize;
		return layout;
	}

	/**
	 * The number of columns in this grid layout.
	 * 
	 * @param columns
	 *            The number of columns in this grid layout.
	 * @return This builder for chaining.
	 * @see GridLayout#numColumns
	 */
	public GridLayoutBuilder columns(final int columns) {
		this.columns = columns;
		return this;
	}

	/**
	 * All cells will be of equal size, regardless of their contents.
	 * 
	 * @return This builder for chaining.
	 * @see GridLayout#makeColumnsEqualWidth
	 */
	public GridLayoutBuilder equalSize() {
		equalSize = true;
		return this;
	}

}
