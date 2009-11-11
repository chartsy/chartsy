package org.chartsy.annotation.line;

import org.chartsy.main.chartsy.ChartFrame;
import org.chartsy.main.chartsy.chart.AbstractAnnotation;
import org.chartsy.main.chartsy.chart.Annotation;

/**
 *
 * @author Viorel
 */
public class AbstractLine extends AbstractAnnotation {
    
    public AbstractLine() { super("Line"); }
    public Annotation newInstance(ChartFrame cf) { return new Line(cf); }

}
