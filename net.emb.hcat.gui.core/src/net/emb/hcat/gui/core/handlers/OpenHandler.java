package net.emb.hcat.gui.core.handlers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import net.emb.hcat.cli.io.ESequenceType;
import net.emb.hcat.cli.io.ISequenceReader;
import net.emb.hcat.cli.sequence.Sequence;
import net.emb.hcat.gui.core.Constants;
import net.emb.hcat.gui.core.messages.Messages;
import net.emb.hcat.gui.core.parts.MainPart;

public class OpenHandler {

	@CanExecute
	public boolean canExecute() {
		return true;
	}

	@Execute
	public void execute(final EPartService partService, final Shell shell) {
		final FileDialog dialog = createFileDialog(shell);
		final String fileName = dialog.open();

		if (fileName != null) {
			final Path path = Paths.get(fileName);
			openPart(partService, shell, path);
		}
	}

	private FileDialog createFileDialog(final Shell shell) {
		final FileDialog dialog = new FileDialog(shell, SWT.OPEN);
		dialog.setText(Messages.OpenHandler_openDialogTitle);
		dialog.setFilterExtensions(new String[] { "*.fas;*.txt;*.phy", "*.fas;*.txt", "*.phy", "*" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		dialog.setFilterNames(new String[] { Messages.OpenHandler_openDialogAllFormats, Messages.OpenHandler_openDialogFastaFormat, Messages.OpenHandler_openDialogPhylipFormat, Messages.OpenHandler_openDialogAllFiles });
		return dialog;
	}

	@Execute
	public void execute(final EPartService partService, final Shell shell, @Named(Constants.OPEN_COMMAND_PARAMETER_ID) final String fileParam) {
		final Path path = Paths.get(fileParam);

		openPart(partService, shell, path);
	}

	private void openPart(final EPartService partService, final Shell shell, final Path path) {
		MPart part = findPart(partService, path);
		if (part == null) {
			part = createPart(partService, path);
		}

		final List<Sequence> sequences = getSequences(path);
		if (sequences.isEmpty()) {
			createMessageDialog(shell, path);
		}
		final MainPart mainPart = (MainPart) part.getObject();
		mainPart.setSequences(sequences);

		partService.showPart(part, PartState.ACTIVATE);
	}

	private List<Sequence> getSequences(final Path path) {
		final List<Sequence> sequences = new ArrayList<>();
		final ESequenceType type = getSequenceType(path);
		if (type != null) {
			try (ISequenceReader reader = type.createReader(new BufferedReader(new FileReader(path.toString())))) {
				sequences.addAll(reader.read());
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
		return sequences;
	}

	private ESequenceType getSequenceType(final Path path) {
		final String fileName = path.getFileName().toString();
		final int index = fileName.lastIndexOf('.');
		if (index != -1) {
			final String ending = fileName.substring(index + 1).toLowerCase();
			switch (ending) {
			case "fas": //$NON-NLS-1$
			case "txt": //$NON-NLS-1$
				return ESequenceType.FASTA;
			case "phy": //$NON-NLS-1$
				return ESequenceType.PHYLIP;
			default:
				return null;
			}
		}
		return null;
	}

	// Check if part is already open.
	private MPart findPart(final EPartService partService, final Path path) {
		final String id = path.toString();

		for (final MPart part : partService.getParts()) {
			if (part.getElementId().equals(Constants.MAIN_EDITOR_PART_ID)) {
				final MainPart mainPart = (MainPart) part.getObject();
				if (mainPart != null && id.equals(mainPart.getId())) {
					return part;
				}
			}
		}

		return null;
	}

	// Create new part.
	private MPart createPart(final EPartService partService, final Path path) {
		String label = path.getFileName().toString();
		final int index = label.indexOf('.');
		if (index != -1) {
			label = label.substring(0, index);
		}

		final MPart part = partService.createPart(Constants.MAIN_EDITOR_PART_ID);
		partService.showPart(part, PartState.CREATE);
		part.setLabel(label);

		final MainPart mainPart = (MainPart) part.getObject();
		mainPart.setId(path.toString());

		return part;
	}

	private void createMessageDialog(final Shell shell, final Path path) {
		final ESequenceType sequenceType = getSequenceType(path);

		int kind;
		String title;
		String message;

		if (sequenceType == null) {
			// Opened file format could not be determined.
			kind = MessageDialog.ERROR;
			title = Messages.OpenHandler_errorUnknownFormatTitle;
			message = MessageFormat.format(Messages.OpenHandler_errorUnknownFormatMessage, path);
		} else {
			// File format was recognized, but empty.
			kind = MessageDialog.WARNING;
			title = Messages.OpenHandler_errorEmptyFileTitle;
			message = MessageFormat.format(Messages.OpenHandler_errorEmptyFileMessage, path, sequenceType);
		}

		MessageDialog.open(kind, shell, title, message, SWT.NONE);
	}
}
