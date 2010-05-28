package org.chartsy.main.chart;

import java.awt.Graphics2D;
import java.io.Serializable;
import org.chartsy.main.ChartFrame;

/**
 *
 * @author viorel.gheba
 */
public abstract class Chart implements Serializable {

    private static final long serialVersionUID = 2L;

    public Chart() {}

    public abstract String getName();
    public abstract void paint(Graphics2D g, ChartFrame cf);

}
