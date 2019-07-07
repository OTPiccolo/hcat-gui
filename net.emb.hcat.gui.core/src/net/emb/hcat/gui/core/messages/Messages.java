package net.emb.hcat.gui.core.messages;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "net.emb.hcat.gui.core.messages.messages"; //$NON-NLS-1$
	public static String HcatFunction_CutSequences;
	public static String HcatFunction_ExportConvertedFile;
	public static String HcatFunction_HaplotypeAnalysis;
	public static String HcatFunction_Indices;
	public static String HcatFunction_SaveHaplotypes;
	public static String HcatFunction_SequenceTranslation;
	public static String MainPart_DistanceMatrixTab;
	public static String MainPart_OverviewTab;
	public static String MainPart_TranslationTab;
	public static String SelectionPage_Description;
	public static String SelectionPage_Title;
	public static String SelectionPage_button_text;
	public static String SelectionPage_lblName_text;
	public static String SelectionPage_lblModus_text;
	public static String SelectionPage_lblWorkspace_text;
	public static String SelectionWizard_Title;
	public static String WorkspaceModus_BatchFunctionProcession;
	public static String WorkspaceModus_SingleFunctionProcession;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
