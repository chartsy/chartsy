package org.chartsy.main.chartsy.chart;

import org.chartsy.main.chartsy.ChartFrame;

/**
 *
 * @author viorel.gheba
 */
public abstract class AbstractAnnotation extends Object {

    private String name;

    public AbstractAnnotation(String n) { name = n; }
    public String getName() { return name; }
    public abstract Annotation newInstance(ChartFrame cf);

}
