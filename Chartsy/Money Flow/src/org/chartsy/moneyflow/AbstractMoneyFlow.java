package org.chartsy.moneyflow;

import org.chartsy.main.chartsy.chart.AbstractIndicator;
import org.chartsy.main.chartsy.chart.Indicator;

/**
 *
 * @author viorel.gheba
 */
public class AbstractMoneyFlow extends AbstractIndicator {

    public AbstractMoneyFlow() { super("Money Flow", "Description"); }
    public Indicator newInstance() { return new MoneyFlow(); }

}
