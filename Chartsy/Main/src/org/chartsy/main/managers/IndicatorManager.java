package org.chartsy.main.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import org.chartsy.main.chartsy.chart.AbstractIndicator;
import org.openide.util.Lookup;

/**
 *
 * @author viorel.gheba
 */
public class IndicatorManager {

    protected static IndicatorManager instance;
    protected LinkedHashMap<String, AbstractIndicator> indicators;

    public static IndicatorManager getDefault() {
        if (instance == null) instance = new IndicatorManager();
        return instance;
    }

    protected IndicatorManager() {}

    public void initialize() {
        indicators = new LinkedHashMap<String, AbstractIndicator>();
        Collection<? extends AbstractIndicator> list = Lookup.getDefault().lookupAll(AbstractIndicator.class);
        for (AbstractIndicator ai : list) {
            addIndicator(ai.getName(), ai);
        }
        sort();
    }

    protected void sort() {
        List<String> mapKeys = new ArrayList<String>(indicators.keySet());
        Collections.sort(mapKeys);
        
        LinkedHashMap<String, AbstractIndicator> someMap = new LinkedHashMap<String, AbstractIndicator>();
        for (int i = 0; i < mapKeys.size(); i++)
            someMap.put(mapKeys.get(i), indicators.get(mapKeys.get(i)));
        indicators = someMap;
    }

    public void addIndicator(String key, AbstractIndicator value) { indicators.put(key, value); }
    public void removeIndicator(String key) { indicators.remove(key); }
    
    public AbstractIndicator[] getIndicators() {
        AbstractIndicator[] list = new AbstractIndicator[indicators.size()];
        int i = 0;
        Iterator it = indicators.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            AbstractIndicator value = indicators.get(key);
            if (value != null) {
                list[i] = value;
                i++;
            }
        }
        return list;
    }

    public AbstractIndicator getIndicator(String key) {
        return indicators.get(key);
    }

    public void print() {
        Iterator it = indicators.keySet().iterator();
        while (it.hasNext()) System.out.println(it.next().toString());
    }

}
