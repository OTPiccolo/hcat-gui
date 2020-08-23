package net.emb.hcat.gui.core.items;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;
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

	@SuppressWarnings("javadoc")
	@AboutToShow
	public void show(final List<MMenuElement> items) {
		try {
			final Preferences node = InstanceScope.INSTANCE.getNode(Constants.CORE_PREFERENCES_ID).node("recentFiles"); //$NON-NLS-1$
			final String[] keys = node.keys();
			Arrays.sort(keys);

			if (keys.length == 0) {
				final MDirectMenuItem item = MMenuFactory.INSTANCE.createDirectMenuItem();
				item.setElementId("noRecentFiles"); //$NON-NLS-1$
				item.setLabel(Messages.RecentFilesItem_noRecentFiles);
				item.setEnabled(false);
				items.add(item);
			} else {
				for (final String key : keys) {
					final String value = convertValue(node.get(key, null));
					final MDirectMenuItem item = MMenuFactory.INSTANCE.createDirectMenuItem();
					item.setElementId("recentFiles" + items.size()); //$NON-NLS-1$
					item.setLabel(value);
					items.add(item);
				}
			}
		} catch (final BackingStoreException e) {
			log.error(e.getMessage(), e);
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
