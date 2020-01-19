package net.emb.hcat.gui.core.components;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import net.emb.hcat.cli.haplotype.Haplotype;
import net.emb.hcat.cli.sequence.Sequence;
import net.emb.hcat.gui.core.EventTopics;
import net.emb.hcat.gui.core.messages.Messages;
import net.emb.hcat.gui.core.parts.ColumnSorter;

/**
 * Component to display an overview over sequences and haplotypes.
 *
 * @author OT Piccolo
 */
public class OverviewComponent {

	// Maybe use StyledCellLabelProvider?
	private static abstract class ColumnLabelProvider extends org.eclipse.jface.viewers.ColumnLabelProvider {
		@Override
		public String getText(final Object element) {
			if (element instanceof Sequence) {
				return getText((Sequence) element);
			}
			if (element instanceof Haplotype) {
				return getText((Haplotype) element);
			}
			return super.getText(element);
		}

		protected abstract String getText(final Sequence sequence);

		protected abstract String getText(final Haplotype haplotype);
	}

	@SuppressWarnings("unused")
	private static abstract class EditingSupport extends org.eclipse.jface.viewers.EditingSupport {

		public EditingSupport(final ColumnViewer viewer) {
			super(viewer);
		}

		@Override
		protected boolean canEdit(final Object element) {
			if (element instanceof Sequence) {
				return canEdit((Sequence) element);
			}
			if (element instanceof Haplotype) {
				return canEdit((Haplotype) element);
			}
			return false;
		}

		protected boolean canEdit(final Sequence sequence) {
			return false;
		}

		protected boolean canEdit(final Haplotype haplotype) {
			return false;
		}

		@Override
		protected Object getValue(final Object element) {
			if (element instanceof Sequence) {
				return getValue((Sequence) element);
			}
			if (element instanceof Haplotype) {
				return getValue((Haplotype) element);
			}
			return null;
		}

		protected Object getValue(final Sequence sequence) {
			return sequence.toString();
		}

		protected Object getValue(final Haplotype haplotype) {
			return haplotype.toString();
		}

		@Override
		public void setValue(final Object element, final Object value) {
			if (element instanceof Sequence) {
				setValue((Sequence) element, value);
			}
			if (element instanceof Haplotype) {
				setValue((Haplotype) element, value);
			}
			getViewer().update(element, null);
		}

		public void setValue(final Sequence element, final Object value) {
			// Do nothing.
		}

		public void setValue(final Haplotype element, final Object value) {
			// Do nothing
		}

		@Override
		protected CellEditor getCellEditor(final Object element) {
			if (element instanceof Sequence) {
				return getCellEditor((Sequence) element);
			}
			if (element instanceof Haplotype) {
				return getCellEditor((Haplotype) element);
			}
			return null;
		}

		protected CellEditor getCellEditor(final Sequence element) {
			return null;
		}

		protected CellEditor getCellEditor(final Haplotype element) {
			return null;
		}
	}

	private Control control;
	private GridTableViewer tableViewer;

	@Inject
	private IEventBroker broker;

	private List<Sequence> seqModel;
	private List<Haplotype> haploModel;

	private boolean showAsSequnces;

	/**
	 * Creates this component.
	 *
	 * @param parent
	 *            The parent composite
	 * @return The top control of this component.
	 */
	public Control createComposite(final Composite parent) {
		final Composite body = new Composite(parent, SWT.NONE);
		body.setLayout(new GridLayout(1, false));

		tableViewer = createViewer(body);
		tableViewer.getGrid().setLayoutData(new GridData(GridData.FILL_BOTH));

		final Button button = createSwitch(body);
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		control = body;
		return body;
	}

