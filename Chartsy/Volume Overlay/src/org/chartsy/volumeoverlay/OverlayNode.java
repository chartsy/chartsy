package org.chartsy.volumeoverlay;

import java.awt.Color;
import java.beans.PropertyEditorSupport;
import java.util.logging.Level;
import org.chartsy.main.chart.AbstractPropertiesNode;
import org.chartsy.main.utils.AlphaPropertyEditor;
import org.chartsy.main.utils.SerialVersion;
import org.openide.nodes.Sheet;

public class OverlayNode 
        extends AbstractPropertiesNode
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public OverlayNode()
    {
        super("Volume Overlay Properties");
    }

    public OverlayNode(OverlayProperties overlayProperties)
    {
        super("Volume Overlay Properties", overlayProperties);
    }

    @SuppressWarnings("unchecked")
    protected @Override Sheet createSheet()
    {
        Sheet sheet = new Sheet();
        sheet.put(getSets()[0]);
        return sheet;
    }

    public @Override Sheet.Set[] getSets()
    {
        Sheet.Set[] sets = new Sheet.Set[1];
        Sheet.Set set = getPropertiesSet();
        sets[0] = set;

        try
        {
            // Label
            set.put(getProperty(
                    "Label", // property name
                    "Sets the label", // property description
                    OverlayProperties.class, // properties class
                    String.class, // property class
                    PropertyEditorSupport.class, // property editor class (null if none)
                    "getLabel", // get method name
                    "setLabel", // set method name
                    OverlayProperties.LABEL // default property value
                    ));
            // Bars Color
            set.put(getProperty(
                    "Bars Color", // property name
                    "Sets the bars color", // property description
                    OverlayProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getColor", // get method name
                    "setColor", // set method name
                    OverlayProperties.COLOR // default property value
                    ));
            // Bars Alpha
            set.put(getProperty(
                    "Bars Alpha", // property name
                    "Sets the bars alpha value", // property description
                    OverlayProperties.class, // properties class
                    Integer.class, // property class
                    AlphaPropertyEditor.class, // property editor class (null if none)
                    "getAlpha", // get method name
                    "setAlpha", // set method name
                    OverlayProperties.ALPHA // default property value
                    ));
        }
        catch (NoSuchMethodException ex)
        {
            LOG.log(Level.SEVERE, "[VolumeOverlayNode] : Method does not exist.", ex);
        }

        return sets;
    }
    
}
