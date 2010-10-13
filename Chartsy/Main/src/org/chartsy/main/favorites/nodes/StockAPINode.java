package org.chartsy.main.favorites.nodes;

import java.io.IOException;
import javax.swing.Action;
import org.openide.actions.CopyAction;
import org.openide.actions.CutAction;
import org.openide.actions.DeleteAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Viorel
 */
public class StockAPINode extends AbstractNode
{

	public StockAPINode(StockAPI stock)
	{
		super(Children.LEAF, Lookups.fixed(new Object[] {stock}));
		setDisplayName(stock.getDisplayName());
		stock.initializeDataProvider();
	}

	public StockAPI getStock()
	{
		return getLookup().lookup(StockAPI.class);
	}

	public @Override boolean canDestroy()
	{
		return true;
	}

	public @Override boolean canCopy()
	{
		return true;
	}

	public @Override boolean canCut()
	{
		return true;
	}

	public @Override boolean canRename()
	{
		return false;
	}

	public @Override Action[] getActions(boolean popup)
	{
		return new Action[]
		{
			SystemAction.get(CopyAction.class),
            SystemAction.get(CutAction.class),
            null,
            SystemAction.get(DeleteAction.class),
			null,
			Actions.openStock(this)
		};
	}

	public @Override void destroy() throws IOException
	{
		Node parent = getParentNode();
		if (parent instanceof RootAPINode)
		{
			RootAPINode root = (RootAPINode) parent;
			root.getRoot().removeStock(getStock());
			root.getChildren().remove(new Node[] { this });
		}
		else if(parent instanceof FolderAPINode)
		{
			FolderAPINode folder = (FolderAPINode) parent;
			folder.getFolder().removeStock(getStock());
			folder.getChildren().remove(new Node[] { this });
		}

		super.destroy();
	}

}
