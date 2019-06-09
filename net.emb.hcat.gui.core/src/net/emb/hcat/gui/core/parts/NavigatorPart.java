package net.emb.hcat.gui.core.parts;

import java.io.File;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.internal.navigator.NavigatorContentService;
import org.eclipse.ui.navigator.CommonViewerSorter;

public class NavigatorPart {

	private static final String ID = "net.emb.hcat.gui.core.part.navigator";

	private StructuredViewer viewer;

	public NavigatorPart() {
		super();
		System.out.println("Boo!");
	}

	@PostConstruct
	public void createComposite(final Composite parent) {
		parent.setLayout(new FillLayout());
		System.out.println("Nabaadfda");
		setViewer(createViewer(parent));

		final NavigatorContentService service = new NavigatorContentService(ID, getViewer());
		getViewer().setContentProvider(service.createCommonContentProvider());
		// getViewer().setContentProvider(ArrayContentProvider.getInstance());
		getViewer().setLabelProvider(service.createCommonLabelProvider());

		// final INavigatorFilterService filterService =
		// getViewer().getNavigatorContentService().getFilterService();
		// final ViewerFilter[] visibleFilters =
		// filterService.getVisibleFilters(true);
		// for (final ViewerFilter visibleFilter : visibleFilters) {
		// getViewer().addFilter(visibleFilter);
		// }

		getViewer().setSorter(new CommonViewerSorter());

		System.out.println(getViewer().getContentProvider());
		getViewer().setInput(new File("D:\\Downloads").listFiles());
		System.out.println(getViewer().getInput());
		parent.getDisplay().timerExec(500, () -> System.out.println(getViewer().getControl().getBounds()));
	}

	@PreDestroy
	public void destroy() {
		// getViewer().dispose();
	}

	@Focus
	public void setFocus() {
		// getViewer().getTree().setFocus();
	}

	private StructuredViewer getViewer() {
		return viewer;
	}

	private void setViewer(final StructuredViewer viewer) {
		this.viewer = viewer;
	}

	private StructuredViewer createViewer(final Composite parent) {
		// return new CommonViewer(getClass().getCanonicalName(), parent,
		// SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		return new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
	}

}
