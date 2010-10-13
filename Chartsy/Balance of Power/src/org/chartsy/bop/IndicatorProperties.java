/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.chartsy.bop;

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

    public static String LABEL = "BOP";
    public static boolean MARKER = true;
    public static Color ZERO_LINE_COLOR = new Color(0x000000);
    public static int ZERO_LINE_STROKE_INDEX = 0;
    public static Color HISTOGRAM_POSITIVE_COLOR = new Color(0x73880A);
    public static Color HISTOGRAM_NEGATIVE_COLOR = new Color(0xCC0000);

    private String label = LABEL;
    private boolean marker = MARKER;
    private Color zeroLineColor = ZERO_LINE_COLOR;
    private int zeroLineStrokeIndex = ZERO_LINE_STROKE_INDEX;
    private Color histogramPositiveColor = HISTOGRAM_POSITIVE_COLOR;
    private Color histogramNegativeColor = HISTOGRAM_NEGATIVE_COLOR;

    public IndicatorProperties() {}

    public String getLabel() { return label; }
    public void setLabel(String s) { label = s; }

    public boolean getMarker() { return marker; }
    public void setMarker(boolean b) { marker = b; }

    public Color getZeroLineColor() { return zeroLineColor; }
    public void setZeroLineColor(Color c) { zeroLineColor = c; }

    public int getZeroLineStrokeIndex() { return zeroLineStrokeIndex; }
    public void setZeroLineStrokeIndex(int i) { zeroLineStrokeIndex = i; }
    public Stroke getZeroLineStroke() { return StrokeGenerator.getStroke(zeroLineStrokeIndex); }
    public void setZeroLineStroke(Stroke s) { zeroLineStrokeIndex = StrokeGenerator.getStrokeIndex(s); }

    public Color getHistogramPositiveColor() { return histogramPositiveColor; }
    public void setHistogramPositiveColor(Color c) { histogramPositiveColor = c; }

    public Color getHistogramNegativeColor() { return histogramNegativeColor; }
    public void setHistogramNegativeColor(Color c) { histogramNegativeColor = c; }

}
