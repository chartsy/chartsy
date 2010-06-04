package org.chartsy.fractaldimension;

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
public class FractalDimension 
        extends Indicator
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public static final String FD = "fd";
    private IndicatorProperties properties;

    public FractalDimension()
    {
        super();
        properties = new IndicatorProperties();
    }

    public String getName() 
    { return "Fractal Dimension"; }

    public String getLabel()
    { return properties.getLabel() + " (" + properties.getPeriod() + ")"; }

    public String getPaintedLabel(ChartFrame cf)
    { return getLabel(); }

    public Indicator newInstance() 
    { return new FractalDimension(); }

    public LinkedHashMap getHTML(ChartFrame cf, int i)
    {
        LinkedHashMap ht = new LinkedHashMap();

        DecimalFormat df = new DecimalFormat("#,##0.00");
        double[] values = getValues(cf, i);
        String[] labels = {"FD:"};

        ht.put(getLabel(), " ");
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

    @Override
    public Range getRange(ChartFrame cf)
    {
        Range range = super.getRange(cf);
        range = new Range(Math.min(1.3d, range.getLowerBound()), Math.max(1.7d, range.getUpperBound()));
        return range;
    }

    public void paint(Graphics2D g, ChartFrame cf, Rectangle bounds)
    {
        Dataset d = visibleDataset(cf, FD);

        if (d != null)
        {
            if (maximized)
            {
                Range range = getRange(cf);

                DefaultPainter.line(g, cf, range, bounds, d, properties.getColor(), properties.getStroke()); // paint line
                if (properties.getInsideVisibility())
                    paintFill(g, cf, d, bounds, range, properties.getInsideTransparentColor(), 1.4d, 1.6d, true); // paint fill
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
            addDataset(FD, d);
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
    { return new double[] {1.4d, 1.6d}; }

    public Color getDelimitersColor() 
    { return properties.getDelimiterColor(); }

    public Stroke getDelimitersStroke() 
    { return properties.getDelimiterStroke(); }

    public Color[] getColors()
    { return new Color[] {properties.getColor()}; }

    public double[] getValues(ChartFrame cf)
    {
        Dataset d = visibleDataset(cf, FD);
        
        int i = d.getLastIndex();
        if (d.getDataItem(i) != null)
            return new double[] {d.getCloseAt(i)};
        return new double[] {0};
    }

    public double[] getValues(ChartFrame cf, int i)
    {
        Dataset d = visibleDataset(cf, FD);

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
    { return new Double[] {new Double(0.2), new Double(0.4), new Double(0.6), new Double(0.8), new Double(1), new Double(1.2), new Double(1.4), new Double(1.6), new Double(1.8), new Double(2.0)}; }

    private Dataset getDataset(final Dataset initial, final int period) {
        int T = (int)(period/2);
        int count = initial.getItemsCount();

        double[] input = new double[count];
        for (int i = 0; i < count; i++)
            input[i] = (initial.getHighAt(i) + initial.getLowAt(i)) / 2;

        double[] smooth = new double[count];
        for (int i = 0; i < count; i++)
            smooth[i] = 0;
        for (int i = 3; i < count; i++)
            smooth[i] = (input[i] + 2*input[i-1] + 2*input[i-2] + input[i-3]) / 6;

        double[] n1 = new double[count];
        double[] n2 = new double[count];
        double[] n3 = new double[count];
        for (int i = 0; i < count; i++) {
            n1[i] = 0;
            n2[i] = 0;
            n3[i] = 0;
        }
        for (int i = 2*T + 3; i < count; i++) {
            n1[i] = (max(smooth, i, T) - min(smooth, i, T)) / T;
            n2[i] = (max(smooth, i - T, T) - min(smooth, i - T, T)) / T;
            n3[i] = (max(smooth, i, 2*T) - min(smooth, i, 2*T)) / (2*T);
        }

        double[] dimen = new double[count];
        double[] ratio = new double[count];
        for (int i = 0; i < count; i++) {
            ratio[i] = 0;
            dimen[i] = 0;
        }
        for (int i = 2*T + 23; i < count; i++) {
            if (n1[i] > 0.0 && n2[i] > 0.0 && n3[i] > 0.0)
                ratio[i] = ((Math.log(n1[i] + n2[i]) - Math.log(n3[i])) / Math.log(2) + dimen[i-1]) / 2f;
            dimen[i] = avg(ratio, i, 20);
        }

        Dataset d = Dataset.EMPTY(count);

        for (int i = 0; i < count; i++)
            d.setDataItem(i, (dimen[i] == 0 ? null : new DataItem(initial.getTimeAt(i), dimen[i])));

        return d;
    }

    private double avg(double[] list, int current, int period) {
        double result = 0;
        for (int i = current; i > current - period; i--) {
            result += list[i];
        }
        return result/(double)period;
    }

    private double max(double[] list, int current, int period) {
        double result = list[current];
        for (int i = current; i > current - period; i--) {
            if (result < list[i]) {
                result = list[i];
            }
        }
        return result;
    }

    private double min(double[] list, int current, int period) {
        double result = list[current];
        for (int i = current; i > current - period; i--) {
            if (result > list[i]) {
                result = list[i];
            }
        }
        return result;
    }

}
