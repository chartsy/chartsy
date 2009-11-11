package org.chartsy.ema;

import org.chartsy.main.chartsy.chart.AbstractOverlay;
import org.chartsy.main.chartsy.chart.Overlay;

/**
 *
 * @author viorel.gheba
 */
public class AbstractEMA extends AbstractOverlay {
    
    public AbstractEMA() { super("EMA", "Description"); }
    public Overlay newInstance() { return new EMA(); }

}
