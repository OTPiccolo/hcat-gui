package net.emb.hcat.gui.core.items;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.commands.MParameter;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.emb.hcat.gui.core.Constants;
import net.emb.hcat.gui.core.messages.Messages;

/**
 * Displays recently opened files in a menu.
 *
 * @author OT Piccolo
 */
public class RecentFilesItem {

	private static final Logger log = LoggerFactory.getLogger(RecentFilesItem.class);

	private static final String FILE_SEPARATOR = System.getProperty("file.separator"); //$NON-NLS-1$

	@Inject
	private EModelService modelService;

	private MCommand openCommand;

	@SuppressWarnings("javadoc")
	@PostConstruct
	public void initialize(final MApplication application) {
		openCommand = modelService.findElements(application, Constants.OPEN_COMMAND_ID, MCommand.class).get(0);
	}

	@SuppressWarnings("javadoc")
	@AboutToShow
	public void show(final List<MMenuElement> items) {
		final Preferences node = InstanceScope.INSTANCE.getNode(Constants.CORE_PREFERENCES_ID).node("recentFiles"); //$NON-NLS-1$
		final String[] keys;
		try {
			keys = node.keys();
		} catch (final BackingStoreException e) {
			log.error(e.getMessage(), e);
			final MDirectMenuItem item = modelService.createModelElement(MDirectMenuItem.class);
			item.setContributorURI(Constants.PLUGIN_URI);
			item.setElementId("errorRecentFilesItem"); //$NON-NLS-1$
			item.setLabel(Messages.RecentFilesItem_errorRecentFiles);
			item.setEnabled(false);
			items.add(item);
			return;
		}

		if (keys.length == 0) {
			final MDirectMenuItem item = modelService.createModelElement(MDirectMenuItem.class);
			item.setContributorURI(Constants.PLUGIN_URI);
			item.setElementId("noRecentFilesItem"); //$NON-NLS-1$
			item.setLabel(Messages.RecentFilesItem_noRecentFiles);
			item.setEnabled(false);
			items.add(item);
		} else {
			Arrays.sort(keys);
			for (final String key : keys) {
				final String value = node.get(key, null);

				final MParameter param = modelService.createModelElement(MParameter.class);
				param.setContributorURI(Constants.PLUGIN_URI);
				param.setElementId("recentFilesParam" + items.size()); //$NON-NLS-1$
				param.setName(Constants.OPEN_COMMAND_PARAMETER_ID);
				param.setValue(value);

				final MHandledMenuItem item = modelService.createModelElement(MHandledMenuItem.class);
				item.setContributorURI(Constants.PLUGIN_URI);
				item.setElementId("recentFilesItem" + items.size()); //$NON-NLS-1$
				item.setLabel(convertValue(value));
				item.setCommand(openCommand);
				item.getParameters().add(param);
				items.add(item);
			}
		}
	}

	private String convertValue(final String nodeValue) {
		final int index = nodeValue.lastIndexOf(FILE_SEPARATOR);
		if (index == -1) {
			return nodeValue;
		}

		final String fileName = nodeValue.substring(index + 1);
		final String pathName = nodeValue.substring(0, index);
		return fileName + " (" + pathName + ")"; //$NON-NLS-1$//$NON-NLS-2$
	}

}
