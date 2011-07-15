package org.chartsy.demarkssetup;

import java.awt.Color;
import java.awt.Stroke;
import org.chartsy.main.chart.AbstractPropertyListener;
import org.chartsy.main.data.Dataset;
import org.chartsy.main.utils.ColorGenerator;
import org.chartsy.main.utils.SerialVersion;
import org.chartsy.main.utils.StrokeGenerator;

/**
 *
 * @author Viorel
 */
public class OverlayProperties extends AbstractPropertyListener {

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public static Color COLOR;
    public static final int STROKE_INDEX = 0;
    public static final String PRICE = Dataset.CLOSE;
    public static final String LABEL = "DeMark's Setup";

    private Color color;
    private int strokeIndex = STROKE_INDEX;
    private String price = PRICE;
    private String label = LABEL;

    public OverlayProperties() { COLOR = ColorGenerator.getRandomColor(); color = COLOR; }

    public String getPrice() { return price; }
    public void setPrice(String s) { price = s; }

    public String getLabel() { return label; }
    public void setLabel(String s) { label = s; }

    public Color getColor() { return color; }
    public void setColor(Color c) { color = c; }

    public int getStrokeIndex() { return strokeIndex; }
    public void setStrokeIndex(int i) { strokeIndex = i; }
    public Stroke getStroke() { return StrokeGenerator.getStroke(strokeIndex); }
    public void setStroke(Stroke s) { strokeIndex = StrokeGenerator.getStrokeIndex(s); }

}
