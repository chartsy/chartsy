package org.chartsy.main.history;

import java.io.Serializable;
import org.chartsy.main.data.Stock;
import org.chartsy.main.utils.SerialVersion;

/**
 *
 * @author viorel.gheba
 */
public class HistoryItem implements Serializable
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    private Stock stock;
    private int intervalHash;

    public HistoryItem(Stock stock, int intervalHash)
    {
        this.stock = stock;
        this.intervalHash = intervalHash;
    }

    public Stock getStock()
    { 
		return stock;
	}

    public void setStock(Stock stock)
    { 
		this.stock = stock;
	}

    public int getIntervalHash()
    { 
		return this.intervalHash;
	}

    public void setInterval(int intervalHash)
    { 
		this.intervalHash = intervalHash;
	}

	public @Override boolean equals(Object obj)
	{
		if (obj == null)
			return false;

		if (!(obj instanceof HistoryItem)) 
			return false;

		final HistoryItem other = (HistoryItem) obj;

		if (hashCode() != other.hashCode())
			return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		int hash = 5;
		hash = 79 * hash + (this.stock != null ? this.stock.hashCode() : 0);
		hash = 79 * hash + this.intervalHash;
		return hash;
	}

}
