package org.chartsy.annotation.rectangle;

import java.awt.Graphics2D;
import java.awt.Stroke;
import org.chartsy.main.ChartFrame;
import org.chartsy.main.chart.Annotation;
import org.chartsy.main.utils.SerialVersion;
import org.openide.nodes.AbstractNode;

/**
 *
 * @author viorel.gheba
 */
public class Rectangle
        extends Annotation
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;
    private AnnotationProperties properties;

    public Rectangle()
    { super(); }

    public Rectangle(ChartFrame frame)
    {
        super(frame);
        actionSet.set(TOP);
        actionSet.set(TOP_LEFT);
        actionSet.set(TOP_RIGHT);
        actionSet.set(LEFT);
        actionSet.set(RIGHT);
        actionSet.set(BOTTOM);
        actionSet.set(BOTTOM_LEFT);
        actionSet.set(BOTTOM_RIGHT);
        properties = new AnnotationProperties();
    }

    public @Override String getName()
    { return "Rectangle"; }

    public Annotation newInstance(ChartFrame frame) 
    { return new Rectangle(frame); }

    public boolean pointIntersects(double x, double y) {
        if (getActionPoint(x, y) != NONE)
            return true;

        double X1 = getXFromTime(getT1()), X2 = getXFromTime(getT2());
        double Y1 = getYFromValue(getV1()), Y2 = getYFromValue(getV2());

        java.awt.Rectangle r = new java.awt.Rectangle();
        r.setFrameFromDiagonal(X1, Y1, X2, Y2);

        return r.contains(x, y);
    }

    public void paint(Graphics2D g) 
    {
        Stroke old = g.getStroke();

        double X1 = getXFromTime(getT1()), X2 = getXFromTime(getT2());
        double Y1 = getYFromValue(getV1()), Y2 = getYFromValue(getV2());

        java.awt.Rectangle r = new java.awt.Rectangle();
        r.setFrameFromDiagonal(X1, Y1, X2, Y2);

        if (properties.getInsideVisibility())
        {
            g.setPaint(properties.getFillColor());
            g.fill(r);
        }
        
        g.setPaint(properties.getColor());
        g.setStroke(properties.getStroke());
        g.draw(r);
        g.setStroke(old);

        if (isSelected())
            paintActionPoints(g);
    }

    public AbstractNode getNode() 
    { return new AnnotationNode(properties); }

}
