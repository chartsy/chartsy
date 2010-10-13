package org.chartsy.mfi;

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
import org.chartsy.main.utils.StrokeGenerator;
import org.openide.nodes.AbstractNode;

/**
 *
 * @author viorel.gheba
 */
public class MoneyFlowIndex 
        extends Indicator
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public static final String MFI = "mfi";
    private IndicatorProperties properties;

    public MoneyFlowIndex()
    {
        super();
        properties = new IndicatorProperties();
    }

    public String getName(){ return "MFI"; }

    public String getLabel() 
    { return properties.getLabel() + " (" + properties.getPeriod() + ")"; }

    public String getPaintedLabel(ChartFrame cf){ return getLabel(); }

    public Indicator newInstance(){ return new MoneyFlowIndex(); }

    public LinkedHashMap getHTML(ChartFrame cf, int i)
    {
        LinkedHashMap ht = new LinkedHashMap();

        DecimalFormat df = new DecimalFormat("#,##0.00");
        double[] values = getValues(cf, i);
        String[] labels = {"MFI:"};

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

    @Override
    public Range getRange(ChartFrame cf){ return new Range(0, 100); }

    public void paint(Graphics2D g, ChartFrame cf, Rectangle bounds)
    {
        Dataset d = visibleDataset(cf, MFI);

        if (d != null)
        {
            if (maximized)
            {
                Range range = getRange(cf);

                DefaultPainter.line(g, cf, range, bounds, d, properties.getColor(), properties.getStroke()); // paint line
                if (properties.getInsideVisibility())
                {
                    paintFill(g, cf, d, bounds, range, properties.getInsideTransparentColor(), 20d, 0d, false);
                    paintFill(g, cf, d, bounds, range, properties.getInsideTransparentColor(), 80d, 100d, true);
                }
            }
        }
    }

    public void calculate()
    {
        Dataset initial = getDataset();
        if (initial != null && !initial.isEmpty())
        {
            int period = properties.getPeriod();

            Dataset d = getDataset(initial, period);
            addDataset(MFI, d);
        }
    }

    public boolean hasZeroLine(){ return false; }

    public boolean getZeroLineVisibility(){ return false; }

    public Color getZeroLineColor(){ return null; }

    public Stroke getZeroLineStroke(){ return null; }

    public boolean hasDelimiters(){ return true; }

    public boolean getDelimitersVisibility(){ return true; }

    public double[] getDelimitersValues(){ return new double[] {20d, 50d, 80d}; }

    public Color getDelimitersColor(){ return properties.getDelimiterColor(); }

    public Stroke getDelimitersStroke(){ return properties.getDelimiterLineStroke(); }

    public Color[] getColors(){ return new Color[] {properties.getColor()}; }

    public boolean getMarkerVisibility(){ return properties.getMarker(); }

    public AbstractNode getNode(){ return new IndicatorNode(properties); }

    public double[] getValues(ChartFrame cf)
    {
        Dataset d = visibleDataset(cf, MFI);
        if (d != null)
            return new double[] {d.getLastClose()};
        
        return new double[] {};
    }

    public double[] getValues(ChartFrame cf, int i)
    {
        Dataset d = visibleDataset(cf, MFI);
        if (d != null)
            return new double[] {d.getCloseAt(i)};

        return new double[] {};
    }

    @Override
    public Double[] getPriceValues(ChartFrame cf)
    { return new Double[] {new Double(20), new Double(50), new Double(80)}; }

    private Dataset getDataset(final Dataset initial, final int period)
    {
        int count = initial.getItemsCount();
        Dataset posMF = Dataset.EMPTY(count);
        Dataset negMF = Dataset.EMPTY(count);

        for (int i = 1; i < count; i++)
        {
            double tp1 = (initial.getCloseAt(i) + initial.getHighAt(i) + initial.getLowAt(i)) / 3;
            double tp2 = (initial.getCloseAt(i-1) + initial.getHighAt(i-1) + initial.getLowAt(i-1)) / 3;
            double rmf = tp1 * initial.getVolumeAt(i);

            if (tp1 > tp2)
                posMF.setDataItem(i, new DataItem(initial.getTimeAt(i), rmf));
            else
                negMF.setDataItem(i, new DataItem(initial.getTimeAt(i), rmf));
        }

        Dataset d = Dataset.EMPTY(count);

        for (int i = period; i < count; i++)
        {
            double posSum = 0;
            double negSum = 0;

            for (int j = 0; j < period; j++)
            {
                posSum += posMF.getDataItem(i-j) != null ? posMF.getCloseAt(i-j) : 0;
                negSum += negMF.getDataItem(i-j) != null ? negMF.getCloseAt(i-j) : 0;
            }

            double mr = posSum / negSum;
            double mfi = 100 - (100 / (1 + mr));
            d.setDataItem(i, new DataItem(initial.getTimeAt(i), mfi));
        }

        return d;
    }

}
