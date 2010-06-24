/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.chartsy.cmo;

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

    public static final String LABEL = "CMO";
    public static final boolean MARKER = true;
    public static final Color COLOR = new Color(0x4e9a06);
    public static int STROKE_INDEX = 0;
    public static int DEFAULT_PERIOD = 20;//default value
    public static final Color INSIDE_COLOR = new Color(0x4e9a06);
    public static final int INSIDE_ALPHA = 25;
    public static final boolean INSIDE_VISIBILITY = true;
    public static final Color INSIDE_HIGH_COLOR = new Color(0xFF0000);//red
    public static final Color INSIDE_LOW_COLOR = new Color(0x006600);//green

    private String label = LABEL;
    private boolean marker = MARKER;
    private Color color = COLOR;
    private int strokeIndex = STROKE_INDEX;
    private Color insideColor = INSIDE_COLOR;
    private Color insideHighColor = INSIDE_HIGH_COLOR;
    private Color insideLowColor = INSIDE_LOW_COLOR;
    private int insideAlpha = INSIDE_ALPHA;
    private boolean insideVisibility = INSIDE_VISIBILITY;
    
    private int period = DEFAULT_PERIOD;

    public IndicatorProperties() {}

    public int getPeriod() {return period; }
    public void setPeriod(int period) {this.period = period; }

    public String getLabel() { return label; }
    public void setLabel(String s) { label = s; }

    public boolean getMarker() { return marker; }
    public void setMarker(boolean b) { marker = b; }

    public Color getColor() { return color; }
    public void setColor(Color c) { color = c; }

    public Color getInsideHighColor() {return insideHighColor; }
    public void setInsideHighColor(Color insideHighColor) {this.insideHighColor = insideHighColor; }
    public Color getInsideTransparentHighColor() { return new Color(insideColor.getRed(), 0, 0, insideAlpha); }

    public Color getInsideLowColor() {return insideLowColor; }
    public void setInsideLowColor(Color insideLowColor) {this.insideLowColor = insideLowColor; }
    public Color getInsideTransparentLowColor() { return new Color(0, insideColor.getGreen(), 0, insideAlpha); }

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

}
