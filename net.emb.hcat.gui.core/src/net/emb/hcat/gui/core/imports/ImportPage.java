package net.emb.hcat.gui.core.imports;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import net.emb.hcat.gui.core.messages.Messages;

public class ImportPage extends WizardPage {

	public ImportPage() {
		super("importPage"); //$NON-NLS-1$
		setTitle(Messages.ImportPage_Title);
		setDescription(Messages.ImportPage_Description);
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);

		setControl(container);
	}

}
