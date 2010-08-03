package org.chartsy.main.utils;

/**
 *
 * @author Viorel
 */
public enum Price
{

	OPEN("Open"),
	CLOSE("Close"),
	HIGH("High"),
	LOW("Low"),
	VOLUME("Volume");

	private final String price;

	Price(String price)
	{
		this.price = price;
	}

	public String getPrice()
	{
		return this.price;
	}

	public String toString()
	{
		return this.price;
	}

	public static Price getPrice(String name)
	{
		if (name.equals(OPEN.getPrice()))
			return OPEN;
		if (name.equals(CLOSE.getPrice()))
			return CLOSE;
		if (name.equals(HIGH.getPrice()))
			return HIGH;
		if (name.equals(LOW.getPrice()))
			return LOW;
		if (name.equals(VOLUME.getPrice()))
			return VOLUME;
		return null;
	}

	public static Price[] getPrices()
	{
		return new Price[]
		{ OPEN, CLOSE, HIGH, LOW };
	}

	public static String[] getPriceList()
	{
		return new String[]
		{
			OPEN.getPrice(),
			CLOSE.getPrice(),
			HIGH.getPrice(),
			LOW.getPrice()
		};
	}

}
