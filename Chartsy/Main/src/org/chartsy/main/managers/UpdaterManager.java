package org.chartsy.main.managers;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.Vector;
import org.chartsy.main.RestoreSettings;
import org.chartsy.main.dataset.Dataset;
import org.chartsy.main.updater.AbstractUpdater;
import org.chartsy.main.utils.XMLUtils;

/**
 *
 * @author viorel.gheba
 */
public class UpdaterManager {

    protected static UpdaterManager instance;
    protected Hashtable<Object, Object> updaters;
    protected AbstractUpdater active;
    protected boolean update = false;

    public static UpdaterManager getDefault() {
        if (instance == null) instance = new UpdaterManager();
        return instance;
    }

    protected UpdaterManager() {}

    public void initialize() {
        updaters = new Hashtable<Object, Object>();
        ServiceLoader<AbstractUpdater> service = ServiceLoader.load(AbstractUpdater.class);
        Iterator<AbstractUpdater> it = service.iterator();
        while (it.hasNext()) {
            AbstractUpdater au = it.next();
            addUpdater(au.getName(), au);
        }
    }

    public void addUpdater(Object key, Object value) { updaters.put(key, value); }
    public void removeUpdater(Object key) { updaters.remove(key); }

    public AbstractUpdater getUpdater(Object key) {
        Object obj = updaters.get(key);
        if (obj != null && obj instanceof AbstractUpdater) return (AbstractUpdater) obj;
        return null;
    }

    public void setActiveUpdater(Object key) {
        Object obj = updaters.get(key);
        if (obj != null && obj instanceof AbstractUpdater) {
            AbstractUpdater inactive = (AbstractUpdater) obj;
            if (active == null) {
                active = inactive;
                XMLUtils.setActiveDataProvider(active.getName());
            } else {
                String activeName = active.getName();
                String inactiveName = inactive.getName();
                if (!activeName.equals(inactiveName)) {
                    fireUpdaterChange(inactive);
                }
            }
        }
    }
    public AbstractUpdater getActiveUpdater() { return active; }
    public String getActiveUpdaterName() { return active != null ? active.getName() : null; }

    public Vector getUpdaters() {
        Vector v = new Vector();
        Iterator it = updaters.keySet().iterator();
        while (it.hasNext()) v.add(it.next());
        Collections.sort(v);
        return v;
    }

    public void update(String symbol) {
        Dataset dataset;
        if (active != null) {
            for (String time : DatasetManager.LIST) {
                dataset = active.update(symbol, time);
                if (dataset != null) DatasetManager.getDefault().addDataset(DatasetManager.getName(symbol, time), dataset);
            }
        }
        setUpdate(true);
    }

    public void update(String[] symbols) {
        Dataset dataset;
        if (active != null) {
            for (String symbol : symbols) {
                for (String time : DatasetManager.LIST) {
                    dataset = active.update(symbol, time);
                    if (dataset != null) DatasetManager.getDefault().addDataset(DatasetManager.getName(symbol, time), dataset);
                }
            }
        }
        setUpdate(true);
    }

    public void update(Hashtable symbols) {
        Dataset dataset;
        if (active != null) {
            Iterator it = symbols.keySet().iterator();
            while (it.hasNext()) {
                String symbol = (String) it.next();
                String time = (String) symbols.get(symbol);
                if (!time.contains("Min")) {
                    for (String t : DatasetManager.LIST) {
                        dataset = active.update(symbol, t);
                        if (dataset != null) DatasetManager.getDefault().addDataset(DatasetManager.getName(symbol, t), dataset);
                    }
                } else {
                    for (String t : DatasetManager.LIST) {
                        dataset = active.update(symbol, t);
                        if (dataset != null) DatasetManager.getDefault().addDataset(DatasetManager.getName(symbol, t), dataset);
                    }
                    dataset = active.updateIntraDay(symbol, time);
                    if (dataset != null) DatasetManager.getDefault().addDataset(DatasetManager.getName(symbol, time), dataset);
                }
            }
        }
        setUpdate(true);
    }

    public void update(String symbol, String time) {
        Dataset dataset;
        if (active != null) {
            dataset = (!time.contains("Min")) ? active.update(symbol, time) : active.updateIntraDay(symbol, time);
            if (dataset != null) DatasetManager.getDefault().addDataset(DatasetManager.getName(symbol, time), dataset);
        }
        setUpdate(true);
    }

    public void updateIntraDay(String symbol, String time) {
        Dataset dataset;
        if (active != null) {
            dataset = active.updateIntraDay(symbol, time);
            if (dataset != null) DatasetManager.getDefault().addDataset(DatasetManager.getName(symbol, time), dataset);
        }
        setUpdate(true);
    }

    public void setUpdate(boolean b) { update = b; }
    public boolean isUpdated() { return update; }

    public void print() {
        Iterator it = updaters.keySet().iterator();
        while (it.hasNext()) System.out.println(it.next().toString());
    }

    public void fireUpdaterChange(AbstractUpdater inactive) {
        ChartFrameManager.getDefault().saveAll(); // save settings
        ChartFrameManager.getDefault().closeAll(); // close all chart frames
        DatasetManager.getDefault().removeAll(); // remove datasets
        active = inactive;
        XMLUtils.setActiveDataProvider(active.getName());
        RestoreSettings.newInstance().restore();
    }

}
