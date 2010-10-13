/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.chartsy.rocr;

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

    public static final String LABEL = "Rate of Change - Ratio";
    public static final boolean MARKER = true;
    public static final Color COLOR = new Color(0x4e9a06);
    public static int STROKE_INDEX = 0;
    public static final Color DELIMITER_COLOR = new Color(0xbbbbbb);
    public static final int PERIOD = 10;//default
    public static int DELIMITER_STROKE_INDEX = 1;

    private String label = LABEL;
    private boolean marker = MARKER;
    private Color color = COLOR;
    private int strokeIndex = STROKE_INDEX;
    private Color delimiterColor = DELIMITER_COLOR;
    private int delimiterStrokeIndex = DELIMITER_STROKE_INDEX;
    private int period = PERIOD;

    public IndicatorProperties() {}

    public String getLabel() { return label; }
    public void setLabel(String s) { label = s; }

    public int getPeriod() {return period; }
    public void setPeriod(int period) {this.period = period; }

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