/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.chartsy.htphasor;

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

    public static final String LABEL = "Hilbert Transform InPhase Quadrature";
    public static final boolean MARKER = true;
    public static final Color IN_PHASE_LINE_COLOR = new Color(0x5c3566);
    public static int IN_PHASE_LINE_STROKE_INDEX = 0;
    public static final Color QUADRATURE_LINE_COLOR = new Color(0x4e9a06);
    public static int QUADRATURE_LINE_STROKE_INDEX = 0;
    public static final Color ZERO_LINE_COLOR = new Color(0xbbbbbb);


    private String label = LABEL;
    private boolean marker = MARKER;
    private Color inPhaseLineColor = IN_PHASE_LINE_COLOR;
    private int inPhaseLineStrokeIndex = IN_PHASE_LINE_STROKE_INDEX;
    private Color quadratureLineColor = QUADRATURE_LINE_COLOR;
    private int quadratureLineStrokeIndex = QUADRATURE_LINE_STROKE_INDEX;
    private Color zeroLineColor = ZERO_LINE_COLOR;

    public IndicatorProperties() {}

    public String getLabel() { return label; }
    public void setLabel(String s) { label = s; }

    public boolean getMarker() { return marker; }
    public void setMarker(boolean b) { marker = b; }

    public Color getInPhaseLineColor() {return inPhaseLineColor; }
    public void setInPhaseLineColor(Color inPhaseLineColor) {this.inPhaseLineColor = inPhaseLineColor; }

    public int getInPhaseLineStrokeIndex() {return inPhaseLineStrokeIndex; }
    public void setInPhaseLineStrokeIndex(int inPhaseLineStrokeIndex) {this.inPhaseLineStrokeIndex = inPhaseLineStrokeIndex; }
    public Stroke getInPhaseLineStroke() { return StrokeGenerator.getStroke(inPhaseLineStrokeIndex); }
    public void setInPhaseLineStroke(Stroke s) { inPhaseLineStrokeIndex = StrokeGenerator.getStrokeIndex(s); }


    public Color getQuadratureLineColor() {return quadratureLineColor; }
    public void setQuadratureLineColor(Color quadratureLineColor) {this.quadratureLineColor = quadratureLineColor; }

    public int getQuadratureLineStrokeIndex() {return quadratureLineStrokeIndex; }
    public void setQuadratureLineStrokeIndex(int quadratureLineStrokeIndex) {this.quadratureLineStrokeIndex = quadratureLineStrokeIndex; }
    public Stroke getQuadratureLineStroke() { return StrokeGenerator.getStroke(quadratureLineStrokeIndex); }
    public void setQuadratureLineStroke(Stroke s) { quadratureLineStrokeIndex = StrokeGenerator.getStrokeIndex(s); }

    public Color getZeroLineColor() {return zeroLineColor; }
    public void setZeroLineColor(Color zeroLineColor) {this.zeroLineColor = zeroLineColor; }

}
