package net.emb.hcat.gui.core.imports;

import net.emb.hcat.gui.core.base.HumanReadableSelection;
import net.emb.hcat.gui.core.messages.Messages;

public enum HcatFunction implements HumanReadableSelection {

	HAPLOTYP_ANALYSIS(Messages.HcatFunction_HaplotypeAnalysis), INDICES(Messages.HcatFunction_Indices), SAVE_DISTINCT_HAPLOTYPES(Messages.HcatFunction_SaveHaplotypes), SEQUENCE_TRANSLATION(Messages.HcatFunction_SequenceTranslation), SAVE_CUTS(Messages.HcatFunction_CutSequences),
	CONVERT_FILE(Messages.HcatFunction_ExportConvertedFile);

	private String displayName;

	private HcatFunction(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}
}
