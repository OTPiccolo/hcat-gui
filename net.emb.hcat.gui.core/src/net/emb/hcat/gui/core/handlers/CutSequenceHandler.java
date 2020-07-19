package net.emb.hcat.gui.core.handlers;

import java.util.Collection;
import java.util.List;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import net.emb.hcat.cli.sequence.Sequence;
import net.emb.hcat.gui.core.Constants;
import net.emb.hcat.gui.core.parts.MainPart;
import net.emb.hcat.gui.core.wizards.CutSequenceWizard;

/**
 * Opens a wizard to cut one or more sequences to a specified size.
 *
 * @author OT Piccolo
 */
public class CutSequenceHandler {

	@SuppressWarnings("javadoc")
	@CanExecute
	public boolean canExecute(final EPartService partService) {
		return getSelectedPart(partService) != null;
	}

	@SuppressWarnings("javadoc")
	@Execute
	public void execute(final EPartService partService, final Shell shell, final CutSequenceWizard wizard) {
		final MPart part = getSelectedPart(partService);
		if (part != null) {
			final MainPart mainPart = (MainPart) part.getObject();
			final List<Sequence> sequences = mainPart.getSequences();

			final WizardDialog dialog = makeWizardDialog(shell, wizard, sequences);
			if (dialog.open() == Window.OK) {
				createPart(partService, wizard, part);
			}
			return;
		}
	}

	private void createPart(final EPartService partService, final CutSequenceWizard wizard, final MPart orgPart) {
		final String name = orgPart.getLabel();
		final String label = name + " " + (wizard.getFrom() + 1) + " -> " + wizard.getTo(); //$NON-NLS-1$ //$NON-NLS-2$
		final String id = name + System.currentTimeMillis();
		final MPart newPart = partService.createPart(Constants.MAIN_EDITOR_PART_ID);
		partService.showPart(newPart, PartState.CREATE);
		newPart.setLabel(label);

		final MainPart mainPart = (MainPart) newPart.getObject();
		mainPart.setId(id);
		((MainPart) newPart.getObject()).setSequences(wizard.getSequences());
		partService.showPart(newPart, PartState.ACTIVATE);
	}

	private MPart getSelectedPart(final EPartService partService) {
		for (final MPart part : partService.getParts()) {
			if (part.getElementId().equals(Constants.MAIN_EDITOR_PART_ID)) {
				if (partService.isPartVisible(part)) {
					return part;
				}
			}
		}
		return null;
	}

	private WizardDialog makeWizardDialog(final Shell shell, final CutSequenceWizard wizard, final Collection<Sequence> sequences) {
		wizard.setSequences(sequences);
		final WizardDialog dialog = new WizardDialog(shell, wizard);
		return dialog;
	}

}