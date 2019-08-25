package net.emb.hcat.gui.core.imports;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import net.emb.hcat.gui.core.messages.Messages;

public class SelectionPage extends WizardPage {
	private Text workspace;
	private Button select;
	private Label lblName;
	private Text workspaceName;
	private Label lblModus;
	private Composite modusComposite;
	private Button singleModus;
	private Button batchModus;

	private String workspaceStr;

	public SelectionPage() {
		super("selectionPage"); //$NON-NLS-1$
		setTitle(Messages.SelectionPage_Title);
		setDescription(Messages.SelectionPage_Description);
	}

	@Override
	public void createControl(final Composite parent) {
		final Composite container = new Composite(parent, SWT.NONE);

		setControl(container);
		container.setLayout(new GridLayout(3, false));

		lblName = new Label(container, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblName.setText(Messages.SelectionPage_lblName_text);

		workspaceName = new Text(container, SWT.BORDER);
		workspaceName.setText("");
		workspaceName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		final Label lblWorkspace = new Label(container, SWT.NONE);
		lblWorkspace.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblWorkspace.setText(Messages.SelectionPage_lblWorkspace_text);

		workspaceStr = System.getProperty("user.dir");
		workspace = new Text(container, SWT.BORDER);
		workspace.setText(workspaceStr);
		workspace.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		workspace.addModifyListener(e -> workspaceStr = workspace.getText());

		select = new Button(container, SWT.NONE);
		select.setText(Messages.SelectionPage_button_text);
		select.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				final DirectoryDialog dialog = new DirectoryDialog(getShell(), SWT.OPEN);
				dialog.setFilterPath(workspace.getText());
				dialog.setText("Choose directory");
				dialog.setText("Choose a directory, where your haplotypes reside.");
				final String directory = dialog.open();
				if (directory != null) {
					workspace.setText(directory);
				}
			}
		});

		lblModus = new Label(container, SWT.NONE);
		lblModus.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblModus.setText(Messages.SelectionPage_lblModus_text);

		modusComposite = new Composite(container, SWT.NONE);
		modusComposite.setLayout(new GridLayout(2, false));
		modusComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));

		singleModus = new Button(modusComposite, SWT.RADIO);
		singleModus.setText(WorkspaceModus.SINGLE_FUNCTION_PROCESSION.getDisplayName());

		batchModus = new Button(modusComposite, SWT.RADIO);
		batchModus.setText(WorkspaceModus.BATCH_FUNCTION_PROCESSION.getDisplayName());
	}

	public Text getWorkspace() {
		return workspace;
	}

	public Button getSelect() {
		return select;
	}

	public Button getBatchModus() {
		return batchModus;
	}

	public Button getSingleModus() {
		return singleModus;
	}

	public Text getWorkspaceName() {
		return workspaceName;
	}

	public String getWorkspaceStr() {
		return workspaceStr;
	}
}
