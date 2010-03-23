package org.chartsy.cci;

import org.chartsy.main.chartsy.chart.AbstractIndicator;
import org.chartsy.main.chartsy.chart.Indicator;

/**
 *
 * @author viorel.gheba
 */
public class AbstractCCI extends AbstractIndicator {

    public AbstractCCI() { super("CCI", "Description"); }
    public Indicator newInstance() { return new CCI(); }

}
