package org.chartsy.rsi;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
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
public class RSI extends Indicator implements Serializable {

    private static final long serialVersionUID = 101L;
    public static final String RSI = "rsi";

    private IndicatorProperties properties = new IndicatorProperties();

    public RSI() {
        super("RSI", "Description", "RSI");
    }

    public String getLabel() { return properties.getLabel() + " (" + properties.getPeriod() + ")"; }

    public LinkedHashMap getHTML(ChartFrame cf, int i) {
        LinkedHashMap ht = new LinkedHashMap();

        DecimalFormat df = new DecimalFormat("#,##0.00");
        double[] values = getValues(cf, i);
        String[] labels = {"RSI:"};

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

    public Range getRange(ChartFrame cf) { return new Range(0, 100); }

    public void paint(Graphics2D g, ChartFrame cf) {
        Dataset dataset = visibleDataset(cf, RSI);

        if (dataset != null) {
            Range range = getRange(cf);
            Rectangle2D.Double bounds = getBounds();

            DefaultPainter.line(g, cf, range, bounds, dataset, properties.getColor(), properties.getStroke()); // paint line
            paintFill(g, cf, dataset, bounds, range, properties.getInsideTransparentColor(), 30, 0); // paint fill
            paintFill(g, cf, dataset, bounds, range, properties.getInsideTransparentColor(), 70, 100); // paint fill

            DefaultPainter.label(g, cf, getLabel(), bounds); // paint label
        }
    }

    private void paintFill(Graphics2D g, ChartFrame cf, Dataset dataset, Rectangle2D.Double bounds, Range range, Color color, int f, int t) {
        if (properties.getInsideVisibility()) {
            double x;
            double y = cf.getChartRenderer().valueToY(f, bounds, range);
            double dx;
            Range r = new Range(f > t ? t : f, f > t ? f : t);
            
            g.setColor(color);
            for (int i = 1; i < dataset.getItemCount(); i++) {
                double value1 = dataset.getCloseValue(i-1);
                double value2 = dataset.getCloseValue(i);
                if (value1 != 0 && value2 != 0) {
                    Point2D.Double p1 = cf.getChartRenderer().valueToJava2D(i-1, value1, bounds, range);
                    Point2D.Double p2 = cf.getChartRenderer().valueToJava2D(i, value2, bounds, range);
                    if (!r.contains(value1) && r.contains(value2)) {
                        GeneralPath gp = new GeneralPath();
                        dx = (y - p1.y)/(p2.y - p1.y);
                        x = p1.x + dx*(p2.x - p1.x);
                        gp.moveTo((float)x, (float)y);
                        gp.lineTo((float)p2.x, (float)p2.y);
                        gp.lineTo((float)p2.x, (float)y);
                        gp.closePath();
                        g.fill(gp);
                    } else if (r.contains(value1) && r.contains(value2)) {
                        GeneralPath gp = new GeneralPath();
                        gp.moveTo((float)p1.x, (float)y);
                        gp.lineTo((float)p1.x, (float)p1.y);
                        gp.lineTo((float)p2.x, (float)p2.y);
                        gp.lineTo((float)p2.x, (float)y);
                        gp.closePath();
                        g.fill(gp);
                    } else if (r.contains(value1) && !r.contains(value2)) {
                        GeneralPath gp = new GeneralPath();
                        dx = (y - p1.y)/(p2.y - p1.y);
                        x = p1.x + dx*(p2.x - p1.x);
                        gp.moveTo((float) p1.x, (float) p1.y);
                        gp.lineTo((float) x, (float) y);
                        gp.lineTo((float) p1.x, (float) y);
                        gp.closePath();
                        g.fill(gp);
                    }
                }
            }
        }
    }

    public void calculate() {
        Dataset initial = getDataset();
        if (initial != null && !initial.isEmpty()) {
            int period = properties.getPeriod();
            Dataset dataset = getDataset(initial, period); addDataset(RSI, dataset);
        }
    }

    public boolean hasZeroLine() { return false; }
    public boolean getZeroLineVisibility() { return false; }
    public Color getZeroLineColor() { return null; }
    public Stroke getZeroLineStroke() { return null; }
    public Color[] getColors() { return new Color[] {properties.getColor()}; }
    public double[] getValues(ChartFrame cf) {
        Dataset dataset = visibleDataset(cf, RSI);
        if (dataset != null)
            return new double[] {dataset.getLastPriceValue(Dataset.CLOSE)};
        return new double[] {};
    }
    public double[] getValues(ChartFrame cf, int i) {
        Dataset dataset = visibleDataset(cf, RSI);
        if (dataset != null)
            return new double[] {dataset.getPriceValue(i, Dataset.CLOSE)};
        return new double[] {};
    }
    public boolean getMarkerVisibility() { return properties.getMarker(); }
    public AbstractNode getNode() { return new IndicatorNode(properties); }

    private Dataset getDataset(final Dataset initial, final int period) {
        Vector<DataItem> items = new Vector<DataItem>();
        int itemsCount = initial.getItemCount();

        double[] close = new double[itemsCount];
        for (int i = 0; i < itemsCount; i++) close[i] = initial.getCloseValue(i);

        for (int i = 0; i < period; i++) {
            DataItem item = new DataItem(initial.getDate(i), 0, 0, 0, 0, 0, 0);
            items.add(item);
        }

        for (int i = period; i < itemsCount; i++) {
            double adva = 0;
            double decl = 0;
            double currentRSI;

            for (int j = 0; j < period; j++) {
                if (initial.getCloseValue(i-j) > initial.getCloseValue(i-j-1)) {
                    adva += initial.getCloseValue(i-j) - initial.getCloseValue(i-j-1);
                } else if (initial.getCloseValue(i-j) < initial.getCloseValue(i-j-1)) {
                    decl += initial.getCloseValue(i-j-1) - initial.getCloseValue(i-j);
                }
            }

            if (decl == 0) { currentRSI = 100000D; }
            else { currentRSI = (double) adva/decl + 1; }

            DataItem item = new DataItem(initial.getDate(i), 0, (100 - 100/currentRSI), 0, 0, 0, 0);
            items.add(item);
        }

        DataItem[] data = items.toArray(new DataItem[items.size()]);
        return new Dataset(data);
    }

}
