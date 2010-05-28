package org.chartsy.main.events;

import java.util.EventListener;

/**
 *
 * @author viorel.gheba
 */
public interface LogListener extends EventListener {

    public void fire(LogEvent evt);

}
