package net.emb.hcat.gui.core.parts;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.UIEvents.EventTags;
import org.eclipse.e4.ui.workbench.UIEvents.UILifeCycle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import net.emb.hcat.cli.Sequence;
import net.emb.hcat.cli.haplotype.Haplotype;
import net.emb.hcat.gui.core.EventTopics;
import net.emb.hcat.gui.core.components.TranslationComponent;
import net.emb.hcat.gui.core.components.DistanceMatrixComponent;
import net.emb.hcat.gui.core.components.OverviewComponent;
import net.emb.hcat.gui.core.messages.Messages;

public class MainPart {

	@Inject
	private IEventBroker broker;

	private String id;
	private List<Sequence> sequences;
	private List<Haplotype> haplotypes;

	private OverviewComponent overview;
	private DistanceMatrixComponent matrix;
	private TranslationComponent codon;

	private EventHandler partListener;

	@PostConstruct
	public void createComposite(final Composite parent, final IEclipseContext context) {
		parent.setLayout(new FillLayout());
		final TabFolder folder = new TabFolder(parent, SWT.BOTTOM);

		final TabItem overviewItem = new TabItem(folder, SWT.NONE);
		overviewItem.setText(Messages.MainPart_OverviewTab);
		overview = ContextInjectionFactory.make(OverviewComponent.class, context);
		overview.createComposite(folder);
		overviewItem.setControl(overview.getControl());

		final TabItem matrixItem = new TabItem(folder, SWT.NONE);
		matrixItem.setText(Messages.MainPart_DistanceMatrixTab);
		matrix = ContextInjectionFactory.make(DistanceMatrixComponent.class, context);
		matrix.createComposite(folder);
		matrixItem.setControl(matrix.getControl());

		final TabItem translationItem = new TabItem(folder, SWT.NONE);
		translationItem.setText(Messages.MainPart_TranslationTab);
		codon = ContextInjectionFactory.make(TranslationComponent.class, context);
		codon.createComposite(folder);
		translationItem.setControl(codon.getControl());

		partListener = e -> handleActivate(e);
		broker.subscribe(UILifeCycle.ACTIVATE, partListener);
	}

	@PreDestroy
	public void destroy() {
		if (partListener != null) {
			broker.unsubscribe(partListener);
		}
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
		updateItems();
	}

	private void updateItems() {
		overview.setModel(getHaplotypes(), getSequences());
		matrix.setModel(getHaplotypes());
		codon.setModel(getSequences());
	}

	public List<Haplotype> getHaplotypes() {
		return haplotypes;
	}

	private void handleActivate(final Event e) {
		if (isPart(e)) {
			broker.post(EventTopics.ACTIVE_SEQUENCES, getSequences());
			broker.post(EventTopics.ACTIVE_HAPLOTYPES, getHaplotypes());
		}
	}

	private boolean isPart(final Event e) {
		final Object element = e.getProperty(EventTags.ELEMENT);
		return element instanceof MPart && ((MPart) element).getObject() == this;
	}

}
