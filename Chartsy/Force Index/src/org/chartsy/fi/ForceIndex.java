package org.chartsy.fi;

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
public class ForceIndex
        extends Indicator
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;
    
    public static final String FI1 = "fi1";
    public static final String FI2 = "fi2";
    private IndicatorProperties properties;

    public ForceIndex()
    {
        super();
        properties = new IndicatorProperties();
    }

    public String getName()
    { return "FI"; }

    public String getLabel() 
    { return properties.getLabel() + "(" + properties.getPeriod1() + ", " + properties.getPeriod2() + ")"; }

    public String getPaintedLabel(ChartFrame cf)
    {
        DecimalFormat df = new DecimalFormat("###,###");
        double factor = getFactor(cf);
        return getLabel() + " x " + df.format((int)factor);
    }

    public Indicator newInstance() 
    { return new ForceIndex(); }

    public LinkedHashMap getHTML(ChartFrame cf, int i)
    {
        LinkedHashMap ht = new LinkedHashMap();

        DecimalFormat df = new DecimalFormat("###,###");
        double factor = getFactor(cf);
        double[] values = getValues(cf, i);
        String[] labels = {"FI(" + properties.getPeriod1() + "):", "FI(" + properties.getPeriod2() + "):"};

        ht.put(getLabel() + " x " + df.format((int)factor), " ");
        df = new DecimalFormat("#,##0.00");
        if (values.length > 0)
        {
            Color[] colors = getColors();
            for (int j = 0; j < values.length; j++)
            {
                ht.put(getFontHTML(colors[j], labels[j]),
                        getFontHTML(colors[j], df.format(values[j])));
            }
        }

        return ht;
    }

    public @Override Range getRange(ChartFrame cf)
    {
        Range range = super.getRange(cf);
        double factor = getFactor(cf);
        range = new Range(range.getLowerBound()/factor, range.getUpperBound()/factor);
        return range;
    }

    public void paint(Graphics2D g, ChartFrame cf, Rectangle bounds)
    {
        Dataset dataset1 = visibleDataset(cf, FI1);
        Dataset dataset2 = visibleDataset(cf, FI2);

        if (dataset1 != null && dataset2 != null) 
        {            
            if (maximized)
            {
                double factor = getFactor(cf);
                Range range = getRange(cf);

                DefaultPainter.line(g, cf, range, bounds, Dataset.DIV(dataset1, factor), properties.getColor1(), properties.getStroke1()); // paint line
                DefaultPainter.line(g, cf, range, bounds, Dataset.DIV(dataset2, factor), properties.getColor2(), properties.getStroke2()); // paint line
            }
        }
    }

    public void calculate()
    {
        Dataset initial = getDataset();
        if (initial != null && !initial.isEmpty())
        {
            int period1 = properties.getPeriod1();
            int period2 = properties.getPeriod2();

            Dataset dataset1 = getDataset(initial, period1);
            addDataset(FI1, dataset1);

            Dataset dataset2 = getDataset(initial, period2);
            addDataset(FI2, dataset2);
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
    { return new Color[] {properties.getColor1(), properties.getColor2()}; }

    public double[] getValues(ChartFrame cf)
    {
        Dataset dataset1 = visibleDataset(cf, FI1);
        Dataset dataset2 = visibleDataset(cf, FI2);
        double factor = getFactor(cf);

        if (dataset1 != null && dataset2 != null)
            return new double[] {dataset1.getLastClose()/factor, dataset2.getLastClose()/factor};
        return new double[] {};
    }

    public double[] getValues(ChartFrame cf, int i)
    {
        Dataset dataset1 = visibleDataset(cf, FI1);
        Dataset dataset2 = visibleDataset(cf, FI2);
        double factor = getFactor(cf);
        
        if (dataset1 != null && dataset2 != null)
            return new double[] {dataset1.getCloseAt(i)/factor, dataset2.getCloseAt(i)/factor};
        return new double[] {};
    }

    public boolean getMarkerVisibility()
    { return properties.getMarker(); }

    public AbstractNode getNode()
    { return new IndicatorNode(properties); }

    private Dataset getDataset(final Dataset initial, final int period) 
    {
        int count = initial.getItemsCount();
        Dataset d = Dataset.EMPTY(count);

        for (int i = 1; i < count; i++)
        {
            double fi = initial.getVolumeAt(i) * (initial.getCloseAt(i) - initial.getCloseAt(i-1));
            d.setDataItem(i, new DataItem(initial.getTimeAt(i), fi));
        }

        return Dataset.SMA(d, period);
    }

    private double getFactor(ChartFrame cf)
    {
        Dataset dataset1 = visibleDataset(cf, FI1);
        Dataset dataset2 = visibleDataset(cf, FI2);

        if (dataset1 != null && dataset2 != null)
        {
            Range range =  new Range(dataset1.getMin(Dataset.CLOSE), dataset1.getMax(Dataset.CLOSE));
            range = Range.combine(range, new Range(dataset2.getMin(Dataset.CLOSE), dataset2.getMax(Dataset.CLOSE)));

            int max = Integer.toString((int)Math.round(range.getUpperBound())).length() - 1;
            int min = Integer.toString((int)Math.round(range.getLowerBound())).length() - 1;
            
            int scale = Math.min(max, min);
            if (scale > 1) scale--;
            
            return Math.pow(10, scale);
        }

        return 1;
    }

}
