/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.chartsy.mesa;

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

    public static String LABEL = "MESA Adaptive Moving Avg.";
    public static boolean MARKER = true;

    public static double FAST_LIMIT = 0.5;
    public static double SLOW_LIMIT = 0.05;

    public static final Color MAMA_LINE_COLOR = new Color(0x5c3566);
    public static int MAMA_LINE_STROKE_INDEX = 0;
    public static final Color FAMA_LINE_COLOR = new Color(0x4e9a06);
    public static int FAMA_LINE_STROKE_INDEX = 0;
    public static final Color DELIMITER_COLOR = new Color(0xbbbbbb);

    private String label = LABEL;
    private boolean marker = MARKER;
    private double fastLimit = FAST_LIMIT;
    private double slowLimit = SLOW_LIMIT;
    private int mamaLineStrokeIndex = MAMA_LINE_STROKE_INDEX;
    private int famaLineStrokeIndex = FAMA_LINE_STROKE_INDEX;
    private Color famaLineColor = FAMA_LINE_COLOR;
    private Color mamaLineColor = MAMA_LINE_COLOR;
    private Color delimiterColor = DELIMITER_COLOR;

    public IndicatorProperties() {}

    public String getLabel() { return label; }
    public void setLabel(String s) { label = s; }

    public boolean getMarker() { return marker; }
    public void setMarker(boolean b) { marker = b; }

    public Color getDelimiterColor() {return delimiterColor; }
    public void setDelimiterColor(Color delimiterColor) {this.delimiterColor = delimiterColor; }

    public Color getFamaLineColor() {return famaLineColor; }
    public void setFamaLineColor(Color famaLineColor) {this.famaLineColor = famaLineColor; }

    public int getFamaLineStrokeIndex() {return famaLineStrokeIndex; }
    public void setFamaLineStrokeIndex(int famaLineStrokeIndex) {this.famaLineStrokeIndex = famaLineStrokeIndex; }
    public Stroke getFamaLineStroke() { return StrokeGenerator.getStroke(famaLineStrokeIndex); }
    public void setFamaLineStroke(Stroke s) { famaLineStrokeIndex = StrokeGenerator.getStrokeIndex(s); }


    public double getFastLimit() {return fastLimit; }
    public void setFastLimit(double fastLimit) {this.fastLimit = fastLimit; }

    public Color getMamaLineColor() {return mamaLineColor; }
    public void setMamaLineColor(Color mamaLineColor) {this.mamaLineColor = mamaLineColor; }

    public int getMamaLineStrokeIndex() {return mamaLineStrokeIndex; }
    public void setMamaLineStrokeIndex(int mamaLineStrokeIndex) {this.mamaLineStrokeIndex = mamaLineStrokeIndex; }
    public Stroke getMamaLineStroke() { return StrokeGenerator.getStroke(mamaLineStrokeIndex); }
    public void setMamaLineStroke(Stroke s) { mamaLineStrokeIndex = StrokeGenerator.getStrokeIndex(s); }

    public double getSlowLimit() {return slowLimit; }
    public void setSlowLimit(double slowLimit) {this.slowLimit = slowLimit; }
    
}
