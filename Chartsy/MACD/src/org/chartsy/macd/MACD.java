package org.chartsy.macd;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Vector;
import org.chartsy.main.chartsy.ChartFrame;
import org.chartsy.main.chartsy.DefaultPainter;
import org.chartsy.main.chartsy.chart.Indicator;
import org.chartsy.main.dataset.DataItem;
import org.chartsy.main.dataset.Dataset;
import org.chartsy.main.utils.Range;
import org.openide.nodes.AbstractNode;

/**
 *
 * @author viorel.gheba
 */
public class MACD extends Indicator implements Serializable {

    private static final long serialVersionUID = 101L;
    public static final String MACD = "macd";
    public static final String SIGNAL = "signal";
    public static final String HISTOGRAM = "histogram";

    private IndicatorProperties properties = new IndicatorProperties();
    
    public MACD() { super("MACD", "Description", "MACD"); }

    public String getLabel() { return properties.getLabel() + " (" + properties.getFast() + ", " + properties.getSlow() + ", " + properties.getSmooth() + ")"; }

    public LinkedHashMap getHTML(ChartFrame cf, int i) {
        LinkedHashMap ht = new LinkedHashMap();

        DecimalFormat df = new DecimalFormat("#,##0.00");
        double[] values = getValues(cf, i);
        String[] labels = {"Histogram:", "Signal:", "MACD:"};

        ht.put(getLabel(), " ");
        if (values.length > 0) {
            Color[] colors = getColors();
            for (int j = 0; j < values.length; j++) {
                ht.put(getFontHTML(colors[j], labels[j]),
                        getFontHTML(colors[j], df.format(values[j])));
            }
        }

        return ht;
    }

    public Range getRange(ChartFrame cf) {
        Dataset macd = visibleDataset(cf, MACD);
        Dataset signal = visibleDataset(cf, SIGNAL);
        Dataset histogram = visibleDataset(cf, HISTOGRAM);

        if (histogram != null && signal != null && macd != null) {
            Range range = new Range(macd.getMin(Dataset.CLOSE), macd.getMax(Dataset.CLOSE));
            range = Range.combine(range, new Range(signal.getMin(Dataset.CLOSE), signal.getMax(Dataset.CLOSE)));
            range = Range.combine(range, new Range(histogram.getMin(Dataset.CLOSE), histogram.getMax(Dataset.CLOSE)));
            return range;
        }
        return null;
    }

    public void calculate() {
        Dataset initial = getDataset();
        if (initial != null && !initial.isEmpty()) {
            int fast = properties.getFast();
            int slow = properties.getSlow();
            int smooth = properties.getSmooth();

            Dataset fastEMA = initial.getEMA(fast);
            Dataset slowEMA = initial.getEMA(slow);

            Dataset macd = getMACD(fastEMA, slowEMA, slow); addDataset(MACD, macd);
            Dataset signal = getSignal(macd, slow, smooth); addDataset(SIGNAL, signal);
            Dataset MACDHistogram = getMACDHistogram(macd, signal); addDataset(HISTOGRAM, MACDHistogram);
        }
    }

    public void paint(Graphics2D g, ChartFrame cf) {       
        Dataset macd = visibleDataset(cf, MACD);
        Dataset signal = visibleDataset(cf, SIGNAL);
        Dataset histogram = visibleDataset(cf, HISTOGRAM);

        if (histogram != null && signal != null && macd != null) {
            Range range = getRange(cf);
            Rectangle2D.Double bounds = getBounds();

            DefaultPainter.bar(g, cf, range, bounds, histogram, properties.getHistogramColor()); // paint the histogram
            DefaultPainter.line(g, cf, range, bounds, signal, properties.getSignalColor(), properties.getSignalStroke()); // paint the signal
            DefaultPainter.line(g, cf, range, bounds, macd, properties.getMacdColor(), properties.getMacdStroke()); // paint the MACD

            LineMetrics lm = cf.getChartProperties().getFont().getLineMetrics("012345679", g.getFontRenderContext());
            g.setFont(cf.getChartProperties().getFont());
            g.setPaint(cf.getChartProperties().getFontColor());
            g.drawString(getLabel(), (float) cf.getChartProperties().getDataOffset().left, (float)(bounds.getMinY() - cf.getChartProperties().getAxisOffset().top + lm.getAscent()));
        }
    }

