package org.chartsy.main.exceptions;

/**
 *
 * @author Viorel
 */
public class InvalidStockException extends Exception
{

	public InvalidStockException()
	{
		super("The symbol is invalid. Please enter a valid symbol.");
	}

	@Override public String toString()
	{
		return "The symbol is invalid. Please enter a valid symbol.";
	}

}
