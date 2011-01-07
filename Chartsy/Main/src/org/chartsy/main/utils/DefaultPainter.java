package org.chartsy.main.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import org.chartsy.main.ChartFrame;
import org.chartsy.main.data.ChartData;
import org.chartsy.main.data.Dataset;

/**
 *
 * @author viorel.gheba
 */
public final class DefaultPainter {

    private DefaultPainter() {}

    public static void line(Graphics2D g, ChartFrame cf, Range range, Rectangle bounds, Dataset dataset, Color color, Stroke stroke)
    {
        line(g, cf, range, bounds, dataset, color, stroke, Dataset.CLOSE_PRICE);
    }

    public static void line(Graphics2D g, ChartFrame cf, Range range, Rectangle bounds, Dataset dataset, Color color, Stroke stroke, int price)
    {
		boolean isLog = cf.getChartProperties().getAxisLogarithmicFlag();
        Stroke old = g.getStroke();
        g.setPaint(color);
        if (stroke != null) g.setStroke(stroke);
        Point2D.Double point = null;
        for (int i = 0; i < dataset.getItemsCount(); i++)
        {
            if (dataset.getDataItem(i) != null)
            {
                double value = dataset.getPriceAt(i, price);
                double x = cf.getChartData().getX(i, bounds);
                double y = cf.getChartData().getY(value, bounds, range, isLog);

                Point2D.Double p = new Point2D.Double(x, y);
                if (point != null)
                    g.draw(new Line2D.Double(point, p));
                point = p;
            }
        }
        g.setStroke(old);
    }

    public static void bar(Graphics2D g, ChartFrame cf, Range range, Rectangle bounds, Dataset dataset, Color color)
    {
        bar(g, cf, range, bounds, dataset, color, Dataset.CLOSE_PRICE);
    }

    public static void bar(Graphics2D g, ChartFrame cf, Range range, Rectangle bounds, Dataset dataset, Color color, int price)
    {
        g.setPaint(color);
        ChartData cd = cf.getChartData();
        double zeroY = cd.getY(0D, bounds, range, cf.getChartProperties().getAxisLogarithmicFlag());
        for (int i = 0; i < dataset.getItemsCount(); i++)
        {
            if (dataset.getDataItem(i) != null)
            {
                double value = dataset.getPriceAt(i, price);
                double x = cd.getX(i, bounds);
                double y = cd.getY(value, bounds, range, cf.getChartProperties().getAxisLogarithmicFlag());
                
                double width = cf.getChartProperties().getBarWidth();
                double height = Math.abs(y - zeroY);

                if (value > 0)
                {
                    g.fill(CoordCalc.rectangle(x - (width/2), y, width, height));
                }
                else
                {
                    g.fill(CoordCalc.rectangle(x - (width/2), y - height, width, height));
                }
            }
        }
    }

    public static void histogram(Graphics2D g, ChartFrame cf, Range range, Rectangle bounds, Dataset dataset, Color c1, Color c2)
    {
        histogram(g, cf, range, bounds, dataset, c1, c2, Dataset.CLOSE_PRICE);
    }

    public static void histogram(Graphics2D g, ChartFrame cf, Range range, Rectangle bounds, Dataset dataset, Color c1, Color c2, int price)
    {
        ChartData cd = cf.getChartData();
        double zeroY = cd.getY(0D, bounds, range, cf.getChartProperties().getAxisLogarithmicFlag());
        for (int i = 0; i < dataset.getItemsCount(); i++)
        {
            if (dataset.getDataItem(i) != null)
            {
                double value = dataset.getPriceAt(i, price);
                double x = cd.getX(i, bounds);
                double y = cd.getY(value, bounds, range, cf.getChartProperties().getAxisLogarithmicFlag());

                double width = cf.getChartProperties().getBarWidth();
                double height = Math.abs(y - zeroY);

                if (value > 0)
                {
                    g.setColor(c1);
                    g.fill(CoordCalc.rectangle(x - (width/2), y, width, height));
                }
                else
                {
                    g.setColor(c2);
                    g.fill(CoordCalc.rectangle(x - (width/2), y - height, width, height));
                }
            }
        }
    }

    public static void insideFill(Graphics2D g, ChartFrame cf, Range range, Rectangle bounds, Dataset upper, Dataset lower, Color color)
    {
        insideFill(g, cf, range, bounds, upper, lower, color, Dataset.CLOSE_PRICE);
    }

