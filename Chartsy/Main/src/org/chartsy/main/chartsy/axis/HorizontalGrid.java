package org.chartsy.main.chartsy.axis;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Vector;
import org.chartsy.main.chartsy.ChartFrame;
import org.chartsy.main.chartsy.chart.Indicator;
import org.chartsy.main.utils.Range;
import org.chartsy.main.utils.RectangleInsets;

/**
 *
 * @author viorel.gheba
 */
public class HorizontalGrid {
    
    private HorizontalGrid() {}

    public static void paint(Graphics2D g, ChartFrame cf) {
        paint(g, cf, cf.getChartRenderer().getChartRange(), cf.getChartRenderer().getChartBounds()); // paint chart horizontal grid
        for (Indicator i : cf.getChartRenderer().getIndicators()) {
            Range range = i.getRange(cf); // get indicator range
            Rectangle2D.Double bounds = cf.getChartRenderer().getIndicatorBounds(i.getAreaIndex()); // get indicator bounds
            paint(g, cf, range, bounds); // paint indicator horizontal grid
            if (i.hasZeroLine()) 
                if (range.contains(0))
                    if (i.getZeroLineVisibility())
                        zeroline(g, cf, range, bounds, i.getZeroLineColor(), i.getZeroLineStroke()); // paint indicator zero line
        }
    }

    public static void paint(Graphics2D g, ChartFrame cf, Range range, Rectangle2D.Double bounds) {
        Vector<Double> values = PriceAxisRenderer.values(g, cf, range); // get values
        paint(g, cf, range, bounds, values);
    }
    
    public static void paint(Graphics2D g, ChartFrame cf, Range range, Rectangle2D.Double bounds, Vector<Double> values) {
        if (cf.getChartProperties().getGridHorizontalVisibility()) {
            RectangleInsets axisOffset = cf.getChartProperties().getAxisOffset(); // get axis offset
            g.setPaint(cf.getChartProperties().getGridHorizontalColor()); // set horizontal grid color
            g.setStroke(cf.getChartProperties().getGridHorizontalStroke()); // set horizontal grid stroke
            for (int i = 0; i < values.size(); i++) {
                double value = values.get(i); // set value
                Point2D.Double point = cf.getChartRenderer().valueToJava2DY(value, bounds, range); // get point
                if (point != null) 
                    if (point.getY() > bounds.getMinY() && point.getY() < bounds.getMaxY()) 
                        g.draw(new Line2D.Double(bounds.getMinX() - axisOffset.left, point.getY(), bounds.getMaxX() + axisOffset.right, point.getY())); // draw horizontal line
            }
        }
    }

    public static void zeroline(Graphics2D g, ChartFrame cf, Range range, Rectangle2D.Double bounds, Color color, Stroke stroke) {
        RectangleInsets axisOffset = cf.getChartProperties().getAxisOffset(); // get axis offset
        g.setPaint(color); // set color
        g.setStroke(stroke); // set stroke
        Point2D.Double point = cf.getChartRenderer().valueToJava2DY(0, bounds, range); // get zero point
        if (point != null)
            g.draw(new Line2D.Double(bounds.getMinX() - axisOffset.left, point.getY(), bounds.getMaxX() + axisOffset.right, point.getY())); // paint zero line
    }

}
