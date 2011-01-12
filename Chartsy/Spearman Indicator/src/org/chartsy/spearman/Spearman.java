package org.chartsy.spearman;

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
 * @author Viorel
 */
public class Spearman extends Indicator
{

	private static final long serialVersionUID = SerialVersion.APPVERSION;

	public static final String SPEARMAN = "spearman";
	public static final String SMA = "sma";
	private IndicatorProperties properties;

	public Spearman()
	{
		super();
		properties = new IndicatorProperties();
	}

	public @Override String getName()
	{
		return "Spearman Indicator";
	}

	public @Override String getLabel()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(properties.getLabel());
		builder.append(" (").append(properties.getPeriod()).append(")");
		return builder.toString();
	}

	public @Override String getPaintedLabel(ChartFrame cf)
	{
		return getLabel();
	}

	public @Override Indicator newInstance()
	{
		Indicator indicator = new Spearman();
		return indicator;
	}

	public @Override LinkedHashMap getHTML(ChartFrame cf, int i)
	{
		LinkedHashMap ht = new LinkedHashMap();

		DecimalFormat df = new DecimalFormat("#,##0.00");
        double[] values = getValues(cf, i);
        String[] labels = {"Spearman:", "SMA:"};

		ht.put(getLabel(), " ");
		if (values.length > 0) {
            Color[] colors = getColors();
            for (int j = 0; j < values.length; j++)
			{
                ht.put(getFontHTML(colors[j], labels[j]),
                        getFontHTML(colors[j], df.format(values[j])));
            }
        }

		return ht;
	}

	public @Override void paint(Graphics2D graphics, ChartFrame chartFrame, Rectangle bounds)
	{
		Dataset spearman = visibleDataset(chartFrame, SPEARMAN);
		Dataset sma = visibleDataset(chartFrame, SMA);

		if (spearman != null)
		{
			if (maximized)
			{
				Range range = getRange(chartFrame);
				DefaultPainter.line(
					graphics, chartFrame, range, bounds, spearman,
					properties.getColor(), properties.getStroke()); // paint line

				if (sma != null)
				{
					DefaultPainter.line(
						graphics, chartFrame, range, bounds, sma,
						properties.getSMAColor(), properties.getSMAStroke()); // paint sma line
				}
			}
		}
	}

	public @Override void calculate()
	{
		long[] times = getDataset().getTimeValues();
		double[] closes = getDataset().getCloseValues();
		int count = getDataset().getItemsCount();
		int period = properties.getPeriod();

		Dataset spearman = Dataset.EMPTY(count);

		for (int i = period; i < count; i++)
		{
			double[] r1 =  new double[period], // time series order
				r2 = new double[period],
				r11 = new double[period], // price (close)
				r21 = new double[period], // internal sort table
				r22 = new double[period]; // order of prices (close)

			for (int j = 0; j < period; j++)
			{
				r1[j] = j;
				r22[j] = j;
				r11[j] = closes[i - period + j];
				r21[j] = closes[i - period + j];
			}

			int changed = 1;
			while (changed > 0)
			{
				changed = 0;
				for (int j = 0; j < period - 1; j++)
				{
					if (r21[j + 1] < r21[j])
					{
						double temp = r21[j];
						r21[j] = r21[j + 1];
						r21[j + 1] = temp;
						changed = 1;
					}
				}
			}

			for (int j = 0; j < period; j++)
			{
				int found = 0;
				while (found < 1)
				{
					for (int k = 0; k < period; k++)
					{
						if (r21[k] == r11[j])
						{
							r22[j] = k;
							found = 1;
						}
					}
				}
			}

			double absum = 0;
			for (int j = 0; j < period; j++)
			{
				double ab = r1[j] - r22[j];
				double ab2 = Math.pow(ab, 2);
				absum += ab2;
			}

			double coefcorr = (1 - (6 * absum) / (period * (period * period - 1)));
			long time = times[i];
			double close = 100 * coefcorr;
			
			DataItem item = new DataItem(time, close);
			spearman.setDataItem(i, item);
		}

		int smaPeriod = properties.getSMAPeriod();
		Dataset sma = Dataset.SMA(spearman, smaPeriod);

		addDataset(SPEARMAN, spearman);
		addDataset(SMA, sma);
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
		return false;
	}

    public @Override boolean getDelimitersVisibility()
    {
		return false;
	}

    public @Override double[] getDelimitersValues()
    {
		return new double[] {};
	}

    public @Override Color getDelimitersColor()
    {
		return null;
	}

    public @Override Stroke getDelimitersStroke()
    {
		return null;
	}

    public @Override Color[] getColors()
    {
		return new Color[]
		{
			properties.getColor(),
			properties.getSMAColor()
		};
	}

	public @Override double[] getValues(ChartFrame chartFrame)
    {
        if (datasetExists(SPEARMAN) && datasetExists(SMA))
            return new double[]
			{
				visibleDataset(chartFrame, SPEARMAN).getLastClose(),
				visibleDataset(chartFrame, SMA).getLastClose()
			};
        return new double[] {};
    }

    public @Override double[] getValues(ChartFrame chartFrame, int i)
    {
        if (datasetExists(SPEARMAN) && datasetExists(SMA))
            return new double[]
			{
				visibleDataset(chartFrame, SPEARMAN).getCloseAt(i),
				visibleDataset(chartFrame, SMA).getCloseAt(i)
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

	@Override
	public Range getRange(ChartFrame cf)
	{
		if (datasets.isEmpty())
			return new Range();

		Dataset spearman = visibleDataset(cf, SPEARMAN);
		Dataset sma = visibleDataset(cf, SMA);

		double min = Math.min(
			spearman.getMin(Dataset.CLOSE_PRICE),
			sma.getMin(Dataset.CLOSE_PRICE));
		double max = Math.max(
			spearman.getMax(Dataset.CLOSE_PRICE),
			sma.getMax(Dataset.CLOSE_PRICE));
		max = Math.max(max, Math.abs(min));
		max = Math.round(max);
		max = max + (10 - (max % 10)) + 10;
		min = -1 * max;

		Range range = new Range(min, max);
		return range;
	}

	@Override
	public Double[] getPriceValues(ChartFrame cf)
	{
		Double[] values = new Double[5];
		Range range = getRange(cf);

		double max = range.getUpperBound() - 10;
		values[0] = new Double(0);
		values[1] = new Double(max);
		values[2] = new Double(max / 2);
		values[3] = new Double(-1 * max);
		values[4] = new Double((-1 * max) / 2);

		return values;
	}

}
