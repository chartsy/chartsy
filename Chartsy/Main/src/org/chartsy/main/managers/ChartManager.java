package org.chartsy.main.managers;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.Vector;
import org.chartsy.main.chartsy.chart.AbstractChart;

/**
 *
 * @author viorel.gheba
 */
public class ChartManager {

    protected static ChartManager instance;
    protected Hashtable<Object, Object> charts;

    public static ChartManager getDefault() {
        if (instance == null) instance = new ChartManager();
        return instance;
    }

    protected ChartManager() {}

    public void initialize() {
        charts = new Hashtable<Object, Object>();
        ServiceLoader<AbstractChart> service = ServiceLoader.load(AbstractChart.class);
        Iterator<AbstractChart> it = service.iterator();
        while (it.hasNext()) {
            AbstractChart c = it.next();
            addChart(c.getName(), c);
        }
    }

    public void addChart(Object key, Object value) { charts.put(key, value); }
    public void removeChart(Object key) { charts.remove(key); }

    public AbstractChart getChart(Object key) {
        Object obj = charts.get(key);
        if (obj != null && obj instanceof AbstractChart) return (AbstractChart) obj;
        return null;
    }

    public Vector getCharts() {
        Vector v = new Vector();
        Iterator it = charts.keySet().iterator();
        while (it.hasNext()) v.add(it.next());
        Collections.sort(v);
        return v;
    }

    public void print() {
        Iterator it = charts.keySet().iterator();
        while (it.hasNext()) System.out.println(it.next().toString());
    }

}
