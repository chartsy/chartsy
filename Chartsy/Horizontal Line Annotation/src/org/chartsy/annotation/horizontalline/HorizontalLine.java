package org.chartsy.annotation.horizontalline;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.font.LineMetrics;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.text.DecimalFormat;
import org.chartsy.main.chartsy.ChartFrame;
import org.chartsy.main.chartsy.chart.Annotation;
import org.openide.nodes.AbstractNode;

/**
 *
 * @author viorel.gheba
 */
public class HorizontalLine extends Annotation implements Serializable {

    private static final long serialVersionUID = 101L;

    private AnnotationProperties properties = new AnnotationProperties();

    protected DecimalFormat df = new DecimalFormat("#,##0.00");
    protected Font font;

    public HorizontalLine(ChartFrame chartFrame) {
        super(chartFrame);
        inflectionSet.set(LEFT);
        inflectionSet.set(RIGHT);
        Font f = chartFrame.getChartProperties().getFont();
        font = new Font(f.getName(), Font.PLAIN, 10);
    }

    public boolean pointIntersects(double x, double y) { double Y = getYCoord(getV1()); return (getInflectionPoint(x, y) != NONE) || lineContains(getBounds().getMinX(), Y, getBounds().getMaxX(), Y, x, y, 4); }

    public void paint(Graphics2D g) {
        double y = getYCoord(getV1());
        Stroke oldStroke = g.getStroke();
        Font oldFont = g.getFont();
        g.setPaint(properties.getColor());
        g.setStroke(properties.getStroke());
        g.setFont(font);
        g.draw(new Line2D.Double(getBounds().getMinX(), y, getBounds().getMaxX(), y));
        LineMetrics lm = font.getLineMetrics("0123456789", g.getFontRenderContext());
        g.drawString(df.format(getV1()), (float) (getBounds().getMinX() + 10), (float) (y + lm.getDescent() - 4));
        g.setStroke(oldStroke);
        g.setFont(oldFont);
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

    public AbstractNode getNode() { return new AnnotationNode(properties); }

}
