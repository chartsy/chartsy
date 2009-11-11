package org.chartsy.bollingerbands;

import java.awt.Color;
import java.awt.Graphics2D;
import java.text.DecimalFormat;
import java.util.Vector;
import org.chartsy.main.chartsy.ChartFrame;
import org.chartsy.main.chartsy.DefaultPainter;
import org.chartsy.main.chartsy.chart.Overlay;
import org.chartsy.main.dataset.DataItem;
import org.chartsy.main.dataset.Dataset;
import org.chartsy.main.utils.ComponentGenerator;
import org.chartsy.main.utils.Properties;
import org.chartsy.main.utils.PropertyItem;
import org.chartsy.main.utils.Range;
import org.chartsy.main.utils.StrokeGenerator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author viorel.gheba
 */
public class BollingerBands extends Overlay {

    public static final String STD = "# x Std. Dev.";
    public static final String PERIOD = "Period";
    public static final String PRICE = "Price";
    public static final String LABEL = "Label";
    public static final String MARKER = "Marker";
    public static final String LOWER_COLOR = "Lower Band Color";
    public static final String LOWER_STYLE = "Lower Band Line Style";
    public static final String MIDDLE_COLOR = "Middle Band Color";
    public static final String MIDDLE_STYLE = "Middle Band Line Style";
    public static final String UPPER_COLOR = "Upper Band Color";
    public static final String UPPER_STYLE = "Upper Band Line Style";
    public static final String INSIDE_COLOR = "Inside Fill";
    public static final String INSIDE_ALPHA = "Inside Alpha";
    public static final String UPPER = "upper";
    public static final String MIDDLE = "middle";
    public static final String LOWER = "lower";

    public BollingerBands() { super("Bollinger Bands", "Description", "Bollinger"); }

    public String getLabel() {
        String price = getStringParam(PRICE); String stddev = getStringParam(STD); String period = getStringParam(PERIOD); String label = getStringParam(LABEL);
        return label + "(" + price + ", " + stddev + ", " + period + ")";
    }

