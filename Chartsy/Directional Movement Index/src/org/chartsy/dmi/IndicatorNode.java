package org.chartsy.dmi;

import java.awt.Color;
import java.awt.Stroke;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
        setDisplayName("DMI Properties");
    }

    public IndicatorNode(IndicatorProperties indicatorProperties) {
        super(Children.LEAF, Lookups.singleton(indicatorProperties));
        setDisplayName("DMI Properties");
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

            // PDI Color
            @SuppressWarnings(value = "unchecked")
            Property pdiColor = new PropertySupport.Reflection(indicatorProperties, Color.class, "getPDIColor", "setPDIColor") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(IndicatorProperties.PDI_COLOR); }
                public boolean supportsDefaultValue() { return true; }
            };
            pdiColor.setName("DI+ Color");
            set.put(pdiColor);

            // PDI Stroke
            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection pdiStroke = new PropertySupport.Reflection(indicatorProperties, Stroke.class, "getPDIStroke", "setPDIStroke") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(StrokeGenerator.getStroke(IndicatorProperties.PDI_STROKE_INDEX)); }
                public boolean supportsDefaultValue() { return true; }
            };
            pdiStroke.setPropertyEditorClass(StrokePropertyEditor.class);
            pdiStroke.setName("DI+ Line Style");
            set.put(pdiStroke);

            // MDI Color
            @SuppressWarnings(value = "unchecked")
            Property mdiColor = new PropertySupport.Reflection(indicatorProperties, Color.class, "getMDIColor", "setMDIColor") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(IndicatorProperties.MDI_COLOR); }
                public boolean supportsDefaultValue() { return true; }
            };
            mdiColor.setName("DI- Color");
            set.put(mdiColor);

            // MDI Stroke
            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection mdiStroke = new PropertySupport.Reflection(indicatorProperties, Stroke.class, "getMDIStroke", "setMDIStroke") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(StrokeGenerator.getStroke(IndicatorProperties.MDI_STROKE_INDEX)); }
                public boolean supportsDefaultValue() { return true; }
            };
            mdiStroke.setPropertyEditorClass(StrokePropertyEditor.class);
            mdiStroke.setName("DI- Line Style");
            set.put(mdiStroke);

            // ADX Color
            @SuppressWarnings(value = "unchecked")
            Property adxColor = new PropertySupport.Reflection(indicatorProperties, Color.class, "getADXColor", "setADXColor") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(IndicatorProperties.ADX_COLOR); }
                public boolean supportsDefaultValue() { return true; }
            };
            adxColor.setName("ADX Color");
            set.put(adxColor);

            // ADX Stroke
            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection adxStroke = new PropertySupport.Reflection(indicatorProperties, Stroke.class, "getADXStroke", "setADXStroke") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(StrokeGenerator.getStroke(IndicatorProperties.ADX_STROKE_INDEX)); }
                public boolean supportsDefaultValue() { return true; }
            };
            adxStroke.setPropertyEditorClass(StrokePropertyEditor.class);
            adxStroke.setName("ADX Line Style");
            set.put(adxStroke);
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