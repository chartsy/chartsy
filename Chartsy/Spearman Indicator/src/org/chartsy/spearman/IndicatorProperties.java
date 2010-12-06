package org.chartsy.spearman;

import java.awt.Color;
import java.awt.Stroke;
import org.chartsy.main.chart.AbstractPropertyListener;
import org.chartsy.main.utils.SerialVersion;
import org.chartsy.main.utils.StrokeGenerator;

/**
 *
 * @author Viorel
 */
public class IndicatorProperties extends AbstractPropertyListener
{

	private static final long serialVersionUID = SerialVersion.APPVERSION;

	public static final int PERIOD = 10;
	public static final int SMA_PERIOD = 3;
	public static final String LABEL = "Spearman";
	public static boolean MARKER = true;
	public static Color COLOR = new Color(0x204a87);
	public static Color SMA_COLOR = new Color(0xCC0000);
	public static int STROKE_INDEX = 0;
	public static int SMA_STROKE_INDEX = 0;

	private int period = PERIOD;
	private int smaPeriod = SMA_PERIOD;
	private String label = LABEL;
    private boolean marker = MARKER;
    private Color color = COLOR;
	private Color smaColor = SMA_COLOR;
    private int strokeIndex = STROKE_INDEX;
	private int smaStrokeIndex = SMA_STROKE_INDEX;

	public IndicatorProperties() {}

	public int getPeriod() { return period; }
    public void setPeriod(int i) { period = i; }

	public int getSMAPeriod() { return smaPeriod; }
    public void setSMAPeriod(int i) { smaPeriod = i; }

    public String getLabel() { return label; }
    public void setLabel(String s) { label = s; }

    public boolean getMarker() { return marker; }
    public void setMarker(boolean b) { marker = b; }

    public Color getColor() { return color; }
    public void setColor(Color c) { color = c; }

	public Color getSMAColor() { return smaColor; }
    public void setSMAColor(Color c) { smaColor = c; }

    public int getStrokeIndex() { return strokeIndex; }
    public void setStrokeIndex(int i) { strokeIndex = i; }
    public Stroke getStroke() { return StrokeGenerator.getStroke(strokeIndex); }
    public void setStroke(Stroke s) { strokeIndex = StrokeGenerator.getStrokeIndex(s); }

	public int getSMAStrokeIndex() { return smaStrokeIndex; }
    public void setSMAStrokeIndex(int i) { smaStrokeIndex = i; }
    public Stroke getSMAStroke() { return StrokeGenerator.getStroke(smaStrokeIndex); }
    public void setSMAStroke(Stroke s) { smaStrokeIndex = StrokeGenerator.getStrokeIndex(s); }

}
