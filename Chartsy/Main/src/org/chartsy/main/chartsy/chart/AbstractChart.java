package org.chartsy.main.chartsy.chart;

import java.awt.Graphics2D;
import java.io.Serializable;
import org.chartsy.main.chartsy.ChartFrame;

/**
 *
 * @author viorel.gheba
 */
public abstract class AbstractChart extends Object implements Serializable {

    private static final long serialVersionUID = 101L;

    private String name;
    private String description;

    public AbstractChart(String n, String desc) {
        name = n;
        description = desc;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public abstract void paint(Graphics2D g, ChartFrame cf);

}
