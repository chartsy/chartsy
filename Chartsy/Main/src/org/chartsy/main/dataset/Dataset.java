package org.chartsy.main.dataset;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Vector;
import org.chartsy.main.utils.DateCompare;

/**
 *
 * @author viorel.gheba
 */
public class Dataset implements Serializable {

    private static final long serialVersionUID = 101L;

    private DataItem[] data;
    private Vector<DataItem> dataItems = new Vector<DataItem>();

    public Dataset(DataItem[] data) {
        this.data = data;
        dataItems.addAll(Arrays.asList(data));
        DateCompare compare = new DateCompare();
        Collections.sort(dataItems, compare);
    }

    public boolean isEmpty() { return (data.length == 0); }
    public boolean isItemNull(int i) {
        if (i < 0 || i > getItemCount()) return true;
        DataItem d = getData()[i];
        return (d.getOpenValue() == 0 && d.getCloseValue() == 0 && d.getHighValue() == 0 && d.getLowValue() == 0);
    }
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
        for (int i = 0; i < period - 1; i++)
            list.add(new DataItem(getDate(i), 0, 0, 0, 0, 0, 0));
        
        for (int i = period - 1; i < getItemCount(); i++) {
            double open = 0;
            double close = 0;
            double high = 0;
            double low = 0;
            
            for (int j = i - period + 1; j <= i; j++) {
                open += getOpenValue(j);
                close += getCloseValue(j);
                high += getHighValue(j);
                low += getLowValue(j);
            }
            
            open /= (double)period;
            close /= (double)period;
            high /= (double)period;
            low /= (double)period;
            
            list.add(new DataItem(getDate(i), open, close, high, low, 0, 0));
        }
        
