package net.emb.hcat.gui.core.imports;

import org.eclipse.jface.wizard.Wizard;

import net.emb.hcat.gui.core.messages.Messages;

public class SelectionWizard extends Wizard {

	private SelectionPage page;

	public SelectionWizard() {
		setWindowTitle(Messages.SelectionWizard_Title);
	}

	@Override
	public void addPages() {
		page = new SelectionPage();
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	public String getWorkspace() {
		return page.getWorkspaceStr();
	}

}
