package org.chartsy.moneyflow;

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
import org.chartsy.main.updater.AbstractUpdater;
import org.chartsy.main.utils.Range;
import org.openide.nodes.AbstractNode;

/**
 *
 * @author viorel.gheba
 */
public class MoneyFlow extends Indicator implements Serializable {

    private static final long serialVersionUID = 101L;
    public static final String MFH = "mfh";
    public static final String MFL = "mfl";

    private IndicatorProperties properties = new IndicatorProperties();

    public MoneyFlow() {
        super("Money Flow", "Description", "Money Flow");
    }

    public String getLabel() { return properties.getLabel(); }

    public LinkedHashMap getHTML(ChartFrame cf, int i) {
        LinkedHashMap ht = new LinkedHashMap();

        DecimalFormat df = new DecimalFormat("#,##0.00");
        double[] values = getValues(cf, i);
        String[] labels = new String[] {"+MF:", "-MF:"};

        ht.put(getLabel(), " ");
        if (cf.getTime().equals(AbstractUpdater.DAILY)) {
            if (values.length > 0) {
                Color[] colors = getColors();
                for (int j = 0; j < values.length; j++) {
                    ht.put(getFontHTML(colors[j], labels[j]),
                            getFontHTML(colors[j], df.format(values[j])));
                }
            }
        }

        return ht;
    }

    public Range getRange(ChartFrame cf) {
        Dataset mfh = visibleDataset(cf, MFH);
        Dataset mfl = visibleDataset(cf, MFL);
        double factor = getFactor(cf);

        if (mfh != null && mfl != null) {
            double max = mfh.getCloseValue(0);
            double min = mfl.getCloseValue(0);
            for (int i = 1; i < mfh.getItemCount(); i++) {
                if (max < mfh.getCloseValue(i)) max = mfh.getCloseValue(i);
                if (min > mfl.getCloseValue(i)) min = mfl.getCloseValue(i);
            }
            Range range = new Range((min - (max - min) * 0.2d)/factor, (max + (max - min) * 0.2d)/factor);
            return range;
        }
        
        return new Range();
    }

    public void paint(Graphics2D g, ChartFrame cf) {
        if (cf.getTime().equals(AbstractUpdater.DAILY)) {
            Dataset mfh = visibleDataset(cf, MFH);
            Dataset mfl = visibleDataset(cf, MFL);

            if (mfh != null && mfl != null) {
                Range range = getRange(cf);
                Rectangle2D.Double bounds = getBounds();
                double factor = getFactor(cf);

                DefaultPainter.bar(g, cf, range, bounds, Dataset.DIV(mfh, factor), properties.getMFHColor());
                DefaultPainter.bar(g, cf, range, bounds, Dataset.DIV(mfl, factor), properties.getMFLColor());
                DecimalFormat df = new DecimalFormat("###,###");
                DefaultPainter.label(g, cf, getLabel() + " x " + df.format((int)factor), bounds); // paint label
            }
        } else {
            Rectangle2D.Double bounds = getBounds();
            DefaultPainter.label(g, cf, "Money Flow indicator is available only for daily data", bounds); // paint label
        }
    }

    public void calculate() {
        Dataset initial = getDataset();
        if (initial != null && !initial.isEmpty()) {
            Dataset mfh = Dataset.EMPTY(initial);
            Dataset mfl = Dataset.EMPTY(initial);

            mfh.setData(0, ((initial.getCloseValue(0) + initial.getHighValue(0) + initial.getLowValue(0)) / 3) * initial.getVolumeValue(0), 0, 0, 0, 0, 0);
            for (int i = 1; i < initial.getItemCount(); i++) {
                double tp1 = (initial.getCloseValue(i) + initial.getHighValue(i) + initial.getLowValue(i)) / 3;
                double tp2 = (initial.getCloseValue(i-1) + initial.getHighValue(i-1) + initial.getLowValue(i-1)) / 3;
                double rmf = tp1 * initial.getVolumeValue(i);

                if (tp1 > tp2) mfh.setData(0, rmf, 0, 0, 0, 0, i);
                else if (tp1 < tp2) mfl.setData(0, -1*rmf, 0, 0, 0, 0, i);
            }

            addDataset(MFH, mfh);
            addDataset(MFL, mfl);
        }
    }

    public boolean hasZeroLine() { return true; }
    public boolean getZeroLineVisibility() { return properties.getZeroLineVisibility(); }
    public Color getZeroLineColor() { return properties.getZeroLineColor(); }
    public Stroke getZeroLineStroke() { return properties.getZeroLineStroke(); }
    public Color[] getColors() { 
        return new Color[] {properties.getMFHColor(), properties.getMFLColor()};
    }
    public double[] getValues(ChartFrame cf) {
        Dataset mfh = visibleDataset(cf, MFH);
        Dataset mfl = visibleDataset(cf, MFL);
        double factor = getFactor(cf);
        if (mfh != null && mfl != null)
            return new double[] {mfh.getLastPriceValue(Dataset.CLOSE)/factor, mfl.getLastPriceValue(Dataset.CLOSE)/factor};
        return new double[] {};
    }
    public double[] getValues(ChartFrame cf, int i) {
        Dataset mfh = visibleDataset(cf, MFH);
        Dataset mfl = visibleDataset(cf, MFL);
        double factor = getFactor(cf);
        if (mfh != null && mfl != null)
            return new double[] {mfh.getPriceValue(i, Dataset.CLOSE)/factor, mfl.getPriceValue(i, Dataset.CLOSE)/factor};
        return new double[] {};
    }
    public boolean getMarkerVisibility() { return properties.getMarker(); }
    public AbstractNode getNode() { return new IndicatorNode(properties); }

    private double getFactor(ChartFrame cf) {
        Dataset mfh = visibleDataset(cf, MFH);
        Dataset mfl = visibleDataset(cf, MFL);

        if (mfh != null && mfl != null) {
            double max = mfh.getCloseValue(0);
            double min = mfl.getCloseValue(0);
            for (int i = 1; i < mfh.getItemCount(); i++) {
                if (max < mfh.getCloseValue(i)) max = mfh.getCloseValue(i);
                if (min > mfl.getCloseValue(i)) min = mfl.getCloseValue(i);
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
