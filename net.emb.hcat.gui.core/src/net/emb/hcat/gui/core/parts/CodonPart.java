package net.emb.hcat.gui.core.parts;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;

import net.emb.hcat.cli.Sequence;
import net.emb.hcat.cli.codon.CodonTransformationData;
import net.emb.hcat.cli.codon.CodonTransformer;
import net.emb.hcat.cli.io.CodonTableReader;

public class CodonPart {

	private Control control;
	private ComboViewer comboViewer;
	private GridTableViewer tableViewer;

	private CodonTransformationData data;
	private Function<CodonTransformer, Sequence> transformation;

	private List<Sequence> seqModel;

	public Control createComposite(final Composite parent) {
		final Composite body = new Composite(parent, SWT.NONE);
		body.setLayout(new GridLayout(2, false));

		final Label labelCodon = new Label(body, SWT.NONE);
		labelCodon.setText("Codon Transformation:");
		labelCodon.setLayoutData(GridDataFactory.defaultsFor(labelCodon).create());

		comboViewer = createCombo(body);
		comboViewer.getCombo().setLayoutData(GridDataFactory.defaultsFor(comboViewer.getCombo()).create());
		comboViewer.setInput(CodonTableReader.readDefaultTable());

		final Label labelOffset = new Label(body, SWT.NONE);
		labelOffset.setText("Offset:");
		labelOffset.setLayoutData(GridDataFactory.defaultsFor(labelOffset).create());

		final Control radio = createRadio(body);
		radio.setLayoutData(GridDataFactory.defaultsFor(radio).create());

		tableViewer = createViewer(body);
		tableViewer.getGrid().setLayoutData(GridDataFactory.defaultsFor(tableViewer.getGrid()).span(2, 1).create());

		control = body;
		return control;
	}

	private ComboViewer createCombo(final Composite parent) {
		final ComboViewer viewer = new ComboViewer(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(final Object element) {
				return ((CodonTransformationData) element).name;
			}
		});
		viewer.setComparator(new ViewerComparator());
		viewer.addPostSelectionChangedListener(e -> {
			data = (CodonTransformationData) ((IStructuredSelection) viewer.getSelection()).getFirstElement();
			updateViewer();
		});
		return viewer;
	}

	private Control createRadio(final Composite parent) {
		final Composite body = new Composite(parent, SWT.NONE);
		body.setLayout(new RowLayout());

		final Button automaticButton = new Button(body, SWT.RADIO);
		automaticButton.setText("Automatic");
		automaticButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				transformation = t -> t.transformAuto();
				updateViewer();
			}
		});

		final Button o1Button = new Button(body, SWT.RADIO);
		o1Button.setText("1");
		o1Button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				transformation = t -> t.transform(0);
				updateViewer();
			}
		});

		final Button o2Button = new Button(body, SWT.RADIO);
		o2Button.setText("2");
		o2Button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				transformation = t -> t.transform(1);
				updateViewer();
			}
		});

		final Button o3Button = new Button(body, SWT.RADIO);
		o3Button.setText("3");
		o3Button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				transformation = t -> t.transform(2);
				updateViewer();
			}
		});

		automaticButton.setSelection(true);
		automaticButton.notifyListeners(SWT.Selection, new Event());

		return body;
	}

	private GridTableViewer createViewer(final Composite parent) {
		final GridTableViewer viewer = new GridTableViewer(parent);
		viewer.setContentProvider(ArrayContentProvider.getInstance());

		final GridViewerColumn idColumn = new GridViewerColumn(viewer, SWT.NONE);
		idColumn.getColumn().setText("ID");
		idColumn.getColumn().setWidth(200);
		idColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(final Object element) {
				return ((Sequence) element).getName();
			}
		});
		final GridViewerColumn codonColumn = new GridViewerColumn(viewer, SWT.NONE);
		codonColumn.getColumn().setText("Codon");
		codonColumn.getColumn().setWidth(400);
		codonColumn.getColumn().setWordWrap(true);
		codonColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(final Object element) {
				return ((Sequence) element).getValue();
			}
		});

		final Grid grid = viewer.getGrid();
		grid.setHeaderVisible(true);
		grid.setAutoHeight(true);

		return viewer;
	}

	private void updateViewer() {
		if (tableViewer == null) {
			return;
		}

		if (data == null || transformation == null) {
			tableViewer.setInput(null);
			return;
		}

		final List<Sequence> codons = new ArrayList<Sequence>(seqModel.size());
		for (final Sequence seq : seqModel) {
			final CodonTransformer transformer = new CodonTransformer(data, seq);
			codons.add(transformation.apply(transformer));
		}

		tableViewer.setInput(codons);
	}

	public Control getControl() {
		return control;
	}

	@Focus
	public void setFocus() {
		comboViewer.getCombo().setFocus();
	}

	public void setModel(final List<Sequence> sequences) {
		seqModel = sequences;
		updateViewer();
	}

}