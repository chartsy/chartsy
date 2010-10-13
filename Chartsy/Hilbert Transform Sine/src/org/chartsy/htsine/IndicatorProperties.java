/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.chartsy.htsine;

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

    public static final String LABEL = "Hilbert Transform Sine";
    public static final boolean MARKER = true;
    public static final Color SINE_LINE_COLOR = new Color(0x5c3566);
    public static int SINE_LINE_STROKE_INDEX = 0;
    public static final Color LEAD_SINE_LINE_COLOR = new Color(0x4e9a06);
    public static int LEAD_SINE_LINE_STROKE_INDEX = 0;
    public static final Color ZERO_LINE_COLOR = new Color(0xbbbbbb);
    public static int ZERO_LINE_STROKE_INDEX = 0;



    private String label = LABEL;
    private boolean marker = MARKER;
    private Color sineLineColor = SINE_LINE_COLOR;
    private int sineLineStrokeIndex = SINE_LINE_STROKE_INDEX;
    private Color leadSineLineColor = LEAD_SINE_LINE_COLOR;
    private int leadSineLineStrokeIndex = LEAD_SINE_LINE_STROKE_INDEX;
    private Color zeroLineColor = ZERO_LINE_COLOR;
    private int zeroLineStrokeIndex = ZERO_LINE_STROKE_INDEX;
    

    public IndicatorProperties() {}

    public String getLabel() { return label; }
    public void setLabel(String s) { label = s; }

    public boolean getMarker() { return marker; }
    public void setMarker(boolean b) { marker = b; }

    public Color getSineLineColor() {return sineLineColor; }
    public void setSineLineColor(Color sineLineColor) {this.sineLineColor = sineLineColor; }

    public int getSineLineStrokeIndex() {return sineLineStrokeIndex; }
    public void setSineLineStrokeIndex(int sineLineStrokeIndex) {this.sineLineStrokeIndex = sineLineStrokeIndex; }
    public Stroke getSineLineStroke() { return StrokeGenerator.getStroke(sineLineStrokeIndex); }
    public void setSineLineStroke(Stroke s) { sineLineStrokeIndex = StrokeGenerator.getStrokeIndex(s); }


    public Color getLeadSineLineColor() {return leadSineLineColor; }
    public void setLeadSineLineColor(Color leadSineLineColor) {this.leadSineLineColor = leadSineLineColor; }

    public int getLeadSineLineStrokeIndex() {return leadSineLineStrokeIndex; }
    public void setLeadSineLineStrokeIndex(int leadSineLineStrokeIndex) {this.leadSineLineStrokeIndex = leadSineLineStrokeIndex; }
    public Stroke getLeadSineLineStroke() { return StrokeGenerator.getStroke(leadSineLineStrokeIndex); }
    public void setLeadSineLineStroke(Stroke s) { leadSineLineStrokeIndex = StrokeGenerator.getStrokeIndex(s); }

    public Color getZeroLineColor() { return zeroLineColor; }
    public void setZeroLineColor(Color zeroLineColor) { this.zeroLineColor = zeroLineColor; }

    public int getZeroLineStrokeIndex() {return zeroLineStrokeIndex; }
    public void setZeroLineStrokeIndex(int zeroLineStrokeIndex) {this.zeroLineStrokeIndex = zeroLineStrokeIndex; }
    public Stroke getZeroLineStroke() { return StrokeGenerator.getStroke(zeroLineStrokeIndex); }
    public void setZeroLineStroke(Stroke s) { zeroLineStrokeIndex = StrokeGenerator.getStrokeIndex(s); }

}
