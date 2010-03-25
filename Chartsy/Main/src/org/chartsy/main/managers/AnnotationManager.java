package org.chartsy.main.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;
import org.chartsy.main.chartsy.ChartFrame;
import org.chartsy.main.chartsy.chart.AbstractAnnotation;
import org.chartsy.main.chartsy.chart.Annotation;
import org.openide.util.Lookup;

/**
 *
 * @author viorel.gheba
 */
public class AnnotationManager {

    protected static AnnotationManager instance;
    protected LinkedHashMap<String, AbstractAnnotation> annotations;
    protected String newAnnotationName = "";

    public static AnnotationManager getDefault() {
        if (instance == null) instance = new AnnotationManager();
        return instance;
    }

    protected AnnotationManager() {}

    public void initialize() {
        annotations = new LinkedHashMap<String, AbstractAnnotation>();
        Collection<? extends AbstractAnnotation> list = Lookup.getDefault().lookupAll(AbstractAnnotation.class);
        for (AbstractAnnotation aa : list) {
            addAnnotation(aa.getName(), aa);
        }
        sort();
    }

    protected void sort() {
        List<String> mapKeys = new ArrayList<String>(annotations.keySet());
        Collections.sort(mapKeys);

        LinkedHashMap<String, AbstractAnnotation> someMap = new LinkedHashMap<String, AbstractAnnotation>();
        for (int i = 0; i < mapKeys.size(); i++)
            someMap.put(mapKeys.get(i), annotations.get(mapKeys.get(i)));
        annotations = someMap;
    }

    public String getNewAnnotationName() { return newAnnotationName; }
    public void setNewAnnotationName(String s) { newAnnotationName = s; }

    public void addAnnotation(String key, AbstractAnnotation value) { annotations.put(key, value); }
    public void removeAnnotation(String key) { annotations.remove(key); }

    public AbstractAnnotation getAbstractAnnotation(String key) {
        return annotations.get(key);
    }

    public Vector getAnnotations() {
        Vector v = new Vector();
        Iterator it = annotations.keySet().iterator();
        while (it.hasNext()) v.add(it.next());
        Collections.sort(v);
        return v;
    }

    public Annotation getNewAnnotation(ChartFrame cf) {
        if (!newAnnotationName.equals("")) {
            Object obj = annotations.get(newAnnotationName);
            if (obj != null && obj instanceof AbstractAnnotation) return ((AbstractAnnotation) obj).newInstance(cf);
        }
        return null;
    }

    public void print() {
        Iterator it = annotations.keySet().iterator();
        while (it.hasNext()) LoggerManager.getDefault().log(it.next().toString());
    }

}
