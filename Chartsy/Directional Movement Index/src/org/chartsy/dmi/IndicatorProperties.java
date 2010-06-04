package org.chartsy.dmi;

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

    public static final int PERIOD = 14;
    public static final String LABEL = "DMI";
    public static final boolean MARKER = true;
    public static final Color PDI_COLOR = new Color(0x5c3566);
    public static final Color MDI_COLOR = new Color(0x4e9a06);
    public static final Color ADX_COLOR = new Color(0x204a87);
    public static int PDI_STROKE_INDEX = 0;
    public static int MDI_STROKE_INDEX = 0;
    public static int ADX_STROKE_INDEX = 0;

    private int period = PERIOD;
    private String label = LABEL;
    private boolean marker = MARKER;
    private Color pdiColor = PDI_COLOR;
    private Color mdiColor = MDI_COLOR;
    private Color adxColor = ADX_COLOR;
    private int pdiStrokeIndex = PDI_STROKE_INDEX;
    private int mdiStrokeIndex = MDI_STROKE_INDEX;
    private int adxStrokeIndex = ADX_STROKE_INDEX;

    public IndicatorProperties() {}

    public int getPeriod() { return period; }
    public void setPeriod(int i) { period = i; }

    public String getLabel() { return label; }
    public void setLabel(String s) { label = s; }

    public boolean getMarker() { return marker; }
    public void setMarker(boolean b) { marker = b; }

    public Color getPDIColor() { return pdiColor; }
    public void setPDIColor(Color c) { pdiColor = c; }

    public Color getMDIColor() { return mdiColor; }
    public void setMDIColor(Color c) { mdiColor = c; }

    public Color getADXColor() { return adxColor; }
    public void setADXColor(Color c) { adxColor = c; }

    public int getPDIStrokeIndex() { return pdiStrokeIndex; }
    public void setPDIStrokeIndex(int i) { pdiStrokeIndex = i; }
    public Stroke getPDIStroke() { return StrokeGenerator.getStroke(pdiStrokeIndex); }
    public void setPDIStroke(Stroke s) { pdiStrokeIndex = StrokeGenerator.getStrokeIndex(s); }

    public int getMDIStrokeIndex() { return mdiStrokeIndex; }
    public void setMDIStrokeIndex(int i) { mdiStrokeIndex = i; }
    public Stroke getMDIStroke() { return StrokeGenerator.getStroke(mdiStrokeIndex); }
    public void setMDIStroke(Stroke s) { mdiStrokeIndex = StrokeGenerator.getStrokeIndex(s); }

    public int getADXStrokeIndex() { return adxStrokeIndex; }
    public void setADXStrokeIndex(int i) { adxStrokeIndex = i; }
    public Stroke getADXStroke() { return StrokeGenerator.getStroke(adxStrokeIndex); }
    public void setADXStroke(Stroke s) { adxStrokeIndex = StrokeGenerator.getStrokeIndex(s); }

}