        DataItem[] items = list.toArray(new DataItem[list.size()]);
        return new Dataset(items);
    }

    public Dataset getEMA(int period) {
        Vector<DataItem> list = new Vector<DataItem>();
        
        double open = 0;
        double close = 0;
        double high = 0;
        double low = 0;
        
        for (int i = 0; i < period; i++) {
            open += getOpenValue(i);
            close += getCloseValue(i);
            high += getHighValue(i);
            low += getLowValue(i);

            if (i == period - 1) {
                open /= (double)period;
                close /= (double)period;
                high /= (double)period;
                low /= (double)period;
                
                list.add(new DataItem(getDate(i), open, close, high, low, 0, 0));
            } else {
                list.add(new DataItem(getDate(i), 0, 0, 0, 0, 0, 0));
            }
        }

        double k = 2 / ((double)(period + 1));
        for (int i = period; i < getItemCount(); i++) {
            open = (getOpenValue(i) - open) * k + open;
            close = (getCloseValue(i) - close) * k + close;
            high = (getHighValue(i) - high) * k + high;
            low = (getLowValue(i) - low) * k + low;
            
            list.add(new DataItem(getDate(i), open, close, high, low, 0, 0));
        }
        
        DataItem[] items = list.toArray(new DataItem[list.size()]);
        return new Dataset(items);
    }

    public static Dataset SUM(Dataset d1, Dataset d2) {
        if (d1==null || d2==null) return null;

        Vector<DataItem> list = new Vector<DataItem>();

        for (int i = 0; i < d1.getItemCount(); i++) {
            double open = d1.getOpenValue(i) + d2.getOpenValue(i);
            double close = d1.getCloseValue(i) + d2.getCloseValue(i);
            double high = d1.getHighValue(i) + d2.getHighValue(i);
            double low = d1.getLowValue(i) + d2.getLowValue(i);

            list.add(new DataItem(d1.getDate(i), open, close, high, low, 0, 0));
        }

        DataItem[] items = list.toArray(new DataItem[list.size()]);
        return new Dataset(items);
    }

    public static Dataset DIFF(Dataset d1, Dataset d2) {
        if (d1==null || d2==null) return null;

        Vector<DataItem> list = new Vector<DataItem>();

        for (int i = 0; i < d1.getItemCount(); i++) {
            double open = d1.getOpenValue(i) - d2.getOpenValue(i);
            double close = d1.getCloseValue(i) - d2.getCloseValue(i);
            double high = d1.getHighValue(i) - d2.getHighValue(i);
            double low = d1.getLowValue(i) - d2.getLowValue(i);
            
            list.add(new DataItem(d1.getDate(i), open, close, high, low, 0, 0));
        }

        DataItem[] items = list.toArray(new DataItem[list.size()]);
        return new Dataset(items);
    }

    public static Dataset DIV(Dataset dataset, double d) {
        if (dataset==null) return null;

        Dataset result = Dataset.EMPTY(dataset);
        for (int i = 0; i < dataset.getItemCount(); i++)
            result.setData(dataset.getOpenValue(i)/d, dataset.getCloseValue(i)/d, dataset.getHighValue(i)/d, dataset.getLowValue(i)/d, dataset.getVolumeValue(i)/d, dataset.getAdjCloseValue(i)/d, i);

        return result;
    }

    public static Dataset SMA(Dataset dataset, int period) {
        if (dataset == null) return null;

        Vector<DataItem> list = new Vector<DataItem>();

        for (int i = 0; i < period - 1; i++)
            list.add(new DataItem(dataset.getDate(i), 0, 0, 0, 0, 0, 0));

        for (int i = period - 1; i < dataset.getItemCount(); i++) {
            double open = 0;
            double close = 0;
            double high = 0;
            double low = 0;

            for (int j = i - period + 1; j <= i; j++) {
                open += dataset.getOpenValue(j);
                close += dataset.getCloseValue(j);
                high += dataset.getHighValue(j);
                low += dataset.getLowValue(j);
            }

            open /= (double)period;
            close /= (double)period;
            high /= (double)period;
            low /= (double)period;

            list.add(new DataItem(dataset.getDate(i), open, close, high, low, 0, 0));
        }
        
        DataItem[] items = list.toArray(new DataItem[list.size()]);
        return new Dataset(items);
    }

    public static Dataset EMA(Dataset dataset, int period) {
        if (dataset == null) return null;

        Vector<DataItem> list = new Vector<DataItem>();

        int j = 0;
        for (j = 0; j < dataset.getItemCount() && (dataset.getCloseValue(j)==0); j++)
            list.add(new DataItem(dataset.getDate(j), 0, 0, 0, 0, 0, 0));

        double open = 0;
        double close = 0;
        double high = 0;
        double low = 0;
        
        for (int i = j; i < period + j && i < dataset.getItemCount(); i++) {
            open += dataset.getOpenValue(i);
            close += dataset.getCloseValue(i);
            high += dataset.getHighValue(i);
            low += dataset.getLowValue(i);

            if (i == period + j - 1) {
                open /= (double)period;
                close /= (double)period;
                high /= (double)period;
                low /= (double)period;
                
                list.add(new DataItem(dataset.getDate(i), open, close, high, low, 0, 0));
            } else {
                list.add(new DataItem(dataset.getDate(i), 0, 0, 0, 0, 0, 0));
            }
        }

        double k = 2 / ((double)(period + 1));
        for (int i = period + j; i < dataset.getItemCount(); i++) {
            open = (dataset.getOpenValue(i) - open) * k + open;
            close = (dataset.getCloseValue(i) - close) * k + close;
            high = (dataset.getHighValue(i) - high) * k + high;
            low = (dataset.getLowValue(i) - low) * k + low;
            
            list.add(new DataItem(dataset.getDate(i), open, close, high, low, 0, 0));
        }

        DataItem[] items = list.toArray(new DataItem[list.size()]);
        return new Dataset(items);
    }

    public static Dataset WMA(Dataset dataset, int period) {
        if (dataset == null) return null;
        
        Vector<DataItem> list = new Vector<DataItem>();
        double denominator = (period * (period + 1)) / 2;

        int j = 0;
        for (j = 0; j < dataset.getItemCount() && (dataset.getCloseValue(j)==0); j++)
            list.add(new DataItem(dataset.getDate(j), 0, 0, 0, 0, 0, 0));

        for (int i = j; i < period + j; i++)
            list.add(new DataItem(dataset.getDate(i), 0, 0, 0, 0, 0, 0));

        for (int i = period + j; i < dataset.getItemCount(); i++) {
            double open = 0;
            double close = 0;
            double high = 0;
            double low = 0;
            
            for (int k = i - period; k < i; k++) {
                open += (period - i + k + 1) * dataset.getOpenValue(k);
                close += (period - i + k + 1) * dataset.getCloseValue(k);
                high += (period - i + k + 1) * dataset.getHighValue(k);
                low += (period - i + k + 1) * dataset.getLowValue(k);
            }

            open /= denominator;
            close /= denominator;
            high /= denominator;
            low /= denominator;

            list.add(new DataItem(dataset.getDate(i), open, close, high, low, 0, 0));
        }

        DataItem[] items = list.toArray(new DataItem[list.size()]);
        return new Dataset(items);
    }

    public static Dataset TEMA(Dataset dataset, int period) {
        if (dataset == null) return null;

        Vector<DataItem> list = new Vector<DataItem>();

        Dataset ema1 = EMA(dataset, period);
        Dataset ema2 = EMA(ema1, period);
        Dataset ema3 = EMA(ema2, period);

        int j = 0;
        for (j = 0; j < dataset.getItemCount() && (dataset.getCloseValue(j)==0 || ema1.getCloseValue(j)==0 || ema2.getCloseValue(j)==0 || ema3.getCloseValue(j)==0); j++)
            list.add(new DataItem(dataset.getDate(j), 0, 0, 0, 0, 0, 0));

        for (int i = j; i < dataset.getItemCount(); i++) {
            double open = 0;
            double close = 0;
            double high = 0;
            double low = 0;

            if (ema1.getCloseValue(i)!=0 && ema2.getCloseValue(i)!=0 && ema3.getCloseValue(i)!=0) {
                open = 3*ema1.getOpenValue(i) - 3*ema2.getOpenValue(i) + ema3.getOpenValue(i);
                close = 3*ema1.getCloseValue(i) - 3*ema2.getCloseValue(i) + ema3.getCloseValue(i);
                high = 3*ema1.getHighValue(i) - 3*ema2.getHighValue(i) + ema3.getHighValue(i);
                low = 3*ema1.getLowValue(i) - 3*ema2.getLowValue(i) + ema3.getLowValue(i);
            }

            list.add(new DataItem(dataset.getDate(i), open, close, high, low, 0, 0));
        }

        DataItem[] items = list.toArray(new DataItem[list.size()]);
        return new Dataset(items);
    }

    public static Dataset[] ADX(Dataset dataset, int period) {
        if (dataset == null) return null;

        Dataset pdi = Dataset.EMPTY(dataset);
        Dataset mdi = Dataset.EMPTY(dataset);
        Dataset dx = Dataset.EMPTY(dataset);

        Dataset tr = Dataset.EMPTY(dataset);
        Dataset hmhp = Dataset.EMPTY(dataset);
        Dataset lmlp = Dataset.EMPTY(dataset);

        for (int i = 1; i < dataset.getItemCount(); i++) {
            double tr0 = dataset.getHighValue(i) - dataset.getCloseValue(i-1);
            double tr1 = Math.abs(dataset.getHighValue(i) - dataset.getCloseValue(i-1));
            tr0 = Math.max(tr0, tr1);
            tr1 = Math.abs(dataset.getLowValue(i) - dataset.getCloseValue(i-1));
            tr.setData(0, Math.max(tr0, tr1), 0, 0, 0, 0, i);

            if (dataset.getHighValue(i) <= dataset.getHighValue(i-1) && dataset.getLowValue(i) < dataset.getLowValue(i-1)) {
                lmlp.setData(0, dataset.getLowValue(i-1) - dataset.getLowValue(i), 0, 0, 0, 0, i);
            } else if (dataset.getHighValue(i) > dataset.getHighValue(i-1) && dataset.getLowValue(i) >= dataset.getLowValue(i-1)) {
                hmhp.setData(0, dataset.getHighValue(i) - dataset.getHighValue(i-1), 0, 0, 0, 0, i);
            } else {
                double tempH = Math.abs(dataset.getHighValue(i) - dataset.getHighValue(i-1));
                double tempL = Math.abs(dataset.getLowValue(i) - dataset.getLowValue(i-1));
                
                if (tempH > tempL) hmhp.setData(0, tempH, 0, 0, 0, 0, i);
                else lmlp.setData(0, tempL, 0, 0, 0, 0, i);
            }
        }

        Dataset strDS = Dataset.EMA(tr, period);
        Dataset shmhpDS = Dataset.EMA(hmhp, period);
        Dataset slmlpDS = Dataset.EMA(lmlp, period);

        for (int i = period; i < dataset.getItemCount(); i++) {
            double curPDI = 0;
            double curMDI = 0;

            if (strDS.getCloseValue(i) != 0) {
                curPDI = (shmhpDS.getCloseValue(i) / strDS.getCloseValue(i)) * 100;
                curMDI = (slmlpDS.getCloseValue(i) / strDS.getCloseValue(i)) * 100;
            }

            pdi.setData(0, curPDI, 0, 0, 0, 0, i);
            mdi.setData(0, curMDI, 0, 0, 0, 0, i);

            if (curPDI + curMDI != 0)
                dx.setData(0, (Math.abs(curPDI - curMDI) / (curPDI + curMDI) * 100), 0, 0, 0, 0, i);
        }

        Dataset adx = Dataset.EMA(dx, period);

        Dataset[] result = new Dataset[3];
        result[0] = pdi;
        result[1] = mdi;
        result[2] = adx;
        return result;
    }

    public static Dataset EMPTY(Dataset dataset) {
        Vector<DataItem> list = new Vector<DataItem>();

        for (int i = 0; i < dataset.getItemCount(); i++)
            list.add(new DataItem(dataset.getDate(i), 0, 0, 0, 0, 0, 0));

        DataItem[] items = list.toArray(new DataItem[list.size()]);
        return new Dataset(items);
    }

    public void setData(double open, double close, double high, double low, double volume, double adjclose, int i) {
        if (this.data.length < i) return;
        DataItem dataItem = data[i];
        dataItem.update(open, close, high, low, volume, adjclose);
        data[i] = dataItem;
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
