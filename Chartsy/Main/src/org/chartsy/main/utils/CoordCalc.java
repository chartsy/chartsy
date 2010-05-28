package org.chartsy.main.utils;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author viorel.gheba
 */
public class CoordCalc {

    protected CoordCalc() {}

    public static Line2D.Double line(Point2D p1, Point2D p2)
    { return new Line2D.Double(p1, p2); }

    public static Line2D.Double line(double x1, double y1, double x2, double y2)
    { return new Line2D.Double(x1, y1, x2, y2); }

    public static Bounds bounds(double x, double y, double width, double height) 
    { return new Bounds(x, y, width, height); }

    public static Rectangle2D.Double rectangle(double x, double y, double w, double h)
    { return new Rectangle2D.Double(x, y, w, h); }

    /*public static double getX(ChartRenderer cr, double index, Bounds bounds) {
        double dx = (bounds.getWidth() / cr.getPeriod()) * index;
        double cx = bounds.getWidth() / (2*cr.getVisibleDataset().getItemsCount());
        return dx + cx;
    }

    public static double getY(ChartRenderer cr, double value, Bounds bounds, Range range) {
        double dif = range.getUpperBound() - range.getLowerBound();
        double percent = ((range.getUpperBound() - value) / dif) * 100;
        double py = bounds.getMinY() + (bounds.getHeight() * percent) / 100;

        if (range.getLowerBound() < 0.0) {
            dif = Math.abs(range.getUpperBound()) + Math.abs(range.getLowerBound());
            double h1 = (Math.abs(range.getUpperBound()) * bounds.getHeight()) / dif;
            double h2 = (Math.abs(range.getLowerBound()) * bounds.getHeight()) / dif;
            if (value >= 0) {
                percent = ((range.getUpperBound() - value) / range.getUpperBound()) * 100;
                py = bounds.getMinY() + (h1 * percent) / 100;
            } else {
                percent = ((Math.abs(range.getLowerBound()) - Math.abs(value)) / Math.abs(range.getLowerBound())) * 100;
                py = bounds.getMinY() + h1 + (h2 - ((h2 * percent) / 100));
            }
        }

        return py;
    }

    public static int getIndex(ChartRenderer cr, double x, Bounds bounds)
    {
        int index = -1;
        int items = cr.getPeriod();
        double w = bounds.getWidth() / items;

        for (int i = 0; i < items; i++)
        {
            Bounds b = new Bounds(bounds.getMinX() + (i * w), 0, w, 10);
            if (b.contains(x, 1))
            {
                index = i;
                break;
            }
        }

        return index;
    }

    public static double getX(ChartRenderer cr, double x) {
        Integer index = null;
        double xc = 0;
        int items = cr.getPeriod();
        int itemsCount = cr.getChartFrame().getDataset().getItemsCount();
        int negativeNr = itemsCount - items - (itemsCount - cr.getLast());
        int positiveNr = itemsCount - negativeNr;
        double w = cr.getChartBounds().getWidth() / items;
        double h = cr.getChartBounds().getHeight();
        double minX = cr.getChartBounds().getMinX();
        boolean negative = (x < minX);
        if (negative) {
            for (int i = 0; i < negativeNr; i++) {
                Rectangle2D.Double rect = new Rectangle2D.Double(minX + (-1 * (i * w)), 0, w, h);
                if (rect.contains(x, 0)) {
                    index = -1 * i;
                    xc = rect.getCenterX();
                    break;
                }
            }
            if (index == null) 
                xc = new Rectangle2D.Double(minX + (-1 * (negativeNr * w)), 0, w, h).getCenterX();
        } else {
            for (int i = 0; i < positiveNr; i++) {
                Rectangle2D.Double rect = new Rectangle2D.Double(minX + (i * w), 0, w, h);
                if (rect.contains(x, 0)) {
                    index = i;
                    xc = rect.getCenterX();
                    break;
                }
            }
            if (index == null) 
                xc = new Rectangle2D.Double(minX + ((positiveNr -1 ) * w), 0, w, h).getCenterX();
        }
        return xc;
    }

    public static long xToLong(ChartRenderer cr, double x) {
        Integer index = null;
        int items = cr.getPeriod(); int end = cr.getLast();
        int itemsCount = cr.getChartFrame().getDataset().getItemsCount();
        int negativeNr = itemsCount - items - (itemsCount - end);
        int positiveNr = itemsCount - negativeNr;
        double w = cr.getChartBounds().getWidth() / items;
        double h = cr.getChartBounds().getHeight();
        double minX = cr.getChartBounds().getMinX();
        boolean negative = (x < minX);
        if (negative) {
            for (int i = 0; i < negativeNr; i++) {
                Rectangle2D.Double rect = new Rectangle2D.Double(minX + (-1 * (i * w)), 0, w, h);
                if (rect.contains(x, 0)) {
                    index = negativeNr - i;
                    break;
                }
            }
            if (index == null) index = 0;
        } else {
            for (int i = 0; i < positiveNr; i++) {
                Rectangle2D.Double rect = new Rectangle2D.Double(minX + (i * w), 0, w, h);
                if (rect.contains(x, 0)) {
                    index = negativeNr + i;
                    break;
                }
            }
            if (index == null) index = itemsCount - 1;
        }
        return cr.getChartFrame().getDataset().getTimeAt(index);
    }

    public static Integer longIndex(ChartRenderer cr, long t) {
        Integer index = null;
        Dataset dataset = cr.getChartFrame().getDataset();
        int itemsCount = dataset.getItemsCount();
        if (cr.getInterval() instanceof DailyInterval) {
            for (int i = 0; i < itemsCount; i++) {
                long l = dataset.getTimeAt(i);
                if (l == t) { index = i; break; }
            }
        } else if (cr.getInterval() instanceof WeeklyInterval) {
            Calendar c1 = Calendar.getInstance();
            Calendar c2 = Calendar.getInstance();
            c1.setTimeInMillis(t);
            for (int i = 0; i < itemsCount; i++) {
                long l = dataset.getTimeAt(i);
                c2.setTimeInMillis(l);
                if (c1.get(Calendar.WEEK_OF_YEAR) == c2.get(Calendar.WEEK_OF_YEAR) && c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)) {
                    index = i; break;
                }
            }
        } else if (cr.getInterval() instanceof MonthlyInterval) {
            Calendar c1 = Calendar.getInstance();
            Calendar c2 = Calendar.getInstance();
            c1.setTimeInMillis(t);
            for (int i = 0; i < itemsCount; i++) {
                long l = dataset.getTimeAt(i);
                c2.setTimeInMillis(l);
                if (c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) && c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)) {
                    index = i; break;
                }
            }
        } else {
            for (int i = 0; i < itemsCount; i++) {
                long l = dataset.getTimeAt(i);
                if (l == t) { index = i; break; }
            }
        }
        if (index == null) {
            long min = dataset.getTimeAt(0);
            long max = dataset.getTimeAt(itemsCount - 1);
            if (t < min) index = 0;
            else if (t > max) index = itemsCount - 1;
        }
        return index;
    }

    public static double longToX(ChartRenderer cr, long t) {
        double xc = 0;
        Integer index = longIndex(cr, t);
        if (index != null) {
            int items = cr.getPeriod();
            int end = cr.getLast();

            int itemsCount = cr.getChartFrame().getDataset().getItemsCount();
            int negativeNr = itemsCount - items - (itemsCount - end);

            double w = cr.getChartBounds().getWidth() / items;
            double h = cr.getChartBounds().getHeight();
            double minX = cr.getChartBounds().getMinX();

            boolean negative = (index < negativeNr);

            if (negative)
                    xc = new Rectangle2D.Double(minX + ((index - negativeNr) * w), 0, w, h).getCenterX();
            else 
                xc = new Rectangle2D.Double(minX + ((index - negativeNr) * w), 0, w, h).getCenterX();

            return xc;
        }
        return 0;
    }
    
    public static boolean hasPrevT(ChartRenderer cr, long t) {
        int index = longIndex(cr, t);
        return (index - 1) >= 0;
    }

    public static long getPrevT(ChartRenderer cr, long t) {
        int index = longIndex(cr, t);
        return cr.getChartFrame().getDataset().getTimeAt(index - 1);
    }

    public static boolean hasNextT(ChartRenderer cr, long t) {
        int index = longIndex(cr, t);
        int itemsCount = cr.getChartFrame().getDataset().getItemsCount();
        return (index + 1) < itemsCount;
    }

    public static long getNextT(ChartRenderer cr, long t) {
        int index = longIndex(cr, t);
        return cr.getChartFrame().getDataset().getTimeAt(index + 1);
    }

    public static double yToValue(ChartRenderer cr, double y, Bounds bounds, Range range) {
        if (bounds != null && range != null) {
            double dif = range.getUpperBound() - range.getLowerBound();
            double percent = (y - bounds.getMinY()) / bounds.getHeight();
            double value = range.getUpperBound() - (percent * dif);

            Point2D.Double zero = range.contains(0) ? valueToJava2D(cr, 0, 0, bounds, range) : null;
            if (zero != null) {
                dif = Math.abs(range.getUpperBound()) + Math.abs(range.getLowerBound());

                double h1 = (Math.abs(range.getUpperBound()) * bounds.getHeight()) / dif;
                double h2 = (Math.abs(range.getLowerBound()) * bounds.getHeight()) / dif;

                if (y < zero.getY()) { // positiv
                    percent = 100 * (y - bounds.getMinY()) / h1;
                    value = range.getUpperBound() - ((percent / 100) * range.getUpperBound());
                } else { // negativ
                    percent = 100 * (h2 - y + bounds.getMinY() + h1) / h2;
                    value = Math.abs(range.getLowerBound()) - (percent * Math.abs(range.getLowerBound()) / 100);
                    value = value > 0 ? value * (-1) : value;
                }
            }
            return value;
        }
        return 0;
    }

    public static double valueToY(ChartRenderer cr, double value, Rectangle2D.Double bounds, Range range) {
        if (bounds != null && range != null) {
            double dif = range.getUpperBound() - range.getLowerBound();
            double percent = ((range.getUpperBound() - value) / dif) * 100;
            double py = bounds.getMinY() + (bounds.getHeight() * percent) / 100;

            if (range.contains(0)) {
                dif = Math.abs(range.getUpperBound()) + Math.abs(range.getLowerBound());

                double h1 = (Math.abs(range.getUpperBound()) * bounds.getHeight()) / dif;
                double h2 = (Math.abs(range.getLowerBound()) * bounds.getHeight()) / dif;

                if (value >= 0) {
                    percent = ((range.getUpperBound() - value) / range.getUpperBound()) * 100;
                    py = bounds.getMinY() + (h1 * percent) / 100;
                } else {
                    percent = ((Math.abs(range.getLowerBound()) - Math.abs(value)) / Math.abs(range.getLowerBound())) * 100;
                    py = bounds.getMinY() + h1 + (h2 - ((h2 * percent) / 100));
                }
            }
            return py;
        }
        return 0;
    }

    public static Point2D.Double valueToJava2D(ChartRenderer cr, double xvalue, double yvalue, Bounds bounds, Range range) {
        double x = (bounds.getWidth() / cr.getPeriod()) * xvalue;
        double c = bounds.getWidth() / (2 * cr.getVisibleDataset().getItemsCount());
        double px = bounds.getMinX() + x + c;

        double dif = range.getUpperBound() - range.getLowerBound();
        double percent = ((range.getUpperBound() - yvalue) / dif) * 100;
        double py = bounds.getMinY() + (bounds.getHeight() * percent) / 100;

        if (range.contains(0)) {
            dif = Math.abs(range.getUpperBound()) + Math.abs(range.getLowerBound());

            double h1 = (Math.abs(range.getUpperBound()) * bounds.getHeight()) / dif;
            double h2 = (Math.abs(range.getLowerBound()) * bounds.getHeight()) / dif;

            if (yvalue >= 0) {
                percent = ((range.getUpperBound() - yvalue) / range.getUpperBound()) * 100;
                py = bounds.getMinY() + (h1 * percent) / 100;
            } else {
                percent = ((Math.abs(range.getLowerBound()) - Math.abs(yvalue)) / Math.abs(range.getLowerBound())) * 100;
                py = bounds.getMinY() + h1 + (h2 - ((h2 * percent) / 100));
            }
        }

        Point2D.Double p = new Point2D.Double(px, py);

        return p;
    }*/

}
