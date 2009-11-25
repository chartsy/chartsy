package org.chartsy.main.updater;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Vector;
import org.chartsy.main.dataset.Dataset;
import org.chartsy.main.utils.Stock;

/**
 *
 * @author viorel.gheba
 */
public abstract class AbstractUpdater implements Serializable {

    private static final long serialVersionUID = 101L;

    public static final String DAILY = "Daily";
    public static final String WEEKLY = "Weekly";
    public static final String MONTHLY = "Monthly";
    public static final String[] LIST = {"Daily", "Weekly", "Monthly"};

    protected String name;
    protected Hashtable<Object, Object> xcg;
    protected Vector exchanges;
    protected Vector sufixes;
    protected Vector times;
    protected Hashtable<Object, Object> userdata;
    protected LinkedHashMap<Object, Object> datasets = new LinkedHashMap<Object, Object>();

    public AbstractUpdater(final String name, final String[] exchanges, final String[] sufixes, final String[] times) {
        this.name = name;
        this.exchanges = new Vector();
        this.sufixes = new Vector();
        this.times = new Vector();
        this.userdata = new Hashtable<Object, Object>();
        this.datasets = new LinkedHashMap<Object, Object>();

        if (exchanges != null) {
            this.exchanges.add("Default");
            for (String s : exchanges) {
                this.exchanges.add(s);
            }
        }

        if (sufixes != null) {
            this.sufixes.add("");
            for (String s : sufixes) {
                this.sufixes.add(s);
            }
        }

        if (times != null) {
            for (String s : times) {
                this.times.add(s);
            }
        }

        xcg = new Hashtable<Object, Object>();
        for (int i = 0; i < exchanges.length; i++) {
            xcg.put(exchanges[i], sufixes[i]);
        }

        initializeUserData();
    }

    public String getName() { return name; }    
    public Vector getExchanges() { return exchanges; }
    public Vector getSufixes() { return sufixes; }
    public Vector getTimes() { return times; }
    public String getSufix(Object key) { return xcg.get(key) == null ? "" : (String) xcg.get(key); }

    public abstract Stock getStock(String symbol, String exchange);
    public abstract Dataset update(String symbol, String time);
    public abstract Dataset updateLastValues(String symbol, String time, Dataset dataset);
    public abstract Dataset updateIntraDay(String symbol, String time);
    public abstract Dataset updateIntraDayLastValues(String symbol, String time, Dataset dataset);

    protected abstract void initializeUserData();
    public Hashtable getUserData() { return userdata; }
    public void clearUserData() { userdata.clear(); }
    public void setUserData(Object key, Object value) { userdata.put(key, value); }

    public String getKey(String symbol, String time) { return symbol + "-" + time; }
    public String getKey(Stock stock, String time) { return stock.getKey() + "-" + time; }

    public void addDataset(Object key, Object value) { datasets.put(key, value); }
    public void removeDataset(Object key) { datasets.remove(key); }
    public void removeAllDatasets() { datasets.clear(); datasets = new LinkedHashMap<Object, Object>(); }

    public Object getDatasetObject(Object key) { return datasets.get(key); }
    public Dataset getDataset(Object key) {
        Object obj = datasets.get(key);
        if (obj != null && obj instanceof Dataset) {
            return (Dataset) obj;
        }
        return null;
    }
    public Dataset getDataset(Stock stock) {
        Object obj = datasets.get(getKey(stock, DAILY));
        if (obj != null && obj instanceof Dataset) {
            return (Dataset) obj;
        }
        return null;
    }
    public Dataset getDataset(Stock stock, String time) {
        Object obj = datasets.get(getKey(stock, time));
        if (obj != null && obj instanceof Dataset) {
            return (Dataset) obj;
        }
        return null;
    }

    public boolean datasetExists(Object key) { return (datasets.get(key) != null); }
    public boolean datasetExists(Stock stock) { return (datasets.get(getKey(stock, DAILY)) != null); }
    public boolean datasetExists(Stock stock, String time) { return (datasets.get(getKey(stock, time)) != null); }

}
