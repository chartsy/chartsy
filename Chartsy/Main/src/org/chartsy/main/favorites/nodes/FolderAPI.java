package org.chartsy.main.favorites.nodes;

import java.util.LinkedList;

/**
 *
 * @author Viorel
 */
public class FolderAPI extends Object
{

	private String displayName;
	private LinkedList<StockAPI> stocks;

	public FolderAPI()
	{
		this.stocks = new LinkedList<StockAPI>();
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	public String getDisplayName()
	{
		return this.displayName;
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

	public @Override String toString()
	{
		String newline = System.getProperty("line.separator");

		StringBuilder builder = new StringBuilder();

		builder.append("---").append(getDisplayName()).append(" ---").append(newline);
		for (StockAPI stock : stocks)
			builder.append(stock.toString()).append(newline);

		return builder.toString();
	}

	public @Override boolean equals(Object obj)
	{
		if (obj == this)
			return true;

		if (!(obj instanceof FolderAPI))
			return false;

		FolderAPI that = (FolderAPI) obj;

		if (that.hashCode() != hashCode())
			return false;

		return true;
	}

	public @Override int hashCode()
	{
		int hash = 3;
		hash = 97 * hash + (this.displayName != null ? this.displayName.hashCode() : 0);
		hash = 97 * hash + (this.stocks != null ? this.stocks.hashCode() : 0);
		return hash;
	}

}
