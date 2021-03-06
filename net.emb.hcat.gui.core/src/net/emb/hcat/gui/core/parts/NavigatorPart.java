package net.emb.hcat.gui.core.parts;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import net.emb.hcat.gui.core.Constants;
import net.emb.hcat.gui.core.EventTopics;

/**
 * Shows all relevant files of a folder to open them in the editor.
 *
 * @author OT Piccolo
 */
@SuppressWarnings("restriction")
public class NavigatorPart {

	@Inject
	private ECommandService commandService;

	@Inject
	private EHandlerService handlerService;

	private TreeViewer treeViewer;

	private Set<String> fileEndings;

	private Image folderImage;
	private Image fileImage;

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
		setViewer(createViewer(parent));
		setWorkingDirectory((Path) context.get(Constants.WORKSPACE_CONTEXT_ID));
		try (InputStream is = getClass().getResourceAsStream("/icons/folder.gif")) { //$NON-NLS-1$
			folderImage = new Image(parent.getDisplay(), is);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		try (InputStream is = getClass().getResourceAsStream("/icons/file.gif")) { //$NON-NLS-1$
			fileImage = new Image(parent.getDisplay(), is);
		} catch (final IOException e) {
			e.printStackTrace();
		}
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

	/**
	 * Destructor.
	 */
	@PreDestroy
	public void destroy() {
		if (folderImage != null) {
			folderImage.dispose();
		}
		if (fileImage != null) {
			fileImage.dispose();
		}
	}

	/**
	 * Focus method.
	 */
	@Focus
	public void setFocus() {
		if (getViewer() != null && !getViewer().getTree().isDisposed()) {
			treeViewer.getTree().setFocus();
		}
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

			Predicate<? super Path> validElement = p -> {
				if (Files.isDirectory(p)) {
					return true;
				}
				final String s = p.getFileName().toString();
				final int index = s.lastIndexOf('.');
				return index == -1 ? false : getFileEndings().contains(s.substring(index));
			};
			Comparator<? super Path> pathComparator = (p1, p2) -> {
				if (Files.isDirectory(p1)) {
					if (Files.isDirectory(p2)) {
						return p1.compareTo(p2);
					}
					return -1;
				}
				if (Files.isDirectory(p2)) {
					return 1;
				}
				return p1.compareTo(p2);
			};

			@Override
			public boolean hasChildren(final Object element) {
				final Path path = (Path) element;
				if (!Files.isDirectory(path)) {
					return false;
				}
				// If a directory can't be accessed, don't try to check what is
				// inside.
				if (!Files.isReadable(path)) {
					return false;
				}
				try (Stream<Path> s = Files.list(path)) {
					return s.anyMatch(validElement);
				} catch (final IOException e) {
					e.printStackTrace();
					return false;
				}
			}

			@Override
			public Object getParent(final Object element) {
				return ((Path) element).getParent();
			}

			@Override
			public Object[] getElements(final Object inputElement) {
				return getChildren(inputElement);
			}

			@Override
			public Object[] getChildren(final Object parentElement) {
				Object[] elements = null;
				if (parentElement != null) {
					final Path path = (Path) parentElement;
					try {
						elements = Files.list(path).filter(validElement).sorted(pathComparator).collect(Collectors.toList()).toArray();
					} catch (final IOException e) {
						e.printStackTrace();
					}

				}
				return elements;
			}
		});

		viewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(final Object element) {
				return ((Path) element).getFileName().toString();
			}

			@Override
			public Image getImage(final Object element) {
				final Path p = (Path) element;
				if (Files.isDirectory(p)) {
					return folderImage;
				}
				return fileImage;
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
			final Command cmd = commandService.getCommand(Constants.OPEN_COMMAND_ID);
			final IParameter columnParam = cmd.getParameter(Constants.OPEN_COMMAND_PARAMETER_ID);
			final Parameterization param = new Parameterization(columnParam, path.toString());
			pcmd = new ParameterizedCommand(cmd, new Parameterization[] { param });
		} catch (final NotDefinedException e) {
			e.printStackTrace();
			return;
		}

		handlerService.executeHandler(pcmd);
	}

	/**
	 * Sets the working directory, to display files and folders.
	 *
	 * @param path
	 *            The path of the directory.
	 */
	@Inject
	@Optional
	public void setWorkingDirectory(@UIEventTopic(EventTopics.WORKING_DIRECTORY) final Path path) {
		getViewer().setInput(path);
		// After opening the application, the part isn't yet displayed, which
		// seems to muck with the displaying of folder/file icons. Refreshing
		// fixes that problem.
		getViewer().getTree().getDisplay().timerExec(50, () -> getViewer().refresh());
	}

}
