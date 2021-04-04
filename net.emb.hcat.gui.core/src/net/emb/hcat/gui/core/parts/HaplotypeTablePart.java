package net.emb.hcat.gui.core.parts;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import net.emb.hcat.cli.haplotype.Haplotype;
import net.emb.hcat.cli.sequence.Sequence;
import net.emb.hcat.gui.core.EventTopics;
import net.emb.hcat.gui.core.components.HaplotypeComponent;

/**
 * Shows information about haplotypes and how they diverge from each other.
 *
 * @author OT Piccolo
 */
@SuppressWarnings("javadoc")
@Deprecated
public class HaplotypeTablePart {

	private HaplotypeComponent haplotype;

	@PostConstruct
	public void createComposite(final Composite parent, final IEclipseContext context) {
		parent.setLayout(new FillLayout());
		haplotype = ContextInjectionFactory.make(HaplotypeComponent.class, context);
		haplotype.createComposite(parent);
	}

	@Focus
	public void setFocus() {
		if (haplotype != null) {
			haplotype.setFocus();
		}
	}

	@Inject
	@Optional
	public void setActiveHaplotypes(@UIEventTopic(EventTopics.ACTIVE_HAPLOTYPES) final List<Haplotype> haplotypes) {
		haplotype.setModel(haplotypes);
	}

	@Inject
	@Optional
	public void setSelectedHaplotype(@UIEventTopic(EventTopics.SELECTED_HAPLOTYPE) final Haplotype haplotype) {
		this.haplotype.setSelectedHaplotype(haplotype);
	}

	@Inject
	@Optional
	public void setSelectedSequence(@UIEventTopic(EventTopics.SELECTED_SEQUENCE) final Sequence sequence) {
		haplotype.setSelectedSequence(sequence);
	}

	@Inject
	@Optional
	public void updateHaplotype(@UIEventTopic(EventTopics.UPDATE_HAPLOTYPE) final Haplotype haplotype) {
		this.haplotype.updateHaplotype(haplotype);
	}

}