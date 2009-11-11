package org.chartsy.main.managers;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.ServiceLoader;
import org.chartsy.main.chartsy.chart.AbstractIndicator;

/**
 *
 * @author viorel.gheba
 */
public class IndicatorManager {

    protected static IndicatorManager instance;
    protected Hashtable<Object, Object> indicators;

    public static IndicatorManager getDefault() {
        if (instance == null) instance = new IndicatorManager();
        return instance;
    }

    protected IndicatorManager() {}

    public void initialize() {
        indicators = new Hashtable<Object, Object>();
        ServiceLoader<AbstractIndicator> service = ServiceLoader.load(AbstractIndicator.class);
        Iterator<AbstractIndicator> it = service.iterator();
        while (it.hasNext()) {
            AbstractIndicator ai = it.next();
            addIndicator(ai.getName(), ai);
        }
    }

    public void addIndicator(Object key, Object value) { indicators.put(key, value); }
    public void removeIndicator(Object key) { indicators.remove(key); }
    
    public AbstractIndicator[] getIndicators() {
        AbstractIndicator[] list = new AbstractIndicator[indicators.size()];
        int i = 0;
        Iterator it = indicators.keySet().iterator();
        while (it.hasNext()) {
            Object obj = indicators.get(it.next());
            if (obj != null && obj instanceof AbstractIndicator) {
                list[i] = (AbstractIndicator) obj;
                i++;
            }
        }
        return list;
    }

    public AbstractIndicator getIndicator(Object key) {
        Object obj = indicators.get(key);
        if (obj != null && obj instanceof AbstractIndicator) return (AbstractIndicator) obj;
        return null;
    }

    public void print() {
        Iterator it = indicators.keySet().iterator();
        while (it.hasNext()) System.out.println(it.next().toString());
    }

}
