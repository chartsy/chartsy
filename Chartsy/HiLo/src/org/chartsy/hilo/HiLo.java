package org.chartsy.hilo;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import org.chartsy.main.chartsy.ChartFrame;
import org.chartsy.main.chartsy.chart.AbstractChart;
import org.chartsy.main.dataset.Dataset;

/**
 *
 * @author viorel.gheba
 */
public class HiLo extends AbstractChart implements Serializable {

    private static final long serialVersionUID = 101L;

    public HiLo() { super("HiLo", "Description"); }

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
            double width = cf.getChartProperties().getBarWidth();
            double height = Math.abs(pHigh.getY() - pLow.getY());
            Rectangle2D.Double rect = new Rectangle2D.Double(pHigh.getX() - width / 2, pHigh.getY(), width, height);
            g.fill(rect);
        }
    }

}
