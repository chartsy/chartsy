package org.chartsy.main.updater;

import java.util.Hashtable;
import java.util.Vector;
import org.chartsy.main.dataset.Dataset;

/**
 *
 * @author viorel.gheba
 */
public abstract class AbstractUpdater {

    private String name;
    private Hashtable<Object, Object> xcg;
    private Vector exchanges;
    private Vector sufixes;
    private Vector times;
    private Hashtable<Object, Object> userdata;

    public AbstractUpdater(final String name, final String[] exchanges, final String[] sufixes, final String[] times) {
        this.name = name;
        this.exchanges = new Vector();
        this.sufixes = new Vector();
        this.times = new Vector();
        this.userdata = new Hashtable<Object, Object>();

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

    public abstract Dataset update(String symbol, String time);
    public abstract Dataset updateLastValues(String symbol, String time, Dataset dataset);
    public abstract Dataset updateIntraDay(String symbol, String time);
    public abstract Dataset updateIntraDayLastValues(String symbol, String time, Dataset dataset);

    protected abstract void initializeUserData();
    public Hashtable getUserData() { return userdata; }
    public void clearUserData() { userdata.clear(); }
    public void setUserData(Object key, Object value) { userdata.put(key, value); }

}
