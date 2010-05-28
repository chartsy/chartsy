package org.chartsy.main.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import org.chartsy.main.chart.Overlay;
import org.openide.util.Lookup;

/**
 *
 * @author viorel.gheba
 */
public class OverlayManager {

    private static OverlayManager instance;
    private LinkedHashMap<String, Overlay> overlays;

    public static OverlayManager getDefault() {
        if (instance == null) instance = new OverlayManager();
        return instance;
    }

    private OverlayManager() {
        overlays = new LinkedHashMap<String, Overlay>();
        Collection<? extends Overlay> list = Lookup.getDefault().lookupAll(Overlay.class);
        for (Overlay o : list) {
            overlays.put(o.getName(), o);
        }
        sort();
    }

    private void sort()
    {
        List<String> mapKeys = new ArrayList<String>(overlays.keySet());
        Collections.sort(mapKeys);

        LinkedHashMap<String, Overlay> someMap = new LinkedHashMap<String, Overlay>();
        for (int i = 0; i < mapKeys.size(); i++)
            someMap.put(mapKeys.get(i), overlays.get(mapKeys.get(i)));
        overlays = someMap;
    }

    public Overlay getOverlay(String key) {
        return overlays.get(key);
    }

    public List<Overlay> getOverlaysList() {
        List<Overlay> list = new ArrayList<Overlay>();
        Iterator<String> it = overlays.keySet().iterator();
        while (it.hasNext()) {
            list.add(overlays.get(it.next()));
        }
        return list;
    }

    public List<String> getOverlays()
    {
        List<String> list =  new ArrayList<String>(overlays.keySet());
        Collections.sort(list);
        return list;
    }

}
