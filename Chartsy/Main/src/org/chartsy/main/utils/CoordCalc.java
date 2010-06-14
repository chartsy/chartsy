package org.chartsy.main.utils;

import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author viorel.gheba
 */
public class CoordCalc
{

    protected CoordCalc()
    {}

    public static Line2D.Double line(Point2D p1, Point2D p2)
    {
        return new Line2D.Double(p1, p2);
    }

    public static Line2D.Double line(double x1, double y1, double x2, double y2)
    {
        return new Line2D.Double(x1, y1, x2, y2);
    }

    public static Rectangle growRectangle(Rectangle rectangle, int value)
    {
        Rectangle result = new Rectangle(rectangle);
        result.grow(value, value);
        return result;
    }

    public static Bounds bounds(double x, double y, double width, double height) 
    {
        return new Bounds(x, y, width, height);
    }

    public static Rectangle2D.Double rectangle(double x, double y, double w, double h)
    {
        return new Rectangle2D.Double(x, y, w, h);
    }

}
