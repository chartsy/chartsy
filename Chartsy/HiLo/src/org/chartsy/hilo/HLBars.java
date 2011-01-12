package org.chartsy.hilo;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import org.chartsy.main.ChartFrame;
import org.chartsy.main.ChartProperties;
import org.chartsy.main.chart.Chart;
import org.chartsy.main.data.ChartData;
import org.chartsy.main.data.Dataset;
import org.chartsy.main.utils.CoordCalc;
import org.chartsy.main.utils.Range;
import org.chartsy.main.utils.SerialVersion;

/**
 *
 * @author viorel.gheba
 */
public class HLBars 
        extends Chart
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public HLBars()
    {}

    public String getName()
    { return "HL Bars"; }

    public void paint(Graphics2D g, ChartFrame cf)
    {
        ChartData cd = cf.getChartData();
        ChartProperties cp = cf.getChartProperties();
		boolean isLog = cp.getAxisLogarithmicFlag();
        Rectangle rect = cf.getSplitPanel().getChartPanel().getBounds();
        rect.grow(-2, -2);
        Range range = cf.getSplitPanel().getChartPanel().getRange();

        if (!cd.isVisibleNull())
        {
            Dataset dataset = cd.getVisible();
            for(int i = 0; i < dataset.getItemsCount(); i++)
            {
                double open = dataset.getOpenAt(i);
                double close = dataset.getCloseAt(i);
                double high = dataset.getHighAt(i);
                double low = dataset.getLowAt(i);

                double x = cd.getX(i, rect);
                double yHigh = cd.getY(high, rect, range, isLog);
                double yLow = cd.getY(low, rect, range, isLog);

                double candleWidth = cp.getBarWidth();
                double candleHeight = Math.abs(yHigh - yLow);

                g.setPaint(open > close ? cp.getBarDownColor() : cp.getBarUpColor());
                if (open > close ? cp.getBarDownVisibility() : cp.getBarUpVisibility())
                    g.fill(CoordCalc.rectangle(x - candleWidth / 2, yHigh, candleWidth, candleHeight));
                else
                {
                    g.setStroke(cp.getBarStroke());
                    g.draw(CoordCalc.rectangle(x - candleWidth / 2, yHigh, candleWidth, candleHeight));
                }
            }
        }
    }

}
