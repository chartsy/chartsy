package org.chartsy.main.utils;

/**
 *
 * @author viorel.gheba
 */
public class PropertyItem {

    private String name;
    private String component;
    private String[] list;
    private Object value;

    public PropertyItem(String name, String component, Object value) {
        this(name, component, null, value);
    }

    public PropertyItem(String name, String component, String[] list, Object value) {
        this.name = name;
        this.component = component;
        this.list = list;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public String getComponent() {
        return this.component;
    }

    public String[] getList() {
        return this.list;
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

}
