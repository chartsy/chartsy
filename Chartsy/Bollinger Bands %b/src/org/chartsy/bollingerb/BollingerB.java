package org.chartsy.bollingerb;

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
import org.chartsy.main.utils.CalcUtil;
import org.chartsy.main.utils.DefaultPainter;
import org.chartsy.main.utils.Range;
import org.chartsy.main.utils.SerialVersion;
import org.openide.nodes.AbstractNode;

/**
 *
 * @author viorel.gheba
 */
public class BollingerB
        extends Indicator
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;
    
    public static final String UPPER = "upper";
    public static final String LOWER = "lower";
    public static final String SVE_BB = "SVE_BB";
    public static final String D50 = "50";
    
    private IndicatorProperties properties;

    public BollingerB()
    {
        super();
        properties = new IndicatorProperties();
    }

    public String getName()
    { return "SVE_BB%b"; }

    public String getLabel() 
    { return properties.getLabel() + " (" + properties.getPeriod() + ")"; }

    public String getPaintedLabel(ChartFrame cf)
    { return getLabel(); }

    public Indicator newInstance() 
    { return new BollingerB(); }

    public LinkedHashMap getHTML(ChartFrame cf, int i)
    {
        LinkedHashMap ht = new LinkedHashMap();

        DecimalFormat df = new DecimalFormat("#,##0.00");
        double[] values = getValues(cf, i);
        String[] labels = {"SVE_BB%b:"};

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
        Dataset d = visibleDataset(cf, SVE_BB);
        Dataset upper = visibleDataset(cf, UPPER);
        Dataset lower = visibleDataset(cf, LOWER);

        if (d != null) 
        {
            if (maximized)
            {
                Range range = getRange(cf);

                if (upper != null)
                    DefaultPainter.line(g, cf, range, bounds, upper, properties.getStdColor(), properties.getStdStroke());

                if (lower != null)
                    DefaultPainter.line(g, cf, range, bounds, lower, properties.getStdColor(), properties.getStdStroke());

                DefaultPainter.line(g, cf, range, bounds, d, properties.getColor(), properties.getStroke());
            }
        }
    }

    public void calculate()
    {
        Dataset initial = getDataset();
        if (initial != null && !initial.isEmpty()) {
            Dataset d = getSVE_BB(initial);
            addDataset(SVE_BB, d);

            Dataset upper = getLowerUpperDataset(d, UPPER);
            addDataset(UPPER, upper);

            Dataset lower = getLowerUpperDataset(d, LOWER);
            addDataset(LOWER, lower);
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
    { return new double[] {50.0d}; }

    public Color getDelimitersColor() 
    { return properties.getStdColor(); }

    public Stroke getDelimitersStroke()
    { return properties.getStdStroke(); }

    public Color[] getColors()
    { return new Color[] {properties.getColor()}; }

    public double[] getValues(ChartFrame cf)
    {
        Dataset d = visibleDataset(cf, SVE_BB);

        int i = d.getLastIndex();
        if (d.getDataItem(i) != null)
            return new double[] {d.getCloseAt(i)};
        return new double[] {0};
    }

    public double[] getValues(ChartFrame cf, int i)
    {
        Dataset d = visibleDataset(cf, SVE_BB);

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
        list.add(new Double(50));

        Range range = getRange(cf);
        int max = (int) range.getUpperBound();
        if (max > 0)
            max = max - max%10;
        else
        {
            int i = Math.abs(max%10);
            max = max - (5-i);
        }

        int min = (int) Math.ceil(range.getLowerBound());
        if (min > 0)
        {
            int i = min%10;
            min = min + (i<5 ? 5-i : 10-i);
        }
        else
        {
            int i = Math.abs(min%10);
            min = min + (i<5 ? i : i-5);
        }

        list.add(new Double((double)min));
        list.add(new Double((double)max));
        list.add(new Double((max+50)/2));
        list.add(new Double((50/2) + (min/2)));

        return list.toArray(new Double[list.size()]);
    }

    private Dataset getHaC(Dataset dataset)
    {
        int count = dataset.getItemsCount();
        Dataset HaC = Dataset.EMPTY(count);

        double o = dataset.getOpenAt(0);
        double c = dataset.getCloseAt(0);
        double h = dataset.getHighAt(0);
        double l = dataset.getLowAt(0);
        double prev = (o+c+h+l)/4;
        HaC.setDataItem(0, new DataItem(dataset.getTimeAt(0), prev));

        for (int i = 1; i < count; i++)
        {
            o = dataset.getOpenAt(i-1);
            c = dataset.getCloseAt(i-1);
            h = dataset.getHighAt(i-1);
            l = dataset.getLowAt(i-1);

            double haOpen = ((o+c+h+l)/4 + prev)/2;
            prev = haOpen;

            o = dataset.getOpenAt(i);
            c = dataset.getCloseAt(i);
            h = dataset.getHighAt(i);
            l = dataset.getLowAt(i);

            double haClose = ((o+c+h+l)/4 + haOpen + Math.max(h, haOpen) + Math.min(l, haOpen))/4;

            HaC.setDataItem(i, new DataItem(dataset.getTimeAt(i), haClose));
        }

        return HaC;
    }

    private Dataset getSVE_BB(final Dataset initial)
    {
        int period = properties.getPeriod();
        int temaPeriod = properties.getTemaPeriod();
        int count = initial.getItemsCount();

        Dataset sve = Dataset.EMPTY(count);
        Dataset haC = getHaC(initial);
        Dataset TMA1 = Dataset.TEMA(haC, temaPeriod);
        Dataset TMA2 = Dataset.TEMA(TMA1, temaPeriod);
        Dataset ZLHA = Dataset.TEMA(Dataset.SUM(TMA1, Dataset.DIFF(TMA1, TMA2)), temaPeriod);
        Dataset MOV = Dataset.WMA(ZLHA, period);

        int j = 0;
        for (j = 0; j < count && (MOV.getDataItem(j)==null); j++)
            sve.setDataItem(j, null);

        for (int i = j; i < count; i++)
        {
            double mov = MOV.getCloseAt(i);
            double stddev = CalcUtil.stdDev(ZLHA, Dataset.CLOSE_PRICE, i, period);
            double close = stddev == 0 ? 0 : ((ZLHA.getCloseAt(i) + 2*stddev - mov)/(4*stddev))*100;
            sve.setDataItem(i, new DataItem(initial.getTimeAt(i), close));
        }

        return sve;
    }

    private Dataset getLowerUpperDataset(final Dataset initial, final String type) 
    {
        int count = initial.getItemsCount();
        int stdPeriod = properties.getStdPeriod();

        double sgn = type.equals(LOWER) ? -1 : 1;
        double afw = type.equals(LOWER) ? properties.getStdLow() : properties.getStdHigh();

        Dataset d = Dataset.EMPTY(count);

        int j = 0;
        for (j = 0; j < count && (initial.getDataItem(j)==null); j++)
            d.setDataItem(j, null);

        for (int i = j; i < count; i++)
        {
            double stddev = CalcUtil.stdDev(initial, Dataset.CLOSE, i, stdPeriod);
            d.setDataItem(i, new DataItem(initial.getTimeAt(i), (50D + (sgn * stddev * afw))));
        }

        return d;
    }

}
