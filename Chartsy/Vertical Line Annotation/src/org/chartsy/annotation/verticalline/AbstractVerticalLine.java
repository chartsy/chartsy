package org.chartsy.annotation.verticalline;

import org.chartsy.main.chartsy.ChartFrame;
import org.chartsy.main.chartsy.chart.AbstractAnnotation;
import org.chartsy.main.chartsy.chart.Annotation;

/**
 *
 * @author viorel.gheba
 */
public class AbstractVerticalLine extends AbstractAnnotation {

    public AbstractVerticalLine() { super("Vertical Line"); }
    public Annotation newInstance(ChartFrame cf) { return new VerticalLine(cf); }

}
