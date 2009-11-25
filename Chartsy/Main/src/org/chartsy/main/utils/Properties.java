package org.chartsy.main.utils;

import java.io.Serializable;

/**
 *
 * @author viorel.gheba
 */
public class Properties implements Serializable {

    private static final long serialVersionUID = 101L;

    private PropertyItem[] data;

    public Properties(PropertyItem[] data) { this.data = data; }
    public int getItems() { return data.length; }
    public PropertyItem[] getPropertyItems() { return data; }
    public PropertyItem getPropertyItem(int index) { return data[index]; }
    public String getName(int index) { return data[index].getName(); }
    public String getComponent(int index) { return data[index].getComponent(); }
    public String[] getList(int index) { return data[index].getList(); }
    public Object getValue(int index) { return data[index].getValue(); }
    public Object getValue(String name) {
        for (PropertyItem p : data) {
            if (p.getName().equals(name)) {
                return p.getValue();
            }
        }
        return null;
    }
    public void setValue(int index, Object value) { data[index].setValue(value); }

}
