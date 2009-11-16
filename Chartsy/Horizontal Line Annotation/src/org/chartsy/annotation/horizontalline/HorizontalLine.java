package org.chartsy.annotation.horizontalline;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import org.chartsy.main.chartsy.ChartFrame;
import org.chartsy.main.chartsy.chart.Annotation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author viorel.gheba
 */
public class HorizontalLine extends Annotation {

    public HorizontalLine(ChartFrame chartFrame) {
        super(chartFrame);
        inflectionSet.set(LEFT);
        inflectionSet.set(RIGHT);
    }

    public boolean pointIntersects(double x, double y) { double Y = getYCoord(getV1()); return (getInflectionPoint(x, y) != NONE) || lineContains(getBounds().getMinX(), Y, getBounds().getMaxX(), Y, x, y, 4); }

    public void paint(Graphics2D g) {
        double y = getYCoord(getV1());
        Stroke old = g.getStroke();
        g.setPaint(color);
        g.setStroke(stroke);
        g.draw(new Line2D.Double(getBounds().getMinX(), y, getBounds().getMaxX(), y));
        g.setStroke(old);
        if (isSelected()) paintInflectionPoints(g);
    }

    protected void paintInflectionPoints(Graphics2D g) {
        double y = getYCoord(getV1());
        g.setPaint(Color.BLACK);
        if (inflectionSet.get(LEFT)) {
            g.draw(new Rectangle2D.Double(getBounds().getMinX() + 5 - 2, y - 2, 4, 4));
        }
        if (inflectionSet.get(RIGHT)) {
            g.draw(new Rectangle2D.Double(getBounds().getMaxX() - 5 - 2, y - 2, 4, 4));
        }
    }

    public void readXMLDocument(Element parent) { readFromXMLDocument(parent); }
    public void writeXMLDocument(Document document, Element parent) { writeToXMLDocument(document, parent, "Horizontal Line"); }

}
