package org.chartsy.tbibp;

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
public class ThreeBarInsideBarPattern extends Overlay
{

	private static final long serialVersionUID = SerialVersion.APPVERSION;

	private OverlayProperties properties;

	public ThreeBarInsideBarPattern()
	{
		super();
		properties = new OverlayProperties();
	}

	@Override
	public Overlay newInstance()
	{
		return new ThreeBarInsideBarPattern();
	}

	@Override
	public String getName()
	{
		return properties.getLabel();
	}

	@Override
	public String getLabel()
	{
		return properties.getLabel();
	}

	@Override
	public Color[] getColors()
	{
		return new Color[0];
	}

	@Override
	public double[] getValues(ChartFrame cf)
	{
		return new double[0];
	}

	@Override
	public double[] getValues(ChartFrame cf, int i)
	{
		return new double[0];
	}

	@Override
	public boolean getMarkerVisibility()
	{
		return false;
	}

	@Override
	public AbstractNode getNode()
	{
		return new OverlayNode(properties);
	}

	@Override
	public String getPrice()
	{
		return Dataset.CLOSE;
	}

	@Override
	public LinkedHashMap getHTML(ChartFrame cf, int i)
	{
		LinkedHashMap ht = new LinkedHashMap();
		ht.put(getLabel(), " ");
		return ht;
	}

	@Override
	public void paint(Graphics2D g, ChartFrame cf, Rectangle bounds)
	{
		Dataset dataset = getDataset();
		int count = dataset.getItemsCount();

		boolean[] c1 = new boolean[count];
		boolean[] c2 = new boolean[count];
		boolean[] c3 = new boolean[count];

		double tr = 0.75, sl = 0.75;
		int marketposition = 0;
		int mp = marketposition;

		for (int i = 0; i < 2; i++)
		{
			c1[i] = dataset.getCloseAt(i) > dataset.getCloseAt(i-1); // condition1
			c2[i] = (dataset.getHighAt(i) < dataset.getHighAt(i-1))
				&& (dataset.getLowAt(i) > dataset.getLowAt(i-1)); // condition2
			c3[i] = dataset.getCloseAt(i) < dataset.getCloseAt(i-1); // condition3
		}
		
		for (int i = 2; i < count; i++)
		{
			c1[i] = dataset.getCloseAt(i) > dataset.getCloseAt(i-1); // condition1
			c2[i] = (dataset.getHighAt(i) < dataset.getHighAt(i-1))
				&& (dataset.getLowAt(i) > dataset.getLowAt(i-1)); // condition2
			c3[i] = dataset.getCloseAt(i) < dataset.getCloseAt(i-1); // condition3

			if (mp == 0)
			{
				if (c1[i] && c2[i-1] && c1[i-2])
				{
					// buy next bar at open
				}
				if (c3[i] && c2[i-1] && c3[i-2])
				{
					// sell short next bar at open
				}
			}

			if (marketposition == 1)
			{
				// sell next bar at entryprice+(entryprice*tr/100) Limit
				// sell next bar at entryprice-(entryprice*sl/100) stop
			}

			if (marketposition == -1)
			{
				// buy to cover next bar at entryprice-(entryprice*tr/100) Limit
				// buy to cover next bar at entryprice+(entryprice*sl/100) stop
			}
		}
	}

	@Override
	public void calculate()
	{
		
	}

}
