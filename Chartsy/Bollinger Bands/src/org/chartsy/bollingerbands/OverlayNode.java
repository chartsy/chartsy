package org.chartsy.bollingerbands;

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
import org.chartsy.main.utils.PricePropertyEditor;
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
public class OverlayNode extends AbstractNode implements PropertyChangeListener, Externalizable {

    private static final long serialVersionUID = 101L;

    public OverlayNode() {
        super(Children.LEAF);
        setDisplayName("Bollinger Bands Properties");
    }

    public OverlayNode(OverlayProperties overlayProperties) {
        super(Children.LEAF, Lookups.singleton(overlayProperties));
        setDisplayName("Bollinger Bands Properties");
        overlayProperties.addPropertyChangeListener(this);
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();

        Sheet.Set set = Sheet.createPropertiesSet();
        final OverlayProperties overlayProperties = getLookup().lookup(OverlayProperties.class);

        try {
            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection std = new PropertySupport.Reflection(overlayProperties, int.class, "getStd", "setStd") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(OverlayProperties.STD); }
                public boolean supportsDefaultValue() { return true; }
            };
            std.setName("# x Std. Dev.");
            set.put(std);

            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection period = new PropertySupport.Reflection(overlayProperties, int.class, "getPeriod", "setPeriod") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(OverlayProperties.PERIOD); }
                public boolean supportsDefaultValue() { return true; }
            };
            period.setName("Period");
            set.put(period);

            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection price = new PropertySupport.Reflection(overlayProperties, String.class, "getPrice", "setPrice") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(OverlayProperties.PRICE); }
                public boolean supportsDefaultValue() { return true; }
            };
            price.setPropertyEditorClass(PricePropertyEditor.class);
            price.setName("Price");
            set.put(price);

            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection label = new PropertySupport.Reflection(overlayProperties, String.class, "getLabel", "setLabel") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(OverlayProperties.LABEL); }
                public boolean supportsDefaultValue() { return true; }
            };
            label.setPropertyEditorClass(PropertyEditorSupport.class);
            label.setName("Label");
            set.put(label);

            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection marker = new PropertySupport.Reflection(overlayProperties, boolean.class, "getMarker", "setMarker") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(OverlayProperties.MARKER); }
                public boolean supportsDefaultValue() { return true; }
            };
            marker.setName("Marker");
            set.put(marker);

            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection lowerColor = new PropertySupport.Reflection(overlayProperties, Color.class, "getLowerColor", "setLowerColor") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(OverlayProperties.LOWER_COLOR); }
                public boolean supportsDefaultValue() { return true; }
            };
            lowerColor.setName("Lower Line Color");
            set.put(lowerColor);

            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection lowerStrokeIndex = new PropertySupport.Reflection(overlayProperties, Stroke.class, "getLowerStroke", "setLowerStroke") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(StrokeGenerator.getStroke(OverlayProperties.LOWER_STROKE_INDEX)); }
                public boolean supportsDefaultValue() { return true; }
            };
            lowerStrokeIndex.setPropertyEditorClass(StrokePropertyEditor.class);
            lowerStrokeIndex.setName("Lower Line Style");
            set.put(lowerStrokeIndex);

            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection middleColor = new PropertySupport.Reflection(overlayProperties, Color.class, "getMiddleColor", "setMiddleColor") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(OverlayProperties.MIDDLE_COLOR); }
                public boolean supportsDefaultValue() { return true; }
            };
            middleColor.setName("Middle Line Color");
            set.put(middleColor);

            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection middleStrokeIndex = new PropertySupport.Reflection(overlayProperties, Stroke.class, "getMiddleStroke", "setMiddleStroke") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(StrokeGenerator.getStroke(OverlayProperties.MIDDLE_STROKE_INDEX)); }
                public boolean supportsDefaultValue() { return true; }
            };
            middleStrokeIndex.setPropertyEditorClass(StrokePropertyEditor.class);
            middleStrokeIndex.setName("Middle Line Style");
            set.put(middleStrokeIndex);

            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection upperColor = new PropertySupport.Reflection(overlayProperties, Color.class, "getUpperColor", "setUpperColor") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(OverlayProperties.UPPER_COLOR); }
                public boolean supportsDefaultValue() { return true; }
            };
            upperColor.setName("Upper Line Color");
            set.put(upperColor);

            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection upperStrokeIndex = new PropertySupport.Reflection(overlayProperties, Stroke.class, "getUpperStroke", "setUpperStroke") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(StrokeGenerator.getStroke(OverlayProperties.UPPER_STROKE_INDEX)); }
                public boolean supportsDefaultValue() { return true; }
            };
            upperStrokeIndex.setPropertyEditorClass(StrokePropertyEditor.class);
            upperStrokeIndex.setName("Upper Line Style");
            set.put(upperStrokeIndex);

            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection insideColor = new PropertySupport.Reflection(overlayProperties, Color.class, "getInsideColor", "setInsideColor") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(OverlayProperties.INSIDE_COLOR); }
                public boolean supportsDefaultValue() { return true; }
            };
            insideColor.setName("Inside Color");
            set.put(insideColor);

            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection insideAlpha = new PropertySupport.Reflection(overlayProperties, int.class, "getInsideAlpha", "setInsideAlpha") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(OverlayProperties.INSIDE_ALPHA); }
                public boolean supportsDefaultValue() { return true; }
            };
            insideAlpha.setPropertyEditorClass(AlphaPropertyEditor.class);
            insideAlpha.setName("Inside Opacity");
            set.put(insideAlpha);

            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection insideVisibility = new PropertySupport.Reflection(overlayProperties, boolean.class, "getInsideVisibility", "setInsideVisibility") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(OverlayProperties.INSIDE_VISIBILITY); }
                public boolean supportsDefaultValue() { return true; }
            };
            insideVisibility.setName("Inside Visibility");
            set.put(insideVisibility);
        } catch (Exception ex) {
            ex.printStackTrace();
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
