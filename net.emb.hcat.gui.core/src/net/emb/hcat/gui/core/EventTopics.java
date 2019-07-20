package net.emb.hcat.gui.core;

import org.eclipse.e4.ui.di.UIEventTopic;

/**
 * All event topics for HCAT.
 *
 * @author Heiko Mattes
 * @see UIEventTopic
 */
@SuppressWarnings("nls")
public class EventTopics {

	/**
	 * The event topic for the currently active haplotypes to work on.
	 */
	public static final String ACTIVE_HAPLOTYPES = "ActiveHaplotypes";
	/**
	 * The event topic for the currently active sequences to work on.
	 */
	public static final String ACTIVE_SEQUENCES = "ActiveSequences";
	/**
	 * The event topic for the currently selected haplotype, for which
	 * information should be displayed.
	 */
	public static final String SELECTED_HAPLOTYPE = "SelectedHaplotype";
	/**
	 * The event topic for the currently selected sequence, for which
	 * information should be displayed.
	 */
	public static final String SELECTED_SEQUENCE = "SelectedSequenec";

	/**
	 * The event topic for the working directory, where the sequences are
	 * stored.
	 */
	public static final String WORKING_DIRECTORY = "WorkingDirectory";

}
