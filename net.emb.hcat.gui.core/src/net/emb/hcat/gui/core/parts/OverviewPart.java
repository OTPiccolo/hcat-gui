package net.emb.hcat.gui.core.parts;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.viewers.ArrayContentProvider;
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

import net.emb.hcat.cli.Sequence;
import net.emb.hcat.cli.haplotype.Haplotype;
import net.emb.hcat.gui.core.EventTopics;

public class OverviewPart {

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

	private Control control;
	private GridTableViewer tableViewer;

	@Inject
	private IEventBroker broker;

	private List<Sequence> seqModel;
	private List<Haplotype> haploModel;

	private boolean showAsSequnces;

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
		idColumn.getColumn().setText("ID");
		idColumn.getColumn().setWidth(200);
		idColumn.getColumn().setWordWrap(true);
		idColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			protected String getText(final Sequence sequence) {
				return sequence.getName();
			}

			@Override
			protected String getText(final Haplotype haplotype) {
				String text = haplotype.stream().map(seq -> seq.getName() == null ? "<N/A>" : seq.getName()).reduce("", (a, b) -> a + "\n" + b);
				if (!text.isEmpty()) {
					text = text.substring(1);
				}
				return text;
			}
		});

		final GridViewerColumn seqColumn = new GridViewerColumn(viewer, SWT.NONE);
		seqColumn.getColumn().setText("Sequence");
		seqColumn.getColumn().setWidth(400);
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
		grid.setHeaderVisible(true);
		grid.setAutoHeight(true);

		return viewer;
	}

	private Button createSwitch(final Composite parent) {
		final Button button = new Button(parent, SWT.CHECK);
		button.setText("Show as Sequences");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				showAsSequnces = button.getSelection();
				updateViewer();
			}
		});

		return button;
	}

	public Control getControl() {
		return control;
	}

	@Focus
	public void setFocus() {
		tableViewer.getGrid().setFocus();
	}

	public void setModel(final List<Haplotype> haplotypes, final List<Sequence> sequences) {
		seqModel = sequences;
		haploModel = haplotypes;
		updateViewer();
	}

	private void updateViewer() {
		tableViewer.setInput(showAsSequnces ? seqModel : haploModel);
	}

}