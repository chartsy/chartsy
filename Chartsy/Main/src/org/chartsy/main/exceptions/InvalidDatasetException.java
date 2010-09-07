package org.chartsy.main.exceptions;

/**
 *
 * @author Viorel
 */
public class InvalidDatasetException extends Exception
{

	private String message;

	public InvalidDatasetException(String message)
	{
		super(message);
		this.message = message;
	}

	@Override public String toString()
	{
		return this.message;
	}

}
