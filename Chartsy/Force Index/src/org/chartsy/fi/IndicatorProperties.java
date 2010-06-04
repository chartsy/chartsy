package org.chartsy.fi;

import java.awt.Color;
import java.awt.Stroke;
import org.chartsy.main.chart.AbstractPropertyListener;
import org.chartsy.main.utils.SerialVersion;
import org.chartsy.main.utils.StrokeGenerator;

/**
 *
 * @author viorel.gheba
 */
public class IndicatorProperties 
        extends AbstractPropertyListener
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public static final int PERIOD1 = 13;
    public static final int PERIOD2 = 3;
    public static final String LABEL = "FI";
    public static final boolean MARKER = true;
    public static final Color COLOR1 = new Color(0x4e9a06);
    public static final Color COLOR2 = new Color(0x5c3566);
    public static int STROKE_INDEX1 = 0;
    public static int STROKE_INDEX2 = 0;

    private int period1 = PERIOD1;
    private int period2 = PERIOD2;
    private String label = LABEL;
    private boolean marker = MARKER;
    private Color color1 = COLOR1;
    private Color color2 = COLOR2;
    private int strokeIndex1 = STROKE_INDEX1;
    private int strokeIndex2 = STROKE_INDEX2;

    public IndicatorProperties() {}

    public int getPeriod1() { return period1; }
    public void setPeriod1(int i) { period1 = i; }

    public int getPeriod2() { return period2; }
    public void setPeriod2(int i) { period2 = i; }

    public String getLabel() { return label; }
    public void setLabel(String s) { label = s; }

    public boolean getMarker() { return marker; }
    public void setMarker(boolean b) { marker = b; }

    public Color getColor1() { return color1; }
    public void setColor1(Color c) { color1 = c; }

    public Color getColor2() { return color2; }
    public void setColor2(Color c) { color2 = c; }

    public int getStrokeIndex1() { return strokeIndex1; }
    public void setStrokeIndex1(int i) { strokeIndex1 = i; }
    public Stroke getStroke1() { return StrokeGenerator.getStroke(strokeIndex1); }
    public void setStroke1(Stroke s) { strokeIndex1 = StrokeGenerator.getStrokeIndex(s); }

    public int getStrokeIndex2() { return strokeIndex2; }
    public void setStrokeIndex2(int i) { strokeIndex2 = i; }
    public Stroke getStroke2() { return StrokeGenerator.getStroke(strokeIndex2); }
    public void setStroke2(Stroke s) { strokeIndex2 = StrokeGenerator.getStrokeIndex(s); }

}
