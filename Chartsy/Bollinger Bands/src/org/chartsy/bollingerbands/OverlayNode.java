package org.chartsy.bollingerbands;

import java.awt.Color;
import java.awt.Stroke;
import java.beans.PropertyEditorSupport;
import java.util.logging.Level;
import org.chartsy.main.chart.AbstractPropertiesNode;
import org.chartsy.main.utils.AlphaPropertyEditor;
import org.chartsy.main.utils.PricePropertyEditor;
import org.chartsy.main.utils.SerialVersion;
import org.chartsy.main.utils.StrokeGenerator;
import org.chartsy.main.utils.StrokePropertyEditor;
import org.openide.nodes.Sheet;

/**
 *
 * @author viorel.gheba
 */
public class OverlayNode 
        extends AbstractPropertiesNode
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public OverlayNode()
    {
        super("Bollinger Bands Properties");
    }

    public OverlayNode(OverlayProperties overlayProperties)
    {
        super("Bollinger Bands Properties", overlayProperties);
    }

    @SuppressWarnings("unchecked")
    protected @Override Sheet createSheet()
    {
        Sheet sheet = new Sheet();
        sheet.put(getSets()[0]);
        return sheet;
    }

    public @Override Sheet.Set[] getSets()
    {
        Sheet.Set[] sets = new Sheet.Set[1];
        Sheet.Set set = getPropertiesSet();
        sets[0] = set;

        try
        {
            // Label
            set.put(getProperty(
                    "Label", // property name
                    "Sets the label", // property description
                    OverlayProperties.class, // properties class
                    String.class, // property class
                    PropertyEditorSupport.class, // property editor class (null if none)
                    "getLabel", // get method name
                    "setLabel", // set method name
                    OverlayProperties.LABEL // default property value
                    ));
            // Marker Visibility
            set.put(getProperty(
                    "Marker Visibility", // property name
                    "Sets the marker visibility", // property description
                    OverlayProperties.class, // properties class
                    boolean.class, // property class
                    null, // property editor class (null if none)
                    "getMarker", // get method name
                    "setMarker", // set method name
                    OverlayProperties.MARKER // default property value
                    ));
            // # x Std. Dev.
            set.put(getProperty(
                    "# x Std. Dev.", // property name
                    "Sets the std. dev. value", // property description
                    OverlayProperties.class, // properties class
                    int.class, // property class
                    null, // property editor class (null if none)
                    "getStd", // get method name
                    "setStd", // set method name
                    OverlayProperties.STD // default property value
                    ));
            // Period
            set.put(getProperty(
                    "Period", // property name
                    "Sets the period value", // property description
                    OverlayProperties.class, // properties class
                    int.class, // property class
                    null, // property editor class (null if none)
                    "getPeriod", // get method name
                    "setPeriod", // set method name
                    OverlayProperties.PERIOD // default property value
                    ));
            // Price
            set.put(getProperty(
                    "Price", // property name
                    "Sets the price type", // property description
                    OverlayProperties.class, // properties class
                    String.class, // property class
                    PricePropertyEditor.class, // property editor class (null if none)
                    "getPrice", // get method name
                    "setPrice", // set method name
                    OverlayProperties.PRICE // default property value
                    ));
            // Lower Line Color
            set.put(getProperty(
                    "Lower Line Color", // property name
                    "Sets the lower line color", // property description
                    OverlayProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getLowerColor", // get method name
                    "setLowerColor", // set method name
                    OverlayProperties.LOWER_COLOR // default property value
                    ));
            // Lower Line Style
            set.put(getProperty(
                    "Lower Line Style", // property name
                    "Sets the lower line style", // property description
                    OverlayProperties.class, // properties class
                    Stroke.class, // property class
                    StrokePropertyEditor.class, // property editor class (null if none)
                    "getLowerStroke", // get method name
                    "setLowerStroke", // set method name
                    StrokeGenerator.getStroke(OverlayProperties.LOWER_STROKE_INDEX) // default property value
                    ));
            // Middle Line Color
            set.put(getProperty(
                    "Middle Line Color", // property name
                    "Sets the middle line color", // property description
                    OverlayProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getMiddleColor", // get method name
                    "setMiddleColor", // set method name
                    OverlayProperties.MIDDLE_COLOR // default property value
                    ));
            // Middle Line Stroke
            set.put(getProperty(
                    "Middle Line Style", // property name
                    "Sets the middle line style", // property description
                    OverlayProperties.class, // properties class
                    Stroke.class, // property class
                    StrokePropertyEditor.class, // property editor class (null if none)
                    "getMiddleStroke", // get method name
                    "setMiddleStroke", // set method name
                    StrokeGenerator.getStroke(OverlayProperties.MIDDLE_STROKE_INDEX) // default property value
                    ));
            // Upper Line Color
            set.put(getProperty(
                    "Upper Line Color", // property name
                    "Sets the upper line color", // property description
                    OverlayProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getUpperColor", // get method name
                    "setUpperColor", // set method name
                    OverlayProperties.UPPER_COLOR // default property value
                    ));
            // Upper Line Stroke
            set.put(getProperty(
                    "Upper Line Style", // property name
                    "Sets the upper line style", // property description
                    OverlayProperties.class, // properties class
                    Stroke.class, // property class
                    StrokePropertyEditor.class, // property editor class (null if none)
                    "getUpperStroke", // get method name
                    "setUpperStroke", // set method name
                    StrokeGenerator.getStroke(OverlayProperties.UPPER_STROKE_INDEX) // default property value
                    ));
            // Inside Color
            set.put(getProperty(
                    "Inside Color", // property name
                    "Sets the inside color", // property description
                    OverlayProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getInsideColor", // get method name
                    "setInsideColor", // set method name
                    OverlayProperties.INSIDE_COLOR // default property value
                    ));
            // Inside Alpha
            set.put(getProperty(
                    "Inside Alpha", // property name
                    "Sets the inside alpha value", // property description
                    OverlayProperties.class, // properties class
                    int.class, // property class
                    AlphaPropertyEditor.class, // property editor class (null if none)
                    "getInsideAlpha", // get method name
                    "setInsideAlpha", // set method name
                    OverlayProperties.INSIDE_ALPHA // default property value
                    ));
            // Inside Visibility
            set.put(getProperty(
                    "Inside Visibility", // property name
                    "Sets the inside visibility flag", // property description
                    OverlayProperties.class, // properties class
                    boolean.class, // property class
                    null, // property editor class (null if none)
                    "getInsideVisibility", // get method name
                    "setInsideVisibility", // set method name
                    OverlayProperties.INSIDE_VISIBILITY // default property value
                    ));
        }
        catch (NoSuchMethodException ex)
        {
            LOG.log(Level.SEVERE, "[Bollinger Bands Node] : Method does not exist.", ex);
        }

        return sets;
    }

}
