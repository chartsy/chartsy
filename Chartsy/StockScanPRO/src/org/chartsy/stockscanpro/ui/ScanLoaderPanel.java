package org.chartsy.stockscanpro.ui;

import java.awt.Dimension;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.Iterator;
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
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

/**
 *
 * @author Viorel
 */
public class ScanLoaderPanel extends JPanel 
	implements ExplorerManager.Provider, Lookup.Provider, LookupListener
{

	private ExplorerManager manager;
	private BeanTreeView treeView;
	private JEditorPane editorPane;
	private QueryPanel queryPanel;
	private Lookup lookup;
	private Lookup.Result result;
	private String scanTitle;

	public ScanLoaderPanel(QueryPanel panel)
	{
		super(SpringUtilities.getLayout());
		setOpaque(false);

		queryPanel = panel;
		manager = new ExplorerManager();
		lookup = ExplorerUtils.createLookup(manager, getActionMap());
		manager.setRootContext(LoadScansAction.getRootNode());
		initComponents();
	}

	private void initComponents()
	{
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

		SpringUtilities.makeCompactGrid(this,
			getComponentCount(), 1, // rows, cols
			5, 5, // initialX, initialY
			5, 5);// xPad, yPad
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
		result = lookup.lookupResult(ScanDataObject.class);
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
			StringBuilder builder = new StringBuilder();
			for (Iterator it = collection.iterator(); it.hasNext();)
			{
				ScanDataObject dataObject = (ScanDataObject) it.next();
				builder.append(decode((String) dataObject.getPrimaryFile().getAttribute("content")));
				scanTitle = decode((String) dataObject.getPrimaryFile().getAttribute("title"));
			}
			editorPane.setText(builder.toString());
		}
		else
		{
			scanTitle = "Untitled Scan";
			editorPane.setText("");
		}
	}

	public void setRootNode(Node rootNode)
	{
		getExplorerManager().setRootContext(rootNode);
	}

	public BeanTreeView getTreeView()
	{
		return treeView;
	}

	public void setQueryPanel(QueryPanel panel)
	{
		this.queryPanel = panel;
	}

	public void loadScan()
	{
		queryPanel.setScanTitle(scanTitle);
		queryPanel.setScan(editorPane.getText());
	}

	public String decode(String text)
	{
		try
		{
			return URLDecoder.decode(text, "UTF-8");
		}
		catch (UnsupportedEncodingException ex)
		{
			return text;
		}
	}

}
