package org.chartsy.main.favorites.nodes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.openide.nodes.Index;
import org.openide.nodes.Node;

/**
 *
 * @author Viorel
 */
public class RootAPIChildren extends Index.ArrayChildren
{
	
	private RootAPI root;

	public RootAPIChildren(RootAPI root)
	{
		this.root = root;
	}

	protected @Override List<Node> initCollection()
	{
		if (nodes == null)
			nodes = new ArrayList<Node>();

		for (FolderAPI folder : root.getFolders())
			nodes.add(new FolderAPINode(folder));

		for (StockAPI stock : root.getStocks())
			nodes.add(new StockAPINode(stock));

		return (List<Node>) nodes;
	}

	public void addNewFolder(FolderAPINode node)
	{
		ArrayList<Node> list = new ArrayList<Node>();
		Iterator<Node> iterator = nodes.iterator();
		while (iterator.hasNext())
			list.add(iterator.next());
		list.add(getLastFolderIndex(), node);
		nodes = list;
		refresh();
	}

	private int getLastFolderIndex()
	{
		int i = -1;
		Iterator<Node> iterator = nodes.iterator();
		while (iterator.hasNext())
		{
			Node node = iterator.next();
			if (node instanceof FolderAPINode)
				i++;
			else if (node instanceof StockAPINode)
				return i+1;
		}
		return -1;
	}

}
