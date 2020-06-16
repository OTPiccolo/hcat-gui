package net.emb.hcat.gui.core.lifecycle;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;

import javax.inject.Inject;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.Bundle;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import net.emb.hcat.gui.core.Constants;
import net.emb.hcat.gui.core.EventTopics;
import net.emb.hcat.gui.core.imports.SelectionWizard;

public class WorkspaceSelectionManager {

	@Inject
	private IEventBroker broker;

	@PostContextCreate
	void postContextCreate(final IApplicationContext appContext, final IEclipseContext eclipseContext, final Display display) {
		configureLogger(appContext);

		final Shell shell = new Shell(display, SWT.SHELL_TRIM);

		// final IEclipseContext eclipseContext =
		// EclipseContextFactory.create();
		final SelectionWizard wizard = ContextInjectionFactory.make(SelectionWizard.class, eclipseContext);

		final WizardDialog dialog = new WizardDialog(shell, wizard);

		// close the static splash screen
		appContext.applicationRunning();

		// position the shell
		setLocation(display, shell);

		if (dialog.open() != Window.OK) {
			// close the application
			System.exit(-1);
		}
		shell.dispose();
		final String workspace = wizard.getWorkspace();

		eclipseContext.set(Constants.WORKSPACE_CONTEXT_ID, Paths.get(workspace));
		broker.post(EventTopics.WORKING_DIRECTORY, Paths.get(workspace));
	}

	private void setLocation(final Display display, final Shell shell) {
		final Monitor monitor = display.getPrimaryMonitor();
		final Rectangle monitorRect = monitor.getBounds();
		final Rectangle shellRect = shell.getBounds();
		final int x = monitorRect.x + (monitorRect.width - shellRect.width) / 2;
		final int y = monitorRect.y + (monitorRect.height - shellRect.height) / 2;
		shell.setLocation(x, y);
	}

	private void configureLogger(final IApplicationContext appContext) {
		final Bundle bundle = appContext.getBrandingBundle();

		final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
		final JoranConfigurator jc = new JoranConfigurator();
		jc.setContext(context);
		context.reset();

		// Get the configuration location where the logback.xml is located
		final Location configurationLocation = Platform.getInstallLocation();
		final File logbackFile = new File(configurationLocation.getURL().getPath(), "logback.xml"); //$NON-NLS-1$
		try {
			if (logbackFile.exists()) {
				jc.doConfigure(logbackFile);
			} else {
				// Fallback. This assumes that the logback.xml file is in the
				// root of the bundle.
				final URL logbackConfigFileUrl = FileLocator.find(bundle, new Path("logback.xml"), null); //$NON-NLS-1$
				jc.doConfigure(logbackConfigFileUrl);
			}
		} catch (final JoranException e) {
			e.printStackTrace();
		}
	}

}
