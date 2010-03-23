package org.chartsy.moneyflow;

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

    private static final long serialVersionUID = 101L;

    public static final String LABEL = "Money Flow";
    public static final boolean MARKER = true;
    public static final Color MFH_COLOR = new Color(0x4e9a06);
    public static final Color MFL_COLOR = new Color(0xef2929);
    public static final Color ZERO_LINE_COLOR = new Color(0xeeeeec);
    public static final int ZERO_LINE_STROKE = 0;
    public static final boolean ZERO_LINE_VISIBILITY = true;

    private String label = LABEL;
    private boolean marker = MARKER;
    private Color mfhColor = MFH_COLOR;
    private Color mflColor = MFL_COLOR;
    private Color zeroLineColor = ZERO_LINE_COLOR;
    private int zeroLineStrokeIndex = ZERO_LINE_STROKE;
    private boolean zeroLineVisibility = ZERO_LINE_VISIBILITY;

    public IndicatorProperties() {}

    public String getLabel() { return label; }
    public void setLabel(String s) { label = s; }

    public boolean getMarker() { return marker; }
    public void setMarker(boolean b) { marker = b; }

    public Color getMFHColor() { return mfhColor; }
    public void setMFHColor(Color c) { mfhColor = c; }

    public Color getMFLColor() { return mflColor; }
    public void setMFLColor(Color c) { mflColor = c; }

    public Color getZeroLineColor() { return zeroLineColor; }
    public void setZeroLineColor(Color c) { zeroLineColor = c; }

    public int getZeroLineStrokeIndex() { return zeroLineStrokeIndex; }
    public void setZeroLineStrokeIndex(int i) { zeroLineStrokeIndex = i; }
    public Stroke getZeroLineStroke() { return StrokeGenerator.getStroke(zeroLineStrokeIndex); }
    public void setZeroLineStroke(Stroke s) { zeroLineStrokeIndex = StrokeGenerator.getStrokeIndex(s); }

    public boolean getZeroLineVisibility() { return zeroLineVisibility; }
    public void setZeroLineVisibility(boolean b) { zeroLineVisibility = b; }

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
