package org.chartsy.main.utils;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Calendar;
import org.chartsy.main.chartsy.ChartRenderer;
import org.chartsy.main.dataset.Dataset;
import org.chartsy.main.managers.DatasetManager;

/**
 *
 * @author viorel.gheba
 */
public class CoordCalc {

    protected CoordCalc() {}

    protected static Rectangle2D.Double rectangle(double x, double y, double w, double h) { return new Rectangle2D.Double(x, y, w, h); }
    protected static Bounds bounds(double x, double y, double w, double h) { return new Bounds(x, y, w, h); }

    public static double getX(ChartRenderer cr, double x) {
        Integer index = null;
        double xc = 0;
        int items = cr.getItems();
        int itemsCount = cr.getMainDataset().getItemCount();
        int negativeNr = itemsCount - items - (itemsCount - cr.getEnd());
        int positiveNr = itemsCount - negativeNr;
        double w = cr.getChartBounds().getWidth() / items;
        double h = cr.getHeight();
        double minX = cr.getChartBounds().getMinX();
        boolean negative = (x < minX);
        if (negative) {
            for (int i = 0; i < negativeNr; i++) {
                Bounds rect = bounds(minX + (-1 * (i * w)), 0, w, h);
                if (rect.contains(x, 0)) {
                    index = -1 * i;
                    xc = rect.getCenterX();
                    break;
                }
            }
            if (index == null) xc = bounds(minX + (-1 * (negativeNr * w)), 0, w, h).getCenterX();
        } else {
            for (int i = 0; i < positiveNr; i++) {
                Bounds rect = bounds(minX + (i * w), 0, w, h);
                if (rect.contains(x, 0)) {
                    index = i;
                    xc = rect.getCenterX();
                    break;
                }
            }
            if (index == null) xc = bounds(minX + ((positiveNr -1 ) * w), 0, w, h).getCenterX();
        }
        return xc;
    }

    public static long xToLong(ChartRenderer cr, double x) {
        Integer index = null;
        int items = cr.getItems(); int end = cr.getEnd();
        int itemsCount = cr.getMainDataset().getItemCount();
        int negativeNr = itemsCount - items - (itemsCount - end);
        int positiveNr = itemsCount - negativeNr;
        double w = cr.getChartBounds().getWidth() / items;
        double h = cr.getHeight();
        double minX = cr.getChartBounds().getMinX();
        boolean negative = (x < minX);
        if (negative) {
            for (int i = 0; i < negativeNr; i++) {
                Bounds rect = bounds(minX + (-1 * (i * w)), 0, w, h);
                if (rect.contains(x, 0)) {
                    index = negativeNr - i;
                    break;
                }
            }
            if (index == null) index = 0;
        } else {
            for (int i = 0; i < positiveNr; i++) {
                Bounds rect = bounds(minX + (i * w), 0, w, h);
                if (rect.contains(x, 0)) {
                    index = negativeNr + i;
                    break;
                }
            }
            if (index == null) index = itemsCount - 1;
        }
        return cr.getMainDataset().getDate(index).getTime();
    }

    public static Integer longIndex(ChartRenderer cr, long t) {
        Integer index = null;
        Dataset dataset = cr.getMainDataset();
        int itemsCount = dataset.getItemCount();
        if (cr.getTime().equals(DatasetManager.DAILY)) {
            for (int i = 0; i < itemsCount; i++) {
                long l = dataset.getDate(i).getTime();
                if (l == t) { index = i; break; }
            }
        } else if (cr.getTime().equals(DatasetManager.WEEKLY)) {
            Calendar c1 = Calendar.getInstance();
            Calendar c2 = Calendar.getInstance();
            c1.setTimeInMillis(t);
            for (int i = 0; i < itemsCount; i++) {
                long l = dataset.getDate(i).getTime();
                c2.setTimeInMillis(l);
                if (c1.get(Calendar.WEEK_OF_YEAR) == c2.get(Calendar.WEEK_OF_YEAR) && c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)) {
                    index = i; break;
                }
            }
        } else if (cr.getTime().equals(DatasetManager.MONTHLY)) {
            Calendar c1 = Calendar.getInstance();
            Calendar c2 = Calendar.getInstance();
            c1.setTimeInMillis(t);
            for (int i = 0; i < itemsCount; i++) {
                long l = dataset.getDate(i).getTime();
                c2.setTimeInMillis(l);
                if (c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) && c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)) {
                    index = i; break;
                }
            }
        } else {
            for (int i = 0; i < itemsCount; i++) {
                long l = dataset.getDate(i).getTime();
                if (l == t) { index = i; break; }
            }
        }
        if (index == null) {
            long min = dataset.getDate(0).getTime();
            long max = dataset.getDate(itemsCount - 1).getTime();
            if (t < min) index = 0;
            else if (t > max) index = itemsCount - 1;
        }
        return index;
    }

    public static double longToX(ChartRenderer cr, long t) {
        double xc = 0;
        Integer index = longIndex(cr, t);
        if (index != null) {
            int items = cr.getItems();
            int end = cr.getEnd();

            int itemsCount = cr.getMainDataset().getItemCount();
            int negativeNr = itemsCount - items - (itemsCount - end);

            double w = cr.getChartBounds().getWidth() / items;
            double h = cr.getHeight();
            double minX = cr.getChartBounds().getMinX();

            boolean negative = (index < negativeNr);

            if (negative) xc = bounds(minX + ((index - negativeNr) * w), 0, w, h).getCenterX();
            else xc = bounds(minX + ((index - negativeNr) * w), 0, w, h).getCenterX();

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
        return cr.getMainDataset().getDate(index - 1).getTime();
    }

    public static boolean hasNextT(ChartRenderer cr, long t) {
        int index = longIndex(cr, t);
        int itemsCount = cr.getMainDataset().getItemCount();
        return (index + 1) <= itemsCount;
    }

    public static long getNextT(ChartRenderer cr, long t) {
        int index = longIndex(cr, t);
        return cr.getMainDataset().getDate(index + 1).getTime();
    }

    public static double yToValue(ChartRenderer cr, double y, Rectangle2D.Double bounds, Range range) {
        if (bounds != null && range != null) {
            double dif = range.getUpperBound() - range.getLowerBound();
            double percent = (y - bounds.getMinY()) / bounds.getHeight();
            double value = range.getUpperBound() - (percent * dif);

            Point2D.Double zero = range.contains(0) ? cr.valueToJava2D(0, 0, bounds, range) : null;
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

}
