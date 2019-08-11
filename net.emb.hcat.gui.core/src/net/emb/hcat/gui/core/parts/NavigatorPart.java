package net.emb.hcat.gui.core.parts;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.IParameter;
import org.eclipse.core.commands.Parameterization;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import net.emb.hcat.gui.core.Constants;
import net.emb.hcat.gui.core.EventTopics;

@SuppressWarnings("restriction")
public class NavigatorPart {

	private TreeViewer treeViewer;

	private Set<String> fileEndings;

	@Inject
	private ECommandService commandService;

	@Inject
	private EHandlerService handlerService;

	@PostConstruct
	public void createComposite(final Composite parent, final IEclipseContext context) {
		parent.setLayout(new FillLayout());
		setViewer(createViewer(parent));
		setWorkingDirectory((Path) context.get(Constants.WORKSPACE_CONTEXT));
	}

	private Set<String> getFileEndings() {
		if (fileEndings == null) {
			fileEndings = createFileEndings();
		}
		return fileEndings;
	}

	private Set<String> createFileEndings() {
		final Set<String> fileEndings = new HashSet<String>();
		fileEndings.add(".txt"); //$NON-NLS-1$
		fileEndings.add(".fas"); //$NON-NLS-1$
		fileEndings.add(".phy"); //$NON-NLS-1$
		return fileEndings;
	}

	@PreDestroy
	public void destroy() {
		// Do nothing.
	}

	@Focus
	public void setFocus() {
		getViewer().getTree().setFocus();
	}

	private TreeViewer getViewer() {
		return treeViewer;
	}

	private void setViewer(final TreeViewer viewer) {
		this.treeViewer = viewer;
	}

	private TreeViewer createViewer(final Composite parent) {
		final TreeViewer viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		viewer.setContentProvider(new ITreeContentProvider() {

			@Override
			public boolean hasChildren(final Object element) {
				return false;
			}

			@Override
			public Object getParent(final Object element) {
				return null;
			}

			@Override
			public Object[] getElements(final Object inputElement) {
				Object[] elements = null;
				if (inputElement != null) {
					try {
						elements = Files.list((Path) inputElement).filter(p -> {
							final String s = p.getFileName().toString();
							final int index = s.lastIndexOf('.');
							return index == -1 ? false : getFileEndings().contains(s.substring(index));
						}).collect(Collectors.toList()).toArray();
					} catch (final IOException e) {
						e.printStackTrace();
					}

				}
				return elements;
			}

			@Override
			public Object[] getChildren(final Object parentElement) {
				return null;
			}
		});

		viewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(final Object element) {
				return ((Path) element).getFileName().toString();
			}
		});

		viewer.addDoubleClickListener(e -> {
			for (final Object element : ((ITreeSelection) e.getSelection()).toArray()) {
				openFile((Path) element);
			}
		});
		return viewer;
	}

	private void openFile(final Path path) {
		final ParameterizedCommand pcmd;
		try {
			final Command cmd = commandService.getCommand(Constants.OPEN_FILE_COMMAND_ID);
			final IParameter columnParam = cmd.getParameter(Constants.OPEN_FILE_COMMAND_PARAMETER_ID);
			final Parameterization param = new Parameterization(columnParam, path.toString());
			pcmd = new ParameterizedCommand(cmd, new Parameterization[] { param });
		} catch (final NotDefinedException e) {
			e.printStackTrace();
			return;
		}

		handlerService.executeHandler(pcmd);
	}

	@Inject
	@Optional
	public void setWorkingDirectory(@UIEventTopic(EventTopics.WORKING_DIRECTORY) final Path path) {
		getViewer().setInput(path);
	}

}
