package org.chartsy.macd;

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
        super("MACD Properties");
    }

    public IndicatorNode(IndicatorProperties indicatorProperties)
    {
        super("MACD Properties", indicatorProperties);
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
            // Fast
            set.put(getProperty(
                    "Fast", // property name
                    "Sets the fast value", // property description
                    IndicatorProperties.class, // properties class
                    int.class, // property class
                    null, // property editor class (null if none)
                    "getFast", // get method name
                    "setFast", // set method name
                    IndicatorProperties.FAST // default property value
                    ));
            // Slow
            set.put(getProperty(
                    "Slow", // property name
                    "Sets the slow value", // property description
                    IndicatorProperties.class, // properties class
                    int.class, // property class
                    null, // property editor class (null if none)
                    "getSlow", // get method name
                    "setSlow", // set method name
                    IndicatorProperties.SLOW // default property value
                    ));
            // Smooth
            set.put(getProperty(
                    "Smooth", // property name
                    "Sets the smooth value", // property description
                    IndicatorProperties.class, // properties class
                    int.class, // property class
                    null, // property editor class (null if none)
                    "getSmooth", // get method name
                    "setSmooth", // set method name
                    IndicatorProperties.SMOOTH // default property value
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
                    StrokeGenerator.getStroke(IndicatorProperties.ZERO_LINE_STROKE_INDEX) // default property value
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
            // Histogram Positive Color
            set.put(getProperty(
                    "Histogram Positive Color", // property name
                    "Sets the histogram positive color", // property description
                    IndicatorProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getHistogramPositiveColor", // get method name
                    "setHistogramPositiveColor", // set method name
                    IndicatorProperties.HISTOGRAM_POSITIVE_COLOR // default property value
                    ));
            // Histogram Negative Color
            set.put(getProperty(
                    "Histogram Negative Color", // property name
                    "Sets the histogram negative color", // property description
                    IndicatorProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getHistogramNegativeColor", // get method name
                    "setHistogramNegativeColor", // set method name
                    IndicatorProperties.HISTOGRAM_NEGATIVE_COLOR // default property value
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
                    IndicatorProperties.SIGNAL_COLOR // default property value
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
            // MACD Line Color
            set.put(getProperty(
                    "MACD Line Color", // property name
                    "Sets the macd line color", // property description
                    IndicatorProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getMacdColor", // get method name
                    "setMacdColor", // set method name
                    IndicatorProperties.MACD_COLOR // default property value
                    ));
            // MACD Line Style
            set.put(getProperty(
                    "MACD Line Style", // property name
                    "Sets the macd line style", // property description
                    IndicatorProperties.class, // properties class
                    Stroke.class, // property class
                    StrokePropertyEditor.class, // property editor class (null if none)
                    "getMacdStroke", // get method name
                    "setMacdStroke", // set method name
                    StrokeGenerator.getStroke(IndicatorProperties.MACD_STROKE_INDEX) // default property value
                    ));
        }
        catch (NoSuchMethodException ex)
        {
            LOG.log(Level.SEVERE, "[MACD Node] : Method does not exist.", ex);
        }

        return sheet;
    }

}
