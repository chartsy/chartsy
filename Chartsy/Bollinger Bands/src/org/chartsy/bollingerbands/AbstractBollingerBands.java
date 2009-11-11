package org.chartsy.bollingerbands;

import org.chartsy.main.chartsy.chart.AbstractOverlay;
import org.chartsy.main.chartsy.chart.Overlay;

/**
 *
 * @author viorel.gheba
 */
public class AbstractBollingerBands extends AbstractOverlay {

    public AbstractBollingerBands() { super("Bollinger Bands", "Description"); }
    public Overlay newInstance() { return new BollingerBands(); }

}
