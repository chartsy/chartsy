package org.chartsy.bollingerb;

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
import org.chartsy.main.utils.CalcUtil;
import org.chartsy.main.utils.Range;
import org.chartsy.main.utils.StrokeGenerator;
import org.openide.nodes.AbstractNode;

/**
 *
 * @author viorel.gheba
 */
public class BollingerB extends Indicator implements Serializable {

    private static final long serialVersionUID = 101L;
    public static final String UPPER = "upper";
    public static final String LOWER = "lower";
    public static final String SVE_BB = "SVE_BB";
    public static final String D50 = "50";

    private IndicatorProperties properties;

    public BollingerB() { super("SVE_BB%b", "Description", "SVE_BB%b"); properties = new IndicatorProperties(); }

    public String getLabel() { return properties.getLabel() + " (" + properties.getPeriod() + ")"; }

    public LinkedHashMap getHTML(ChartFrame cf, int i) {
        LinkedHashMap ht = new LinkedHashMap();

        DecimalFormat df = new DecimalFormat("#,##0.00");
        double[] values = getValues(cf, i);
        String[] labels = {"SVE_BB%b:"};

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
        Dataset dataset = visibleDataset(cf, SVE_BB);
        Dataset upper = visibleDataset(cf, UPPER);
        Dataset lower = visibleDataset(cf, LOWER);

        if (dataset != null) {
            Range range = new Range(dataset.getMin(Dataset.CLOSE), dataset.getMax(Dataset.CLOSE));
            if (upper != null) range = Range.combine(range, new Range(upper.getMin(Dataset.CLOSE), upper.getMax(Dataset.CLOSE)));
            if (lower != null) range = Range.combine(range, new Range(lower.getMin(Dataset.CLOSE), lower.getMax(Dataset.CLOSE)));
            return range;
        }
        return null;
    }

    public void calculate() {
        Dataset initial = getDataset();
        if (initial != null && !initial.isEmpty()) {
            Dataset dataset = getSVE_BB(initial); addDataset(SVE_BB, dataset);
            Dataset upper = getLowerUpperDataset(dataset, UPPER); addDataset(UPPER, upper);
            Dataset lower = getLowerUpperDataset(dataset, LOWER); addDataset(LOWER, lower);
            Dataset d50 = get50Dataset(dataset); addDataset(D50, d50);
        }
    }

    public void paint(Graphics2D g, ChartFrame cf) {
        Dataset dataset = visibleDataset(cf, SVE_BB);
        Dataset upper = visibleDataset(cf, UPPER);
        Dataset lower = visibleDataset(cf, LOWER);
        Dataset d50 = visibleDataset(cf, D50);

        if (dataset != null) {
            Range range = getRange(cf);
            Rectangle2D.Double bounds = getBounds();

            if (d50 != null) DefaultPainter.line(g, cf, range, bounds, d50, Color.RED, StrokeGenerator.getStroke(1));
            if (upper != null) DefaultPainter.line(g, cf, range, bounds, upper, Color.RED, StrokeGenerator.getStroke(1));
            if (lower != null) DefaultPainter.line(g, cf, range, bounds, lower, Color.RED, StrokeGenerator.getStroke(1));

            DefaultPainter.line(g, cf, range, bounds, dataset, properties.getColor(), properties.getStroke());
            DefaultPainter.label(g, cf, getLabel(), bounds);
        }
    }

    public boolean hasZeroLine() { return false; }
    public boolean getZeroLineVisibility() { return false; }
    public Color getZeroLineColor() { return null; }
    public Stroke getZeroLineStroke() { return null; }
    public Color[] getColors() { return new Color[] {properties.getColor()}; }
    public double[] getValues(ChartFrame cf) {
        Dataset dataset = visibleDataset(cf, SVE_BB);
        if (dataset != null)
            return new double[] {dataset.getLastPriceValue(Dataset.CLOSE)};
        return new double[] {};
    }
    public double[] getValues(ChartFrame cf, int i) {
        Dataset dataset = visibleDataset(cf, SVE_BB);
        if (dataset != null)
            return new double[] {dataset.getPriceValue(i, Dataset.CLOSE)};
        return new double[] {};
    }
    public boolean getMarkerVisibility() { return properties.getMarker(); }

    public AbstractNode getNode() {
        return new IndicatorNode(properties);
    }

