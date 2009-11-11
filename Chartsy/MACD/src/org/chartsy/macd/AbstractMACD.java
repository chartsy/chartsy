package org.chartsy.macd;

import org.chartsy.main.chartsy.chart.AbstractIndicator;
import org.chartsy.main.chartsy.chart.Indicator;

/**
 *
 * @author viorel.gheba
 */
public class AbstractMACD extends AbstractIndicator {
    
    public AbstractMACD() { super("MACD", "Description"); }
    public Indicator newInstance() { return new MACD(); }

}
