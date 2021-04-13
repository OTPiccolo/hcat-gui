package net.emb.hcat.gui.core.handlers;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

// Unused. Only here for reference, can be deleted later.

@SuppressWarnings("javadoc")
@Deprecated
public class SaveHandler {

	@CanExecute
	public boolean canExecute(final EPartService partService) {
		if (partService != null) {
			return !partService.getDirtyParts().isEmpty();
		}
		return false;
	}

	@Execute
	public void execute(final EPartService partService) {
		partService.saveAll(false);
	}
}