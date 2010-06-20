/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.chartsy.kama;

import java.awt.Color;
import java.awt.Stroke;
import org.chartsy.main.chart.AbstractPropertyListener;
import org.chartsy.main.data.Dataset;
import org.chartsy.main.utils.ColorGenerator;
import org.chartsy.main.utils.SerialVersion;
import org.chartsy.main.utils.StrokeGenerator;

/**
 *
 * @author joshua.taylor
 */
public class OverlayProperties extends AbstractPropertyListener
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public static final int PERIOD = 20;
    public static final String PRICE = Dataset.CLOSE;
    public static final String LABEL = "KAMA";
    public static final boolean MARKER = true;
    public static Color COLOR;
    public static final int STROKE_INDEX = 0;

    private int period = PERIOD;
    private String price = PRICE;
    private String label = LABEL;
    private boolean marker = MARKER;
    private Color color;
    private int strokeIndex = STROKE_INDEX;

    public OverlayProperties() { COLOR = ColorGenerator.getRandomColor(); color = COLOR; }

    public int getPeriod() { return period; }
    public void setPeriod(int i) { period = i; }

    public String getPrice() { return price; }
    public void setPrice(String s) { price = s; }

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
