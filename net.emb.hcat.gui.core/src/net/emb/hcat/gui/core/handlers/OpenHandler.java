package net.emb.hcat.gui.core.handlers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Named;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.emb.hcat.cli.ErrorCodeException;
import net.emb.hcat.cli.ErrorCodeException.EErrorCode;
import net.emb.hcat.cli.io.sequence.BaseSequenceReader;
import net.emb.hcat.cli.io.sequence.ESequenceType;
import net.emb.hcat.cli.io.sequence.ISequenceReader;
import net.emb.hcat.cli.sequence.Sequence;
import net.emb.hcat.gui.core.Constants;
import net.emb.hcat.gui.core.messages.ErrorCodeMessages;
import net.emb.hcat.gui.core.messages.Messages;
import net.emb.hcat.gui.core.parts.MainPart;

/**
 * Opens a file to read sequences within.
 *
 * @author OT Piccolo
 */
public class OpenHandler {

	private static final Logger log = LoggerFactory.getLogger(OpenHandler.class);

	@SuppressWarnings("javadoc")
	@CanExecute
	public boolean canExecute() {
		return true;
	}

	// More detailed execute method must come first in code declaration, as
	// otherwise, less detailed method might be called even though a more
	// detailed command was exectued.

	@SuppressWarnings("javadoc")
	@Execute
	public void execute(final EPartService partService, final Shell shell, @Named(Constants.OPEN_COMMAND_PARAMETER_ID) final String fileParam) {
		final Path path = Paths.get(fileParam);

		openPart(partService, shell, path);
	}

	@SuppressWarnings("javadoc")
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

	private void openPart(final EPartService partService, final Shell shell, final Path path) {
		final List<Sequence> sequences = getSequences(shell, path);
		if (sequences.isEmpty()) {
			// Don't open an editor, if no sequences can be displayed.
			return;
		}

		MPart part = findPart(partService, path);
		if (part == null) {
			part = createPart(partService, path);
		}

		final MainPart mainPart = (MainPart) part.getObject();
		mainPart.setSequences(sequences);

		partService.showPart(part, PartState.ACTIVATE);

		addToRecentFiles(path);
	}

	private List<Sequence> getSequences(final Shell shell, final Path path) {
		final List<Sequence> sequences = new ArrayList<>();

		final ESequenceType type = getSequenceType(path);
		if (type != null) {
			try (ISequenceReader reader = type.createReader(new BufferedReader(new FileReader(path.toString())))) {
				if (reader instanceof BaseSequenceReader) {
					((BaseSequenceReader) reader).setEnforceSameLength(true);
				}
				sequences.addAll(reader.read());
				if (sequences.isEmpty()) {
					createMessageDialog(shell, MessageDialog.WARNING, Messages.OpenHandler_errorEmptyFileTitle, MessageFormat.format(Messages.OpenHandler_errorEmptyFileMessage, path, type));
				}
			} catch (final ErrorCodeException e) {
				log.error(e.getMessage(), e);
				createErrorCodeDialog(shell, path, e);
			} catch (final IOException e) {
				log.error(e.getMessage(), e);
				createErrorCodeDialog(shell, path, new ErrorCodeException(EErrorCode.GENERIC_READ, e, e.getMessage(), e.getMessage()));
			}
		} else {
			createMessageDialog(shell, MessageDialog.ERROR, Messages.OpenHandler_errorUnknownFormatTitle, MessageFormat.format(Messages.OpenHandler_errorUnknownFormatMessage, path));
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

	private void createMessageDialog(final Shell shell, final int kind, final String title, final String message) {
		MessageDialog.open(kind, shell, title, message, SWT.NONE);
	}

	private void createErrorCodeDialog(final Shell shell, final Path path, final ErrorCodeException e) {
		final String errorCodeMessage = getErrorCodeMessage(e);
		createMessageDialog(shell, MessageDialog.ERROR, Messages.OpenHandler_errorCodeTitle, MessageFormat.format(Messages.OpenHandler_errorCodeMessage, path, errorCodeMessage));
	}

	private String getErrorCodeMessage(final ErrorCodeException e) {
		return ErrorCodeMessages.getErrorCodeMessage(e);
	}

	private void addToRecentFiles(final Path path) {
		final String pathStr = path.toString();

		try {
			final Preferences node = InstanceScope.INSTANCE.getNode(Constants.RECENT_FILES_PREFERENCES_NODE_ID);
			final String[] keys = node.keys();
			Arrays.sort(keys);

			final List<String> values = new ArrayList<String>(keys.length + 1);
			for (final String key : keys) {
				values.add(node.get(key, null));
			}

			final int index = values.indexOf(pathStr);
			if (index == 0) {
				// Path already at first index. Nothing to do here.
				return;
			}
			if (index > 0) {
				// Path already present, remove old entry, so can be put in
				// front again.
				values.remove(index);
			}

			values.add(0, pathStr);

			// We want to store at most 10 files.
			final int size = Math.min(values.size(), 10);
			for (int i = 0; i < size; i++) {
				final String value = values.get(i);
				node.put(String.valueOf(i), value);
			}

			node.flush();
		} catch (final BackingStoreException e) {
			log.error(e.getMessage(), e);
		}
	}

}
