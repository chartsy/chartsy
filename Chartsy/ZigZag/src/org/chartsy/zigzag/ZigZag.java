package org.chartsy.zigzag;

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
public class ZigZag extends Overlay implements Serializable {

    private static final long serialVersionUID = 101L;
    public static final String ZZ = "zz";

    private OverlayProperties properties;

    public ZigZag() { super("ZigZag", "Description", "ZigZag"); properties = new OverlayProperties(); }

    public String getLabel() { return properties.getLabel(); }

    public LinkedHashMap getHTML(ChartFrame cf, int i) {
        LinkedHashMap ht = new LinkedHashMap();

        DecimalFormat df = new DecimalFormat("#,##0.00");
        double[] values = getValues(cf, i);

        ht.put(getLabel(), " ");
        if (values.length > 0) {
            Color[] colors = getColors();
            for (int j = 0; j < values.length; j++) {
                ht.put(getFontHTML(colors[j], "ZigZag:"),
                        getFontHTML(colors[j], df.format(values[j])));
            }
        }

        return ht;
    }

    public Range getRange(ChartFrame cf) {
        return cf.getChartRenderer().getChartRange();
    }

    public void calculate() {
        Dataset initial = getDataset();
        if (initial != null && !initial.isEmpty()) {
            Dataset dataset = getDataset(initial); addDataset(ZZ, dataset);
        }
    }

    public void paint(Graphics2D g, ChartFrame cf) {
        Dataset dataset = visibleDataset(cf, ZZ);
        if (dataset != null)
            DefaultPainter.line(g, cf, dataset, properties.getColor(), properties.getStroke()); // paint line
    }

    public Color[] getColors() { return new Color[] {properties.getColor()}; }
    public double[] getValues(ChartFrame cf) {
        Dataset dataset = visibleDataset(cf, ZZ);
        if (dataset != null) {
            return new double[] {dataset.getCloseValue(dataset.getLastIndex())};
        }
        return new double[] {};
    }
    public double[] getValues(ChartFrame cf, int i) {
        Dataset dataset = visibleDataset(cf, ZZ);
        if (dataset != null) {
            return new double[] {dataset.getCloseValue(i)};
        }
        return new double[] {};
    }
    public boolean getMarkerVisibility() { return properties.getMarker(); }
    public AbstractNode getNode() { return new OverlayNode(properties); }

    private Dataset getDataset(final Dataset initial) {
        Dataset dataset = Dataset.EMPTY(initial);

        boolean switchVar = false;
        for (int i = 2; i <= initial.getItemCount(); i++) {
            if (i < initial.getItemCount()) {
                if (switchVar == false) {
                    if (initial.getHighValue(i-1) > initial.getHighValue(i-2) && initial.getHighValue(i-1) > initial.getHighValue(i)) {
                        dataset.setData(0, initial.getHighValue(i-1), 0, 0, 0, 0, i-1);
                        switchVar = true;
                        continue;
                    }
                }
                if (switchVar == true) {
                    if (initial.getLowValue(i-1) < initial.getLowValue(i-2) && initial.getLowValue(i-1) < initial.getLowValue(i)) {
                        dataset.setData(0, initial.getLowValue(i-1), 0, 0, 0, 0, i-1);
                        switchVar = false;
                        continue;
                    }
                }
            }
        }

        return dataset;
    }

}
