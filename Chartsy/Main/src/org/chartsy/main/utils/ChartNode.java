package org.chartsy.main.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.InvocationTargetException;
import org.chartsy.main.chartsy.ChartProperties;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author viorel.gheba
 */
public class ChartNode extends AbstractNode implements PropertyChangeListener, Externalizable {

    private static final long serialVersionUID = 101L;

    public ChartNode() {
        super(Children.LEAF);
        setDisplayName("Chart Properties");
    }

    public ChartNode(ChartProperties chartProperties) {
        super(Children.LEAF, Lookups.singleton(chartProperties));
        setDisplayName("Chart Properties");
        chartProperties.addPropertyChangeListener(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();

        Sheet.Set set = Sheet.createPropertiesSet();

        Sheet.Set axis = Sheet.createPropertiesSet();
        axis.setName("Axis Properties");
        axis.setDisplayName("Axis Properties");
        
        Sheet.Set data = Sheet.createPropertiesSet();
        data.setName("Data Properties");
        data.setDisplayName("Data Properties");

        Sheet.Set grid = Sheet.createPropertiesSet();
        grid.setName("Grid Properties");
        grid.setDisplayName("Grid Properties");

        final ChartProperties chartProperties = getLookup().lookup(ChartProperties.class);

        try {
            // Background Color
            @SuppressWarnings(value = "unchecked")
            Property backgroundColor = new PropertySupport.Reflection(chartProperties, Color.class, "getBackgroundColor", "setBackgroundColor") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(DefaultTheme.BACKGROUND_COLOR); }
                public boolean supportsDefaultValue() { return true; }
            };

            // Font
            @SuppressWarnings(value = "unchecked")
            Property font = new PropertySupport.Reflection(chartProperties, Font.class, "getFont", "setFont") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(DefaultTheme.FONT); }
                public boolean supportsDefaultValue() { return true; }
            };

            // Font Color
            @SuppressWarnings(value = "unchecked")
            Property fontColor = new PropertySupport.Reflection(chartProperties, Color.class, "getFontColor", "setFontColor") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(DefaultTheme.FONT_COLOR); }
                public boolean supportsDefaultValue() { return true; }
            };

            backgroundColor.setName("Background Color");
            font.setName("Font");
            fontColor.setName("Font Color");

            // Axis Color
            @SuppressWarnings(value = "unchecked")
            Property axisColor = new PropertySupport.Reflection(chartProperties, Color.class, "getAxisColor", "setAxisColor") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(DefaultTheme.AXIS_COLOR); }
                public boolean supportsDefaultValue() { return true; }
            };

            // Axis Stroke
            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection axisStroke = new PropertySupport.Reflection(chartProperties, Stroke.class, "getAxisStroke", "setAxisStroke") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(StrokeGenerator.DEFAULT_STROKE); }
                public boolean supportsDefaultValue() { return true; }
            };
            axisStroke.setPropertyEditorClass(StrokePropertyEditor.class);

            axisColor.setName("Axis Color");
            axisStroke.setName("Axis Style");

            // Bar Color
            @SuppressWarnings(value = "unchecked")
            Property barColor = new PropertySupport.Reflection(chartProperties, Color.class, "getBarColor", "setBarColor") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(DefaultTheme.AXIS_COLOR); }
                public boolean supportsDefaultValue() { return true; }
            };

            // Bar Stroke
            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection barStroke = new PropertySupport.Reflection(chartProperties, Stroke.class, "getBarStroke", "setBarStroke") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(StrokeGenerator.DEFAULT_STROKE); }
                public boolean supportsDefaultValue() { return true; }
            };
            barStroke.setPropertyEditorClass(StrokePropertyEditor.class);

            // Bar Visibility
            @SuppressWarnings(value = "unchecked")
            Property barVisibility = new PropertySupport.Reflection(chartProperties, boolean.class, "getBarVisibility", "setBarVisibility") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(DefaultTheme.BAR_VISIBILITY); }
                public boolean supportsDefaultValue() { return true; }
            };

            // Up Bar Color
            @SuppressWarnings(value = "unchecked")
            Property barUpColor = new PropertySupport.Reflection(chartProperties, Color.class, "getBarUpColor", "setBarUpColor") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(DefaultTheme.BAR_UP_COLOR); }
                public boolean supportsDefaultValue() { return true; }
            };

            // Up Bar Visibility
            @SuppressWarnings(value = "unchecked")
            Property barUpVisibility = new PropertySupport.Reflection(chartProperties, boolean.class, "getBarUpVisibility", "setBarUpVisibility") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(DefaultTheme.BAR_UP_VISIBILITY); }
                public boolean supportsDefaultValue() { return true; }
            };

            // Down Bar Color
            @SuppressWarnings(value = "unchecked")
            Property barDownColor = new PropertySupport.Reflection(chartProperties, Color.class, "getBarDownColor", "setBarDownColor") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(DefaultTheme.BAR_DOWN_COLOR); }
                public boolean supportsDefaultValue() { return true; }
            };

            // Down Bar Visibility
            @SuppressWarnings(value = "unchecked")
            Property barDownVisibility = new PropertySupport.Reflection(chartProperties, boolean.class, "getBarDownVisibility", "setBarDownVisibility") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(DefaultTheme.BAR_DOWN_VISIBILITY); }
                public boolean supportsDefaultValue() { return true; }
            };

            barColor.setName("Bar Color");
            barStroke.setName("Bar Style");
            barVisibility.setName("Bar Visibility");
            barUpColor.setName("Bar Up Color");
            barUpVisibility.setName("Bar Up Visibility");
            barDownColor.setName("Bar Down Color");
            barDownVisibility.setName("Bar Down Visibility");

            // Horizontal Grid Color
            @SuppressWarnings(value = "unchecked")
            Property horizontalColor = new PropertySupport.Reflection(chartProperties, Color.class, "getGridHorizontalColor", "setGridHorizontalColor") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(DefaultTheme.HORIZONTAL_GRID_COLOR); }
                public boolean supportsDefaultValue() { return true; }
            };

            // Vertical Grid Color
            @SuppressWarnings(value = "unchecked")
            Property verticalColor = new PropertySupport.Reflection(chartProperties, Color.class, "getGridVerticalColor", "setGridVerticalColor") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(DefaultTheme.VERTICAL_GRID_COLOR); }
                public boolean supportsDefaultValue() { return true; }
            };

            // Horizontal Grid Stroke
            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection horizontalStroke = new PropertySupport.Reflection(chartProperties, Stroke.class, "getGridHorizontalStroke", "setGridHorizontalStroke") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(StrokeGenerator.DEFAULT_STROKE); }
                public boolean supportsDefaultValue() { return true; }
            };
            horizontalStroke.setPropertyEditorClass(StrokePropertyEditor.class);

            // Vertical Grid Stroke
            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection verticalStroke = new PropertySupport.Reflection(chartProperties, Stroke.class, "getGridVerticalStroke", "setGridVerticalStroke") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(StrokeGenerator.DEFAULT_STROKE); }
                public boolean supportsDefaultValue() { return true; }
            };
            verticalStroke.setPropertyEditorClass(StrokePropertyEditor.class);

            // Horizontal Grid Visibility
            @SuppressWarnings(value = "unchecked")
            Property horizontalVisibility = new PropertySupport.Reflection(chartProperties, boolean.class, "getGridHorizontalVisibility", "setGridHorizontalVisibility") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(DefaultTheme.HORIZONTAL_GRID_VISIBILITY); }
                public boolean supportsDefaultValue() { return true; }
            };

            // Vertical Grid Visibility
            @SuppressWarnings(value = "unchecked")
            Property verticalVisibility = new PropertySupport.Reflection(chartProperties, boolean.class, "getGridVerticalVisibility", "setGridVerticalVisibility") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(DefaultTheme.VERTICAL_GRID_VISIBILITY); }
                public boolean supportsDefaultValue() { return true; }
            };

            horizontalColor.setName("Horizontal Color");
            verticalColor.setName("Vertical Color");
            horizontalStroke.setName("Horizontal Style");
            verticalStroke.setName("Vertical Style");
            horizontalVisibility.setName("Horizontal Visibility");
            verticalVisibility.setName("Vertical Visibility");

            set.put(backgroundColor);
            set.put(font);
            set.put(fontColor);

            axis.put(axisColor);
            axis.put(axisStroke);

            data.put(barColor);
            data.put(barStroke);
            data.put(barVisibility);
            data.put(barUpColor);
            data.put(barDownColor);
            data.put(barUpVisibility);
            data.put(barDownVisibility);

            grid.put(horizontalColor);
            grid.put(verticalColor);
            grid.put(horizontalStroke);
            grid.put(verticalStroke);
            grid.put(horizontalVisibility);
            grid.put(verticalVisibility);
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        sheet.put(set);
        sheet.put(axis);
        sheet.put(data);
        sheet.put(grid);

        return sheet;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        firePropertySetsChange(null, getPropertySets());
    }

    public void writeExternal(ObjectOutput out) throws IOException {}
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {}

}
