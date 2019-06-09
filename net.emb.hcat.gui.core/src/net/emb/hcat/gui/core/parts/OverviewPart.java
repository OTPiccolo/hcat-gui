package net.emb.hcat.gui.core.parts;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
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

import net.emb.hcat.cli.Sequence;
import net.emb.hcat.cli.haplotype.Haplotype;

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

	private static final List<Sequence> loadSequences() {
		// Load your sequences.
		return null;
	}

	private GridTableViewer tableViewer;

	@Inject
	private MDirtyable dirty;

	private List<Sequence> seqModel;
	private List<Haplotype> haploModel;

	@PostConstruct
	public void createComposite(final Composite parent) {
		parent.setLayout(new GridLayout(1, false));

		seqModel = createSeqDataModel();
		haploModel = createHaploDataModel();

		tableViewer = createViewer(parent);
		tableViewer.getGrid().setLayoutData(new GridData(GridData.FILL_BOTH));

		final Button button = createSwitch(parent);
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		tableViewer.setInput(seqModel);
	}

	private GridTableViewer createViewer(final Composite parent) {
		final GridTableViewer viewer = new GridTableViewer(parent);
		viewer.setContentProvider(ArrayContentProvider.getInstance());

		final GridViewerColumn idColumn = new GridViewerColumn(viewer, SWT.NONE);
		idColumn.getColumn().setText("ID");
		idColumn.getColumn().setWidth(200);
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

		final ColumnSorter sorter = new ColumnSorter(viewer);
		sorter.applySorting();

		final Grid grid = viewer.getGrid();
		grid.setHeaderVisible(true);
		grid.setAutoHeight(true);

		return viewer;
	}

	private Button createSwitch(final Composite parent) {
		final Button button = new Button(parent, SWT.CHECK);
		button.setText("Show as Haplotypes");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				if (button.getSelection()) {
					tableViewer.setInput(haploModel);
				} else {
					tableViewer.setInput(seqModel);
				}
			}
		});

		return button;
	}

	@Focus
	public void setFocus() {
		tableViewer.getGrid().setFocus();
	}

	@Persist
	public void save() {
		dirty.setDirty(false);
	}

	private List<Sequence> createSeqDataModel() {
		return loadSequences();
	}

	private List<Haplotype> createHaploDataModel() {
		return Haplotype.createHaplotypes(seqModel);
	}
}