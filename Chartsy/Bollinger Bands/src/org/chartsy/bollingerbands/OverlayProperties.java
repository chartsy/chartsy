package org.chartsy.bollingerbands;

import java.awt.Color;
import java.awt.Stroke;
import org.chartsy.main.chart.AbstractPropertyListener;
import org.chartsy.main.data.Dataset;
import org.chartsy.main.utils.ColorGenerator;
import org.chartsy.main.utils.SerialVersion;
import org.chartsy.main.utils.StrokeGenerator;

/**
 *
 * @author viorel.gheba
 */
public class OverlayProperties 
        extends AbstractPropertyListener
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public static final int STD = 2;
    public static final int PERIOD = 20;
    public static final String PRICE = Dataset.CLOSE;
    public static final String LABEL = "Bollinger";
    public static final boolean MARKER = true;
    public static Color LOWER_COLOR;
    public static final int LOWER_STROKE_INDEX = 0;
    public static Color MIDDLE_COLOR;
    public static final int MIDDLE_STROKE_INDEX = 0;
    public static Color UPPER_COLOR;
    public static final int UPPER_STROKE_INDEX = 0;
    public static Color INSIDE_COLOR;
    public static final int INSIDE_ALPHA = 25;
    public static final boolean INSIDE_VISIBILITY = true;

    private int std = STD;
    private int period = PERIOD;
    private String price = PRICE;
    private String label = LABEL;
    private boolean marker = MARKER;
    private Color lowerColor;
    private int lowerStrokeIndex = LOWER_STROKE_INDEX;
    private Color middleColor;
    private int middleStrokeIndex = MIDDLE_STROKE_INDEX;
    private Color upperColor;
    private int upperStrokeIndex = UPPER_STROKE_INDEX;
    private Color insideColor;
    private int insideAlpha = INSIDE_ALPHA;
    private boolean insideVisibility = INSIDE_VISIBILITY;

    public OverlayProperties()
    {
        Color c = ColorGenerator.getRandomColor();
        LOWER_COLOR = c;
        MIDDLE_COLOR = c;
        UPPER_COLOR = c;
        INSIDE_COLOR = c;

        lowerColor = c;
        middleColor = c;
        upperColor = c;
        insideColor = c;
    }

    public int getStd() { return std; }
    public void setStd(int i) { std = i; }

    public int getPeriod() { return period; }
    public void setPeriod(int i) { period = i; }

    public String getPrice() { return price; }
    public void setPrice(String s) { price = s; }

    public String getLabel() { return label; }
    public void setLabel(String s) { label = s; }

    public boolean getMarker() { return marker; }
    public void setMarker(boolean b) { marker = b; }

    public Color getLowerColor() { return lowerColor; }
    public void setLowerColor(Color c) { lowerColor = c; }

    public int getLowerStrokeIndex() { return lowerStrokeIndex; }
    public void setLowerStrokeIndex(int i) { lowerStrokeIndex = i; }
    public Stroke getLowerStroke() { return StrokeGenerator.getStroke(lowerStrokeIndex); }
    public void setLowerStroke(Stroke s) { lowerStrokeIndex = StrokeGenerator.getStrokeIndex(s); }

    public Color getMiddleColor() { return middleColor; }
    public void setMiddleColor(Color c) { middleColor = c; }

    public int getMiddleStrokeIndex() { return middleStrokeIndex; }
    public void setMiddleStrokeIndex(int i) { middleStrokeIndex = i; }
    public Stroke getMiddleStroke() { return StrokeGenerator.getStroke(middleStrokeIndex); }
    public void setMiddleStroke(Stroke s) { middleStrokeIndex = StrokeGenerator.getStrokeIndex(s); }

    public Color getUpperColor() { return upperColor; }
    public void setUpperColor(Color c) { upperColor = c; }

    public int getUpperStrokeIndex() { return upperStrokeIndex; }
    public void setUpperStrokeIndex(int i) { upperStrokeIndex = i; }
    public Stroke getUpperStroke() { return StrokeGenerator.getStroke(upperStrokeIndex); }
    public void setUpperStroke(Stroke s) { upperStrokeIndex = StrokeGenerator.getStrokeIndex(s); }

    public Color getInsideColor() { return insideColor; }
    public void setInsideColor(Color c) { insideColor = c; }
    public Color getInsideTransparentColor() { return ColorGenerator.getTransparentColor(insideColor, insideAlpha); }

    public int getInsideAlpha() { return insideAlpha; }
    public void setInsideAlpha(int i) { insideAlpha = i; }

    public boolean getInsideVisibility() { return insideVisibility; }
    public void setInsideVisibility(boolean b) { insideVisibility = b; }

}
