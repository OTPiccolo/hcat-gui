package net.emb.hcat.gui.core.handlers;

import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import net.emb.hcat.cli.io.ESequenceType;
import net.emb.hcat.cli.io.ISequenceWriter;
import net.emb.hcat.cli.sequence.Sequence;
import net.emb.hcat.gui.core.Constants;
import net.emb.hcat.gui.core.messages.Messages;
import net.emb.hcat.gui.core.parts.MainPart;

public class SaveAsHandler {

	@CanExecute
	public boolean canExecute(final EPartService partService) {
		for (final MPart part : partService.getParts()) {
			if (part.getElementId().equals(Constants.MAIN_EDITOR_PART_ID) && part.isToBeRendered()) {
				return true;
			}
		}
		return false;
	}

	@Execute
	public void execute(final EPartService partService, final Shell shell) {
		for (final MPart part : partService.getParts()) {
			if (part.getElementId().equals(Constants.MAIN_EDITOR_PART_ID)) {
				if (partService.isPartVisible(part)) {
					saveAs(part, shell);
					return;
				}
			}
		}
	}

	private void saveAs(final MPart part, final Shell shell) {
		final FileDialog dialog = createDialog(shell);
		final String file = dialog.open();
		if (file != null) {
			final MainPart mainPart = (MainPart) part.getObject();
			final List<Sequence> sequences = mainPart.getSequences();
			try {
				saveAs(file, sequences, getType(dialog));
			} catch (final IOException e) {
				final String message = MessageFormat.format(Messages.SaveAsHandler_errorSaveFileMessage, file, e.getLocalizedMessage());
				MessageDialog.open(MessageDialog.ERROR, shell, Messages.SaveAsHandler_errorSaveFileTitle, message, SWT.NONE);
			}
		}
	}

	private void saveAs(final String file, final List<Sequence> sequences, final ESequenceType type) throws IOException {
		try (final ISequenceWriter writer = type.createWriter(new FileWriter(file))) {
			writer.write(sequences);
		}
	}

	private FileDialog createDialog(final Shell shell) {
		final FileDialog dialog = new FileDialog(shell, SWT.SAVE);
		dialog.setFilterExtensions(new String[] { "*.fas", "*.phy", "*.phy" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		dialog.setFilterNames(new String[] { Messages.SaveAsHandler_fastaFileExtension, Messages.SaveAsHandler_phylipFileExtension, Messages.SaveAsHandler_phylipTcsFileExtension });
		return dialog;
	}

	private ESequenceType getType(final FileDialog dialog) {
		return ESequenceType.values()[dialog.getFilterIndex()];
	}
}