package org.chartsy.rsi;

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
public class RSI 
        extends Indicator
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;
    
    public static final String RSI = "rsi";
    private IndicatorProperties properties;

    public RSI()
    {
        super();
        properties = new IndicatorProperties();
    }

    public String getName()
    { return "RSI"; }

    public String getLabel() 
    { return properties.getLabel() + " (" + properties.getPeriod() + ")"; }

    public String getPaintedLabel(ChartFrame cf)
    { return getLabel(); }

    public Indicator newInstance() 
    { return new RSI(); }

    public LinkedHashMap getHTML(ChartFrame cf, int i)
    {
        LinkedHashMap ht = new LinkedHashMap();

        DecimalFormat df = new DecimalFormat("#,##0.00");
        double[] values = getValues(cf, i);
        String[] labels = {"RSI:"};

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
    public Range getRange(ChartFrame cf) 
    { return new Range(0, 100); }

    public void paint(Graphics2D g, ChartFrame cf, Rectangle bounds)
    {
        Dataset d = visibleDataset(cf, RSI);

        if (d != null)
        {
            if (maximized)
            {
                Range range = getRange(cf);

                DefaultPainter.line(g, cf, range, bounds, d, properties.getColor(), properties.getStroke()); // paint line
                if (properties.getInsideVisibility())
                {
                    paintFill(g, cf, d, bounds, range, properties.getInsideTransparentColor(), 30d, 0d, false); // paint fill
                    paintFill(g, cf, d, bounds, range, properties.getInsideTransparentColor(), 70d, 100d, true); // paint fill
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
            addDataset(RSI, d);
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
    { return true; }

    public boolean getDelimitersVisibility() 
    { return true; }

    public double[] getDelimitersValues() 
    { return new double[] {30d, 50d, 70d}; }

    public Color getDelimitersColor() 
    { return new Color(0xbbbbbb); }

    public Stroke getDelimitersStroke() 
    { return StrokeGenerator.getStroke(1); }

    public Color[] getColors()
    { return new Color[] {properties.getColor()}; }

    public double[] getValues(ChartFrame cf)
    {
        Dataset d = visibleDataset(cf, RSI);
        if (d != null)
            return new double[] {d.getLastClose()};
        return new double[] {};
    }

    public double[] getValues(ChartFrame cf, int i)
    {
        Dataset d = visibleDataset(cf, RSI);
        if (d != null)
            return new double[] {d.getCloseAt(i)};
        return new double[] {};
    }

    public boolean getMarkerVisibility()
    { return properties.getMarker(); }

    public AbstractNode getNode()
    { return new IndicatorNode(properties); }

    @Override
    public Double[] getPriceValues(ChartFrame cf)
    { return new Double[] {new Double(10), new Double(30), new Double(50), new Double(70), new Double(90)}; }


    private Dataset getDataset(final Dataset initial, final int period)
    {
        int count = initial.getItemsCount();
        Dataset result = Dataset.EMPTY(count);

        for (int i = period; i < count; i++)
        {
            double adva = 0;
            double decl = 0;
            double currentRSI;

            for (int j = 0; j < period; j++)
            {
                if (initial.getCloseAt(i-j) > initial.getCloseAt(i-j-1))
                {
                    adva += initial.getCloseAt(i-j) - initial.getCloseAt(i-j-1);
                } 
                else if (initial.getCloseAt(i-j) < initial.getCloseAt(i-j-1))
                {
                    decl += initial.getCloseAt(i-j-1) - initial.getCloseAt(i-j);
                }
            }

            if (decl == 0)
            {
                currentRSI = 100000D;
            }
            else 
            {
                currentRSI = (double) adva/decl + 1;
            }

            result.setDataItem(i, new DataItem(initial.getTimeAt(i), (100 - 100/currentRSI)));

        }

        return result;
    }

}
