package org.chartsy.dots;

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
public class Dots 
        extends Chart
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public Dots()
    {}

    public String getName() 
    { return "Dots"; }

    public void paint(Graphics2D g, ChartFrame cf)
    {
        ChartData cd = cf.getChartData();
        ChartProperties cp = cf.getChartProperties();
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

                double x = cd.getX(i, rect);
                double yClose = cd.getY(close, rect, range, cp.getAxisLogarithmicFlag());

                double dotWidth = cp.getBarWidth() < 4.0d ? cp.getBarWidth() : 4.0d;
                double dotHeight = dotWidth;

                g.setPaint(open > close ? cp.getBarDownColor() : cp.getBarUpColor());
                g.draw(CoordCalc.rectangle(x - dotWidth/2, yClose - dotHeight/2, dotWidth, dotHeight));
            }
        }
    }

}
