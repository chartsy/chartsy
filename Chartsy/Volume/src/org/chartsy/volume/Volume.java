package org.chartsy.volume;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.font.LineMetrics;
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
public class Volume extends Indicator implements Serializable {

    private static final long serialVersionUID = 101L;
    public static final String VOLUME = "volume";

    private IndicatorProperties properties = new IndicatorProperties();

    public Volume() { super("Volume", "Description", "Volume"); }

    public String getLabel() { return properties.getLabel(); }

    public LinkedHashMap getHTML(ChartFrame cf, int i) {
        LinkedHashMap ht = new LinkedHashMap();

        DecimalFormat df = new DecimalFormat();
        df.applyPattern("###,###");
        String factor = df.format((int) getVolumeFactor(cf));
        df.applyPattern("###,##0.00");
        double[] values = getValues(cf, i);

        ht.put(getLabel() + " x " + factor, " ");
        if (values.length > 0) {
            Color[] colors = getColors();
            for (int j = 0; j < values.length; j++) {
                ht.put(getFontHTML(colors[j], "Volume:"),
                        getFontHTML(colors[j], df.format(values[j])));
            }
        }
        
        return ht;
    }

    public Range getRange(ChartFrame cf) {
        Dataset volume = visibleDataset(cf, VOLUME);
        Range range = new Range(0, volume.getVolumeMax());
        return range;
    }

    public void calculate() {
        Dataset initial = getDataset();
        if (initial != null && !initial.isEmpty()) {
            Range range = new Range(0, initial.getVolumeMax());
            double factor = Math.pow(10, String.valueOf(Math.round(range.getUpperBound())).length() - 2);
            Vector<DataItem> items = new Vector<DataItem>();
            for (int i = 0; i < initial.getItemCount(); i++) {
                DataItem item = new DataItem(initial.getDate(i), 0, 0, 0, 0, getVolume(initial.getVolumeValue(i), factor), 0);
                items.add(item);
            }
            DataItem[] data = items.toArray(new DataItem[items.size()]);
            Dataset dataset = new Dataset(data);
            addDataset(VOLUME, dataset);
        }
    }

    public void paint(Graphics2D g, ChartFrame cf) {
        Dataset volume = visibleDataset(cf, VOLUME);
        if (volume != null) {
            Range range = getRange(cf);
            Rectangle2D.Double bounds = getBounds();

            DefaultPainter.bar(g, cf, range, bounds, volume, properties.getColor(), Dataset.VOLUME); // paint volume bars

            DecimalFormat df = new DecimalFormat("###,###");
            LineMetrics lm = cf.getChartProperties().getFont().getLineMetrics("012345679", g.getFontRenderContext());
            String factor = df.format((int) getVolumeFactor(cf));
            g.setFont(cf.getChartProperties().getFont());
            g.setPaint(cf.getChartProperties().getFontColor());
            g.drawString(getLabel() + " x " + factor, (float) cf.getChartProperties().getDataOffset().left, (float)(bounds.getMinY() - cf.getChartProperties().getAxisOffset().top + lm.getAscent()));
        }
    }
    
    private double getVolume(double volume, double factor) { return volume / factor; }
    private double getVolumeFactor(ChartFrame cf) { return Math.pow(10, String.valueOf(Math.round(cf.getChartRenderer().getVisibleDataset().getVolumeMax())).length() - 1); }

    public boolean hasZeroLine() { return true; }
    public boolean getZeroLineVisibility() { return properties.getZeroLineVisibility(); }
    public Color getZeroLineColor() { return properties.getZeroLineColor(); }
    public Stroke getZeroLineStroke() { return properties.getZeroLineStroke(); }
    public Color[] getColors() { return new Color[] {properties.getColor()}; }
    public double[] getValues(ChartFrame cf) {
        Dataset volume = visibleDataset(cf, VOLUME);
        if (volume != null) return new double[] {volume.getLastPriceValue(Dataset.VOLUME)};
        return new double[] {};
    }
    public double[] getValues(ChartFrame cf, int i) {
        Dataset volume = visibleDataset(cf, VOLUME);
        if (volume != null) return new double[] {volume.getPriceValue(i, Dataset.VOLUME)};
        return new double[] {};
    }
    public boolean getMarkerVisibility() { return properties.getMarker(); }

    public AbstractNode getNode() {
        return new IndicatorNode(properties);
    }

}
