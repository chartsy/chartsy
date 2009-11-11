package org.chartsy.main.managers;

import java.util.Hashtable;
import java.util.Iterator;
import org.chartsy.main.dataset.Dataset;

/**
 *
 * @author viorel.gheba
 */
public class DatasetManager {

    public static final String DAILY = "Daily";
    public static final String WEEKLY = "Weekly";
    public static final String MONTHLY = "Monthly";
    public static final String[] LIST = {"Daily", "Weekly", "Monthly"};
    public static final String DELIMITATOR = "-";

    protected static DatasetManager instance;
    protected Hashtable<Object, Object> datasets;

    public static DatasetManager getDefault() {
        if (instance == null) instance = new DatasetManager();
        return instance;
    }

    protected DatasetManager() {}

    public void initialize() {
        datasets = new Hashtable<Object, Object>();
    }

    public static String getName(String symbol, String time) { return symbol + DELIMITATOR + time; }

    public void addDataset(Object key, Object value) { datasets.put(key, value); }
    public void removeDataset(Object key) { datasets.remove(key); }
    public void removeAll() { datasets.clear(); }

    public Object getDatasetObject(Object key) { return datasets.get(key); }
    public Dataset getDataset(Object key) {
        Object obj = datasets.get(key);
        if (obj != null && obj instanceof Dataset) return (Dataset) obj;
        return null;
    }

    public boolean dataExists(String symbol) {
        Iterator it = datasets.keySet().iterator();
        while (it.hasNext()) {
            String s = (String) it.next();
            if (s.startsWith(symbol + DELIMITATOR)) return true;
        }
        return false;
    }

    public void print() {
        Iterator it = datasets.keySet().iterator();
        while (it.hasNext()) System.out.println(it.next().toString());
    }

}
