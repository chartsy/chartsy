package org.chartsy.lineonclose;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.Serializable;
import org.chartsy.main.ChartFrame;
import org.chartsy.main.ChartProperties;
import org.chartsy.main.chart.Chart;
import org.chartsy.main.data.ChartData;
import org.chartsy.main.data.Dataset;
import org.chartsy.main.utils.DefaultPainter;
import org.chartsy.main.utils.Range;

/**
 *
 * @author viorel.gheba
 */
public class LineOnClose 
        extends Chart
        implements Serializable
{

    private static final long serialVersionUID = 2L;

    public LineOnClose()
    {}

    public String getName()
    { return "Line On Close"; }

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
            DefaultPainter.line(g, cf, range, rect, dataset, cp.getBarUpColor(), null);
        }
    }

}
