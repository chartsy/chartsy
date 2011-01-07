package org.chartsy.main.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.util.logging.Level;
import org.chartsy.main.ChartProperties;
import org.chartsy.main.chart.AbstractPropertiesNode;
import org.openide.nodes.Sheet;

/**
 *
 * @author viorel.gheba
 */
public class ChartNode
        extends AbstractPropertiesNode
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public ChartNode()
    {
        super("Chart Properties");
    }

    public ChartNode(ChartProperties chartProperties)
    {
        super("Chart Properties", chartProperties);
    }

    @SuppressWarnings("unchecked")
    protected @Override Sheet createSheet()
    {
        Sheet sheet = new Sheet();

        for (Sheet.Set set : getSets())
            sheet.put(set);

        return sheet;
    }

    public @Override Sheet.Set[] getSets()
    {
        Sheet.Set[] sets = new Sheet.Set[4];

        Sheet.Set window = getPropertiesSet(
                "Window Properties", // properties set name
                "Window Properties" // properties set description
                );
        sets[0] = window;

        Sheet.Set axis = getPropertiesSet(
                "Axis Properties", // properties set name
                "Axis Properties" // properties set description
                );
        sets[1] = axis;

        Sheet.Set data = getPropertiesSet(
                "Data Properties", // properties set name
                "Data Properties" // properties set description
                );
        sets[2] = data;

        Sheet.Set grid = getPropertiesSet(
                "Grid Properties", // properties set name
                "Grid Properties" // properties set description
                );
        sets[3] = grid;

        try
        {
            // Window Properties

            // Background Color
            window.put(getProperty(
                    "Background Color", // property name
                    "Sets the background color", // property description
                    ChartProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getBackgroundColor", // get method name
                    "setBackgroundColor", // set method name
                    ChartProperties.BACKGROUND_COLOR // default property value
                    ));
            // Font
            window.put(getProperty(
                    "Font", // property name
                    "Sets the font", // property description
                    ChartProperties.class, // properties class
                    Font.class, // property class
                    null, // property editor class (null if none)
                    "getFont", // get method name
                    "setFont", // set method name
                    ChartProperties.FONT // default property value
                    ));
            // Font Color
            window.put(getProperty(
                    "Font Color", // property name
                    "Sets the font color", // property description
                    ChartProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getFontColor", // get method name
                    "setFontColor", // set method name
                    ChartProperties.FONT_COLOR // default property value
                    ));

            // Asix Properties

            // Axis Color
            axis.put(getProperty(
                    "Axis Color", // property name
                    "Sets the axis line color", // property description
                    ChartProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getAxisColor", // get method name
                    "setAxisColor", // set method name
                    ChartProperties.AXIS_COLOR // default property value
                    ));
            // Axis Stroke
            axis.put(getProperty(
                    "Axis Style", // property name
                    "Sets the axis line style", // property description
                    ChartProperties.class, // properties class
                    Stroke.class, // property class
                    StrokePropertyEditor.class, // property editor class (null if none)
                    "getAxisStroke", // get method name
                    "setAxisStroke", // set method name
                    StrokeGenerator.getStroke(ChartProperties.AXIS_STROKE_INDEX) // default property value
                    ));
            // Logarithmic Flag
            axis.put(getProperty(
                    "Logarithmic Axis", // property name
                    "Sets the flag for logarithmic axis", // property description
                    ChartProperties.class, // properties class
                    boolean.class, // property class
                    null, // property editor class (null if none)
                    "getAxisLogarithmicFlag", // get method name
                    "setAxisLogarithmicFlag", // set method name
                    ChartProperties.AXIS_LOGARITHMIC_FLAG // default property value
                    ));

            // Data Properties

            // Bar Color
            data.put(getProperty(
                    "Bar Border Color", // property name
                    "Sets the bar border color", // property description
                    ChartProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getBarColor", // get method name
                    "setBarColor", // set method name
                    ChartProperties.BAR_COLOR // default property value
                    ));
            // Bar Stroke
            data.put(getProperty(
                    "Bar Border Style", // property name
                    "Sets the bar border line style", // property description
                    ChartProperties.class, // properties class
                    Stroke.class, // property class
                    StrokePropertyEditor.class, // property editor class (null if none)
                    "getBarStroke", // get method name
                    "setBarStroke", // set method name
                    StrokeGenerator.getStroke(ChartProperties.BAR_STROKE_INDEX) // default property value
                    ));
            // Bar Visibility
            data.put(getProperty(
                    "Bar Border Visibility", // property name
                    "Sets the bar border visibility", // property description
                    ChartProperties.class, // properties class
                    boolean.class, // property class
                    null, // property editor class (null if none)
                    "getBarVisibility", // get method name
                    "setBarVisibility", // set method name
                    ChartProperties.BAR_VISIBILITY // default property value
                    ));
            // Up Bar Color
            data.put(getProperty(
                    "Up Bar Color", // property name
                    "Sets the up bar color", // property description
                    ChartProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getBarUpColor", // get method name
                    "setBarUpColor", // set method name
                    ChartProperties.BAR_UP_COLOR // default property value
                    ));
            // Up Bar Visibility
            data.put(getProperty(
                    "Up Bar Visibility", // property name
                    "Sets the up bar visibility", // property description
                    ChartProperties.class, // properties class
                    boolean.class, // property class
                    null, // property editor class (null if none)
                    "getBarUpVisibility", // get method name
                    "setBarUpVisibility", // set method name
                    ChartProperties.BAR_UP_VISIBILITY // default property value
                    ));
            // Down Bar Color
            data.put(getProperty(
                    "Down Bar Color", // property name
                    "Sets the down bar color", // property description
                    ChartProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getBarDownColor", // get method name
                    "setBarDownColor", // set method name
                    ChartProperties.BAR_DOWN_COLOR // default property value
                    ));
            // Down Bar Visibility
            data.put(getProperty(
                    "Down Bar Visibility", // property name
                    "Sets the down bar visibility", // property description
                    ChartProperties.class, // properties class
                    boolean.class, // property class
                    null, // property editor class (null if none)
                    "getBarDownVisibility", // get method name
                    "setBarDownVisibility", // set method name
                    ChartProperties.BAR_DOWN_VISIBILITY // default property value
                    ));

            // Grid Properties

            // Horizontal Grid Color
            grid.put(getProperty(
                    "Horizontal Lines Color", // property name
                    "Sets the grid horizontal lines color", // property description
                    ChartProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getGridHorizontalColor", // get method name
                    "setGridHorizontalColor", // set method name
                    ChartProperties.GRID_HORIZONTAL_COLOR // default property value
                    ));
            // Horizontal Grid Stroke
            grid.put(getProperty(
                    "Horizontal Lines Style", // property name
                    "Sets the grid horizontal lines style", // property description
                    ChartProperties.class, // properties class
                    Stroke.class, // property class
                    StrokePropertyEditor.class, // property editor class (null if none)
                    "getGridHorizontalStroke", // get method name
                    "setGridHorizontalStroke", // set method name
                    StrokeGenerator.getStroke(ChartProperties.GRID_HORIZONTAL_STROKE_INDEX) // default property value
                    ));
            // Horizontal Grid Visibility
            grid.put(getProperty(
                    "Horizontal Lines Visibility", // property name
                    "Sets the grid horizontal lines visibility", // property description
                    ChartProperties.class, // properties class
                    boolean.class, // property class
                    null, // property editor class (null if none)
                    "getGridHorizontalVisibility", // get method name
                    "setGridHorizontalVisibility", // set method name
                    ChartProperties.GRID_HORIZONTAL_VISIBILITY // default property value
                    ));
            // Vertical Grid Color
            grid.put(getProperty(
                    "Vertical Lines Color", // property name
                    "Sets the grid vertical lines color", // property description
                    ChartProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getGridVerticalColor", // get method name
                    "setGridVerticalColor", // set method name
                    ChartProperties.GRID_VERTICAL_COLOR // default property value
                    ));
            // Vertical Grid Stroke
            grid.put(getProperty(
                    "Vertical Lines Style", // property name
                    "Sets the grid vertical lines style", // property description
                    ChartProperties.class, // properties class
                    Stroke.class, // property class
                    StrokePropertyEditor.class, // property editor class (null if none)
                    "getGridVerticalStroke", // get method name
                    "setGridVerticalStroke", // set method name
                    StrokeGenerator.getStroke(ChartProperties.GRID_VERTICAL_STROKE_INDEX) // default property value
                    ));
            // Vertical Grid Visibility
            grid.put(getProperty(
                    "Vertical Lines Visibility", // property name
                    "Sets the grid vertical lines visibility", // property description
                    ChartProperties.class, // properties class
                    boolean.class, // property class
                    null, // property editor class (null if none)
                    "getGridVerticalVisibility", // get method name
                    "setGridVerticalVisibility", // set method name
                    ChartProperties.GRID_VERTICAL_VISIBILITY // default property value
                    ));
        }
        catch (NoSuchMethodException ex)
        {
            LOG.log(Level.SEVERE, "[ChartNode] : Method does not exist.", ex);
        }

        return sets;
    }

}
