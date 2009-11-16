package org.chartsy.main.managers;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
//import java.util.ServiceLoader;
import org.chartsy.main.chartsy.chart.AbstractOverlay;
import org.openide.util.Lookup;

/**
 *
 * @author viorel.gheba
 */
public class OverlayManager {

    protected static OverlayManager instance;
    protected Hashtable<Object, Object> overlays;

    public static OverlayManager getDefault() {
        if (instance == null) instance = new OverlayManager();
        return instance;
    }

    protected OverlayManager() {}

    public void initialize() {
        overlays = new Hashtable<Object, Object>();
        Collection<? extends AbstractOverlay> list = Lookup.getDefault().lookupAll(AbstractOverlay.class);
        for (AbstractOverlay ao : list) {
            addOverlay(ao.getName(), ao);
        }
        /*ServiceLoader<AbstractOverlay> service = ServiceLoader.load(AbstractOverlay.class);
        Iterator<AbstractOverlay> it = service.iterator();
        while (it.hasNext()) {
            AbstractOverlay ao = it.next();
            addOverlay(ao.getName(), ao);
        }*/
    }

    public void addOverlay(Object key, Object value) { overlays.put(key, value); }
    public void removeOverlay(Object key) { overlays.remove(key); }

    public AbstractOverlay[] getOverlays() {
        AbstractOverlay[] list = new AbstractOverlay[overlays.size()];
        int i = 0;
        Iterator it = overlays.keySet().iterator();
        while (it.hasNext()) {
            Object obj = overlays.get(it.next());
            if (obj != null && obj instanceof AbstractOverlay) {
                list[i] = (AbstractOverlay) obj;
                i++;
            }
        }
        return list;
    }

    public AbstractOverlay getOverlay(Object key) {
        Object obj = overlays.get(key);
        if (obj != null && obj instanceof AbstractOverlay) return (AbstractOverlay) obj;
        return null;
    }

    public void print() {
        Iterator it = overlays.keySet().iterator();
        while (it.hasNext()) System.out.println(it.next().toString());
    }

}
