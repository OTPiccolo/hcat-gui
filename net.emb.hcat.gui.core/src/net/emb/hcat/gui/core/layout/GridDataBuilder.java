package net.emb.hcat.gui.core.layout;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;

/**
 * Builder for {@link GridData} objects.
 *
 * @author OT Piccolo
 * @see GridData
 * @see GridLayoutBuilder
 */
@Creatable
public class GridDataBuilder {

	private int horizontalAlignment = SWT.BEGINNING;
	private int verticalAlignment = SWT.CENTER;
	private boolean horizontalExcessSpace;
	private boolean verticalExcessSpace;
	private int horizontalSpan = 1;
	private int verticalSpan = 1;

	/**
	 * Creates the grid data object. Always creates a new {@link GridData}, and
	 * retains the settings in the builder, so calling create twice in a row
	 * will create two objects with the same settings.
	 *
	 * @return The grid data.
	 */
	public GridData create() {
		final GridData data = new GridData();
		data.horizontalAlignment = horizontalAlignment;
		data.verticalAlignment = verticalAlignment;
		data.grabExcessHorizontalSpace = horizontalExcessSpace;
		data.grabExcessVerticalSpace = verticalExcessSpace;
		data.horizontalSpan = horizontalSpan;
		data.verticalSpan = verticalSpan;
		return data;
	}

	/**
	 * Horizontal alignment: Beginning
	 *
	 * @return This builder for chaining.
	 * @see #horizontalAlignment(int)
	 * @see GridData#horizontalAlignment
	 */
	public GridDataBuilder hBegin() {
		horizontalAlignment(SWT.BEGINNING);
		return this;
	}

	/**
	 * Horizontal alignment: Center
	 *
	 * @return This builder for chaining.
	 * @see #horizontalAlignment(int)
	 * @see GridData#horizontalAlignment
	 */
	public GridDataBuilder hCenter() {
		horizontalAlignment(SWT.CENTER);
		return this;
	}

	/**
	 * Horizontal alignment: End
	 *
	 * @return This builder for chaining.
	 * @see #horizontalAlignment(int)
	 * @see GridData#horizontalAlignment
	 */
	public GridDataBuilder hEnd() {
		horizontalAlignment(SWT.END);
		return this;
	}

	/**
	 * Horizontal alignment: Fill
	 *
	 * @return This builder for chaining.
	 * @see #horizontalAlignment(int)
	 * @see GridData#horizontalAlignment
	 */
	public GridDataBuilder hFill() {
		horizontalAlignment(SWT.FILL);
		return this;
	}

	/**
	 * Vertical alignment: Beginning
	 *
	 * @return This builder for chaining.
	 * @see #verticalAlignment(int)
	 * @see GridData#verticalAlignment
	 */
	public GridDataBuilder vBegin() {
		horizontalAlignment(SWT.BEGINNING);
		return this;
	}

	/**
	 * Vertical alignment: Center
	 *
	 * @return This builder for chaining.
	 * @see #verticalAlignment(int)
	 * @see GridData#verticalAlignment
	 */
	public GridDataBuilder vCenter() {
		horizontalAlignment(SWT.CENTER);
		return this;
	}

	/**
	 * Vertical alignment: End
	 *
	 * @return This builder for chaining.
	 * @see #verticalAlignment(int)
	 * @see GridData#verticalAlignment
	 */
	public GridDataBuilder vEnd() {
		horizontalAlignment(SWT.END);
		return this;
	}

	/**
	 * Vertical alignment: Fill
	 *
	 * @return This builder for chaining.
	 * @see #verticalAlignment(int)
	 * @see GridData#verticalAlignment
	 */
	public GridDataBuilder vFill() {
		horizontalAlignment(SWT.FILL);
		return this;
	}

	/**
	 * Horizontal alignment.
	 *
	 * @param alignment
	 *            The alignment. One of {@link SWT#BEGINNING},
	 *            {@link SWT#CENTER}, {@link SWT#END} or {@link SWT#FILL}
	 *
	 * @return This builder for chaining.
	 * @see GridData#horizontalAlignment
	 */
	public GridDataBuilder horizontalAlignment(final int alignment) {
		switch (alignment) {
		case SWT.BEGINNING:
		case SWT.CENTER:
		case SWT.END:
		case SWT.FILL:
			horizontalAlignment = alignment;
			break;
		default:
			throw new IllegalArgumentException("Alignment has not a valid value."); //$NON-NLS-1$
		}

		return this;
	}

	/**
	 * Vertical alignment.
	 *
	 * @param alignment
	 *            The alignment. One of {@link SWT#BEGINNING},
	 *            {@link SWT#CENTER}, {@link SWT#END} or {@link SWT#FILL}
	 *
	 * @return This builder for chaining.
	 * @see GridData#verticalAlignment
	 */
	public GridDataBuilder verticalAlignment(final int alignment) {
		switch (alignment) {
		case SWT.BEGINNING:
		case SWT.CENTER:
		case SWT.END:
		case SWT.FILL:
			verticalAlignment = alignment;
			break;
		default:
			throw new IllegalArgumentException("Alignment has not a valid value."); //$NON-NLS-1$
		}

		return this;
	}

	/**
	 * Grabs excess horizontal space.
	 *
	 * @return This builder for chaining.
	 * @see GridData#grabExcessHorizontalSpace
	 */
	public GridDataBuilder hExcessSpace() {
		horizontalExcessSpace = true;
		return this;
	}

	/**
	 * Grabs excess vertical space.
	 *
	 * @return This builder for chaining.
	 * @see GridData#grabExcessVerticalSpace
	 */
	public GridDataBuilder vExcessSpace() {
		verticalExcessSpace = true;
		return this;
	}

	/**
	 * Horizontal span.
	 *
	 * @param span
	 *            The number of cells to span. Must be greater than zero.
	 *            Default is one.
	 *
	 * @return This builder for chaining.
	 * @see GridData#horizontalSpan
	 */
	public GridDataBuilder hSpan(final int span) {
		if (span < 1) {
			throw new IllegalArgumentException("Span has not a valid value."); //$NON-NLS-1$
		}
		horizontalSpan = span;
		return this;
	}

	/**
	 * Vertical span.
	 *
	 * @param span
	 *            The number of cells to span. Must be greater than zero.
	 *            Default is one.
	 *
	 * @return This builder for chaining.
	 * @see GridData#verticalSpan
	 */
	public GridDataBuilder vSpan(final int span) {
		if (span < 1) {
			throw new IllegalArgumentException("Span has not a valid value."); //$NON-NLS-1$
		}
		verticalSpan = span;
		return this;
	}

}
