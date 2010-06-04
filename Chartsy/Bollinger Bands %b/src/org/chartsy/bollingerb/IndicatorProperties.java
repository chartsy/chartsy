package org.chartsy.bollingerb;

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

    public static final int PERIOD = 18;
    public static final int STDPERIOD = 63;
    public static final double STDHIGH = 1.6;
    public static final double STDLOW = 1.6;
    public static final int TEMAPERIOD = 8;
    public static final String LABEL = "SVE_BB%b";
    public static final boolean MARKER = true;
    public static Color COLOR = new Color(0x204a87);
    public static final int STROKE_INDEX = 0;
    public static Color STD_COLOR = new Color(0xcc0000);
    public static final int STD_STROKE_INDEX = 1;

    private int period = PERIOD;
    private int stdPeriod = STDPERIOD;
    private double stdHigh = STDHIGH;
    private double stdLow = STDLOW;
    private int temaPeriod = TEMAPERIOD;
    private String label = LABEL;
    private boolean marker = MARKER;
    private Color color = COLOR;
    private int strokeIndex = STROKE_INDEX;
    private Color stdColor = STD_COLOR;
    private int stdStrokeIndex = STD_STROKE_INDEX;

    public IndicatorProperties() {}

    public int getPeriod() { return period; }
    public void setPeriod(int i) { period = i; }

    public int getStdPeriod() { return stdPeriod; }
    public void setStdPeriod(int i) { stdPeriod = i; }

    public double getStdHigh() { return stdHigh; }
    public void setStdHigh(double i) { stdHigh = i; }

    public double getStdLow() { return stdLow; }
    public void setStdLow(double i) { stdLow = i; }

    public int getTemaPeriod() { return temaPeriod; }
    public void setTemaPeriod(int i) { temaPeriod = i; }

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

    public Color getStdColor() { return stdColor; }
    public void setStdColor(Color c) { stdColor = c; }

    public int getStdStrokeIndex() { return stdStrokeIndex; }
    public void setStdStrokeIndex(int i) { stdStrokeIndex = i; }
    public Stroke getStdStroke() { return StrokeGenerator.getStroke(stdStrokeIndex); }
    public void setStdStroke(Stroke s) { stdStrokeIndex = StrokeGenerator.getStrokeIndex(s); }

}