    private Dataset getHaC(Dataset dataset) {
        Vector<DataItem> list = new Vector<DataItem>();

        double o = dataset.getOpenValue(0);
        double c = dataset.getCloseValue(0);
        double h = dataset.getHighValue(0);
        double l = dataset.getLowValue(0);
        double prev = (o+c+h+l)/4;
        list.add(new DataItem(dataset.getDate(0), 0, prev, 0, 0, 0, 0));
        
        for (int i = 1; i < dataset.getItemCount(); i++) {
            o = dataset.getOpenValue(i-1);
            c = dataset.getCloseValue(i-1);
            h = dataset.getHighValue(i-1);
            l = dataset.getLowValue(i-1);

            double haOpen = ((o+c+h+l)/4 + prev)/2;
            prev = haOpen;

            o = dataset.getOpenValue(i);
            c = dataset.getCloseValue(i);
            h = dataset.getHighValue(i);
            l = dataset.getLowValue(i);

            double haClose = ((o+c+h+l)/4 + haOpen + Math.max(h, haOpen) + Math.min(l, haOpen))/4;

            list.add(new DataItem(dataset.getDate(i), 0, haClose, 0, 0, 0, 0));
        }

        DataItem[] items = list.toArray(new DataItem[list.size()]);
        return new Dataset(items);
    }

    private Dataset getSVE_BB(final Dataset initial) {
        int period = properties.getPeriod();
        int temaPeriod = properties.getTemaPeriod();
        Vector<DataItem> items = new Vector<DataItem>();

        Dataset haC = getHaC(initial);
        Dataset TMA1 = Dataset.TEMA(haC, temaPeriod);
        Dataset TMA2 = Dataset.TEMA(TMA1, temaPeriod);
        Dataset ZLHA = Dataset.TEMA(Dataset.SUM(TMA1, Dataset.DIFF(TMA1, TMA2)), temaPeriod);
        Dataset MOV = Dataset.WMA(ZLHA, period);

        int j = 0;
        for (j = 0; j < initial.getItemCount() && (MOV.getCloseValue(j)==0); j++)
            items.add(new DataItem(initial.getDate(j), 0, 0, 0, 0, 0, 0));

        for (int i = j; i < initial.getItemCount(); i++) {
            double mov = MOV.getCloseValue(i);
            double stddev = CalcUtil.stdDev(ZLHA, Dataset.CLOSE, i, period);
            double close = stddev == 0 ? 0 : ((ZLHA.getCloseValue(i) + 2*stddev - mov)/(4*stddev))*100;
            items.add(new DataItem(initial.getDate(i), 0, close, 0, 0, 0, 0));
        }

        DataItem[] data = items.toArray(new DataItem[items.size()]);
        return new Dataset(data);
    }

    private Dataset get50Dataset(final Dataset initial) {
        Vector<DataItem> items = new Vector<DataItem>();

        int j = 0;
        for (j = 0; j < initial.getItemCount() && (initial.getCloseValue(j)==0); j++)
            items.add(new DataItem(initial.getDate(j), 0, 0, 0, 0, 0, 0));

        for (int i = j; i < initial.getItemCount(); i++)
            items.add(new DataItem(initial.getDate(i), 0, 50, 0, 0, 0, 0));
        
        DataItem[] data = items.toArray(new DataItem[items.size()]);
        return new Dataset(data);
    }

    private Dataset getLowerUpperDataset(final Dataset initial, final String type) {
        int stdPeriod = properties.getStdPeriod();
        double sgn = type.equals(LOWER) ? -1 : 1;
        double afw = type.equals(LOWER) ? properties.getStdLow() : properties.getStdHigh();
        Vector<DataItem> items = new Vector<DataItem>();

        int j = 0;
        for (j = 0; j < initial.getItemCount() && (initial.getCloseValue(j)==0); j++)
            items.add(new DataItem(initial.getDate(j), 0, 0, 0, 0, 0, 0));

        for (int i = j; i < initial.getItemCount(); i++) {
            double stddev = CalcUtil.stdDev(initial, Dataset.CLOSE, i, stdPeriod);
            items.add(new DataItem(initial.getDate(i), 0, (50D + (sgn * stddev * afw)), 0, 0, 0, 0));
        }
        DataItem[] data = items.toArray(new DataItem[items.size()]);
        return new Dataset(data);
    }

}
