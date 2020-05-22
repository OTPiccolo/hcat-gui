package net.emb.hcat.gui.core.parts;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import net.emb.hcat.gui.core.components.HaplotypeComponent;

/**
 * Shows information about haplotypes and how they diverge from each other.
 * 
 * @author OT Piccolo
 */
public class HaplotypeTablePart {

	private HaplotypeComponent haplotype;

	/**
	 * Constructor.
	 *
	 * @param parent
	 *            Parent composite.
	 * @param context
	 *            Eclipse context to get information from.
	 */
	@PostConstruct
	public void createComposite(final Composite parent, final IEclipseContext context) {
		parent.setLayout(new FillLayout());
		haplotype = ContextInjectionFactory.make(HaplotypeComponent.class, context);
		haplotype.createComposite(parent);
	}

	/**
	 * Focus method.
	 */
	@Focus
	public void setFocus() {
		if (haplotype != null) {
			haplotype.setFocus();
		}
	}

}