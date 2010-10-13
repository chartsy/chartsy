package org.chartsy.onbalancevolume;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import org.chartsy.main.ChartFrame;
import org.chartsy.main.chart.Indicator;
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
public class OnBalanceVolume 
        extends Indicator
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public static final String OBV = "obv";

    private IndicatorProperties properties;

    public OnBalanceVolume()
    {
        super();
        properties = new IndicatorProperties();
    }

    public String getName(){ return "OBV"; }

    public String getLabel(){ return properties.getLabel(); }

    public String getPaintedLabel(ChartFrame cf)
    {
        DecimalFormat df = new DecimalFormat("###,###");
        double factor = getFactor(cf);
        return getLabel() + " x " + df.format((int)factor);
    }

    public Indicator newInstance() 
    { return new OnBalanceVolume(); }

    public LinkedHashMap getHTML(ChartFrame cf, int i)
    {
        LinkedHashMap ht = new LinkedHashMap();

        double[] values = getValues(cf, i);
        String[] labels = {"OBV:"};

        ht.put(getLabel(), " ");
        DecimalFormat df = new DecimalFormat("#,##0.00");
        if (values.length > 0) {
            Color[] colors = getColors();
            for (int j = 0; j < values.length; j++) {
                ht.put(getFontHTML(colors[j], labels[j]),
                        getFontHTML(colors[j], df.format(values[j])));
            }
        }

        return ht;
    }

    @Override
    public Range getRange(ChartFrame cf)
    {
        Dataset d = visibleDataset(cf, OBV);
        if (d != null)
        {
            double factor = getFactor(cf);
            double min = d.getCloseAt(0), max = min;
            for (int i = 0; i < d.getItemsCount(); i++)
            {
                if (min > d.getCloseAt(i))
                    min = d.getCloseAt(i);

                if (max < d.getCloseAt(i))
                    max = d.getCloseAt(i);
            }
            
            return Range.scale(new Range(min, max), 1/factor);
        }
        return new Range();
    }

    public void paint(Graphics2D g, ChartFrame cf, Rectangle bounds)
    {
        Dataset d = visibleDataset(cf, OBV);

        if (d != null)
        {
            if (maximized)
            {
                double factor = getFactor(cf);
                Range range = getRange(cf);

                DefaultPainter.line(g, cf, range, bounds, Dataset.DIV(d, factor), properties.getColor(), properties.getStroke()); // paint line
            }
        }
    }

    public void calculate()
    {
        Dataset initial = getDataset();
        if (initial != null && !initial.isEmpty())
        {
            Dataset d = getDataset(initial);
            addDataset(OBV, d);
        }
    }

    public boolean hasZeroLine()
    { return false; }

    public boolean getZeroLineVisibility()
    { return false; }

    public Color getZeroLineColor()
    { return null; }

    public Stroke getZeroLineStroke()
    { return null; }

    public boolean hasDelimiters() 
    { return false; }

    public boolean getDelimitersVisibility() 
    { return false; }

    public double[] getDelimitersValues() 
    { return new double[] {}; }

    public Color getDelimitersColor() 
    { return null; }

    public Stroke getDelimitersStroke() 
    { return null; }

    public Color[] getColors() 
    { return new Color[] {properties.getColor()}; }

    public double[] getValues(ChartFrame cf)
    {
        Dataset d = visibleDataset(cf, OBV);
        double factor = getFactor(cf);
        if (d != null)
            return new double[] {d.getLastClose()/factor};
        return new double[] {};
    }

    public double[] getValues(ChartFrame cf, int i)
    {
        Dataset d = visibleDataset(cf, OBV);
        double factor = getFactor(cf);
        if (d != null)
            return new double[] {d.getCloseAt(i)/factor};
        return new double[] {};
    }

    public boolean getMarkerVisibility()
    { return false; }

    @Override
    public boolean paintValues()
    { return false; }

    public AbstractNode getNode()
    { return new IndicatorNode(properties); }

    private Dataset getDataset(final Dataset initial)
    {
        int count = initial.getItemsCount();
        Dataset obv = Dataset.EMPTY(count);

        double currentOBV = 0;
        for (int i = 1; i < count; i++)
        {
            if (initial.getCloseAt(i) > initial.getCloseAt(i-1))
            {
                currentOBV += initial.getVolumeAt(i);
            }
            else if (initial.getCloseAt(i) < initial.getCloseAt(i-1))
            {
                currentOBV -= initial.getVolumeAt(i);
            }

            obv.setDataItem(i, new DataItem(initial.getTimeAt(i), currentOBV));
        }

        return obv;
    }

    private double getFactor(ChartFrame cf) {
        Dataset d = visibleDataset(cf, OBV);

        if (d != null)
        {
            double max = d.getCloseAt(0);
            double min = d.getCloseAt(0);

            for (int i = 0; i < d.getItemsCount(); i++)
            {
                if (max < d.getCloseAt(i))
                    max = d.getCloseAt(i);

                if (min > d.getCloseAt(i))
                    min = d.getCloseAt(i);
            }

            int scaleMax = Integer.toString((int)Math.round(max + (max - min) * 0.2d)).length() - 1;
            int scaleMin = Integer.toString((int)Math.round(min - (max - min) * 0.2d)).length() - 1;
            int scale = Math.min(scaleMin, scaleMax);
            if (scale > 1) scale--;
            
            return Math.pow(10, scale);
        }

        return 1;
    }

}
