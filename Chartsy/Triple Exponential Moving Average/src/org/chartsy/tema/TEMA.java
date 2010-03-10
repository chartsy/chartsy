package org.chartsy.tema;

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
public class TEMA extends Overlay implements Serializable {

    private static final long serialVersionUID = 101L;
    public static final String TEMA = "tema";

    private OverlayProperties properties;

    public TEMA() { super("TEMA", "Description", "TEMA"); properties = new OverlayProperties(); }

    public String getLabel() { return properties.getLabel() + " (" + properties.getPrice() + ", " + properties.getPeriod() + ")"; }

    public LinkedHashMap getHTML(ChartFrame cf, int i) {
        LinkedHashMap ht = new LinkedHashMap();

        DecimalFormat df = new DecimalFormat("#,##0.00");
        double[] values = getValues(cf, i);

        ht.put(getLabel(), " ");
        if (values.length > 0) {
            Color[] colors = getColors();
            for (int j = 0; j < values.length; j++) {
                ht.put(getFontHTML(colors[j], "TEMA:"),
                        getFontHTML(colors[j], df.format(values[j])));
            }
        }

        return ht;
    }

    public Range getRange(ChartFrame cf) {
        String price = properties.getPrice();
        Dataset ema = visibleDataset(cf, TEMA);
        if (ema != null) {
            Range chartRange = cf.getChartRenderer().getChartRange();
            Range range = Range.combine(chartRange, new Range(ema.getMinNotZero(price), ema.getMaxNotZero(price)));
            return range;
        }
        return null;
    }

    public void calculate() {
        int period = properties.getPeriod();
        Dataset initial = getDataset();
        if (initial != null && !initial.isEmpty()) {
            Dataset tema = Dataset.TEMA(initial, period); addDataset(TEMA, tema);
        }
    }

    public void paint(Graphics2D g, ChartFrame cf) {
        Dataset ema = visibleDataset(cf, TEMA);
        if (ema != null) DefaultPainter.line(g, cf, ema, properties.getColor(), properties.getStroke(), properties.getPrice()); // paint tema line
    }

    public Color[] getColors() { return new Color[] {properties.getColor()}; }
    public double[] getValues(ChartFrame cf) {
        Dataset ema = visibleDataset(cf, TEMA);
        if (ema != null) {
            String price = properties.getPrice();
            return new double[] {ema.getLastPriceValue(price)};
        }
        return new double[] {};
    }
    public double[] getValues(ChartFrame cf, int i) {
        Dataset ema = visibleDataset(cf, TEMA);
        if (ema != null) {
            String price = properties.getPrice();
            return new double[] {ema.getPriceValue(i, price)};
        }
        return new double[] {};
    }
    public boolean getMarkerVisibility() { return properties.getMarker(); }

    public AbstractNode getNode() { return new OverlayNode(properties); }

}
