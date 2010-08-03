package org.chartsy.stockscanpro.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.tree.TreeSelectionModel;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.chartsy.main.managers.ProxyManager;
import org.chartsy.stockscanpro.actions.SaveScansAction;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.editor.EditorUI;
import org.netbeans.editor.Utilities;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor.Confirmation;
import org.openide.NotifyDescriptor.InputLine;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.loaders.DataFolder;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.TaskListener;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author Viorel
 */
public final class ScanSaver extends TopComponent implements ExplorerManager.Provider, LookupListener
{

	private static ScanSaver instance;
	private static final String PREFERRED_ID = "ScanSaver";

	private ExplorerManager manager;
	private BeanTreeView treeView;
	private JEditorPane editorPane;
	private JTextField scanTitle;

	private Lookup.Result result;
	private DataFolder folder = null;

	public ScanSaver()
	{
		setName(NbBundle.getMessage(ScanSaver.class, "CTL_ScanSaver"));
		setDisplayName(NbBundle.getMessage(ScanSaver.class, "CTL_ScanSaver"));
		setToolTipText(NbBundle.getMessage(ScanSaver.class, "HINT_ScanSaver"));

		putClientProperty("TopComponentAllowDockAnywhere", Boolean.TRUE);
		putClientProperty(TopComponent.PROP_CLOSING_DISABLED, Boolean.TRUE);
		putClientProperty(TopComponent.PROP_UNDOCKING_DISABLED, Boolean.TRUE);
		putClientProperty(TopComponent.PROP_MAXIMIZATION_DISABLED, Boolean.TRUE);

		initComponents();
		manager = new ExplorerManager();		
		associateLookup(ExplorerUtils.createLookup(manager, getActionMap()));
		setRootNode(SaveScansAction.getRootNode());
	}

	private void initComponents()
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JPanel panel;

		treeView = new BeanTreeView();
		treeView.setRootVisible(false);
		treeView.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		treeView.setDragSource(true);
        treeView.setUseSubstringInQuickSearch(true);
		treeView.setPreferredSize(new Dimension(600, 300));
		treeView.setPopupAllowed(false);
		add(treeView);

		panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

		JButton deleteBtn = new JButton("Delete Folder");
		deleteBtn.addActionListener(DeleteFolderAction.delete(this));
		panel.add(deleteBtn);

		JButton createBtn = new JButton("Create new folder");
		createBtn.addActionListener(CreateFolderAction.create(this));
		panel.add(createBtn);
		panel.setPreferredSize(new Dimension(600, 50));
		add(panel);

		panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BorderLayout());

		JLabel scanTitleLbl = new JLabel("Scan title:");
		panel.add(scanTitleLbl, BorderLayout.NORTH);

		scanTitle = new JTextField();
		panel.add(scanTitle, BorderLayout.CENTER);

		panel.setPreferredSize(new Dimension(600, 50));
		add(panel);

		editorPane = new JEditorPane();
		editorPane.setEditable(false);
		editorPane.setContentType("text/x-scan");
		EditorUI editorUI = Utilities.getEditorUI(editorPane);

		JComponent mainComp = null;
		if (editorUI != null)
			mainComp = editorUI.getExtComponent();

		if (mainComp == null)
			mainComp = new javax.swing.JScrollPane(editorPane);

		mainComp.setPreferredSize(new Dimension(600, 150));
		add(mainComp);

		panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

		JButton okBtn = new JButton("Save Scan");
		okBtn.addActionListener(SaveScanAction.save(this));
		panel.add(okBtn);

		JButton cancelBtn = new JButton("Exit");
		cancelBtn.addActionListener(ExitAction.exit(this));
		panel.add(cancelBtn);
		panel.setPreferredSize(new Dimension(600, 50));
		add(panel);
	}

	public static synchronized ScanSaver getDefault()
	{
		if (instance == null)
			instance = new ScanSaver();
		return instance;
	}

	public static synchronized ScanSaver findInstance()
	{
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null)
		{
            Logger.getLogger(ScanSaver.class.getName()).warning(
				"Cannot find " + PREFERRED_ID + " component. It will not be "
				+ "located properly in the window system.");
            return getDefault();
        }
        if (win instanceof ScanSaver)
		{
            return (ScanSaver) win;
        }

        Logger.getLogger(ScanSaver.class.getName()).warning(
			"There seem to be multiple components with the '"
			+ PREFERRED_ID + "' ID. That is a potential source of errors and "
			+ "unexpected behavior.");
        return getDefault();
    }

	protected @Override void componentActivated()
    {
        ExplorerUtils.activateActions(manager, true);
    }

    protected @Override void componentDeactivated()
    {
        ExplorerUtils.activateActions(manager, false);
    }

	@Override
    @SuppressWarnings(value = "unchecked")
    public void componentOpened() {
        @SuppressWarnings(value = "unchecked")
        Lookup.Template tpl = new Lookup.Template(DataFolder.class);
        result = org.openide.util.Utilities.actionsGlobalContext().lookup(tpl);
        result.addLookupListener(this);
    }

	@Override
    public void componentClosed() {
        result.removeLookupListener(this);
        result = null;
    }

	public @Override void open()
	{
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		Mode mode = WindowManager.getDefault().findMode("undockedEditor");
		if (mode != null)
		{
			mode.setBounds(new Rectangle((dim.width - 600) / 2, (dim.height - 600) / 2, 600, 600));
			mode.dockInto(this);
			super.open();
		}
	}

	public ExplorerManager getExplorerManager()
	{
		return manager;
	}

	public void setRootNode(Node rootNode)
	{
		getExplorerManager().setRootContext(rootNode);
	}

	public void setQueryPanel(QueryPanel panel)
	{
		scanTitle.setText(panel.getScanTitle());
		editorPane.setText(panel.getScan());
	}

	public int getPersistenceType()
	{
        return TopComponent.PERSISTENCE_NEVER;
    }

	protected String preferredID()
	{
        return PREFERRED_ID;
    }

	public DataFolder getFolder()
	{
		return folder;
	}

	public String getScanTitle()
	{
		return scanTitle.getText();
	}

	public String getScan()
	{
		return editorPane.getText();
	}

	public void resultChanged(LookupEvent lookupEvent)
	{
		Lookup.Result res = (Lookup.Result) lookupEvent.getSource();
		Collection collection = res.allInstances();

		if (!collection.isEmpty())
		{
			for (Iterator it = collection.iterator(); it.hasNext();)
				folder = (DataFolder) it.next();
		}
	}

	public void refreshRootNode()
	{
		setRootNode(SaveScansAction.getRootNode());
	}

	private static class DeleteFolderAction implements ActionListener
	{

		private static DeleteFolderAction DELETE;
		private ScanSaver scanSaver;
		private final static RequestProcessor RP = new RequestProcessor("interruptible tasks", 1, true);
		private String responce;

		public static ActionListener delete(ScanSaver scanSaver)
		{
			if (DELETE == null)
				DELETE = new DeleteFolderAction(scanSaver);
			return DELETE;
		}

		private DeleteFolderAction(ScanSaver scanSaver)
		{
			this.scanSaver = scanSaver;
		}

		public void actionPerformed(ActionEvent e)
		{
			Confirmation descriptor = new DialogDescriptor.Confirmation("Are you sure you want to remove this folder?", "Delete Folder");
			descriptor.setMessageType(DialogDescriptor.QUESTION_MESSAGE);
			descriptor.setOptions(new Object[]
			{
				DialogDescriptor.OK_OPTION,
				DialogDescriptor.CANCEL_OPTION
			});

			Object ret = DialogDisplayer.getDefault().notify(descriptor);
			if (ret.equals(DialogDescriptor.OK_OPTION))
			{
				final Preferences preferences = NbPreferences.root().node("/org/chartsy/register");
				final DataFolder dataFolder = scanSaver.getFolder();

				final RequestProcessor.Task task = RP.create(new Runnable()
				{
					public void run()
					{
						HttpClient client = ProxyManager.getDefault().getHttpClient();
						PostMethod method = new PostMethod(NbBundle.getMessage(SaveScansAction.class, "StockScanPRO_URL"));

						try
						{
							method.setQueryString(new NameValuePair[]
							{
								new NameValuePair("option", "com_chartsy"),
								new NameValuePair("view", "savescans"),
								new NameValuePair("format", "action"),
								new NameValuePair("username", preferences.get("username", "")),
								new NameValuePair("passwd", preferences.get("password", "")),
								new NameValuePair("action", "delete"),
								new NameValuePair("folderId", (String) dataFolder.getPrimaryFile().getAttribute("folderId"))
							});
							
							client.executeMethod(method);
							responce = method.getResponseBodyAsString();
							method.releaseConnection();
							method.recycle();
						}
						catch (IOException ex)
						{
							Exceptions.printStackTrace(ex);
						}
					}
				});

				final ProgressHandle handle = ProgressHandleFactory.createHandle("Delete Folder", task);
				task.addTaskListener(new TaskListener()
				{
					public void taskFinished(Task task)
					{
						if (responce.equals("DELETED"))
						{
							handle.finish();
							scanSaver.refreshRootNode();
						}
					}
				});

				handle.start();
				task.schedule(0);
			}
		}
		
	}

	private static class CreateFolderAction implements ActionListener
	{

		private static CreateFolderAction CREATE;
		private ScanSaver scanSaver;
		private final static RequestProcessor RP = new RequestProcessor("interruptible tasks", 1, true);
		private String responce;

		public static ActionListener create(ScanSaver scanSaver)
		{
			if (CREATE == null)
				CREATE = new CreateFolderAction(scanSaver);
			return CREATE;
		}

		private CreateFolderAction(ScanSaver scanSaver)
		{
			this.scanSaver = scanSaver;
		}

		public void actionPerformed(ActionEvent e)
		{
			InputLine descriptor = new DialogDescriptor.InputLine("Folder Name:", "Add Folder");
			descriptor.setOptions(new Object[]
			{
				DialogDescriptor.OK_OPTION,
				DialogDescriptor.CANCEL_OPTION
			});

			Object ret = DialogDisplayer.getDefault().notify(descriptor);
			if (ret.equals(DialogDescriptor.OK_OPTION))
			{
				final String folderName = descriptor.getInputText();
				if ((folderName != null) && (!folderName.isEmpty()))
				{
					final Preferences preferences = NbPreferences.root().node("/org/chartsy/register");
					final DataFolder dataFolder = scanSaver.getFolder();

					final RequestProcessor.Task task = RP.create(new Runnable()
					{
						public void run()
						{
							HttpClient client = ProxyManager.getDefault().getHttpClient();
							PostMethod method = new PostMethod(NbBundle.getMessage(SaveScansAction.class, "StockScanPRO_URL"));

							try
							{
								method.setQueryString(new NameValuePair[]
								{
									new NameValuePair("option", "com_chartsy"),
									new NameValuePair("view", "savescans"),
									new NameValuePair("format", "action"),
									new NameValuePair("username", preferences.get("username", "")),
									new NameValuePair("passwd", preferences.get("password", "")),
									new NameValuePair("action", "add"),
									new NameValuePair("folderName", folderName),
									new NameValuePair("parentId", (String) dataFolder.getPrimaryFile().getAttribute("folderId"))
								});
								
								client.executeMethod(method);
								responce = method.getResponseBodyAsString();
								method.releaseConnection();
								method.recycle();
							}
							catch (IOException ex)
							{
								Exceptions.printStackTrace(ex);
							}
						}
					});

					final ProgressHandle handle = ProgressHandleFactory.createHandle("Cheching registration", task);
					task.addTaskListener(new TaskListener()
					{
						public void taskFinished(Task task)
						{
							if (responce.equals("ADDED"))
							{
								handle.finish();
								scanSaver.refreshRootNode();
							}
						}
					});

					handle.start();
					task.schedule(0);
				}
			}
		}

	}

	private static class SaveScanAction implements ActionListener
	{

		private static SaveScanAction SAVE;
		private ScanSaver scanSaver;
		private final static RequestProcessor RP = new RequestProcessor("interruptible tasks", 1, true);
		private String responce;

		public static ActionListener save(ScanSaver scanSaver)
		{
			if (SAVE == null)
				SAVE = new SaveScanAction(scanSaver);
			return SAVE;
		}

		private SaveScanAction(ScanSaver scanSaver)
		{
			this.scanSaver = scanSaver;
		}

		public void actionPerformed(ActionEvent e)
		{
			Confirmation descriptor = new DialogDescriptor.Confirmation("Are you sure you want to save the scan in this folder?", "Save Scan");
			descriptor.setMessageType(DialogDescriptor.QUESTION_MESSAGE);
			descriptor.setOptions(new Object[]
			{
				DialogDescriptor.OK_OPTION,
				DialogDescriptor.CANCEL_OPTION
			});

			Object ret = DialogDisplayer.getDefault().notify(descriptor);
			if (ret.equals(DialogDescriptor.OK_OPTION))
			{
				final Preferences preferences = NbPreferences.root().node("/org/chartsy/register");
				final DataFolder dataFolder = scanSaver.getFolder();

				final RequestProcessor.Task task = RP.create(new Runnable()
				{
					public void run()
					{
						HttpClient client = ProxyManager.getDefault().getHttpClient();
						PostMethod method = new PostMethod(NbBundle.getMessage(SaveScansAction.class, "StockScanPRO_URL"));

						try
						{
							method.setQueryString(new NameValuePair[]
							{
								new NameValuePair("option", "com_chartsy"),
								new NameValuePair("view", "savescans"),
								new NameValuePair("format", "action"),
								new NameValuePair("username", preferences.get("username", "")),
								new NameValuePair("passwd", preferences.get("password", "")),
								new NameValuePair("action", "save"),
								new NameValuePair("folderId", (String) dataFolder.getPrimaryFile().getAttribute("folderId")),
								new NameValuePair("scanTitle", scanSaver.getScanTitle()),
								new NameValuePair("scan", scanSaver.getScan())
							});
							
							client.executeMethod(method);
							responce = method.getResponseBodyAsString();
							method.releaseConnection();
							method.recycle();
						}
						catch (IOException ex)
						{
							Exceptions.printStackTrace(ex);
						}
					}
				});

				final ProgressHandle handle = ProgressHandleFactory.createHandle("Save Scan", task);
				task.addTaskListener(new TaskListener()
				{
					public void taskFinished(Task task)
					{
						if (responce.equals("SAVED"))
							handle.finish();
					}
				});

				handle.start();
				task.schedule(0);
			}
		}

	}

	private static class ExitAction implements ActionListener
	{

		private static ExitAction EXIT;
		private ScanSaver scanSaver;

		public static ActionListener exit(ScanSaver scanSaver)
		{
			if (EXIT == null)
				EXIT = new ExitAction(scanSaver);
			return EXIT;
		}

		private ExitAction(ScanSaver scanSaver)
		{
			this.scanSaver = scanSaver;
		}

		public void actionPerformed(ActionEvent e)
		{
			scanSaver.close();
		}

	}

}
