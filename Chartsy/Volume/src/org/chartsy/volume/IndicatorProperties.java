package org.chartsy.volume;

import java.awt.Color;
import java.awt.Stroke;
import org.chartsy.main.chart.AbstractPropertyListener;
import org.chartsy.main.utils.SerialVersion;
import org.chartsy.main.utils.StrokeGenerator;

/**
 *
 * @author viorel.gheba
 */
public class IndicatorProperties 
        extends AbstractPropertyListener
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public static final String LABEL = "Volume";
    public static final boolean MARKER = true;
    public static final Color ZERO_LINE_COLOR = new Color(0xeeeeec);
    public static final int ZERO_LINE_STROKE = 0;
    public static final boolean ZERO_LINE_VISIBILITY = true;
    public static final Color COLOR = new Color(0xf57900);

	public static final int SMA_PERIOD = 5;
	public static final Color SMA_COLOR = Color.BLUE;
	public static final int SMA_STROKE_INDEX = 0;

    private String label = LABEL;
    private boolean marker = MARKER;
    private Color zeroLineColor = ZERO_LINE_COLOR;
    private int zeroLineStrokeIndex = ZERO_LINE_STROKE;
    private boolean zeroLineVisibility = ZERO_LINE_VISIBILITY;
    private Color color = COLOR;

	private int smaPeriod = SMA_PERIOD;
	private Color smaColor = SMA_COLOR;
	private int smaStrokeIndex = SMA_STROKE_INDEX;

    public IndicatorProperties() {}

    public String getLabel() { return label; }
    public void setLabel(String s) { label = s; }

    public boolean getMarker() { return marker; }
    public void setMarker(boolean b) { marker = b; }

    public Color getZeroLineColor() { return zeroLineColor; }
    public void setZeroLineColor(Color c) { zeroLineColor = c; }

    public int getZeroLineStrokeIndex() { return zeroLineStrokeIndex; }
    public void setZeroLineStrokeIndex(int i) { zeroLineStrokeIndex = i; }
    public Stroke getZeroLineStroke() { return StrokeGenerator.getStroke(zeroLineStrokeIndex); }
    public void setZeroLineStroke(Stroke s) { zeroLineStrokeIndex = StrokeGenerator.getStrokeIndex(s); }

    public boolean getZeroLineVisibility() { return zeroLineVisibility; }
    public void setZeroLineVisibility(boolean b) { zeroLineVisibility = b; }

    public Color getColor() { return color; }
    public void setColor(Color c) { color = c; }

	public int getSmaPeriod() { return smaPeriod; }
	public void setSmaPeriod(int i) { smaPeriod = i; }

	public Color getSmaColor() { return smaColor; }
	public void setSmaColor(Color c) { smaColor = c; }

	public int getSmaStrokeIndex() { return smaStrokeIndex; }
    public void setSmaStrokeIndex(int i) { smaStrokeIndex = i; }
    public Stroke getSmaStroke() { return StrokeGenerator.getStroke(smaStrokeIndex); }
    public void setSmaStroke(Stroke s) { smaStrokeIndex = StrokeGenerator.getStrokeIndex(s); }

}
