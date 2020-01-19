package net.emb.hcat.gui.core.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import net.emb.hcat.gui.core.messages.Messages;

/**
 * Quits the current application.
 *
 * @author OT Piccolo
 */
public class QuitHandler {

	@SuppressWarnings("javadoc")
	@Execute
	public void execute(final IWorkbench workbench, final Shell shell) {
		if (MessageDialog.openConfirm(shell, Messages.QuitHandler_title, Messages.QuitHandler_message)) {
			workbench.close();
		}
	}

}
