/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.chartsy.stochf;

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

    public static final int PERIOD_K = 14;
    public static final int PERIOD_D = 3;
    public static final int SMOOTH = 3;
    public static final String LABEL = "Stochastic Fast";
    public static boolean MARKER = true;
    public static boolean SF = true;
    public static Color COLOR_D = new Color(0x4e9a06);
    public static int STROKE_INDEX_D = 0;
    public static Color COLOR_K = new Color(0xf57900);
    public static int STROKE_INDEX_K = 0;
    public static final Color DELIMITER_COLOR = new Color(0xbbbbbb);
    public static int DELIMITER_STROKE_INDEX = 1;
    

    private int periodK = PERIOD_K;
    private int periodD = PERIOD_D;
    private int smooth = SMOOTH;
    private String label = LABEL;
    private boolean marker = MARKER;
    private boolean sf = SF;
    private Color colorD = COLOR_D;
    private int strokeIndexD = STROKE_INDEX_D;
    private Color colorK = COLOR_K;
    private int strokeIndexK = STROKE_INDEX_K;
    private Color delimiterColor = DELIMITER_COLOR;
    private int delimiterStrokeIndex = DELIMITER_STROKE_INDEX;

    public IndicatorProperties() {}

    public int getPeriodK() { return periodK; }
    public void setPeriodK(int i) { periodK = i; }

    public int getPeriodD() { return periodD; }
    public void setPeriodD(int i) { periodD = i; }

    public int getSmooth() { return smooth; }
    public void setSmooth(int i) { smooth = i; }

    public String getLabel() { return label; }
    public void setLabel(String s) { label = s; }

    public boolean getMarker() { return marker; }
    public void setMarker(boolean b) { marker = b; }

    public boolean getSF() { return sf; }
    public void setSF(boolean b) { sf = b; }

    public Color getColorD() { return colorD; }
    public void setColorD(Color c) { colorD = c; }

    public int getStrokeIndexD() { return strokeIndexD; }
    public void setStrokeIndexD(int i) { strokeIndexD = i; }
    public Stroke getStrokeD() { return StrokeGenerator.getStroke(strokeIndexD); }
    public void setStrokeD(Stroke s) { strokeIndexD = StrokeGenerator.getStrokeIndex(s); }

    public Color getColorK() { return colorK; }
    public void setColorK(Color c) { colorK = c; }

    public int getStrokeIndexK() { return strokeIndexK; }
    public void setStrokeIndexK(int i) { strokeIndexK = i; }
    public Stroke getStrokeK() { return StrokeGenerator.getStroke(strokeIndexK); }
    public void setStrokeK(Stroke s) { strokeIndexK = StrokeGenerator.getStrokeIndex(s); }

    public Color getDelimiterColor() { return delimiterColor; }
    public void setDelimiterColor(Color delimiterColor) { this.delimiterColor = delimiterColor; }

    public int getDelimiterStrokeIndex() {return delimiterStrokeIndex; }
    public void setDelimiterStrokeIndex(int delimiterStrokeIndex) {this.delimiterStrokeIndex = delimiterStrokeIndex; }
    public Stroke getDelimiterLineStroke() { return StrokeGenerator.getStroke(delimiterStrokeIndex); }
    public void setDelimiterLineStroke(Stroke s) { delimiterStrokeIndex = StrokeGenerator.getStrokeIndex(s); }

}
