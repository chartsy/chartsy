/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.chartsy.apo;

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

    public static final String LABEL = "Absolute Price Oscillator";
    public static final boolean MARKER = true;
    public static final Color COLOR = new Color(0x4e9a06);
    public static int STROKE_INDEX = 0;
    public static final Color ZEROLINE_COLOR = new Color(0xbbbbbb);
    public static final int FAST_PERIOD = 12;//default
    public static final int SLOW_PERIOD = 26;//default

    private String label = LABEL;
    private boolean marker = MARKER;
    private Color color = COLOR;
    private int strokeIndex = STROKE_INDEX;
    private Color zeroLineColor = ZEROLINE_COLOR;
    private int fastPeriod = FAST_PERIOD;
    private int slowPeriod = SLOW_PERIOD;

    public IndicatorProperties() {}

    public String getLabel() { return label; }
    public void setLabel(String s) { label = s; }

    public int getFastPeriod() {return fastPeriod; }
    public void setFastPeriod(int fastPeriod) {this.fastPeriod = fastPeriod; }

    public int getSlowPeriod() {return slowPeriod; }
    public void setSlowPeriod(int slowPeriod) {this.slowPeriod = slowPeriod; }
    
    public boolean getMarker() { return marker; }
    public void setMarker(boolean b) { marker = b; }

    public Color getColor() { return color; }
    public void setColor(Color c) { color = c; }

    public int getStrokeIndex() { return strokeIndex; }
    public void setStrokeIndex(int i) { strokeIndex = i; }
    public Stroke getStroke() { return StrokeGenerator.getStroke(strokeIndex); }
    public void setStroke(Stroke s) { strokeIndex = StrokeGenerator.getStrokeIndex(s); }

    public Color getZeroLineColor() { return zeroLineColor; }
    public void setZeroLineColor(Color zeroLineColor) { this.zeroLineColor = zeroLineColor; }

}