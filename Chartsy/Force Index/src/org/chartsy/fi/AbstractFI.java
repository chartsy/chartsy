package org.chartsy.fi;

import org.chartsy.main.chartsy.chart.AbstractIndicator;
import org.chartsy.main.chartsy.chart.Indicator;

/**
 *
 * @author viorel.gheba
 */
public class AbstractFI extends AbstractIndicator {

    public AbstractFI() { super("FI", "Description"); }
    public Indicator newInstance() { return new FI(); }

}
