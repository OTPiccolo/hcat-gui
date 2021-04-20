package net.emb.hcat.gui.core.parts;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.PersistState;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.UIEvents.EventTags;
import org.eclipse.e4.ui.workbench.UIEvents.UILifeCycle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.emb.hcat.cli.ErrorCodeException;
import net.emb.hcat.cli.haplotype.Haplotype;
import net.emb.hcat.cli.io.sequence.ESequenceType;
import net.emb.hcat.cli.io.sequence.ISequenceReader;
import net.emb.hcat.cli.io.sequence.ISequenceWriter;
import net.emb.hcat.cli.sequence.Sequence;
import net.emb.hcat.gui.core.EventTopics;
import net.emb.hcat.gui.core.components.DistanceMatrixComponent;
import net.emb.hcat.gui.core.components.HaplotypeTableComponent;
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

	private static final Logger log = LoggerFactory.getLogger(MainPart.class);

	private static final String PERSIST_ID_MAIN_ID = "ID"; //$NON-NLS-1$
	private static final String PERSIST_ID_MAIN_SEQUENCES = "Sequences"; //$NON-NLS-1$
	private static final String PERSIST_ID_MAIN_DISPLAYED_COMPONENT = "DisplayedComponent"; //$NON-NLS-1$
	private static final String PERSIST_ID_OVERVIEW_SHOW_AS_SEQUENCES = "ShowAsSequences"; //$NON-NLS-1$
	private static final String PERSIST_ID_HAPLOTYPETABLE_MASTER_ID = "HaplotypeTable.MasterId"; //$NON-NLS-1$

	/**
	 * Describes which content is currently displayed in this part.
	 *
	 * @author OT Piccolo
	 */
	@SuppressWarnings("javadoc")
	public enum DISPLAYED_CONTENT {
		SEQUENCES, HAPLOTYPES, HAPLOTYPE_TABLE, DISTANCE_MATRIX, TEXT_LOG;
	}

	@Inject
	private IEventBroker broker;

	private String id;
	private List<Sequence> sequences;
	private List<Haplotype> haplotypes;

	private TabFolder folder;
	private OverviewComponent overview;
	private HaplotypeTableComponent haplotypeTable;
	private DistanceMatrixComponent matrix;
	private TextLogComponent textLog;

	private EventHandler partListener;

	/**
	 * Constructor.
	 *
	 * @param parent
	 *            Parent composite.
	 * @param part
	 *            The part this part will be created in.
	 */
	@PostConstruct
	public void createComposite(final Composite parent, final MPart part) {
		final IEclipseContext context = part.getContext();

		parent.setLayout(new FillLayout());
		folder = new TabFolder(parent, SWT.BOTTOM);

		final TabItem overviewItem = new TabItem(folder, SWT.NONE);
		overviewItem.setText(Messages.MainPart_OverviewTab);
		overview = ContextInjectionFactory.make(OverviewComponent.class, context);
		overview.createComposite(folder);
		overviewItem.setControl(overview.getControl());

		final TabItem haplotypeItem = new TabItem(folder, SWT.NONE);
		haplotypeItem.setText(Messages.MainPart_HaplotypeTable);
		haplotypeTable = ContextInjectionFactory.make(HaplotypeTableComponent.class, context);
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

		restoreState(part.getPersistedState());
	}

	private void restoreState(final Map<String, String> state) {
		if (!state.containsKey(PERSIST_ID_MAIN_ID)) {
			return;
		}

		final String id = state.get(PERSIST_ID_MAIN_ID);
		log.debug("Restoring state for: {}", id); //$NON-NLS-1$

		setId(id);

		final StringReader reader = new StringReader(state.get(PERSIST_ID_MAIN_SEQUENCES));
		final ISequenceReader csvReader = ESequenceType.CSV.createReader(reader);
		try {
			setSequences(csvReader.read());
		} catch (final ErrorCodeException e) {
			log.error("Could not restore state of \"" + getId() + "\". Error message: " + e.getMessage(), e); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}

		if (state.containsKey(PERSIST_ID_OVERVIEW_SHOW_AS_SEQUENCES)) {
			if (Boolean.parseBoolean(state.get(PERSIST_ID_OVERVIEW_SHOW_AS_SEQUENCES))) {
				overview.setShowAsSequnces();
			} else {
				overview.setShowAsHaplotypes();
			}
		}

		if (state.containsKey(PERSIST_ID_HAPLOTYPETABLE_MASTER_ID)) {
			final String masterId = state.get(PERSIST_ID_HAPLOTYPETABLE_MASTER_ID);
			for (final Sequence sequence : sequences) {
				if (masterId.equals(sequence.getName())) {
					if (overview.isShowAsHaplotypes()) {
						final Haplotype haplotype = Haplotype.find(sequence, haplotypes);
						haplotypeTable.setSelectedHaplotype(haplotype);
					} else {
						haplotypeTable.setSelectedSequence(sequence);
					}
					break;
				}
			}
		}

		if (state.containsKey(PERSIST_ID_MAIN_DISPLAYED_COMPONENT)) {
			DISPLAYED_CONTENT component = null;
			try {
				component = DISPLAYED_CONTENT.valueOf(state.get(PERSIST_ID_MAIN_DISPLAYED_COMPONENT));
			} catch (final IllegalArgumentException e) {
				// Should never happen.
				log.error("Unknown value for displayed component encountered on restoring part: " + state.get(PERSIST_ID_MAIN_DISPLAYED_COMPONENT), e); //$NON-NLS-1$
			}
			setDisplayedContent(component);
		}
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

	/**
	 * Persisting state when HCAT is closed.
	 *
	 * @param part
	 *            The part this part belongs to.
	 */
	@PersistState
	public void persist(final MPart part) {
		if (!part.isToBeRendered() || sequences == null || sequences.isEmpty()) {
			return;
		}

		log.debug("Persisting state of: {}", getId()); //$NON-NLS-1$
		final Map<String, String> state = part.getPersistedState();

		final StringWriter writer = new StringWriter(sequences.size() * (sequences.get(0).getLength() + 100));
		final ISequenceWriter csvWriter = ESequenceType.CSV.createWriter(writer);
		try {
			csvWriter.write(sequences);
		} catch (final IOException e) {
			log.error("Could not persist state of \"" + getId() + "\". Error message: " + e.getMessage(), e); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
		state.put(PERSIST_ID_MAIN_SEQUENCES, writer.toString());

		state.put(PERSIST_ID_MAIN_ID, getId());

		state.put(PERSIST_ID_MAIN_DISPLAYED_COMPONENT, getDisplayedContent().name());

		state.put(PERSIST_ID_OVERVIEW_SHOW_AS_SEQUENCES, Boolean.toString(overview.isShowAsSequences()));

		final String masterId = (overview.isShowAsSequences() ? haplotypeTable.getSelectedSequence() : haplotypeTable.getSelectedHaplotype().getFirstSequence()).getName();
		if (masterId != null && !masterId.isBlank()) {
			state.put(PERSIST_ID_HAPLOTYPETABLE_MASTER_ID, masterId);
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
		return sequences == null ? null : Collections.unmodifiableList(sequences);
	}

	/**
	 * Sets all sequences that are displayed in this part. From these sequences,
	 * the haplotypes will be generated.
	 *
	 * @param sequences
	 *            The sequences. May be null to display nothing.
	 */
	public void setSequences(final List<Sequence> sequences) {
		this.sequences = sequences == null ? null : new ArrayList<Sequence>(sequences);
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

	/**
	 * Gets the selected master haplotype in the haplotype table component.
	 *
	 * @return The selected master haplotype, or <code>null</code> if it can't
	 *         be computed.
	 */
	public Haplotype getMasterHaplotype() {
		final Haplotype haplotype = haplotypeTable.getSelectedHaplotype();
		if (haplotype != null) {
			return haplotype;
		}

		final Sequence sequence = haplotypeTable.getSelectedSequence();
		return Haplotype.find(sequence, getHaplotypes());
	}

	/**
	 * Gets the displayed text log.
	 *
	 * @return The currently displayed text in the log.
	 */
	public String getTextLog() {
		return textLog.getTextLog();
	}

	/**
	 * Gets the currently displayed component of this part.
	 *
	 * @return The component that is currently displayed in the part.
	 */
	public DISPLAYED_CONTENT getDisplayedContent() {
		if (folder == null || folder.isDisposed()) {
			return null;
		}

		final TabItem item = folder.getItem(folder.getSelectionIndex());
		if (item.getControl() == overview.getControl()) {
			if (overview.isShowAsHaplotypes()) {
				return DISPLAYED_CONTENT.HAPLOTYPES;
			}
			return DISPLAYED_CONTENT.SEQUENCES;
		}

		if (item.getControl() == haplotypeTable.getControl()) {
			return DISPLAYED_CONTENT.HAPLOTYPE_TABLE;
		}

		if (item.getControl() == matrix.getControl()) {
			return DISPLAYED_CONTENT.DISTANCE_MATRIX;
		}

		if (item.getControl() == textLog.getControl()) {
			return DISPLAYED_CONTENT.TEXT_LOG;
		}

		// Should never happen as all tab items must be listed here.
		log.warn("Unknown tab selected: {}", item.getText()); //$NON-NLS-1$
		return null;
	}

	/**
	 * Sets the displayed component of this part.
	 *
	 * @param displayedComponent
	 *            The component that is currently displayed in the part.
	 */
	public void setDisplayedContent(final DISPLAYED_CONTENT displayedComponent) {
		if (folder == null || folder.isDisposed() || displayedComponent == null) {
			return;
		}

		Control lookup = null;
		switch (displayedComponent) {
		case DISTANCE_MATRIX:
			lookup = matrix.getControl();
			break;

		case HAPLOTYPES:
			overview.setShowAsHaplotypes();
			lookup = overview.getControl();
			break;

		case HAPLOTYPE_TABLE:
			lookup = haplotypeTable.getControl();
			break;

		case SEQUENCES:
			overview.setShowAsSequnces();
			lookup = overview.getControl();
			break;

		case TEXT_LOG:
			lookup = textLog.getControl();
			break;

		default:
			// Should never happen as all components need to be listed here.
			log.warn("Unknown component to display selected: {}", displayedComponent); //$NON-NLS-1$
			break;
		}

		for (final TabItem item : folder.getItems()) {
			if (item.getControl() == lookup) {
				folder.setSelection(item);
				break;
			}
		}
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
