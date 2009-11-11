package org.chartsy.main.dataset;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Vector;
import org.chartsy.main.utils.DateCompare;

/**
 *
 * @author viorel.gheba
 */
public class Dataset {

    private DataItem[] data;
    private Vector<DataItem> dataItems = new Vector<DataItem>();

    public Dataset(DataItem[] data) {
        this.data = data;
        dataItems.addAll(Arrays.asList(data));
        DateCompare compare = new DateCompare();
        Collections.sort(dataItems, compare);
    }

    public boolean isEmpty() { return (data.length == 0); }
    public DataItem[] getData() { return data; }
    public Number getX(int item) { return new Long(data[item].getDate().getTime()); }
    public Date getDate(int item) { return data[item].getDate(); }
    public int getIndex(Date date) {
        int index = -1;
        for (int i = 0; i < getItemCount(); i++) {
            if (getDate(i).equals(date)) {
                index = i;
            }
        }
        return index;
    }

    public int getLastIndex() { return getItemCount() - 1; }
    public int getItemCount() { return data.length; }
    public void sordDataByDate() { Arrays.sort(data); }

    public double getMax() { return getMax(HIGH); }
    public double getMax(final String price) {
        double val = Double.MIN_VALUE;
        for (int i = 0; i < this.data.length; i++) {
            if (price.equals(OPEN)) val = Math.max(val, getOpenValue(i));
            if (price.equals(CLOSE)) val = Math.max(val, getCloseValue(i));
            if (price.equals(HIGH)) val = Math.max(val, getHighValue(i));
            if (price.equals(LOW)) val = Math.max(val, getLowValue(i));
        }
        return val;
    }

    public double getMaxNotZero() { return getMaxNotZero(HIGH); }
    public double getMaxNotZero(String price) {
        double val = Double.MIN_VALUE;
        for (int i = 0; i < this.data.length; i++) {
            if (price.equals(OPEN)) val = Math.max(val, getOpenValue(i) != 0 ? getOpenValue(i) : val);
            if (price.equals(CLOSE)) val = Math.max(val, getCloseValue(i) != 0 ? getCloseValue(i) : val);
            if (price.equals(HIGH)) val = Math.max(val, getHighValue(i) != 0 ? getHighValue(i) : val);
            if (price.equals(LOW)) val = Math.max(val, getLowValue(i) != 0 ? getLowValue(i) : val);
        }
        return val;
    }

    public double getMin() { return getMin(LOW); }
    public double getMin(final String price) {
        double val = Double.MAX_VALUE;
        for (int i = 0; i < this.data.length; i++) {
            if (price.equals(OPEN)) val = Math.min(val, getOpenValue(i));
            if (price.equals(CLOSE)) val = Math.min(val, getCloseValue(i));
            if (price.equals(HIGH)) val = Math.min(val, getHighValue(i));
            if (price.equals(LOW)) val = Math.min(val, getLowValue(i));
        }
        return val;
    }

    public double getMinNotZero() { return getMinNotZero(LOW); }
    public double getMinNotZero(String price) {
        double val = Double.MAX_VALUE;
        for (int i = 0; i < this.data.length; i++) {
            if (price.equals(OPEN)) val = Math.min(val, getOpenValue(i) != 0 ? getOpenValue(i) : val);
            if (price.equals(CLOSE)) val = Math.min(val, getCloseValue(i) != 0 ? getCloseValue(i) : val);
            if (price.equals(HIGH)) val = Math.min(val, getHighValue(i) != 0 ? getHighValue(i) : val);
            if (price.equals(LOW)) val = Math.min(val, getLowValue(i) != 0 ? getLowValue(i) : val);
        }
        return val;
    }

    public double getVolumeMax() {
        double val = Double.MIN_VALUE;
        for (int i = 0; i < this.data.length; i++) {
            val = Math.max(val, this.getVolumeValue(i));
        }
        return val;
    }

    public Number getOpen(int item) { return this.data[item].getOpen(); }
    public Number getClose(int item) { return this.data[item].getClose(); }
    public Number getHigh(int item) { return this.data[item].getHigh(); }
    public Number getLow(int item) { return this.data[item].getLow(); }
    public Number getVolume(int item) { return this.data[item].getVolume(); }
    public Number getAdjClose(int item) { return this.data[item].getAdjClose(); }

    public double getOpenValue(int item) {
        double result = Double.NaN;
        Number openVal = getOpen(item);
        if (openVal != null) result = openVal.doubleValue();
        return result;
    }

    public double getCloseValue(int item) {
        double result = Double.NaN;
        Number closeVal = getClose(item);
        if (closeVal != null) result = closeVal.doubleValue();
        return result;
    }

    public double getHighValue(int item) {
        double result = Double.NaN;
        Number highVal = getHigh(item);
        if (highVal != null) result = highVal.doubleValue();
        return result;
    }

    public double getLowValue(int item) {
        double result = Double.NaN;
        Number lowVal = getLow(item);
        if (lowVal != null) result = lowVal.doubleValue();
        return result;
    }

    public double getVolumeValue(int item) {
        double result = Double.NaN;
        Number volumeVal = getVolume(item);
        if (volumeVal != null) result = volumeVal.doubleValue();
        return result;
    }

    public double getAdjCloseValue(int item) {
        double result = Double.NaN;
        Number adjValue = getAdjClose(item);
        if (adjValue != null) result = adjValue.doubleValue();
        return result;
    }

