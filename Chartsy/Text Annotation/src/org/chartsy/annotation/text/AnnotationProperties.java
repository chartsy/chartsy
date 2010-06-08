package org.chartsy.annotation.text;

import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import org.chartsy.main.chart.AbstractPropertyListener;
import org.chartsy.main.utils.ColorGenerator;
import org.chartsy.main.utils.SerialVersion;
import org.chartsy.main.utils.StrokeGenerator;

/**
 *
 * @author Viorel
 */
public class AnnotationProperties
        extends AbstractPropertyListener
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public static final String TEXT = "";
    public static final Color TEXT_COLOR = new Color(0x2e3436);
    public static final Font FONT = new Font("Dialog", Font.PLAIN, 12);
    public static final Color COLOR = Color.RED;
    public static final int STROKE_INDEX = 0;
    public static final int INSIDE_ALPHA = 25;
    public static final boolean INSIDE_VISIBILITY = true;

    private String text = TEXT;
    private Color textColor = TEXT_COLOR;
    private Font font = FONT;
    private Color color = COLOR;
    private int strokeIndex = STROKE_INDEX;
    private int insideAlpha = INSIDE_ALPHA;
    private boolean insideVisibility = INSIDE_VISIBILITY;

    public AnnotationProperties() {}

    public String getText() { return text; }
    public void setText(String s) { text = s; }

    public Color getTextColor() { return textColor; }
    public void setTextColor(Color c) { textColor = c; }

    public Font getFont() { return font; }
    public void setFont(Font f) { font = f; }

    public Color getColor() { return color; }
    public void setColor(Color c) { color = c; }

    public int getStrokeIndex() { return strokeIndex; }
    public void setStrokeIndex(int i) { strokeIndex = i; }
    public Stroke getStroke() { return StrokeGenerator.getStroke(strokeIndex); }
    public void setStroke(Stroke s) { strokeIndex = StrokeGenerator.getStrokeIndex(s); }

    public int getInsideAlpha() { return insideAlpha; }
    public void setInsideAlpha(int i) { insideAlpha = i; }
    public Color getFillColor() { return ColorGenerator.getTransparentColor(color, insideAlpha); }

    public boolean getInsideVisibility() { return insideVisibility; }
    public void setInsideVisibility(boolean b) { insideVisibility = b; }

}
