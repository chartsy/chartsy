package org.chartsy.main.data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author viorel.gheba
 */
public class DataItem implements Serializable
{

    private static final long serialVersionUID = 2L;

    private long time;
    private double open;
    private double high;
    private double low;
    private double close;
    private double volume;

    public DataItem(long time, double close)
    {
        this.time = time;
        this.open = 0;
        this.high = 0;
        this.low = 0;
        this.close = close;
        this.volume = 0;
    }

    public DataItem(long time, double open, double high, double low, double close, double volume)
    {
        this.time = time;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }

    public DataItem getEmptyDataItem()
    { return null; }

    public long getTime() 
    { return time; }

    public void setTime(long time) 
    { this.time = time; }

    public Date getDate() 
    { return new Date(time); }

    public double getOpen() 
    { return open; }

    public void setOpen(double open) 
    { this.open = open; }

    public double getHigh() 
    { return high; }

    public void setHigh(double high) 
    { this.high = high; }

    public double getLow() 
    { return low; }

    public void setLow(double low) 
    { this.low = low; }

    public double getClose() 
    { return close; }

    public void setClose(double close) 
    { this.close = close; }

    public double getVolume() 
    { return volume; }

    public void setVolume(double volume) 
    { this.volume = volume; }

    public boolean equals(Object obj)
    {
        if (obj == this)
            return true;

        if (!(obj instanceof DataItem))
            return false;
        
        DataItem that = (DataItem) obj;
        if (getTime() != that.getTime()) 
            return false;

        if (getOpen() != that.getOpen()) 
            return false;

        if (getHigh() != that.getHigh()) 
            return false;

        if (getLow() != that.getLow()) 
            return false;

        if (getClose() != that.getClose()) 
            return false;

        if (getVolume() != that.getVolume()) 
            return false;
        
        return true;
    }

    public int compareTo(Object object)
    {
        if (object instanceof DataItem)
        {
            DataItem item = (DataItem) object;
            return getDate().compareTo(item.getDate());
        }
        else
        {
            throw new ClassCastException("DataItem.compareTo().");
        }
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("DataItem [open=");
        sb.append(Double.toString(open));
        sb.append(", high=");
        sb.append(Double.toString(high));
        sb.append(", low=");
        sb.append(Double.toString(low));
        sb.append(", close=");
        sb.append(Double.toString(close));
        sb.append(", volume=");
        sb.append(Double.toString(volume));
        sb.append(", time=");
        sb.append(Long.toString(time));
        return sb.toString();
    }

}
