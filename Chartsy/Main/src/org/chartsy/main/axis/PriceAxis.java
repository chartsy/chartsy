package org.chartsy.main.axis;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.LineMetrics;
import java.text.DecimalFormat;
import java.util.List;
import org.chartsy.main.AbstractComponent;
import org.chartsy.main.ChartFrame;
import org.chartsy.main.ChartProperties;
import org.chartsy.main.IndicatorPanel;
import org.chartsy.main.chart.Overlay;
import org.chartsy.main.data.ChartData;
import org.chartsy.main.utils.CoordCalc;
import org.chartsy.main.utils.Range;

/**
 *
 * @author viorel.gheba
 */
public class PriceAxis extends AbstractComponent {

    private static final long serialVersionUID = 2L;
    private ChartFrame chartFrame;

    public PriceAxis(ChartFrame frame) {
        chartFrame = frame;
        setOpaque(false);
    }

    protected void paintAbstractComponent(Graphics g) {
        int width = getWidth();
        int height = getHeight();

        g.setColor(chartFrame.getChartProperties().getBackgroundColor());
        g.fillRect(0, 0, width, height);

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
            g2.setFont(cp.getFont());
            g2.translate(0, 0);
            g2.setPaint(cp.getAxisColor());
            g2.setStroke(cp.getAxisStroke());
            g2.drawLine(0, 0, 0, height);

            // paint values for chart
            Rectangle chartBounds = chartFrame.getSplitPanel().getChartPanel().getBounds();
            chartBounds.grow(-2, -2);
            Range chartRange = chartFrame.getSplitPanel().getChartPanel().getRange();

            List<Double> values = cd.getPriceValues(chartBounds, chartRange);
            double axisTick = cp.getAxisTick();
            double axisStick = cp.getAxisPriceStick();
            double y;

            g.setFont(cp.getFont());
            LineMetrics lm = cp.getFont().getLineMetrics("0123456789", g2.getFontRenderContext());
            DecimalFormat df = new DecimalFormat("#,###.##");

            for (int i = 0; i < values.size(); i++) {
                double value = values.get(i);
                y = cd.getY(value, chartBounds, chartRange);
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
            y = cd.getY(close, chartBounds, chartRange);
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
                                y = cd.getY(ds[i], chartBounds, chartRange);
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
                                y = cd.getY(value, indicatorBounds, indicatorRange);
                                if (indicatorBounds.contains(indicatorBounds.getCenterX(), y))
                                {
                                    g2.setPaint(cp.getAxisColor());
                                    g2.draw(CoordCalc.line(0, y, axisTick, y));
                                    g2.setPaint(cp.getFontColor());
                                    g2.drawString(df.format(value), (float)(axisTick + axisStick), (float)(y + lm.getDescent()));
                                }
                            }

                            if (panel.getIndicator().hasZeroLine())
                            {
                                y = cd.getY(0, indicatorBounds, indicatorRange);
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
                                        y = cd.getY(ds[i], indicatorBounds, indicatorRange);
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

}
