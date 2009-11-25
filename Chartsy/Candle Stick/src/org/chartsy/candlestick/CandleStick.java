package org.chartsy.candlestick;

import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import org.chartsy.main.chartsy.ChartFrame;
import org.chartsy.main.chartsy.ChartProperties;
import org.chartsy.main.chartsy.ChartRenderer;
import org.chartsy.main.chartsy.chart.AbstractChart;
import org.chartsy.main.dataset.Dataset;

/**
 *
 * @author viorel.gheba
 */
public class CandleStick extends AbstractChart implements Serializable {

    private static final long serialVersionUID = 101L;

    public CandleStick() { super("Candle Stick", "Description"); }

    public void paint(Graphics2D g, ChartFrame chartFrame) {
        ChartProperties cp = chartFrame.getChartProperties();
        ChartRenderer cr = chartFrame.getChartRenderer();

        Stroke stroke = g.getStroke();

        g.setStroke(cp.getBarStroke());
        Dataset dataset = cr.getVisibleDataset();
        if (dataset != null) {
            for(int i = 0; i < dataset.getItemCount(); i++) {

                double open = dataset.getOpenValue(i);
                double close = dataset.getCloseValue(i);
                double high = dataset.getHighValue(i);
                double low = dataset.getLowValue(i);

                Point2D.Double pOpen = cr.valueToJava2D(i, open);
                Point2D.Double pClose = cr.valueToJava2D(i, close);
                Point2D.Double pHigh = cr.valueToJava2D(i, high);
                Point2D.Double pLow = cr.valueToJava2D(i, low);

                double candleWidth = cp.getBarWidth();
                double candleHeight = Math.abs(pOpen.getY() - pClose.getY());

                if (open > close ? cp.getBarDownVisibility() : cp.getBarUpVisibility()) {
                    g.setPaint(open > close ? cp.getBarDownColor() : cp.getBarUpColor());
                    g.fill(new Rectangle2D.Double((open > close ? pOpen.getX() : pClose.getX()) - candleWidth/2, (open > close ? pOpen.getY() : pClose.getY()), candleWidth, candleHeight));
                }

                if (cp.getBarVisibility()) {
                    g.setPaint(cp.getBarColor());
                    g.draw(new Rectangle2D.Double((open > close ? pOpen.getX() : pClose.getX()) - candleWidth/2, (open > close ? pOpen.getY() : pClose.getY()), candleWidth, candleHeight));
                    g.draw(new Line2D.Double(pHigh, (open > close ? pOpen : pClose)));
                    g.draw(new Line2D.Double((open > close ? pClose : pOpen), pLow));
                }
            }
        }

        g.setStroke(stroke);
    }

}
