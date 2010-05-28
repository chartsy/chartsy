package org.chartsy.stochastics;

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
public class IndicatorProperties implements Serializable {

    private static final long serialVersionUID = 2L;

    public static final int PERIOD_K = 14;
    public static final int PERIOD_D = 3;
    public static final int SMOOTH = 3;
    public static final String LABEL = "Stochastics";
    public static boolean MARKER = true;
    public static boolean SF = true;
    public static Color COLOR_D = new Color(0x4e9a06);
    public static int STROKE_INDEX_D = 0;
    public static Color COLOR_K = new Color(0xf57900);
    public static int STROKE_INDEX_K = 0;

    private int periodK = PERIOD_K;
    private int periodD = PERIOD_D;
    private int smooth = SMOOTH;
    private String label = LABEL;
    private boolean marker = MARKER;
    private boolean sf = SF;
    private Color colorD = COLOR_D;
    private int strokeIndexD = STROKE_INDEX_D;
    private Color colorK = COLOR_K;
    private int strokeIndexK = STROKE_INDEX_K;

    public IndicatorProperties() {}

    public int getPeriodK() { return periodK; }
    public void setPeriodK(int i) { periodK = i; }

    public int getPeriodD() { return periodD; }
    public void setPeriodD(int i) { periodD = i; }

    public int getSmooth() { return smooth; }
    public void setSmooth(int i) { smooth = i; }

    public String getLabel() { return label; }
    public void setLabel(String s) { label = s; }

    public boolean getMarker() { return marker; }
    public void setMarker(boolean b) { marker = b; }

    public boolean getSF() { return sf; }
    public void setSF(boolean b) { sf = b; }

    public Color getColorD() { return colorD; }
    public void setColorD(Color c) { colorD = c; }

    public int getStrokeIndexD() { return strokeIndexD; }
    public void setStrokeIndexD(int i) { strokeIndexD = i; }
    public Stroke getStrokeD() { return StrokeGenerator.getStroke(strokeIndexD); }
    public void setStrokeD(Stroke s) { strokeIndexD = StrokeGenerator.getStrokeIndex(s); }

    public Color getColorK() { return colorK; }
    public void setColorK(Color c) { colorK = c; }

    public int getStrokeIndexK() { return strokeIndexK; }
    public void setStrokeIndexK(int i) { strokeIndexK = i; }
    public Stroke getStrokeK() { return StrokeGenerator.getStroke(strokeIndexK); }
    public void setStrokeK(Stroke s) { strokeIndexK = StrokeGenerator.getStrokeIndex(s); }

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

}
