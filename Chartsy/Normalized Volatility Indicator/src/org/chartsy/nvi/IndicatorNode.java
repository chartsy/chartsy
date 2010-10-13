package org.chartsy.nvi;

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
 * @author Viorel
 */
public class IndicatorNode
        extends AbstractPropertiesNode
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public IndicatorNode()
    {
        super("Normalized Volatility Indicator Properties");
    }

    public IndicatorNode(IndicatorProperties indicatorProperties)
    {
        super("Normalized Volatility Indicator Properties", indicatorProperties);
    }

    @SuppressWarnings("unchecked")
    protected @Override Sheet createSheet()
    {
        Sheet sheet = new Sheet();
        Sheet.Set set = getPropertiesSet();
        Sheet.Set ma1Set = getPropertiesSet("65 Moving Average Properties");
        Sheet.Set ma2Set = getPropertiesSet("200 Moving Average Properties");

        sheet.put(set);
        sheet.put(ma1Set);
        sheet.put(ma2Set);

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
                    "Sets the period value", // property description
                    IndicatorProperties.class, // properties class
                    int.class, // property class
                    null, // property editor class (null if none)
                    "getPeriod", // get method name
                    "setPeriod", // set method name
                    IndicatorProperties.PERIOD // default property value
                    ));
            // Line Color
            set.put(getProperty(
                    "Line Color", // property name
                    "Sets the line color", // property description
                    IndicatorProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getColor", // get method name
                    "setColor", // set method name
                    IndicatorProperties.COLOR // default property value
                    ));
            // Line Style
            set.put(getProperty(
                    "Line Style", // property name
                    "Sets the line style", // property description
                    IndicatorProperties.class, // properties class
                    Stroke.class, // property class
                    StrokePropertyEditor.class, // property editor class (null if none)
                    "getStroke", // get method name
                    "setStroke", // set method name
                    StrokeGenerator.getStroke(IndicatorProperties.STROKE_INDEX) // default property value
                    ));
            // 65 MA Period
            ma1Set.put(getProperty(
                    "Period", // property name
                    "Sets the moving average period value", // property description
                    IndicatorProperties.class, // properties class
                    int.class, // property class
                    null, // property editor class (null if none)
                    "getMA65Period", // get method name
                    "setMA65Period", // set method name
                    IndicatorProperties.MA65_PERIOD // default property value
                    ));
            // 65 MA Line Color
            ma1Set.put(getProperty(
                    "Line Color", // property name
                    "Sets the moving average line color", // property description
                    IndicatorProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getMA65Color", // get method name
                    "setMA65Color", // set method name
                    IndicatorProperties.MA65_COLOR // default property value
                    ));
            // 65 MA Line Style
            ma1Set.put(getProperty(
                    "Line Style", // property name
                    "Sets the moving average line style", // property description
                    IndicatorProperties.class, // properties class
                    Stroke.class, // property class
                    StrokePropertyEditor.class, // property editor class (null if none)
                    "getMA65Stroke", // get method name
                    "setMA65Stroke", // set method name
                    StrokeGenerator.getStroke(IndicatorProperties.MA65_STROKE_INDEX) // default property value
                    ));
            // 65 MA Visibility
            ma1Set.put(getProperty(
                    "Line Visibility", // property name
                    "Sets the line visibility", // property description
                    IndicatorProperties.class, // properties class
                    boolean.class, // property class
                    null, // property editor class (null if none)
                    "getMA65Visibility", // get method name
                    "setMA65Visibility", // set method name
                    IndicatorProperties.MA65_VISIBILITY // default property value
                    ));
            // 200 MA Period
            ma2Set.put(getProperty(
                    "Period", // property name
                    "Sets the moving average period value", // property description
                    IndicatorProperties.class, // properties class
                    int.class, // property class
                    null, // property editor class (null if none)
                    "getMA200Period", // get method name
                    "setMA200Period", // set method name
                    IndicatorProperties.MA200_PERIOD // default property value
                    ));
            // 200 MA Line Color
            ma2Set.put(getProperty(
                    "Line Color", // property name
                    "Sets the moving average line color", // property description
                    IndicatorProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getMA200Color", // get method name
                    "setMA200Color", // set method name
                    IndicatorProperties.MA200_COLOR // default property value
                    ));
            // 200 MA Line Style
            ma2Set.put(getProperty(
                    "Line Style", // property name
                    "Sets the moving average line style", // property description
                    IndicatorProperties.class, // properties class
                    Stroke.class, // property class
                    StrokePropertyEditor.class, // property editor class (null if none)
                    "getMA200Stroke", // get method name
                    "setMA200Stroke", // set method name
                    StrokeGenerator.getStroke(IndicatorProperties.MA200_STROKE_INDEX) // default property value
                    ));
            // 200 MA Visibility
            ma2Set.put(getProperty(
                    "Line Visibility", // property name
                    "Sets the line visibility", // property description
                    IndicatorProperties.class, // properties class
                    boolean.class, // property class
                    null, // property editor class (null if none)
                    "getMA200Visibility", // get method name
                    "setMA200Visibility", // set method name
                    IndicatorProperties.MA200_VISIBILITY // default property value
                    ));
        }
        catch (NoSuchMethodException ex)
        {
            LOG.log(Level.SEVERE, "[VPI Node] : Method does not exist.", ex);
        }

        return sheet;
    }

}
