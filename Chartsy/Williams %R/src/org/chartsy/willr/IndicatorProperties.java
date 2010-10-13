/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.chartsy.willr;

import java.awt.Color;
import java.awt.Stroke;
import org.chartsy.main.chart.AbstractPropertyListener;
import org.chartsy.main.utils.SerialVersion;
import org.chartsy.main.utils.StrokeGenerator;

/**
 *
 * @author joshua.taylor
 */
public class IndicatorProperties extends AbstractPropertyListener
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public static final int PERIOD = 14;
    public static final String LABEL = "Williams %R";
    public static boolean MARKER = true;
    public static Color COLOR = new Color(0x4e9a06);
    public static int STROKE_INDEX = 0;
    public static final Color INSIDE_COLOR = new Color(0x4e9a06);
    public static final int INSIDE_ALPHA = 25;
    public static final boolean INSIDE_VISIBILITY = true;
    public static final Color DELIMITER_COLOR = new Color(0xbbbbbb);
    public static int DELIMITER_STROKE_INDEX = 1;

    private int period = PERIOD;
    private String label = LABEL;
    private boolean marker = MARKER;
    private Color color = COLOR;
    private int strokeIndex = STROKE_INDEX;
    private Color insideColor = INSIDE_COLOR;
    private int insideAlpha = INSIDE_ALPHA;
    private boolean insideVisibility = INSIDE_VISIBILITY;
    private Color delimiterColor = DELIMITER_COLOR;
    private int delimiterStrokeIndex = DELIMITER_STROKE_INDEX;
    
    public IndicatorProperties() {}

    public int getPeriod() { return period; }
    public void setPeriod(int i) { period = i; }

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

    public Color getInsideColor() { return insideColor; }
    public void setInsideColor(Color c) { insideColor = c; }
    public Color getInsideTransparentColor() { return new Color(insideColor.getRed(), insideColor.getGreen(), insideColor.getBlue(), insideAlpha); }

    public int getInsideAlpha() { return insideAlpha; }
    public void setInsideAlpha(int i) { insideAlpha = i; }

    public boolean getInsideVisibility() { return insideVisibility; }
    public void setInsideVisibility(boolean b) { insideVisibility = b; }

    public Color getDelimiterColor() { return delimiterColor; }
    public void setDelimiterColor(Color delimiterColor) { this.delimiterColor = delimiterColor; }

    public int getDelimiterStrokeIndex() {return delimiterStrokeIndex; }
    public void setDelimiterStrokeIndex(int delimiterStrokeIndex) {this.delimiterStrokeIndex = delimiterStrokeIndex; }
    public Stroke getDelimiterLineStroke() { return StrokeGenerator.getStroke(delimiterStrokeIndex); }
    public void setDelimiterLineStroke(Stroke s) { delimiterStrokeIndex = StrokeGenerator.getStrokeIndex(s); }


}

