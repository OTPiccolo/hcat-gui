package net.emb.hcat.gui.core.imports;

import net.emb.hcat.gui.core.base.HumanReadableSelection;
import net.emb.hcat.gui.core.messages.Messages;

public enum WorkspaceModus implements HumanReadableSelection {
	
	/**
	 * The selected {@link HcatFunction}s for the current workspace are processed at opening of each file
	 */
	SINGLE_FUNCTION_PROCESSION(Messages.WorkspaceModus_SingleFunctionProcession), 
	
	/**
	 * The selected {@link HcatFunction}s for the current workspace are processed for each file at opening of the workspace
	 */
	BATCH_FUNCTION_PROCESSION(Messages.WorkspaceModus_BatchFunctionProcession);
	
	private String displayName;
	
	private WorkspaceModus(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}
}
