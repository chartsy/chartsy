package org.chartsy.pricezoneoscillator;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import org.chartsy.main.ChartFrame;
import org.chartsy.main.chart.Indicator;
import org.chartsy.main.data.DataItem;
import org.chartsy.main.data.Dataset;
import org.chartsy.main.utils.DefaultPainter;
import org.chartsy.main.utils.Range;
import org.chartsy.main.utils.SerialVersion;
import org.chartsy.main.utils.StrokeGenerator;
import org.openide.nodes.AbstractNode;

/**
 *
 * @author Viorel
 */
public class PriceZoneOscillator extends Indicator
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public static final String PZO = "pzo";
    private IndicatorProperties properties;
    private Color green = new Color(0x73880A);
    private Color red = new Color(0xCC0000);

    public PriceZoneOscillator()
    {
        super();
        properties = new IndicatorProperties();
    }

    @Override
    public String getName() {
        return "Price Zero Oscillator";
    }

    @Override
    public String getLabel() {
        return properties.getLabel() + " (" + properties.getPeriod() + ")";
    }

    @Override
    public String getPaintedLabel(ChartFrame cf) {
        return properties.getLabel() + " (" + properties.getPeriod() + ")";
    }

    @Override
    public Indicator newInstance() {
        return new PriceZoneOscillator();
    }

    @Override
    public LinkedHashMap getHTML(ChartFrame cf, int i) {
        LinkedHashMap ht = new LinkedHashMap();

        DecimalFormat df = new DecimalFormat("#,##0.00");
        double[] values = getValues(cf, i);
        String[] labels = {"PZO:"};

        ht.put(getLabel(), " ");
        if (values.length > 0)
        {
            Color[] colors = getColors();
            for (int j = 0; j < values.length; j++)
                ht.put(getFontHTML(colors[j], labels[j]), getFontHTML(colors[j], df.format(values[j])));
        }

        return ht;
    }

    @Override
    public Range getRange(ChartFrame cf) {
        return new Range(-100, 100);
    }

    @Override
    public void paint(Graphics2D g, ChartFrame cf, Rectangle bounds) {
        Dataset d = visibleDataset(cf, PZO);
        if (d != null) {
            if (maximized) {
                Range range = getRange(cf);
                paintConstLine(g, cf, range, bounds, green, StrokeGenerator.getStroke(8), 60);
                paintConstLine(g, cf, range, bounds, green, StrokeGenerator.getStroke(0), 40);
                paintConstLine(g, cf, range, bounds, Color.gray, StrokeGenerator.getStroke(0), 15);
                paintConstLine(g, cf, range, bounds, Color.gray, StrokeGenerator.getStroke(0), -5);
                paintConstLine(g, cf, range, bounds, red, StrokeGenerator.getStroke(0), -40);
                paintConstLine(g, cf, range, bounds, red, StrokeGenerator.getStroke(8), -60);
                DefaultPainter.line(g, cf, range, bounds, d, properties.getColor(), properties.getStroke());
            }
        }
    }

    private void paintConstLine(Graphics2D g, ChartFrame cf, Range range, Rectangle bounds, Color color, Stroke stroke, double value)
    {
        Stroke old = g.getStroke();
        g.setPaint(color);
if (stroke != null) g.setStroke(stroke);
        double y = cf.getChartData().getY(value, bounds, range, false);
        g.draw(new Line2D.Double(bounds.getMinX(), y, bounds.getMaxX(), y));
        g.setStroke(old);
    }

    @Override
    public void calculate() {
        int period = properties.getPeriod();
        Dataset initial = getDataset();
        Dataset pzo = Dataset.EMPTY(initial.getItemsCount());
        Dataset r = Dataset.EMPTY(initial.getItemsCount());

        for (int i = 0; i < initial.getItemsCount(); i++) {
            double close;
            if (initial.getCloseAt(i) > initial.getCloseAt(i-1)) {
                close = initial.getCloseAt(i);
            } else {
                close = -1 * initial.getCloseAt(i);
            }
            r.setDataItem(i, new DataItem(initial.getTimeAt(i), close));
        }

        Dataset cp = Dataset.EMA(r, period);
        Dataset tc = Dataset.EMA(initial, period);

        for (int i = 0; i < initial.getItemsCount(); i++) {
            if (cp.getDataItem(i) != null && tc.getDataItem(i) != null) {
                pzo.setDataItem(i, new DataItem(initial.getTimeAt(i), (100 * (cp.getCloseAt(i) / tc.getCloseAt(i)))));
            }
        }

        r = cp = tc = null;

        addDataset(PZO, pzo);
    }

    @Override
    public boolean hasZeroLine() {
        return true;
    }

    @Override
    public boolean getZeroLineVisibility() {
        return true;
    }

    @Override
    public Color getZeroLineColor() {
        return Color.black;
    }

    @Override
    public Stroke getZeroLineStroke() {
        return StrokeGenerator.getStroke(0);
    }

    @Override
    public boolean hasDelimiters() {
        return false;
    }

    @Override
    public boolean getDelimitersVisibility() {
        return false;
    }

    @Override
    public double[] getDelimitersValues() {
        return null;
    }

    @Override
    public Color getDelimitersColor() {
        return null;
    }

    @Override
    public Stroke getDelimitersStroke() {
        return null;
    }

    @Override
    public Color[] getColors() {
        return new Color[] { properties.getColor() };
    }

    @Override
    public double[] getValues(ChartFrame cf) {
        Dataset d = visibleDataset(cf, PZO);
        if (d != null) {
            return new double[] {d.getLastClose()};
        }
        return new double[] {};
    }

    @Override
    public double[] getValues(ChartFrame cf, int i) {
        Dataset d = visibleDataset(cf, PZO);
        if (d != null) {
            return new double[] {d.getCloseAt(i)};
        }
        return new double[] {};
    }

    @Override
    public boolean getMarkerVisibility() {
        return properties.getMarker();
    }

    @Override
    public AbstractNode getNode() {
        return new IndicatorNode(properties);
    }

}
