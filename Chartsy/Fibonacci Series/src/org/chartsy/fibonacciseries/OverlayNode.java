package org.chartsy.fibonacciseries;

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
public class OverlayNode extends AbstractPropertiesNode {

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public OverlayNode()
    {
        super("Fibonacci Series Properties");
    }

    public OverlayNode(OverlayProperties overlayProperties)
    {
        super("Fibonacci Series Properties", overlayProperties);
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
            // Starting Month
            set.put(getProperty(
                    "Starting Month", // property name
                    "Sets the starting month value", // property description
                    OverlayProperties.class, // properties class
                    int.class, // property class
                    null, // property editor class (null if none)
                    "getStartingMonth", // get method name
                    "setStartingMonth", // set method name
                    OverlayProperties.STARTING_MONTH // default property value
                    ));
            // Starting Day
            set.put(getProperty(
                    "Starting Day", // property name
                    "Sets the starting day value", // property description
                    OverlayProperties.class, // properties class
                    int.class, // property class
                    null, // property editor class (null if none)
                    "getStartingDay", // get method name
                    "setStartingDay", // set method name
                    OverlayProperties.STARTING_DAY // default property value
                    ));
            // Starting Year
            set.put(getProperty(
                    "Starting Year", // property name
                    "Sets the starting year value", // property description
                    OverlayProperties.class, // properties class
                    int.class, // property class
                    null, // property editor class (null if none)
                    "getStartingYear", // get method name
                    "setStartingYear", // set method name
                    OverlayProperties.STARTING_YEAR // default property value
                    ));
            // Starting Hour
            set.put(getProperty(
                    "Starting Hour", // property name
                    "Sets the starting hour value", // property description
                    OverlayProperties.class, // properties class
                    int.class, // property class
                    null, // property editor class (null if none)
                    "getStartingHour", // get method name
                    "setStartingHour", // set method name
                    OverlayProperties.STARTING_HOUR // default property value
                    ));
            // Starting Minute
            set.put(getProperty(
                    "Starting Minute", // property name
                    "Sets the starting minute value", // property description
                    OverlayProperties.class, // properties class
                    int.class, // property class
                    null, // property editor class (null if none)
                    "getStartingMinute", // get method name
                    "setStartingMinute", // set method name
                    OverlayProperties.STARTING_MINUTE // default property value
                    ));
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
            // Line Color
            set.put(getProperty(
                    "Line Color", // property name
                    "Sets the line color", // property description
                    OverlayProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getColor", // get method name
                    "setColor", // set method name
                    OverlayProperties.COLOR // default property value
                    ));
            // Line Style
            set.put(getProperty(
                    "Line Style", // property name
                    "Sets the line style", // property description
                    OverlayProperties.class, // properties class
                    Stroke.class, // property class
                    StrokePropertyEditor.class, // property editor class (null if none)
                    "getStroke", // get method name
                    "setStroke", // set method name
                    StrokeGenerator.getStroke(OverlayProperties.STROKE_INDEX) // default property value
                    ));
        }
        catch (NoSuchMethodException ex)
        {
            LOG.log(Level.SEVERE, "[FibonacciSeriesNode] : Method does not exist.", ex);
        }

        return sets;
    }

}
