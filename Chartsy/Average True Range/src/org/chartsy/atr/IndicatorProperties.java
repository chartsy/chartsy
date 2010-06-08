package org.chartsy.atr;

import java.awt.Color;
import java.awt.Stroke;
import org.chartsy.main.chart.AbstractPropertyListener;
import org.chartsy.main.utils.SerialVersion;
import org.chartsy.main.utils.StrokeGenerator;

/**
 *
 * @author Viorel
 */
public class IndicatorProperties
        extends AbstractPropertyListener
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public static final int PERIOD = 14;
    public static final String LABEL = "ATR";
    public static final boolean MARKER = true;
    public static final Color COLOR = new Color(0x204a87);
    public static final int STROKE_INDEX = 0;

    private int period = PERIOD;
    private String label = LABEL;
    private boolean marker = MARKER;
    private Color color = COLOR;
    private int strokeIndex = STROKE_INDEX;

    public IndicatorProperties()
    {}

    /**
     * Get the value of period
     *
     * @return the value of period
     */
    public int getPeriod()
    {
        return period;
    }

    /**
     * Set the value of period
     *
     * @param int new value of period
     */
    public void setPeriod(int i)
    {
        period = i;
    }

    /**
     * Get the value of label
     *
     * @return the value of label
     */
    public String getLabel() 
    {
        return label;
    }

    /**
     * Set the value of label
     *
     * @param string new value of label
     */
    public void setLabel(String s)
    {
        label = s;
    }

    /**
     * Get the value of marker visibility
     *
     * @return the value of marker visibility
     */
    public boolean getMarker() 
    {
        return marker;
    }

    /**
     * Set the value of marker visibility
     *
     * @param boolean new value of marker visibility
     */
    public void setMarker(boolean b)
    {
        marker = b;
    }

    /**
     * Get the value of color
     *
     * @return the value of color
     */
    public Color getColor() 
    {
        return color;
    }

    /**
     * Set the value of color
     *
     * @param color new value of color
     */
    public void setColor(Color c)
    {
        color = c;
    }

    /**
     * Get the value of stroke index
     *
     * @return the value of stroke index
     */
    public int getStrokeIndex() 
    {
        return strokeIndex;
    }

    /**
     * Set the value of stroke index
     *
     * @param int new value of stroke index
     */
    public void setStrokeIndex(int i) 
    {
        strokeIndex = i;
    }

    /**
     * Get the value of stroke
     *
     * @return the value of stroke
     */
    public Stroke getStroke() 
    {
        return StrokeGenerator.getStroke(strokeIndex);
    }

    /**
     * Set the value of stroke index
     *
     * @param stroke to int new value of stroke index
     */
    public void setStroke(Stroke s)
    {
        strokeIndex = StrokeGenerator.getStrokeIndex(s);
    }

}
