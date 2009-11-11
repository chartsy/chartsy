package org.chartsy.volume;

import org.chartsy.main.chartsy.chart.AbstractIndicator;
import org.chartsy.main.chartsy.chart.Indicator;

/**
 *
 * @author viorel.gheba
 */
public class AbstractVolume extends AbstractIndicator {
    
    public AbstractVolume() { super("Volume", "Description"); }
    public Indicator newInstance() { return new Volume(); }

}
