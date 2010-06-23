/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.chartsy.bop;

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
        super("BOP Properties");
    }

    public IndicatorNode(IndicatorProperties indicatorProperties)
    {
        super("BOP Properties", indicatorProperties);
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
        }
        catch (NoSuchMethodException ex)
        {
            LOG.log(Level.SEVERE, "[BOP Node] : Method does not exist.", ex);
        }

        return sheet;
    }

}
