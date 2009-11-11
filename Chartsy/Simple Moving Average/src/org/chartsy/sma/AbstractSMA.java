package org.chartsy.sma;

import org.chartsy.main.chartsy.chart.AbstractOverlay;
import org.chartsy.main.chartsy.chart.Overlay;

/**
 *
 * @author viorel.gheba
 */
public class AbstractSMA extends AbstractOverlay {

    public AbstractSMA() { super("SMA", "Description"); }
    public Overlay newInstance() { return new SMA(); }

}