    private Dataset getMACD(final Dataset fastEMA, final Dataset slowEMA, final int slow) {
        Vector<DataItem> items = new Vector<DataItem>();
        for (int i = 0; i < slow; i++) {
            DataItem item = new DataItem(fastEMA.getDate(i), 0, 0, 0, 0, 0, 0);
            items.add(item);
        }
        for (int i = slow; i < fastEMA.getItemCount(); i++) {
            double diff = fastEMA.getCloseValue(i) - slowEMA.getCloseValue(i);
            DataItem item = new DataItem(fastEMA.getDate(i), 0, diff, 0, 0, 0, 0);
            items.add(item);
        }
        DataItem[] data = items.toArray(new DataItem[items.size()]);
        return new Dataset(data);
    }

    private Dataset getSignal(final Dataset MACD, final int slow, final int smooth) {
        Vector<DataItem> items = new Vector<DataItem>();
        for (int i = 0; i < slow + smooth; i++) {
            DataItem item = new DataItem(MACD.getDate(i), 0, 0, 0, 0, 0, 0);
            items.add(item);
        }
        double close = MACD.getPriceSumValue(slow, slow + smooth, Dataset.CLOSE) / smooth;
        for (int i = (slow + smooth); i < MACD.getItemCount(); i++) {
            double close2 = (2 * (MACD.getCloseValue(i) - close))/(1 + smooth) + close;
            close = close2;
            DataItem item = new DataItem(MACD.getDate(i), 0, close, 0, 0, 0, 0);
            items.add(item);
        }
        DataItem[] data = items.toArray(new DataItem[items.size()]);
        return new Dataset(data);
    }

    private Dataset getMACDHistogram(final Dataset MACD, final Dataset signal) {
        Vector<DataItem> items = new Vector<DataItem>();
        for (int i = 0; i < MACD.getItemCount(); i++) {
            if (signal.getCloseValue(i) != 0 && MACD.getCloseValue(i) != 0) {
                double diff = MACD.getCloseValue(i) - signal.getCloseValue(i);
                DataItem item = new DataItem(MACD.getDate(i), 0, diff, 0, 0, 0, 0);
                items.add(item);
            } else {
                DataItem item = new DataItem(MACD.getDate(i), 0, 0, 0, 0, 0, 0);
                items.add(item);
            }
        }
        DataItem[] data = items.toArray(new DataItem[items.size()]);
        return new Dataset(data);
    }

    public boolean hasZeroLine() { return true; }
    public boolean getZeroLineVisibility() { return properties.getZeroLineVisibility(); }
    public Color getZeroLineColor() { return properties.getZeroLineColor(); }
    public Stroke getZeroLineStroke() { return properties.getZeroLineStroke(); }
    public Color[] getColors() { return new Color[] {properties.getHistogramColor(), properties.getSignalColor(), properties.getMacdColor()}; }
    public double[] getValues(ChartFrame cf) {
        Dataset macd = visibleDataset(cf, MACD);
        Dataset signal = visibleDataset(cf, SIGNAL);
        Dataset histogram = visibleDataset(cf, HISTOGRAM);
        if (histogram != null && signal != null && macd != null)
            return new double[] {histogram.getLastPriceValue(Dataset.CLOSE), signal.getLastPriceValue(Dataset.CLOSE), macd.getLastPriceValue(Dataset.CLOSE)};
        return new double[] {};
    }
    public double[] getValues(ChartFrame cf, int i) {
        Dataset macd = visibleDataset(cf, MACD);
        Dataset signal = visibleDataset(cf, SIGNAL);
        Dataset histogram = visibleDataset(cf, HISTOGRAM);
        if (histogram != null && signal != null && macd != null)
            return new double[] {histogram.getPriceValue(i, Dataset.CLOSE), signal.getPriceValue(i, Dataset.CLOSE), macd.getPriceValue(i, Dataset.CLOSE)};
        return new double[] {};
    }
    public boolean getMarkerVisibility() { return properties.getMarker(); }

    public AbstractNode getNode() {
        return new IndicatorNode(properties);
    }

}
