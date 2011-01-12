package org.chartsy.vwap;

import java.awt.Color;
import java.beans.PropertyEditorSupport;
import java.util.logging.Level;
import org.chartsy.main.chart.AbstractPropertiesNode;
import org.chartsy.main.utils.PricePropertyEditor;
import org.chartsy.main.utils.SerialVersion;
import org.openide.nodes.Sheet;

/**
 *
 * @author Viorel
 */
public class OverlayNode extends AbstractPropertiesNode
{

	private static final long serialVersionUID = SerialVersion.APPVERSION;

	public OverlayNode()
    {
        super("Volume Weighted Average Price");
    }

    public OverlayNode(OverlayProperties overlayProperties)
    {
        super("Volume Weighted Average Price", overlayProperties);
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
			// Color
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
			// Band 1 Visibility
            set.put(getProperty(
                    "Band 1 Visibility", // property name
                    "Sets the band 1 visibility", // property description
                    OverlayProperties.class, // properties class
                    boolean.class, // property class
                    null, // property editor class (null if none)
                    "getBand1Visibility", // get method name
                    "setBand1Visibility", // set method name
                    OverlayProperties.BAND1_VISIBILITY // default property value
                    ));
			// Band 1 Color
            set.put(getProperty(
                    "Band 1 Color", // property name
                    "Sets the band 1 color", // property description
                    OverlayProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getBand1Color", // get method name
                    "setBand1Color", // set method name
                    OverlayProperties.BAND1_COLOR // default property value
                    ));
			// Band 1 Dev
            set.put(getProperty(
                    "Band 1 Dev", // property name
                    "Sets the band 1 dev value", // property description
                    OverlayProperties.class, // properties class
                    int.class, // property class
                    null, // property editor class (null if none)
                    "getBand1Dev", // get method name
                    "setBand1Dev", // set method name
                    OverlayProperties.BAND1_DEV // default property value
                    ));
			// Band 2 Visibility
            set.put(getProperty(
                    "Band 2 Visibility", // property name
                    "Sets the band 2 visibility", // property description
                    OverlayProperties.class, // properties class
                    boolean.class, // property class
                    null, // property editor class (null if none)
                    "getBand2Visibility", // get method name
                    "setBand2Visibility", // set method name
                    OverlayProperties.BAND2_VISIBILITY // default property value
                    ));
			// Band 2 Color
            set.put(getProperty(
                    "Band 2 Color", // property name
                    "Sets the band 2 color", // property description
                    OverlayProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getBand2Color", // get method name
                    "setBand2Color", // set method name
                    OverlayProperties.BAND2_COLOR // default property value
                    ));
			// Band 2 Dev
            set.put(getProperty(
                    "Band 2 Dev", // property name
                    "Sets the band 2 dev value", // property description
                    OverlayProperties.class, // properties class
                    int.class, // property class
                    null, // property editor class (null if none)
                    "getBand2Dev", // get method name
                    "setBand2Dev", // set method name
                    OverlayProperties.BAND2_DEV // default property value
                    ));
			// Band 3 Visibility
            set.put(getProperty(
                    "Band 3 Visibility", // property name
                    "Sets the band 3 visibility", // property description
                    OverlayProperties.class, // properties class
                    boolean.class, // property class
                    null, // property editor class (null if none)
                    "getBand3Visibility", // get method name
                    "setBand3Visibility", // set method name
                    OverlayProperties.BAND3_VISIBILITY // default property value
                    ));
			// Band 3 Color
            set.put(getProperty(
                    "Band 3 Color", // property name
                    "Sets the band 3 color", // property description
                    OverlayProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getBand3Color", // get method name
                    "setBand3Color", // set method name
                    OverlayProperties.BAND3_COLOR // default property value
                    ));
			// Band 3 Dev
            set.put(getProperty(
                    "Band 3 Dev", // property name
                    "Sets the band 3 dev value", // property description
                    OverlayProperties.class, // properties class
                    int.class, // property class
                    null, // property editor class (null if none)
                    "getBand3Dev", // get method name
                    "setBand3Dev", // set method name
                    OverlayProperties.BAND3_DEV // default property value
                    ));
        }
        catch (NoSuchMethodException ex)
        {
            LOG.log(Level.SEVERE, "[VWAPNode] : Method does not exist.", ex);
        }

        return sets;
    }

}
