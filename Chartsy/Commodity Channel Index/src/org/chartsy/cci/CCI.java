package org.chartsy.cci;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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
public class CCI 
        extends Indicator
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;
    
    public static final String CCI = "cci";
    private IndicatorProperties properties;

    public CCI()
    {
        super();
        properties = new IndicatorProperties();
    }

    public String getName()
    { return "CCI"; }

    public String getLabel() 
    { return properties.getLabel() + "(" + properties.getPeriod() + ")"; }

    public String getPaintedLabel(ChartFrame cf)
    { return getLabel(); }

    public Indicator newInstance() 
    { return new CCI(); }

    public LinkedHashMap getHTML(ChartFrame cf, int i)
    {
        LinkedHashMap ht = new LinkedHashMap();

        DecimalFormat df = new DecimalFormat("#,##0.00");
        double[] values = getValues(cf, i);
        String[] labels = {"CCI:"};

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
        Dataset d = visibleDataset(cf, CCI);

        if (d != null) 
        {
            if (maximized)
            {
                Range range = getRange(cf);
                DefaultPainter.line(g, cf, range, bounds, d, properties.getColor(), properties.getStroke()); // paint line
                if (properties.getInsideVisibility())
                {
                    paintFill(g, cf, d, bounds, range, properties.getInsideTransparentColor(), -100d, range.getLowerBound() - 10, false); // paint fill
                    paintFill(g, cf, d, bounds, range, properties.getInsideTransparentColor(), 100d, range.getUpperBound() + 10, true); // paint fill
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
            addDataset(CCI, d);
        }
    }

    public boolean hasZeroLine()
    { return true; }

    public boolean getZeroLineVisibility()
    { return properties.getZeroLineVisibility(); }

    public Color getZeroLineColor()
    { return properties.getZeroLineColor(); }

    public Stroke getZeroLineStroke()
    { return properties.getZeroLineStroke(); }

    public boolean hasDelimiters()
    { return true; }

    public boolean getDelimitersVisibility()
    { return true; }

    public double[] getDelimitersValues()
    { return new double[] {-200.0d, -100.0d, 100.0d, 200.0d}; }

    public Color getDelimitersColor() 
    { return properties.getZeroLineColor(); }

    public Stroke getDelimitersStroke()
    { return properties.getZeroLineStroke(); }

    public Color[] getColors()
    { return new Color[] {properties.getColor()}; }

    public double[] getValues(ChartFrame cf)
    {
        Dataset d = visibleDataset(cf, CCI);
        
        int i = d.getLastIndex();
        if (d.getDataItem(i) != null)
            return new double[] {d.getCloseAt(i)};
        return new double[] {0};
    }

    public double[] getValues(ChartFrame cf, int i)
    {
        Dataset d = visibleDataset(cf, CCI);

        if (d.getDataItem(i) != null)
            return new double[] {d.getCloseAt(i)};
        return new double[] {0};
    }

    public boolean getMarkerVisibility()
    { return properties.getMarker(); }

    public AbstractNode getNode()
    { return new IndicatorNode(properties); }

    @Override
    public Double[] getPriceValues(ChartFrame cf)
    {
        List<Double> list = new ArrayList<Double>();
        Range range = getRange(cf);

        list.add(new Double(-200));
        list.add(new Double(-100));
        list.add(new Double(100));
        list.add(new Double(200));

        if (range.getUpperBound() >= 300)
            list.add(new Double(300));
        if (range.getLowerBound() <= -300)
            list.add(new Double(-300));

        return list.toArray(new Double[list.size()]);
    }


    private Dataset getDataset(final Dataset initial, final int period) {
        int count = initial.getItemsCount();
        Dataset d = Dataset.EMPTY(count);

        for (int i = period - 1; i < count; i++)
        {
            double TP = (initial.getHighAt(i) + initial.getLowAt(i) + initial.getCloseAt(i)) / 3;

            double TPMA = 0D;
            for (int j = i - period + 1; j <= i; j++) 
                TPMA += ((initial.getHighAt(j) + initial.getLowAt(j) + initial.getCloseAt(j)) / 3);

            TPMA /= (double)period;

            double MD = 0D;
            for (int j = i - period + 1; j <= i; j++) 
                MD += Math.abs((initial.getHighAt(j) + initial.getLowAt(j) + initial.getCloseAt(j)) / 3 - TPMA);
            
            MD /= (double)period;

            double close = (TP - TPMA)/(0.015D * MD);
            d.setDataItem(i, new DataItem(initial.getTimeAt(i), close));
        }

        return d;
    }

}
