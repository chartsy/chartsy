package org.chartsy.main.favorites;

import java.awt.Rectangle;
import java.beans.PropertyVetoException;
import javax.swing.BorderFactory;
import javax.swing.tree.TreePath;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.NodeTreeModel;
import org.openide.explorer.view.TreeView;
import org.openide.nodes.Node;

/**
 *
 * @author Viorel
 */
public class FavoritesTreeView extends TreeView
{

	private static FavoritesTreeView instance;

	public static FavoritesTreeView getDefault()
	{
		if (instance == null)
			instance = new FavoritesTreeView();
		return instance;
	}

	private FavoritesTreeView()
	{
		setBorder(BorderFactory.createEmptyBorder());
		setRootVisible(false);
		tree.setCellRenderer(new FavoritesNodeRenderer());
	}

	protected NodeTreeModel createModel()
	{
		return new NodeTreeModel();
	}

	protected void selectionChanged(Node[] nodes, ExplorerManager em) 
		throws PropertyVetoException
	{
		if (nodes.length > 0)
		{
			Node context = nodes[0].getParentNode();
			for (int i = 1; i < nodes.length; i++)
			{
				if (context != nodes[i].getParentNode())
				{
					em.setSelectedNodes(nodes);
					return;
				}
			}

			if (em.getRootContext().getParentNode() == context)
				em.setExploredContextAndSelection(null, nodes);
			else
				em.setExploredContextAndSelection(context, nodes);
		}
		else
			em.setSelectedNodes(nodes);
	}

	protected boolean selectionAccept(Node[] nodes)
	{
		return true;
	}

	protected void showPath(TreePath path)
	{
		tree.expandPath(path);
		showPathWithoutExpansion(path);
	}

	private void showPathWithoutExpansion(TreePath path)
	{
		Rectangle rect = tree.getPathBounds(path);
		if (rect != null)
			tree.scrollRectToVisible(rect);
	}

	protected void showSelection(TreePath[] treePaths)
	{
		tree.getSelectionModel().setSelectionPaths(treePaths);
		if (treePaths.length == 1)
			showPathWithoutExpansion(treePaths[0]);
	}

	public @Override void setEnabled(boolean enabled)
	{
		this.tree.setEnabled(enabled);
	}

	public @Override boolean isEnabled()
	{
		if (this.tree == null)
			return true;
		return this.tree.isEnabled();
	}

}
