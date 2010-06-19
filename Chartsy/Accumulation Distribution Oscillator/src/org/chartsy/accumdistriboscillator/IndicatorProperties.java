/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.chartsy.accumdistriboscillator;

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

    public static final String LABEL = "Accumulation/Distribution Oscillator";
    public static final boolean MARKER = true;
    public static final Color COLOR = new Color(0x4e9a06);
    public static int STROKE_INDEX = 0;
    public static int DEFAULT_FAST_PERIOD = 3;
    public static int DEFAULT_SLOW_PERIOD = 10;

    private String label = LABEL;
    private boolean marker = MARKER;
    private Color color = COLOR;
    private int strokeIndex = STROKE_INDEX;

    private int fastPeriod = DEFAULT_FAST_PERIOD;//standard default according to Chaikin
    private int slowPeriod = DEFAULT_SLOW_PERIOD;//standard default according to Chaikin

    public IndicatorProperties() {}

    public int getFastPeriod() {return fastPeriod; }
    public void setFastPeriod(int fastPeriod) {this.fastPeriod = fastPeriod; }

    public int getSlowPeriod() {return slowPeriod; }
    public void setSlowPeriod(int slowPeriod) {this.slowPeriod = slowPeriod; }

    public String getLabel() { return label; }
    public void setLabel(String s) { label = s; }

    public boolean getMarker() { return marker; }
    public void setMarker(boolean b) { marker = b; }

    public Color getColor() { return color; }
    public void setColor(Color c) { color = c; }

    public int getStrokeIndex() { return strokeIndex; }
    public void setStrokeIndex(int i) { strokeIndex = i; }
    public Stroke getStroke() { return StrokeGenerator.getStroke(strokeIndex); }
    public void setStroke(Stroke s) { strokeIndex = StrokeGenerator.getStrokeIndex(s);

    }
}