	private GridTableViewer createViewer(final Composite parent) {
		final GridTableViewer viewer = new GridTableViewer(parent);
		viewer.setContentProvider(ArrayContentProvider.getInstance());

		final GridViewerColumn idColumn = new GridViewerColumn(viewer, SWT.NONE);
		idColumn.getColumn().setText(Messages.OverviewComponent_IdColumn);
		idColumn.getColumn().setWidth(150);
		idColumn.getColumn().setWordWrap(false);
		idColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			protected String getText(final Sequence sequence) {
				return sequence.getName();
			}

			@Override
			protected String getText(final Haplotype haplotype) {
				return haplotype.getName();
			}
		});
		idColumn.setEditingSupport(new EditingSupport(viewer) {
			@Override
			protected boolean canEdit(final Haplotype haplotype) {
				return true;
			}

			@Override
			protected Object getValue(final Haplotype haplotype) {
				return haplotype.getName();
			}

			@Override
			public void setValue(final Haplotype element, final Object value) {
				element.setName((String) value);
				broker.post(EventTopics.UPDATE_HAPLOTYPE, element);
			}

			@Override
			protected CellEditor getCellEditor(final Haplotype element) {
				return new TextCellEditor(viewer.getGrid());
			}
		});

		final GridViewerColumn nameColumn = new GridViewerColumn(viewer, SWT.NONE);
		nameColumn.getColumn().setText(Messages.OverviewComponent_SeqNameColumn);
		nameColumn.getColumn().setWidth(150);
		nameColumn.getColumn().setWordWrap(true);
		nameColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			protected String getText(final Sequence sequence) {
				return sequence.getName();
			}

			@Override
			protected String getText(final Haplotype haplotype) {
				String text = haplotype.stream().map(seq -> seq.getName() == null ? Messages.OverviewComponent_NotAvailable : seq.getName()).reduce("", (a, b) -> a + "\n" + b); //$NON-NLS-1$ //$NON-NLS-2$
				if (!text.isEmpty()) {
					text = text.substring(1);
				}
				return text;
			}
		});

		final GridViewerColumn seqColumn = new GridViewerColumn(viewer, SWT.NONE);
		seqColumn.getColumn().setText(Messages.OverviewComponent_SequenceColumn);
		seqColumn.getColumn().setWidth(500);
		seqColumn.getColumn().setWordWrap(true);
		seqColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			protected String getText(final Sequence sequence) {
				return sequence.getValue();
			}

			@Override
			protected String getText(final Haplotype haplotype) {
				return haplotype.getFirstSequence().getValue();
			}
		});

		viewer.addPostSelectionChangedListener(e -> broker.post(showAsSequnces ? EventTopics.SELECTED_SEQUENCE : EventTopics.SELECTED_HAPLOTYPE, e.getStructuredSelection().getFirstElement()));

		final ColumnSorter sorter = new ColumnSorter(viewer);
		sorter.applySorting();

		final Grid grid = viewer.getGrid();
		grid.setCellSelectionEnabled(true);
		grid.setHeaderVisible(true);
		grid.setAutoHeight(true);
		grid.enableDefaultKeyListener();

		return viewer;
	}

	private Button createSwitch(final Composite parent) {
		final Button button = new Button(parent, SWT.CHECK);
		button.setText(Messages.OverviewComponent_ShowAsSequenceButton);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				showAsSequnces = button.getSelection();
				updateViewer();
			}
		});

		return button;
	}

	/**
	 * Gets the top control of this component.
	 *
	 * @return The control, or <code>null</code> if not yet created.
	 */
	public Control getControl() {
		return control;
	}

	/**
	 * Sets focus to this component.
	 */
	@Focus
	public void setFocus() {
		if (tableViewer != null && !tableViewer.getGrid().isDisposed()) {
			tableViewer.getGrid().setFocus();
		}
	}

	/**
	 * Sets the model this component is working on.
	 *
	 * @param haplotypes
	 *            The haplotypes this component is working on. Must be the
	 *            haplotypes corresponding to the given sequences.
	 * @param sequences
	 *            The sequences this component is working on.
	 */
	public void setModel(final List<Haplotype> haplotypes, final List<Sequence> sequences) {
		seqModel = sequences;
		haploModel = haplotypes;
		updateViewer();
	}

	private void updateViewer() {
		tableViewer.setInput(showAsSequnces ? seqModel : haploModel);
	}

	/**
	 * Updates the given haplotype.
	 * 
	 * @param haplotype
	 *            The haplotype that has been changed.
	 */
	@Inject
	@Optional
	public void updateHaplotype(@UIEventTopic(EventTopics.UPDATE_HAPLOTYPE) final Haplotype haplotype) {
		if (haplotype != null && tableViewer != null && !tableViewer.getGrid().isDisposed()) {
			tableViewer.update(haplotype, null);
		}
	}

	/**
	 * Whether the component shows sequences.
	 *
	 * @return <code>true</code>, if sequences are shown, <code>false</code>
	 *         otherwise.
	 * @see #isShowAsHaplotypes()
	 */
	public boolean isShowAsSequences() {
		return showAsSequnces;
	}

	/**
	 * Whether the component shows haplotypes.
	 *
	 * @return <code>true</code>, if haplotypes are shown, <code>false</code>
	 *         otherwise.
	 * @see #isShowAsSequences()
	 */
	public boolean isShowAsHaplotypes() {
		return !showAsSequnces;
	}

	/**
	 * Gets the currently selected sequence.
	 *
	 * @return The currently selected sequence, or <code>null</code>, if no
	 *         sequence is selected or haplotypes are shown.
	 * @see #isShowAsSequences()
	 */
	public Sequence getSelectedSequence() {
		return isShowAsSequences() ? (Sequence) ((IStructuredSelection) tableViewer.getSelection()).getFirstElement() : null;
	}

	/**
	 * Gets the currently selected haplotype.
	 *
	 * @return The currently selected haplotype, or <code>null</code>, if no
	 *         sequence is selected or sequences are shown.
	 * @see #isShowAsHaplotypes()
	 */
	public Haplotype getSelectedHaplotype() {
		return isShowAsHaplotypes() ? (Haplotype) ((IStructuredSelection) tableViewer.getSelection()).getFirstElement() : null;
	}

}