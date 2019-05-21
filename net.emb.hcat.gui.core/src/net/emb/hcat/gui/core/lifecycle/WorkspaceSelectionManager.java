package net.emb.hcat.gui.core.lifecycle;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

import net.emb.hcat.gui.core.imports.SelectionWizard;

public class WorkspaceSelectionManager {
	@PostContextCreate
    void postContextCreate(IApplicationContext appContext, Display display) {
		final Shell shell = new Shell(SWT.SHELL_TRIM);
		
		IEclipseContext eclipseContext = EclipseContextFactory.create();
		IWizard wizard = ContextInjectionFactory.make(SelectionWizard.class, eclipseContext);

		WizardDialog dialog = new WizardDialog(shell, wizard);
		
        // close the static splash screen
        appContext.applicationRunning();

        // position the shell
        setLocation(display, shell);

        if (dialog.open() != Window.OK) {
            // close the application
            System.exit(-1);
        }
    }

    private void setLocation(Display display, Shell shell) {
        Monitor monitor = display.getPrimaryMonitor();
        Rectangle monitorRect = monitor.getBounds();
        Rectangle shellRect = shell.getBounds();
        int x = monitorRect.x + (monitorRect.width - shellRect.width) / 2;
        int y = monitorRect.y + (monitorRect.height - shellRect.height) / 2;
        shell.setLocation(x, y);
    }
}
