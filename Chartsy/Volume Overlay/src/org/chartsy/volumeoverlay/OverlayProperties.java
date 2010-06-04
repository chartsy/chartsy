package org.chartsy.volumeoverlay;

import java.awt.Color;
import org.chartsy.main.chart.AbstractPropertyListener;
import org.chartsy.main.utils.SerialVersion;

public class OverlayProperties
        extends AbstractPropertyListener
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public static final String LABEL = "Volume";
    public static final Color COLOR = new Color(0xf57900);
    public static final int ALPHA = 128;

    private String label = LABEL;
    private Color color = COLOR;
    private int alpha = ALPHA;

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

    public Color getColor()
    {
        return color;
    }

    public void setColor(Color c)
    {
        color = c;
    }

    public Integer getAlpha()
    {
        return alpha;
    }

    public void setAlpha(Integer a)
    {
        alpha = a;
    }
    
}
