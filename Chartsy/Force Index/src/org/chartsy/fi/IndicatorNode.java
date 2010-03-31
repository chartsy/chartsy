package org.chartsy.fi;

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
import org.chartsy.main.managers.LoggerManager;
import org.chartsy.main.utils.StrokeGenerator;
import org.chartsy.main.utils.StrokePropertyEditor;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node.Property;
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
        setDisplayName("FI Properties");
    }

    public IndicatorNode(IndicatorProperties indicatorProperties) {
        super(Children.LEAF, Lookups.singleton(indicatorProperties));
        setDisplayName("FI Properties");
        indicatorProperties.addPropertyChangeListener(this);
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();

        Sheet.Set set = Sheet.createPropertiesSet();
        final IndicatorProperties indicatorProperties = getLookup().lookup(IndicatorProperties.class);

        try {
            // Period1
            @SuppressWarnings(value = "unchecked")
            Property period1 = new PropertySupport.Reflection(indicatorProperties, int.class, "getPeriod1", "setPeriod1") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(IndicatorProperties.PERIOD1); }
                public boolean supportsDefaultValue() { return true; }
            };
            period1.setName("Period 1");
            set.put(period1);

            // Period2
            @SuppressWarnings(value = "unchecked")
            Property period2 = new PropertySupport.Reflection(indicatorProperties, int.class, "getPeriod2", "setPeriod2") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(IndicatorProperties.PERIOD2); }
                public boolean supportsDefaultValue() { return true; }
            };
            period2.setName("Period 2");
            set.put(period2);

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

            // Color1
            @SuppressWarnings(value = "unchecked")
            Property color1 = new PropertySupport.Reflection(indicatorProperties, Color.class, "getColor1", "setColor1") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(IndicatorProperties.COLOR1); }
                public boolean supportsDefaultValue() { return true; }
            };
            color1.setName("Color 1");
            set.put(color1);

            // Stroke1
            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection stroke1 = new PropertySupport.Reflection(indicatorProperties, Stroke.class, "getStroke1", "setStroke1") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(StrokeGenerator.getStroke(IndicatorProperties.STROKE_INDEX1)); }
                public boolean supportsDefaultValue() { return true; }
            };
            stroke1.setPropertyEditorClass(StrokePropertyEditor.class);
            stroke1.setName("Line Style 1");
            set.put(stroke1);

            // Color2
            @SuppressWarnings(value = "unchecked")
            Property color2 = new PropertySupport.Reflection(indicatorProperties, Color.class, "getColor2", "setColor2") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(IndicatorProperties.COLOR2); }
                public boolean supportsDefaultValue() { return true; }
            };
            color2.setName("Color 2");
            set.put(color2);

            // Stroke2
            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection stroke2 = new PropertySupport.Reflection(indicatorProperties, Stroke.class, "getStroke2", "setStroke2") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(StrokeGenerator.getStroke(IndicatorProperties.STROKE_INDEX2)); }
                public boolean supportsDefaultValue() { return true; }
            };
            stroke2.setPropertyEditorClass(StrokePropertyEditor.class);
            stroke2.setName("Line Style 2");
            set.put(stroke2);
        } catch (Exception e) {
            LoggerManager.getDefault().log(e.getMessage(), e);
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