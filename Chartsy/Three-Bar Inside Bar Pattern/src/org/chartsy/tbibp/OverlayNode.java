package org.chartsy.tbibp;

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
        super("Three-Bar Inside Bar Pattern Properties");
    }

    public OverlayNode(OverlayProperties overlayProperties)
	{
        super("Three-Bar Inside Bar Pattern Properties", overlayProperties);
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
        }
		catch (NoSuchMethodException ex)
        {
            LOG.log(Level.SEVERE, "[Three-Bar_Inside_Bar_Pattern_Node] : Method does not exist.", ex);
        }

        return sets;
	}

}
