package org.chartsy.sma;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import org.chartsy.main.chartsy.ChartFrame;
import org.chartsy.main.chartsy.DefaultPainter;
import org.chartsy.main.chartsy.chart.Overlay;
import org.chartsy.main.dataset.Dataset;
import org.chartsy.main.utils.Range;
import org.openide.nodes.AbstractNode;

/**
 *
 * @author viorel.gheba
 */
public class SMA extends Overlay implements Serializable {

    private static final long serialVersionUID = 101L;
    public static final String SMA = "sma";

    private OverlayProperties properties;

    public SMA() { super("SMA", "Description", "SMA"); properties = new OverlayProperties(); }

    public String getLabel() { return properties.getLabel() + " (" + properties.getPrice() + ", " + properties.getPeriod() + ")"; }

    public LinkedHashMap getHTML(ChartFrame cf, int i) {
        LinkedHashMap ht = new LinkedHashMap();

        DecimalFormat df = new DecimalFormat("#,##0.00");
        double[] values = getValues(cf, i);

        ht.put(getLabel(), " ");
        if (values.length > 0) {
            Color[] colors = getColors();
            for (int j = 0; j < values.length; j++) {
                ht.put(getFontHTML(colors[j], "SMA:"),
                        getFontHTML(colors[j], df.format(values[j])));
            }
        }

        return ht;
    }

    public Range getRange(ChartFrame cf) {
        String price = properties.getPrice();
        Dataset sma = visibleDataset(cf, SMA);
        if (sma != null) {
            Range chartRange = cf.getChartRenderer().getChartRange();
            Range range = Range.combine(chartRange, new Range(sma.getMinNotZero(price), sma.getMaxNotZero(price)));
            return range;
        }
        return null;
    }

    public void calculate() {
        Dataset initial = getDataset();
        if (initial != null && !initial.isEmpty()) {
            int period = properties.getPeriod();
            Dataset sma = initial.getSMA(period);
            addDataset(SMA, sma);
        }
    }

    public void paint(Graphics2D g, ChartFrame cf) {
        Dataset sma = visibleDataset(cf, SMA);
        if (sma != null) DefaultPainter.line(g, cf, sma, properties.getColor(), properties.getStroke(), properties.getPrice()); // paint sma line
    }

    public Color[] getColors() { return new Color[] {properties.getColor()}; }
    public double[] getValues(ChartFrame cf) {
        Dataset sma = visibleDataset(cf, SMA);
        if (sma != null) {
            String price = properties.getPrice();
            return new double[] {sma.getLastPriceValue(price)};
        }
        return new double[] {};
    }
    public double[] getValues(ChartFrame cf, int i) {
        Dataset sma = visibleDataset(cf, SMA);
        if (sma != null) {
            String price = properties.getPrice();
            return new double[] {sma.getPriceValue(i, price)};
        }
        return new double[] {};
    }
    public boolean getMarkerVisibility() { return properties.getMarker(); }

    public AbstractNode getNode() { return new OverlayNode(properties); }

}
