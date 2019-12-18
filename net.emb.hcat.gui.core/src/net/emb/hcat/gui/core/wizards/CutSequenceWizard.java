package net.emb.hcat.gui.core.wizards;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

import net.emb.hcat.cli.sequence.Sequence;
import net.emb.hcat.gui.core.layout.GridDataBuilder;
import net.emb.hcat.gui.core.layout.GridLayoutBuilder;
import net.emb.hcat.gui.core.messages.Messages;

/**
 * A wizard to cut sequences.
 *
 * @author OT Piccolo
 */
@Creatable
public class CutSequenceWizard extends Wizard {

	private static final class CutSequencePage extends WizardPage {

		private Spinner fromSpinner;
		private Spinner toSpinner;

		private int max;

		private CutSequencePage() {
			super("cutSequence"); //$NON-NLS-1$
		}

		@Override
		public void createControl(final Composite parent) {
			final Composite body = new Composite(parent, SWT.NONE);
			body.setLayout(GridLayoutBuilder.simple(4));
			setControl(body);

			final GridDataBuilder labelBuilder = new GridDataBuilder();
			final Label fromLabel = new Label(body, SWT.NONE);
			fromLabel.setLayoutData(labelBuilder.create());
			fromLabel.setText(Messages.CutSequenceWizard_fromSpinner);

			final GridDataBuilder spinnerBuilder = new GridDataBuilder();
			fromSpinner = new Spinner(body, SWT.NONE);
			fromSpinner.setLayoutData(spinnerBuilder.create());
			fromSpinner.setMinimum(1);
			fromSpinner.setPageIncrement(20);

			final Label toLabel = new Label(body, SWT.NONE);
			toLabel.setLayoutData(labelBuilder.create());
			toLabel.setText(Messages.CutSequenceWizard_toSpinner);

			toSpinner = new Spinner(body, SWT.NONE);
			toSpinner.setLayoutData(spinnerBuilder.create());
			toSpinner.setMinimum(1);
			toSpinner.setPageIncrement(20);

			setMaxSpinner(max);
		}

		public void setMax(final int max) {
			this.max = max;
			setMaxSpinner(max);
		}

		private void setMaxSpinner(final int max) {
			if (max < 1 || fromSpinner == null || fromSpinner.isDisposed()) {
				return;
			}

			fromSpinner.setMaximum(max);
			toSpinner.setMaximum(max);
			fromSpinner.setSelection(1);
			toSpinner.setSelection(max);
		}

		public int getFrom() {
			if (getControl().isDisposed()) {
				return -1;
			}
			return fromSpinner.getSelection();
		}

		public int getTo() {
			if (getControl().isDisposed()) {
				return -1;
			}
			return toSpinner.getSelection();
		}

	}

	private final CutSequencePage page;

	private final List<Sequence> sequences = new ArrayList<Sequence>();

	private int from;
	private int to;

	/**
	 * Constructor.
	 */
	public CutSequenceWizard() {
		page = new CutSequencePage();
		page.setTitle(Messages.CutSequenceWizard_pageTitle);
		page.setDescription(Messages.CutSequenceWizard_pageDescription);
		addPage(page);
		setWindowTitle(Messages.CutSequenceWizard_wizardTitle);
	}

	@Override
	public boolean performFinish() {
		from = page.getFrom() - 1;
		to = page.getTo();

		final List<Sequence> cut = new ArrayList<Sequence>(sequences.size());
		for (final Sequence seq : sequences) {
			final String value = seq.getValue();
			final Sequence cutSeq = new Sequence(value.substring(from, to), seq.getName());
			cut.add(cutSeq);
		}

		sequences.clear();
		sequences.addAll(cut);
		return true;
	}

	/**
	 * After finishing the wizard, this contains all sequences cut at the
	 * desired point.
	 *
	 * @return A list of all cut sequences.
	 */
	public List<Sequence> getSequences() {
		return new ArrayList<Sequence>(sequences);
	}

	/**
	 * Sets the sequences to be cut.
	 *
	 * @param sequences
	 *            The sequnces to be cut.
	 */
	public void setSequences(final Collection<Sequence> sequences) {
		this.sequences.clear();
		if (!sequences.isEmpty()) {
			this.sequences.addAll(sequences);
			page.setMax(this.sequences.get(0).getLength());
		}
	}

	/**
	 * Gets the selected from position to cut.
	 * 
	 * @return The selected from position to cut.
	 */
	public int getFrom() {
		return from;
	}

	/**
	 * Gets the selected to position to cut.
	 * 
	 * @return The selected to position to cut.
	 */
	public int getTo() {
		return to;
	}

}
