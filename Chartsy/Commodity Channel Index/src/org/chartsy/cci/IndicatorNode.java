package org.chartsy.cci;

import java.awt.Color;
import java.awt.Stroke;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditorSupport;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.InvocationTargetException;
import org.chartsy.main.utils.AlphaPropertyEditor;
import org.chartsy.main.utils.StrokeGenerator;
import org.chartsy.main.utils.StrokePropertyEditor;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author viorel.gheba
 */
public class IndicatorNode extends AbstractNode implements PropertyChangeListener, Externalizable {

    private static final long serialVersionUID = 2L;

    public IndicatorNode() {
        super(Children.LEAF);
        setDisplayName("CCI Properties");
    }

    public IndicatorNode(IndicatorProperties indicatorProperties) {
        super(Children.LEAF, Lookups.singleton(indicatorProperties));
        setDisplayName("CCI Properties");
        indicatorProperties.addPropertyChangeListener(this);
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();

        Sheet.Set set = Sheet.createPropertiesSet();
        final IndicatorProperties indicatorProperties = getLookup().lookup(IndicatorProperties.class);

        try {
            // Period
            @SuppressWarnings(value = "unchecked")
            Property period = new PropertySupport.Reflection(indicatorProperties, int.class, "getPeriod", "setPeriod") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(IndicatorProperties.PERIOD); }
                public boolean supportsDefaultValue() { return true; }
            };
            period.setName("Period");
            set.put(period);

            // Label
            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection label = new PropertySupport.Reflection(indicatorProperties, String.class, "getLabel", "setLabel") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(IndicatorProperties.LABEL); }
                public boolean supportsDefaultValue() { return true; }
            };
            label.setPropertyEditorClass(PropertyEditorSupport.class);
            label.setName("Label");
            set.put(label);

            // Marker
            @SuppressWarnings(value = "unchecked")
            Property marker = new PropertySupport.Reflection(indicatorProperties, boolean.class, "getMarker", "setMarker") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(IndicatorProperties.MARKER); }
                public boolean supportsDefaultValue() { return true; }
            };
            marker.setName("Marker");
            set.put(marker);

            // Color
            @SuppressWarnings(value = "unchecked")
            Property color = new PropertySupport.Reflection(indicatorProperties, Color.class, "getColor", "setColor") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(IndicatorProperties.COLOR); }
                public boolean supportsDefaultValue() { return true; }
            };
            color.setName("Signal Color");
            set.put(color);

            // Stroke
            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection stroke = new PropertySupport.Reflection(indicatorProperties, Stroke.class, "getStroke", "setStroke") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(StrokeGenerator.getStroke(IndicatorProperties.STROKE_INDEX)); }
                public boolean supportsDefaultValue() { return true; }
            };
            stroke.setPropertyEditorClass(StrokePropertyEditor.class);
            stroke.setName("Line Style");
            set.put(stroke);

            // Inside Color
            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection insideColor = new PropertySupport.Reflection(indicatorProperties, Color.class, "getInsideColor", "setInsideColor") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(IndicatorProperties.INSIDE_COLOR); }
                public boolean supportsDefaultValue() { return true; }
            };
            insideColor.setName("Inside Color");
            set.put(insideColor);

            // Inside Alpha
            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection insideAlpha = new PropertySupport.Reflection(indicatorProperties, int.class, "getInsideAlpha", "setInsideAlpha") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(IndicatorProperties.INSIDE_ALPHA); }
                public boolean supportsDefaultValue() { return true; }
            };
            insideAlpha.setPropertyEditorClass(AlphaPropertyEditor.class);
            insideAlpha.setName("Inside Opacity");
            set.put(insideAlpha);

            // Inside Visibility
            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection insideVisibility = new PropertySupport.Reflection(indicatorProperties, boolean.class, "getInsideVisibility", "setInsideVisibility") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(IndicatorProperties.INSIDE_VISIBILITY); }
                public boolean supportsDefaultValue() { return true; }
            };
            insideVisibility.setName("Inside Visibility");
            set.put(insideVisibility);

            @SuppressWarnings(value = "unchecked")
            Property zeroLineColor = new PropertySupport.Reflection(indicatorProperties, Color.class, "getZeroLineColor", "setZeroLineColor") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(IndicatorProperties.ZERO_LINE_COLOR); }
                public boolean supportsDefaultValue() { return true; }
            };
            zeroLineColor.setName("Zero Line Color");
            set.put(zeroLineColor);

            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection zeroLineStroke = new PropertySupport.Reflection(indicatorProperties, Stroke.class, "getZeroLineStroke", "setZeroLineStroke") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(StrokeGenerator.getStroke(IndicatorProperties.ZERO_LINE_STROKE)); }
                public boolean supportsDefaultValue() { return true; }
            };
            zeroLineStroke.setPropertyEditorClass(StrokePropertyEditor.class);
            zeroLineStroke.setName("Zero Line Style");
            set.put(zeroLineStroke);

            @SuppressWarnings(value = "unchecked")
            Property zeroLineVisibility = new PropertySupport.Reflection(indicatorProperties, boolean.class, "getZeroLineVisibility", "setZeroLineVisibility") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(IndicatorProperties.ZERO_LINE_VISIBILITY); }
                public boolean supportsDefaultValue() { return true; }
            };
            zeroLineVisibility.setName("Zero Line Visibility");
            set.put(zeroLineVisibility);
        } catch (Exception e) {
            e.printStackTrace();
        }

        sheet.put(set);

        return sheet;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        firePropertySetsChange(null, getPropertySets());
    }

    public void writeExternal(ObjectOutput out) throws IOException {}

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {}

}
