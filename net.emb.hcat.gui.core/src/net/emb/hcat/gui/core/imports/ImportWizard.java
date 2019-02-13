package net.emb.hcat.gui.core.imports;

import org.eclipse.jface.wizard.Wizard;

import net.emb.hcat.gui.core.messages.Messages;

public class ImportWizard extends Wizard {

	public ImportWizard() {
		setWindowTitle(Messages.ImportWizard_Title);
	}

	@Override
	public void addPages() {
		addPage(new ImportPage());
	}

	@Override
	public boolean performFinish() {
		return true;
	}

}
