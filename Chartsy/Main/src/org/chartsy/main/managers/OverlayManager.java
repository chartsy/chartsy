package org.chartsy.main.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import org.chartsy.main.chartsy.chart.AbstractOverlay;
import org.openide.util.Lookup;

/**
 *
 * @author viorel.gheba
 */
public class OverlayManager {

    protected static OverlayManager instance;
    protected LinkedHashMap<String, AbstractOverlay> overlays;

    public static OverlayManager getDefault() {
        if (instance == null) instance = new OverlayManager();
        return instance;
    }

    protected OverlayManager() {}

    public void initialize() {
        overlays = new LinkedHashMap<String, AbstractOverlay>();
        Collection<? extends AbstractOverlay> list = Lookup.getDefault().lookupAll(AbstractOverlay.class);
        for (AbstractOverlay ao : list) {
            addOverlay(ao.getName(), ao);
        }
        sort();
    }

    protected void sort() {
        List<String> mapKeys = new ArrayList<String>(overlays.keySet());
        Collections.sort(mapKeys);

        LinkedHashMap<String, AbstractOverlay> someMap = new LinkedHashMap<String, AbstractOverlay>();
        for (int i = 0; i < mapKeys.size(); i++)
            someMap.put(mapKeys.get(i), overlays.get(mapKeys.get(i)));
        overlays = someMap;
    }

    public void addOverlay(String key, AbstractOverlay value) { overlays.put(key, value); }
    public void removeOverlay(String key) { overlays.remove(key); }

    public AbstractOverlay[] getOverlays() {
        AbstractOverlay[] list = new AbstractOverlay[overlays.size()];
        int i = 0;
        Iterator it = overlays.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            AbstractOverlay value = overlays.get(key);
            if (value != null) {
                list[i] = value;
                i++;
            }
        }
        return list;
    }

    public AbstractOverlay getOverlay(String key) {
        return overlays.get(key);
    }

    public void print() {
        Iterator it = overlays.keySet().iterator();
        while (it.hasNext()) System.out.println(it.next().toString());
    }

}
