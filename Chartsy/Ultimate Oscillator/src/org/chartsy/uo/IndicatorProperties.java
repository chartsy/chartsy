package org.chartsy.uo;

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

    public static final int FAST = 7;
    public static final int INTERMEDIATE = 14;
    public static final int SLOW = 28;
    public static final String LABEL = "Ultimate Oscillator";
    public static final boolean MARKER = true;
    public static final Color COLOR = new Color(0x4e9a06);
    public static final int STROKE_INDEX = 0;
    public static final Color DELIMITER_COLOR = new Color(0xbbbbbb);
    public static int DELIMITER_STROKE_INDEX = 1;

    private int fast = FAST;
    private int intermediate = INTERMEDIATE;
    private int slow = SLOW;
    private String label = LABEL;
    private boolean marker = MARKER;
    private Color color = COLOR;
    private int strokeIndex = STROKE_INDEX;
    private Color delimiterColor = DELIMITER_COLOR;
    private int delimiterStrokeIndex = DELIMITER_STROKE_INDEX;

    public IndicatorProperties() {}

    public int getFast() { return fast; }
    public void setFast(int i) { fast = i; }

    public int getIntermediate() { return intermediate; }
    public void setIntermediate(int i) { intermediate = i; }

    public int getSlow() { return slow; }
    public void setSlow(int i) { slow = i; }

    public String getLabel() { return label; }
    public void setLabel(String s) { label = s; }

    public boolean getMarker() { return marker; }
    public void setMarker(boolean b) { marker = b; }

    public Color getColor() { return color; }
    public void setColor(Color c) { color = c; }

    public int getStrokeIndex() { return strokeIndex; }
    public void setStrokeIndex(int i) { strokeIndex = i; }
    public Stroke getStroke() { return StrokeGenerator.getStroke(strokeIndex); }
    public void setStroke(Stroke s) { strokeIndex = StrokeGenerator.getStrokeIndex(s); }

    public Color getDelimiterColor() { return delimiterColor; }
    public void setDelimiterColor(Color delimiterColor) { this.delimiterColor = delimiterColor; }

    public int getDelimiterStrokeIndex() {return delimiterStrokeIndex; }
    public void setDelimiterStrokeIndex(int delimiterStrokeIndex) {this.delimiterStrokeIndex = delimiterStrokeIndex; }
    public Stroke getDelimiterLineStroke() { return StrokeGenerator.getStroke(delimiterStrokeIndex); }
    public void setDelimiterLineStroke(Stroke s) { delimiterStrokeIndex = StrokeGenerator.getStrokeIndex(s); }

}
