/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.chartsy.trix;

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
 * @author joshua.taylor
 */
public class IndicatorNode extends AbstractPropertiesNode
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public IndicatorNode()
    {
        super("TRIX Properties");
    }

    public IndicatorNode(IndicatorProperties indicatorProperties)
    {
        super("TRIX Properties", indicatorProperties);
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
            // Period
            set.put(getProperty(
                    "Period", // property name
                    "Sets the Period", // property description
                    IndicatorProperties.class, // properties class
                    int.class, // property class
                    null, // property editor class (null if none)
                    "getPeriod", // get method name
                    "setPeriod", // set method name
                    IndicatorProperties.PERIOD // default property value
                    ));
            // EMA Period
            set.put(getProperty(
                    "Signal Line", // property name
                    "Sets the signal line", // property description
                    IndicatorProperties.class, // properties class
                    int.class, // property class
                    null, // property editor class (null if none)
                    "getEmaPeriod", // get method name
                    "setEmaPeriod", // set method name
                    IndicatorProperties.EMA_PERIOD // default property value
                    ));
            // TRIX Color
            set.put(getProperty(
                    "TRIX Line Color", // property name
                    "Sets the TRIX line color", // property description
                    IndicatorProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getTrixColor", // get method name
                    "setTrixColor", // set method name
                    IndicatorProperties.TRIX_LINE_COLOR // default property value
                    ));
            // TRIX Line Style
            set.put(getProperty(
                    "Trix Line Style", // property name
                    "Sets the TRIX line style", // property description
                    IndicatorProperties.class, // properties class
                    Stroke.class, // property class
                    StrokePropertyEditor.class, // property editor class (null if none)
                    "getTrixStroke", // get method name
                    "setTrixStroke", // set method name
                    StrokeGenerator.getStroke(IndicatorProperties.TRIX_STROKE_INDEX) // default property value
                    ));
            // Signal Line Color
            set.put(getProperty(
                    "Signal Line Color", // property name
                    "Sets the signal line color", // property description
                    IndicatorProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getSignalColor", // get method name
                    "setSignalColor", // set method name
                    IndicatorProperties.SIGNAL_LINE_COLOR // default property value
                    ));
            // Signal Line Style
            set.put(getProperty(
                    "Signal Line Style", // property name
                    "Sets the signal line style", // property description
                    IndicatorProperties.class, // properties class
                    Stroke.class, // property class
                    StrokePropertyEditor.class, // property editor class (null if none)
                    "getSignalStroke", // get method name
                    "setSignalStroke", // set method name
                    StrokeGenerator.getStroke(IndicatorProperties.SIGNAL_STROKE_INDEX) // default property value
                    ));

        }
        catch (NoSuchMethodException ex)
        {
            LOG.log(Level.SEVERE, "[TRIX Node] : Method does not exist.", ex);
        }

        return sheet;
    }
}
