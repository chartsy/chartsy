package org.chartsy.pricezoneoscillator;

import java.awt.Color;
import java.awt.Stroke;
import org.chartsy.main.chart.AbstractPropertyListener;
import org.chartsy.main.utils.SerialVersion;
import org.chartsy.main.utils.StrokeGenerator;

/**
 *
 * @author Viorel
 */
public class IndicatorProperties extends AbstractPropertyListener
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public static final String LABEL = "PZO";
    public static final int PERIOD = 14;
    public static final boolean MARKER = true;
    public static final Color COLOR = new Color(0x0251F1);
    public static final int STROKE_INDEX = 0;

    private String label = LABEL;
    private int period = PERIOD;
    private boolean marker = MARKER;
    private Color color = COLOR;
    private int strokeIndex = STROKE_INDEX;

    public IndicatorProperties() {}

    public String getLabel() { return label; }
    public void setLabel(String s) { label = s; }

    public int getPeriod() { return period; }
    public void setPeriod(int i) { period = i; }

    public boolean getMarker() { return marker; }
    public void setMarker(boolean b) { marker = b; }

    public Color getColor() { return color; }
    public void setColor(Color c) { color = c; }

    public int getStrokeIndex() { return strokeIndex; }
    public void setStrokeIndex(int i) { strokeIndex = i; }
    public Stroke getStroke() { return StrokeGenerator.getStroke(strokeIndex); }
    public void setStroke(Stroke s) { strokeIndex = StrokeGenerator.getStrokeIndex(s); }

}
