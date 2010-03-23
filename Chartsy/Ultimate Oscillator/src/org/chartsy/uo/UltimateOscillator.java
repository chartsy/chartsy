package org.chartsy.uo;

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
public class UltimateOscillator extends Indicator implements Serializable {

    private static final long serialVersionUID = 101L;
    public static final String UO = "UO";

    private IndicatorProperties properties = new IndicatorProperties();

    public UltimateOscillator() {
        super("Ultimate Oscillator", "Description", "Ultimate Oscillator");
    }

    public String getLabel() { return properties.getLabel(); }

    public LinkedHashMap getHTML(ChartFrame cf, int i) {
        LinkedHashMap ht = new LinkedHashMap();

        DecimalFormat df = new DecimalFormat("#,##0.00");
        double[] values = getValues(cf, i);
        String[] labels = {"UO:"};

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
        return new Range(0, 100);
    }

    public void paint(Graphics2D g, ChartFrame cf) {
        Dataset dataset = visibleDataset(cf, UO);
        if (dataset != null) {
            Range range = getRange(cf);
            Rectangle2D.Double bounds = getBounds();

            DefaultPainter.line(g, cf, range, bounds, dataset, properties.getColor(), properties.getStroke()); // paint line
            DefaultPainter.label(g, cf, getLabel(), bounds); // paint label
        }
    }

    public void calculate() {
        Dataset initial = getDataset();
        if (initial != null && !initial.isEmpty()) {
            Dataset dataset = getDataset(initial); addDataset(UO, dataset);
        }
    }

    public boolean hasZeroLine() { return false; }
    public boolean getZeroLineVisibility() { return false; }
    public Color getZeroLineColor() { return null; }
    public Stroke getZeroLineStroke() { return null; }
    public Color[] getColors() { return new Color[] {properties.getColor()}; }
    public double[] getValues(ChartFrame cf) {
        Dataset dataset = visibleDataset(cf, UO);
        if (dataset != null)
            return new double[] {dataset.getLastPriceValue(Dataset.CLOSE)};
        return new double[] {};
    }
    public double[] getValues(ChartFrame cf, int i) {
        Dataset dataset = visibleDataset(cf, UO);
        if (dataset != null)
            return new double[] {dataset.getPriceValue(i, Dataset.CLOSE)};
        return new double[] {};
    }
    public boolean getMarkerVisibility() { return properties.getMarker(); }
    public AbstractNode getNode() { return new IndicatorNode(properties); }

    private Dataset getDataset(final Dataset initial) {
        int fast = properties.getFast();
        int inter = properties.getIntermediate();
        int slow = properties.getSlow();

        Dataset dataset = Dataset.EMPTY(initial);

        double temp, sbFast, srFast, sbInter, srInter, sbSlow, srSlow, rFast, rInter, rSlow;
        for (int i = slow; i < initial.getItemCount(); i++) {
            sbFast = 0;
            srFast = 0;
            for (int j = 0; j < fast; j++) {
                if (initial.getCloseValue(i-j-1) < initial.getLowValue(i-j)) sbFast += initial.getCloseValue(i-j) - initial.getCloseValue(i-j-1);
                else sbFast += initial.getCloseValue(i-j) - initial.getLowValue(i-j);

                if ((initial.getHighValue(i-j) - initial.getLowValue(i-j)) > (initial.getHighValue(i-j) - initial.getCloseValue(i-j-1))) temp = initial.getHighValue(i-j) - initial.getLowValue(i-j);
                else temp = initial.getHighValue(i-j) - initial.getCloseValue(i-j-1);

                if (temp > (initial.getCloseValue(i-j-1) - initial.getLowValue(i-j))) rFast = temp;
                else rFast = initial.getCloseValue(i-j-1) - initial.getLowValue(i-j);
                srFast += rFast;
            }

            sbInter = sbFast;
            srInter = srFast;
            for (int j = fast; j < inter; j++) {
                if (initial.getCloseValue(i-j-1) < initial.getLowValue(i-j)) sbInter += initial.getCloseValue(i-j) - initial.getCloseValue(i-j-1);
                else sbInter += initial.getCloseValue(i-j) - initial.getLowValue(i-j);

                if ((initial.getHighValue(i-j) - initial.getLowValue(i-j)) > (initial.getHighValue(i-j) - initial.getCloseValue(i-j-1))) temp = initial.getHighValue(i-j) - initial.getLowValue(i-j);
                else temp = initial.getHighValue(i-j) - initial.getCloseValue(i-j-1);

                if (temp > (initial.getCloseValue(i-j-1) - initial.getLowValue(i-j))) rInter = temp;
                else rInter = initial.getCloseValue(i-j-1) - initial.getLowValue(i-j);
                srInter += rInter;
            }

            sbSlow = sbInter;
            srSlow = srInter;
            for (int j = inter; j < slow; j++) {
                if (initial.getCloseValue(i-j-1) < initial.getLowValue(i-j)) sbSlow += initial.getCloseValue(i-j) - initial.getCloseValue(i-j-1);
                else sbSlow += initial.getCloseValue(i-j) - initial.getLowValue(i-j);

                if ((initial.getHighValue(i-j) - initial.getLowValue(i-j)) > (initial.getHighValue(i-j) - initial.getCloseValue(i-j-1))) temp = initial.getHighValue(i-j) - initial.getLowValue(i-j);
                else temp = initial.getHighValue(i-j) - initial.getCloseValue(i-j-1);

                if (temp > (initial.getCloseValue(i-j-1) - initial.getLowValue(i-j))) rSlow = temp;
                else rSlow = initial.getCloseValue(i-j-1) - initial.getLowValue(i-j);
                srSlow += rSlow;
            }

            if (srFast != 0 && srInter != 0 && srSlow != 0) dataset.setData(0, (100d * (4*sbFast/srFast + 2*sbInter/srInter + sbSlow/srSlow) / 7d), 0, 0, 0, 0, i);
        }

        return dataset;
    }

}
