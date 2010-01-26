package org.chartsy.ema;

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
public class EMA extends Overlay implements Serializable {

    private static final long serialVersionUID = 101L;
    public static final String EMA = "ema";

    private OverlayProperties properties;

    public EMA() { super("EMA", "Description", "EMA"); properties = new OverlayProperties(); }

    public String getLabel() { return properties.getLabel() + " (" + properties.getPrice() + ", " + properties.getPeriod() + ")"; }

    public LinkedHashMap getHTML(ChartFrame cf, int i) {
        LinkedHashMap ht = new LinkedHashMap();

        DecimalFormat df = new DecimalFormat("#,##0.00");
        double[] values = getValues(cf, i);

        ht.put(getLabel(), " ");
        if (values.length > 0) {
            Color[] colors = getColors();
            for (int j = 0; j < values.length; j++) {
                ht.put(getFontHTML(colors[j], "EMA:"),
                        getFontHTML(colors[j], df.format(values[j])));
            }
        }

        return ht;
    }

    public Range getRange(ChartFrame cf) {
        String price = properties.getPrice();
        Dataset ema = visibleDataset(cf, EMA);
        if (ema != null) {
            Range chartRange = cf.getChartRenderer().getChartRange();
            Range range = Range.combine(chartRange, new Range(ema.getMinNotZero(price), ema.getMaxNotZero(price)));
            return range;
        }
        return null;
    }

    public void calculate() {
        Dataset initial = getDataset();
        if (initial != null && !initial.isEmpty()) {
            int period = properties.getPeriod();
            Dataset ema = initial.getEMA(period);
            addDataset(EMA, ema);
        }
    }

    public void paint(Graphics2D g, ChartFrame cf) {
        Dataset ema = visibleDataset(cf, EMA);
        if (ema != null) DefaultPainter.line(g, cf, ema, properties.getColor(), properties.getStroke(), properties.getPrice()); // paint sma line
    }

    public Color[] getColors() { return new Color[] {properties.getColor()}; }
    public double[] getValues(ChartFrame cf) {
        Dataset ema = visibleDataset(cf, EMA);
        if (ema != null) {
            String price = properties.getPrice();
            return new double[] {ema.getLastPriceValue(price)};
        }
        return new double[] {};
    }
    public double[] getValues(ChartFrame cf, int i) {
        Dataset ema = visibleDataset(cf, EMA);
        if (ema != null) {
            String price = properties.getPrice();
            return new double[] {ema.getPriceValue(i, price)};
        }
        return new double[] {};
    }
    public boolean getMarkerVisibility() { return properties.getMarker(); }

    public AbstractNode getNode() { return new OverlayNode(properties); }

}
