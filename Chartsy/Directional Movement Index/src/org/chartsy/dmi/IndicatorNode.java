package org.chartsy.dmi;

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
        super("DMI Properties");
    }

    public IndicatorNode(IndicatorProperties indicatorProperties)
    {
        super("DMI Properties", indicatorProperties);
    }

    @SuppressWarnings("unchecked")
    protected @Override Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = getPropertiesSet();
        sheet.put(set);

        try
        {
            // Period
            set.put(getProperty(
                    "Period", // property name
                    "Sets the period value", // property description
                    IndicatorProperties.class, // properties class
                    int.class, // property class
                    null, // property editor class (null if none)
                    "getPeriod", // get method name
                    "setPeriod", // set method name
                    IndicatorProperties.PERIOD // default property value
                    ));
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
            // PDI Line Color
            set.put(getProperty(
                    "DI+ Line Color", // property name
                    "Sets the di+ line color", // property description
                    IndicatorProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getPDIColor", // get method name
                    "setPDIColor", // set method name
                    IndicatorProperties.PDI_COLOR // default property value
                    ));
            // PDI Line Style
            set.put(getProperty(
                    "DI+ Line Style", // property name
                    "Sets the di+ line style", // property description
                    IndicatorProperties.class, // properties class
                    Stroke.class, // property class
                    StrokePropertyEditor.class, // property editor class (null if none)
                    "getPDIStroke", // get method name
                    "setPDIStroke", // set method name
                    StrokeGenerator.getStroke(IndicatorProperties.PDI_STROKE_INDEX) // default property value
                    ));
            // MDI Line Color
            set.put(getProperty(
                    "DI- Line Color", // property name
                    "Sets the di- line color", // property description
                    IndicatorProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getMDIColor", // get method name
                    "setMDIColor", // set method name
                    IndicatorProperties.MDI_COLOR // default property value
                    ));
            // MDI Line Style
            set.put(getProperty(
                    "DI- Line Style", // property name
                    "Sets the di- line style", // property description
                    IndicatorProperties.class, // properties class
                    Stroke.class, // property class
                    StrokePropertyEditor.class, // property editor class (null if none)
                    "getMDIStroke", // get method name
                    "setMDIStroke", // set method name
                    StrokeGenerator.getStroke(IndicatorProperties.MDI_STROKE_INDEX) // default property value
                    ));
            // ADX Line Color
            set.put(getProperty(
                    "ADX Line Color", // property name
                    "Sets the adx line color", // property description
                    IndicatorProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getADXColor", // get method name
                    "setADXColor", // set method name
                    IndicatorProperties.ADX_COLOR // default property value
                    ));
            // ADX Line Style
            set.put(getProperty(
                    "ADX Line Style", // property name
                    "Sets the adx line style", // property description
                    IndicatorProperties.class, // properties class
                    Stroke.class, // property class
                    StrokePropertyEditor.class, // property editor class (null if none)
                    "getADXStroke", // get method name
                    "setADXStroke", // set method name
                    StrokeGenerator.getStroke(IndicatorProperties.ADX_STROKE_INDEX) // default property value
                    ));
        }
        catch (NoSuchMethodException ex)
        {
            LOG.log(Level.SEVERE, "[DMINode] : Method does not exist.", ex);
        }

        return sheet;
    }

}