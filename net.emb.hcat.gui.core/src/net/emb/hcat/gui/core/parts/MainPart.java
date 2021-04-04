package net.emb.hcat.gui.core.parts;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
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

import net.emb.hcat.cli.haplotype.Haplotype;
import net.emb.hcat.cli.sequence.Sequence;
import net.emb.hcat.gui.core.EventTopics;
import net.emb.hcat.gui.core.components.DistanceMatrixComponent;
import net.emb.hcat.gui.core.components.HaplotypeComponent;
import net.emb.hcat.gui.core.components.OverviewComponent;
import net.emb.hcat.gui.core.components.TextLogComponent;
import net.emb.hcat.gui.core.messages.Messages;

/**
 * Main part, containing all relevant information about a list of
 * sequences/haplotypes.
 *
 * @author OT Piccolo
 */
public class MainPart {

	@Inject
	private IEventBroker broker;

	private String id;
	private List<Sequence> sequences;
	private List<Haplotype> haplotypes;

	private OverviewComponent overview;
	private HaplotypeComponent haplotypeTable;
	private DistanceMatrixComponent matrix;
	private TextLogComponent textLog;

	private EventHandler partListener;

	/**
	 * Constructor.
	 *
	 * @param parent
	 *            Parent composite.
	 * @param context
	 *            Eclipse context to get information from.
	 */
	@PostConstruct
	public void createComposite(final Composite parent, final IEclipseContext context) {
		parent.setLayout(new FillLayout());
		final TabFolder folder = new TabFolder(parent, SWT.BOTTOM);

		final TabItem overviewItem = new TabItem(folder, SWT.NONE);
		overviewItem.setText(Messages.MainPart_OverviewTab);
		overview = ContextInjectionFactory.make(OverviewComponent.class, context);
		overview.createComposite(folder);
		overviewItem.setControl(overview.getControl());

		final TabItem haplotypeItem = new TabItem(folder, SWT.NONE);
		haplotypeItem.setText(Messages.MainPart_HaplotypeTable);
		haplotypeTable = ContextInjectionFactory.make(HaplotypeComponent.class, context);
		haplotypeTable.createComposite(folder);
		haplotypeItem.setControl(haplotypeTable.getControl());

		final TabItem matrixItem = new TabItem(folder, SWT.NONE);
		matrixItem.setText(Messages.MainPart_DistanceMatrixTab);
		matrix = ContextInjectionFactory.make(DistanceMatrixComponent.class, context);
		matrix.createComposite(folder);
		matrixItem.setControl(matrix.getControl());

		final TabItem textLogItem = new TabItem(folder, SWT.NONE);
		textLogItem.setText(Messages.MainPart_TextLogTab);
		textLog = ContextInjectionFactory.make(TextLogComponent.class, context);
		textLog.createComposite(folder);
		textLogItem.setControl(textLog.getControl());

		partListener = e -> handleActivate(e);
		broker.subscribe(UILifeCycle.ACTIVATE, partListener);
	}

	/**
	 * Destructor.
	 *
	 * @param part
	 *            The part that will be destroyed.
	 */
	@PreDestroy
	public void destroy(final MPart part) {
		if (partListener != null) {
			broker.unsubscribe(partListener);
			clearWorkbench(part);
		}
	}

	/**
	 * Focus method.
	 */
	@Focus
	public void setFocus() {
		if (overview != null) {
			overview.setFocus();
		}
	}

	// Hack that the HaplotypeTablePart will be cleared when no part is still
	// open.
	private void clearWorkbench(final MPart part) {
		for (final MUIElement element : part.getParent().getChildren()) {
			if (element.isToBeRendered()) {
				return;
			}
		}

		broker.post(EventTopics.ACTIVE_SEQUENCES, null);
		broker.post(EventTopics.ACTIVE_HAPLOTYPES, null);
	}

	/**
	 * Gets the ID of this part. It must be unique between all parts.
	 *
	 * @return The ID of this part.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the ID of this part. It must be unique between all parts.
	 *
	 * @param id
	 *            The ID of this part. Usually it is the path to the sequence
	 *            file that has been opened.
	 */
	public void setId(final String id) {
		this.id = id;
	}

	/**
	 * Gets all sequences that are displayed in this part.
	 *
	 * @return All sequences.
	 */
	public List<Sequence> getSequences() {
		return sequences;
	}

	/**
	 * Sets all sequences that are displayed in this part. From these sequences,
	 * the haplotypes will be generated.
	 *
	 * @param sequences
	 *            The sequences. May be null to display nothing.
	 */
	public void setSequences(final List<Sequence> sequences) {
		this.sequences = sequences;
		haplotypes = sequences == null ? null : Haplotype.wrap(sequences);
		updateComponents();
	}

	private void updateComponents() {
		overview.setModel(getHaplotypes(), getSequences());
		haplotypeTable.setModel(getHaplotypes());
		matrix.setModel(getHaplotypes());
		textLog.setModel(getHaplotypes());
		if (overview.isShowAsHaplotypes()) {
			haplotypeTable.setSelectedHaplotype(getHaplotypes().isEmpty() ? null : getHaplotypes().get(0));
		} else {
			haplotypeTable.setSelectedSequence(getSequences().isEmpty() ? null : getSequences().get(0));
		}
	}

	/**
	 * Gets all haplotypes that are displayed in this part.
	 *
	 * @return A list containing all haplotypes. Can be <code>null</code> if no
	 *         sequences have yet been set.
	 * @see #setSequences(List)
	 */
	public List<Haplotype> getHaplotypes() {
		return haplotypes;
	}

	private void handleActivate(final Event e) {
		if (isPart(e)) {
			broker.post(EventTopics.ACTIVE_SEQUENCES, getSequences());
			broker.post(EventTopics.ACTIVE_HAPLOTYPES, getHaplotypes());
			if (overview.isShowAsHaplotypes()) {
				broker.post(EventTopics.SELECTED_HAPLOTYPE, overview.getSelectedHaplotype());
			} else if (overview.isShowAsSequences()) {
				broker.post(EventTopics.SELECTED_SEQUENCE, overview.getSelectedSequence());
			}
		}
	}

	private boolean isPart(final Event e) {
		final Object element = e.getProperty(EventTags.ELEMENT);
		return element instanceof MPart && ((MPart) element).getObject() == this;
	}

	@SuppressWarnings({ "javadoc", "unused" })
	@Inject
	@Optional
	public void setSelectedHaplotype(@UIEventTopic(EventTopics.SELECTED_HAPLOTYPE) final Haplotype haplotype) {
		// A bit of a hack to make sure that when the 'show as sequence' button
		// in the overview component is pressed, the corresponding selection in
		// the haplotype table component is now using haplotypes.
		haplotypeTable.selectWithHaplotypes();
	}

	@SuppressWarnings({ "javadoc", "unused" })
	@Inject
	@Optional
	public void setSelectedSequence(@UIEventTopic(EventTopics.SELECTED_SEQUENCE) final Sequence sequence) {
		// A bit of a hack to make sure that when the 'show as sequence' button
		// in the overview component is pressed, the corresponding selection in
		// the haplotype table component is now using sequences.
		haplotypeTable.selectWithSequences();
	}

}
