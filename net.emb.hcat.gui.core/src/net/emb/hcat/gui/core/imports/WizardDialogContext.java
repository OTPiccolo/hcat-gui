package net.emb.hcat.gui.core.imports;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.emb.hcat.gui.core.base.BaseContext;

public class WizardDialogContext extends BaseContext {

	private String name;

	private File workspace;

	private List<HcatFunction> autoFunctions = new ArrayList<HcatFunction>(5);

	/**
	 * The workspace can open in two different modus.
	 */
	private WorkspaceModus workspaceModus;

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		firePropertyChange("name", this.name, this.name = name);
	}

	public File getWorkspace() {
		return workspace;
	}

	public void setWorkspace(final File workspace) {
		firePropertyChange("workspace", this.workspace, this.workspace = workspace);
	}

	public List<HcatFunction> getAutoFunctions() {
		return autoFunctions;
	}

	public void setAutoFunctions(final List<HcatFunction> autoFunctions) {
		this.autoFunctions = autoFunctions;
		firePropertyChange("autoFunctions", Collections.emptyList(), this.autoFunctions = autoFunctions);
	}

	public void addAutoFunctions(final HcatFunction autoFunction) {
		autoFunctions.add(autoFunction);
		firePropertyChange("autoFunctions", Collections.emptyList(), this.autoFunctions);
	}

	public WorkspaceModus getWorkspaceModus() {
		return workspaceModus;
	}

	public void setWorkspaceModus(final WorkspaceModus workspaceModus) {
		firePropertyChange("workspaceModus", this.workspaceModus, this.workspaceModus = workspaceModus);
	}

}
