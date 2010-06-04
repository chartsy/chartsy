package org.chartsy.annotation.verticalline;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import org.chartsy.main.ChartFrame;
import org.chartsy.main.chart.Annotation;
import org.chartsy.main.utils.CoordCalc;
import org.chartsy.main.utils.SerialVersion;
import org.openide.nodes.AbstractNode;

/**
 *
 * @author viorel.gheba
 */
public class VerticalLine
        extends Annotation
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;
    
    private AnnotationProperties properties;

    public VerticalLine()
    { super(); }

    public VerticalLine(ChartFrame frame)
    {
        super(frame);
        actionSet.set(TOP);
        actionSet.set(BOTTOM);
        properties = new AnnotationProperties();
    }

    public @Override String getName()
    { return "Vertical Line"; }

    public Annotation newInstance(ChartFrame frame) 
    { return new VerticalLine(frame); }

    public boolean pointIntersects(double x, double y)
    {
        Rectangle bounds = getAnnotationPanel().getBounds();
        double X = getXFromTime(getT1());
        return (getActionPoint(x, y) != NONE) || lineContains(X, bounds.getMinY(), X, bounds.getMaxY(), x, y, 4);
    }

    public void paint(Graphics2D g) 
    {
        Rectangle bounds = getAnnotationPanel().getBounds();
        double x = getXFromTime(getT1());

        Stroke old = g.getStroke();
        g.setPaint(properties.getColor());
        g.setStroke(properties.getStroke());

        g.draw(CoordCalc.line(x, bounds.getMinY(), x, bounds.getMaxY()));
        g.setStroke(old);

        if (isSelected())
            paintActionPoints(g);
    }

    protected void paintActionPoints(Graphics2D g) {
        Rectangle bounds = getAnnotationPanel().getBounds();
        double x = getXFromTime(getT1());
        g.setPaint(Color.BLACK);

        if (actionSet.get(TOP)) {
            g.draw(CoordCalc.rectangle(x - 2, bounds.getMinY() + 5 - 2, 4, 4));
        }
        if (actionSet.get(BOTTOM)) {
            g.draw(CoordCalc.rectangle(x - 2, bounds.getMaxY() - 5 - 2, 4, 4));
        }
    }

    public AbstractNode getNode()
    { return new AnnotationNode(properties); }

}
