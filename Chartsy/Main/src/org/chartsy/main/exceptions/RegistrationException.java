package org.chartsy.main.exceptions;

/**
 *
 * @author Viorel
 */
public class RegistrationException extends Exception
{
	
	private String message;

	public RegistrationException(String message)
	{
		super(message);
		this.message = message;
	}

	@Override public String toString()
	{
		return message;
	}

}
