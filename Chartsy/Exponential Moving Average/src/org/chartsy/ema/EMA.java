package org.chartsy.ema;

import java.awt.Color;
import java.awt.Graphics2D;
import java.text.DecimalFormat;
import org.chartsy.main.chartsy.ChartFrame;
import org.chartsy.main.chartsy.DefaultPainter;
import org.chartsy.main.chartsy.chart.Overlay;
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
public class EMA extends Overlay {

    public static final String PERIOD = "Period";
    public static final String PRICE = "Price";
    public static final String LABEL = "Label";
    public static final String MARKER = "Marker";
    public static final String COLOR = "Color";
    public static final String STYLE = "Style";
    public static final String EMA = "ema";

    public EMA() { super("EMA", "Description", "EMA"); }

    public String getLabel() {
        if (getProperties() == null) {
            return getDialogLabel();
        } else {
            String period = getStringParam(PERIOD); String price = getStringParam(PRICE); String label = getStringParam(LABEL);
            return label + "(" + price + ", " + period + ")";
        }
    }

    public String getMarkerLabel(ChartFrame cf, int i) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        StringBuilder sb = new StringBuilder();
        sb.append(getLabel() + "\n");
        double[] values = getValues(cf, i);
        if (values.length > 0) {
            Color[] colors = getColors();
            for (int j = 0; j < values.length; j++) sb.append("<font color=\"" + Integer.toHexString(colors[j].getRGB() & 0x00ffffff) + "\">&nbsp;&nbsp;&nbsp;EMA:&nbsp;" + df.format(values[j]) + "</font>\n");
        }
        return sb.toString();
    }

    public Range getRange(ChartFrame cf) {
        String price = getStringParam(PRICE);
        Dataset ema = visibleDataset(cf, EMA);
        if (ema != null) {
            Range chartRange = cf.getChartRenderer().getChartRange();
            Range range = Range.combine(chartRange, new Range(ema.getMinNotZero(price), ema.getMaxNotZero(price)));
            return range;
        }
        return null;
    }

    public void initialize() {
        PropertyItem[] data = new PropertyItem[] {
            new PropertyItem(PERIOD, ComponentGenerator.JTEXTFIELD, "20"),
            new PropertyItem(PRICE, ComponentGenerator.JCOMBOBOX, Dataset.LIST, Dataset.CLOSE),
            new PropertyItem(LABEL, ComponentGenerator.JTEXTFIELD, "EMA"),
            new PropertyItem(MARKER, ComponentGenerator.JCHECKBOX, Boolean.TRUE),
            new PropertyItem(COLOR, ComponentGenerator.JLABEL, new Color(0x204a87)),
            new PropertyItem(STYLE, ComponentGenerator.JSTROKECOMBOBOX, "0")
        };
        Properties p = new Properties(data);
        setProperties(p);
    }

    public void calculate() {
        Dataset initial = getDataset();
        if (initial != null && !initial.isEmpty()) {
            int period = getIntParam(PERIOD);
            Dataset ema = initial.getEMA(period);
            addDataset(EMA, ema);
        }
    }

    public void paint(Graphics2D g, ChartFrame cf) {
        Dataset ema = visibleDataset(cf, EMA);
        if (ema != null) DefaultPainter.line(g, cf, ema, getColorParam(COLOR), getStrokeParam(STYLE), getStringParam(PRICE)); // paint sma line
    }

    public Color[] getColors() { return new Color[] {getColorParam(COLOR)}; }
    public double[] getValues(ChartFrame cf) {
        Dataset ema = visibleDataset(cf, EMA);
        if (ema != null) {
            String price = getStringParam(PRICE);
            return new double[] {ema.getLastPriceValue(price)};
        }
        return new double[] {};
    }
    public double[] getValues(ChartFrame cf, int i) {
        Dataset ema = visibleDataset(cf, EMA);
        if (ema != null) {
            String price = getStringParam(PRICE);
            return new double[] {ema.getPriceValue(i, price)};
        }
        return new double[] {};
    }
    public boolean getMarkerVisibility() { return getBooleanParam(MARKER); }

    public void readXMLDocument(Element parent) { readFromXMLDocument(parent); }
    public void writeXMLDocument(Document document, Element parent) { writeToXMLDocument(document, parent, "EMA"); }

}
