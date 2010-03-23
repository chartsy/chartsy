package org.chartsy.zigzag;

import org.chartsy.main.chartsy.chart.AbstractOverlay;
import org.chartsy.main.chartsy.chart.Overlay;

/**
 *
 * @author viorel.gheba
 */
public class AbstractZigZag extends AbstractOverlay {

    public AbstractZigZag() { super("ZigZag", "Description"); }
    public Overlay newInstance() { return new ZigZag(); }

}
