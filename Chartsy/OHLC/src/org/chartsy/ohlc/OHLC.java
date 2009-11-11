package org.chartsy.ohlc;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import org.chartsy.main.chartsy.ChartFrame;
import org.chartsy.main.chartsy.chart.AbstractChart;
import org.chartsy.main.dataset.Dataset;

/**
 *
 * @author viorel.gheba
 */
public class OHLC extends AbstractChart {

    public OHLC() { super("OHLC", "Description"); }

    public void paint(Graphics2D g, ChartFrame cf) {
        Dataset dataset = cf.getChartRenderer().getVisibleDataset();
        for(int i = 0; i < dataset.getItemCount(); i++) {
            double open = dataset.getOpenValue(i);
            double close = dataset.getCloseValue(i);
            double high = dataset.getHighValue(i);
            double low = dataset.getLowValue(i);
            g.setPaint(open > close ? cf.getChartProperties().getBarDownColor() : cf.getChartProperties().getBarUpColor());
            Point2D.Double pHigh = cf.getChartRenderer().valueToJava2D(i, high);
            Point2D.Double pLow = cf.getChartRenderer().valueToJava2D(i, low);
            Point2D.Double pOpen = cf.getChartRenderer().valueToJava2D(i, open);
            Point2D.Double pClose = cf.getChartRenderer().valueToJava2D(i, close);
            g.draw(new Line2D.Double(pHigh, pLow));
            g.draw(new Line2D.Double(pOpen, new Point2D.Double(pOpen.getX() - cf.getChartProperties().getBarWidth() / 2, pOpen.getY())));
            g.draw(new Line2D.Double(pClose, new Point2D.Double(pClose.getX() + cf.getChartProperties().getBarWidth() / 2, pClose.getY())));
        }
    }

}
