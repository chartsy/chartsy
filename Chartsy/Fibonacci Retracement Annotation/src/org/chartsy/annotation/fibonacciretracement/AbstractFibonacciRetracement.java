package org.chartsy.annotation.fibonacciretracement;

import org.chartsy.main.chartsy.ChartFrame;
import org.chartsy.main.chartsy.chart.AbstractAnnotation;
import org.chartsy.main.chartsy.chart.Annotation;

/**
 *
 * @author viorel.gheba
 */
public class AbstractFibonacciRetracement extends AbstractAnnotation {
    
    public AbstractFibonacciRetracement() { super("Fibonacci Retracement"); }
    public Annotation newInstance(ChartFrame cf) { return new FibonacciRetracement(cf); }

}
