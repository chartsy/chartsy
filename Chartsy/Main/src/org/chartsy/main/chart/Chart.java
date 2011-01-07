package org.chartsy.main.chart;

import java.awt.Graphics2D;
import java.io.Serializable;
import org.chartsy.main.ChartFrame;
import org.chartsy.main.utils.SerialVersion;

/**
 *
 * @author viorel.gheba
 */
public abstract class Chart
        implements Serializable
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public Chart()
	{
	}

    public abstract String getName();
    public abstract void paint(Graphics2D g, ChartFrame cf);

}
