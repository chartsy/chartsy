/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.chartsy.ppo;

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

    public static final int FAST = 12;
    public static final int SLOW = 26;
    public static final int SMOOTH = 9;
    public static final String LABEL = "PPO";
    public static final boolean MARKER = true;
    public static final Color COLOR = new Color(0x4e9a06);
    public static final Color DELIMITER_COLOR = new Color(0xbbbbbb);
    public static int STROKE_INDEX = 0;
    public static Color ZERO_LINE_COLOR = new Color(0xbbbbbb);
    public static int ZERO_LINE_STROKE_INDEX = 0;
    public static boolean ZERO_LINE_VISIBILITY = true;

    private int fastPeriod = FAST;
    private int slowPeriod = SLOW;
    private int smoothPeriod = SMOOTH;
    private String label = LABEL;
    private boolean marker = MARKER;
    private Color color = COLOR;
    private Color delimiterColor = DELIMITER_COLOR;
    private int strokeIndex = STROKE_INDEX;
    private Color zeroLineColor = ZERO_LINE_COLOR;
    private int zeroLineStrokeIndex = ZERO_LINE_STROKE_INDEX;
    private boolean zeroLineVisibility = ZERO_LINE_VISIBILITY;

    public IndicatorProperties() {}

    public int getFastPeriod() {return fastPeriod; }
    public void setFastPeriod(int fastPeriod) {this.fastPeriod = fastPeriod; }

    public int getSlowPeriod() {return slowPeriod; }
    public void setSlowPeriod(int slowPeriod) {this.slowPeriod = slowPeriod; }

    public int getSmoothPeriod() {return smoothPeriod; }
    public void setSmoothPeriod(int smoothPeriod) {this.smoothPeriod = smoothPeriod; }

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

    public Color getDelimiterColor() {return delimiterColor; }
    public void setDelimiterColor(Color delimiterColor) {this.delimiterColor = delimiterColor; }

    public Color getZeroLineColor() { return zeroLineColor; }
    public void setZeroLineColor(Color c) { zeroLineColor = c; }

    public int getZeroLineStrokeIndex() { return zeroLineStrokeIndex; }
    public void setZeroLineStrokeIndex(int i) { zeroLineStrokeIndex = i; }
    public Stroke getZeroLineStroke() { return StrokeGenerator.getStroke(zeroLineStrokeIndex); }
    public void setZeroLineStroke(Stroke s) { zeroLineStrokeIndex = StrokeGenerator.getStrokeIndex(s); }

    public boolean getZeroLineVisibility() { return zeroLineVisibility; }
    public void setZeroLineVisibility(boolean b) { zeroLineVisibility = b; }
}
