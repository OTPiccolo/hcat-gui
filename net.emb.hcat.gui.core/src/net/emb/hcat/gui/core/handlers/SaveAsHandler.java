package net.emb.hcat.gui.core.handlers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import net.emb.hcat.cli.haplotype.Haplotype;
import net.emb.hcat.cli.haplotype.HaplotypeTransformer;
import net.emb.hcat.cli.io.HaplotypeTableWriter;
import net.emb.hcat.cli.io.sequence.ESequenceType;
import net.emb.hcat.cli.io.sequence.ISequenceWriter;
import net.emb.hcat.cli.sequence.Sequence;
import net.emb.hcat.gui.core.Constants;
import net.emb.hcat.gui.core.messages.Messages;
import net.emb.hcat.gui.core.parts.MainPart;

/**
 * Saves the currently active editor into a new file.
 *
 * @author OT Piccolo
 */
public class SaveAsHandler {

	@SuppressWarnings("javadoc")
	@CanExecute
	public boolean canExecute(final EPartService partService) {
		for (final MPart part : partService.getParts()) {
			if (part.getElementId().equals(Constants.MAIN_EDITOR_PART_ID) && part.isToBeRendered()) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("javadoc")
	@Execute
	public void execute(final EPartService partService, final Shell shell, @Named(Constants.SAVE_COMMAND_PARAMETER_ID) final String contentParam) {
		final MainPart part = getPart(partService);
		if (part != null) {
			saveAs(part, shell, contentParam);
		}
	}

	private MainPart getPart(final EPartService partService) {
		for (final MPart part : partService.getParts()) {
			if (part.getElementId().equals(Constants.MAIN_EDITOR_PART_ID) && partService.isPartVisible(part)) {
				return (MainPart) part.getObject();
			}
		}
		return null;
	}

	private void saveAs(final MainPart part, final Shell shell, final String contentType) {
		switch (contentType) {
		case Constants.SAVE_COMMAND_PARAMETER_VALUE_SEQUENCES:
			saveSequences(shell, part.getSequences());
			break;

		case Constants.SAVE_COMMAND_PARAMETER_VALUE_HAPLOTYPES:
			saveSequences(shell, part.getHaplotypes().stream().map(h -> h.asSequence()).collect(Collectors.toList()));
			break;

		case Constants.SAVE_COMMAND_PARAMETER_VALUE_HAPLOTYPE_TABLE:
			saveHaplotypeTable(shell, part.getHaplotypes(), part.getMasterHaplotype());
			break;

		case Constants.SAVE_COMMAND_PARAMETER_VALUE_DISTANCE_MATRIX:
		case Constants.SAVE_COMMAND_PARAMETER_VALUE_TEXT_LOG:
		default:
			// Should never happen.
			throw new IllegalArgumentException("Unknown content type to save: " + contentType); //$NON-NLS-1$
		}
	}

	private void saveSequences(final Shell shell, final List<Sequence> sequences) {
		final FileDialog dialog = createSequenceDialog(shell);
		final String file = dialog.open();
		if (file != null) {
			try {
				saveSequences(file, sequences, getType(dialog));
			} catch (final IOException e) {
				final String message = MessageFormat.format(Messages.SaveAsHandler_errorSaveFileMessage, file, e.getLocalizedMessage());
				MessageDialog.open(MessageDialog.ERROR, shell, Messages.SaveAsHandler_errorSaveFileTitle, message, SWT.NONE);
			}
		}
	}

	private void saveSequences(final String file, final List<Sequence> sequences, final ESequenceType type) throws IOException {
		try (final ISequenceWriter writer = type.createWriter(new FileWriter(file))) {
			writer.write(sequences);
		}
	}

	private void saveHaplotypeTable(final Shell shell, final List<Haplotype> haplotypes, final Haplotype masterHaplotype) {
		final FileDialog dialog = createTextDialog(shell);
		final String file = dialog.open();
		if (file != null) {
			try {
				saveHaplotypeTable(file, haplotypes, masterHaplotype);
			} catch (final IOException e) {
				final String message = MessageFormat.format(Messages.SaveAsHandler_errorSaveFileMessage, file, e.getLocalizedMessage());
				MessageDialog.open(MessageDialog.ERROR, shell, Messages.SaveAsHandler_errorSaveFileTitle, message, SWT.NONE);
			}
		}
	}

	private void saveHaplotypeTable(final String file, final List<Haplotype> haplotypes, final Haplotype masterHaplotype) throws IOException {
		final Sequence masterSequence = masterHaplotype.getFirstSequence();
		final HaplotypeTransformer transformer = new HaplotypeTransformer(haplotypes);
		try (Writer buffer = new BufferedWriter(new FileWriter(file))) {
			final HaplotypeTableWriter writer = new HaplotypeTableWriter(buffer);
			writer.write(masterSequence, transformer.compareToMaster(masterSequence));
		}
	}

	private FileDialog createTextDialog(final Shell shell) {
		final FileDialog dialog = new FileDialog(shell, SWT.SAVE);
		dialog.setFilterExtensions(new String[] { "*.txt" }); //$NON-NLS-1$
		dialog.setFilterNames(new String[] { Messages.SaveAsHandler_textFileExtension });
		return dialog;
	}

	private FileDialog createSequenceDialog(final Shell shell) {
		final FileDialog dialog = new FileDialog(shell, SWT.SAVE);
		dialog.setFilterExtensions(new String[] { "*.fas", "*.phy", "*.phy", "*.csv" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		dialog.setFilterNames(new String[] { Messages.SaveAsHandler_fastaFileExtension, Messages.SaveAsHandler_phylipFileExtension, Messages.SaveAsHandler_phylipTcsFileExtension, Messages.SaveAsHandler_csvFileExtension });
		return dialog;
	}

	private ESequenceType getType(final FileDialog dialog) {
		return ESequenceType.values()[dialog.getFilterIndex()];
	}

}