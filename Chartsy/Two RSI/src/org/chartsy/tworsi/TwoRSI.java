package org.chartsy.tworsi;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.util.LinkedHashMap;
import org.chartsy.main.ChartFrame;
import org.chartsy.main.chart.Overlay;
import org.chartsy.main.data.DataItem;
import org.chartsy.main.data.Dataset;
import org.chartsy.main.utils.Range;
import org.chartsy.main.utils.SerialVersion;
import org.openide.nodes.AbstractNode;

/**
 *
 * @author Viorel
 */
public class TwoRSI extends Overlay
{

	private static final long serialVersionUID = SerialVersion.APPVERSION;

	private OverlayProperties properties;

	private int buyRsiS = 61;
	private int sellRsiS = 39;
	private int buyRsiQ = 61;
	private int sellRsiQ = 39;

	public TwoRSI()
	{
		super();
        properties = new OverlayProperties();
	}

	@Override
	public Overlay newInstance()
	{
		return new TwoRSI();
	}

	@Override
	public String getName()
	{
		return properties.getLabel();
	}

	@Override
	public String getLabel()
	{
		return properties.getLabel();
	}

	@Override
	public Color[] getColors()
	{
		return new Color[0];
	}

	@Override
	public double[] getValues(ChartFrame cf)
	{
		return new double[0];
	}

	@Override
	public double[] getValues(ChartFrame cf, int i)
	{
		return new double[0];
	}

	@Override
	public boolean getMarkerVisibility()
	{
		return false;
	}

	@Override
	public AbstractNode getNode()
	{
		return new OverlayNode(properties);
	}

	@Override
	public String getPrice()
	{
		return Dataset.CLOSE;
	}

	@Override
	public LinkedHashMap getHTML(ChartFrame cf, int i)
	{
		LinkedHashMap ht = new LinkedHashMap();
		ht.put(getLabel(), " ");
		return ht;
	}

	@Override
	public void paint(Graphics2D g, ChartFrame cf, Rectangle bounds)
	{
		Dataset buy = visibleDataset(cf, "BUY");
		Dataset sell = visibleDataset(cf, "SELL");

		if (buy != null && sell != null)
		{
			double barWidth = cf.getChartProperties().getBarWidth();
			Range range = cf.getSplitPanel().getChartPanel().getRange();
			for (int i = 0; i < buy.getItemsCount(); i++)
			{
				double value = 0;
				if (buy.getDataItem(i) != null)
				{
					g.setColor(properties.getBuyColor());
					value = buy.getCloseAt(i);
				}
				if (sell.getDataItem(i) != null)
				{
					g.setColor(properties.getSellColor());
					value = sell.getCloseAt(i);
				}

				double x = cf.getChartData().getX(i, bounds);
				double y = cf.getChartData().getY(value, bounds, range, cf.getChartProperties().getAxisLogarithmicFlag());

				Ellipse2D.Double circle =  new Ellipse2D.Double(x - barWidth/2, y, barWidth, barWidth);
				g.fill(circle);
			}
		}
	}

