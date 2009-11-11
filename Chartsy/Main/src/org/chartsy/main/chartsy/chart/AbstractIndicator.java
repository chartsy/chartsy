package org.chartsy.main.chartsy.chart;

/**
 *
 * @author viorel.gheba
 */
public abstract class AbstractIndicator extends Object {
    
    private String name;
    private String description;

    public AbstractIndicator(String n, String desc) {
        name = n;
        description = desc;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public abstract Indicator newInstance();

}
