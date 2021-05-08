package net.emb.hcat.gui.core.wizards;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

import net.emb.hcat.cli.codon.CodonTransformationData;
import net.emb.hcat.cli.sequence.Sequence;
import net.emb.hcat.gui.core.components.TranslationComponent;
import net.emb.hcat.gui.core.messages.Messages;

/**
 * A wizard to do a translation on sequences.
 *
 * @author OT Piccolo
 */
@Creatable
public class SequenceTranslationWizard extends Wizard {

	private static final class SequenceTranslationPage extends WizardPage {

		private TranslationComponent component;
		private List<Sequence> sequences;

		private SequenceTranslationPage() {
			super("sequenceTransaltion"); //$NON-NLS-1$
		}

		@Override
		public void createControl(final Composite parent) {
			component = new TranslationComponent();
			if (sequences != null) {
				component.setModel(sequences);
			}
			setControl(component.createComposite(parent));
		}

		public TranslationComponent getComponent() {
			return component;
		}

		public void setSequences(final List<Sequence> sequences) {
			this.sequences = sequences;
			if (component != null) {
				component.setModel(sequences);
			}
		}

	}

	private final SequenceTranslationPage page;

	private final List<Sequence> sequences = new ArrayList<Sequence>();

	private CodonTransformationData data;

	/**
	 * Constructor.
	 */
	public SequenceTranslationWizard() {
		page = new SequenceTranslationPage();
		page.setTitle(Messages.SequenceTranslationWizard_pageTitle);
		page.setDescription(Messages.SequenceTranslationWizard_pageDescription);
		addPage(page);
		setWindowTitle(Messages.SequenceTranslationWizard_wizardTitle);
	}

	@Override
	public boolean performFinish() {
		final TranslationComponent component = page.getComponent();
		if (component.getData() == null) {
			page.setErrorMessage(Messages.SequenceTranslationWizard_error_noTranslationTable);
			return false;
		}

		sequences.clear();
		sequences.addAll(component.getTranslation());

		data = component.getData();

		return true;
	}

	/**
	 * After finishing the wizard, this contains all sequences translated at the
	 * desired point.
	 *
	 * @return A list of all translated sequences.
	 */
	public List<Sequence> getSequences() {
		return new ArrayList<Sequence>(sequences);
	}

	/**
	 * Sets the sequences to be translated.
	 *
	 * @param sequences
	 *            The sequences to be translated.
	 */
	public void setSequences(final Collection<Sequence> sequences) {
		this.sequences.clear();
		if (!sequences.isEmpty()) {
			this.sequences.addAll(sequences);
			page.setSequences(getSequences());
		}
	}

	/**
	 * Gets the used transformation data.
	 *
	 * @return The used transformation data. Or <code>null</code>, if no
	 *         transformation data was selected.
	 */
	public CodonTransformationData getTransformationData() {
		return data;
	}

}
