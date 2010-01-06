package org.chartsy.main.chartsy;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.font.LineMetrics;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.chartsy.main.dataset.Dataset;
import org.chartsy.main.utils.Range;

/**
 *
 * @author viorel.gheba
 */
public final class DefaultPainter {

    private DefaultPainter() {}

    public static void label(Graphics2D g, ChartFrame cf, String s, Rectangle2D.Double bounds) {
        LineMetrics lm = cf.getChartProperties().getFont().getLineMetrics("012345679", g.getFontRenderContext());
        g.setFont(cf.getChartProperties().getFont());
        g.setPaint(cf.getChartProperties().getFontColor());
        g.drawString(s, (float) cf.getChartProperties().getDataOffset().left, (float)(bounds.getMinY() - cf.getChartProperties().getAxisOffset().top + lm.getAscent()));
    }

    public static void line(Graphics2D g, ChartFrame cf, Dataset dataset, Color color, Stroke stroke) { line(g, cf, dataset, color, stroke, Dataset.CLOSE); }
    public static void line(Graphics2D g, ChartFrame cf, Dataset dataset, Color color, Stroke stroke, String price) {
        Stroke old = g.getStroke();
        g.setPaint(color);
        if (stroke != null) g.setStroke(stroke);
        Point2D.Double point = null;
        for (int i = 0; i < dataset.getItemCount(); i++) {
            double value = dataset.getPriceValue(i, price);
            if (value != 0) {
                Point2D.Double p = cf.getChartRenderer().valueToJava2D(i, value);
                if (point != null) g.draw(new Line2D.Double(point, p));
                point = p;
            }
        }
        g.setStroke(old);
    }
    public static void line(Graphics2D g, ChartFrame cf, Range range, Rectangle2D.Double bounds, Dataset dataset, Color color, Stroke stroke) { line(g, cf, range, bounds, dataset, color, stroke, Dataset.CLOSE); }
    public static void line(Graphics2D g, ChartFrame cf, Range range, Rectangle2D.Double bounds, Dataset dataset, Color color, Stroke stroke, String price) {
        Stroke old = g.getStroke();
        g.setPaint(color);
        if (stroke != null) g.setStroke(stroke);
        Point2D.Double point = null;
        for (int i = 0; i < dataset.getItemCount(); i++) {
            double value = dataset.getPriceValue(i, price);
            if (value != 0) {
                Point2D.Double p = cf.getChartRenderer().valueToJava2D(i, value, bounds, range);
                if (point != null) g.draw(new Line2D.Double(point, p));
                point = p;
            }
        }
        g.setStroke(old);
    }

    public static void insideFill(Graphics2D g, ChartFrame cf, Dataset upper, Dataset lower, Color color) { insideFill(g, cf, upper, lower, color, Dataset.CLOSE); }
    public static void insideFill(Graphics2D g, ChartFrame cf, Dataset upper, Dataset lower, Color color, String price) {
        g.setPaint(color);
        Point2D.Double point1 = null; Point2D.Double point2 = null;
        for (int i = 0; i < upper.getItemCount(); i++) {
            double value1 = upper.getPriceValue(i, price);
            double value2 = lower.getPriceValue(i, price);
            if (value1 != 0 && value2 != 0) {
                Point2D.Double p1 = cf.getChartRenderer().valueToJava2D(i, value1);
                Point2D.Double p2 = cf.getChartRenderer().valueToJava2D(i, value2);
                if (point1 != null && point2 != null) {
                    GeneralPath gp = new GeneralPath();
                    gp.moveTo((float) point1.x, (float) point1.y);
                    gp.lineTo((float) p1.x, (float) p1.y);
                    gp.lineTo((float) p2.x, (float) p2.y);
                    gp.lineTo((float) point2.x, (float) point2.y);
                    gp.closePath();
                    g.fill(gp);
                }
                point1 = p1; point2 = p2;
            }
        }
    }

    public static void bar(Graphics2D g, ChartFrame cf, Range range, Rectangle2D.Double bounds, Dataset dataset, Color color) { bar(g, cf, range, bounds, dataset, color, Dataset.CLOSE); }
    public static void bar(Graphics2D g, ChartFrame cf, Range range, Rectangle2D.Double bounds, Dataset dataset, Color color, String price) {
        g.setPaint(color);
        Point2D.Double zero = cf.getChartRenderer().valueToJava2D(0, 0, bounds, range);
        for (int i = 0; i < dataset.getItemCount(); i++) {
            double value = dataset.getPriceValue(i, price);
            if (value != 0) {
                Point2D.Double p = cf.getChartRenderer().valueToJava2D(i, value, bounds, range);
                double width = cf.getChartProperties().getBarWidth();
                double height = Math.abs(p.getY() - zero.getY());
                Rectangle2D.Double rect = value > 0 ? new Rectangle2D.Double(p.getX() - (width/2), p.getY(), width, height) : new Rectangle2D.Double(p.getX() - (width/2), p.getY() - height, width, height);
                g.fill(rect);
            }
        }
    }

}
