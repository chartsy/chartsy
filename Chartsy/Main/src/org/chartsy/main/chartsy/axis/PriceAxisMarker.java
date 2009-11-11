package org.chartsy.main.chartsy.axis;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import org.chartsy.main.chartsy.ChartFrame;
import org.chartsy.main.chartsy.chart.Indicator;
import org.chartsy.main.chartsy.chart.Overlay;
import org.chartsy.main.dataset.Dataset;
import org.chartsy.main.utils.Range;
import org.chartsy.main.utils.RectangleInsets;

/**
 *
 * @author viorel.gheba
 */
public class PriceAxisMarker {

    private PriceAxisMarker() {}

    public static void paint(Graphics2D g, ChartFrame cf) {
        double open = cf.getChartRenderer().getVisibleDataset().getLastPriceValue(Dataset.OPEN);
        double close = cf.getChartRenderer().getVisibleDataset().getLastPriceValue(Dataset.CLOSE);
        paint(g, cf, cf.getChartRenderer().getChartRange(), cf.getChartRenderer().getPriceAxisBounds(), close, open > close ? cf.getChartProperties().getBarDownColor() : cf.getChartProperties().getBarUpColor());
        for (Overlay o : cf.getChartRenderer().getOverlays()) {
            if (o.getMarkerVisibility()) {
                double[] values = o.getValues(cf);
                if (values.length > 0) {
                    Color[] colors = o.getColors();
                    for (int j = 0; j < values.length; j++) if (values[j] != 0)
                        paint(g, cf, cf.getChartRenderer().getChartRange(), cf.getChartRenderer().getPriceAxisBounds(), values[j], colors[j]);
                }
            }
        }
        for (Indicator i : cf.getChartRenderer().getIndicators()) {
            if (i.getMarkerVisibility()) {
                double[] values = i.getValues(cf);
                if (values.length > 0) {
                    Range range = i.getRange(cf);
                    Rectangle2D.Double axisBounds = cf.getChartRenderer().getIndicatorAxisBounds(i.getAreaIndex());
                    Color[] colors = i.getColors();
                    for (int j = 0; j < values.length; j++) 
                        if (values[j] != 0)
                            paint(g, cf, range, axisBounds, values[j], colors[j]);
                }
            }
        }
    }

    public static void paint(Graphics2D g, ChartFrame cf, Range range, Rectangle2D.Double bounds, double value, Color color) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        RectangleInsets dataOffset = cf.getChartProperties().getDataOffset();
        FontRenderContext frc = g.getFontRenderContext();
        FontMetrics fm = g.getFontMetrics();

        g.setPaint(color);
        Point2D.Double point = cf.getChartRenderer().valueToJava2DY(value, bounds, range);

        double w = dataOffset.right - 6;
        double h = fm.getHeight() + 4;
        double x = bounds.getMinX() + 1;
        double y = point.y;

        GeneralPath gp = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 5);
        gp.moveTo(x, y);
        gp.lineTo(x + 6, y - h/2);
        gp.lineTo(x + w + 8, y - h/2);
        gp.lineTo(x + w + 8, y + h/2);
        gp.lineTo(x + 6, y + h/2);
        gp.closePath();
        g.fill(gp);

        g.setPaint(new Color(0xffffff));
        g.drawString(df.format(value), (float)(x + 6 + 1), (float)(y + fm.getDescent()));
    }
    
}