    public double getLastPriceValue(String price) {
        double result = Double.NaN;
        if (price.equals(OPEN)) result = getOpenValue(getLastIndex());
        if (price.equals(CLOSE)) result = getCloseValue(getLastIndex());
        if (price.equals(HIGH)) result = getHighValue(getLastIndex());
        if (price.equals(LOW)) result = getLowValue(getLastIndex());
        if (price.equals(VOLUME)) result = getVolumeValue(getLastIndex());
        return result;
    }

    public double getPriceValue(int item, String price) {
        double result = Double.NaN;
        if (price.equals(OPEN)) result = getOpenValue(item);
        if (price.equals(CLOSE)) result = getCloseValue(item);
        if (price.equals(HIGH)) result = getHighValue(item);
        if (price.equals(LOW)) result = getLowValue(item);
        if (price.equals(VOLUME)) result = getVolumeValue(item);
        return result;
    }

    public double getPriceSumValue(int start, int end, String price) {
        if (start < 0) start = 0;
        if (end > data.length) end = data.length;
        double result = 0;
        for (int i = start; i < end; i++) result += getPriceValue(i, price);
        return result;
    }

    public Dataset getDrawableDataset(int itemsNumber, int end) {
        Vector<DataItem> list = new Vector<DataItem>();
        for (int i = 0; i < itemsNumber; i++) {
            int j = end - itemsNumber + i;
            if (j < getItemCount() && j >= 0) {
                Date date = getDate(j);
                double open = getOpenValue(j);
                double close = getCloseValue(j);
                double high = getHighValue(j);
                double low = getLowValue(j);
                double volume = getVolumeValue(j);
                double adjclose = getAdjCloseValue(j);
                DataItem item = new DataItem(date, open, close, high, low, volume, adjclose);
                list.add(item);
            }
        }
        DataItem[] items = list.toArray(new DataItem[list.size()]);
        return new Dataset(items);
    }

    public Dataset getSMA(int period) {
        Vector<DataItem> list = new Vector<DataItem>();
        for (int i = 0; i < period; i++) {
            DataItem item = new DataItem(getDate(i), 0, 0, 0, 0, 0, 0);
            list.add(item);
        }
        for (int i = period; i < getItemCount(); i++) {
            double open = 0; double close = 0; double high = 0; double low = 0;
            for (int j = 0; j < period; j++) {
                open += getOpenValue(i-j);
                close += getCloseValue(i-j);
                high += getHighValue(i-j);
                low += getLowValue(i-j);
            }
            open /= period; close /= period; high /= period; low /= period;
            DataItem item = new DataItem(getDate(i), open, close, high, low, 0, 0);
            list.add(item);
        }
        DataItem[] items = list.toArray(new DataItem[list.size()]);
        return new Dataset(items);
    }

    public Dataset getEMA(int period) {
        Vector<DataItem> list = new Vector<DataItem>();
        for (int i = 0; i < period; i++) {
            DataItem item = new DataItem(getDate(i), 0, 0, 0, 0, 0, 0);
            list.add(item);
        }
        double open = 0; double close = 0; double high = 0; double low = 0;
        for (int i = 0; i < period; i++) {
            open += getOpenValue(i);
            close += getCloseValue(i);
            high += getHighValue(i);
            low += getLowValue(i);
        }
        close /= period; open /= period; high /= period; low /= period;
        for (int i = period; i < getItemCount(); i++) {
            double open2 = 0; double close2 = 0; double high2 = 0; double low2 = 0;
            open2 = (2 * (getOpenValue(i) - open))/(1 + period) + open;
            close2 = (2 * (getCloseValue(i) - close))/(1 + period) + close;
            high2 = (2 * (getHighValue(i) - high))/(1 + period) + high;
            low2 = (2 * (getLowValue(i) - low))/(1 + period) + low;
            open = open2; close = close2; high = high2; low = low2;
            DataItem item = new DataItem(getDate(i), open2, close2, high2, low2, 0, 0);
            list.add(item);
        }
        DataItem[] items = list.toArray(new DataItem[list.size()]);
        return new Dataset(items);
    }

    public void enterData(DataItem item) { enterData(item.getDate(), item.getOpenValue(), item.getCloseValue(), item.getHighValue(), item.getLowValue(), item.getVolumeValue(), item.getAdjCloseValue()); }
    public void enterData(Date date, double open, double close, double high, double low, double volume, double adjclose) {
        DataItem last = data[getLastIndex()];
        DataItem item = new DataItem(date, open, close, high, low, volume, adjclose);

        System.out.println(last.getDate());
        System.out.println(item.getDate());

        state = item.compareTo(last) > 0 ? ADD : item.compareTo(last) == 0 ? UPDATE : -1;
        System.out.println(state);

        switch (state) {
            case ADD:
                dataItems.add(item);
                data = dataItems.toArray(new DataItem[dataItems.size()]);
                break;
            case UPDATE:
                data[data.length - 1].update(open, close, high, low, volume, adjclose);
                break;
        }
    }

    public static final String OPEN = "Open";
    public static final String CLOSE = "Close";
    public static final String HIGH = "High";
    public static final String LOW = "Low";
    public static final String VOLUME = "Volume";
    public static final String[] LIST = new String[] {OPEN, CLOSE, HIGH, LOW};

    private static final int ADD = 0;
    private static final int UPDATE = 1;
    private int state;

}
