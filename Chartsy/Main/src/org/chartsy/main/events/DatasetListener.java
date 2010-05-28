package org.chartsy.main.events;

import java.util.EventListener;

/**
 *
 * @author viorel.gheba
 */
public interface DatasetListener 
        extends EventListener
{

    public void datasetChanged(DatasetEvent evt);

}
