package org.chartsy.tbibp;

import org.chartsy.main.chart.AbstractPropertyListener;
import org.chartsy.main.utils.SerialVersion;

/**
 *
 * @author Viorel
 */
public class OverlayProperties extends AbstractPropertyListener
{

	private static final long serialVersionUID = SerialVersion.APPVERSION;

	public static final String LABEL = "Three-Bar Inside Bar Pattern";

	private String label = LABEL;

	public OverlayProperties()
	{
	}

	public String getLabel()
	{
		return label;
	}

	public void setLabel(String s)
	{
		label = s;
	}

}
