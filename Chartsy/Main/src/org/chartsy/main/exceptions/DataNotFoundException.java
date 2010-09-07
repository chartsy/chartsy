package org.chartsy.main.exceptions;

/**
 *
 * @author Viorel
 */
public class DataNotFoundException extends Exception
{

	private String message;

	public DataNotFoundException(String message)
	{
		super(message);
		this.message = message;
	}

	@Override public String toString()
	{
		return this.message;
	}

}
