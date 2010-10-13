package org.chartsy.srift;

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

    public static final int RSI_PERIOD = 4;
    public static final int EMA_PERIOD = 4;
    public static final int SVE_PERIOD = 2;
    public static final String LABEL = "SRIFT";
    public static boolean MARKER = true;
    public static Color COLOR = new Color(0x4e9a06);
    public static int STROKE_INDEX = 0;

    private int rsiPeriod = RSI_PERIOD;
    private int emaPeriod = EMA_PERIOD;
    private int svePeriod = SVE_PERIOD;
    private String label = LABEL;
    private boolean marker = MARKER;
    private Color color = COLOR;
    private int strokeIndex = STROKE_INDEX;

	public IndicatorProperties() {}

    public int getRsiPeriod() { return rsiPeriod; }
    public void setRsiPeriod(int i) { rsiPeriod = i; }

	public int getEmaPeriod() { return emaPeriod; }
    public void setEmaPeriod(int i) { emaPeriod = i; }

	public int getSvePeriod() { return svePeriod; }
    public void setSvePeriod(int i) { svePeriod = i; }

    public String getLabel() { return label; }
    public void setLabel(String s) { label = s; }

    public boolean getMarker() { return marker; }
    public void setMarker(boolean b) { marker = b; }

    public Color getColor() { return color; }
    public void setColor(Color c) { color = c; }

    public int getStrokeIndex() { return strokeIndex; }
    public void setStrokeIndex(int i) { strokeIndex = i; }
    public Stroke getStroke() { return StrokeGenerator.getStroke(strokeIndex); }
    public void setStroke(Stroke s) { strokeIndex = StrokeGenerator.getStrokeIndex(s); }

}
