package org.chartsy.main.history;

import java.io.Serializable;
import org.chartsy.main.data.Stock;
import org.chartsy.main.intervals.Interval;
import org.chartsy.main.utils.SerialVersion;

/**
 *
 * @author viorel.gheba
 */
public class HistoryItem implements Serializable
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    private Stock stock;
    private Interval interval;

    public HistoryItem(Stock stock, Interval interval)
    {
        this.stock = stock;
        this.interval = interval;
    }

    public Stock getStock()
    { 
		return stock;
	}

    public void setStock(Stock stock)
    { 
		this.stock = stock;
	}

    public Interval getInterval()
    { 
		return this.interval;
	}

    public void setInterval(Interval interval)
    { 
		this.interval = interval;
	}

	@Override
	public boolean equals(Object obj)
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
		int hash = 7;
		hash = 37 * hash + (this.stock != null ? this.stock.hashCode() : 0);
		hash = 37 * hash + (this.interval != null ? this.interval.hashCode() : 0);
		return hash;
	}

}
