package org.chartsy.annotation.rectangle;

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
public class AnnotationNode extends AbstractNode implements PropertyChangeListener, Externalizable {
    
    private static final long serialVersionUID = 101L;

    public AnnotationNode() {
        super(Children.LEAF);
        setDisplayName("Rectangle Properties");
    }

    public AnnotationNode(AnnotationProperties annotationProperties) {
        super(Children.LEAF, Lookups.singleton(annotationProperties));
        setDisplayName("Rectangle Properties");
        annotationProperties.addPropertyChangeListener(this);
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();

        Sheet.Set set = Sheet.createPropertiesSet();
        final AnnotationProperties annotationProperties = getLookup().lookup(AnnotationProperties.class);

        try {
            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection color = new PropertySupport.Reflection(annotationProperties, Color.class, "getColor", "setColor") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(AnnotationProperties.COLOR); }
                public boolean supportsDefaultValue() { return true; }
            };
            color.setName("Color");
            set.put(color);

            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection stroke = new PropertySupport.Reflection(annotationProperties, Stroke.class, "getStroke", "setStroke") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(StrokeGenerator.getStroke(AnnotationProperties.STROKE_INDEX)); }
                public boolean supportsDefaultValue() { return true; }
            };
            stroke.setPropertyEditorClass(StrokePropertyEditor.class);
            stroke.setName("Line Style");
            set.put(stroke);

            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection insideAlpha = new PropertySupport.Reflection(annotationProperties, int.class, "getInsideAlpha", "setInsideAlpha") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(AnnotationProperties.INSIDE_ALPHA); }
                public boolean supportsDefaultValue() { return true; }
            };
            insideAlpha.setPropertyEditorClass(AlphaPropertyEditor.class);
            insideAlpha.setName("Inside Opacity");
            set.put(insideAlpha);

            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection insideVisibility = new PropertySupport.Reflection(annotationProperties, boolean.class, "getInsideVisibility", "setInsideVisibility") {
                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { return super.getValue(); }
                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { super.setValue(obj); }
                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException { super.setValue(AnnotationProperties.INSIDE_VISIBILITY); }
                public boolean supportsDefaultValue() { return true; }
            };
            insideVisibility.setName("Inside Visibility");
            set.put(insideVisibility);
        } catch (Exception ex) {
            LoggerManager.getDefault().log(ex.getMessage(), ex);
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
