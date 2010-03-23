package org.chartsy.mfi;

import org.chartsy.main.chartsy.chart.AbstractIndicator;
import org.chartsy.main.chartsy.chart.Indicator;

/**
 *
 * @author viorel.gheba
 */
public class AbstractMFI extends AbstractIndicator {

    public AbstractMFI() { super("MFI", "Description"); }
    public Indicator newInstance() { return new MFI(); }

}