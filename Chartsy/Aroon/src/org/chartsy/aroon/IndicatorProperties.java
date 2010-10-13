/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.chartsy.aroon;

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

    public static String LABEL = "Aroon";
    public static boolean MARKER = true;
    public static int PERIOD = 10;//standard Aroon period value mostly
    public static final Color UP_TREND_LINE_COLOR = new Color(0x5c3566);
    public static int UP_TREND_STROKE_INDEX = 0;
    public static final Color DOWN_TREND_LINE_COLOR = new Color(0x4e9a06);
    public static int DOWN_TREND_STROKE_INDEX = 0;
    public static final Color DELIMITER_COLOR = new Color(0xbbbbbb);
    public static int DELIMITER_STROKE_INDEX = 1;


    private String label = LABEL;
    private boolean marker = MARKER;
    private int period = PERIOD;
    private int upTrendStrokeIndex = UP_TREND_STROKE_INDEX;
    private int downTrendStrokeIndex = DOWN_TREND_STROKE_INDEX;
    private int delimiterStrokeIndex = DELIMITER_STROKE_INDEX;
    private Color downTrendColor = DOWN_TREND_LINE_COLOR;
    private Color upTrendColor = UP_TREND_LINE_COLOR;
    private Color delimiterColor = DELIMITER_COLOR;

    public IndicatorProperties() {}

    public String getLabel() { return label; }
    public void setLabel(String s) { label = s; }

    public boolean getMarker() { return marker; }
    public void setMarker(boolean b) { marker = b; }

    public int getPeriod() {return period; }
    public void setPeriod(int period) {this.period = period; }

    public int getDownTrendStrokeIndex() {return downTrendStrokeIndex; }
    public void setDownTrendStrokeIndex(int downTrendStrokeIndex) { this.downTrendStrokeIndex = downTrendStrokeIndex; }
    public Stroke getDownTrendLineStroke() { return StrokeGenerator.getStroke(downTrendStrokeIndex); }
    public void setDownTrendLineStroke(Stroke s) { downTrendStrokeIndex = StrokeGenerator.getStrokeIndex(s); }

    public int getUpTrendStrokeIndex() {return upTrendStrokeIndex; }
    public void setUpTrendStrokeIndex(int upTrendStrokeIndex) {this.upTrendStrokeIndex = upTrendStrokeIndex; }
    public Stroke getUpTrendLineStroke() { return StrokeGenerator.getStroke(upTrendStrokeIndex); }
    public void setUpTrendLineStroke(Stroke s) { upTrendStrokeIndex = StrokeGenerator.getStrokeIndex(s); }

    public int getDelimiterStrokeIndex() {return delimiterStrokeIndex; }
    public void setDelimiterStrokeIndex(int delimiterStrokeIndex) {this.delimiterStrokeIndex = delimiterStrokeIndex; }
    public Stroke getDelimiterLineStroke() { return StrokeGenerator.getStroke(delimiterStrokeIndex); }
    public void setDelimiterLineStroke(Stroke s) { delimiterStrokeIndex = StrokeGenerator.getStrokeIndex(s); }

    public Color getDownTrendColor() {return downTrendColor; }
    public void setDownTrendColor(Color downTrendColor) {this.downTrendColor = downTrendColor; }

    public Color getUpTrendColor() {return upTrendColor; }
    public void setUpTrendColor(Color upTrendColor) {this.upTrendColor = upTrendColor; }

    public Color getDelimiterColor() {return delimiterColor; }
    public void setDelimiterColor(Color delimiterColor) {this.delimiterColor = delimiterColor; }    

}
