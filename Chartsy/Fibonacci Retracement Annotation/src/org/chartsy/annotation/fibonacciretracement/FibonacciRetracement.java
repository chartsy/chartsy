package org.chartsy.annotation.fibonacciretracement;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.font.LineMetrics;
import java.awt.geom.Line2D;
import java.text.DecimalFormat;
import org.chartsy.main.chartsy.ChartFrame;
import org.chartsy.main.chartsy.chart.Annotation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author viorel.gheba
 */
public class FibonacciRetracement extends Annotation {

    protected DecimalFormat df = new DecimalFormat("#,##0.00");
    protected double[] defaultLines = new double[]{38.2, 50, 61.8, 127.2, 161.8};
    protected boolean[] defaultShow = new boolean[]{true, false, true, false, false};
    protected Font font;

    public FibonacciRetracement(ChartFrame cf) {
        super(cf);
        inflectionSet.set(TOP_RIGHT);
        inflectionSet.set(TOP_LEFT);
        inflectionSet.set(BOTTOM_RIGHT);
        inflectionSet.set(BOTTOM_LEFT);
        Font f = cf.getChartProperties().getFont();
        font = new Font(f.getName(), f.getStyle(), 10);
    }

    protected String getLabel(double percent, double value) { return df.format(percent) + "% (" + df.format(value) + ")"; }

    public boolean pointIntersects(double x, double y) {
        if (getInflectionPoint(x, y) != NONE) return true;
        double X1 = getXCoord(getT1()), X2 = getXCoord(getT2());
        double Y1 = getYCoord(getV1()), Y2 = getYCoord(getV2());
        Rectangle r = new Rectangle(); r.setFrameFromDiagonal(X1, Y1, X2, Y2);
        return r.contains(x, y);
    }

    public void paint(Graphics2D g) {
        Stroke old = g.getStroke();
        g.setPaint(color);
        g.setStroke(stroke);
        g.setFont(font);
        double max = Math.max(getV1(), getV2());
        double min = Math.min(getV1(), getV2());
        paintLine(g, 0, max, min);
        for (int i = 0; i < defaultShow.length; i++) if (defaultShow[i]) paintLine(g, defaultLines[i], max, min);
        paintLine(g, 100, max, min);
        g.setStroke(old);
        if (isSelected ()) paintInflectionPoints(g);
    }

    protected void paintLine(Graphics2D g, double percent, double max, double min) {
        double dif = max - min;
        double value = min + (dif * percent) / 100;
        double X1 = getXCoord(getT1()), X2 = getXCoord(getT2());
        double Y = getYCoord(value);
        g.draw(new Line2D.Double(X1, Y, X2, Y));
        String label = getLabel(percent, value);
        LineMetrics lm = font.getLineMetrics("0123456789", g.getFontRenderContext());
        paintLabel(g, (float) Math.min(X1, X2) + 4, (float) (Y + lm.getDescent() - 4), label);
    }

    protected void paintLabel(Graphics2D g, float x, float y, String label) {
        g.drawString(label, x, y);
    }

    public void readXMLDocument(Element parent) { readFromXMLDocument(parent); }
    public void writeXMLDocument(Document document, Element parent) { writeToXMLDocument(document, parent, "Fibonacci Retracement"); }

}
