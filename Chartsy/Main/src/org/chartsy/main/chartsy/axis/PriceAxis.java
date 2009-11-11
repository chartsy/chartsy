package org.chartsy.main.chartsy.axis;

import java.awt.Graphics2D;
import java.awt.font.LineMetrics;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.Vector;
import org.chartsy.main.chartsy.ChartFrame;
import org.chartsy.main.chartsy.chart.Indicator;
import org.chartsy.main.utils.Range;
import org.chartsy.main.utils.RectangleInsets;

/**
 *
 * @author viorel.gheba
 */
public class PriceAxis {

    private PriceAxis() {}

    public static void paint(Graphics2D g, ChartFrame cf) {
        paint(g, cf, cf.getChartRenderer().getChartRange(), cf.getChartRenderer().getChartBounds(), cf.getChartRenderer().getPriceAxisBounds(), false); // paint chart price axis
        for (Indicator i : cf.getChartRenderer().getIndicators()) {
            Range range = i.getRange(cf); // get indicator range
            Rectangle2D.Double bounds = cf.getChartRenderer().getIndicatorBounds(i.getAreaIndex()); // get indicator bounds
            Rectangle2D.Double axisBounds = cf.getChartRenderer().getIndicatorAxisBounds(i.getAreaIndex()); // get indicator axis bounds
            paint(g, cf, range, bounds, axisBounds, true); // paint indicator price axis
        }
    }
    
    public static void paint(Graphics2D g, ChartFrame cf, Range range, Rectangle2D.Double bounds, Rectangle2D.Double axisBounds, boolean indicator) {
        Vector<Double> values = PriceAxisRenderer.values(g, cf, range); // get values
        paint(g, cf, range, bounds, axisBounds, values, indicator);
    }

    public static void paint(Graphics2D g, ChartFrame cf, Range range, Rectangle2D.Double bounds, Rectangle2D.Double axisBounds, Vector<Double> values, boolean indicator) {
        RectangleInsets axisOffset = cf.getChartProperties().getAxisOffset(); // get axis offset
        double axisTick = cf.getChartProperties().getAxisTick(); // get axis tick
        double axisStick = cf.getChartProperties().getAxisPriceStick(); // get axis stick

        g.setPaint(cf.getChartProperties().getBackgroundColor()); // set background color
        g.fill(axisBounds); // paint axis bounds
        g.setPaint(cf.getChartProperties().getAxisColor()); // set axis color
        g.setStroke(cf.getChartProperties().getAxisStroke()); // set axis stroke
        g.draw(new Line2D.Double(axisBounds.getMinX(), axisBounds.getMinY() - axisOffset.top, axisBounds.getMinX(), axisBounds.getMaxY() + axisOffset.bottom)); // axis line
        if (indicator) g.draw(new Line2D.Double(bounds.getMinX() - axisOffset.left, bounds.getMinY() - axisOffset.top, bounds.getMaxX() + axisOffset.right, bounds.getMinY() - axisOffset.top)); // top line

        g.setFont(cf.getChartProperties().getFont()); // set font
        LineMetrics lm = cf.getChartProperties().getFont().getLineMetrics("0123456789", g.getFontRenderContext()); // get line metrics
        DecimalFormat df = new DecimalFormat("#,###.##"); // set decimal format pattern

        for (int i = 0; i < values.size(); i++) {
            double value = values.get(i);
            Point2D.Double point = cf.getChartRenderer().valueToJava2DY(value, axisBounds, range); // get point for value
            if (point != null) {
                if (point.getY() < axisBounds.getMaxY() && point.getY() > axisBounds.getMinY()) {
                    g.setPaint(cf.getChartProperties().getAxisColor()); // set axis color
                    g.draw(new Line2D.Double(point.getX(), point.getY(), point.getX() + axisTick, point.getY())); // paint tick
                    g.setPaint(cf.getChartProperties().getFontColor()); // set font color
                    g.drawString(df.format(value), (float)(point.getX() + axisTick + axisStick), (float)(point.getY() + lm.getDescent())); // paint label
                }
            }
        }

        if (indicator) {
            if (range.getLowerBound() == 0) {
                Point2D.Double point = cf.getChartRenderer().valueToJava2DY(0, axisBounds, range); // get point for zero value
                if (point != null) {
                    g.setPaint(cf.getChartProperties().getAxisColor()); // set axis color
                    g.draw(new Line2D.Double(point.getX(), point.getY(), point.getX() + axisTick, point.getY())); // paint tick
                    g.setPaint(cf.getChartProperties().getFontColor()); // set font color
                    g.drawString(df.format(0), (float)(point.getX() + axisTick + axisStick), (float)(point.getY() + lm.getDescent())); // paint label
                }
            }
        }
    }

}
