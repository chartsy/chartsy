package org.chartsy.stochastics;

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
        super("Stochastic Properties");
    }

    public IndicatorNode(IndicatorProperties indicatorProperties)
    {
        super("Stochastic Properties", indicatorProperties);
    }

    @SuppressWarnings("unchecked")
    protected @Override Sheet createSheet()
    {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = getPropertiesSet();
        sheet.put(set);

        try
        {
            // K Line Period
            set.put(getProperty(
                    "K Line Period", // property name
                    "Sets the k line period value", // property description
                    IndicatorProperties.class, // properties class
                    int.class, // property class
                    null, // property editor class (null if none)
                    "getPeriodK", // get method name
                    "setPeriodK", // set method name
                    IndicatorProperties.PERIOD_K // default property value
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
            // D Line Period
            set.put(getProperty(
                    "D Line Period", // property name
                    "Sets the d line period value", // property description
                    IndicatorProperties.class, // properties class
                    int.class, // property class
                    null, // property editor class (null if none)
                    "getPeriodD", // get method name
                    "setPeriodD", // set method name
                    IndicatorProperties.PERIOD_D // default property value
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
            // Slow or Fast
            set.put(getProperty(
                    "Fast", // property name
                    "Sets the fast or slow flag", // property description
                    IndicatorProperties.class, // properties class
                    boolean.class, // property class
                    null, // property editor class (null if none)
                    "getSF", // get method name
                    "setSF", // set method name
                    IndicatorProperties.SF // default property value
                    ));
            // D Line Color
            set.put(getProperty(
                    "D Line Color", // property name
                    "Sets the d line color", // property description
                    IndicatorProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getColorD", // get method name
                    "setColorD", // set method name
                    IndicatorProperties.COLOR_D // default property value
                    ));
            // D Line Style
            set.put(getProperty(
                    "D Line Style", // property name
                    "Sets the d line style", // property description
                    IndicatorProperties.class, // properties class
                    Stroke.class, // property class
                    StrokePropertyEditor.class, // property editor class (null if none)
                    "getStrokeD", // get method name
                    "setStrokeD", // set method name
                    StrokeGenerator.getStroke(IndicatorProperties.STROKE_INDEX_D) // default property value
                    ));
            // K Line Color
            set.put(getProperty(
                    "K Line Color", // property name
                    "Sets the k line color", // property description
                    IndicatorProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getColorK", // get method name
                    "setColorK", // set method name
                    IndicatorProperties.COLOR_K // default property value
                    ));
            // K Line Style
            set.put(getProperty(
                    "K Line Style", // property name
                    "Sets the k line style", // property description
                    IndicatorProperties.class, // properties class
                    Stroke.class, // property class
                    StrokePropertyEditor.class, // property editor class (null if none)
                    "getStrokeK", // get method name
                    "setStrokeK", // set method name
                    StrokeGenerator.getStroke(IndicatorProperties.STROKE_INDEX_K) // default property value
                    ));
        }
        catch (NoSuchMethodException ex)
        {
            LOG.log(Level.SEVERE, "[StochasticNode] : Method does not exist.", ex);
        }

        return sheet;
    }

}
