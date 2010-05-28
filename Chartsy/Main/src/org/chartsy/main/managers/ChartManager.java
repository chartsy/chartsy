package org.chartsy.main.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import org.chartsy.main.chart.Chart;
import org.openide.util.Lookup;

/**
 *
 * @author viorel.gheba
 */
public class ChartManager
{

    private static ChartManager instance;
    private LinkedHashMap<String, Chart> charts;

    public static ChartManager getDefault()
    {
        if (instance == null)
            instance = new ChartManager();
        return instance;
    }

    private ChartManager()
    {
        charts = new LinkedHashMap<String, Chart>();
        Collection<? extends Chart> list = Lookup.getDefault().lookupAll(Chart.class);
        for (Chart c : list)
            charts.put(c.getName(), c);
        sort();
    }

    private void sort()
    {
        List<String> mapKeys = new ArrayList<String>(charts.keySet());
        Collections.sort(mapKeys);

        LinkedHashMap<String, Chart> someMap = new LinkedHashMap<String, Chart>();
        for (int i = 0; i < mapKeys.size(); i++)
            someMap.put(mapKeys.get(i), charts.get(mapKeys.get(i)));
        charts = someMap;
    }

    public Chart getChart(String key)
    {
        return charts.get(key);
    }

    public List<String> getCharts()
    {
        List<String> list = new ArrayList<String>(charts.keySet());
        Collections.sort(list);
        return list;
    }

    public List<String> getCharts(String current)
    {
        List<String> list = new ArrayList<String>(charts.keySet());
        list.remove(current);
        Collections.sort(list);
        return list;
    }

}