	@Override
	public void calculate()
	{
		double[] price = getDataset().getCloseValues();
		int count = price.length;

		int sRsiPer = properties.getSlowRsiPeriod(),
			qRsiPer = properties.getQuickRsiPeriod();

		int sMaPer = properties.getSlowMaPeriod(),
			qMaPer = properties.getQuickMaPeriod();

		int maxPer = Math.max(Math.max(sRsiPer, qRsiPer), Math.max(sMaPer, sMaPer));

		// calculate slow and quick RSI
		double[] sRsi = getRsiVector(price, sRsiPer),
			qRsi = getRsiVector(price, qRsiPer);

		// calculate slow and quick MA
		double[] sMa = getMaVector(price, sMaPer),
			qMa = getMaVector(price, qMaPer);

		boolean[] b01 = new boolean[count],
			b02 = new boolean[count],
			b03 = new boolean[count],
			b04 = new boolean[count],
			b05 = new boolean[count],
			b06 = new boolean[count];

		// calculate buy and sell points for slow RSI
		double[] sRSIsf = shift(sRsi, 1);
		for (int i = maxPer; i < count; i++)
		{
			b01[i] = (sRSIsf[i] <= buyRsiS) && (sRsi[i] > buyRsiS) && (price[i] > sMa[i]);
			b02[i] = ((sRSIsf[i] >= sellRsiS) && (sRsi[i] < sellRsiS)) || (price[i] < sMa[i]);
		}
		for (int i = maxPer + 1; i < count - 1; i++)
		{
			if (b01[i] == false && b02[i] == true)
			{
				b03[i] = false;
				b04[i] = true;
			}
			else
			{
				if (b01[i] == true && b02[i] == false)
				{
					b03[i] = true;
					b04[i] = false;
				}
				else
				{
					b03[i] = b03[i - 1];
					b04[i] = b04[i - 1];
				}
			}
		}

		// calculate buy and sell points for quick RSI when slow RSI is in downtrend
		double[] qRSIsf = shift(qRsi, 1);
		for (int i = maxPer; i < count; i++)
		{
			b01[i] = (qRSIsf[i] <= buyRsiQ) && (qRsi[i] > buyRsiQ) && (price[i] > qMa[i]) && b04[i];
			b02[i] = ((qRSIsf[i] >= sellRsiQ) && (qRsi[i] < sellRsiQ)) || (price[i] < qMa[i]);
		}
		for (int i = maxPer + 1; i < count - 1; i++)
		{
			if (b01[i] == false && b02[i] == true)
			{
				b05[i] = false;
				b06[i] = true;
			}
			else
			{
				if (b01[i] == true && b02[i] == false)
				{
					b05[i] = true;
					b06[i] = false;
				}
				else
				{
					b05[i] = b05[i - 1];
					b06[i] = b06[i - 1];
				}
			}
		}

		for (int i = maxPer; i < count; i++)
		{
			// determine the buy signals
			b01[i] = (b03[i] && b05[i]) || (b03[i] && b06[i]) || (b04[i] && b05[i]);
			// determine the sell signals
			b02[i] = (b04[i] && b06[i]);
		}

		boolean[] buy = filterbuy(b01, b02);
		boolean[] sell = filtersell(b01, b02);

		Dataset buyD = Dataset.EMPTY(count);
		Dataset sellD = Dataset.EMPTY(count);
		Dataset initial = getDataset();

		for (int i = maxPer; i < initial.getItemsCount(); i++)
		{
			if (buy[i]) buyD.setDataItem(i, new DataItem(initial.getTimeAt(i), initial.getLowAt(i) * 0.95 ));
			if (sell[i]) sellD.setDataItem(i, new DataItem(initial.getTimeAt(i), initial.getHighAt(i) * 1.05));
		}

		addDataset("BUY", buyD);
		addDataset("SELL", sellD);
	}

	private double[] shift(double[] price, int steps)
	{
		int count = price.length;	
		double[] result = new double[count];
		
		for (int i = steps; i < count; i++)
			result[i] = price[i - steps];
		
		return result;
	}

	private static boolean[] filterbuy(boolean[] b1, boolean[] b2)
	{
		int count = Math.min(b1.length, b2.length);
		boolean[] result = new boolean[count];

		for (int i = 1; i < count - 1; i++)
		{
			boolean res = b1[i];
			boolean buyFlag = false;
			for (int j = i - 1; j >= 0; j--)
			{
				if (b2[j])
					break;
				if (b1[j])
				{
					buyFlag = true;
					break;
				}
			}
			if (buyFlag)
				res = false;
			
			result[i] = res;
		}

		return result;
	}

	private static boolean[] filtersell(boolean[] b1, boolean[] b2)
	{
		int count = Math.min(b1.length, b2.length);
		boolean[] result = new boolean[count];

		for (int i = 1; i < count - 1; i++)
		{
			boolean res = b2[i];
			boolean sellFlag = false;
			for (int j = i - 1; j >= 0; j--)
			{
				if (b1[j])
					break;
				if (b2[j])
				{
					sellFlag = true;
					break;
				}
			}
			if (sellFlag)
				res = false;
			result[i] = res;
		}

		return result;
	}

	private double[] getRsiVector(double[] price, int period)
	{
		double[] vector = new double[price.length];

		for (int i = period; i < price.length; i++)
		{
			double adva = 0;
            double decl = 0;
            double currentRSI;

			for (int j = 0; j < period; j++)
			{
				if (price[i-j] > price[i-j-1])
                {
                    adva += price[i-j] - price[i-j-1];
                }
                else if (price[i-j] < price[i-j-1])
                {
                    decl += price[i-j-1] - price[i-j];
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

			vector[i] = 100 - 100/currentRSI;
		}

		return vector;
	}

	private double[] getMaVector(double[] price, int period)
	{
		double[] vector = new double[price.length];

		for (int i = period - 1; i < price.length; i++)
		{
			double close = 0;

			for (int j = 0; j < period; j++)
			{
				close += price[i - j];
			}

			close /= (double)period;

			vector[i] = close;
		}

		return vector;
	}

}
