package org.chartsy.main.events;

import java.util.EventListener;

/**
 *
 * @author Viorel
 */
public interface DataProviderListener extends EventListener
{

	public void triggerDataProviderListener(DataProviderEvent evt);

}
