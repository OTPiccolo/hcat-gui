package net.emb.hcat.gui.core.components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.eclipse.swt.events.ExpandEvent;
import org.eclipse.swt.events.ExpandListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

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

	private final Set<Integer> additionalStart = new HashSet<Integer>();
	private final Set<Integer> additionalEnd = new HashSet<Integer>();

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
		body.setLayout(new GridLayout(1, false));

		final ExpandBar expandBar = new ExpandBar(body, SWT.NONE);
		expandBar.setLayoutData(GridDataFactory.defaultsFor(expandBar).create());
		expandBar.setSpacing(1);
		expandBar.addExpandListener(new ExpandListener() {

			@Override
			public void itemExpanded(final ExpandEvent e) {
				layout();
			}

			@Override
			public void itemCollapsed(final ExpandEvent e) {
				layout();
			}

			private void layout() {
				// Need to do it async, as at the time of this call, the item is
				// still in the previous state, so it will be computed at the
				// wrong state.
				body.getDisplay().asyncExec(() -> body.layout());
			}
		});

		final ExpandItem dataItem = translationItem(expandBar);
		dataItem.setExpanded(true);

		final ExpandItem dataItem2 = additionalItem(expandBar);
		dataItem2.setExpanded(false);

		tableViewer = createViewer(body);
		tableViewer.getGrid().setLayoutData(GridDataFactory.defaultsFor(tableViewer.getGrid()).minSize(SWT.DEFAULT, 400).create());

		control = body;
		return control;
	}

	private ExpandItem translationItem(final ExpandBar parent) {
		final Composite body = new Composite(parent, SWT.NONE);
		body.setLayout(new GridLayout(2, false));

		final Label codonLabel = new Label(body, SWT.NONE);
		codonLabel.setText(Messages.TranslationComponent_CodonLabel);
		codonLabel.setLayoutData(GridDataFactory.defaultsFor(codonLabel).create());

		comboViewer = createCombo(body);
		comboViewer.getCombo().setLayoutData(GridDataFactory.defaultsFor(comboViewer.getCombo()).create());
		comboViewer.setInput(CodonTableReader.readDefaultTable());

		final Label offsetLabel = new Label(body, SWT.NONE);
		offsetLabel.setText(Messages.TranslationComponent_OffsetLabel);
		offsetLabel.setLayoutData(GridDataFactory.defaultsFor(offsetLabel).create());

		final Control radio = createRadio(body);
		radio.setLayoutData(GridDataFactory.defaultsFor(radio).create());

		final ExpandItem item = new ExpandItem(parent, SWT.NONE);
		item.setText(Messages.TranslationComponent_TranslationItem);
		item.setControl(body);
		item.setHeight(body.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);

		return item;
	}

	private ExpandItem additionalItem(final ExpandBar parent) {
		final Composite body = new Composite(parent, SWT.NONE);
		body.setLayout(new GridLayout(2, false));

		final Label descLabel = new Label(body, SWT.WRAP);
		descLabel.setText(Messages.TranslationComponent_AdditionalDescription);
		descLabel.setLayoutData(GridDataFactory.defaultsFor(descLabel).span(2, 1).create());

		final Label startLabel = new Label(body, SWT.NONE);
		startLabel.setText(Messages.TranslationComponent_AdditionalStartLabel);
		startLabel.setLayoutData(GridDataFactory.defaultsFor(startLabel).create());

		final Text startText = createAdditionText(body, additionalStart);
		startText.setLayoutData(GridDataFactory.defaultsFor(startText).create());

		final Label endLabel = new Label(body, SWT.NONE);
		endLabel.setText(Messages.TranslationComponent_AdditionalEndLabel);
		endLabel.setLayoutData(GridDataFactory.defaultsFor(endLabel).create());

		final Text endText = createAdditionText(body, additionalEnd);
		endText.setLayoutData(GridDataFactory.defaultsFor(endText).create());

		final ExpandItem item = new ExpandItem(parent, SWT.NONE);
		item.setText(Messages.TranslationComponent_AdditionalItem);
		item.setControl(body);
		item.setHeight(body.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);

		return item;
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

	private Text createAdditionText(final Composite parent, final Set<Integer> additionalSet) {
		final Text text = new Text(parent, SWT.BORDER | SWT.SINGLE);

		final Pattern pattern = Pattern.compile("\\d+"); //$NON-NLS-1$
		text.addModifyListener(e -> {
			additionalSet.clear();
			final Matcher matcher = pattern.matcher(text.getText());
			while (matcher.find()) {
				additionalSet.add(Integer.parseInt(matcher.group()) - 1);
			}
			update();
		});

		return text;
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
			transformer.getAlternativeStart().addAll(additionalStart);
			transformer.getAlternativeEnd().addAll(additionalEnd);
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
	 *         translations.
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