package org.chartsy.annotation.horizontalline;

import org.chartsy.main.chartsy.ChartFrame;
import org.chartsy.main.chartsy.chart.AbstractAnnotation;
import org.chartsy.main.chartsy.chart.Annotation;

/**
 *
 * @author viorel.gheba
 */
public class AbstractHorizontalLine extends AbstractAnnotation {

    public AbstractHorizontalLine() { super("Horizontal Line"); }
    public Annotation newInstance(ChartFrame cf) { return new HorizontalLine(cf); }

}
