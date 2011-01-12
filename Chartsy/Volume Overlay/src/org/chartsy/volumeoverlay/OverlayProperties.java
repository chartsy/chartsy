package org.chartsy.volumeoverlay;

import java.awt.Color;
import java.awt.Stroke;
import org.chartsy.main.chart.AbstractPropertyListener;
import org.chartsy.main.utils.SerialVersion;
import org.chartsy.main.utils.StrokeGenerator;

public class OverlayProperties
        extends AbstractPropertyListener
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public static final String LABEL = "Volume";
    public static final Color COLOR = new Color(0xf57900);
    public static final int ALPHA = 128;

	public static final int SMA_PERIOD = 5;
	public static final Color SMA_COLOR = Color.BLUE;
	public static final int SMA_STROKE_INDEX = 0;

    private String label = LABEL;
    private Color color = COLOR;
    private int alpha = ALPHA;

	private int smaPeriod = SMA_PERIOD;
	private Color smaColor = SMA_COLOR;
	private int smaStrokeIndex = SMA_STROKE_INDEX;

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

	public int getSmaPeriod() { return smaPeriod; }
	public void setSmaPeriod(int i) { smaPeriod = i; }

	public Color getSmaColor() { return smaColor; }
	public void setSmaColor(Color c) { smaColor = c; }

	public int getSmaStrokeIndex() { return smaStrokeIndex; }
    public void setSmaStrokeIndex(int i) { smaStrokeIndex = i; }
    public Stroke getSmaStroke() { return StrokeGenerator.getStroke(smaStrokeIndex); }
    public void setSmaStroke(Stroke s) { smaStrokeIndex = StrokeGenerator.getStrokeIndex(s); }
    
}
