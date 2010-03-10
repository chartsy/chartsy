package org.chartsy.tema;

import org.chartsy.main.chartsy.chart.AbstractOverlay;
import org.chartsy.main.chartsy.chart.Overlay;

/**
 *
 * @author viorel.gheba
 */
public class AbstractTEMA extends AbstractOverlay {

    public AbstractTEMA() { super("TEMA", "Description"); }
    public Overlay newInstance() { return new TEMA(); }

}
