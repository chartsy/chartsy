/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.chartsy.htsine;

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
        super("Hilbert Transform Sine Properties");
    }

    public IndicatorNode(IndicatorProperties indicatorProperties)
    {
        super("Hilbert Transform Sine Properties", indicatorProperties);
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
            // Sine Line Color
            set.put(getProperty(
                    "In Phase Line Color", // property name
                    "Sets the in phase line color", // property description
                    IndicatorProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getSineLineColor", // get method name
                    "setSineLineColor", // set method name
                    IndicatorProperties.SINE_LINE_COLOR // default property value
                    ));
            // Sine Line Style
            set.put(getProperty(
                    "Sine Line Style", // property name
                    "Sets the sine line style", // property description
                    IndicatorProperties.class, // properties class
                    Stroke.class, // property class
                    StrokePropertyEditor.class, // property editor class (null if none)
                    "getSineLineStroke", // get method name
                    "setSineLineStroke", // set method name
                    StrokeGenerator.getStroke(IndicatorProperties.SINE_LINE_STROKE_INDEX) // default property value
                    ));
            // Lead Sine Line Color
            set.put(getProperty(
                    "Lead Sine Line Color", // property name
                    "Sets the lead sine line color", // property description
                    IndicatorProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getLeadSineLineColor", // get method name
                    "setLeadSineLineColor", // set method name
                    IndicatorProperties.LEAD_SINE_LINE_COLOR // default property value
                    ));
            // Lead Sine Line Style
            set.put(getProperty(
                    "Lead Sine Line Style", // property name
                    "Sets the lead sine line style", // property description
                    IndicatorProperties.class, // properties class
                    Stroke.class, // property class
                    StrokePropertyEditor.class, // property editor class (null if none)
                    "getLeadSineLineStroke", // get method name
                    "setLeadSineLineStroke", // set method name
                    StrokeGenerator.getStroke(IndicatorProperties.LEAD_SINE_LINE_STROKE_INDEX) // default property value
                    ));

        }
        catch (NoSuchMethodException ex)
        {
            LOG.log(Level.SEVERE, "[Hilbert Transform Sine Node] : Method does not exist.", ex);
        }

        return sheet;
    }

}
