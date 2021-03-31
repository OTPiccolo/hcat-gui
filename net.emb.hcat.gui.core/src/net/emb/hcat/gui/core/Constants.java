package net.emb.hcat.gui.core;

/**
 * Constants class.
 *
 * @author OT Piccolo
 */
@SuppressWarnings("nls")
public class Constants {
	/** Command ID to exit the program. */
	public static final String EXIT_COMMAND_ID = "org.eclipse.ui.file.exit";
	/** Command ID to open a sequence file. */
	public static final String OPEN_COMMAND_ID = "org.eclipse.ui.file.open";
	/** Command Parameter ID for the file to open. */
	public static final String OPEN_COMMAND_PARAMETER_ID = "org.eclipse.ui.file.open.parameter.file";
	/** Command ID to save editor contents. */
	public static final String SAVE_COMMAND_ID = "org.eclipse.ui.file.save";
	/** Command Parameter ID to save editor content. */
	public static final String SAVE_COMMAND_PARAMETER_ID = "org.eclipse.ui.file.save.parameter.contentType";
	/** Command Parameter Value to save sequences. */
	public static final String SAVE_COMMAND_PARAMETER_VALUE_SEQUENCES = "Sequences";
	/** Command Parameter Value to save haplotypes. */
	public static final String SAVE_COMMAND_PARAMETER_VALUE_HAPLOTYPES = "Haplotypes";
	/** Command Parameter Value to save haplotype table. */
	public static final String SAVE_COMMAND_PARAMETER_VALUE_HAPLOTYPE_TABLE = "Haplotype Table";
	/** Command Parameter Value to save distance matrix. */
	public static final String SAVE_COMMAND_PARAMETER_VALUE_DISTANCE_MATRIX = "Distance Matrix";
	/** Command Parameter Value to save text log. */
	public static final String SAVE_COMMAND_PARAMETER_VALUE_TEXT_LOG = "Text Log";
	/** Command ID of the about dialog. */
	public static final String ABOUT_COMMAND_ID = "org.eclipse.ui.help.aboutAction";
	/** Command ID to cut sequences. */
	public static final String CUT_SEQUENCE_COMMAND_ID = "net.emb.hcat.ui.cutSequence";
	/** Command ID to cut sequences. */
	public static final String SEQUENCE_TRANSLATION_COMMAND_ID = "net.emb.hcat.ui.sequenceTranslation";

	/** Part ID of the main editor. */
	public static final String MAIN_EDITOR_PART_ID = "net.emb.hcat.gui.core.partdescriptor.main";
	/** Part ID of the navigator view. */
	public static final String NAVIGATOR_PART_ID = "net.emb.hcat.gui.core.part.navigator";
	/** Part ID of the transformer view. */
	public static final String TRANSFORM_PART_ID = "net.emb.hcat.gui.core.part.transform";

	/** ID to the node of preferences for the core. */
	public static final String CORE_PREFERENCES_ID = "net.emb.hcat.gui.core.preferences";

	/** Context ID of the workspace location */
	public static final String WORKSPACE_CONTEXT_ID = "workspace";

	/** ID of this plugin. */
	public static final String PLUGIN_ID = "net.emb.hcat.gui.core";
	/** URI to this plugin on the platform. */
	public static final String PLUGIN_URI = "platform:/plugin/" + PLUGIN_ID;
}
