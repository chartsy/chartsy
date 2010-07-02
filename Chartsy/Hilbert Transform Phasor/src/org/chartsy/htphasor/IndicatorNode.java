/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.chartsy.htphasor;

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
        super("Hilbert Transform Phasor Properties");
    }

    public IndicatorNode(IndicatorProperties indicatorProperties)
    {
        super("Hilbert Transform Phasor Properties", indicatorProperties);
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
            // In Phase Line Color
            set.put(getProperty(
                    "In Phase Line Color", // property name
                    "Sets the in phase line color", // property description
                    IndicatorProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getInPhaseLineColor", // get method name
                    "setInPhaseLineColor", // set method name
                    IndicatorProperties.IN_PHASE_LINE_COLOR // default property value
                    ));
            // In Phase Line Style
            set.put(getProperty(
                    "In Phase Line Style", // property name
                    "Sets the in phase line style", // property description
                    IndicatorProperties.class, // properties class
                    Stroke.class, // property class
                    StrokePropertyEditor.class, // property editor class (null if none)
                    "getInPhaseLineStroke", // get method name
                    "setInPhaseLineStroke", // set method name
                    StrokeGenerator.getStroke(IndicatorProperties.IN_PHASE_LINE_STROKE_INDEX) // default property value
                    ));
            // Quadrature Line Color
            set.put(getProperty(
                    "Quadrature Line Color", // property name
                    "Sets the quadrature line color", // property description
                    IndicatorProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getQuadratureLineColor", // get method name
                    "setQuadratureLineColor", // set method name
                    IndicatorProperties.QUADRATURE_LINE_COLOR // default property value
                    ));
            // Quadrature Line Style
            set.put(getProperty(
                    "Quadrature Line Style", // property name
                    "Sets the quadrature line style", // property description
                    IndicatorProperties.class, // properties class
                    Stroke.class, // property class
                    StrokePropertyEditor.class, // property editor class (null if none)
                    "getQuadratureLineStroke", // get method name
                    "setQuadratureLineStroke", // set method name
                    StrokeGenerator.getStroke(IndicatorProperties.QUADRATURE_LINE_STROKE_INDEX) // default property value
                    ));

        }
        catch (NoSuchMethodException ex)
        {
            LOG.log(Level.SEVERE, "[Hilbert Transform Phasor Node] : Method does not exist.", ex);
        }

        return sheet;
    }


}
