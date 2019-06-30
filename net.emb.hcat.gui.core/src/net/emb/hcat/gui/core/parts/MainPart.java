package net.emb.hcat.gui.core.parts;

import java.util.List;

import javax.annotation.PostConstruct;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;

import net.emb.hcat.cli.Sequence;
import net.emb.hcat.cli.haplotype.Haplotype;

public class MainPart {

	private String id;
	private List<Sequence> sequences;
	private List<Haplotype> haplotypes;

	@PostConstruct
	public void createComposite(final Composite parent) {
		parent.setLayout(new FillLayout());
		final TabFolder folder = new TabFolder(parent, SWT.BOTTOM);

	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public List<Sequence> getSequences() {
		return sequences;
	}

	public void setSequences(final List<Sequence> sequences) {
		this.sequences = sequences;
		haplotypes = sequences == null ? null : Haplotype.createHaplotypes(sequences);
	}

	public List<Haplotype> getHaplotypes() {
		return haplotypes;
	}

}
