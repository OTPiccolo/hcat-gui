package net.emb.hcat.gui.core.lifecycle;

import java.nio.file.Paths;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

import net.emb.hcat.gui.core.Constants;
import net.emb.hcat.gui.core.EventTopics;
import net.emb.hcat.gui.core.imports.SelectionWizard;

public class WorkspaceSelectionManager {

	@Inject
	private IEventBroker broker;

	@PostContextCreate
	void postContextCreate(final IApplicationContext appContext, final IEclipseContext eclipseContext, final Display display) {
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

		eclipseContext.set(Constants.WORKSPACE_CONTEXT, Paths.get(workspace));
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
}
