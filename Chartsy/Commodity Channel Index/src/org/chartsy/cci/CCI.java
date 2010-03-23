package org.chartsy.cci;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
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
public class CCI extends Indicator implements Serializable {

    private static final long serialVersionUID = 101L;
    public static final String CCI = "cci";

    private IndicatorProperties properties = new IndicatorProperties();

    public CCI() { 
        super("CCI", "Description", "CCI");
    }

    public String getLabel() { return properties.getLabel() + "(" + properties.getPeriod() + ")"; }

    public LinkedHashMap getHTML(ChartFrame cf, int i) {
        LinkedHashMap ht = new LinkedHashMap();

        DecimalFormat df = new DecimalFormat("#,##0.00");
        double[] values = getValues(cf, i);
        String[] labels = {"CCI:"};

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
        Dataset dataset = visibleDataset(cf, CCI);
        if (dataset != null) return new Range(dataset.getMin(Dataset.CLOSE), dataset.getMax(Dataset.CLOSE));
        return null;
    }

    public void paint(Graphics2D g, ChartFrame cf) {
        Dataset dataset = visibleDataset(cf, CCI);

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
            int period = properties.getPeriod();
            Dataset dataset = getDataset(initial, period); addDataset(CCI, dataset);
        }
    }

    public boolean hasZeroLine() { return false; }
    public boolean getZeroLineVisibility() { return false; }
    public Color getZeroLineColor() { return null; }
    public Stroke getZeroLineStroke() { return null; }
    public Color[] getColors() { return new Color[] {properties.getColor()}; }
    public double[] getValues(ChartFrame cf) {
        Dataset dataset = visibleDataset(cf, CCI);
        if (dataset != null)
            return new double[] {dataset.getLastPriceValue(Dataset.CLOSE)};
        return new double[] {};
    }
    public double[] getValues(ChartFrame cf, int i) {
        Dataset dataset = visibleDataset(cf, CCI);
        if (dataset != null)
            return new double[] {dataset.getPriceValue(i, Dataset.CLOSE)};
        return new double[] {};
    }
    public boolean getMarkerVisibility() { return properties.getMarker(); }
    public AbstractNode getNode() { return new IndicatorNode(properties); }

    private Dataset getDataset(final Dataset initial, final int period) {
        Vector<DataItem> items = new Vector<DataItem>();

        for (int i = 0; i < period - 1; i++) {
            items.add(new DataItem(initial.getDate(i), 0, 0, 0, 0, 0, 0));
        }

        for (int i = period - 1; i < initial.getItemCount(); i++) {
            double TP = (initial.getHighValue(i) + initial.getLowValue(i) + initial.getCloseValue(i)) / 3;

            double TPMA = 0D;
            for (int j = i - period + 1; j <= i; j++) {
                TPMA += ((initial.getHighValue(j) + initial.getLowValue(j) + initial.getCloseValue(j)) / 3);
            }
            TPMA /= (double)period;

            double MD = 0D;
            for (int j = i - period + 1; j <= i; j++) {
                MD += Math.abs((initial.getHighValue(j) + initial.getLowValue(j) + initial.getCloseValue(j)) / 3 - TPMA);
            }
            MD /= (double)period;

            double close = (TP - TPMA)/(0.015D * MD);
            items.add(new DataItem(initial.getDate(i), 0, close, 0, 0, 0, 0));
        }

        DataItem[] data = items.toArray(new DataItem[items.size()]);
        return new Dataset(data);
    }

}
