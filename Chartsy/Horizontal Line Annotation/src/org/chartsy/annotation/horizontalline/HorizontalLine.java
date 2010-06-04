package org.chartsy.annotation.horizontalline;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.font.LineMetrics;
import java.text.DecimalFormat;
import org.chartsy.main.ChartFrame;
import org.chartsy.main.chart.Annotation;
import org.chartsy.main.utils.CoordCalc;
import org.chartsy.main.utils.SerialVersion;
import org.openide.nodes.AbstractNode;

/**
 *
 * @author viorel.gheba
 */
public class HorizontalLine
        extends Annotation
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    private AnnotationProperties properties;

    private DecimalFormat df = new DecimalFormat("#,##0.00");
    private Font font;

    public HorizontalLine()
    { super(); }

    public HorizontalLine(ChartFrame frame)
    {
        super(frame);
        actionSet.set(LEFT);
        actionSet.set(RIGHT);
        Font f = frame.getChartProperties().getFont();
        font = new Font(f.getName(), Font.PLAIN, 10);
        properties = new AnnotationProperties();
    }

    public @Override String getName()
    { return "Horizontal Line"; }

    public Annotation newInstance(ChartFrame frame)
    { return new HorizontalLine(frame); }

    public boolean pointIntersects(double x, double y)
    {
        Rectangle bounds = getAnnotationPanel().getBounds();
        double Y = getYFromValue(getV1());
        return (getActionPoint(x, y) != NONE) || lineContains(bounds.getMinX(), Y, bounds.getMaxX(), Y, x, y, 4);
    }

    public void paint(Graphics2D g)
    {
        Rectangle bounds = getAnnotationPanel().getBounds();
        double y = getYFromValue(getV1());
        Stroke oldStroke = g.getStroke();
        Font oldFont = g.getFont();
        g.setPaint(properties.getColor());
        g.setStroke(properties.getStroke());
        g.setFont(font);

        g.draw(CoordCalc.line(bounds.getMinX(), y, bounds.getMaxX(), y));

        LineMetrics lm = font.getLineMetrics("0123456789", g.getFontRenderContext());
        g.drawString(df.format(getV1()), (float) (bounds.getMinX() + 10), (float) (y + lm.getDescent() - 4));

        g.setStroke(oldStroke);
        g.setFont(oldFont);

        if (isSelected()) 
            paintActionPoints(g);
    }

    protected @Override void paintActionPoints(Graphics2D g)
    {
        Rectangle bounds = getAnnotationPanel().getBounds();
        double y = getYFromValue(getV1());
        g.setPaint(Color.BLACK);
        
        if (actionSet.get(LEFT))
        {
            g.draw(CoordCalc.rectangle(bounds.getMinX() + 5 - 2, y - 2, 4, 4));
        }
        if (actionSet.get(RIGHT))
        {
            g.draw(CoordCalc.rectangle(bounds.getMaxX() - 5 - 2, y - 2, 4, 4));
        }
    }

    public AbstractNode getNode() 
    { return new AnnotationNode(properties); }

}
