package org.chartsy.annotation.fibonacciextension;

import org.chartsy.main.chartsy.ChartFrame;
import org.chartsy.main.chartsy.chart.AbstractAnnotation;
import org.chartsy.main.chartsy.chart.Annotation;

/**
 *
 * @author viorel.gheba
 */
public class AbstractFibonacciExtension extends AbstractAnnotation {
    
    public AbstractFibonacciExtension() { super("Fibonacci Extension"); }
    public Annotation newInstance(ChartFrame cf) { return new FibonacciExtension(cf); }

}
