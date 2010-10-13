package org.chartsy.moneyflow;

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
import org.chartsy.main.intervals.DailyInterval;
import org.chartsy.main.utils.DefaultPainter;
import org.chartsy.main.utils.Range;
import org.chartsy.main.utils.SerialVersion;
import org.openide.nodes.AbstractNode;

/**
 *
 * @author viorel.gheba
 */
public class MoneyFlow
        extends Indicator
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public static final String MFH = "mfh";
    public static final String MFL = "mfl";
    private boolean toggle = false;
    private boolean toggle2 = false;

    private IndicatorProperties properties;

    public MoneyFlow()
    {
        super();
        properties = new IndicatorProperties();
    }

    public String getName(){ return "Money Flow"; }

    public String getLabel(){ return properties.getLabel(); }

    public Indicator newInstance(){ return new MoneyFlow(); }

    public String getPaintedLabel(ChartFrame cf)
    {
        if (cf.getChartData().getInterval() instanceof DailyInterval)
        {
            DecimalFormat df = new DecimalFormat("###,###");
            double factor = getFactor(cf);
            return getLabel() + " x " + df.format((int)factor);
        }
        else
        {
            return "Money Flow indicator is available only for daily data";
        }
    }

    public LinkedHashMap getHTML(ChartFrame cf, int i)
    {
        LinkedHashMap ht = new LinkedHashMap();

        DecimalFormat df = new DecimalFormat("#,##0.00");
        double[] values = getValues(cf, i);
        String[] labels = !toggle2 ? new String[] {"+MF:"} : new String[] {"-MF:"};

        ht.put(getLabel(), " ");
        if (cf.getChartData().getInterval() instanceof DailyInterval)
        {
            if (values.length > 0)
            {
                Color[] colors = getColors();
                for (int j = 0; j < values.length; j++)
                {
                    ht.put(getFontHTML(colors[j], labels[j]),
                            getFontHTML(colors[j], df.format(values[j])));
                }
            }
        }

        return ht;
    }

    @Override
    public Range getRange(ChartFrame cf)
    {
        double factor = getFactor(cf);
        Range range = super.getRange(cf);
        double d = Math.max(Math.abs(range.getLowerBound()), Math.abs(range.getUpperBound())) / factor;
        return new Range(-1*d, d);
    }

    public void paint(Graphics2D g, ChartFrame cf, Rectangle bounds)
    {
        if (cf.getChartData().getInterval() instanceof DailyInterval)
        {
            Dataset mfh = visibleDataset(cf, MFH);
            Dataset mfl = visibleDataset(cf, MFL);

            if (mfh != null && mfl != null)
            {
                if (maximized)
                {
                    double factor = getFactor(cf);
                    Range range = getRange(cf);

                    DefaultPainter.bar(g, cf, range, bounds, Dataset.DIV(mfh, factor), properties.getMFHColor());
                    DefaultPainter.bar(g, cf, range, bounds, Dataset.DIV(mfl, factor), properties.getMFLColor());
                }
            }
        }
    }

    public void calculate()
    {
        Dataset initial = getDataset();
        if (initial != null && !initial.isEmpty())
        {
            int count = initial.getItemsCount();
            Dataset mfh = Dataset.EMPTY(count);
            Dataset mfl = Dataset.EMPTY(count);

            mfh.setDataItem(0, new DataItem(initial.getTimeAt(0), ((initial.getCloseAt(0) + initial.getHighAt(0) + initial.getLowAt(0)) / 3) * initial.getVolumeAt(0)));

            for (int i = 1; i < count; i++)
            {
                double tp1 = (initial.getCloseAt(i) + initial.getHighAt(i) + initial.getLowAt(i)) / 3;
                double tp2 = (initial.getCloseAt(i-1) + initial.getHighAt(i-1) + initial.getLowAt(i-1)) / 3;
                double rmf = tp1 * initial.getVolumeAt(i);

                if (tp1 > tp2)
                    mfh.setDataItem(i, new DataItem(initial.getTimeAt(i), rmf));
                else if (tp1 < tp2)
                    mfl.setDataItem(i, new DataItem(initial.getTimeAt(i), -1*rmf));
            }

            addDataset(MFH, mfh);
            addDataset(MFL, mfl);
        }
    }

    public boolean hasZeroLine(){ return true; }

    public boolean getZeroLineVisibility(){ return properties.getZeroLineVisibility(); }

    public Color getZeroLineColor(){ return properties.getZeroLineColor(); }

    public Stroke getZeroLineStroke(){ return properties.getZeroLineStroke(); }

    public boolean hasDelimiters(){ return false; }

    public boolean getDelimitersVisibility(){ return false; }

    public double[] getDelimitersValues(){ return new double[] {}; }

    public Color getDelimitersColor(){ return null; }

    public Stroke getDelimitersStroke(){ return null; }

    public Color[] getColors()
    { 
        if (!toggle)
            return new Color[] {properties.getMFHColor()};
        else
            return new Color[] {properties.getMFLColor()};
    }

    public double[] getValues(ChartFrame cf)
    {
        Dataset mfh = visibleDataset(cf, MFH);
        Dataset mfl = visibleDataset(cf, MFL);
        double factor = getFactor(cf);

        if (mfh.getDataItem(mfh.getLastIndex()) != null)
        {
            toggle = false;
            return new double[] {mfh.getLastClose()/factor};
        }

        if (mfl.getDataItem(mfl.getLastIndex()) != null)
        {
            toggle = true;
            return new double[] {mfl.getLastClose()/factor};
        }

        return new double[] {};
    }

    public double[] getValues(ChartFrame cf, int i)
    {
        Dataset mfh = visibleDataset(cf, MFH);
        Dataset mfl = visibleDataset(cf, MFL);
        double factor = getFactor(cf);

        if (mfh.getDataItem(mfh.getLastIndex()) != null)
        {
            toggle2 = false;
            return new double[] {mfh.getCloseAt(i)/factor};
        }

        if (mfl.getDataItem(mfl.getLastIndex()) != null)
        {
            toggle2 = true;
            return new double[] {mfl.getCloseAt(i)/factor};
        }

        return new double[] {};
    }

    public boolean getMarkerVisibility(){ return properties.getMarker(); }

    public AbstractNode getNode(){ return new IndicatorNode(properties); }

    @Override
    public Double[] getPriceValues(ChartFrame cf)
    {
        List<Double> list = new ArrayList<Double>();

        Range range = getRange(cf);

        int step = (int) (range.getUpperBound() / 3) + 1;
        for (int i = step; i <= range.getUpperBound(); i+=step)
        {
            list.add(new Double(i));
            list.add(new Double(-1*i));
        }

        return list.toArray(new Double[list.size()]);
    }

    private double getFactor(ChartFrame cf)
    {
        Dataset mfh = visibleDataset(cf, MFH);
        Dataset mfl = visibleDataset(cf, MFL);

        if (mfh != null && mfl != null)
        {
            double max = mfh.getCloseAt(0);
            double min = mfl.getCloseAt(0);

            for (int i = 1; i < mfh.getItemsCount(); i++)
            {
                if (max < mfh.getCloseAt(i))
                    max = mfh.getCloseAt(i);

                if (min > mfl.getCloseAt(i))
                    min = mfl.getCloseAt(i);
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
