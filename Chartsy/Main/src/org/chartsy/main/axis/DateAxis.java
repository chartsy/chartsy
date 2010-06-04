package org.chartsy.main.axis;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.util.Calendar;
import javax.swing.JLabel;
import org.chartsy.main.AbstractComponent;
import org.chartsy.main.ChartFrame;
import org.chartsy.main.ChartProperties;
import org.chartsy.main.data.ChartData;
import org.chartsy.main.data.Dataset;
import org.chartsy.main.intervals.Interval;
import org.chartsy.main.intervals.MonthlyInterval;
import org.chartsy.main.utils.CoordCalc;
import org.chartsy.main.utils.SerialVersion;

/**
 *
 * @author viorel.gheba
 */
public class DateAxis extends AbstractComponent
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;
    private ChartFrame chartFrame;
    private JLabel copyright;

    public DateAxis(ChartFrame frame)
    {
        chartFrame = frame;
        copyright = new JLabel("Chartsy.org \u00a9 2009-2010 mrswing.com");
        setOpaque(false);
        setLayout(new BorderLayout());

        Font f = chartFrame.getChartProperties().getFont();
        copyright.setFont(f.deriveFont(f.getStyle() ^ Font.BOLD));
        add(copyright, BorderLayout.PAGE_END);
    }

    protected void paintAbstractComponent(Graphics g)
    {
        int width = getWidth();

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
            Rectangle bounds = chartFrame.getSplitPanel().getChartPanel().getBounds();
            bounds.grow(-2, -2);
            Dataset dataset = cd.getVisible();
            Interval interval = cd.getInterval();

            g2.setFont(cp.getFont());
            g2.setPaint(cp.getAxisColor());
            g2.setStroke(cp.getAxisStroke());
            g2.drawLine(0, 0, width, 0);

            String[] months = cp.getMonths();
            double axisTick = cp.getAxisTick();
            double axisStick = cp.getAxisDateStick();

            g.setFont(cp.getFont());
            FontRenderContext frc = g2.getFontRenderContext();
            LineMetrics lm = cp.getFont().getLineMetrics("0123456789/", g2.getFontRenderContext());

            if (!interval.isIntraDay())
            {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(dataset.getTimeAt(0));
                int month = cal.get(Calendar.MONTH);
                for(int j = 0; j < dataset.getItemsCount(); j++)
                {
                    cal.setTimeInMillis(dataset.getTimeAt(j));
                    if (month != cal.get(Calendar.MONTH))
                    {
                        g2.setPaint(cp.getAxisColor());
                        double x = cd.getX(j, bounds);
                        g2.draw(CoordCalc.line(x, 0, x, axisTick));
                        g2.setPaint(cp.getFontColor());
                        String s = months[cal.get(Calendar.MONTH)];
                        s = s.equals("Jan") ? String.valueOf(cal.get(Calendar.YEAR)).substring(2) : (interval instanceof MonthlyInterval ? s.substring(0, 1) : s);
                        float w = (float)(cp.getFont().getStringBounds(s, frc).getWidth());
                        g2.drawString(s, (float)(x - w / 2), (float)(axisTick + axisStick + lm.getAscent()));
                        month = cal.get(Calendar.MONTH);
                    }
                }
            } 
            else
            {
                Calendar cal = Calendar.getInstance();
                for(int j = 0; j < dataset.getItemsCount(); j++)
                {
                    if (j % 10 == 0)
                    {
                        cal.setTimeInMillis(dataset.getTimeAt(j));
                        g2.setPaint(cp.getAxisColor());
                        double x = cd.getX(j, bounds);
                        g2.draw(CoordCalc.line(x, 0, x, axisTick));
                        g2.setPaint(cp.getFontColor());
                        StringBuffer sb = new StringBuffer();
                        if (cal.get(Calendar.HOUR_OF_DAY) < 10)
                            sb.append("0");
                        sb.append(String.valueOf(cal.get(Calendar.HOUR_OF_DAY)));
                        sb.append(":");
                        if (cal.get(Calendar.MINUTE) < 10)
                            sb.append("0");
                        sb.append(String.valueOf(cal.get(Calendar.MINUTE)));
                        float w = (float)(cp.getFont().getStringBounds(sb.toString(), frc).getWidth());
                        g2.drawString(sb.toString(), (float)(x - w/2), (float)(axisTick + axisStick + lm.getAscent()));
                    }
                }
            }
        }
        
        g2.dispose();
    }

}
