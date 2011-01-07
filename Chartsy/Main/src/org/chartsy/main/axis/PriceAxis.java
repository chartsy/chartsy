package org.chartsy.main.axis;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.LineMetrics;
import java.io.Serializable;
import java.text.DecimalFormat;
import javax.swing.JPanel;
import org.chartsy.main.ChartFrame;
import org.chartsy.main.ChartProperties;
import org.chartsy.main.IndicatorPanel;
import org.chartsy.main.chart.Overlay;
import org.chartsy.main.data.ChartData;
import org.chartsy.main.utils.CoordCalc;
import org.chartsy.main.utils.GraphicsUtils;
import org.chartsy.main.utils.Range;
import org.chartsy.main.utils.SerialVersion;

/**
 *
 * @author viorel.gheba
 */
public class PriceAxis extends JPanel implements Serializable
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;
    private ChartFrame chartFrame;

    public PriceAxis(ChartFrame frame)
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
            // paint values for chart
            Rectangle chartBounds = chartFrame.getSplitPanel().getChartPanel().getBounds();
			int indicatorsHeight = chartFrame.getSplitPanel().getIndicatorsPanel().getHeight();
            Range chartRange = chartFrame.getSplitPanel().getChartPanel().getRange();
			FontMetrics fm = getFontMetrics(chartFrame.getChartProperties().getFont());

			g2.setFont(cp.getFont());
            g2.translate(0, 0);
            g2.setPaint(cp.getAxisColor());
            g2.setStroke(cp.getAxisStroke());
            g2.drawLine(0, 0, 0, chartBounds.height + indicatorsHeight);

			chartBounds.grow(-2, -2);

			double[] values = cd.getYValues(chartBounds, chartRange, fm.getHeight());
            double axisTick = cp.getAxisTick();
            double axisStick = cp.getAxisPriceStick();
            double y;

            g.setFont(cp.getFont());
            LineMetrics lm = cp.getFont().getLineMetrics("0123456789", g2.getFontRenderContext());
            DecimalFormat df = new DecimalFormat("#,###.##");

            for (int i = 0; i < values.length; i++)
			{
                double value = values[i];
                y = cd.getY(value, chartBounds, chartRange, isLog);
                if (chartBounds.contains(chartBounds.getCenterX(), y))
                {
                    g2.setPaint(cp.getAxisColor());
                    g2.draw(CoordCalc.line(0, y, axisTick, y));
                    g2.setPaint(cp.getFontColor());
                    g2.drawString(df.format(value), (float)(axisTick + axisStick), (float)(y + lm.getDescent()));
                }
            }

            // paint chart marker
            double open = cd.getVisible().getLastOpen();
            double close = cd.getVisible().getLastClose();
            y = cd.getY(close, chartBounds, chartRange, isLog);
            PriceAxisMarker.paint(g2, chartFrame, close, open > close ? cp.getBarDownColor() : cp.getBarUpColor(), y);

            // paint overlays marker
            if (chartFrame.getSplitPanel().getChartPanel().getOverlaysCount() > 0)
            {
                for (Overlay overlay : chartFrame.getSplitPanel().getChartPanel().getOverlays())
                {
                    if (overlay.getMarkerVisibility())
                    {
                        double[] ds = overlay.getValues(chartFrame);
                        if (ds.length > 0)
                        {
                            Color[] cs = overlay.getColors();
                            for (int i = 0; i < ds.length; i++)
                            {
                                y = cd.getY(ds[i], chartBounds, chartRange, isLog);
                                PriceAxisMarker.paint(g2, chartFrame, ds[i], cs[i], y);
                            }
                        }
                    }
                }
            }

            chartBounds.grow(2, 2);
            double hy = chartBounds.getHeight();

            // paint values for indicators
            if (chartFrame.getSplitPanel().getIndicatorsPanel().getIndicatorsCount() > 0) {
                int ind = 0;
                for (IndicatorPanel panel : chartFrame.getSplitPanel().getIndicatorsPanel().getIndicatorPanels()) {
                    g2.translate(0.0, hy);
                    if (panel.isMaximized())
                    {
                        Rectangle indicatorBounds = panel.getBounds();
                        indicatorBounds.grow(-2, -2);
                        Range indicatorRange = panel.getIndicator().getRange(chartFrame);

                        if (panel.getIndicator().paintValues())
                        {
                            Double[] list = panel.getIndicator().getPriceValues(chartFrame);
                            for (Double value : list) {
                                y = cd.getY(value, indicatorBounds, indicatorRange, false);
                                if (indicatorBounds.contains(indicatorBounds.getCenterX(), y))
                                {
                                    g2.setPaint(cp.getAxisColor());
                                    g2.draw(CoordCalc.line(0, y, axisTick, y));
                                    g2.setPaint(cp.getFontColor());
                                    g2.drawString(df.format(value), (float)(axisTick + axisStick), (float)(y + lm.getDescent()));
                                }
                            }
                            list = null;

                            if (panel.getIndicator().hasZeroLine())
                            {
                                y = cd.getY(0, indicatorBounds, indicatorRange, false);
                                g2.setPaint(cp.getAxisColor());
                                g2.draw(CoordCalc.line(0, y, axisTick, y));
                                g2.setPaint(cp.getFontColor());
                                g2.drawString(df.format(0.0), (float)(axisTick + axisStick), (float)(y + lm.getDescent()));
                            }

                            // paint indicators marker
                            if (panel.getIndicator().getMarkerVisibility())
                            {
                                double[] ds = panel.getIndicator().getValues(chartFrame);
                                if (ds.length > 0)
                                {
                                    Color[] cs = panel.getIndicator().getColors();
                                    for (int i = 0; i < ds.length; i++)
                                    {
                                        y = cd.getY(ds[i], indicatorBounds, indicatorRange, false);
                                        if (cs[i] != null)
                                            PriceAxisMarker.paint(g2, chartFrame, ds[i], cs[i], y);
                                    }
                                }
                            }
                        }
                        indicatorBounds.grow(2, 2);
                        hy = indicatorBounds.getHeight();
                    }
                    else
                    {
                        hy = panel.getBounds().getHeight();
                    }
                    ind++;
                }
            }
        }
        g2.dispose();
	}

	@Override
	public void update(Graphics g)
	{
		paint(g);
	}

}
