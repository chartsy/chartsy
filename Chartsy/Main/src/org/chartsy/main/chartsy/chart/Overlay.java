package org.chartsy.main.chartsy.chart;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import org.chartsy.main.chartsy.ChartFrame;
import org.chartsy.main.dataset.Dataset;
import org.chartsy.main.utils.Range;
import org.openide.nodes.AbstractNode;

/**
 *
 * @author viorel.gheba
 */
public abstract class Overlay extends Object implements Serializable {

    private static final long serialVersionUID = 101L;

    private String name;
    private String description;
    private String dialogLabel;
    private transient Rectangle2D.Double bounds;
    private transient Rectangle2D.Double axisBounds;
    private Range range;
    private Dataset dataset;
    private Hashtable<Object, Object> datasets;

    public Overlay(String n, String desc, String d) {
        name = n;
        description = desc;
        dialogLabel = d;
        datasets = new Hashtable<Object, Object>();
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

    public Rectangle2D.Double getBounds() { return bounds; }
    public void setBounds(Rectangle2D.Double b) { bounds = b; }

    public Rectangle2D.Double getAxisBounds() { return axisBounds; }
    public void setAxisBounds(Rectangle2D.Double b) { axisBounds = b; }

    public abstract Range getRange(ChartFrame cf);
    public void setRange(Range r) { range = r; }

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

    public void paintOverlay(Graphics2D g, ChartFrame cf) {
        Rectangle2D.Double b = cf.getChartRenderer().getChartBounds();
        Rectangle2D.Double aB = cf.getChartRenderer().getPriceAxisBounds();
        setBounds(b);
        setAxisBounds(aB);
        paint(g, cf);
    }
    public abstract void calculate();
    public abstract void paint(Graphics2D g, ChartFrame cf);
    public abstract Color[] getColors();
    public abstract double[] getValues(ChartFrame cf);
    public abstract double[] getValues(ChartFrame cf, int i);
    public abstract boolean getMarkerVisibility();
    public abstract AbstractNode getNode();

}
