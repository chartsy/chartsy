package org.chartsy.ema;

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
public class EMA 
        extends Overlay
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public static final String EMA = "ema";

    private OverlayProperties properties;

    public EMA()
    {
        super();
        properties = new OverlayProperties();
    }

    public String getName()
    { return "EMA"; }

    public String getLabel() 
    { return properties.getLabel() + " (" + properties.getPrice() + ", " + properties.getPeriod() + ")"; }

    public Overlay newInstance() 
    { return new EMA(); }

    public LinkedHashMap getHTML(ChartFrame cf, int i)
    {
        LinkedHashMap ht = new LinkedHashMap();

        DecimalFormat df = new DecimalFormat("#,##0.00");
        double[] values = getValues(cf, i);

        ht.put(getLabel(), " ");
        if (values.length > 0) {
            Color[] colors = getColors();
            for (int j = 0; j < values.length; j++) {
                ht.put(getFontHTML(colors[j], "EMA:"),
                        getFontHTML(colors[j], df.format(values[j])));
            }
        }

        return ht;
    }

    public void paint(Graphics2D g, ChartFrame cf, Rectangle bounds)
    {
        Dataset ema = visibleDataset(cf, EMA);
        if (ema != null) {
            Range range = cf.getSplitPanel().getChartPanel().getRange();

            DefaultPainter.line(g, cf, range, bounds, ema, properties.getColor(), properties.getStroke(), Dataset.getPrice(properties.getPrice()));
        }
    }

    public void calculate()
    {
        Dataset initial = getDataset();
        if (initial != null && !initial.isEmpty())
        {
            int period = properties.getPeriod();
            Dataset ema = Dataset.EMA(initial, period);
            addDataset(EMA, ema);
        }
    }

    public Color[] getColors()
    { return new Color[] {properties.getColor()}; }

    public double[] getValues(ChartFrame cf)
    {
        Dataset ema = visibleDataset(cf, EMA);
        
        int i = ema.getLastIndex();
        if (ema.getDataItem(i) != null)
            return new double[] {ema.getPriceAt(i, properties.getPrice())};
        return new double[] {0};
    }

    public double[] getValues(ChartFrame cf, int i)
    {
        Dataset ema = visibleDataset(cf, EMA);

        if (ema.getDataItem(i) != null)
            return new double[] {ema.getPriceAt(i, properties.getPrice())};
        return new double[] {0};
    }

    public boolean getMarkerVisibility()
    { return properties.getMarker(); }

    public AbstractNode getNode() 
    { return new OverlayNode(properties); }

    public String getPrice()
    { return properties.getPrice(); }

}
