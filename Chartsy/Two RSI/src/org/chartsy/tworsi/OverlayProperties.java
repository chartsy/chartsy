package org.chartsy.tworsi;

import java.awt.Color;
import org.chartsy.main.chart.AbstractPropertyListener;
import org.chartsy.main.utils.SerialVersion;

/**
 *
 * @author Viorel
 */
public class OverlayProperties extends AbstractPropertyListener
{

	private static final long serialVersionUID = SerialVersion.APPVERSION;

    public static final String LABEL = "Two RSI";
    public static final Color BUY_COLOR =  new Color(0x73d216);
	public static final Color SELL_COLOR =  new Color(0xef2929);
	public static final int S_RSI_PERIOD = 17;
	public static final int Q_RSI_PERIOD = 5;
	public static final int S_MA_PERIOD = 40;
	public static final int Q_MA_PERIOD = 10;

	private String label = LABEL;
	private Color buyColor = BUY_COLOR;
	private Color sellColor = SELL_COLOR;
	private int slowRsiPeriod = S_RSI_PERIOD;
	private int quickRsiPeriod = Q_RSI_PERIOD;
	private int slowMaPeriod = S_MA_PERIOD;
	private int quickMaPeriod = Q_MA_PERIOD;

	public OverlayProperties()
	{
	}

	public String getLabel()
	{
		return label;
	}

	public void setLabel(String s)
	{
		label = s;
	}

	public Color getBuyColor()
	{
		return buyColor;
	}

	public void setBuyColor(Color c)
	{
		buyColor = c;
	}

	public Color getSellColor()
	{
		return sellColor;
	}

	public void setSellColor(Color c)
	{
		sellColor = c;
	}

	public int getSlowRsiPeriod()
	{
		return slowRsiPeriod;
	}

	public void setSlowRsiPeriod(int i)
	{
		slowRsiPeriod = i;
	}

	public int getQuickRsiPeriod()
	{
		return quickRsiPeriod;
	}

	public void setQuickRsiPeriod(int i)
	{
		quickRsiPeriod = i;
	}

	public int getSlowMaPeriod()
	{
		return slowMaPeriod;
	}

	public void setSlowMaPeriod(int i)
	{
		slowMaPeriod = i;
	}

	public int getQuickMaPeriod()
	{
		return quickMaPeriod;
	}

	public void setQuickMaPeriod(int i)
	{
		quickMaPeriod = i;
	}

}
