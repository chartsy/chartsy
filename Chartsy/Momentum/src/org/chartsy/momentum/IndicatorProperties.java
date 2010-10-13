/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.chartsy.momentum;

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

    public static final String LABEL = "Momentum";
    public static final boolean MARKER = true;
    public static final Color COLOR = new Color(0x4e9a06);
    public static int STROKE_INDEX = 0;
    public static int DEFAULT_PERIOD = 10;
    public static Color ZERO_LINE_COLOR = new Color(0xbbbbbb);
    public static int ZERO_LINE_STROKE_INDEX = 1;
    public static boolean ZERO_LINE_VISIBILITY = true;

    private String label = LABEL;
    private boolean marker = MARKER;
    private Color color = COLOR;
    private int strokeIndex = STROKE_INDEX;
    private Color zeroLineColor = ZERO_LINE_COLOR;
    private int zeroLineStrokeIndex = ZERO_LINE_STROKE_INDEX;
    private boolean zeroLineVisibility = ZERO_LINE_VISIBILITY;


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

    public int getStrokeIndex() { return strokeIndex; }
    public void setStrokeIndex(int i) { strokeIndex = i; }
    public Stroke getStroke() { return StrokeGenerator.getStroke(strokeIndex); }
    public void setStroke(Stroke s) { strokeIndex = StrokeGenerator.getStrokeIndex(s); }

    public Color getZeroLineColor() { return zeroLineColor; }
    public void setZeroLineColor(Color c) { zeroLineColor = c; }

    public int getZeroLineStrokeIndex() { return zeroLineStrokeIndex; }
    public void setZeroLineStrokeIndex(int i) { zeroLineStrokeIndex = i; }
    public Stroke getZeroLineStroke() { return StrokeGenerator.getStroke(zeroLineStrokeIndex); }
    public void setZeroLineStroke(Stroke s) { zeroLineStrokeIndex = StrokeGenerator.getStrokeIndex(s); }

    public boolean getZeroLineVisibility() { return zeroLineVisibility; }
    public void setZeroLineVisibility(boolean b) { zeroLineVisibility = b; }
}
