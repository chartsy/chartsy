package org.chartsy.main.axis;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.io.Serializable;
import java.util.Calendar;
import javax.swing.JPanel;
import org.chartsy.main.ChartFrame;
import org.chartsy.main.ChartProperties;
import org.chartsy.main.data.ChartData;
import org.chartsy.main.data.Dataset;
import org.chartsy.main.intervals.Interval;
import org.chartsy.main.intervals.WeeklyInterval;
import org.chartsy.main.utils.CoordCalc;
import org.chartsy.main.utils.GraphicsUtils;
import org.chartsy.main.utils.SerialVersion;
import org.openide.util.NbBundle;

/**
 *
 * @author viorel.gheba
 */
public class DateAxis extends JPanel implements Serializable
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;
    private ChartFrame chartFrame;

    public DateAxis(ChartFrame frame)
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

		String copy = NbBundle.getMessage(ChartFrame.class, "LBL_Copyright", Integer.toString(Calendar.getInstance().get(Calendar.YEAR)));
		g2.setColor(chartFrame.getChartProperties().getFontColor());
		g2.setFont(chartFrame.getChartProperties().getFont().deriveFont(Font.BOLD));
		g2.drawString(copy, 0, getHeight() - 5);

        if (!cd.isVisibleNull() && !cd.getVisible().isEmpty())
        {
            Rectangle bounds = chartFrame.getSplitPanel().getChartPanel().getBounds();
            Dataset dataset = cd.getVisible();
            Interval interval = cd.getInterval();

            g2.setFont(cp.getFont());
            g2.setColor(cp.getAxisColor());
            g2.setStroke(cp.getAxisStroke());
            g2.drawLine(0, 0, bounds.width, 0);

			bounds.grow(-2, -2);

            String[] months = cp.getMonths();
            FontRenderContext frc = g2.getFontRenderContext();
            LineMetrics lm = cp.getFont().getLineMetrics("0123456789/", g2.getFontRenderContext());

			Calendar calendar = Calendar.getInstance();
			double[] list = cd.getDateValues();
            if (!interval.isIntraDay())
            {
				boolean firstFlag = true;
				for (int j = 0; j < list.length; j++)
				{
					double value = list[j];
					if (value != -1)
					{
						double x = cd.getX(value, bounds);

						calendar.setTimeInMillis(dataset.getTimeAt(j));
						String string = months[calendar.get(Calendar.MONTH)];
						if (string.isEmpty())
						{
							string = String.valueOf(calendar.get(Calendar.YEAR)).substring(2);
						} else
						{
							if (interval instanceof WeeklyInterval)
								string = string.substring(0, 1);
						}
						double h = cp.getFont().getStringBounds(string, frc).getHeight();

						if (firstFlag)
						{
							int index = (int) value;
							long time = dataset.getTimeAt(index);
							if (!cd.isFirstWorkingDayOfMonth(time))
							{
								double nvalue = 0;
								for (int k = j + 1; k < list.length; k++)
									if (list[k] != -1)
									{
										nvalue = list[k];
										break;
									}
								double nx = cd.getX(nvalue, bounds);
								double w = cp.getFont().getStringBounds(string, frc).getWidth();
								if (nx - x > w + 5)
								{
									g2.setColor(cp.getFontColor());
									g2.drawString(string, (float)(x + 5), lm.getAscent());
								}
							} else
							{
								g2.setColor(cp.getAxisColor());
								g2.draw(CoordCalc.line(x, 0, x, h));
								g2.setColor(cp.getFontColor());
								g2.drawString(string, (float)(x + 5), lm.getAscent());
							}
							firstFlag = false;
						} else
						{
							g2.setColor(cp.getAxisColor());
							g2.draw(CoordCalc.line(x, 0, x, h));
							g2.setColor(cp.getFontColor());
							g2.drawString(string, (float)(x + 5), lm.getAscent());
						}
					}
				}
            }
            else
            {
                for (int j = 0; j < list.length; j++)
                {
					double value = list[j];
					if (value != -1)
					{
						double x = cd.getX(value, bounds);
						calendar.setTimeInMillis(dataset.getTimeAt(j));
						StringBuilder sb = new StringBuilder();
						if (calendar.get(Calendar.HOUR_OF_DAY) < 10)
                            sb.append("0");
                        sb.append(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)));
                        sb.append(":");
                        if (calendar.get(Calendar.MINUTE) < 10)
                            sb.append("0");
                        sb.append(String.valueOf(calendar.get(Calendar.MINUTE)));
						String label = sb.toString();
						
						double h = cp.getFont().getStringBounds(label, frc).getHeight();
						g2.setColor(cp.getAxisColor());
						g2.draw(CoordCalc.line(x, 0, x, h));
						g2.setColor(cp.getFontColor());
                        g2.drawString(label, (float)(x + 5), lm.getAscent());
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
