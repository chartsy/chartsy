package org.chartsy.main.exceptions;

/**
 *
 * @author Viorel
 */
public class StockNotFoundException extends Exception
{

	public StockNotFoundException()
	{
		super("Stock not found.");
	}


	@Override public String toString()
	{
		return "Stock not found.";
	}

}
