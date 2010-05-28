package org.chartsy.main.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import org.chartsy.main.ChartFrame;
import org.chartsy.main.chart.Annotation;
import org.openide.util.Lookup;

/**
 *
 * @author viorel.gheba
 */
public class AnnotationManager {

    private static AnnotationManager instance;
    private LinkedHashMap<String, Annotation> annotations;
    private Annotation current;

    public static AnnotationManager getDefault()
    {
        if (instance == null)
            instance = new AnnotationManager();
        return instance;
    }

    private AnnotationManager()
    {
        annotations = new LinkedHashMap<String, Annotation>();
        Collection<? extends Annotation> list = Lookup.getDefault().lookupAll(Annotation.class);
        for (Annotation a : list)
            annotations.put(a.getName(), a);
        
        annotations = sort(annotations);
    }

    private LinkedHashMap<String, Annotation> sort(LinkedHashMap<String, Annotation> oldMap)
    {
        List<String> mapKeys = new ArrayList<String>(oldMap.keySet());
        Collections.sort(mapKeys);

        LinkedHashMap<String, Annotation> newMap = new LinkedHashMap<String, Annotation>();
        for (int i = 0; i < mapKeys.size(); i++)
            newMap.put(mapKeys.get(i), oldMap.get(mapKeys.get(i)));
        
        return newMap;
    }

    public Annotation getAnnotation(String key)
    { return annotations.get(key); }

    public List<String> getAnnotations()
    {
        List<String> list = new ArrayList<String>(annotations.keySet());
        Collections.sort(list);
        return list;
    }

    public void setNewAnnotation(Annotation a)
    { current = a; }

    public boolean hasNew()
    { return current != null; }

    public Annotation getNewAnnotation(ChartFrame frame)
    { return current.newInstance(frame); }

    public void clearNewAnnotation()
    { current = null; }

    /*public void writeAnnotations(LinkedHashMap<String, Annotation> map)
    {
        try
        {
            OutputStream outFile = new FileOutputStream(path);
            OutputStream outBuffer = new BufferedOutputStream(outFile);
            ObjectOutput OUT = new ObjectOutputStream(outBuffer);

            try
            {
                if (map == null)
                {
                    OUT.writeInt(0);
                    return;
                }

                int size = map.size();
                OUT.writeInt(size);

                if (size > 0)
                {
                    ArrayList<String> keys = new ArrayList<String>(map.keySet());
                    ArrayList<Annotation> values = new ArrayList<Annotation>(map.values());

                    for (int i = 0; i < size; i++)
                    {
                        OUT.writeObject(keys.get(i));
                        OUT.writeObject(values.get(i));
                    }
                }
            }
            finally
            {
                OUT.close();
            }
        }
        catch (IOException ex)
        {
            LOG.log(Level.SEVERE, "Cannot perform output.", ex);
        }
    }

    public LinkedHashMap<String, Annotation> readAnnotations()
    {
        LinkedHashMap<String, Annotation> map = new LinkedHashMap<String, Annotation>();
        try
        {
            InputStream inFile = new FileInputStream(path);
            InputStream inBuffer = new BufferedInputStream(inFile);
            ObjectInput IN = new ObjectInputStream(inBuffer);

            try
            {
                int size = IN.readInt();
                if (size > 0)
                {
                    for (int i = 0; i < size; i++)
                    {
                        String key = (String) IN.readObject();
                        Annotation value = (Annotation) IN.readObject();
                        map.put(key, value);
                    }
                }
            }
            finally
            {
                IN.close();
            }
        }
        catch(ClassNotFoundException ex)
        {
            LOG.log(Level.SEVERE, "Cannot perform input. Class not found.", ex);
        }
        catch(IOException ex)
        {
            LOG.log(Level.SEVERE, "Cannot perform input.", ex);
        }
        return map;
    }*/

}
