package org.chartsy.main.managers;

import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Vector;
import org.chartsy.main.chartsy.chart.AbstractChart;
import org.openide.util.Lookup;

/**
 *
 * @author viorel.gheba
 */
public class ChartManager {

    protected static ChartManager instance;
    protected LinkedHashMap<Object, Object> charts;

    public static ChartManager getDefault() {
        if (instance == null) instance = new ChartManager();
        return instance;
    }

    protected ChartManager() {}

    public void initialize() {
        charts = new LinkedHashMap<Object, Object>();
        Collection<? extends AbstractChart> list = Lookup.getDefault().lookupAll(AbstractChart.class);
        for (AbstractChart c : list) {
            addChart(c.getName(), c);
        }
    }

    public void addChart(Object key, Object value) { charts.put(key, value); }
    public void removeChart(Object key) { charts.remove(key); }

    public AbstractChart getChart(Object key) {
        Collection<? extends AbstractChart> list = Lookup.getDefault().lookupAll(AbstractChart.class);
        for (AbstractChart c : list) {
            if (c.getName().equals((String) key)) return c;
        }
        return null;
    }

    public Vector getCharts() {
        Collection<? extends AbstractChart> list = Lookup.getDefault().lookupAll(AbstractChart.class);
        Vector v = new Vector();

        for (AbstractChart c : list) {
            v.add(c.getName());
        }
        
        Collections.sort(v);
        return v;
    }

    public void print() {
        Iterator it = charts.keySet().iterator();
        while (it.hasNext()) System.out.println(it.next().toString());
    }

}
