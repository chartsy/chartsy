package org.chartsy.tworsi;

import java.awt.Color;
import java.beans.PropertyEditorSupport;
import java.util.logging.Level;
import org.chartsy.main.chart.AbstractPropertiesNode;
import org.chartsy.main.utils.SerialVersion;
import org.openide.nodes.Sheet;
import org.openide.nodes.Sheet.Set;

/**
 *
 * @author Viorel
 */
public class OverlayNode extends AbstractPropertiesNode
{

	private static final long serialVersionUID = SerialVersion.APPVERSION;

	public OverlayNode()
	{
        super("RSI with RSI Properties");
    }

    public OverlayNode(OverlayProperties overlayProperties)
	{
        super("RSI with RSI Properties", overlayProperties);
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
			// Buy Color
            set.put(getProperty(
                    "Buy Color", // property name
                    "Sets the buy color", // property description
                    OverlayProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getBuyColor", // get method name
                    "setBuyColor", // set method name
                    OverlayProperties.BUY_COLOR // default property value
                    ));
			// Sell Color
            set.put(getProperty(
                    "Sell Color", // property name
                    "Sets the sell color", // property description
                    OverlayProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getSellColor", // get method name
                    "setSellColor", // set method name
                    OverlayProperties.SELL_COLOR // default property value
                    ));
			// Slow RSI Period
            set.put(getProperty(
                    "Slow RSI Period", // property name
                    "Sets the slow RSI period", // property description
                    OverlayProperties.class, // properties class
                    int.class, // property class
                    null, // property editor class (null if none)
                    "getSlowRsiPeriod", // get method name
                    "setSlowRsiPeriod", // set method name
                    OverlayProperties.S_RSI_PERIOD // default property value
                    ));
			// Quick RSI Period
            set.put(getProperty(
                    "Quick RSI Period", // property name
                    "Sets the quick RSI period", // property description
                    OverlayProperties.class, // properties class
                    int.class, // property class
                    null, // property editor class (null if none)
                    "getQuickRsiPeriod", // get method name
                    "setQuickRsiPeriod", // set method name
                    OverlayProperties.Q_RSI_PERIOD // default property value
                    ));
			// Slow MA Period
            set.put(getProperty(
                    "Slow MA Period", // property name
                    "Sets the slow MA period", // property description
                    OverlayProperties.class, // properties class
                    int.class, // property class
                    null, // property editor class (null if none)
                    "getSlowMaPeriod", // get method name
                    "setSlowMaPeriod", // set method name
                    OverlayProperties.S_MA_PERIOD // default property value
                    ));
			// Quick MA Period
            set.put(getProperty(
                    "Quick MA Period", // property name
                    "Sets the quick MA period", // property description
                    OverlayProperties.class, // properties class
                    int.class, // property class
                    null, // property editor class (null if none)
                    "getQuickMaPeriod", // get method name
                    "setQuickMaPeriod", // set method name
                    OverlayProperties.Q_MA_PERIOD // default property value
                    ));
        }
		catch (NoSuchMethodException ex)
        {
            LOG.log(Level.SEVERE, "[RSI_with_RSI_Node] : Method does not exist.", ex);
        }

        return sets;
	}

}
