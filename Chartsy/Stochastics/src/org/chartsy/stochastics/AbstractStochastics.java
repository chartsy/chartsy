package org.chartsy.stochastics;

import org.chartsy.main.chartsy.chart.AbstractIndicator;
import org.chartsy.main.chartsy.chart.Indicator;

/**
 *
 * @author viorel.gheba
 */
public class AbstractStochastics extends AbstractIndicator {

    public AbstractStochastics() { super("Stochastics", "Description"); }
    public Indicator newInstance() { return new Stochastics(); }

}
