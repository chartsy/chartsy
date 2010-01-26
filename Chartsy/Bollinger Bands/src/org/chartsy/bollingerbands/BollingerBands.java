package org.chartsy.bollingerbands;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Vector;
import org.chartsy.main.chartsy.ChartFrame;
import org.chartsy.main.chartsy.DefaultPainter;
import org.chartsy.main.chartsy.chart.Overlay;
import org.chartsy.main.dataset.DataItem;
import org.chartsy.main.dataset.Dataset;
import org.chartsy.main.utils.Range;
import org.openide.nodes.AbstractNode;

/**
 *
 * @author viorel.gheba
 */
public class BollingerBands extends Overlay implements Serializable {

    private static final long serialVersionUID = 101L;
    public static final String UPPER = "upper";
    public static final String MIDDLE = "middle";
    public static final String LOWER = "lower";

    private OverlayProperties properties;

    public BollingerBands() { super("Bollinger Bands", "Description", "Bollinger"); properties = new OverlayProperties(); }

    public String getLabel() { return properties.getLabel() + " (" + properties.getPrice() + ", " + properties.getStd() + ", " + properties.getPeriod() + ")"; }

    public LinkedHashMap getHTML(ChartFrame cf, int i) {
        LinkedHashMap ht = new LinkedHashMap();

        DecimalFormat df = new DecimalFormat("#,##0.00");
        double[] values = getValues(cf, i);
        String[] labels = {"Upper Band:", "Middle Band:", "Lower Band:"};

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
        String price = properties.getPrice();
        Dataset upper = visibleDataset(cf, UPPER);
        Dataset lower = visibleDataset(cf, LOWER);
        if (upper != null && lower != null) {
            Range chartRange = cf.getChartRenderer().getChartRange();
            Range range = new Range(Math.min(chartRange.getLowerBound(), lower.getMinNotZero(price)), Math.max(chartRange.getUpperBound(), upper.getMaxNotZero(price)));
            return range;
        }
        return null;
    }

    public void calculate() {
        int period = properties.getPeriod();
        int stddev = properties.getStd();
        Dataset initial = getDataset();
        if (initial != null && !initial.isEmpty()) {
            Dataset middle = initial.getSMA(period); addDataset(MIDDLE, middle);
            Dataset upper = getLowerUpperDataset(initial, middle, period, stddev, UPPER); addDataset(UPPER, upper);
            Dataset lower = getLowerUpperDataset(initial, middle, period, stddev, LOWER); addDataset(LOWER, lower);
        }
    }

    public void paint(Graphics2D g, ChartFrame cf) {
        Dataset middle = visibleDataset(cf, MIDDLE);
        Dataset upper = visibleDataset(cf, UPPER);
        Dataset lower = visibleDataset(cf, LOWER);

        if (middle != null && upper != null && lower != null) {
            String price = properties.getPrice();
            if (properties.getInsideVisibility()) DefaultPainter.insideFill(g, cf, upper, lower, properties.getInsideTransparentColor(), price); // paint inside fill
            DefaultPainter.line(g, cf, middle, properties.getMiddleColor(), properties.getMiddleStroke(), price); // paint middle line
            DefaultPainter.line(g, cf, upper, properties.getUpperColor(), properties.getUpperStroke(), price); // paint upper line
            DefaultPainter.line(g, cf, lower, properties.getLowerColor(), properties.getLowerStroke(), price); // paint lower line
        }
    }
    
    public Dataset getLowerUpperDataset(final Dataset initial, final Dataset middle, final int period, final int stddev, final String type) {
        Vector<DataItem> items = new Vector<DataItem>();
        for (int i = 0; i < period; i++) {
            DataItem item = new DataItem(initial.getDate(i), 0, 0, 0, 0, 0, 0);
            items.add(item);
        }
        for (int i = period; i < initial.getItemCount(); i++) {
            double opendev = 0;  double closedev = 0; double highdev = 0; double lowdev = 0;
            for (int j = 0; j < period; j++) {
                opendev += Math.pow(initial.getOpenValue(i-j) - middle.getOpenValue(i), 2);
                closedev += Math.pow(initial.getCloseValue(i-j) - middle.getCloseValue(i), 2);
                highdev += Math.pow(initial.getHighValue(i-j) - middle.getHighValue(i), 2);
                lowdev += Math.pow(initial.getLowValue(i-j) - middle.getLowValue(i), 2);
            }
            opendev = Math.sqrt(opendev / period);
            closedev = Math.sqrt(closedev / period);
            highdev = Math.sqrt(highdev / period);
            lowdev = Math.sqrt(lowdev / period);
            if (type.equals(LOWER)) {
                DataItem item = new DataItem(middle.getDate(i), middle.getOpenValue(i) - stddev * opendev, middle.getCloseValue(i) - stddev * closedev, middle.getHighValue(i) - stddev * highdev, middle.getLowValue(i) - stddev * lowdev, 0, 0);
                items.add(item);
            } else if (type.equals(UPPER)) {
                DataItem item = new DataItem(middle.getDate(i), middle.getOpenValue(i) + stddev * opendev, middle.getCloseValue(i) + stddev * closedev, middle.getHighValue(i) + stddev * highdev, middle.getLowValue(i) + stddev * lowdev, 0, 0);
                items.add(item);
            }
        }
        DataItem[] data = items.toArray(new DataItem[items.size()]);
        return new Dataset(data);
    }

    public Color[] getColors() { return new Color[] {properties.getUpperColor(), properties.getMiddleColor(), properties.getLowerColor()}; }
    public double[] getValues(ChartFrame cf) {
        Dataset middle = visibleDataset(cf, MIDDLE);
        Dataset upper = visibleDataset(cf, UPPER);
        Dataset lower = visibleDataset(cf, LOWER);
        if (middle != null && upper != null && lower != null) {
            String price = properties.getPrice();
            return new double[] {upper.getLastPriceValue(price), middle.getLastPriceValue(price), lower.getLastPriceValue(price)};
        }
        return new double[] {};
    }
    public double[] getValues(ChartFrame cf, int i) {
        Dataset middle = visibleDataset(cf, MIDDLE);
        Dataset upper = visibleDataset(cf, UPPER);
        Dataset lower = visibleDataset(cf, LOWER);
        if (middle != null && upper != null && lower != null) {
            String price = properties.getPrice();
            return new double[] {upper.getPriceValue(i, price), middle.getPriceValue(i, price), lower.getPriceValue(i, price)};
        }
        return new double[] {};
    }
    public boolean getMarkerVisibility() { return properties.getMarker(); }

    public AbstractNode getNode() { return new OverlayNode(properties); }

}
