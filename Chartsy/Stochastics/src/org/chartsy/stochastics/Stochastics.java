package org.chartsy.stochastics;

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
public class Stochastics extends Indicator implements Serializable {

    private static final long serialVersionUID = 101L;
    public static final String SLOWD = "slow%D";
    public static final String SLOWK = "slow%K";
    public static final String FASTD = "fast%D";
    public static final String FASTK = "fast%K";

    private IndicatorProperties properties = new IndicatorProperties();

    public Stochastics() {
        super("Stochastics", "Description", "Stochastics");
    }

    public String getLabel() { return properties.getLabel() + " (" + properties.getPeriodK() + ", " + properties.getSmooth() + ", " + properties.getPeriodD() + ")"; }

    public LinkedHashMap getHTML(ChartFrame cf, int i) {
        LinkedHashMap ht = new LinkedHashMap();

        DecimalFormat df = new DecimalFormat("#,##0.00");
        double[] values = getValues(cf, i);
        String[] labels = {"%D:", "%K:"};

        ht.put((properties.getSF() ? "Fast" : "Slow") + getLabel(), " ");
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
        Dataset stoD = visibleDataset(cf, properties.getSF() ? FASTD : SLOWD);
        Dataset stoK = visibleDataset(cf, properties.getSF() ? FASTK : SLOWK);
        if (stoD != null && stoK != null) {
            Range range = getRange(cf);
            Rectangle2D.Double bounds = getBounds();

            DefaultPainter.line(g, cf, range, bounds, stoD, properties.getColorD(), properties.getStrokeD());
            DefaultPainter.line(g, cf, range, bounds, stoK, properties.getColorK(), properties.getStrokeK());
            DefaultPainter.label(g, cf, (properties.getSF() ? "Fast" : "Slow") + getLabel(), bounds); // paint label
        }
    }

    public void calculate() {
        Dataset initial = getDataset();
        if (initial != null && !initial.isEmpty()) {
            Dataset[] datasets = getDataset(initial);
            addDataset(SLOWD, datasets[0]);
            addDataset(SLOWK, datasets[1]);
            addDataset(FASTD, datasets[2]);
            addDataset(FASTK, datasets[3]);
        }
    }

    public boolean hasZeroLine() { return false; }
    public boolean getZeroLineVisibility() { return false; }
    public Color getZeroLineColor() { return null; }
    public Stroke getZeroLineStroke() { return null; }
    public Color[] getColors() { return new Color[] {properties.getColorD(), properties.getColorK()}; }
    public double[] getValues(ChartFrame cf) {
        Dataset stoD = visibleDataset(cf, properties.getSF() ? FASTD : SLOWD);
        Dataset stoK = visibleDataset(cf, properties.getSF() ? FASTK : SLOWK);
        if (stoD != null && stoK != null)
            return new double[] {stoD.getLastPriceValue(Dataset.CLOSE), stoK.getLastPriceValue(Dataset.CLOSE)};
        return new double[] {};
    }
    public double[] getValues(ChartFrame cf, int i) {
        Dataset stoD = visibleDataset(cf, properties.getSF() ? FASTD : SLOWD);
        Dataset stoK = visibleDataset(cf, properties.getSF() ? FASTK : SLOWK);
        if (stoD != null && stoK != null)
            return new double[] {stoD.getPriceValue(i, Dataset.CLOSE), stoK.getPriceValue(i, Dataset.CLOSE)};
        return new double[] {};
    }
    public boolean getMarkerVisibility() { return properties.getMarker(); }
    public AbstractNode getNode() { return new IndicatorNode(properties); }

    private Dataset[] getDataset(final Dataset initial) {
        int periodK = properties.getPeriodK();
        int smooth = properties.getSmooth();
        int periodD = properties.getPeriodD();
        
        Dataset F_K = Dataset.EMPTY(initial); // fast %K
        for (int i = periodK - 1; i < initial.getItemCount(); i++) {
            double lk = initial.getLowValue(i);
            double hk = initial.getHighValue(i);

            for (int j = 0; j < periodK; j++) {
                lk = Math.min(lk, initial.getLowValue(i-j));
                hk = Math.max(hk, initial.getHighValue(i-j));
            }

            double currentFK = hk != lk ? 100 * (initial.getCloseValue(i) - lk) / (hk - lk) : 0;
            currentFK = currentFK < 0 ? 0 : currentFK;

            F_K.setData(0, currentFK, 0, 0, 0, 0, i);
        }

        Dataset F_D = Dataset.SMA(F_K, periodD); // fast %D
        Dataset S_K = Dataset.SMA(F_K, smooth); // slow %K
        Dataset S_D = Dataset.SMA(S_K, periodD); // slow %D

        Dataset[] result = new Dataset[4];
        result[0] = S_D;
        result[1] = S_K;
        result[2] = F_D;
        result[3] = F_K;
        return result;
    }

}
