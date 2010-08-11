package org.chartsy.srift;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MAType;
import com.tictactec.ta.lib.MInteger;
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
import org.chartsy.talib.TaLibInit;
import org.chartsy.talib.TaLibUtilities;
import org.openide.nodes.AbstractNode;

/**
 *
 * @author Viorel
 */
public class SRIFT extends Indicator
{

	private static final long serialVersionUID = SerialVersion.APPVERSION;

	public static final String SRIFT = "srift";
    private IndicatorProperties properties;

	public SRIFT()
	{
		super();
		properties = new IndicatorProperties();
	}

	public @Override String getName()
	{
		return "Smoothed RSI Inverse Fisher Transform";
	}

	public @Override String getLabel()
	{
		return properties.getLabel();
	}

	public @Override String getPaintedLabel(ChartFrame cf)
	{
		return getLabel();
	}

	public @Override Indicator newInstance()
	{
		return new SRIFT();
	}

	public @Override LinkedHashMap getHTML(ChartFrame cf, int i)
	{
		LinkedHashMap ht = new LinkedHashMap();

        DecimalFormat df = new DecimalFormat("#,##0.00");
        double[] values = getValues(cf, i);
        String[] labels = {"SRIFT:"};

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

    public @Override Range getRange(ChartFrame cf)
    {
		return new Range(-1, 101);
	}

	public @Override void paint(Graphics2D graphics, ChartFrame chartFrame, Rectangle bounds)
	{
		Dataset visibleDataset = visibleDataset(chartFrame, SRIFT);

        if (visibleDataset != null)
        {
            if (maximized)
            {
                Range range = getRange(chartFrame);
                DefaultPainter.line(
					graphics, chartFrame, range, bounds, visibleDataset,
					properties.getColor(), properties.getStroke()); // paint line
            }
        }
	}

	public @Override void calculate()
	{
		Dataset initial = getDataset();
        if (initial != null && !initial.isEmpty())
        {
            int rsiPeriod = properties.getRsiPeriod();
			int emaPeriod = properties.getEmaPeriod();
			int svePeriod = properties.getSvePeriod();

			Dataset sveRaindowWeighted 
				= sveRainbowWeightedDataset(initial, svePeriod);
			Dataset xInput
				= xInputDataset(rsiDataset(sveRaindowWeighted, rsiPeriod));
			Dataset ema1 = Dataset.EMA(xInput, emaPeriod);
			Dataset ema2 = Dataset.EMA(ema1, emaPeriod);
			Dataset diff = Dataset.DIFF(ema1, ema2);
			Dataset zlema = Dataset.SUM(ema1, diff);
            Dataset calculated = getInverseFisher(zlema);

            addDataset(SRIFT, calculated);
        }
	}

	public @Override boolean hasZeroLine()
    { 
		return false;
	}

    public @Override boolean getZeroLineVisibility()
    { 
		return false;
	}

    public @Override Color getZeroLineColor()
    { 
		return null;
	}

    public @Override Stroke getZeroLineStroke()
    { 
		return null;
	}

    public @Override boolean hasDelimiters()
    { 
		return true;
	}

    public @Override boolean getDelimitersVisibility()
    { 
		return true;
	}

    public @Override double[] getDelimitersValues()
    {
		return new double[]
		{
			30d,
			50d,
			70d
		};
	}

    public @Override Color getDelimitersColor()
    { 
		return new Color(0xbbbbbb);
	}

    public @Override Stroke getDelimitersStroke()
    { 
		return StrokeGenerator.getStroke(1);
	}

    public @Override Color[] getColors()
    {
		return new Color[]
		{
			properties.getColor()
		};
	}

	public @Override double[] getValues(ChartFrame chartFrame)
    {
        Dataset visibleDataset = visibleDataset(chartFrame, SRIFT);
        if (visibleDataset != null)
            return new double[]
			{
				visibleDataset.getLastClose()
			};
        return new double[] {};
    }

    public @Override double[] getValues(ChartFrame chartFrame, int i)
    {
        Dataset visibleDataset = visibleDataset(chartFrame, SRIFT);
        if (visibleDataset != null)
            return new double[]
			{
				visibleDataset.getCloseAt(i)
			};
        return new double[] {};
    }

	public @Override boolean getMarkerVisibility()
    { 
		return properties.getMarker();
	}

    public @Override AbstractNode getNode()
    { 
		return new IndicatorNode(properties);
	}

    public @Override Double[] getPriceValues(ChartFrame cf)
    {
		return new Double[]
		{
			new Double(10),
			new Double(30),
			new Double(50),
			new Double(70),
			new Double(90)
		};
	}

	private Dataset getInverseFisher(Dataset initial)
	{
		int count = 0;
		if (initial != null && !initial.isEmpty())
			count = initial.getItemsCount();

		Dataset result = Dataset.EMPTY(count);

		for (int i = 0; i < count; i++)
		{
			if (initial.getDataItem(i) != null)
			{
				long time = initial.getTimeAt(i);
				double close = initial.getCloseAt(i);
				double invfish = ((Math.exp(2 * close) - 1)
					/ (Math.exp(2 * close) + 1) + 1) * 50;
				result.setDataItem(i, new DataItem(time, invfish));
			}
		}

		return result;
	}

	private Dataset xInputDataset(Dataset initial)
	{
		int count = 0;
		if (initial != null && !initial.isEmpty())
			count = initial.getItemsCount();
		
		Dataset result = Dataset.EMPTY(count);

		for (int i = 0; i < count; i++)
		{
			if (initial.getDataItem(i) != null)
			{
				long time = initial.getTimeAt(i);
				double close = initial.getCloseAt(i);
				double x = 0.1D * (close - 50);
				result.setDataItem(i, new DataItem(time, x));
			}
		}

		return result;
	}

	private Dataset rsiDataset(Dataset initial, int period)
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

	private Dataset wmaDataset(Dataset initial, int period)
	{
		int count = 0;
		if (initial != null && !initial.isEmpty())
			count = initial.getItemsCount();

		double[] output = new double[count];
        MInteger outBegIdx = new MInteger();
        MInteger outNbElement = new MInteger();
        Core core = TaLibInit.getCore();

		int lookback = core.movingAverageLookback(period, MAType.Wma);
        core.wma(0, count-1, initial.getCloseValues(), period, outBegIdx, outNbElement, output);

		output = TaLibUtilities.fixOutputArray(output, lookback);

		Dataset wma = Dataset.EMPTY(initial.getItemsCount());
        for (int i = 0; i < output.length; i++)
            wma.setDataItem(i, new DataItem(initial.getTimeAt(i), output[i]));

		return wma;
	}

	private Dataset sveRainbowWeightedDataset(Dataset initial, int period)
	{
		int count = 0;
		if (initial != null && !initial.isEmpty())
			count = initial.getItemsCount();

		Dataset level01		= wmaDataset(initial, period);
		Dataset level02		= wmaDataset(level01, period);
		Dataset level03		= wmaDataset(level02, period);
		Dataset level04		= wmaDataset(level03, period);
		Dataset level05		= wmaDataset(level04, period);
		Dataset level06		= wmaDataset(level05, period);
		Dataset level07		= wmaDataset(level06, period);
		Dataset level08		= wmaDataset(level07, period);
		Dataset level09		= wmaDataset(level08, period);
		Dataset level10		= wmaDataset(level09, period);

		Dataset result = Dataset.EMPTY(count);

		for (int i = 0; i < count; i++)
		{
			if (level10.getDataItem(i) != null)
			{
				long time = level10.getTimeAt(i);
				double value01 = 5 * level01.getCloseAt(i);
				double value02 = 4 * level02.getCloseAt(i);
				double value03 = 3 * level03.getCloseAt(i);
				double value04 = 2 * level04.getCloseAt(i);
				double value05 = level05.getCloseAt(i);
				double value06 = level06.getCloseAt(i);
				double value07 = level07.getCloseAt(i);
				double value08 = level08.getCloseAt(i);
				double value09 = level09.getCloseAt(i);
				double value10 = level10.getCloseAt(i);

				double sveRW = (value01 + value02 + value03 + value04 + value05
					+ value06 + value07 + value08 + value09 + value10) / 20;
				result.setDataItem(i, new DataItem(time, sveRW));
			}
		}

		return initial;
	}

}
