package org.chartsy.stockscanpro.ui;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.tree.TreeSelectionModel;
import org.chartsy.stockscanpro.actions.LoadScansAction;
import org.chartsy.stockscanpro.filetype.ScanDataObject;
import org.netbeans.editor.EditorUI;
import org.netbeans.editor.Utilities;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.explorer.view.TreeView;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author Viorel
 */
public final class ScanLoader extends TopComponent implements ExplorerManager.Provider, LookupListener
{

	private static ScanLoader instance;
	private static final String PREFERRED_ID = "ScanLoader";

	private ExplorerManager manager;
	private BeanTreeView treeView;
	private JEditorPane editorPane;
	private QueryPanel queryPanel;

	private Lookup.Result result;

	private String scanTitle;

	public ScanLoader()
	{
		setName(NbBundle.getMessage(ScanLoader.class, "CTL_ScanLoader"));
		setDisplayName(NbBundle.getMessage(ScanLoader.class, "CTL_ScanLoader"));
		setToolTipText(NbBundle.getMessage(ScanLoader.class, "HINT_ScanLoader"));

		putClientProperty("TopComponentAllowDockAnywhere", Boolean.TRUE);
		putClientProperty(TopComponent.PROP_CLOSING_DISABLED, Boolean.TRUE);
		putClientProperty(TopComponent.PROP_UNDOCKING_DISABLED, Boolean.TRUE);
		putClientProperty(TopComponent.PROP_MAXIMIZATION_DISABLED, Boolean.TRUE);
		
		initComponents();
		manager = new ExplorerManager();
		associateLookup(ExplorerUtils.createLookup(manager, getActionMap()));
		setRootNode(LoadScansAction.getRootNode());
	}

	private void initComponents()
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		treeView = new BeanTreeView();
		treeView.setRootVisible(false);
		treeView.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		treeView.setDragSource(true);
        treeView.setUseSubstringInQuickSearch(true);
		treeView.setPreferredSize(new Dimension(600, 300));
		treeView.setPopupAllowed(false);
		add(treeView);

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

		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

		JButton okBtn = new JButton("Load Scan");
		okBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (!editorPane.getText().equals(""))
				{
					if (queryPanel != null)
					{
						queryPanel.setScanTitle(scanTitle);
						queryPanel.setScan(editorPane.getText());
					}
					close();
				}
			}
		});
		panel.add(okBtn);

		JButton cancelBtn = new JButton("Exit");
		cancelBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				close();
			}
		});
		panel.add(cancelBtn);
		panel.setPreferredSize(new Dimension(600, 50));
		add(panel);
	}

	public static synchronized ScanLoader getDefault()
	{
        if (instance == null)
            instance = new ScanLoader();

        return instance;
    }

	public static synchronized ScanLoader findInstance()
	{
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null)
		{
            Logger.getLogger(ScanLoader.class.getName()).warning(
				"Cannot find " + PREFERRED_ID + " component. It will not be "
				+ "located properly in the window system.");
            return getDefault();
        }
        if (win instanceof ScanLoader)
		{
            return (ScanLoader) win;
        }

        Logger.getLogger(ScanLoader.class.getName()).warning(
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
        Lookup.Template tpl = new Lookup.Template(ScanDataObject.class);
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
			mode.setBounds(new Rectangle((dim.width - 600) / 2, (dim.height - 500) / 2, 600, 500));
			mode.dockInto(this);
			super.open();
		}
	}

	public ExplorerManager getExplorerManager()
	{
		return manager;
	}

	public TreeView getTreeView()
	{
		return treeView;
	}

	public void setRootNode(Node rootNode)
	{
		getExplorerManager().setRootContext(rootNode);
	}

	public void setQueryPanel(QueryPanel panel)
	{
		this.queryPanel = panel;
	}

	public int getPersistenceType()
	{
        return TopComponent.PERSISTENCE_NEVER;
    }

	protected String preferredID()
	{
        return PREFERRED_ID;
    }

	public void resultChanged(LookupEvent lookupEvent)
	{
		Lookup.Result res = (Lookup.Result) lookupEvent.getSource();
		Collection collection = res.allInstances();

		if (!collection.isEmpty())
		{
			StringBuilder builder = new StringBuilder();
			for (Iterator it = collection.iterator(); it.hasNext();)
			{
				ScanDataObject dataObject = (ScanDataObject) it.next();
				builder.append(dataObject.getPrimaryFile().getAttribute("content"));
				scanTitle = (String) dataObject.getPrimaryFile().getAttribute("title");
			}
			editorPane.setText(builder.toString());
		}
		else
		{
			scanTitle = "Untitled Scan";
			editorPane.setText("");
		}
	}

}
