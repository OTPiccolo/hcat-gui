package net.emb.hcat.gui.core.components;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import net.emb.hcat.cli.haplotype.DistanceMatrix;
import net.emb.hcat.cli.haplotype.Haplotype;
import net.emb.hcat.cli.sequence.Sequence;
import net.emb.hcat.gui.core.EventTopics;
import net.emb.hcat.gui.core.messages.Messages;

/**
 * Component to give textual representation of processed sequences.
 *
 * @author OT Piccolo
 */
public class TextLogComponent {

	private Control control;
	private Text text;

	private List<Haplotype> haploModel;

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

		text = createText(body);
		text.setLayoutData(GridDataFactory.defaultsFor(text).create());

		control = body;
		return body;
	}

	private Text createText(final Composite parent) {
		final Text text = new Text(parent, SWT.READ_ONLY | SWT.V_SCROLL | SWT.MULTI | SWT.WRAP);
		return text;
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
		if (text != null && !text.isDisposed()) {
			text.setFocus();
		}
	}

	/**
	 * Sets the model this component is working on.
	 *
	 * @param haplotypes
	 *            The haplotypes this component is working on.
	 */
	public void setModel(final List<Haplotype> haplotypes) {
		haploModel = haplotypes;
		if (text != null && !text.isDisposed()) {
			updateViewer();
		}
	}

	private void updateViewer() {
		text.setText(writeLog(haploModel));
	}

	private String writeLog(final List<Haplotype> haplotypes) {
		final StringBuilder builder = new StringBuilder(1000);

		writeSequences(haplotypes, builder);

		writeHaplotypes(haplotypes, builder);

		writeDistanceMatrix(haplotypes, builder);

		return builder.toString();
	}

	private void writeSequences(final List<Haplotype> haplotypes, final StringBuilder builder) {
		builder.append(Messages.TextLogComponent_Seq_Title);
		builder.append("\n"); //$NON-NLS-1$
		builder.append("--------------------"); //$NON-NLS-1$
		builder.append("\n"); //$NON-NLS-1$
		builder.append("\n"); //$NON-NLS-1$
		builder.append(Messages.TextLogComponent_Seq_NrOfSeq);
		int i = 0;
		for (final Haplotype haplotype : haplotypes) {
			i += haplotype.size();
		}
		builder.append(i);
		builder.append("\n"); //$NON-NLS-1$
		builder.append("\n"); //$NON-NLS-1$
		builder.append("\n"); //$NON-NLS-1$
	}

	private void writeHaplotypes(final List<Haplotype> haplotypes, final StringBuilder builder) {
		builder.append(Messages.TextLogComponent_Hap_Title);
		builder.append("\n"); //$NON-NLS-1$
		builder.append("--------------------"); //$NON-NLS-1$
		builder.append("\n"); //$NON-NLS-1$
		builder.append("\n"); //$NON-NLS-1$
		builder.append(Messages.TextLogComponent_Hap_NrOfHap);
		builder.append(haplotypes.size());
		builder.append("\n"); //$NON-NLS-1$
		builder.append("\n"); //$NON-NLS-1$
		for (final Haplotype haplotype : haplotypes) {
			builder.append(Messages.TextLogComponent_Hap_Id);
			builder.append(haplotype.getName());
			builder.append("\n"); //$NON-NLS-1$
			builder.append(Messages.TextLogComponent_Hap_ContainedSeq);
			for (final Sequence sequence : haplotype) {
				builder.append(sequence.getName());
				builder.append(" / "); //$NON-NLS-1$
			}
			builder.setLength(builder.length() - 3);
			builder.append("\n"); //$NON-NLS-1$
			builder.append("\n"); //$NON-NLS-1$
			builder.append("\n"); //$NON-NLS-1$
		}
	}

	private void writeDistanceMatrix(final List<Haplotype> haplotypes, final StringBuilder builder) {
		final DistanceMatrix matrix = new DistanceMatrix(haplotypes);
		builder.append(Messages.TextLogComponent_DistMatrix_Title);
		builder.append("\n"); //$NON-NLS-1$
		builder.append("--------------------"); //$NON-NLS-1$
		builder.append("\n"); //$NON-NLS-1$
		builder.append("\n"); //$NON-NLS-1$
		builder.append(Messages.TextLogComponent_DistMatrix_Min);
		builder.append(matrix.getMinDistance());
		builder.append("\n"); //$NON-NLS-1$
		builder.append(Messages.TextLogComponent_DistMatrix_Max);
		builder.append(matrix.getMaxDistance());
		builder.append("\n"); //$NON-NLS-1$
		builder.append("\n"); //$NON-NLS-1$
		builder.append("\n"); //$NON-NLS-1$
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
		if (haplotype != null && text != null && !text.isDisposed()) {
			final int index = text.getTopIndex();
			updateViewer();
			text.setTopIndex(index);
		}
	}

}
