package org.chartsy.bollingerb;

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
        setDisplayName("SVE_BB%b Properties");
    }

    public IndicatorNode(IndicatorProperties indicatorProperties) {
        super(Children.LEAF, Lookups.singleton(indicatorProperties));
        setDisplayName("SVE_BB%b Properties");
        indicatorProperties.addPropertyChangeListener(this);
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();

        Sheet.Set set = Sheet.createPropertiesSet();
        final IndicatorProperties indicatorProperties = getLookup().lookup(IndicatorProperties.class);

        try {
            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection period = new PropertySupport.Reflection(indicatorProperties, int.class, "getPeriod", "setPeriod") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(IndicatorProperties.PERIOD); }
                public boolean supportsDefaultValue() { return true; }
            };
            period.setName("Period");
            set.put(period);

            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection stdPeriod = new PropertySupport.Reflection(indicatorProperties, int.class, "getStdPeriod", "setStdPeriod") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(IndicatorProperties.STDPERIOD); }
                public boolean supportsDefaultValue() { return true; }
            };
            stdPeriod.setName("Std. Dev. Period");
            set.put(stdPeriod);

            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection stdHigh = new PropertySupport.Reflection(indicatorProperties, double.class, "getStdHigh", "setStdHith") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(IndicatorProperties.STDHIGH); }
                public boolean supportsDefaultValue() { return true; }
            };
            stdHigh.setName("Std. Dev. High");
            set.put(stdHigh);

            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection stdLow = new PropertySupport.Reflection(indicatorProperties, double.class, "getStdLow", "setStdLow") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(IndicatorProperties.STDLOW); }
                public boolean supportsDefaultValue() { return true; }
            };
            stdLow.setName("Std. Dev. Low");
            set.put(stdLow);

            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection temaPeriod = new PropertySupport.Reflection(indicatorProperties, int.class, "getTemaPeriod", "setTemaPeriod") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(IndicatorProperties.TEMAPERIOD); }
                public boolean supportsDefaultValue() { return true; }
            };
            temaPeriod.setName("TEMA Period");
            set.put(temaPeriod);

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

            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection marker = new PropertySupport.Reflection(indicatorProperties, boolean.class, "getMarker", "setMarker") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(IndicatorProperties.MARKER); }
                public boolean supportsDefaultValue() { return true; }
            };
            marker.setName("Marker");
            set.put(marker);

            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection color = new PropertySupport.Reflection(indicatorProperties, Color.class, "getColor", "setColor") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(IndicatorProperties.COLOR); }
                public boolean supportsDefaultValue() { return true; }
            };
            color.setName("Color");
            set.put(color);

            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection strokeIndex = new PropertySupport.Reflection(indicatorProperties, Stroke.class, "getStroke", "setStroke") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(StrokeGenerator.getStroke(IndicatorProperties.STROKE_INDEX)); }
                public boolean supportsDefaultValue() { return true; }
            };
            strokeIndex.setPropertyEditorClass(StrokePropertyEditor.class);
            strokeIndex.setName("Style");
            set.put(strokeIndex);

            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection stdColor = new PropertySupport.Reflection(indicatorProperties, Color.class, "getStdColor", "setStdColor") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(IndicatorProperties.STD_COLOR); }
                public boolean supportsDefaultValue() { return true; }
            };
            stdColor.setName("Standard Deviation Color");
            set.put(stdColor);

            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection stdStrokeIndex = new PropertySupport.Reflection(indicatorProperties, Stroke.class, "getStdStroke", "setStdStroke") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(StrokeGenerator.getStroke(IndicatorProperties.STD_STROKE_INDEX)); }
                public boolean supportsDefaultValue() { return true; }
            };
            stdStrokeIndex.setPropertyEditorClass(StrokePropertyEditor.class);
            stdStrokeIndex.setName("Standard Deviation Style");
            set.put(stdStrokeIndex);
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
