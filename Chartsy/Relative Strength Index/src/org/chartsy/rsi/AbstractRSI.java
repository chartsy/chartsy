package org.chartsy.rsi;

import org.chartsy.main.chartsy.chart.AbstractIndicator;
import org.chartsy.main.chartsy.chart.Indicator;

/**
 *
 * @author viorel.gheba
 */
public class AbstractRSI extends AbstractIndicator {

    public AbstractRSI() { super("RSI", "Description"); }
    public Indicator newInstance() { return new RSI(); }

}
