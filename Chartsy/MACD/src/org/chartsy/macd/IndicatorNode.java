package org.chartsy.macd;

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
        setDisplayName("MACD Properties");
    }

    public IndicatorNode(IndicatorProperties indicatorProperties) {
        super(Children.LEAF, Lookups.singleton(indicatorProperties));
        setDisplayName("MACD Properties");
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

            // Zero Line Color
            @SuppressWarnings(value = "unchecked")
            Property zeroLineColor = new PropertySupport.Reflection(indicatorProperties, Color.class, "getZeroLineColor", "setZeroLineColor") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(IndicatorProperties.ZERO_LINE_COLOR); }
                public boolean supportsDefaultValue() { return true; }
            };
            zeroLineColor.setName("Zero Line Color");
            set.put(zeroLineColor);

            // Zero Line Stroke
            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection zeroLineStroke = new PropertySupport.Reflection(indicatorProperties, Stroke.class, "getZeroLineStroke", "setZeroLineStroke") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(StrokeGenerator.getStroke(IndicatorProperties.ZERO_STROKE_INDEX)); }
                public boolean supportsDefaultValue() { return true; }
            };
            zeroLineStroke.setPropertyEditorClass(StrokePropertyEditor.class);
            zeroLineStroke.setName("Zero Line Style");
            set.put(zeroLineStroke);

            // Zero Line Visibility
            @SuppressWarnings(value = "unchecked")
            Property zeroLineVisibility = new PropertySupport.Reflection(indicatorProperties, boolean.class, "getZeroLineVisibility", "setZeroLineVisibility") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(IndicatorProperties.ZERO_LINE_VISIBILITY); }
                public boolean supportsDefaultValue() { return true; }
            };
            zeroLineVisibility.setName("Zero Line Visibility");
            set.put(zeroLineVisibility);

            // Histogram Color
            @SuppressWarnings(value = "unchecked")
            Property histogramColor = new PropertySupport.Reflection(indicatorProperties, Color.class, "getHistogramColor", "setHistogramColor") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(IndicatorProperties.HISTOGRAM_COLOR); }
                public boolean supportsDefaultValue() { return true; }
            };
            histogramColor.setName("Histogram Color");
            set.put(histogramColor);

            // Signal Color
            @SuppressWarnings(value = "unchecked")
            Property signalColor = new PropertySupport.Reflection(indicatorProperties, Color.class, "getSignalColor", "setSignalColor") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(IndicatorProperties.SIGNAL_COLOR); }
                public boolean supportsDefaultValue() { return true; }
            };
            signalColor.setName("Signal Color");
            set.put(signalColor);

            // Signal Stroke
            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection signalStroke = new PropertySupport.Reflection(indicatorProperties, Stroke.class, "getSignalStroke", "setSignalStroke") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(StrokeGenerator.getStroke(IndicatorProperties.SIGNAL_STROKE_INDEX)); }
                public boolean supportsDefaultValue() { return true; }
            };
            signalStroke.setPropertyEditorClass(StrokePropertyEditor.class);
            signalStroke.setName("Signal Style");
            set.put(signalStroke);

            // MACD Color
            @SuppressWarnings(value = "unchecked")
            Property macdColor = new PropertySupport.Reflection(indicatorProperties, Color.class, "getMacdColor", "setMacdColor") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(IndicatorProperties.MACD_COLOR); }
                public boolean supportsDefaultValue() { return true; }
            };
            macdColor.setName("MACD Color");
            set.put(macdColor);

            // MACD Stroke
            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection macdStroke = new PropertySupport.Reflection(indicatorProperties, Stroke.class, "getMacdStroke", "setMacdStroke") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(StrokeGenerator.getStroke(IndicatorProperties.MACD_STROKE_INDEX)); }
                public boolean supportsDefaultValue() { return true; }
            };
            macdStroke.setPropertyEditorClass(StrokePropertyEditor.class);
            macdStroke.setName("MACD Style");
            set.put(macdStroke);
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
