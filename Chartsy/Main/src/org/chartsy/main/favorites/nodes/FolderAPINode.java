package org.chartsy.main.favorites.nodes;

import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.io.IOException;
import java.util.List;
import javax.swing.Action;
import org.chartsy.main.favorites.FavoritesTreeView;
import org.openide.actions.DeleteAction;
import org.openide.actions.PasteAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.NodeTransfer;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;
import org.openide.util.datatransfer.PasteType;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Viorel
 */
public class FolderAPINode extends AbstractNode
{

	public FolderAPINode(FolderAPI folder)
	{
		super(new StockAPIChildren(folder), Lookups.singleton(folder));
		this.setDisplayName(folder.getDisplayName());
		this.setIconBaseWithExtension(
			NbBundle.getMessage(FavoritesTreeView.class, "Folder_ICON"));
	}

	public FolderAPI getFolder()
	{
		return getLookup().lookup(FolderAPI.class);
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
							getFolder().addStock(stock);
						else
							getFolder().addStock(stock, index);
						
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

	public @Override Cookie getCookie(Class clazz)
	{
		Children children = getChildren();
		if (clazz.isInstance(children))
			return (Cookie) children;
		return super.getCookie(clazz);
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
			SystemAction.get(PasteAction.class),
			//SystemAction.get(RenameAction.class),
			Actions.renameFolder(),
			null,
			SystemAction.get(DeleteAction.class)
		};
	}

	public @Override boolean canDestroy()
	{
		return true;
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
		return true;
	}

	public @Override void destroy() throws IOException
	{
		Node parent = getParentNode();
		if (parent instanceof RootAPINode)
		{
			RootAPINode root = (RootAPINode) parent;
			root.getRoot().removeFolder(getFolder());
			root.getChildren().remove(new Node[] { this });
		}

		super.destroy();
	}

}
