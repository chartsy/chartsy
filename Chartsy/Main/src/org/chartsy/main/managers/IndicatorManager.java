package org.chartsy.main.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import org.chartsy.main.chart.Indicator;
import org.openide.util.Lookup;

/**
 *
 * @author viorel.gheba
 */
public class IndicatorManager {

    private static IndicatorManager instance;
    private LinkedHashMap<String, Indicator> indicators;

    public static IndicatorManager getDefault() {
        if (instance == null) instance = new IndicatorManager();
        return instance;
    }

    private IndicatorManager() {
        indicators = new LinkedHashMap<String, Indicator>();
        Collection<? extends Indicator> list = Lookup.getDefault().lookupAll(Indicator.class);
        for (Indicator i : list) {
            indicators.put(i.getName(), i);
        }
        sort();
    }

    private void sort()
    {
        List<String> mapKeys = new ArrayList<String>(indicators.keySet());
        Collections.sort(mapKeys);

        LinkedHashMap<String, Indicator> someMap = new LinkedHashMap<String, Indicator>();
        for (int i = 0; i < mapKeys.size(); i++)
            someMap.put(mapKeys.get(i), indicators.get(mapKeys.get(i)));
        indicators = someMap;
    }

    public Indicator getIndicator(String key) {
        return indicators.get(key);
    }

    public List<Indicator> getIndicatorsList() {
        List<Indicator> list = new ArrayList<Indicator>();
        Iterator<String> it = indicators.keySet().iterator();
        while (it.hasNext()) {
            list.add(indicators.get(it.next()));
        }
        return list;
    }

    public List<String> getIndicators()
    {
        List<String> list = new ArrayList<String>(indicators.keySet());
        Collections.sort(list);
        return list;
    }

}
