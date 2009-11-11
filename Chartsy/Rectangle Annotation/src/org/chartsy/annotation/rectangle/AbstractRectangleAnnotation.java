package org.chartsy.annotation.rectangle;

import org.chartsy.main.chartsy.ChartFrame;
import org.chartsy.main.chartsy.chart.AbstractAnnotation;
import org.chartsy.main.chartsy.chart.Annotation;

/**
 *
 * @author viorel.gheba
 */
public class AbstractRectangleAnnotation extends AbstractAnnotation {
    
    public AbstractRectangleAnnotation() { super("Rectangle"); }
    public Annotation newInstance(ChartFrame cf) { return new RectangleAnnotation(cf); }

}
