package org.chartsy.vwap;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.LinkedHashMap;
import org.chartsy.main.ChartFrame;
import org.chartsy.main.chart.Overlay;
import org.chartsy.main.data.Dataset;
import org.chartsy.main.utils.SerialVersion;
import org.openide.nodes.AbstractNode;

/**
 *
 * @author Viorel
 */
public class VWAP extends Overlay
{

	private static final long serialVersionUID = SerialVersion.APPVERSION;

	private OverlayProperties properties;

	public VWAP()
	{
		super();
		properties = new OverlayProperties();
	}

	@Override public String getName()
	{
		return "Volume Weighted Average Price";
	}

	@Override public String getLabel()
	{
		return properties.getLabel();
	}

	@Override public Overlay newInstance()
	{
		return new VWAP();
	}

	@Override public LinkedHashMap getHTML(ChartFrame cf, int i)
	{
		return new LinkedHashMap();
	}

	@Override public void paint(Graphics2D g, ChartFrame cf, Rectangle bounds)
	{
		
	}

	@Override public void calculate()
	{
		
	}

	@Override public Color[] getColors()
	{
		return new Color[0];
	}

	@Override public double[] getValues(ChartFrame cf)
	{
		return new double[0];
	}

	@Override public double[] getValues(ChartFrame cf, int i)
	{
		return new double[0];
	}

	@Override public boolean getMarkerVisibility()
	{
		return properties.getMarker();
	}

	
	@Override public AbstractNode getNode()
	{
		return new OverlayNode(properties);
	}

	
	@Override public String getPrice()
	{
		return Dataset.CLOSE;
	}

}
