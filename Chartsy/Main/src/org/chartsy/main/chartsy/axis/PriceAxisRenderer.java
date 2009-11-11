package org.chartsy.main.chartsy.axis;

import java.awt.Graphics2D;
import java.util.Vector;
import org.chartsy.main.chartsy.ChartFrame;
import org.chartsy.main.utils.Range;

/**
 *
 * @author viorel.gheba
 */
public class PriceAxisRenderer {

    private PriceAxisRenderer() {}

    public static Vector<Double> values(Graphics2D g, ChartFrame cf, Range range) {
        Vector<Double> values = new Vector<Double>();
        double start = Math.floor(range.getLowerBound());
        double diff = range.getUpperBound() - range.getLowerBound();
        double delta = 0;
        if (diff <= 0) return values;
        else if (diff < 0.1d) delta = 0.01d;
        else if (diff < 0.5d) delta = 0.05d;
        else if (diff < 1d) delta = 0.1d;
        else if (diff < 10d) delta = 0.25d;
        else delta = 1d;

        int i = 0;
        while ((cf.getChartRenderer().valueToJava2DY(start).y > cf.getChartRenderer().getChartBounds().getHeight()) && i < 100) { start += delta; i++; }
        int numlines = (int) (cf.getChartRenderer().valueToJava2DY(start).getY() / (g.getFontMetrics().getHeight() * 1.4)) - 1;
        if (numlines <= 1) numlines = 2;
        else if (numlines > 10) numlines = 10;
        start = Math.floor(range.getLowerBound());
        diff = range.getUpperBound() - start;
        double price = diff / (numlines == 2 ? 2.5d : numlines);
        if (price > 2) price = (double)((int)(price + 0.5) / 2 * 2);
        else if (price > 0.75) price = (double)((int)((price + 0.25) * 8) / 2) / 8;
        else price = (double)((int)((price + 0.1) * 20) / 2) / 20;
        price = Math.abs(price);

        for (double grid_i = start; grid_i < range.getUpperBound(); grid_i += price * 2) values.add(new Double(grid_i));
        return values;
    }

}
