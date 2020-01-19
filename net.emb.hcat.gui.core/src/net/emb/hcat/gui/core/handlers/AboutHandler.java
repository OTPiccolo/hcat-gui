package net.emb.hcat.gui.core.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import net.emb.hcat.gui.core.messages.Messages;

/**
 * About handler.
 * 
 * @author OT Piccolo
 */
public class AboutHandler {

	@SuppressWarnings("javadoc")
	@Execute
	public void execute(final Shell shell) {
		MessageDialog.openInformation(shell, Messages.AboutHandler_title, Messages.AboutHandler_message);
	}

}
