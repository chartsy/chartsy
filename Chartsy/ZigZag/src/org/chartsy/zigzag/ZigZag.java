package org.chartsy.zigzag;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import org.chartsy.main.ChartFrame;
import org.chartsy.main.chart.Overlay;
import org.chartsy.main.data.DataItem;
import org.chartsy.main.data.Dataset;
import org.chartsy.main.utils.DefaultPainter;
import org.chartsy.main.utils.Range;
import org.chartsy.main.utils.SerialVersion;
import org.openide.nodes.AbstractNode;

/**
 *
 * @author viorel.gheba
 */
public class ZigZag 
        extends Overlay
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public static final String ZZ = "zz";
    private OverlayProperties properties;

    public ZigZag()
    {
        super();
        properties = new OverlayProperties();
    }

    public String getName()
    { return "ZigZag"; }

    public String getLabel() 
    { return properties.getLabel(); }

    public Overlay newInstance() 
    { return new ZigZag(); }

    public LinkedHashMap getHTML(ChartFrame cf, int i)
    {
        LinkedHashMap ht = new LinkedHashMap();

        DecimalFormat df = new DecimalFormat("#,##0.00");
        double[] values = getValues(cf, i);

        ht.put(getLabel(), " ");
        if (values.length > 0) {
            Color[] colors = getColors();
            for (int j = 0; j < values.length; j++) {
                ht.put(getFontHTML(colors[j], "ZigZag:"),
                        getFontHTML(colors[j], df.format(values[j])));
            }
        }

        return ht;
    }

    public void paint(Graphics2D g, ChartFrame cf, Rectangle bounds)
    {
        Dataset d = visibleDataset(cf, ZZ);
        if (d != null)
        {
            Range range = cf.getSplitPanel().getChartPanel().getRange();
            DefaultPainter.line(g, cf, range, bounds, d, properties.getColor(), properties.getStroke());
        }
    }

    public void calculate()
    {
        Dataset initial = getDataset();
        if (initial != null && !initial.isEmpty())
        {
            Dataset d = getDataset(initial);
            addDataset(ZZ, d);
        }
    }

    public Color[] getColors()
    { return new Color[] {properties.getColor()}; }

    public double[] getValues(ChartFrame cf) {
        Dataset d = visibleDataset(cf, ZZ);
        if (d != null)
        {
            return new double[] {d.getLastClose()};
        }
        return new double[] {};
    }

    public double[] getValues(ChartFrame cf, int i)
    {
        Dataset d = visibleDataset(cf, ZZ);
        if (d != null) {
            return new double[] {d.getCloseAt(i)};
        }
        return new double[] {};
    }

    public boolean getMarkerVisibility()
    { return properties.getMarker(); }

    public AbstractNode getNode()
    { return new OverlayNode(properties); }

    private Dataset getDataset(final Dataset initial)
    {
        int count = initial.getItemsCount();
        Dataset result = Dataset.EMPTY(count);

        boolean switchVar = false;
        for (int i = 2; i <= count; i++)
        {
            if (i < count)
            {
                if (switchVar == false)
                {
                    if (initial.getHighAt(i-1) > initial.getHighAt(i-2) && initial.getHighAt(i-1) > initial.getHighAt(i))
                    {
                        result.setDataItem(i-1, new DataItem(initial.getTimeAt(i-1), initial.getHighAt(i-1)));
                        switchVar = true;
                        continue;
                    }
                }

                if (switchVar == true)
                {
                    if (initial.getLowAt(i-1) < initial.getLowAt(i-2) && initial.getLowAt(i-1) < initial.getLowAt(i))
                    {
                        result.setDataItem(i-1, new DataItem(initial.getTimeAt(i-1), initial.getLowAt(i-1)));
                        switchVar = false;
                        continue;
                    }
                }
            }
        }

        return result;
    }

    public String getPrice() 
    { return Dataset.CLOSE; }

}
