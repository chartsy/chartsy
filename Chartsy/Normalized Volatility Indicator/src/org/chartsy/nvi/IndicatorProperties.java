package org.chartsy.nvi;

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

    public static final int PERIOD = 64;
    public static final String LABEL = "NVI";
    public static final boolean MARKER = true;
    public static final Color COLOR = new Color(0x000000);
    public static final int STROKE_INDEX = 0;
    public static final int MA65_PERIOD = 65;
    public static final Color MA65_COLOR = new Color(0x204a87);
    public static final int MA65_STROKE_INDEX = 0;
    public static final boolean MA65_VISIBILITY = true;
    public static final int MA200_PERIOD = 200;
    public static final Color MA200_COLOR = new Color(0xf57900);
    public static final int MA200_STROKE_INDEX = 0;
    public static final boolean MA200_VISIBILITY = true;

    private int period = PERIOD;
    private String label = LABEL;
    private boolean marker = MARKER;
    private Color color = COLOR;
    private int strokeIndex = STROKE_INDEX;
    private int ma65period = MA65_PERIOD;
    private Color ma65color = MA65_COLOR;
    private int ma65strokeIndex = MA65_STROKE_INDEX;
    private boolean ma65Visibility = MA65_VISIBILITY;
    private int ma200period = MA200_PERIOD;
    private Color ma200color = MA200_COLOR;
    private int ma200strokeIndex = MA200_STROKE_INDEX;
    private boolean ma200Visibility = MA200_VISIBILITY;

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

    /**
     * Get the value of 65 moving average period
     *
     * @return the value of 65 moving average period
     */
    public int getMA65Period()
    {
        return ma65period;
    }

    /**
     * Set the value of 200 moving average period
     *
     * @param int new value of 200 moving average period
     */
    public void setMA65Period(int i)
    {
        ma65period = i;
    }

    /**
     * Get the value of 65 moving average color
     *
     * @return the value of 65 moving average color
     */
    public Color getMA65Color()
    {
        return ma65color;
    }

    /**
     * Set the value of 65 moving average color
     *
     * @param color new value of 65 moving average color
     */
    public void setMA65Color(Color c)
    {
        ma65color = c;
    }

    /**
     * Get the value of 65 moving average stroke index
     *
     * @return the value of 65 moving average stroke index
     */
    public int getMA65StrokeIndex()
    {
        return ma65strokeIndex;
    }

    /**
     * Set the value of 65 moving average stroke index
     *
     * @param int new value of 65 moving average stroke index
     */
    public void setMA65StrokeIndex(int i)
    {
        ma65strokeIndex = i;
    }

    /**
     * Get the value of 65 moving average stroke
     *
     * @return the value of 65 moving average stroke
     */
    public Stroke getMA65Stroke()
    {
        return StrokeGenerator.getStroke(ma65strokeIndex);
    }

    /**
     * Set the value of 65 moving average stroke index
     *
     * @param stroke to int new value of 65 moving average stroke index
     */
    public void setMA65Stroke(Stroke s)
    {
        ma65strokeIndex = StrokeGenerator.getStrokeIndex(s);
    }

    /**
     * Get the value of 65 moving average visibility
     *
     * @return the value of 65 moving average visibility
     */
    public boolean getMA65Visibility()
    {
        return ma65Visibility;
    }

    /**
     * Set the value of 65 moving average visibility
     *
     * @param boolean new value of 65 moving average visibility
     */
    public void setMA65Visibility(boolean b)
    {
        ma65Visibility = b;
    }

    /**
     * Get the value of 200 moving average period
     *
     * @return the value of 200 moving average period
     */
    public int getMA200Period()
    {
        return ma200period;
    }

    /**
     * Set the value of 200 moving average period
     *
     * @param int new value of 200 moving average period
     */
    public void setMA200Period(int i)
    {
        ma200period = i;
    }

    /**
     * Get the value of 200 moving average color
     *
     * @return the value of 200 moving average color
     */
    public Color getMA200Color()
    {
        return ma200color;
    }

    /**
     * Set the value of 200 moving average color
     *
     * @param color new value of 200 moving average color
     */
    public void setMA200Color(Color c)
    {
        ma200color = c;
    }

    /**
     * Get the value of 200 moving average stroke index
     *
     * @return the value of 200 moving average stroke index
     */
    public int getMA200StrokeIndex()
    {
        return ma200strokeIndex;
    }

    /**
     * Set the value of 200 moving average stroke index
     *
     * @param int new value of 200 moving average stroke index
     */
    public void setMA200StrokeIndex(int i)
    {
        ma200strokeIndex = i;
    }

    /**
     * Get the value of 200 moving average stroke
     *
     * @return the value of 200 moving average stroke
     */
    public Stroke getMA200Stroke()
    {
        return StrokeGenerator.getStroke(ma200strokeIndex);
    }

    /**
     * Set the value of 200 moving average stroke index
     *
     * @param stroke to int new value of 200 moving average stroke index
     */
    public void setMA200Stroke(Stroke s)
    {
        ma200strokeIndex = StrokeGenerator.getStrokeIndex(s);
    }

    /**
     * Get the value of 200 moving average visibility
     *
     * @return the value of 200 moving average visibility
     */
    public boolean getMA200Visibility()
    {
        return ma200Visibility;
    }

    /**
     * Set the value of 200 moving average visibility
     *
     * @param boolean new value of 200 moving average visibility
     */
    public void setMA200Visibility(boolean b)
    {
        ma200Visibility = b;
    }

}
