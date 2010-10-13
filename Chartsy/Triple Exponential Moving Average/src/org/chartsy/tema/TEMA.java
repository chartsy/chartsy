package org.chartsy.tema;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import org.chartsy.main.ChartFrame;
import org.chartsy.main.chart.Overlay;
import org.chartsy.main.data.Dataset;
import org.chartsy.main.utils.DefaultPainter;
import org.chartsy.main.utils.Range;
import org.chartsy.main.utils.SerialVersion;
import org.openide.nodes.AbstractNode;

/**
 *
 * @author viorel.gheba
 */
public class TEMA 
        extends Overlay
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public static final String TEMA = "tema";
    private OverlayProperties properties;

    public TEMA()
    {
        super();
        properties = new OverlayProperties();
    }

    public String getName(){ return "TEMA"; }

    public String getLabel()
    { return properties.getLabel() + " (" + properties.getPrice() + ", " + properties.getPeriod() + ")"; }

    public Overlay newInstance(){ return new TEMA(); }

    public LinkedHashMap getHTML(ChartFrame cf, int i)
    {
        LinkedHashMap ht = new LinkedHashMap();

        DecimalFormat df = new DecimalFormat("#,##0.00");
        double[] values = getValues(cf, i);

        ht.put(getLabel(), " ");
        if (values.length > 0) {
            Color[] colors = getColors();
            for (int j = 0; j < values.length; j++) {
                ht.put(getFontHTML(colors[j], "TEMA:"),
                        getFontHTML(colors[j], df.format(values[j])));
            }
        }

        return ht;
    }

    public void paint(Graphics2D g, ChartFrame cf, Rectangle bounds)
    {
        Dataset tema = visibleDataset(cf, TEMA);
        if (tema != null)
        {
            Range range = cf.getSplitPanel().getChartPanel().getRange();
            
            DefaultPainter.line(g, cf, range, bounds, tema, properties.getColor(), properties.getStroke(), Dataset.getPrice(properties.getPrice()));
        }
    }

    public void calculate()
    {
        int period = properties.getPeriod();
        Dataset initial = getDataset();

        if (initial != null && !initial.isEmpty())
        {
            Dataset tema = Dataset.TEMA(initial, period);
            addDataset(TEMA, tema);
        }
    }

    public Color[] getColors(){ return new Color[] {properties.getColor()}; }

    public double[] getValues(ChartFrame cf) {
        Dataset tema = visibleDataset(cf, TEMA);
        if (tema != null)
        {
            String price = properties.getPrice();
            return new double[] {tema.getLastPrice(price)};
        }
        return new double[] {};
    }

    public double[] getValues(ChartFrame cf, int i)
    {
        Dataset tema = visibleDataset(cf, TEMA);
        if (tema != null)
        {
            String price = properties.getPrice();
            return new double[] {tema.getPriceAt(i, price)};
        }
        return new double[] {};
    }

    public boolean getMarkerVisibility(){ return properties.getMarker(); }

    public AbstractNode getNode(){ return new OverlayNode(properties); }

    public String getPrice(){ return properties.getPrice(); }

}
