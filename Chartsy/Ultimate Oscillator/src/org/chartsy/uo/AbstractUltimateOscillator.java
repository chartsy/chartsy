package org.chartsy.uo;

import org.chartsy.main.chartsy.chart.AbstractIndicator;
import org.chartsy.main.chartsy.chart.Indicator;

/**
 *
 * @author viorel.gheba
 */
public class AbstractUltimateOscillator extends AbstractIndicator {

    public AbstractUltimateOscillator() { super("Ultimate Oscillator", "Description"); }
    public Indicator newInstance() { return new UltimateOscillator(); }

}
