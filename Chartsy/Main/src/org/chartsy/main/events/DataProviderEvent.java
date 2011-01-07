package org.chartsy.main.events;

import java.util.EventObject;

/**
 *
 * @author Viorel
 */
public class DataProviderEvent extends EventObject
{

	private int itemsAdded;

	public DataProviderEvent(Object source, int itemsAdded)
	{
		super(source);
		this.itemsAdded = itemsAdded;
	}

	public int getItemsAdded()
	{
		return this.itemsAdded;
	}

}
