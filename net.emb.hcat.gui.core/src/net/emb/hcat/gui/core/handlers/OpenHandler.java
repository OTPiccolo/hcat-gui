package net.emb.hcat.gui.core.handlers;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

import net.emb.hcat.cli.Sequence;
import net.emb.hcat.cli.io.FastaReader;
import net.emb.hcat.gui.core.Constants;
import net.emb.hcat.gui.core.parts.MainPart;

public class OpenHandler {

	@CanExecute
	public boolean canExecute() {
		return true;
	}

	@Execute
	public void execute(final EPartService partService, @Named(Constants.OPEN_FILE_COMMAND_PARAMETER_ID) final String fileParam) {
		final Path path = Paths.get(fileParam);

		List<Sequence> sequences;
		try (FastaReader reader = new FastaReader(new FileReader(fileParam))) {
			sequences = reader.read();
		} catch (final IOException e) {
			e.printStackTrace();
			return;
		}

		MPart part = findPart(partService, path);
		if (part == null) {
			part = createPart(partService, path);
		}

		final MainPart mainPart = (MainPart) part.getObject();
		mainPart.setSequences(sequences);

		partService.showPart(part, PartState.ACTIVATE);
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
}
