package org.chartsy.annotation.fibonacciextension;

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
public class FibonacciExtension
        extends Annotation
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;
    
    private AnnotationProperties properties;

    protected DecimalFormat df = new DecimalFormat("#,##0.00");
    protected double[] defaultLines = new double[]{38.2, 50, 61.8, 127.2, 161.8};
    protected boolean[] defaultShow = new boolean[]{false, false, false, true, true};
    protected Font font;

    public FibonacciExtension()
    { super(); }

    public FibonacciExtension(ChartFrame frame)
    {
        super(frame);
        actionSet.set(TOP_RIGHT);
        actionSet.set(TOP_LEFT);
        actionSet.set(BOTTOM_RIGHT);
        actionSet.set(BOTTOM_LEFT);
        Font f = frame.getChartProperties().getFont();
        font = new Font(f.getName(), f.getStyle(), 10);
        properties = new AnnotationProperties();
    }

    public @Override String getName()
    { return "Fibonacci Extension"; }

    public Annotation newInstance(ChartFrame frame)
    { return new FibonacciExtension(frame); }

    public boolean pointIntersects(double x, double y) {
        if (getActionPoint(x, y) != NONE)
            return true;

        double X1 = getXFromTime(getT1()), X2 = getXFromTime(getT2());
        double Y1 = getYFromValue(getV1()), Y2 = getYFromValue(getV2());

        Rectangle r = new Rectangle();
        r.setFrameFromDiagonal(X1, Y1, X2, Y2);
        
        return r.contains(x, y);
    }

    public void paint(Graphics2D g)
    {
        Stroke old = g.getStroke();
        g.setPaint(properties.getColor());
        g.setStroke(properties.getStroke());
        g.setFont(font);

        double max = Math.max(getV1(), getV2());
        double min = Math.min(getV1(), getV2());

        paintLine(g, 0, max, min);
        paintLine(g, 100, max, min);
        for (int i = 0; i < defaultShow.length; i++)
            if (defaultShow[i])
                paintLine(g, defaultLines[i], max, min);
        
        g.setStroke(old);

        if (isSelected ())
            paintActionPoints(g);
    }

    private String getLabel(double percent, double value)
    { return df.format(percent) + "% (" + df.format(value) + ")"; }

    private void paintLine(Graphics2D g, double percent, double max, double min)
    {
        double dif = max - min;
        double value = min + (dif * (100 - percent)) / 100;
        double X1 = getXFromTime(getT1()), X2 = getXFromTime(getT2());
        double Y = getYFromValue(value);

        g.draw(CoordCalc.line(X1, Y, X2, Y));
        String label = getLabel(percent, value);
        LineMetrics lm = font.getLineMetrics("0123456789", g.getFontRenderContext());
        paintLabel(g, (float) Math.min(X1, X2) + 4, (float) (Y + lm.getDescent() - 4), label);
    }

    private void paintLabel(Graphics2D g, float x, float y, String label)
    { g.drawString(label, x, y); }

    public AbstractNode getNode() 
    { return new AnnotationNode(properties); }

}
