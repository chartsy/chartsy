package org.chartsy.main.managers;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.Vector;
import org.chartsy.main.chartsy.ChartFrame;
import org.chartsy.main.chartsy.chart.AbstractAnnotation;
import org.chartsy.main.chartsy.chart.Annotation;

/**
 *
 * @author viorel.gheba
 */
public class AnnotationManager {

    protected static AnnotationManager instance;
    protected Hashtable<Object, Object> annotations;
    protected String newAnnotationName = "";

    public static AnnotationManager getDefault() {
        if (instance == null) instance = new AnnotationManager();
        return instance;
    }

    protected AnnotationManager() {}

    public void initialize() {
        annotations = new Hashtable<Object, Object>();
        ServiceLoader<AbstractAnnotation> service = ServiceLoader.load(AbstractAnnotation.class);
        Iterator<AbstractAnnotation> it = service.iterator();
        while (it.hasNext()) {
            AbstractAnnotation aa = it.next();
            addAnnotation(aa.getName(), aa);
        }
    }

    public String getNewAnnotationName() { return newAnnotationName; }
    public void setNewAnnotationName(String s) { newAnnotationName = s; }

    public void addAnnotation(Object key, Object value) { annotations.put(key, value); }
    public void removeAnnotation(Object key) { annotations.remove(key); }

    public AbstractAnnotation getAbstractAnnotation(Object key) {
        Object obj = annotations.get(key);
        if (obj != null && obj instanceof AbstractAnnotation) return (AbstractAnnotation) obj;
        return null;
    }

    public Vector getAnnotations() {
        Vector v = new Vector();
        Iterator it = annotations.keySet().iterator();
        while (it.hasNext()) v.add(it.next());
        Collections.sort(v);
        return v;
    }

    public Annotation getNewAnnotation(ChartFrame cf) {
        if (!newAnnotationName.isEmpty()) {
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