    public static void insideFill(Graphics2D g, ChartFrame cf, Range range, Rectangle bounds, Dataset upper, Dataset lower, Color color, int price) {
        ChartData cd = cf.getChartData();
        g.setPaint(color);
        Point2D.Double point1 = null;
        Point2D.Double point2 = null;

        for (int i = 0; i < upper.getItemsCount(); i++)
        {
            if (upper.getDataItem(i) != null && lower.getDataItem(i) != null)
            {
                double value1 = upper.getPriceAt(i, price);
                double value2 = lower.getPriceAt(i, price);

                double x = cd.getX(i, bounds);
                double y1 = cd.getY(value1, bounds, range, cf.getChartProperties().getAxisLogarithmicFlag());
                double y2 = cd.getY(value2, bounds, range, cf.getChartProperties().getAxisLogarithmicFlag());

                Point2D.Double p1 = new Point2D.Double(x, y1);
                Point2D.Double p2 = new Point2D.Double(x, y2);
                
                if (point1 != null && point2 != null)
                {
                    GeneralPath gp = new GeneralPath();
                    gp.moveTo((float) point1.x, (float) point1.y);
                    gp.lineTo((float) p1.x, (float) p1.y);
                    gp.lineTo((float) p2.x, (float) p2.y);
                    gp.lineTo((float) point2.x, (float) point2.y);
                    gp.closePath();
                    g.fill(gp);
                }
                
                point1 = p1;
                point2 = p2;
            }
        }
    }

    public static void paintFill(Graphics2D g, ChartFrame cf, Range range, Rectangle bounds, Dataset dataset, Color color, double min, double max)
    {
        paintFill(g, cf, range, bounds, dataset, color, min, max, Dataset.CLOSE_PRICE);
    }

    public static void paintFill(Graphics2D g, ChartFrame cf, Range range, Rectangle bounds, Dataset dataset, Color color, double min, double max, int price)
    {
        ChartData cd = cf.getChartData();
        double x;
        double y = cd.getY(min, bounds, range, cf.getChartProperties().getAxisLogarithmicFlag());
        double dx;

        Range r = new Range(min > max ? max : min, min > max ? min : max);

        g.setColor(color);
        for (int i = 1; i < dataset.getItemsCount(); i++)
        {
            if (dataset.getDataItem(i) != null)
            {
                double value1 = dataset.getCloseAt(i-1);
                double value2 = dataset.getCloseAt(i);
                
                if (value1 != 0 && value2 != 0)
                {
                    Point2D p1 = cd.getPoint(i-1, value1, range, bounds, false);
                    Point2D p2 = cd.getPoint(i, value2, range, bounds, false);

                    if (!r.contains(value1) && r.contains(value2))
                    {
                        GeneralPath gp = new GeneralPath();
                        dx = (y - p1.getY())/(p2.getY() - p1.getY());
                        x = p1.getX() + dx*(p2.getX() - p1.getX());
                        gp.moveTo((float)x, (float)y);
                        gp.lineTo((float)p2.getX(), (float)p2.getX());
                        gp.lineTo((float)p2.getX(), (float)y);
                        gp.closePath();
                        g.fill(gp);
                    } 
                    else if (r.contains(value1) && r.contains(value2))
                    {
                        GeneralPath gp = new GeneralPath();
                        gp.moveTo((float)p1.getX(), (float)y);
                        gp.lineTo((float)p1.getX(), (float)p1.getY());
                        gp.lineTo((float)p2.getX(), (float)p2.getY());
                        gp.lineTo((float)p2.getX(), (float)y);
                        gp.closePath();
                        g.fill(gp);
                    } 
                    else if (r.contains(value1) && !r.contains(value2))
                    {
                        GeneralPath gp = new GeneralPath();
                        dx = (y - p1.getY())/(p2.getY() - p1.getY());
                        x = p1.getX() + dx*(p2.getX() - p1.getX());
                        gp.moveTo((float) p1.getX(), (float) p1.getY());
                        gp.lineTo((float) x, (float) y);
                        gp.lineTo((float) p1.getX(), (float) y);
                        gp.closePath();
                        g.fill(gp);
                    }
                }
            }
        }
    }

    public static void dot(Graphics2D g, ChartFrame cf, Range range, Rectangle bounds, Dataset dataset, Color color, Stroke stroke)
    {
        dot(g, cf, range, bounds, dataset, color, stroke, Dataset.CLOSE_PRICE);

    }

    public static void dot(Graphics2D g, ChartFrame cf, Range range, Rectangle bounds, Dataset dataset, Color color, Stroke stroke, int price)
    {
        Stroke old = g.getStroke();
        g.setPaint(color);
        if (stroke != null) g.setStroke(stroke);
        for (int i = 0; i < dataset.getItemsCount(); i++)
        {
            if (dataset.getDataItem(i) != null)
            {
                double value = dataset.getPriceAt(i, price);
                double x = cf.getChartData().getX(i, bounds);
                double y = cf.getChartData().getY(value, bounds, range, cf.getChartProperties().getAxisLogarithmicFlag());

                Ellipse2D.Double circle =  new Ellipse2D.Double(x, y, 5, 5);
                g.fill(circle);
            }
        }
        g.setStroke(old);
    }

}
