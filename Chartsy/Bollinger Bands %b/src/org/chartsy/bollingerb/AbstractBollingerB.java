package org.chartsy.bollingerb;

import org.chartsy.main.chartsy.chart.AbstractIndicator;
import org.chartsy.main.chartsy.chart.Indicator;

/**
 *
 * @author viorel.gheba
 */
public class AbstractBollingerB extends AbstractIndicator {

    public AbstractBollingerB() { super("SVE_BB%b", "Description"); }
    public Indicator newInstance() { return new BollingerB(); }

}
