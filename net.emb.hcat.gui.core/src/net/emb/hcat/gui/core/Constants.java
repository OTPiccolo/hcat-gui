package net.emb.hcat.gui.core;

/**
 * Constants class.
 *
 * @author OT Piccolo
 */
@SuppressWarnings("nls")
public class Constants {

	/** ID of this plugin. */
	public static final String PLUGIN_ID = "net.emb.hcat.gui.core";
	/** URI to this plugin on the platform. */
	public static final String PLUGIN_URI = "platform:/plugin/" + PLUGIN_ID;

	/** Command ID to exit the program. */
	public static final String EXIT_COMMAND_ID = "org.eclipse.ui.file.exit";
	/** Command ID to open a sequence file. */
	public static final String OPEN_COMMAND_ID = "org.eclipse.ui.file.open";
	/** Command Parameter ID for the file to open. */
	public static final String OPEN_COMMAND_PARAMETER_ID = OPEN_COMMAND_ID + ".parameter.file";
	/** Command ID to save editor contents. */
	public static final String SAVE_COMMAND_ID = "org.eclipse.ui.file.save";
	/** Command Parameter ID to save editor content. */
	public static final String SAVE_COMMAND_PARAMETER_ID = SAVE_COMMAND_ID + ".parameter.contentType";
	/** Command Parameter Value to save depending on the displayed content. */
	public static final String SAVE_COMMAND_PARAMETER_VALUE_DYNAMIC = "Dynamic";
	/** Command Parameter Value to save haplotypes. */
	public static final String SAVE_COMMAND_PARAMETER_VALUE_HAPLOTYPES = "Haplotypes";
	/** Command Parameter Value to save sequences. */
	public static final String SAVE_COMMAND_PARAMETER_VALUE_SEQUENCES = "Sequences";
	/** Command Parameter Value to save haplotype table. */
	public static final String SAVE_COMMAND_PARAMETER_VALUE_HAPLOTYPE_TABLE = "HaplotypeTable";
	/** Command Parameter Value to save distance matrix. */
	public static final String SAVE_COMMAND_PARAMETER_VALUE_DISTANCE_MATRIX = "DistanceMatrix";
	/** Command Parameter Value to save text log. */
	public static final String SAVE_COMMAND_PARAMETER_VALUE_TEXT_LOG = "TextLog";
	/** Command ID of the about dialog. */
	public static final String ABOUT_COMMAND_ID = "org.eclipse.ui.help.aboutAction";
	/** Command ID to cut sequences. */
	public static final String CUT_SEQUENCE_COMMAND_ID = PLUGIN_ID + ".cmd.cutSequence";
	/** Command ID to cut sequences. */
	public static final String SEQUENCE_TRANSLATION_COMMAND_ID = PLUGIN_ID + ".cmd.sequenceTranslation";
	/** Command ID to toggle color display. */
	public static final String TOGGLE_COLOR_COMMAND_ID = PLUGIN_ID + ".cmd.toggleColor";
	/** Command Parameter ID to toggle color display. */
	public static final String TOGGLE_COLOR_COMMAND_PARAMETER_ID = TOGGLE_COLOR_COMMAND_ID + ".parameter.tab";
	/** Command Parameter Value to toggle in Overview tab. */
	public static final String TOGGLE_COLOR_COMMAND_PARAMETER_VALUE_OVERVIEW = "Overview";
	/** Command Parameter Value to toggle in Haplotype Table tab. */
	public static final String TOGGLE_COLOR_COMMAND_PARAMETER_VALUE_HAPLOTYPE_TABLE = "HaplotypeTable";

	/** Part ID of the main editor. */
	public static final String MAIN_EDITOR_PART_ID = PLUGIN_ID + ".partdescriptor.main";
	/** Part ID of the navigator view. */
	public static final String NAVIGATOR_PART_ID = PLUGIN_ID + ".part.navigator";
	/** Part ID of the transformer view. */
	public static final String TRANSFORM_PART_ID = PLUGIN_ID + ".part.transform";

	/** ID to the node of preferences for the core. */
	public static final String CORE_PREFERENCES_NODE_ID = PLUGIN_ID + ".preferences";
	/** ID to the node of recently opened files. */
	public static final String RECENT_FILES_PREFERENCES_NODE_ID = CORE_PREFERENCES_NODE_ID + ".recentFiles";
	/** ID to the node of display state of colors in editor. */
	public static final String DISPLAY_COLORS_PREFERENCES_NODE_ID = CORE_PREFERENCES_NODE_ID + ".colors.display";

	/** Context ID of the workspace location */
	public static final String WORKSPACE_CONTEXT_ID = "workspace";

}
