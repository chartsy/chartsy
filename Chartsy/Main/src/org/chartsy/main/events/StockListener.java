package org.chartsy.main.events;

import java.util.EventListener;

/**
 *
 * @author viorel.gheba
 */
public interface StockListener extends EventListener {

    public void stockChanged(StockEvent evt);

}
