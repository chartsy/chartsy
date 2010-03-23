package org.chartsy.mfi;

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
public class MFI extends Indicator implements Serializable {

    private static final long serialVersionUID = 101L;
    public static final String MFI = "mfi";

    private IndicatorProperties properties = new IndicatorProperties();

    public MFI() {
        super("MFI", "Description", "MFI");
    }

    public String getLabel() { return properties.getLabel() + " (" + properties.getPeriod() + ")"; }

    public LinkedHashMap getHTML(ChartFrame cf, int i) {
        LinkedHashMap ht = new LinkedHashMap();

        DecimalFormat df = new DecimalFormat("#,##0.00");
        double[] values = getValues(cf, i);
        String[] labels = {"MFI:"};

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
        Dataset dataset = visibleDataset(cf, MFI);

        if (dataset != null) {
            Range range = getRange(cf);
            Rectangle2D.Double bounds = getBounds();

            DefaultPainter.line(g, cf, range, bounds, dataset, properties.getColor(), properties.getStroke()); // paint line
            paintFill(g, cf, dataset, bounds, range, properties.getInsideTransparentColor(), 20, 0); // paint fill
            paintFill(g, cf, dataset, bounds, range, properties.getInsideTransparentColor(), 80, 100); // paint fill

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
            Dataset dataset = getDataset(initial, period); addDataset(MFI, dataset);
        }
    }

    public boolean hasZeroLine() { return false; }
    public boolean getZeroLineVisibility() { return false; }
    public Color getZeroLineColor() { return null; }
    public Stroke getZeroLineStroke() { return null; }
    public Color[] getColors() { return new Color[] {properties.getColor()}; }
    public double[] getValues(ChartFrame cf) {
        Dataset dataset = visibleDataset(cf, MFI);
        if (dataset != null)
            return new double[] {dataset.getLastPriceValue(Dataset.CLOSE)};
        return new double[] {};
    }
    public double[] getValues(ChartFrame cf, int i) {
        Dataset dataset = visibleDataset(cf, MFI);
        if (dataset != null)
            return new double[] {dataset.getPriceValue(i, Dataset.CLOSE)};
        return new double[] {};
    }
    public boolean getMarkerVisibility() { return properties.getMarker(); }
    public AbstractNode getNode() { return new IndicatorNode(properties); }

    private Dataset getDataset(final Dataset initial, final int period) {
        Dataset posMF = Dataset.EMPTY(initial);
        Dataset negMF = Dataset.EMPTY(initial);
        for (int i = 1; i < initial.getItemCount(); i++) {
            double tp1 = (initial.getCloseValue(i) + initial.getHighValue(i) + initial.getLowValue(i)) / 3;
            double tp2 = (initial.getCloseValue(i-1) + initial.getHighValue(i-1) + initial.getLowValue(i-1)) / 3;
            double rmf = tp1 * initial.getVolumeValue(i);

            if (tp1 > tp2) posMF.setData(0, rmf, 0, 0, 0, 0, i);
            else negMF.setData(0, rmf, 0, 0, 0, 0, i);
        }

        Dataset dataset = Dataset.EMPTY(initial);
        for (int i = period; i < initial.getItemCount(); i++) {
            double posSum = 0;
            double negSum = 0;
            for (int j = 0; j < period; j++) {
                posSum += posMF.getCloseValue(i-j);
                negSum += negMF.getCloseValue(i-j);
            }
            double mr = posSum / negSum;
            double mfi = 100 - (100 / (1 + mr));
            dataset.setData(0, mfi, 0, 0, 0, 0, i);
        }

        return dataset;
    }

}
