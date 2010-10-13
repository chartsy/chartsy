/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.chartsy.mesa;

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
        super("MESA Properties");
    }

    public IndicatorNode(IndicatorProperties indicatorProperties)
    {
        super("MESA Properties", indicatorProperties);
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
            // Fast Limit
            set.put(getProperty(
                    "Fast Limit", // property name
                    "Sets the fast limit", // property description
                    IndicatorProperties.class, // properties class
                    double.class, // property class
                    null, // property editor class (null if none)
                    "getFastLimit", // get method name
                    "setFastLimit", // set method name
                    IndicatorProperties.FAST_LIMIT // default property value
                    ));
            // Slow Limit
            set.put(getProperty(
                    "Slow Limit", // property name
                    "Sets the slow limit", // property description
                    IndicatorProperties.class, // properties class
                    double.class, // property class
                    null, // property editor class (null if none)
                    "getSlowLimit", // get method name
                    "setSlowLimit", // set method name
                    IndicatorProperties.SLOW_LIMIT // default property value
                    ));
            // FAMA Line Color
            set.put(getProperty(
                    "FAMA Line Color", // property name
                    "Sets the FAMA line color", // property description
                    IndicatorProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getFamaLineColor", // get method name
                    "setFamaLineColor", // set method name
                    IndicatorProperties.FAMA_LINE_COLOR // default property value
                    ));
            // FAMA Line Style
            set.put(getProperty(
                    "FAMA Line Style", // property name
                    "Sets the FAMA line style", // property description
                    IndicatorProperties.class, // properties class
                    Stroke.class, // property class
                    StrokePropertyEditor.class, // property editor class (null if none)
                    "getFamaLineStroke", // get method name
                    "setFamaLineStroke", // set method name
                    StrokeGenerator.getStroke(IndicatorProperties.FAMA_LINE_STROKE_INDEX) // default property value
                    ));
            // MAMA Line Color
            set.put(getProperty(
                    "MAMA Line Color", // property name
                    "Sets the MAMA line color", // property description
                    IndicatorProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getMamaLineColor", // get method name
                    "setMamaLineColor", // set method name
                    IndicatorProperties.MAMA_LINE_COLOR // default property value
                    ));
            // MAMA Line Style
            set.put(getProperty(
                    "MAMA Line Style", // property name
                    "Sets the MAMA line style", // property description
                    IndicatorProperties.class, // properties class
                    Stroke.class, // property class
                    StrokePropertyEditor.class, // property editor class (null if none)
                    "getMamaLineStroke", // get method name
                    "setMamaLineStroke", // set method name
                    StrokeGenerator.getStroke(IndicatorProperties.MAMA_LINE_STROKE_INDEX) // default property value
                    ));

        }
        catch (NoSuchMethodException ex)
        {
            LOG.log(Level.SEVERE, "[MESA Node] : Method does not exist.", ex);
        }

        return sheet;
    }

}
