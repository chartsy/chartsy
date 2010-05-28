package org.chartsy.main.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import org.chartsy.main.data.DataProvider;
import org.chartsy.main.data.Dataset;
import org.chartsy.main.data.Stock;
import org.chartsy.main.intervals.Interval;
import org.openide.util.Lookup;

/**
 *
 * @author viorel.gheba
 */
public class DataProviderManager
{

    private static DataProviderManager instance;
    private static List<String> ignored;
    private LinkedHashMap<String, DataProvider> dataProviders;
    private boolean updated = false;

    public static DataProviderManager getDefault()
    {
        if (instance == null)
            instance = new DataProviderManager();
        return instance;
    }

    private DataProviderManager()
    {   
        dataProviders = new LinkedHashMap<String, DataProvider>();
        Collection<? extends DataProvider> list = Lookup.getDefault().lookupAll(DataProvider.class);
        for (DataProvider dp : list)
        {
            if (!ignored.contains(dp.getName()))
            {
                dp.initialize();
                dataProviders.put(dp.getName(), dp);
            }
        }

        sort();
    }

    private void sort()
    {
        List<String> mapKeys = new ArrayList<String>(dataProviders.keySet());
        Collections.sort(mapKeys);

        LinkedHashMap<String, DataProvider> someMap = new LinkedHashMap<String, DataProvider>();
        for (int i = 0; i < mapKeys.size(); i++)
            someMap.put(mapKeys.get(i), dataProviders.get(mapKeys.get(i)));
        dataProviders = someMap;
    }

    public DataProvider getDataProvider(String key) 
    { return dataProviders.get(key); }

    public List<String> getDataProviders() 
    {
        List<String> list = new ArrayList<String>(dataProviders.keySet());
        Collections.sort(list);
        return list;
    }

    public void setUpdated(boolean b) {
        updated = b;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void update(Stock stock, DataProvider dataProvider) {
        if (dataProvider != null) {
            for (Interval interval : dataProvider.getIntervals()) {
                Dataset dataset = dataProvider.getData(stock, interval);
                if (dataset != null) dataProvider.addDataset(dataProvider.getKey(stock, interval), dataset);
            }
        }
        setUpdated(true);
    }

    public void removeFiles()
    {
        List<DataProvider> list = new ArrayList<DataProvider>(dataProviders.values());
        for (DataProvider dp : list)
            dp.removeFiles();
    }

    static
    {
        ignored = new ArrayList<String>();
        //ignored.add("Yahoo");
    }

}
