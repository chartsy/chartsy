/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.chartsy.trix;

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

    public static String LABEL = "TRIX";
    public static boolean MARKER = true;
    public static int PERIOD = 13;
    public static int EMA_PERIOD = 8;
    public static final Color TRIX_LINE_COLOR = new Color(0x5c3566);
    public static int TRIX_STROKE_INDEX = 0;
    public static final Color SIGNAL_LINE_COLOR = new Color(0x4e9a06);
    public static int SIGNAL_STROKE_INDEX = 0;
    public static final Color ZERO_LINE_COLOR = new Color(0xbbbbbb);
    public static int ZERO_LINE_STROKE_INDEX = 0;
    
    private String label = LABEL;
    private boolean marker = MARKER;
    private int period = PERIOD;
    private int emaPeriod = EMA_PERIOD;
    private int trixStrokeIndex = TRIX_STROKE_INDEX;
    private int signalStrokeIndex = SIGNAL_STROKE_INDEX;
    private Color signalColor = SIGNAL_LINE_COLOR;
    private Color trixColor = TRIX_LINE_COLOR;
    private Color zeroLineColor = ZERO_LINE_COLOR;
    private int zeroLineStrokeIndex = ZERO_LINE_STROKE_INDEX;

    
    public IndicatorProperties() {}

    public String getLabel() { return label; }
    public void setLabel(String s) { label = s; }

    public boolean getMarker() { return marker; }
    public void setMarker(boolean b) { marker = b; }

    public int getPeriod() {return period; }
    public void setPeriod(int period) {this.period = period; }

    public int getEmaPeriod() {return emaPeriod; }
    public void setEmaPeriod(int emaPeriod) {this.emaPeriod = emaPeriod; }

    public Color getSignalColor() {return signalColor; }
    public void setSignalColor(Color signalColor) {this.signalColor = signalColor; }

    public Color getTrixColor() {return trixColor; }
    public void setTrixColor(Color trixColor) {this.trixColor = trixColor; }
    
    public int getSignalStrokeIndex() {return signalStrokeIndex; }
    public void setSignalStrokeIndex(int signalStrokeIndex) { this.signalStrokeIndex = signalStrokeIndex; }
    public Stroke getSignalStroke() { return StrokeGenerator.getStroke(signalStrokeIndex); }
    public void setSignalStroke(Stroke s) { signalStrokeIndex = StrokeGenerator.getStrokeIndex(s); }

    public int getTrixStrokeIndex() {return trixStrokeIndex; }
    public void setTrixStrokeIndex(int trixStrokeIndex) {this.trixStrokeIndex = trixStrokeIndex; }
    public Stroke getTrixStroke() { return StrokeGenerator.getStroke(trixStrokeIndex); }
    public void setTrixStroke(Stroke s) { trixStrokeIndex = StrokeGenerator.getStrokeIndex(s); }

    public Color getZeroLineColor() {return zeroLineColor; }
    public void setZeroLineColor(Color zeroLineColor) {this.zeroLineColor = zeroLineColor; }

    public int getZeroLineStrokeIndex() {return zeroLineStrokeIndex; }
    public void setZeroLineStrokeIndex(int zeroLineStrokeIndex) {this.zeroLineStrokeIndex = zeroLineStrokeIndex; }
    public Stroke getZeroLineStroke() { return StrokeGenerator.getStroke(zeroLineStrokeIndex); }
    public void setZeroLineStroke(Stroke s) { zeroLineStrokeIndex = StrokeGenerator.getStrokeIndex(s); }



}
