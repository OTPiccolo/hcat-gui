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
	/** Command ID to save a sequence file. */
	public static final String SAVE_COMMAND_ID = "org.eclipse.ui.file.save";
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

	/** Context ID of the workspace location */
	public static final String WORKSPACE_CONTEXT_ID = "workspace";
}
