package org.chartsy.annotation.rectangle;

import java.awt.Color;
import java.awt.Stroke;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.chartsy.main.utils.StrokeGenerator;

/**
 *
 * @author viorel.gheba
 */
public class AnnotationProperties implements Serializable {

    private static final long serialVersionUID = 101L;

    public static final Color COLOR = Color.RED;
    public static final int STROKE_INDEX = 0;
    public static final int INSIDE_ALPHA = 25;
    public static final boolean INSIDE_VISIBILITY = true;

    private Color color = COLOR;
    private int strokeIndex = STROKE_INDEX;
    private int insideAlpha = INSIDE_ALPHA;
    private boolean insideVisibility = INSIDE_VISIBILITY;

    private List listeners = Collections.synchronizedList(new LinkedList());

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        listeners.add(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        listeners.remove(pcl);
    }

    private void fire(String propertyName, Object old, Object nue) {
        PropertyChangeListener[] pcls = (PropertyChangeListener[]) listeners.toArray(new PropertyChangeListener[0]);
        for (int i = 0; i < pcls.length; i++) {
            pcls[i].propertyChange(new PropertyChangeEvent(this, propertyName, old, nue));
        }
    }

    public AnnotationProperties() {}

    public Color getColor() { return color; }
    public void setColor(Color c) { color = c; }

    public int getStrokeIndex() { return strokeIndex; }
    public void setStrokeIndex(int i) { strokeIndex = i; }
    public Stroke getStroke() { return StrokeGenerator.getStroke(strokeIndex); }
    public void setStroke(Stroke s) { strokeIndex = StrokeGenerator.getStrokeIndex(s); }

    public int getInsideAlpha() { return insideAlpha; }
    public void setInsideAlpha(int i) { insideAlpha = i; }
    public Color getFillColor() { return new Color(color.getRed(), color.getGreen(), color.getBlue(), insideAlpha); }

    public boolean getInsideVisibility() { return insideVisibility; }
    public void setInsideVisibility(boolean b) { insideVisibility = b; }

}
