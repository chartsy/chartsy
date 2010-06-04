package org.chartsy.moneyflow;

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
        super("Money Flow Properties");
    }

    public IndicatorNode(IndicatorProperties indicatorProperties)
    {
        super("Money Flow Properties", indicatorProperties);
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
            // +MF Color
            set.put(getProperty(
                    "+MF Color", // property name
                    "Sets the +mf color", // property description
                    IndicatorProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getMFHColor", // get method name
                    "setMFHColor", // set method name
                    IndicatorProperties.MFH_COLOR // default property value
                    ));
            // -MF Color
            set.put(getProperty(
                    "-MF Color", // property name
                    "Sets the -mf color", // property description
                    IndicatorProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getMFLColor", // get method name
                    "setMFLColor", // set method name
                    IndicatorProperties.MFL_COLOR // default property value
                    ));
            // Zero Line Color
            set.put(getProperty(
                    "Zero Line Color", // property name
                    "Sets the zero line color", // property description
                    IndicatorProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getZeroLineColor", // get method name
                    "setZeroLineColor", // set method name
                    IndicatorProperties.ZERO_LINE_COLOR // default property value
                    ));
            // Zero Line Style
            set.put(getProperty(
                    "Zero Line Style", // property name
                    "Sets the zero line style", // property description
                    IndicatorProperties.class, // properties class
                    Stroke.class, // property class
                    StrokePropertyEditor.class, // property editor class (null if none)
                    "getZeroLineStroke", // get method name
                    "setZeroLineStroke", // set method name
                    StrokeGenerator.getStroke(IndicatorProperties.ZERO_LINE_STROKE) // default property value
                    ));
            // Zero Line Visibility
            set.put(getProperty(
                    "Zero Line Visibility", // property name
                    "Sets the zero line visibility flag", // property description
                    IndicatorProperties.class, // properties class
                    boolean.class, // property class
                    null, // property editor class (null if none)
                    "getZeroLineVisibility", // get method name
                    "setZeroLineVisibility", // set method name
                    IndicatorProperties.ZERO_LINE_VISIBILITY // default property value
                    ));
        }
        catch (NoSuchMethodException ex)
        {
            LOG.log(Level.SEVERE, "[MFNode] : Method does not exist.", ex);
        }

        return sheet;
    }

}
