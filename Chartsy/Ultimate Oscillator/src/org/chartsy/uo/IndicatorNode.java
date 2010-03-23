package org.chartsy.uo;

import java.awt.Color;
import java.awt.Stroke;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.InvocationTargetException;
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

    private static final long serialVersionUID = 101L;

    public IndicatorNode() {
        super(Children.LEAF);
        setDisplayName("Ultimate Oscillator Properties");
    }

    public IndicatorNode(IndicatorProperties indicatorProperties) {
        super(Children.LEAF, Lookups.singleton(indicatorProperties));
        setDisplayName("Ultimate Oscillator Properties");
        indicatorProperties.addPropertyChangeListener(this);
    }
    
    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();

        Sheet.Set set = Sheet.createPropertiesSet();
        final IndicatorProperties indicatorProperties = getLookup().lookup(IndicatorProperties.class);

        try {
            // Fast
            @SuppressWarnings(value = "unchecked")
            Property fast = new PropertySupport.Reflection(indicatorProperties, int.class, "getFast", "setFast") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(IndicatorProperties.FAST); }
                public boolean supportsDefaultValue() { return true; }
            };
            fast.setName("Fast");
            set.put(fast);

            // Intermediate
            @SuppressWarnings(value = "unchecked")
            Property intermediate = new PropertySupport.Reflection(indicatorProperties, int.class, "getIntermediate", "setIntermediate") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(IndicatorProperties.INTERMEDIATE); }
                public boolean supportsDefaultValue() { return true; }
            };
            intermediate.setName("Intermediate");
            set.put(intermediate);

            // Slow
            @SuppressWarnings(value = "unchecked")
            Property slow = new PropertySupport.Reflection(indicatorProperties, int.class, "getSlow", "setSlow") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(IndicatorProperties.SLOW); }
                public boolean supportsDefaultValue() { return true; }
            };
            slow.setName("Slow");
            set.put(slow);

            // Label
            @SuppressWarnings(value = "unchecked")
            Property label = new PropertySupport.Reflection(indicatorProperties, String.class, "getLabel", "setLabel") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(IndicatorProperties.LABEL); }
                public boolean supportsDefaultValue() { return true; }
            };
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
            color.setName("Color");
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
        } catch (Exception e) {}

        sheet.put(set);

        return sheet;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        firePropertySetsChange(null, getPropertySets());
    }

    public void writeExternal(ObjectOutput out) throws IOException {}

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {}

}
