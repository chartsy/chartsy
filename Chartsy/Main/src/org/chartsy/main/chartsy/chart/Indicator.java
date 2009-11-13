package org.chartsy.main.chartsy.chart;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import org.chartsy.main.chartsy.ChartFrame;
import org.chartsy.main.dataset.Dataset;
import org.chartsy.main.utils.Properties;
import org.chartsy.main.utils.Range;
import org.chartsy.main.utils.StrokeGenerator;
import org.chartsy.main.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author viorel.gheba
 */
public abstract class Indicator extends Object implements XMLUtils.ToXML {

    private String name;
    private String description;
    private String dialogLabel;
    private Properties properties;
    private Rectangle2D.Double bounds;
    private Rectangle2D.Double axisBounds;
    private Dataset dataset;
    private Hashtable<Object, Object> datasets;
    private int areaIndex;

    public Indicator(String n, String desc, String d) {
        name = n;
        description = desc;
        dialogLabel = d;
        datasets = new Hashtable<Object, Object>();
        initialize();
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public abstract String getLabel();
    public String getFontHTML(Color color, String text) {
        String html = "<font color=\"" + Integer.toHexString(color.getRGB() & 0x00ffffff) + "\">" + text + "</font>";
        return html;
    }
    public abstract LinkedHashMap getHTML(ChartFrame cf, int i);
    public String getDialogLabel() { return dialogLabel; }
    public void setDialogLabel(String d) { dialogLabel = d; }

    public int getAreaIndex() { return areaIndex; }
    public void setAreaIndex(int i) { areaIndex = i; }

    public Properties getProperties() { return properties; }
    public void setProperties(Properties p) { properties = p; }
    public String getStringParam(String s) { return (String) properties.getValue(s); }
    public int getIntParam(String s) { return Integer.parseInt((String) properties.getValue(s)); }
    public boolean getBooleanParam(String s) { return (Boolean) properties.getValue(s); }
    public Color getColorParam(String s) { return (Color) properties.getValue(s); }
    public Stroke getStrokeParam(String s) { return StrokeGenerator.getStroke(Integer.parseInt((String) properties.getValue(s))); }

    public Rectangle2D.Double getBounds() { return bounds; }
    public void setBounds(Rectangle2D.Double b) { bounds = b; }

    public Rectangle2D.Double getAxisBounds() { return axisBounds; }
    public void setAxisBounds(Rectangle2D.Double b) { axisBounds = b; }

    public abstract Range getRange(ChartFrame cf);

    public Dataset getDataset() { return dataset; }
    public void setDataset(Dataset d) { dataset = d; }

    public void addDataset(Object key, Object value) { datasets.put(key, value); }
    public Dataset dataset(Object key) {
        if (datasets.containsKey(key)) return (Dataset) datasets.get(key);
        return null;
    }
    public Dataset visibleDataset(ChartFrame cf, Object key) {
        if (datasets.containsKey(key)) {
            Dataset d = dataset(key);
            return d != null ? d.getDrawableDataset(cf.getChartRenderer().getItems(), cf.getChartRenderer().getEnd()) : null;
        }
        return null;
    }

    public void paintIndicator(Graphics2D g, ChartFrame cf) {
        Rectangle2D.Double b = cf.getChartRenderer().getIndicatorBounds(getAreaIndex());
        Rectangle2D.Double aB = cf.getChartRenderer().getIndicatorAxisBounds(getAreaIndex());
        setBounds(b);
        setAxisBounds(aB);
        paint(g, cf);
    }
    public abstract void paint(Graphics2D g, ChartFrame cf);
    public abstract void initialize();
    public abstract void calculate();
    public abstract boolean hasZeroLine();
    public abstract boolean getZeroLineVisibility();
    public abstract Color getZeroLineColor();
    public abstract Stroke getZeroLineStroke();
    public abstract Color[] getColors();
    public abstract double[] getValues(ChartFrame cf);
    public abstract double[] getValues(ChartFrame cf, int i);
    public abstract boolean getMarkerVisibility();

    protected void readFromXMLDocument(Element parent) {
        setAreaIndex(XMLUtils.getIntegerParam(parent, "areaIndex"));
        setProperties(XMLUtils.getPropertiesParam(parent, "properties"));
    }

    protected void writeToXMLDocument(Document document, Element parent, String name) {
        Element element;
        element = document.createElement("name");
        parent.appendChild(XMLUtils.setStringParam(element, getName()));
        element = document.createElement("areaIndex");
        parent.appendChild(XMLUtils.setIntegerParam(element, getAreaIndex()));
        element = document.createElement("properties");
        Element e;
        for (int i = 0; i < getProperties().getItems(); i++) {
            e = document.createElement("propertyItem");
            element.appendChild(XMLUtils.setPropertiesParam(e, getProperties().getPropertyItem(i)));
        }
        parent.appendChild(element);
    }

}
