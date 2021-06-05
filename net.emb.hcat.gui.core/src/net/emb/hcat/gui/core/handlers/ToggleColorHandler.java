package net.emb.hcat.gui.core.handlers;

import javax.inject.Named;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuItem;
import org.osgi.service.prefs.BackingStoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.emb.hcat.gui.core.Constants;

/**
 * Toggles whether colors will be shown.
 *
 * @author OT Piccolo
 */
public class ToggleColorHandler {

	private static final Logger log = LoggerFactory.getLogger(ToggleColorHandler.class);

	@SuppressWarnings("javadoc")
	@CanExecute
	public boolean canExecute() {
		return true;
	}

	@SuppressWarnings("javadoc")
	@Execute
	public void execute(final MMenuItem item, @Named(Constants.TOGGLE_COLOR_COMMAND_PARAMETER_ID) final String partParam, @Preference(nodePath = Constants.DISPLAY_COLORS_PREFERENCES_NODE_ID) final IEclipsePreferences prefs) {
		try {
			prefs.putBoolean(partParam, item.isSelected());
			prefs.flush();
		} catch (final BackingStoreException e) {
			log.error(e.getMessage(), e);
		}
	}

}
