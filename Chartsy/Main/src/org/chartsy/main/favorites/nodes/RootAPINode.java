package org.chartsy.main.favorites.nodes;

import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.io.IOException;
import java.util.List;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.NodeTransfer;
import org.openide.util.datatransfer.PasteType;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Viorel
 */
public class RootAPINode extends AbstractNode
{

	public RootAPINode(RootAPI root)
	{
		super(new RootAPIChildren(root), Lookups.singleton(root));
		this.setDisplayName("Favorites");
	}

	public RootAPI getRoot()
	{
		return getLookup().lookup(RootAPI.class);
	}

	public @Override Cookie getCookie(Class clazz)
	{
		Children children = getChildren();
		if (clazz.isInstance(children))
			return (Cookie) children;
		return super.getCookie(clazz);
	}

	public @Override boolean canDestroy()
	{
		return false;
	}

	public @Override boolean canCopy()
	{
		return false;
	}

	public @Override boolean canCut()
	{
		return false;
	}

	public @Override boolean canRename()
	{
		return false;
	}

	public @Override PasteType getDropType(Transferable transferable, final int action, final int index)
	{
		final Node dropNode = NodeTransfer.node(transferable,
			DnDConstants.ACTION_COPY_OR_MOVE + NodeTransfer.CLIPBOARD_CUT);

		if (dropNode != null)
		{
			final StockAPI stock = dropNode.getLookup().lookup(StockAPI.class);
			if (stock != null
				&& !this.equals(dropNode.getParentNode()))
			{
				return new PasteType()
				{
					public Transferable paste() throws IOException
					{
						if (index == -1)
							getRoot().addStock(stock);
						else
							getRoot().addStock(stock, index);

						getChildren().add(new Node[] { new StockAPINode(stock) });
						if ((action & DnDConstants.ACTION_MOVE) != 0)
						{
							dropNode.getParentNode().getChildren().remove(new Node[]{ dropNode });
						}
						return null;
					}
				};
			}
		}
		return null;
	}

	protected @Override void createPasteTypes(Transferable transferable, List list)
	{
		super.createPasteTypes(transferable, list);
		PasteType paste = getDropType(transferable, DnDConstants.ACTION_MOVE, -1);
		if (paste != null)
			list.add(paste);
	}

	public @Override Action[] getActions(boolean context)
	{
		return new Action[]
		{
			Actions.addFolder(this)
		};
	}

}
