package org.chartsy.main.dataset;

import java.io.Serializable;
import java.util.Date;
import org.chartsy.main.managers.LoggerManager;

/**
 *
 * @author viorel.gheba
 */
public class DataItem implements Serializable {

    private static final long serialVersionUID = 101L;

    private Date date;
    private long time;
    private Number open;
    private Number close;
    private Number high;
    private Number low;
    private Number volume;
    private Number adjclose;

    public DataItem(Date date, double open, double close, double high, double low, double volume, double adjclose) {
        if (date == null)
            throw new IllegalArgumentException("Null 'date' argument.");
        this.date = date;
        this.time = date.getTime();
        this.open = open;
        this.close = close;
        this.high = high;
        this.low = low;
        this.volume = volume;
        this.adjclose = adjclose;
    }

    public Date getDate() { return this.date; }

    public long getTime() { return time; }

    public Number getOpen() { return this.open; }

    public Number getClose() { return this.close; }

    public Number getHigh() { return this.high; }

    public Number getLow() { return this.low; }

    public Number getVolume() { return this.volume; }

    public Number getAdjClose() { return this.adjclose; }

    public double getOpenValue() {
        double result = Double.NaN;
        Number value = this.open;
        if (value != null) result = value.doubleValue();
        return result;
    }

    public double getCloseValue() {
        double result = Double.NaN;
        Number value = this.close;
        if (value != null) result = value.doubleValue();
        return result;
    }

    public double getHighValue() {
        double result = Double.NaN;
        Number value = this.high;
        if (value != null) result = value.doubleValue();
        return result;
    }

    public double getLowValue() {
        double result = Double.NaN;
        Number value = this.low;
        if (value != null) result = value.doubleValue();
        return result;
    }

    public double getVolumeValue() {
        double result = Double.NaN;
        Number value = this.volume;
        if (value != null) result = value.doubleValue();
        return result;
    }

    public double getAdjCloseValue() {
        double result = Double.NaN;
        Number value = this.adjclose;
        if (value != null) result = value.doubleValue();
        return result;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DataItem)) {
            return false;
        }
        DataItem that = (DataItem) obj;
        if (!getDate().equals(that.getDate())) {
            return false;
        }
        if (!getHigh().equals(that.getHigh())) {
            return false;
        }
        if (!getLow().equals(that.getLow())) {
            return false;
        }
        if (!getOpen().equals(that.getOpen())) {
            return false;
        }
        if (!getClose().equals(that.getClose())) {
            return false;
        }
        if (!getVolume().equals(that.getVolume())) {
            return false;
        }
        if (!getAdjClose().equals(that.getAdjClose())) {
            return false;
        }
        return true;
    }

    public int compareTo(Object object) {
        if (object instanceof DataItem) {
            DataItem item = (DataItem) object;
            return getDate().compareTo(item.getDate());
        }
        else {
            throw new ClassCastException("DataItem.compareTo().");
        }
    }

    public void update(double open, double close, double high, double low, double volume, double adjclose) {
        this.open = open;
        this.close = close;
        this.high = high;
        this.low = low;
        this.volume = volume;
        this.adjclose = adjclose;
    }

    public void print() { LoggerManager.getDefault().log("Date: " + date + " Open: " + open + " Close: " + close + " High: " + high + " Low: " + low + " Volume: " + volume); }

}
