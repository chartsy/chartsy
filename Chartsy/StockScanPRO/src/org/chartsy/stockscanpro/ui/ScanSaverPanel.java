package org.chartsy.stockscanpro.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Iterator;
import java.util.prefs.Preferences;
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

/**
 *
 * @author Viorel
 */
public class ScanSaverPanel extends JPanel
	implements ExplorerManager.Provider, Lookup.Provider, LookupListener
{

	private ExplorerManager manager;
	private BeanTreeView treeView;
	private JEditorPane editorPane;
	private JTextField scanTitle;
	private QueryPanel queryPanel;
	private Lookup lookup;
	private Lookup.Result result;
	private DataFolder folder = null;
	private DialogDescriptor dialogDescriptor;
	public ActionListener saveScan;

	public ScanSaverPanel(QueryPanel panel)
	{
		super(SpringUtilities.getLayout());
		setOpaque(false);

		queryPanel = panel;
		manager = new ExplorerManager();
		lookup = ExplorerUtils.createLookup(manager, getActionMap());
		manager.setRootContext(SaveScansAction.getRootNode());
		initComponents();
	}

	private void initComponents()
	{
		JPanel panel;

		treeView = new BeanTreeView();
		treeView.setRootVisible(false);
		treeView.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		treeView.setDragSource(true);
        treeView.setUseSubstringInQuickSearch(true);
		treeView.setPreferredSize(new Dimension(600, 300));
		treeView.setPopupAllowed(false);
		add(treeView);

		panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		panel.setOpaque(false);
		panel.setPreferredSize(new Dimension(600, 50));
		add(panel);

		JButton createBtn = new JButton("Create new folder");
		createBtn.addActionListener(new CreateFolderAction(this));
		panel.add(createBtn);

		JButton deleteBtn = new JButton("Delete Folder");
		deleteBtn.addActionListener(new DeleteFolderAction(this));
		panel.add(deleteBtn);

		panel = new JPanel(SpringUtilities.getLayout());
		panel.setOpaque(false);

		JLabel scanTitleLbl = new JLabel("Scan title:");
		panel.add(scanTitleLbl);

		scanTitle = new JTextField(100);
		scanTitle.setText(queryPanel.getScanTitle());
		panel.add(scanTitle);

		panel.setPreferredSize(new Dimension(600, 50));
		SpringUtilities.makeCompactGrid(panel,
			panel.getComponentCount(), 1, // rows, cols
			5, 5, // initialX, initialY
			5, 5);// xPad, yPad
		add(panel);

		editorPane = new JEditorPane();
		editorPane.setEditable(false);
		editorPane.setContentType("text/x-scan");
		editorPane.setText(queryPanel.getScan());
		EditorUI editorUI = Utilities.getEditorUI(editorPane);

		JComponent mainComp = null;
		if (editorUI != null)
			mainComp = editorUI.getExtComponent();

		if (mainComp == null)
			mainComp = new javax.swing.JScrollPane(editorPane);

		mainComp.setPreferredSize(new Dimension(600, 150));
		add(mainComp);

		SpringUtilities.makeCompactGrid(this,
			getComponentCount(), 1, // rows, cols
			5, 5, // initialX, initialY
			5, 5);// xPad, yPad

		saveScan = new SaveScanAction(this);
	}

	public void setDialogDescriptor(DialogDescriptor descriptor)
	{
		dialogDescriptor = descriptor;
	}

	public DialogDescriptor getDialogDescriptor()
	{
		return dialogDescriptor;
	}

	public ExplorerManager getExplorerManager()
	{
		return manager;
	}

	public Lookup getLookup()
	{
		return lookup;
	}

	public @Override void addNotify()
	{
		super.addNotify();
		ExplorerUtils.activateActions(manager, true);
		result = lookup.lookupResult(DataFolder.class);
		result.addLookupListener(this);
	}

	public @Override void removeNotify()
	{
		ExplorerUtils.activateActions(manager, false);
		if (result != null)
		{
			result.removeLookupListener(this);
			result = null;
		}
		super.removeNotify();
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

	public DataFolder getFolder()
	{
		return folder;
	}

	public void refreshRootNode()
	{
		setRootNode(SaveScansAction.getRootNode());
	}

	public void setRootNode(Node rootNode)
	{
		getExplorerManager().setRootContext(rootNode);
	}

	public String getScanTitle()
	{
		return scanTitle.getText();
	}

	public String getScan()
	{
		return editorPane.getText();
	}

	class DeleteFolderAction implements ActionListener
	{

		private ScanSaverPanel scanSaver;
		private String responce;

		public DeleteFolderAction(ScanSaverPanel scanSaver)
		{
			this.scanSaver = scanSaver;
		}

		public void actionPerformed(ActionEvent e)
		{
			Confirmation descriptor = new DialogDescriptor.Confirmation(
				"Are you sure you want to remove this folder?", "Delete Folder");
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

				HttpClient client = ProxyManager.getDefault().httpClient();
				PostMethod method = new PostMethod(
					NbBundle.getMessage(SaveScansAction.class, "StockScanPRO_URL"));

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
				}
				catch (IOException ex)
				{
					Exceptions.printStackTrace(ex);
				}
				finally
				{
					method.releaseConnection();
				}

				if (responce.equals("DELETED"))
				{
					System.out.println("Folder removed");
					scanSaver.refreshRootNode();
				}
				else
					System.out.println("Couldn't remove folder");
			}
		}

	}

	class CreateFolderAction implements ActionListener
	{

		private ScanSaverPanel scanSaver;
		private String responce;

		public CreateFolderAction(ScanSaverPanel scanSaver)
		{
			this.scanSaver = scanSaver;
		}

		public void actionPerformed(ActionEvent e)
		{
			InputLine descriptor = new DialogDescriptor.InputLine(
				"Folder Name:", "Add Folder");
			descriptor.setOptions(new Object[]
			{
				DialogDescriptor.OK_OPTION,
				DialogDescriptor.CANCEL_OPTION
			});

			Object ret = DialogDisplayer.getDefault().notify(descriptor);
			if (ret.equals(DialogDescriptor.OK_OPTION))
			{
				final String folderName = descriptor.getInputText();
				if ((folderName != null)
					&& (folderName.hashCode() != "".hashCode()))
				{
					final Preferences preferences = NbPreferences.root().node("/org/chartsy/register");
					final DataFolder dataFolder = scanSaver.getFolder();

					HttpClient client = ProxyManager.getDefault().httpClient();
					PostMethod method = new PostMethod(
						NbBundle.getMessage(SaveScansAction.class, "StockScanPRO_URL"));

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
					}
					catch (IOException ex)
					{
						Exceptions.printStackTrace(ex);
					}
					finally
					{
						method.releaseConnection();
					}

					if (responce.equals("ADDED"))
					{
						System.out.println("Folder added");
						scanSaver.refreshRootNode();
					}
					else
						System.out.println("Couldn't add folder");
				}
			}
		}

	}

	class SaveScanAction implements ActionListener
	{

		private ScanSaverPanel scanSaver;
		private String responce;

		public SaveScanAction(ScanSaverPanel scanSaver)
		{
			this.scanSaver = scanSaver;
		}

		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() == DialogDescriptor.OK_OPTION)
			{
				Confirmation descriptor = new DialogDescriptor.Confirmation(
					"Are you sure you want to save the scan in this folder?",
					"Save Scan");
				descriptor.setMessageType(DialogDescriptor.QUESTION_MESSAGE);
				descriptor.setOptions(new Object[]
				{
					DialogDescriptor.OK_OPTION,
					DialogDescriptor.CANCEL_OPTION
				});

				Object ret = DialogDisplayer.getDefault().notify(descriptor);
				if (ret.equals(DialogDescriptor.OK_OPTION))
				{
					Preferences preferences = NbPreferences.root().node("/org/chartsy/register");
					DataFolder dataFolder = scanSaver.getFolder();

					String username = preferences.get("username", "");
					String password = preferences.get("password", "");
					String folderId
						= (String) dataFolder.getPrimaryFile().getAttribute("folderId");
					String scanTitle = encode(scanSaver.getScanTitle());
					String scan = encode(scanSaver.getScan());

					HttpClient client = ProxyManager.getDefault().httpClient();
					PostMethod method = new PostMethod(
						NbBundle.getMessage(SaveScansAction.class, "StockScanPRO_URL"));

					try
					{
						method.setQueryString(new NameValuePair[]
						{
							new NameValuePair("option", "com_chartsy"),
							new NameValuePair("view", "savescans"),
							new NameValuePair("format", "action"),
							new NameValuePair("username", username),
							new NameValuePair("passwd", password),
							new NameValuePair("action", "save"),
							new NameValuePair("folderId", folderId),
							new NameValuePair("scanTitle", scanTitle),
							new NameValuePair("scan", scan)
						});

						client.executeMethod(method);
						responce = method.getResponseBodyAsString();
					}
					catch (IOException ex)
					{
						Exceptions.printStackTrace(ex);
					}
					finally
					{
						method.releaseConnection();
					}

					if (responce.equals("SAVED"))
							System.out.println("Scan saved");
						else
							System.out.println("Couldn't save scan");
				}
			}
		}

		public String encode(String text)
		{
			try
			{
				return URLEncoder.encode(text, "UTF-8");
			}
			catch (UnsupportedEncodingException ex)
			{
				return text;
			}
		}

	}

}
