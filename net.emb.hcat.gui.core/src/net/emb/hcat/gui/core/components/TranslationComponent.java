package net.emb.hcat.gui.core.components;

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

import net.emb.hcat.cli.codon.CodonTransformationData;
import net.emb.hcat.cli.codon.CodonTransformer;
import net.emb.hcat.cli.io.CodonTableReader;
import net.emb.hcat.cli.sequence.Sequence;
import net.emb.hcat.gui.core.messages.Messages;

/**
 * Component to translate sequences into codons.
 *
 * @author OT Piccolo
 */
public class TranslationComponent {

	private Control control;
	private ComboViewer comboViewer;
	private GridTableViewer tableViewer;

	private CodonTransformationData data;
	private Function<CodonTransformer, Sequence> transformation;

	private List<Sequence> seqModel;
	private final List<Sequence> translationModel = new ArrayList<Sequence>();

	/**
	 * Creates this component.
	 *
	 * @param parent
	 *            The parent composite
	 * @return The top control of this component.
	 */
	public Control createComposite(final Composite parent) {
		final Composite body = new Composite(parent, SWT.NONE);
		body.setLayout(new GridLayout(2, false));

		final Label labelCodon = new Label(body, SWT.NONE);
		labelCodon.setText(Messages.TranslationComponent_CodonLabel);
		labelCodon.setLayoutData(GridDataFactory.defaultsFor(labelCodon).create());

		comboViewer = createCombo(body);
		comboViewer.getCombo().setLayoutData(GridDataFactory.defaultsFor(comboViewer.getCombo()).create());
		comboViewer.setInput(CodonTableReader.readDefaultTable());

		final Label labelOffset = new Label(body, SWT.NONE);
		labelOffset.setText(Messages.TranslationComponent_OffsetLabel);
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
			update();
		});
		return viewer;
	}

	private Control createRadio(final Composite parent) {
		final Composite body = new Composite(parent, SWT.NONE);
		body.setLayout(new RowLayout());

		final Button automaticButton = new Button(body, SWT.RADIO);
		automaticButton.setText(Messages.TranslationComponent_AutomaticButton);
		automaticButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				transformation = t -> t.transformAuto();
				update();
			}
		});

		final Button o1Button = new Button(body, SWT.RADIO);
		o1Button.setText(Messages.TranslationComponent_1Button);
		o1Button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				transformation = t -> t.transform(0);
				update();
			}
		});

		final Button o2Button = new Button(body, SWT.RADIO);
		o2Button.setText(Messages.TranslationComponent_2Button);
		o2Button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				transformation = t -> t.transform(1);
				update();
			}
		});

		final Button o3Button = new Button(body, SWT.RADIO);
		o3Button.setText(Messages.TranslationComponent_3Button);
		o3Button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				transformation = t -> t.transform(2);
				update();
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
		idColumn.getColumn().setText(Messages.TranslationComponent_IdColumn);
		idColumn.getColumn().setWidth(200);
		idColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(final Object element) {
				return ((Sequence) element).getName();
			}
		});
		final GridViewerColumn codonColumn = new GridViewerColumn(viewer, SWT.NONE);
		codonColumn.getColumn().setText(Messages.TranslationComponent_CodonColumn);
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

		viewer.setInput(translationModel);
		return viewer;
	}

	private void update() {
		updateTranslationModel();
		updateViewer();
	}

	private void updateTranslationModel() {
		translationModel.clear();

		if (data == null || transformation == null || seqModel == null) {
			return;
		}

		for (final Sequence seq : seqModel) {
			final CodonTransformer transformer = new CodonTransformer(data, seq);
			translationModel.add(transformation.apply(transformer));
		}
	}

	private void updateViewer() {
		if (tableViewer == null) {
			return;
		}

		tableViewer.refresh();
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
		if (comboViewer != null && !comboViewer.getCombo().isDisposed()) {
			comboViewer.getCombo().setFocus();
		}
	}

	/**
	 * Sets the model this component is working on.
	 *
	 * @param sequences
	 *            The sequences this component is working on.
	 */
	public void setModel(final List<Sequence> sequences) {
		seqModel = sequences;
		update();
	}

	/**
	 * Gets the translated sequences.
	 *
	 * @return The translated sequences. Or and empty list if no translation yet
	 *         happened. This list can not be used to manipulate the viewed
	 *         transaltions.
	 */
	public List<Sequence> getTranslation() {
		return new ArrayList<Sequence>(translationModel);
	}

	/**
	 * Gets the selected transformation data.
	 * 
	 * @return The transformation data. Or null if no data was selected.
	 */
	public CodonTransformationData getData() {
		if (comboViewer != null && !comboViewer.getControl().isDisposed()) {
			return (CodonTransformationData) comboViewer.getStructuredSelection().getFirstElement();
		}
		return null;
	}

}