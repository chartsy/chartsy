package org.chartsy.annotation.rectangle;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.io.Serializable;
import org.chartsy.main.chartsy.ChartFrame;
import org.chartsy.main.chartsy.chart.Annotation;
import org.chartsy.main.utils.StrokeGenerator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author viorel.gheba
 */
public class RectangleAnnotation extends Annotation implements Serializable {

    private static final long serialVersionUID = 101L;

    public RectangleAnnotation(ChartFrame chartFrame) {
        super(chartFrame);
        inflectionSet.set(TOP);
        inflectionSet.set(TOP_LEFT);
        inflectionSet.set(TOP_RIGHT);
        inflectionSet.set(LEFT);
        inflectionSet.set(RIGHT);
        inflectionSet.set(BOTTOM);
        inflectionSet.set(BOTTOM_LEFT);
        inflectionSet.set(BOTTOM_RIGHT);
    }

    public boolean pointIntersects(double x, double y) {
        if (getInflectionPoint(x, y) != NONE) return true;
        double X1 = getXCoord(getT1()), X2 = getXCoord(getT2());
        double Y1 = getYCoord(getV1()), Y2 = getYCoord(getV2());
        Rectangle r = new Rectangle(); r.setFrameFromDiagonal(X1, Y1, X2, Y2);
        return r.contains(x, y);
    }

    public void paint(Graphics2D g) {
        Stroke old = g.getStroke();
        double X1 = getXCoord(getT1()), X2 = getXCoord(getT2());
        double Y1 = getYCoord(getV1()), Y2 = getYCoord(getV2());
        Rectangle r = new Rectangle(); r.setFrameFromDiagonal(X1, Y1, X2, Y2);
        g.setPaint(fillColor);
        g.fill(r);
        g.setStroke(StrokeGenerator.getStroke(strokeIndex));
        g.setPaint(color);
        g.draw(r);
        g.setStroke(old);
        if (isSelected()) paintInflectionPoints(g);
    }

    public void readXMLDocument(Element parent) { readFromXMLDocument(parent); }
    public void writeXMLDocument(Document document, Element parent) { writeToXMLDocument(document, parent, "Rectangle"); }

}
