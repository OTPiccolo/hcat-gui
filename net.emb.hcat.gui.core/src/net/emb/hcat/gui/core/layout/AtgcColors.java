package net.emb.hcat.gui.core.layout;

import java.util.EnumMap;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;

import net.emb.hcat.cli.sequence.ATGC;

/**
 * Defines colors for ATGC nucleotides.
 *
 * @author OT Piccolo
 */
public class AtgcColors {

	private final Device device;

	private final Map<ATGC, Color> bgColor = new EnumMap<ATGC, Color>(ATGC.class);
	private final Map<ATGC, Color> fgColor = new EnumMap<ATGC, Color>(ATGC.class);

	/**
	 * Constructor.
	 *
	 * @param device
	 *            The device for the colors.
	 */
	public AtgcColors(final Device device) {
		this.device = device;
		if (device == null) {
			throw new NullPointerException("Device must not be null"); //$NON-NLS-1$
		}
		prepareColors();
	}

	private void prepareColors() {
		final Color adenine = new Color(device, 0x00, 0x7f, 0xff);
		final Color thymine = new Color(device, 0xff, 0xf7, 0x00);
		final Color guanine = new Color(device, 0x00, 0xff, 0x00);
		final Color cytosine = new Color(device, 0x96, 0x00, 0x18);

		bgColor.put(ATGC.ADENINE, adenine);
		bgColor.put(ATGC.THYMINE, thymine);
		bgColor.put(ATGC.GUANINE, guanine);
		bgColor.put(ATGC.CYTOSINE, cytosine);

		fgColor.put(ATGC.ADENINE, createContrastColor(adenine));
		fgColor.put(ATGC.THYMINE, createContrastColor(thymine));
		fgColor.put(ATGC.GUANINE, createContrastColor(guanine));
		fgColor.put(ATGC.CYTOSINE, createContrastColor(cytosine));
	}

	private Color createContrastColor(final Color color) {
		// Normalize and gamma correct:
		final double rr = Math.pow(color.getRed() / 255.0, 2.2);
		final double gg = Math.pow(color.getGreen() / 255.0, 2.2);
		final double bb = Math.pow(color.getBlue() / 255.0, 2.2);

		// Calculate luminance:
		final double lum = 0.2126 * rr + 0.7152 * gg + 0.0722 * bb;

		if (lum < 0.5) {
			return new Color(color.getDevice(), 0xff, 0xff, 0xff);
		}
		return new Color(color.getDevice(), 0x00, 0x00, 0x00);
	}

	/**
	 * Gets the color of the given nucleotide.
	 *
	 * @param atgc
	 *            The nucleotide.
	 * @return The color, or <code>null</code>, if <code>null</code> is given.
	 */
	public Color getColor(final ATGC atgc) {
		return bgColor.get(atgc);
	}

	/**
	 * Gets the background color for tables for the given nucleotide.
	 *
	 * @param atgc
	 *            The nucleotide.
	 * @return The color, or <code>null</code>, if <code>null</code> is given.
	 */
	public Color getBackgroundColor(final ATGC atgc) {
		return bgColor.get(atgc);
	}

	/**
	 * Gets the foreground color for tables for the given nucleotide.
	 *
	 * @param atgc
	 *            The nucleotide.
	 * @return The color, or <code>null</code>, if <code>null</code> is given.
	 */
	public Color getForegroundColor(final ATGC atgc) {
		return fgColor.get(atgc);
	}

}
