package org.chartsy.stochastics;

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

    private static final long serialVersionUID = 2L;

    public IndicatorNode() {
        super(Children.LEAF);
        setDisplayName("Stochastics Properties");
    }

    public IndicatorNode(IndicatorProperties indicatorProperties) {
        super(Children.LEAF, Lookups.singleton(indicatorProperties));
        setDisplayName("Stochastics Properties");
        indicatorProperties.addPropertyChangeListener(this);
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();

        Sheet.Set set = Sheet.createPropertiesSet();
        final IndicatorProperties indicatorProperties = getLookup().lookup(IndicatorProperties.class);

        try {
            // Period K
            @SuppressWarnings(value = "unchecked")
            Property periodK = new PropertySupport.Reflection(indicatorProperties, int.class, "getPeriodK", "setPeriodK") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(IndicatorProperties.PERIOD_K); }
                public boolean supportsDefaultValue() { return true; }
            };
            periodK.setName("PeriodK");
            set.put(periodK);

            // Smooth
            @SuppressWarnings(value = "unchecked")
            Property smooth = new PropertySupport.Reflection(indicatorProperties, int.class, "getSmooth", "setSmooth") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(IndicatorProperties.SMOOTH); }
                public boolean supportsDefaultValue() { return true; }
            };
            smooth.setName("Smooth");
            set.put(smooth);
            
            // Period D
            @SuppressWarnings(value = "unchecked")
            Property periodD = new PropertySupport.Reflection(indicatorProperties, int.class, "getPeriodD", "setPeriodD") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(IndicatorProperties.PERIOD_D); }
                public boolean supportsDefaultValue() { return true; }
            };
            periodD.setName("PeriodD");
            set.put(periodD);

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

            // Marker
            @SuppressWarnings(value = "unchecked")
            Property sf = new PropertySupport.Reflection(indicatorProperties, boolean.class, "getSF", "setSF") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(IndicatorProperties.SF); }
                public boolean supportsDefaultValue() { return true; }
            };
            sf.setName("Fast/Slow");
            set.put(sf);

            // Color D
            @SuppressWarnings(value = "unchecked")
            Property colorD = new PropertySupport.Reflection(indicatorProperties, Color.class, "getColorD", "setColorD") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(IndicatorProperties.COLOR_D); }
                public boolean supportsDefaultValue() { return true; }
            };
            colorD.setName("ColorD");
            set.put(colorD);

            // Stroke D
            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection strokeD = new PropertySupport.Reflection(indicatorProperties, Stroke.class, "getStrokeD", "setStrokeD") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(StrokeGenerator.getStroke(IndicatorProperties.STROKE_INDEX_D)); }
                public boolean supportsDefaultValue() { return true; }
            };
            strokeD.setPropertyEditorClass(StrokePropertyEditor.class);
            strokeD.setName("Line Style D");
            set.put(strokeD);

            // Color K
            @SuppressWarnings(value = "unchecked")
            Property colorK = new PropertySupport.Reflection(indicatorProperties, Color.class, "getColorK", "setColorK") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(IndicatorProperties.COLOR_K); }
                public boolean supportsDefaultValue() { return true; }
            };
            colorK.setName("ColorK");
            set.put(colorK);

            // Stroke K
            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection strokeK = new PropertySupport.Reflection(indicatorProperties, Stroke.class, "getStrokeK", "setStrokeK") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(StrokeGenerator.getStroke(IndicatorProperties.STROKE_INDEX_K)); }
                public boolean supportsDefaultValue() { return true; }
            };
            strokeK.setPropertyEditorClass(StrokePropertyEditor.class);
            strokeK.setName("Line Style K");
            set.put(strokeD);
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
