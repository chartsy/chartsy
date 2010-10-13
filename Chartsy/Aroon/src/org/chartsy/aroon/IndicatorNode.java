/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.chartsy.aroon;

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
        super("Aroon Properties");
    }

    public IndicatorNode(IndicatorProperties indicatorProperties)
    {
        super("Aroon Properties", indicatorProperties);
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
            // Up Trend Line Color
            set.put(getProperty(
                    "Up Trend Line Color", // property name
                    "Sets the up trend line color", // property description
                    IndicatorProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getUpTrendColor", // get method name
                    "setUpTrendColor", // set method name
                    IndicatorProperties.UP_TREND_LINE_COLOR // default property value
                    ));
            // Up Trend Line Style
            set.put(getProperty(
                    "Up Trend Line Style", // property name
                    "Sets the up trend line style", // property description
                    IndicatorProperties.class, // properties class
                    Stroke.class, // property class
                    StrokePropertyEditor.class, // property editor class (null if none)
                    "getUpTrendLineStroke", // get method name
                    "setUpTrendLineStroke", // set method name
                    StrokeGenerator.getStroke(IndicatorProperties.UP_TREND_STROKE_INDEX) // default property value
                    ));
            // Down Trend Line Color
            set.put(getProperty(
                    "Down Trend Line Color", // property name
                    "Sets the down trend line color", // property description
                    IndicatorProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getDownTrendColor", // get method name
                    "setDownTrendColor", // set method name
                    IndicatorProperties.DOWN_TREND_LINE_COLOR // default property value
                    ));
            // Down Trend Line Style
            set.put(getProperty(
                    "Down Trend Line Style", // property name
                    "Sets the down trend line style", // property description
                    IndicatorProperties.class, // properties class
                    Stroke.class, // property class
                    StrokePropertyEditor.class, // property editor class (null if none)
                    "getDownTrendLineStroke", // get method name
                    "setDownTrendLineStroke", // set method name
                    StrokeGenerator.getStroke(IndicatorProperties.DOWN_TREND_STROKE_INDEX) // default property value
                    ));

        }
        catch (NoSuchMethodException ex)
        {
            LOG.log(Level.SEVERE, "[Aroon Node] : Method does not exist.", ex);
        }

        return sheet;
    }
}
