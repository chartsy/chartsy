package org.chartsy.main.axis;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.List;
import org.chartsy.main.AbstractComponent;
import org.chartsy.main.ChartFrame;
import org.chartsy.main.ChartProperties;
import org.chartsy.main.IndicatorPanel;
import org.chartsy.main.data.ChartData;
import org.chartsy.main.utils.CoordCalc;
import org.chartsy.main.utils.Range;

/**
 *
 * @author viorel.gheba
 */
public class Grid extends AbstractComponent
{

    private static final long serialVersionUID = 2L;
    private transient ChartFrame chartFrame;

    public Grid(ChartFrame frame)
    {
        chartFrame = frame;
        setOpaque(false);
    }

    protected void paintAbstractComponent(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g.create();
        setDoubleBuffered(true);

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

        g2.setPaintMode();

        ChartData cd = chartFrame.getChartData();
        ChartProperties cp = chartFrame.getChartProperties();

        if (!cd.isVisibleNull())
        {
            Rectangle chartBounds = chartFrame.getSplitPanel().getChartPanel().getBounds();
            chartBounds.grow(-2, -2);
            Range chartRange = chartFrame.getSplitPanel().getChartPanel().getRange();
            double x, y;

            // Vertical Grid
            if (cp.getGridVerticalVisibility())
            {
                g2.setPaint(cp.getGridVerticalColor());
                g2.setStroke(cp.getGridVerticalStroke());
                List<Double> list = cd.getDateValues();
                for (int i = 0; i < list.size(); i++)
                {
                    x = cd.getX(list.get(i), chartBounds);
                    g2.draw(CoordCalc.line(x, 0, x, getHeight()));
                }
            }

            // Horizontal Grid
            if (cp.getGridHorizontalVisibility())
            {
                // paint grid for chart
                g2.setPaint(cp.getGridHorizontalColor());
                g2.setStroke(cp.getGridHorizontalStroke());
                List<Double> list = cd.getPriceValues(chartBounds, chartRange);
                for (int i = 0; i < list.size(); i++)
                {
                    y = cd.getY(list.get(i), chartBounds, chartRange);
                    if (chartBounds.contains(2, y))
                    {
                        g2.draw(CoordCalc.line(0, y, getWidth(), y));
                    }
                }

                chartBounds.grow(2, 2);
                double hy = chartBounds.getHeight();

                // Indicators Horizontal Grid
                if (chartFrame.getSplitPanel().getIndicatorsPanel().getIndicatorsCount() > 0)
                {
                    int ind = 0;
                    for (IndicatorPanel panel : chartFrame.getSplitPanel().getIndicatorsPanel().getIndicatorPanels())
                    {
                        g2.translate(0, hy);
                        g2.setPaint(cp.getGridHorizontalColor());
                        g2.setStroke(cp.getGridHorizontalStroke());

                        if (panel.isMaximized())
                        {
                            Rectangle indicatorBounds = panel.getBounds();
                            indicatorBounds.grow(-2, -2);
                            Range indicatorRange = panel.getIndicator().getRange(chartFrame);

                            if (panel.getIndicator().paintValues())
                            {
                                Double[] values = panel.getIndicator().getPriceValues(chartFrame);
                                for (Double d : values)
                                {
                                    y = cd.getY(d, indicatorBounds, indicatorRange);
                                    if (indicatorBounds.contains(2, y))
                                    {
                                        g2.draw(CoordCalc.line(0, y, getWidth(), y));
                                    }
                                }

                                if (panel.getIndicator().hasZeroLine())
                                {
                                    if (panel.getIndicator().getZeroLineVisibility())
                                    {
                                        y = cd.getY(0, indicatorBounds, indicatorRange);
                                        g2.setPaint(panel.getIndicator().getZeroLineColor());
                                        g2.setStroke(panel.getIndicator().getZeroLineStroke());
                                        g2.draw(CoordCalc.line(0, y, getWidth(), y));
                                    }
                                }

                                if (panel.getIndicator().hasDelimiters())
                                {
                                    if (panel.getIndicator().getDelimitersVisibility())
                                    {
                                        for (double d : panel.getIndicator().getDelimitersValues())
                                        {
                                            y = cd.getY(d, indicatorBounds, indicatorRange);
                                            g2.setPaint(panel.getIndicator().getDelimitersColor());
                                            g2.setStroke(panel.getIndicator().getDelimitersStroke());
                                            g2.draw(CoordCalc.line(0, y, getWidth(), y));
                                        }
                                    }
                                }
                            }

                            indicatorBounds.grow(2, 2);
                            hy = indicatorBounds.getHeight();
                        }
                        else
                        {
                            hy = panel.getPanelHeight();
                        }
                        ind++;
                    }
                }
            }
        }
        g2.dispose();
    }

}
