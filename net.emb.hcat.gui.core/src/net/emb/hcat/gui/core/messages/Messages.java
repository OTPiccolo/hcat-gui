package net.emb.hcat.gui.core.messages;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "net.emb.hcat.gui.core.messages.messages"; //$NON-NLS-1$
	public static String ImportPage_Description;
	public static String ImportPage_Title;
	public static String ImportWizard_Title;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
