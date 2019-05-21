package net.emb.hcat.gui.core.imports;

import org.eclipse.jface.wizard.Wizard;

import net.emb.hcat.gui.core.messages.Messages;

public class SelectionWizard extends Wizard {

	public SelectionWizard() {
		setWindowTitle(Messages.SelectionWizard_Title);
	}

	@Override
	public void addPages() {
		addPage(new SelectionPage());
	}

	@Override
	public boolean performFinish() {
		return true;
	}

}
