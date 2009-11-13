package org.chartsy.macd;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Vector;
import org.chartsy.main.chartsy.ChartFrame;
import org.chartsy.main.chartsy.DefaultPainter;
import org.chartsy.main.chartsy.chart.Indicator;
import org.chartsy.main.dataset.DataItem;
import org.chartsy.main.dataset.Dataset;
import org.chartsy.main.utils.ComponentGenerator;
import org.chartsy.main.utils.Properties;
import org.chartsy.main.utils.PropertyItem;
import org.chartsy.main.utils.Range;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author viorel.gheba
 */
public class MACD extends Indicator {

    public static final String FAST = "Fast";
    public static final String SLOW = "Slow";
    public static final String SMOOTH = "Smooth";
    public static final String LABEL = "Label";
    public static final String MARKER = "Marker";
    public static final String ZERO_COLOR = "Zero Line Color";
    public static final String ZERO_STYLE = "Zero Line Style";
    public static final String ZERO_VISIBILITY = "Zero Line Visibility";
    public static final String HISTOGRAM_COLOR = "Histogram Color";
    public static final String SIGNAL_COLOR = "Signal Color";
    public static final String SIGNAL_STYLE = "Signal Style";
    public static final String MACD_COLOR = "MACD Color";
    public static final String MACD_STYLE = "MACD Style";
    public static final String MACD = "macd";
    public static final String SIGNAL = "signal";
    public static final String HISTOGRAM = "histogram";
    
    public MACD() { super("MACD", "Description", "MACD"); }

    public String getLabel() {
        if (getProperties() == null) {
            return getDialogLabel();
        } else {
            String fast = getStringParam(FAST); String slow = getStringParam(SLOW); String smooth = getStringParam(SMOOTH); String label = getStringParam(LABEL);
            return label + "(" + fast + ", " + slow + ", " + smooth + ")";
        }
    }

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

    public void initialize() {
        PropertyItem[] data = new PropertyItem[] {
            new PropertyItem(FAST, ComponentGenerator.JTEXTFIELD, "12"),
            new PropertyItem(SLOW, ComponentGenerator.JTEXTFIELD, "26"),
            new PropertyItem(SMOOTH, ComponentGenerator.JTEXTFIELD, "9"),
            new PropertyItem(LABEL, ComponentGenerator.JTEXTFIELD, "MACD"),
            new PropertyItem(MARKER, ComponentGenerator.JCHECKBOX, Boolean.TRUE),
            new PropertyItem(ZERO_COLOR, ComponentGenerator.JLABEL, new Color(0xeeeeec)),
            new PropertyItem(ZERO_STYLE, ComponentGenerator.JSTROKECOMBOBOX, "0"),
            new PropertyItem(ZERO_VISIBILITY, ComponentGenerator.JCHECKBOX, Boolean.TRUE),
            new PropertyItem(HISTOGRAM_COLOR, ComponentGenerator.JLABEL, new Color(0x204a87)),
            new PropertyItem(SIGNAL_COLOR, ComponentGenerator.JLABEL, new Color(0x5c3566)),
            new PropertyItem(SIGNAL_STYLE, ComponentGenerator.JSTROKECOMBOBOX, "0"),
            new PropertyItem(MACD_COLOR, ComponentGenerator.JLABEL, new Color(0x4e9a06)),
            new PropertyItem(MACD_STYLE, ComponentGenerator.JSTROKECOMBOBOX, "0")
        };
        Properties p = new Properties(data);
        setProperties(p);
    }

    public void calculate() {
        Dataset initial = getDataset();
        if (initial != null && !initial.isEmpty()) {
            int fast = getIntParam(FAST);
            int slow = getIntParam(SLOW);
            int smooth = getIntParam(SMOOTH);

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

            DefaultPainter.bar(g, cf, range, bounds, histogram, getColorParam(HISTOGRAM_COLOR)); // paint the histogram
            DefaultPainter.line(g, cf, range, bounds, signal, getColorParam(SIGNAL_COLOR), getStrokeParam(SIGNAL_STYLE)); // paint the signal
            DefaultPainter.line(g, cf, range, bounds, macd, getColorParam(MACD_COLOR), getStrokeParam(MACD_STYLE)); // paint the MACD

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
    public boolean getZeroLineVisibility() { return getBooleanParam(ZERO_VISIBILITY); }
    public Color getZeroLineColor() { return getColorParam(ZERO_COLOR); }
    public Stroke getZeroLineStroke() { return getStrokeParam(ZERO_STYLE); }
    public Color[] getColors() { return new Color[] {getColorParam(HISTOGRAM_COLOR), getColorParam(SIGNAL_COLOR), getColorParam(MACD_COLOR)}; }
    public double[] getValues(ChartFrame cf) {
        Dataset macd = visibleDataset(cf, MACD);
        Dataset signal = visibleDataset(cf, SIGNAL);
        Dataset histogram = visibleDataset(cf, HISTOGRAM);
        if (histogram != null && signal != null && macd != null) return new double[] {histogram.getLastPriceValue(Dataset.CLOSE), signal.getLastPriceValue(Dataset.CLOSE), macd.getLastPriceValue(Dataset.CLOSE)};
        return new double[] {};
    }
    public double[] getValues(ChartFrame cf, int i) {
        Dataset macd = visibleDataset(cf, MACD);
        Dataset signal = visibleDataset(cf, SIGNAL);
        Dataset histogram = visibleDataset(cf, HISTOGRAM);
        if (histogram != null && signal != null && macd != null) return new double[] {histogram.getPriceValue(i, Dataset.CLOSE), signal.getPriceValue(i, Dataset.CLOSE), macd.getPriceValue(i, Dataset.CLOSE)};
        return new double[] {};
    }
    public boolean getMarkerVisibility() { return getBooleanParam(MARKER); }

    public void readXMLDocument(Element parent) { readFromXMLDocument(parent); }
    public void writeXMLDocument(Document document, Element parent) { writeToXMLDocument(document, parent, "MACD"); }

}