    public String getMarkerLabel(ChartFrame cf, int i) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        StringBuilder sb = new StringBuilder();
        sb.append(getLabel() + "\n");
        double[] values = getValues(cf, i);
        String[] labels = {"Upper Band", "Middle Band", "Lower Band"};
        if (values.length > 0) {
            Color[] colors = getColors();
            for (int j = 0; j < values.length; j++) sb.append("<font color=\"" + Integer.toHexString(colors[j].getRGB() & 0x00ffffff) + "\">&nbsp;&nbsp;&nbsp;" + labels[j] + ":&nbsp;" + df.format(values[j]) + "</font>\n");
        }
        return sb.toString();
    }

    public Range getRange(ChartFrame cf) {
        String price = getStringParam(PRICE);
        Dataset upper = visibleDataset(cf, UPPER);
        Dataset lower = visibleDataset(cf, LOWER);
        if (upper != null && lower != null) {
            Range chartRange = cf.getChartRenderer().getChartRange();
            Range range = new Range(Math.min(chartRange.getLowerBound(), lower.getMinNotZero(price)), Math.max(chartRange.getUpperBound(), upper.getMaxNotZero(price)));
            return range;
        }
        return null;
    }

    public void initialize() {
        PropertyItem[] data = new PropertyItem[] {
            new PropertyItem(STD, ComponentGenerator.JTEXTFIELD, "2"),
            new PropertyItem(PERIOD, ComponentGenerator.JTEXTFIELD, "20"),
            new PropertyItem(PRICE, ComponentGenerator.JCOMBOBOX, Dataset.LIST, Dataset.CLOSE),
            new PropertyItem(LABEL, ComponentGenerator.JTEXTFIELD, "Bollinger"),
            new PropertyItem(MARKER, ComponentGenerator.JCHECKBOX, Boolean.TRUE),
            new PropertyItem(LOWER_COLOR, ComponentGenerator.JLABEL, new Color(0x204a87)),
            new PropertyItem(LOWER_STYLE, ComponentGenerator.JSTROKECOMBOBOX, "0"),
            new PropertyItem(MIDDLE_COLOR, ComponentGenerator.JLABEL, new Color(0x204a87)),
            new PropertyItem(MIDDLE_STYLE, ComponentGenerator.JSTROKECOMBOBOX, "0"),
            new PropertyItem(UPPER_COLOR, ComponentGenerator.JLABEL, new Color(0x204a87)),
            new PropertyItem(UPPER_STYLE, ComponentGenerator.JSTROKECOMBOBOX, "0"),
            new PropertyItem(INSIDE_COLOR, ComponentGenerator.JLABEL, new Color(0x204a87)),
            new PropertyItem(INSIDE_ALPHA, ComponentGenerator.JSLIDER, "25")
        };
        Properties p = new Properties(data);
        setProperties(p);
    }

    public void calculate() {
        int period = getIntParam(PERIOD);
        int stddev = getIntParam(STD);
        Dataset initial = getDataset();
        if (initial != null && !initial.isEmpty()) {
            Dataset middle = initial.getSMA(period); addDataset(MIDDLE, middle);
            Dataset upper = getLowerUpperDataset(initial, middle, period, stddev, UPPER); addDataset(UPPER, upper);
            Dataset lower = getLowerUpperDataset(initial, middle, period, stddev, LOWER); addDataset(LOWER, lower);
        }
    }

    public void paint(Graphics2D g, ChartFrame cf) {
        Dataset middle = visibleDataset(cf, MIDDLE);
        Dataset upper = visibleDataset(cf, UPPER);
        Dataset lower = visibleDataset(cf, LOWER);

        if (middle != null && upper != null && lower != null) {
            String price = getStringParam(PRICE);
            DefaultPainter.insideFill(g, cf, upper, lower, getTransparentColor(getColorParam(INSIDE_COLOR), getIntParam(INSIDE_ALPHA)), price); // paint inside fill
            DefaultPainter.line(g, cf, middle, getColorParam(MIDDLE_COLOR), getStrokeParam(MIDDLE_STYLE), price); // paint middle line
            DefaultPainter.line(g, cf, upper, getColorParam(UPPER_COLOR), getStrokeParam(UPPER_STYLE), price); // paint upper line
            DefaultPainter.line(g, cf, lower, getColorParam(LOWER_COLOR), getStrokeParam(LOWER_STYLE), price); // paint lower line
        }
    }

    private Color getTransparentColor(final Color color, final int alpha) { return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha); }
    
    public Dataset getLowerUpperDataset(final Dataset initial, final Dataset middle, final int period, final int stddev, final String type) {
        Vector<DataItem> items = new Vector<DataItem>();
        for (int i = 0; i < period; i++) {
            DataItem item = new DataItem(initial.getDate(i), 0, 0, 0, 0, 0, 0);
            items.add(item);
        }
        for (int i = period; i < initial.getItemCount(); i++) {
            double opendev = 0;  double closedev = 0; double highdev = 0; double lowdev = 0;
            for (int j = 0; j < period; j++) {
                opendev += Math.pow(initial.getOpenValue(i-j) - middle.getOpenValue(i), 2);
                closedev += Math.pow(initial.getCloseValue(i-j) - middle.getCloseValue(i), 2);
                highdev += Math.pow(initial.getHighValue(i-j) - middle.getHighValue(i), 2);
                lowdev += Math.pow(initial.getLowValue(i-j) - middle.getLowValue(i), 2);
            }
            opendev = Math.sqrt(opendev / period);
            closedev = Math.sqrt(closedev / period);
            highdev = Math.sqrt(highdev / period);
            lowdev = Math.sqrt(lowdev / period);
            if (type.equals(LOWER)) {
                DataItem item = new DataItem(middle.getDate(i), middle.getOpenValue(i) - stddev * opendev, middle.getCloseValue(i) - stddev * closedev, middle.getHighValue(i) - stddev * highdev, middle.getLowValue(i) - stddev * lowdev, 0, 0);
                items.add(item);
            } else if (type.equals(UPPER)) {
                DataItem item = new DataItem(middle.getDate(i), middle.getOpenValue(i) + stddev * opendev, middle.getCloseValue(i) + stddev * closedev, middle.getHighValue(i) + stddev * highdev, middle.getLowValue(i) + stddev * lowdev, 0, 0);
                items.add(item);
            }
        }
        DataItem[] data = items.toArray(new DataItem[items.size()]);
        return new Dataset(data);
    }

    public Color[] getColors() { return new Color[] {getColorParam(UPPER_COLOR), getColorParam(MIDDLE_COLOR), getColorParam(LOWER_COLOR)}; }
    public double[] getValues(ChartFrame cf) {
        Dataset middle = visibleDataset(cf, MIDDLE);
        Dataset upper = visibleDataset(cf, UPPER);
        Dataset lower = visibleDataset(cf, LOWER);
        if (middle != null && upper != null && lower != null) {
            String price = (String) getProperties().getValue("Price");
            return new double[] {upper.getLastPriceValue(price), middle.getLastPriceValue(price), lower.getLastPriceValue(price)};
        }
        return new double[] {};
    }
    public double[] getValues(ChartFrame cf, int i) {
        Dataset middle = visibleDataset(cf, MIDDLE);
        Dataset upper = visibleDataset(cf, UPPER);
        Dataset lower = visibleDataset(cf, LOWER);
        if (middle != null && upper != null && lower != null) {
            String price = (String) getProperties().getValue("Price");
            return new double[] {upper.getPriceValue(i, price), middle.getPriceValue(i, price), lower.getPriceValue(i, price)};
        }
        return new double[] {};
    }
    public boolean getMarkerVisibility() { return getBooleanParam(MARKER); }

    public void readXMLDocument(Element parent) { readFromXMLDocument(parent); }
    public void writeXMLDocument(Document document, Element parent) { writeToXMLDocument(document, parent, "Bollinger Bands"); }

}
