package org.chartsy.volumeoverlay;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditorSupport;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.InvocationTargetException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.lookup.Lookups;

public class OverlayNode extends AbstractNode implements PropertyChangeListener, Externalizable
{

    private static final long serialVersionUID = 2L;

    public OverlayNode()
    {
        super(Children.LEAF);
        setDisplayName("Volume Properties");
    }

    public OverlayNode(OverlayProperties overlayProperties)
    {
        super(Children.LEAF, Lookups.singleton(overlayProperties));
        setDisplayName("Volume Properties");
        overlayProperties.addPropertyChangeListener(this);
    }

    @Override
    protected Sheet createSheet()
    {
        Sheet sheet = Sheet.createDefault();

        Sheet.Set set = Sheet.createPropertiesSet();
        final OverlayProperties overlayProperties = getLookup().lookup(OverlayProperties.class);

        try
        {
            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection label = new PropertySupport.Reflection(overlayProperties, String.class, "getLabel", "setLabel")
            {

                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
                {
                    return super.getValue();
                }

                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
                {
                    super.setValue(obj);
                }

                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException
                {
                    super.setValue(OverlayProperties.LABEL);
                }

                public boolean supportsDefaultValue()
                {
                    return true;
                }
            };
            label.setPropertyEditorClass(PropertyEditorSupport.class);
            label.setName("Label");
            set.put(label);
            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection marker = new PropertySupport.Reflection(overlayProperties, boolean.class, "getMarker", "setMarker")
            {

                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
                {
                    return super.getValue();
                }

                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
                {
                    super.setValue(obj);
                }

                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException
                {
                    super.setValue(OverlayProperties.MARKER);
                }

                public boolean supportsDefaultValue()
                {
                    return true;
                }
            };
            marker.setName("Marker");
            set.put(marker);

            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection color = new PropertySupport.Reflection(overlayProperties, Color.class, "getColor", "setColor")
            {

                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
                {
                    return super.getValue();
                }

                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
                {
                    super.setValue(obj);
                }

                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException
                {
                    super.setValue(OverlayProperties.COLOR);
                }

                public boolean supportsDefaultValue()
                {
                    return true;
                }
            };
            color.setName("Color");
            set.put(color);

            @SuppressWarnings(value = "unchecked")
            PropertySupport.Reflection alpha = new PropertySupport.Reflection(overlayProperties, Integer.class, "getAlpha", "setAlpha")
            {

                public Object getValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
                {
                    return super.getValue();
                }

                public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
                {
                    super.setValue(obj);
                }

                public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException
                {
                    super.setValue(OverlayProperties.ALPHA);
                }

                public boolean supportsDefaultValue()
                {
                    return true;
                }
            };
            alpha.setName("Alpha");
            set.put(alpha);


        } catch (Exception ex)
        {
            ex.printStackTrace();
        }

        sheet.put(set);

        return sheet;
    }

    public void propertyChange(PropertyChangeEvent evt)
    {
        firePropertySetsChange(null, getPropertySets());
    }

    public void writeExternal(ObjectOutput out) throws IOException
    {
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
    }
}
