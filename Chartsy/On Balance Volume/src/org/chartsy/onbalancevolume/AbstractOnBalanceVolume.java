package org.chartsy.onbalancevolume;

import org.chartsy.main.chartsy.chart.AbstractIndicator;
import org.chartsy.main.chartsy.chart.Indicator;

/**
 *
 * @author viorel.gheba
 */
public class AbstractOnBalanceVolume extends AbstractIndicator {

    public AbstractOnBalanceVolume() { super("OBV", "Description"); }
    public Indicator newInstance() { return new OnBalanceVolume(); }

}
