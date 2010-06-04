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
    { return stock; }

    public void setStock(Stock stock) 
    { this.stock = stock; }

    public Interval getInterval()
    { return this.interval; }

    public void setInterval(Interval interval)
    { this.interval = interval; }

    public String toString()
    { return stock.getKey() + " : " + interval.getName(); }

}
