package org.chartsy.bollingerbands;

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
public class BollingerBands
        extends Overlay
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;
    
    public static final String FULL_NAME = "Bollinger Bands";
    public static final String UPPER = "upper";
    public static final String MIDDLE = "middle";
    public static final String LOWER = "lower";

    private OverlayProperties properties;

    public BollingerBands()
    {
        super();
        properties = new OverlayProperties();
    }

    public String getName(){ return FULL_NAME; }

    public String getLabel() 
    { return properties.getLabel() + " (" + properties.getPrice() + ", " + properties.getStd() + ", " + properties.getPeriod() + ")"; }

    public Overlay newInstance(){ return new BollingerBands(); }

    public LinkedHashMap getHTML(ChartFrame cf, int i)
    {
        LinkedHashMap ht = new LinkedHashMap();

        DecimalFormat df = new DecimalFormat("#,##0.00");
        double[] values = getValues(cf, i);
        String[] labels = {"Upper Band:", "Middle Band:", "Lower Band:"};

        ht.put(getLabel(), " ");
        if (values.length > 0) {
            Color[] colors = getColors();
            for (int j = 0; j < values.length; j++) {
                ht.put(getFontHTML(colors[j], labels[j]),
                        getFontHTML(colors[j], df.format(values[j])));
            }
        }

        return ht;
    }

    public void paint(Graphics2D g, ChartFrame cf, Rectangle bounds)
    {
        Dataset middle = visibleDataset(cf, MIDDLE);
        Dataset upper = visibleDataset(cf, UPPER);
        Dataset lower = visibleDataset(cf, LOWER);

        if (middle != null && upper != null && lower != null)
        {
            String price = properties.getPrice();
            Range range = cf.getSplitPanel().getChartPanel().getRange();
            
            if (properties.getInsideVisibility())
                DefaultPainter.insideFill(g, cf, range, bounds, upper, lower, properties.getInsideTransparentColor(), Dataset.getPrice(price));

            DefaultPainter.line(g, cf, range, bounds, middle, properties.getMiddleColor(), properties.getMiddleStroke(), Dataset.getPrice(price)); // paint middle line
            DefaultPainter.line(g, cf, range, bounds, upper, properties.getUpperColor(), properties.getUpperStroke(), Dataset.getPrice(price)); // paint upper line
            DefaultPainter.line(g, cf, range, bounds, lower, properties.getLowerColor(), properties.getLowerStroke(), Dataset.getPrice(price)); // paint lower line
        }
    }

    public void calculate()
    {
        int period = properties.getPeriod();
        int stddev = properties.getStd();
        Dataset initial = getDataset();
        if (initial != null && !initial.isEmpty())
        {
            Dataset middle = Dataset.SMA(initial, period);
            addDataset(MIDDLE, middle);

            Dataset upper = getLowerUpperDataset(initial, middle, period, stddev, UPPER);
            addDataset(UPPER, upper);

            Dataset lower = getLowerUpperDataset(initial, middle, period, stddev, LOWER);
            addDataset(LOWER, lower);
        }
    }

    private Dataset getLowerUpperDataset(final Dataset initial, final Dataset middle, final int period, final int stddev, final String type)
    {
        int count = initial.getItemsCount();
        Dataset d = Dataset.EMPTY(count);

        for (int i = period; i < count; i++)
        {
            double opendev = 0;
            double highdev = 0;
            double lowdev = 0;
            double closedev = 0;

            for (int j = 0; j < period; j++)
            {
                opendev += Math.pow(initial.getOpenAt(i-j) - middle.getOpenAt(i), 2);
                highdev += Math.pow(initial.getHighAt(i-j) - middle.getHighAt(i), 2);
                lowdev += Math.pow(initial.getLowAt(i-j) - middle.getLowAt(i), 2);
                closedev += Math.pow(initial.getCloseAt(i-j) - middle.getCloseAt(i), 2);
            }

            opendev = stddev * Math.sqrt(opendev / period);
            highdev = stddev * Math.sqrt(highdev / period);
            lowdev = stddev * Math.sqrt(lowdev / period);
            closedev = stddev * Math.sqrt(closedev / period);

            if (type.equals(LOWER))
                d.setDataItem(i, new DataItem(middle.getTimeAt(i), middle.getOpenAt(i) - opendev, middle.getHighAt(i) - highdev, middle.getLowAt(i) - lowdev, middle.getCloseAt(i) - closedev, 0));
            else
                d.setDataItem(i, new DataItem(middle.getTimeAt(i), middle.getOpenAt(i) + opendev, middle.getHighAt(i) + highdev, middle.getLowAt(i) + lowdev, middle.getCloseAt(i) + closedev, 0));
        }

        return d;
    }

    public Color[] getColors() 
    { return new Color[] {properties.getUpperColor(), properties.getMiddleColor(), properties.getLowerColor()}; }

    public double[] getValues(ChartFrame cf)
    {
        Dataset middle = visibleDataset(cf, MIDDLE);
        Dataset upper = visibleDataset(cf, UPPER);
        Dataset lower = visibleDataset(cf, LOWER);

        int i = middle.getLastIndex();
        double[] values = new double[3];
        values[0] = upper.getDataItem(i) != null ? upper.getCloseAt(i) : 0;
        values[1] = middle.getDataItem(i) != null ? middle.getCloseAt(i) : 0;
        values[2] = lower.getDataItem(i) != null ? lower.getCloseAt(i) : 0;
        
        return values;
    }

    public double[] getValues(ChartFrame cf, int i)
    {
        Dataset middle = visibleDataset(cf, MIDDLE);
        Dataset upper = visibleDataset(cf, UPPER);
        Dataset lower = visibleDataset(cf, LOWER);
        
        double[] values = new double[3];
        values[0] = upper.getDataItem(i) != null ? upper.getCloseAt(i) : 0;
        values[1] = middle.getDataItem(i) != null ? middle.getCloseAt(i) : 0;
        values[2] = lower.getDataItem(i) != null ? lower.getCloseAt(i) : 0;

        return values;
    }

    public boolean getMarkerVisibility() 
    { return properties.getMarker(); }

    public AbstractNode getNode() 
    { return new OverlayNode(properties); }

    public String getPrice()
    { return properties.getPrice(); }

}
