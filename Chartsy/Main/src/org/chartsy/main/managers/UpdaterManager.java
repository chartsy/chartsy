package org.chartsy.main.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;
import org.chartsy.main.dataset.Dataset;
import org.chartsy.main.updater.AbstractUpdater;
import org.chartsy.main.utils.Stock;
import org.openide.util.Lookup;

/**
 *
 * @author viorel.gheba
 */
public class UpdaterManager {

    protected static UpdaterManager instance;
    protected LinkedHashMap<String, AbstractUpdater> updaters;
    protected boolean update = false;

    public static UpdaterManager getDefault() {
        if (instance == null) instance = new UpdaterManager();
        return instance;
    }

    protected UpdaterManager() {}

    public void initialize() {
        updaters = new LinkedHashMap<String, AbstractUpdater>();
        Collection<? extends AbstractUpdater> list = Lookup.getDefault().lookupAll(AbstractUpdater.class);
        for (AbstractUpdater au : list) {
            addUpdater(au.getName(), au);
        }
    }

    protected void sort() {
        List<String> mapKeys = new ArrayList<String>(updaters.keySet());
        Collections.sort(mapKeys);

        LinkedHashMap<String, AbstractUpdater> someMap = new LinkedHashMap<String, AbstractUpdater>();
        for (int i = 0; i < mapKeys.size(); i++)
            someMap.put(mapKeys.get(i), updaters.get(mapKeys.get(i)));
        updaters = someMap;
    }

    public void addUpdater(String key, AbstractUpdater value) { updaters.put(key, value); }
    public void removeUpdater(String key) { updaters.remove(key); }

    public AbstractUpdater getUpdater(String key) {
        Collection<? extends AbstractUpdater> list = Lookup.getDefault().lookupAll(AbstractUpdater.class);
        for (AbstractUpdater au : list) {
            if (au.getName().equals(key))
                return au;
        }
        return null;
    }

    public Vector getUpdaters() {
        Vector v = new Vector();
        Iterator it = updaters.keySet().iterator();
        while (it.hasNext()) v.add(it.next());
        Collections.sort(v);
        return v;
    }

    public void update(Stock stock, AbstractUpdater updater) {
        if (updater != null) {
            for (String time : AbstractUpdater.LIST) {
                Dataset dataset = updater.update(stock.getKey(), time);
                if (dataset != null) {
                    updater.addDataset(updater.getKey(stock, time), dataset);
                }
            }
        }
        setUpdate(true);
    }

    public void update(Stock[] stocks, AbstractUpdater abstractUpdater) {
        if (abstractUpdater != null) {
            for (Stock stock : stocks) {
                for (String time : AbstractUpdater.LIST) {
                    Dataset dataset = abstractUpdater.update(stock.getKey(), time);
                    if (dataset != null) {
                        abstractUpdater.addDataset(abstractUpdater.getKey(stock, time), dataset);
                    }
                }
            }
        }
        setUpdate(true);
    }

    public void update(LinkedHashMap stocks, AbstractUpdater abstractUpdater) {
        if (abstractUpdater != null) {
            Iterator it = stocks.keySet().iterator();
            while (it.hasNext()) {
                Stock stock = (Stock) it.next();
                String time = (String) stocks.get(stock);
                if (!time.contains("Min")) {
                    for (String t : AbstractUpdater.LIST) {
                        Dataset dataset = abstractUpdater.update(stock.getKey(), t);
                        if (dataset != null) {
                            abstractUpdater.addDataset(abstractUpdater.getKey(stock, t), dataset);
                        }
                    }
                } else {
                    for (String t : AbstractUpdater.LIST) {
                        Dataset dataset = abstractUpdater.update(stock.getKey(), t);
                        if (dataset != null) {
                            abstractUpdater.addDataset(abstractUpdater.getKey(stock, t), dataset);
                        }
                    }
                    Dataset dataset = abstractUpdater.updateIntraDay(stock.getKey(), time);
                    if (dataset != null) {
                        abstractUpdater.addDataset(abstractUpdater.getKey(stock, time), dataset);
                    }
                }
            }
        }
        setUpdate(true);
    }

    public void update(Stock stock, String time, AbstractUpdater abstractUpdater) {
        if (abstractUpdater != null) {
            Dataset dataset = (!time.contains("Min")) ? abstractUpdater.update(stock.getKey(), time) : abstractUpdater.updateIntraDay(stock.getKey(), time);
            if (dataset != null) {
                abstractUpdater.addDataset(abstractUpdater.getKey(stock, time), dataset);
            }
        }
        setUpdate(true);
    }

    public void updateIntraDay(Stock stock, String time, AbstractUpdater abstractUpdater) {
        if (abstractUpdater != null) {
            Dataset dataset = abstractUpdater.updateIntraDay(stock.getKey(), time);
            if (dataset != null) {
                abstractUpdater.addDataset(abstractUpdater.getKey(stock, time), dataset);
            }
        }
        setUpdate(true);
    }

    public void setUpdate(boolean b) { update = b; }
    public boolean isUpdated() { return update; }

    public void print() {
        Iterator it = updaters.keySet().iterator();
        while (it.hasNext()) System.out.println(it.next().toString());
    }

}
