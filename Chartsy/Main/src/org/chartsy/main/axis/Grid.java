package org.chartsy.main.axis;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.Serializable;
import javax.swing.JPanel;
import org.chartsy.main.ChartFrame;
import org.chartsy.main.ChartProperties;
import org.chartsy.main.IndicatorPanel;
import org.chartsy.main.data.ChartData;
import org.chartsy.main.utils.CoordCalc;
import org.chartsy.main.utils.GraphicsUtils;
import org.chartsy.main.utils.Range;
import org.chartsy.main.utils.SerialVersion;

/**
 *
 * @author viorel.gheba
 */
public class Grid extends JPanel implements Serializable
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;
    private ChartFrame chartFrame;

    public Grid(ChartFrame frame)
    {
        chartFrame = frame;
		setOpaque(false);
		setDoubleBuffered(true);
    }

	@Override
	public void paint(Graphics g)
	{
		Graphics2D g2 = GraphicsUtils.prepareGraphics(g);

		ChartData cd = chartFrame.getChartData();
        ChartProperties cp = chartFrame.getChartProperties();
		boolean isLog = cp.getAxisLogarithmicFlag();

        if (!cd.isVisibleNull() && !cd.getVisible().isEmpty())
        {
            Rectangle chartBounds = chartFrame.getSplitPanel().getChartPanel().getBounds();
            chartBounds.grow(-2, -2);
            Range chartRange = chartFrame.getSplitPanel().getChartPanel().getRange();
            double x, y;

            // Vertical Grid
            if (cp.getGridVerticalVisibility())
            {
                g2.setColor(cp.getGridVerticalColor());
                g2.setStroke(cp.getGridVerticalStroke());
				double[] list = cd.getDateValues();
				boolean firstFlag = true;
                for (int i = 0; i < list.length; i++)
                {
					double value = list[i];
					if (value != -1)
					{
						x = cd.getX(value, chartBounds);
						if (firstFlag)
						{
							int index = (int) value;
							long time = cd.getVisible().getTimeAt(index);
							if (cd.isFirstWorkingDayOfMonth(time))
								g2.draw(CoordCalc.line(x, 0, x, getHeight()));
							firstFlag = false;
						} else
						{
							g2.draw(CoordCalc.line(x, 0, x, getHeight()));
						}
					}
                }
            }

            // Horizontal Grid
            if (cp.getGridHorizontalVisibility())
            {
                // paint grid for chart
                g2.setColor(cp.getGridHorizontalColor());
                g2.setStroke(cp.getGridHorizontalStroke());
				FontMetrics fm = getFontMetrics(chartFrame.getChartProperties().getFont());
				double[] list = cd.getYValues(chartBounds, chartRange, fm.getHeight());
                for (int i = 0; i < list.length; i++)
                {
					double value = list[i];
                    y = cd.getY(value, chartBounds, chartRange, isLog);
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
                        g2.setColor(cp.getGridHorizontalColor());
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
                                    y = cd.getY(d, indicatorBounds, indicatorRange, false);
                                    if (indicatorBounds.contains(2, y))
                                    {
                                        g2.draw(CoordCalc.line(0, y, getWidth(), y));
                                    }
                                }
                                values = null;

                                if (panel.getIndicator().hasZeroLine())
                                {
                                    if (panel.getIndicator().getZeroLineVisibility())
                                    {
                                        y = cd.getY(0, indicatorBounds, indicatorRange, false);
                                        g2.setColor(panel.getIndicator().getZeroLineColor());
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
                                            y = cd.getY(d, indicatorBounds, indicatorRange, false);
                                            g2.setColor(panel.getIndicator().getDelimitersColor());
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
	}

	@Override
	public void update(Graphics g)
	{
		paint(g);
	}

}
