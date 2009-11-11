package org.chartsy.annotation.arrowline;

import org.chartsy.main.chartsy.ChartFrame;
import org.chartsy.main.chartsy.chart.AbstractAnnotation;
import org.chartsy.main.chartsy.chart.Annotation;

/**
 *
 * @author viorel.gheba
 */
public class AbstractArrowLine extends AbstractAnnotation {
    
    public AbstractArrowLine() { super("Arrow Line"); }
    public Annotation newInstance(ChartFrame cf) { return new ArrowLine(cf); }

}
