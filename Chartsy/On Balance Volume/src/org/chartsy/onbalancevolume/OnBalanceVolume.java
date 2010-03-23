package org.chartsy.onbalancevolume;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import org.chartsy.main.chartsy.ChartFrame;
import org.chartsy.main.chartsy.DefaultPainter;
import org.chartsy.main.chartsy.chart.Indicator;
import org.chartsy.main.dataset.Dataset;
import org.chartsy.main.utils.Range;
import org.openide.nodes.AbstractNode;

/**
 *
 * @author viorel.gheba
 */
public class OnBalanceVolume extends Indicator implements Serializable {

    private static final long serialVersionUID = 101L;
    public static final String OBV = "obv";

    private IndicatorProperties properties = new IndicatorProperties();

    public OnBalanceVolume() {
        super("OBV", "Description", "OBV");
    }

    public String getLabel() { return properties.getLabel(); }

    public LinkedHashMap getHTML(ChartFrame cf, int i) {
        LinkedHashMap ht = new LinkedHashMap();

        double[] values = getValues(cf, i);
        String[] labels = {"OBV:"};

        ht.put(getLabel(), " ");
        DecimalFormat df = new DecimalFormat("#,##0.00");
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
        Dataset dataset = visibleDataset(cf, OBV);
        double factor = getFactor(cf);
        if (dataset != null) {
            double min = dataset.getCloseValue(0);
            double max = dataset.getCloseValue(0);
            for (int i = 0; i < dataset.getItemCount(); i++) {
                if (min > dataset.getCloseValue(i)) min = dataset.getCloseValue(i);
                if (max < dataset.getCloseValue(i)) max = dataset.getCloseValue(i);
            }
            Range range = new Range((min - (max - min) * 0.2d)/factor, (max + (max - min) * 0.2d)/factor);
            return range;
        }
        return new Range();
    }

    public void paint(Graphics2D g, ChartFrame cf) {
        Dataset dataset = visibleDataset(cf, OBV);

        if (dataset != null) {           
            Range range = getRange(cf);
            Rectangle2D.Double bounds = getBounds();
            double factor = getFactor(cf);

            DefaultPainter.line(g, cf, range, bounds, Dataset.DIV(dataset, factor), properties.getColor(), properties.getStroke()); // paint line
            DecimalFormat df = new DecimalFormat("###,###");
            DefaultPainter.label(g, cf, getLabel() + " x " + df.format((int)factor), bounds); // paint label
        }
    }

    public void calculate() {
        Dataset initial = getDataset();
        if (initial != null && !initial.isEmpty()) {
            Dataset dataset = getDataset(initial); addDataset(OBV, dataset);
        }
    }

    public boolean hasZeroLine() { return false; }
    public boolean getZeroLineVisibility() { return false; }
    public Color getZeroLineColor() { return null; }
    public Stroke getZeroLineStroke() { return null; }
    public Color[] getColors() { return new Color[] {properties.getColor()}; }
    public double[] getValues(ChartFrame cf) {
        Dataset dataset = visibleDataset(cf, OBV);
        double factor = getFactor(cf);
        if (dataset != null) 
            return new double[] {dataset.getLastPriceValue(Dataset.CLOSE)/factor};
        return new double[] {};
    }
    public double[] getValues(ChartFrame cf, int i) {
        Dataset dataset = visibleDataset(cf, OBV);
        double factor = getFactor(cf);
        if (dataset != null)
            return new double[] {dataset.getPriceValue(i, Dataset.CLOSE)/factor};
        return new double[] {};
    }
    public boolean getMarkerVisibility() { return properties.getMarker(); }
    public AbstractNode getNode() { return new IndicatorNode(properties); }

    private Dataset getDataset(final Dataset initial) {
        Dataset obv = Dataset.EMPTY(initial);

        double currentOBV = 0;
        for (int i = 1; i < initial.getItemCount(); i++) {
            if (initial.getCloseValue(i) > initial.getCloseValue(i-1)) {
                currentOBV += initial.getVolumeValue(i);
            } else if (initial.getCloseValue(i) < initial.getCloseValue(i-1)) {
                currentOBV -= initial.getVolumeValue(i);
            }
            obv.setData(0, currentOBV, 0, 0, 0, 0, i);
        }

        return obv;
    }

    private double getFactor(ChartFrame cf) {
        Dataset dataset = visibleDataset(cf, OBV);

        if (dataset != null) {
            double max = dataset.getCloseValue(0);
            double min = dataset.getCloseValue(0);
            for (int i = 0; i < dataset.getItemCount(); i++) {
                if (max < dataset.getCloseValue(i)) max = dataset.getCloseValue(i);
                if (min > dataset.getCloseValue(i)) min = dataset.getCloseValue(i);
            }
            int scaleMax = Integer.toString((int)Math.round(max + (max - min) * 0.2d)).length() - 1;
            int scaleMin = Integer.toString((int)Math.round(min - (max - min) * 0.2d)).length() - 1;
            int scale = Math.min(scaleMin, scaleMax);
            if (scale > 1) scale--;
            return Math.pow(10, scale);
        }

        return 1;
    }

}