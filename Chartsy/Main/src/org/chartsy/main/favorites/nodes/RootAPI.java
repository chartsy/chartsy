package org.chartsy.main.favorites.nodes;

import java.util.LinkedList;

/**
 *
 * @author Viorel
 */
public class RootAPI
{

	private LinkedList<StockAPI> stocks;
	private LinkedList<FolderAPI> folders;

	public RootAPI()
	{
		this.stocks = new LinkedList<StockAPI>();
		this.folders = new LinkedList<FolderAPI>();
	}

	public void addStock(StockAPI stock)
	{
		this.stocks.add(stock);
	}

	public void addStock(StockAPI stock, int index)
	{
		this.stocks.add(index, stock);
	}

	public void removeStock(int index)
	{
		this.stocks.remove(index);
	}

	public void removeStock(StockAPI stock)
	{
		this.stocks.remove(stock);
	}

	public LinkedList<StockAPI> getStocks()
	{
		return this.stocks;
	}

	public boolean findStock(StockAPI search)
	{
		for (StockAPI stock : stocks)
			if (stock.hashCode() == search.hashCode())
				return true;
		
		for (FolderAPI folder : folders)
		{
			for (StockAPI stock : folder.getStocks())
				if (stock.hashCode() == search.hashCode())
					return true;
		}

		return false;
	}

	public void addFolder(FolderAPI folder)
	{
		this.folders.add(folder);
	}

	public void addFolder(FolderAPI folder, int index)
	{
		this.folders.add(index, folder);
	}

	public void removeFolder(int index)
	{
		this.folders.remove(index);
	}

	public void removeFolder(FolderAPI folder)
	{
		this.folders.remove(folder);
	}

	public LinkedList<FolderAPI> getFolders()
	{
		return this.folders;
	}

	public boolean folderExists(FolderAPI folder)
	{
		return this.folders.contains(folder);
	}

	public boolean folderNameExists(String folderName)
	{
		for (FolderAPI folder : folders)
			if (folder.getDisplayName().hashCode() == folderName.hashCode())
				return true;
		return false;
	}

	public @Override String toString()
	{
		String newline = System.getProperty("line.separator");

		StringBuilder builder = new StringBuilder();

		builder.append("--- Stocks ---").append(newline);
		for (StockAPI stock : stocks)
			builder.append(stock.toString()).append(newline);

		builder.append("--- Folders ---").append(newline);
		for (FolderAPI folder : folders)
			builder.append(folder.toString()).append(newline);

		return builder.toString();
	}

	public @Override boolean equals(Object obj)
	{
		if (obj == this)
			return true;

		if (!(obj instanceof RootAPI))
			return false;

		RootAPI that = (RootAPI) obj;

		if (that.hashCode() != hashCode())
			return false;

		return true;
	}

	public @Override int hashCode()
	{
		int hash = 5;
		hash = 37 * hash + (this.stocks != null ? this.stocks.hashCode() : 0);
		hash = 37 * hash + (this.folders != null ? this.folders.hashCode() : 0);
		return hash;
	}

}
