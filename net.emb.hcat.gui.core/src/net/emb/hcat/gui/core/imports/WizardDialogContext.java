package net.emb.hcat.gui.core.imports;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.emb.hcat.gui.core.base.BaseContext;

public class WizardDialogContext extends BaseContext {
	
	private String name;
	
	private File workspace;
	
	List<HcatFunction> autoFunctions = new ArrayList<HcatFunction>(5);
	
	/**
	 * The workspace can open in two different modus. 
	 */
	WorkspaceModus workspaceModus;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		firePropertyChange("name", this.name, this.name = name);
	}

	public File getWorkspace() {
		return workspace;
	}

	public void setWorkspace(File workspace) {
		firePropertyChange("workspace", this.workspace, this.workspace = workspace);
	}

	public List<HcatFunction> getAutoFunctions() {
		return autoFunctions;
	}

	public void setAutoFunctions(List<HcatFunction> autoFunctions) {
		this.autoFunctions = autoFunctions;
		firePropertyChange("autoFunctions", Collections.emptyList(), this.autoFunctions = autoFunctions);
	}
	
	public void addAutoFunctions(HcatFunction autoFunction) {
		autoFunctions.add(autoFunction);
		firePropertyChange("autoFunctions", Collections.emptyList(), this.autoFunctions);
	}

	public WorkspaceModus getWorkspaceModus() {
		return workspaceModus;
	}

	public void setWorkspaceModus(WorkspaceModus workspaceModus) {
		firePropertyChange("workspaceModus", this.workspaceModus, this.workspaceModus = workspaceModus);
	}
	
	
}
