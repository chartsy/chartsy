/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.chartsy.avgdirectionalindex;

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

    public static final String LABEL = "ADX";
    public static final boolean MARKER = true;
    public static final Color COLOR = new Color(0x4e9a06);
    public static final Color DELIMITER_COLOR = new Color(0xbbbbbb);
    public static int STROKE_INDEX = 0;
    public static int DELIMTER_STROKE_INDEX = 1;
    public static int DEFAULT_PERIOD = 14;//standard default according to W. Wilder
    
    private String label = LABEL;
    private boolean marker = MARKER;
    private Color color = COLOR;
    private Color delimiterColor = DELIMITER_COLOR;
    private int strokeIndex = STROKE_INDEX;
    private int delimiterStrokeIndex = DELIMTER_STROKE_INDEX;

    private int period = DEFAULT_PERIOD;//standard default according to W. Wilder
    
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

    public Color getDelimiterColor() {return delimiterColor; }
    public void setDelimiterColor(Color delimiterColor) {this.delimiterColor = delimiterColor; }

    public int getDelimiterStrokeIndex() {return delimiterStrokeIndex; }
    public void setDelimiterStrokeIndex(int delimiterStrokeIndex) {this.delimiterStrokeIndex = delimiterStrokeIndex; }
    public Stroke getDelimterLineStroke() { return StrokeGenerator.getStroke(delimiterStrokeIndex); }
    public void setDelimiterLineStroke(Stroke s) { delimiterStrokeIndex = StrokeGenerator.getStrokeIndex(s); }

}
