package org.chartsy.volume;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Vector;
import org.chartsy.main.chartsy.ChartFrame;
import org.chartsy.main.chartsy.DefaultPainter;
import org.chartsy.main.chartsy.chart.Indicator;
import org.chartsy.main.dataset.DataItem;
import org.chartsy.main.dataset.Dataset;
import org.chartsy.main.utils.ComponentGenerator;
import org.chartsy.main.utils.Properties;
import org.chartsy.main.utils.PropertyItem;
import org.chartsy.main.utils.Range;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author viorel.gheba
 */
public class Volume extends Indicator {

    public static final String LABEL = "Label";
    public static final String MARKER = "Marker";
    public static final String ZERO_COLOR = "Zero Line Color";
    public static final String ZERO_STYLE = "Zero Line Style";
    public static final String ZERO_VISIBILITY = "Zero Line Visibility";
    public static final String COLOR = "Color";
    public static final String VOLUME = "volume";

    public Volume() { super("Volume", "Description", "Volume"); }

    public String getLabel() {
        if (getProperties() == null)
            return getDialogLabel();
        else {
            String label = getStringParam(LABEL);
            return label;
        }
    }

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

    public void initialize() {
        PropertyItem[] data = new PropertyItem[] {
            new PropertyItem(LABEL, ComponentGenerator.JTEXTFIELD, "Volume"),
            new PropertyItem(MARKER, ComponentGenerator.JCHECKBOX, Boolean.TRUE),
            new PropertyItem(ZERO_COLOR, ComponentGenerator.JLABEL, new Color(0xeeeeec)),
            new PropertyItem(ZERO_STYLE, ComponentGenerator.JSTROKECOMBOBOX, "0"),
            new PropertyItem(ZERO_VISIBILITY, ComponentGenerator.JCHECKBOX, Boolean.TRUE),
            new PropertyItem(COLOR, ComponentGenerator.JLABEL, new Color(0xf57900))
        };
        Properties p = new Properties(data);
        setProperties(p);
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

            DefaultPainter.bar(g, cf, range, bounds, volume, getColorParam(COLOR), Dataset.VOLUME); // paint volume bars

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
    public boolean getZeroLineVisibility() { return getBooleanParam(ZERO_VISIBILITY); }
    public Color getZeroLineColor() { return getColorParam(ZERO_COLOR); }
    public Stroke getZeroLineStroke() { return getStrokeParam(ZERO_STYLE); }
    public Color[] getColors() { return new Color[] {getColorParam(COLOR)}; }
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
    public boolean getMarkerVisibility() { return getBooleanParam(MARKER); }

    public void readXMLDocument(Element parent) { readFromXMLDocument(parent); }
    public void writeXMLDocument(Document document, Element parent) { writeToXMLDocument(document, parent, "Volume"); }

}
