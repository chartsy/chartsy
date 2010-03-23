package org.chartsy.fi;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
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
public class FI extends Indicator implements Serializable {

    private static final long serialVersionUID = 101L;
    public static final String FI1 = "fi1";
    public static final String FI2 = "fi2";

    private IndicatorProperties properties = new IndicatorProperties();

    public FI() {
        super("FI", "Description", "FI");
    }

    public String getLabel() { return properties.getLabel() + "(" + properties.getPeriod1() + ", " + properties.getPeriod2() + ")"; }

    public LinkedHashMap getHTML(ChartFrame cf, int i) {
        LinkedHashMap ht = new LinkedHashMap();

        DecimalFormat df = new DecimalFormat("###,###");
        double factor = getFactor(cf);
        double[] values = getValues(cf, i);
        String[] labels = {"FI(" + properties.getPeriod1() + "):", "FI(" + properties.getPeriod2() + "):"};

        ht.put(getLabel() + " x " + df.format((int)factor), " ");
        df = new DecimalFormat("#,##0.00");
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
        Dataset dataset1 = visibleDataset(cf, FI1);
        Dataset dataset2 = visibleDataset(cf, FI2);
        double factor = getFactor(cf);
        if (dataset1 != null && dataset2 != null) {
            Range range =  new Range(dataset1.getMin(Dataset.CLOSE)/factor, dataset1.getMax(Dataset.CLOSE)/factor);
            range = Range.combine(range, new Range(dataset2.getMin(Dataset.CLOSE)/factor, dataset2.getMax(Dataset.CLOSE)/factor));
            return range;
        }
        return null;
    }

    public void paint(Graphics2D g, ChartFrame cf) {
        Dataset dataset1 = visibleDataset(cf, FI1);
        Dataset dataset2 = visibleDataset(cf, FI2);

        if (dataset1 != null && dataset2 != null) {
            Range range = getRange(cf);
            Rectangle2D.Double bounds = getBounds();
            double factor = getFactor(cf);

            DefaultPainter.line(g, cf, range, bounds, Dataset.DIV(dataset1, factor), properties.getColor1(), properties.getStroke1()); // paint line
            DefaultPainter.line(g, cf, range, bounds, Dataset.DIV(dataset2, factor), properties.getColor2(), properties.getStroke2()); // paint line
            DecimalFormat df = new DecimalFormat("###,###");
            DefaultPainter.label(g, cf, getLabel() + " x " + df.format((int)factor), bounds); // paint label
        }
    }

    public void calculate() {
        Dataset initial = getDataset();
        if (initial != null && !initial.isEmpty()) {
            int period1 = properties.getPeriod1();
            int period2 = properties.getPeriod2();
            Dataset dataset1 = getDataset(initial, period1); addDataset(FI1, dataset1);
            Dataset dataset2 = getDataset(initial, period2); addDataset(FI2, dataset2);
        }
    }

    public boolean hasZeroLine() { return false; }
    public boolean getZeroLineVisibility() { return false; }
    public Color getZeroLineColor() { return null; }
    public Stroke getZeroLineStroke() { return null; }
    public Color[] getColors() { return new Color[] {properties.getColor1(), properties.getColor2()}; }
    public double[] getValues(ChartFrame cf) {
        Dataset dataset1 = visibleDataset(cf, FI1);
        Dataset dataset2 = visibleDataset(cf, FI2);
        double factor = getFactor(cf);
        if (dataset1 != null && dataset2 != null)
            return new double[] {dataset1.getLastPriceValue(Dataset.CLOSE)/factor, dataset2.getLastPriceValue(Dataset.CLOSE)/factor};
        return new double[] {};
    }
    public double[] getValues(ChartFrame cf, int i) {
        Dataset dataset1 = visibleDataset(cf, FI1);
        Dataset dataset2 = visibleDataset(cf, FI2);
        double factor = getFactor(cf);
        if (dataset1 != null && dataset2 != null)
            return new double[] {dataset1.getPriceValue(i, Dataset.CLOSE)/factor, dataset2.getPriceValue(i, Dataset.CLOSE)/factor};
        return new double[] {};
    }
    public boolean getMarkerVisibility() { return properties.getMarker(); }
    public AbstractNode getNode() { return new IndicatorNode(properties); }

    private Dataset getDataset(final Dataset initial, final int period) {
        Vector<DataItem> items = new Vector<DataItem>();
        items.add(new DataItem(initial.getDate(0), 0, 0, 0, 0, 0, 0));

        for (int i = 1; i < initial.getItemCount(); i++) {
            double fi = initial.getVolumeValue(i) * (initial.getCloseValue(i) - initial.getCloseValue(i-1));
            items.add(new DataItem(initial.getDate(i), 0, fi, 0, 0, 0, 0));
        }

        DataItem[] data = items.toArray(new DataItem[items.size()]);
        return Dataset.SMA(new Dataset(data), period);
    }

    private double getFactor(ChartFrame cf) {
        Dataset dataset1 = visibleDataset(cf, FI1);
        Dataset dataset2 = visibleDataset(cf, FI2);

        if (dataset1 != null && dataset2 != null) {
            Range range =  new Range(dataset1.getMin(Dataset.CLOSE), dataset1.getMax(Dataset.CLOSE));
            range = Range.combine(range, new Range(dataset2.getMin(Dataset.CLOSE), dataset2.getMax(Dataset.CLOSE)));
            int max = Integer.toString((int)Math.round(range.getUpperBound())).length() - 1;
            int min = Integer.toString((int)Math.round(range.getLowerBound())).length() - 1;
            int scale = Math.min(max, min);
            if (scale > 1) scale--;
            return Math.pow(10, scale);
        }
        
        return 1;
    }

}
