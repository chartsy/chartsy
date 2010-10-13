package org.chartsy.fi;

import java.awt.Color;
import java.awt.Stroke;
import java.beans.PropertyEditorSupport;
import java.util.logging.Level;
import org.chartsy.main.chart.AbstractPropertiesNode;
import org.chartsy.main.utils.SerialVersion;
import org.chartsy.main.utils.StrokeGenerator;
import org.chartsy.main.utils.StrokePropertyEditor;
import org.openide.nodes.Sheet;

/**
 *
 * @author viorel.gheba
 */
public class IndicatorNode 
        extends AbstractPropertiesNode
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public IndicatorNode()
    {
        super("FI Properties");
    }

    public IndicatorNode(IndicatorProperties indicatorProperties)
    {
        super("FI Properties", indicatorProperties);
    }

    @SuppressWarnings("unchecked")
    protected @Override Sheet createSheet()
    {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = getPropertiesSet();
        sheet.put(set);

        try
        {
            // Label
            set.put(getProperty(
                    "Label", // property name
                    "Sets the label", // property description
                    IndicatorProperties.class, // properties class
                    String.class, // property class
                    PropertyEditorSupport.class, // property editor class (null if none)
                    "getLabel", // get method name
                    "setLabel", // set method name
                    IndicatorProperties.LABEL // default property value
                    ));
            // Marker Visibility
            set.put(getProperty(
                    "Marker Visibility", // property name
                    "Sets the marker visibility", // property description
                    IndicatorProperties.class, // properties class
                    boolean.class, // property class
                    null, // property editor class (null if none)
                    "getMarker", // get method name
                    "setMarker", // set method name
                    IndicatorProperties.MARKER // default property value
                    ));
            // Period 1
            set.put(getProperty(
                    "Period 1", // property name
                    "Sets the period 1 value", // property description
                    IndicatorProperties.class, // properties class
                    int.class, // property class
                    null, // property editor class (null if none)
                    "getPeriod1", // get method name
                    "setPeriod1", // set method name
                    IndicatorProperties.PERIOD1 // default property value
                    ));
            // Period 2
            set.put(getProperty(
                    "Period 2", // property name
                    "Sets the period 2 value", // property description
                    IndicatorProperties.class, // properties class
                    int.class, // property class
                    null, // property editor class (null if none)
                    "getPeriod2", // get method name
                    "setPeriod2", // set method name
                    IndicatorProperties.PERIOD2 // default property value
                    ));
            // Line Color 1
            set.put(getProperty(
                    "Line 1 Color", // property name
                    "Sets the line 1 color", // property description
                    IndicatorProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getColor1", // get method name
                    "setColor1", // set method name
                    IndicatorProperties.COLOR1 // default property value
                    ));
            // Line Style 1
            set.put(getProperty(
                    "Line 1 Style", // property name
                    "Sets the line 1 style", // property description
                    IndicatorProperties.class, // properties class
                    Stroke.class, // property class
                    StrokePropertyEditor.class, // property editor class (null if none)
                    "getStroke1", // get method name
                    "setStroke1", // set method name
                    StrokeGenerator.getStroke(IndicatorProperties.STROKE_INDEX1) // default property value
                    ));
            // Line Color 2
            set.put(getProperty(
                    "Line 2 Color", // property name
                    "Sets the line 2 color", // property description
                    IndicatorProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getColor2", // get method name
                    "setColor2", // set method name
                    IndicatorProperties.COLOR2 // default property value
                    ));
            // Line Style 2
            set.put(getProperty(
                    "Line 2 Style", // property name
                    "Sets the line 2 style", // property description
                    IndicatorProperties.class, // properties class
                    Stroke.class, // property class
                    StrokePropertyEditor.class, // property editor class (null if none)
                    "getStroke2", // get method name
                    "setStroke2", // set method name
                    StrokeGenerator.getStroke(IndicatorProperties.STROKE_INDEX2) // default property value
                    ));
        }
        catch (NoSuchMethodException ex)
        {
            LOG.log(Level.SEVERE, "[FI Node] : Method does not exist.", ex);
        }

        return sheet;
    }

}