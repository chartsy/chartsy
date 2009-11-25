package org.chartsy.annotation.arrowline;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
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
public class ArrowLine extends Annotation implements Serializable {

    private static final long serialVersionUID = 101L;

    protected double arrowPosition = 1;
    protected double arrowSize = 14;
    protected double arrowAngle = Math.atan(1) * 2 / 5;

    public ArrowLine(ChartFrame chartFrame) {
        super(chartFrame);
        inflectionSet.set(TOP_LEFT);
        inflectionSet.set(BOTTOM_RIGHT);
    }

    public boolean pointIntersects(double x, double y) { return (getInflectionPoint(x, y) != NONE) || lineContains(getP1(), getP2(), new Point2D.Double(x, y), 4); }

    public void paint(Graphics2D g) {
        Stroke old = g.getStroke();
        g.setPaint(color);
        g.setStroke(StrokeGenerator.getStroke(strokeIndex));
        Point2D.Double p1 = getP1();
        Point2D.Double p2 = getP2();
        g.draw(new Line2D.Double(p1, p2));
        paintArrow(g);
        g.setStroke(old);
        if (isSelected()) paintInflectionPoints(g);
    }

    protected void paintArrow(Graphics2D g) {
        double X1 = getXCoord(getT1()), X2 = getXCoord(getT2());
        double Y1 = getYCoord(getV1()), Y2 = getYCoord(getV2());
        
        double arrowHeadX = X1 + (X2 - X1) * arrowPosition;
        double arrowHeadY = Y1 + (Y2 - Y1) * arrowPosition;
        double alpha = Math.asin((Y2 - Y1) / Math.sqrt(Math.pow(X2 - X1, 2) + Math.pow((Y2 - Y1), 2)));

        double absoluteAngle1 = X2 > X1 ? Math.PI + (alpha - arrowAngle) : arrowAngle - alpha;

        double firstArrowTailX = arrowHeadX + arrowSize * Math.cos(absoluteAngle1);
        double firstArrowTailY = arrowHeadY + arrowSize * Math.sin(absoluteAngle1);

        double absoluteAngle2 = X2 > X1 ? Math.PI + (alpha + arrowAngle) : -arrowAngle - alpha;

        double secondArrowTailX = arrowHeadX + arrowSize * Math.cos(absoluteAngle2);
        double secondArrowTailY = arrowHeadY + arrowSize * Math.sin(absoluteAngle2);

        g.draw(new Line2D.Double(firstArrowTailX, firstArrowTailY, arrowHeadX, arrowHeadY));
        g.draw(new Line2D.Double(secondArrowTailX, secondArrowTailY, arrowHeadX, arrowHeadY));
    }

    protected void updateRectangles(long oldT1, long newT1, long oldT2, long newT2, double oldV1, double newV1, double oldV2, double newV2) {
        Rectangle2D.Double r;
        r = getRectangle(oldT1, oldV1, oldT2, oldV2);
        Rectangle r1 = new Rectangle(); r1.setFrameFromDiagonal(r.getMinX(), r.getMinY(), r.getMaxX(), r.getMaxY());
        r1.grow(3, 3);
        addUpdateRectangle(r1, false);
        r = getRectangle(newT1, newV1, newT2, newV2);
        Rectangle r2 = new Rectangle(); r2.setFrameFromDiagonal(r.getMinX(), r.getMinY(), r.getMaxX(), r.getMaxY());
        r2.grow(3, 3);
        addUpdateRectangle(r2, false);
    }

    public void readXMLDocument(Element parent) { readFromXMLDocument(parent); }
    public void writeXMLDocument(Document document, Element parent) { writeToXMLDocument(document, parent, "Arrow Line"); }

}
