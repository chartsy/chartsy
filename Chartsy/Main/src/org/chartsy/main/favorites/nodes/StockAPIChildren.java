package org.chartsy.main.favorites.nodes;

import java.util.ArrayList;
import java.util.List;
import org.openide.nodes.Index;
import org.openide.nodes.Node;

/**
 *
 * @author Viorel
 */
public class StockAPIChildren extends Index.ArrayChildren
{

	private FolderAPI folder;

	public StockAPIChildren(FolderAPI folder)
	{
		this.folder = folder;
	}

	protected @Override List<Node> initCollection()
	{
		if (nodes == null)
			nodes = new ArrayList<Node>();

		for (StockAPI stock : folder.getStocks())
			nodes.add(new StockAPINode(stock));

		return (List<Node>) nodes;
	}

}
