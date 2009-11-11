package org.chartsy.main.chartsy.chart;

/**
 *
 * @author viorel.gheba
 */
public abstract class AbstractOverlay extends Object {

    private String name;
    private String description;

    public AbstractOverlay(String n, String desc) {
        name = n;
        description = desc;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public abstract Overlay newInstance();

}
