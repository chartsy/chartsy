package org.chartsy.macd;

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
public class MACD
        extends Indicator
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public static final String MACD = "macd";
    public static final String SIGNAL = "signal";
    public static final String HISTOGRAM = "histogram";
    private Color histogramColor = null;
    private IndicatorProperties properties;

    public MACD()
    {
        super();
        properties = new IndicatorProperties();
    }

    public String getName()
    { return "MACD"; }

    public String getLabel()
    { return properties.getLabel() + " (" + properties.getFast() + ", " + properties.getSlow() + ", " + properties.getSmooth() + ")"; }

    public String getPaintedLabel(ChartFrame cf){ return getLabel(); }

    public Indicator newInstance(){ return new MACD(); }

    public LinkedHashMap getHTML(ChartFrame cf, int i)
    {
        LinkedHashMap ht = new LinkedHashMap();

        DecimalFormat df = new DecimalFormat("#,##0.00");
        double[] values = getValues(cf, i);
        String[] labels = {"Histogram:", "Signal:", "MACD:"};

        ht.put(getLabel(), " ");
        if (values.length > 0)
        {
            Color[] colors = getColors();
            colors[0] = values[0] > 0 ? properties.getHistogramPositiveColor() : properties.getHistogramNegativeColor();
            for (int j = 0; j < values.length; j++)
            {
                ht.put(getFontHTML(colors[j], labels[j]), getFontHTML(colors[j], df.format(values[j])));
            }
        }

        return ht;
    }

    @Override
    public Range getRange(ChartFrame cf)
    {
		Range range = super.getRange(cf);
		double d = Math.max(Math.abs(range.getLowerBound()), Math.abs(range.getUpperBound()));
		return new Range(-1 * d, d);
    }


    public void paint(Graphics2D g, ChartFrame cf, Rectangle bounds)
    {
        Dataset macd = visibleDataset(cf, MACD);
        Dataset signal = visibleDataset(cf, SIGNAL);
        Dataset histogram = visibleDataset(cf, HISTOGRAM);

        if (histogram != null && signal != null && macd != null) 
        {
            if (maximized)
            {
                Range range = getRange(cf);

                DefaultPainter.histogram(g, cf, range, bounds, histogram, properties.getHistogramPositiveColor(), properties.getHistogramNegativeColor()); // paint the histogram
                DefaultPainter.line(g, cf, range, bounds, signal, properties.getSignalColor(), properties.getSignalStroke()); // paint the signal
                DefaultPainter.line(g, cf, range, bounds, macd, properties.getMacdColor(), properties.getMacdStroke()); // paint the MACD
            }
        }
    }

    public void calculate()
    {
        Dataset initial = getDataset();

        if (initial != null && !initial.isEmpty())
        {
            int fast = properties.getFast();
            int slow = properties.getSlow();
            int smooth = properties.getSmooth();

            Dataset fastEMA = Dataset.EMA(initial, fast);
            Dataset slowEMA = Dataset.EMA(initial, slow);

            Dataset macd = getMACD(fastEMA, slowEMA, slow);
            addDataset(MACD, macd);

            Dataset signal = getSignal(macd, slow, smooth);
            addDataset(SIGNAL, signal);

            Dataset MACDHistogram = getMACDHistogram(macd, signal);
            addDataset(HISTOGRAM, MACDHistogram);
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
    {  return new Color[] {histogramColor!=null ? histogramColor : properties.getHistogramPositiveColor(), properties.getSignalColor(), properties.getMacdColor()}; }

    public double[] getValues(ChartFrame cf)
    {
        Dataset macd = visibleDataset(cf, MACD);
        Dataset signal = visibleDataset(cf, SIGNAL);
        Dataset histogram = visibleDataset(cf, HISTOGRAM);

		if (macd != null && signal != null && histogram != null)
		{
			int i = histogram.getLastIndex();
			double[] values = new double[3];
			values[0] = histogram.getDataItem(i) != null ? histogram.getCloseAt(i) : 0;
			values[1] = signal.getDataItem(i) != null ? signal.getCloseAt(i) : 0;
			values[2] = macd.getDataItem(i) != null ? macd.getCloseAt(i) : 0;

			if (histogram.getDataItem(i) != null)
				histogramColor = histogram.getCloseAt(i) > 0 ? properties.getHistogramPositiveColor() : properties.getHistogramNegativeColor();

			return values;
		}

		return new double[] {};
    }

    public double[] getValues(ChartFrame cf, int i)
    {
        Dataset macd = visibleDataset(cf, MACD);
        Dataset signal = visibleDataset(cf, SIGNAL);
        Dataset histogram = visibleDataset(cf, HISTOGRAM);

		if (macd != null && signal != null && histogram != null)
		{
			double[] values = new double[3];
			values[0] = histogram.getDataItem(i) != null ? histogram.getCloseAt(i) : 0;
			values[1] = signal.getDataItem(i) != null ? signal.getCloseAt(i) : 0;
			values[2] = macd.getDataItem(i) != null ? macd.getCloseAt(i) : 0;

			if (histogram.getDataItem(i) != null)
				histogramColor = histogram.getCloseAt(i) > 0 ? properties.getHistogramPositiveColor() : properties.getHistogramNegativeColor();

			return values;
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
		Double value;
        if (range.getUpperBound() >= 1)
        {
            int step = (int) (range.getUpperBound() / 3) + 1;
            for (int i = step; i <= range.getUpperBound(); i+=step)
            {
				value = new Double(i);
                list.add(value);
				value = new Double(-1 * i);
                list.add(value);
            }
        }
        else
        {
            double step = (range.getUpperBound() > 0.5) ? 0.25 : 0.15;
            for (double i = step; i <= range.getUpperBound(); i+=step)
            {
				value = new Double(i);
                list.add(value);
				value = new Double(-1 * i);
                list.add(value);
            }
        }

        return list.toArray(new Double[list.size()]);
    }

    private Dataset getMACD(final Dataset fastEMA, final Dataset slowEMA, final int slow)
    {
        int count  = fastEMA.getItemsCount();
        Dataset result = Dataset.EMPTY(count);

        for (int i = slow; i < count; i++)
        {
            double diff = fastEMA.getCloseAt(i) - slowEMA.getCloseAt(i);
            result.setDataItem(i, new DataItem(fastEMA.getTimeAt(i), diff));
        }

        return result;
    }

    private Dataset getSignal(final Dataset MACD, final int slow, final int smooth)
    {
        int count  = MACD.getItemsCount();
        Dataset result = Dataset.EMPTY(count);
        
        double close = 0;
        
        for (int i = slow; i < slow + smooth; i++)
            close += MACD.getCloseAt(i);

        close /= smooth;

        for (int i = (slow + smooth); i < count; i++)
        {
            double close2 = (2 * (MACD.getCloseAt(i) - close))/(1 + smooth) + close;
            result.setDataItem(i, new DataItem(MACD.getTimeAt(i), close));
            close = close2;
        }

        return result;
    }

    private Dataset getMACDHistogram(final Dataset MACD, final Dataset signal)
    {
        int count = MACD.getItemsCount();
        Dataset result = Dataset.EMPTY(count);
        
        for (int i = 0; i < count; i++)
        {
            if (signal.getDataItem(i) != null && MACD.getDataItem(i) != null)
            {
                double diff = MACD.getCloseAt(i) - signal.getCloseAt(i);
                result.setDataItem(i, new DataItem(MACD.getTimeAt(i), diff));
            }
        }

        return result;
    }

}
