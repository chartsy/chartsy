/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.chartsy.parabolicsar;

import java.awt.Color;
import java.awt.Stroke;
import java.beans.PropertyEditorSupport;
import java.util.logging.Level;
import org.chartsy.main.chart.AbstractPropertiesNode;
import org.chartsy.main.utils.PricePropertyEditor;
import org.chartsy.main.utils.StrokeGenerator;
import org.chartsy.main.utils.StrokePropertyEditor;
import org.openide.nodes.Sheet;
import org.openide.nodes.Sheet.Set;

/**
 *
 * @author joshua.taylor
 */
public class OverlayNode extends AbstractPropertiesNode 
{

    private static final long serialVersionUID = 2L;

    public OverlayNode()
	{
        super("Parabolic SAR Properties");
    }

    public OverlayNode(OverlayProperties overlayProperties)
	{
        super("Parabolic SAR Properties", overlayProperties);
    }

    @Override
    protected Sheet createSheet() 
	{
		Sheet sheet = new Sheet();
        sheet.put(getSets()[0]);
        return sheet;
    }

	@Override
	public Set[] getSets()
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
                // Step
                    set.put(getProperty(
                    "Step", // property name
                    "Sets the step value", // property description
                    OverlayProperties.class, // properties class
                    double.class, // property class
                    null, // property editor class (null if none)
                    "getStep", // get method name
                    "setStep", // set method name
                    OverlayProperties.DEFAULT_STEP // default property value
                    ));
                // Max Step
                    set.put(getProperty(
                    "Max Step", // property name
                    "Sets the max step value", // property description
                    OverlayProperties.class, // properties class
                    double.class, // property class
                    null, // property editor class (null if none)
                    "getMaxStep", // get method name
                    "setMaxStep", // set method name
                    OverlayProperties.DEFAULT_MAX_STEP // default property value
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
                // Color
                    set.put(getProperty(
                    "Color", // property name
                    "Sets the color", // property description
                    OverlayProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getColor", // get method name
                    "setColor", // set method name
                    OverlayProperties.COLOR // default property value
                    ));
                // Line Style
                    set.put(getProperty(
                    "Style", // property name
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
            LOG.log(Level.SEVERE, "[ParabolicSARNode] : Method does not exist.", ex);
        }

        return sets;
	}

}
