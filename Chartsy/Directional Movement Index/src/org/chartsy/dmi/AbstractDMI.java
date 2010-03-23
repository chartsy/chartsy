package org.chartsy.dmi;

import org.chartsy.main.chartsy.chart.AbstractIndicator;
import org.chartsy.main.chartsy.chart.Indicator;

/**
 *
 * @author viorel.gheba
 */
public class AbstractDMI extends AbstractIndicator {

    public AbstractDMI() { super("DMI", "Description"); }
    public Indicator newInstance() { return new DMI(); }

}
