package org.chartsy.volume;

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
        super("Volume Properties");
    }

    public IndicatorNode(IndicatorProperties indicatorProperties)
    {
        super("Volume Properties", indicatorProperties);
    }

    @SuppressWarnings("unchecked")
    protected @Override Sheet createSheet()
    {
        Sheet sheet = Sheet.createDefault();
        
		Sheet.Set set = getPropertiesSet();
		Sheet.Set smaSet = getPropertiesSet("SMA Properties");

        sheet.put(set);
		sheet.put(smaSet);

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
            // Bars Color
            set.put(getProperty(
                    "Bars Color", // property name
                    "Sets the bars color", // property description
                    IndicatorProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getColor", // get method name
                    "setColor", // set method name
                    IndicatorProperties.COLOR // default property value
                    ));
			// SMA Period
            smaSet.put(getProperty(
                    "SMA Period", // property name
                    "Sets the SMA period value", // property description
                    IndicatorProperties.class, // properties class
                    int.class, // property class
                    null, // property editor class (null if none)
                    "getSmaPeriod", // get method name
                    "setSmaPeriod", // set method name
                    IndicatorProperties.SMA_PERIOD // default property value
                    ));
			// SMA Line Color
            smaSet.put(getProperty(
                    "SMA Line Color", // property name
                    "Sets the sma line color", // property description
                    IndicatorProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getSmaColor", // get method name
                    "setSmaColor", // set method name
                    IndicatorProperties.SMA_COLOR // default property value
                    ));
			// SMA Line Style
            smaSet.put(getProperty(
                    "SMA Line Style", // property name
                    "Sets the sma line style", // property description
                    IndicatorProperties.class, // properties class
                    Stroke.class, // property class
                    StrokePropertyEditor.class, // property editor class (null if none)
                    "getSmaStroke", // get method name
                    "setSmaStroke", // set method name
                    StrokeGenerator.getStroke(IndicatorProperties.SMA_STROKE_INDEX) // default property value
                    ));
        }
        catch (NoSuchMethodException ex)
        {
            LOG.log(Level.SEVERE, "[VolumeIndicatorNode] : Method does not exist.", ex);
        }

        return sheet;
    }

}
