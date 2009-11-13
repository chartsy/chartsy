package org.chartsy.main.chartsy;

import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import org.chartsy.main.utils.DefaultTheme;
import org.chartsy.main.utils.RectangleInsets;
import org.chartsy.main.utils.StrokeGenerator;
import org.chartsy.main.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author viorel.gheba
 */
public class ChartProperties implements XMLUtils.ToXML {

    private double axisTick = DefaultTheme.AXIS_TICK;
    private double axisDateStick = DefaultTheme.AXIS_DATE_STICK;
    private double axisPriceStick = DefaultTheme.AXIS_PRICE_STICK;

    private RectangleInsets axisOffset = DefaultTheme.AXIS_OFFSET;
    private RectangleInsets dataOffset = DefaultTheme.DATA_OFFSET;

    private Color axisColor = DefaultTheme.AXIS_COLOR;
    private Stroke axisStroke = DefaultTheme.AXIS_STROKE;
    private boolean markerVisibility = true;
    private String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    private double barWidth = DefaultTheme.BAR_WIDTH;
    private Color barColor = DefaultTheme.BAR_COLOR;
    private Stroke barStroke = DefaultTheme.BAR_STROKE;
    private boolean barVisibility = DefaultTheme.BAR_VISIBILITY;

    private Color barDownColor = DefaultTheme.BAR_DOWN_COLOR;
    private boolean barDownVisibility = DefaultTheme.BAR_DOWN_VISIBILITY;
    private Color barUpColor = DefaultTheme.BAR_UP_COLOR;
    private boolean barUpVisibility = DefaultTheme.BAR_UP_VISIBILITY;

    private Color gridHorizontalColor = DefaultTheme.HORIZONTAL_GRID_COLOR;
    private Stroke gridHorizontalStroke = DefaultTheme.HORIZONTAL_GRID_STROKE;
    private boolean gridHorizontalVisibility = DefaultTheme.HORIZONTAL_GRID_VISIBILITY;
    private Color gridVerticalColor = DefaultTheme.VERTICAL_GRID_COLOR;
    private Stroke gridVerticalStroke = DefaultTheme.VERTICAL_GRID_STROKE;
    private boolean gridVerticalVisibility = DefaultTheme.VERTICAL_GRID_VISIBILITY;

    private Color backgroundColor = DefaultTheme.BACKGROUND_COLOR;
    private Font font = DefaultTheme.FONT;
    private Color fontColor = DefaultTheme.FONT_COLOR;

    private Color annotationColor = DefaultTheme.ANNOTATION_COLOR;
    private Stroke annotationStroke = DefaultTheme.ANNOTATION_STROKE;

    public static ChartProperties newInstance() {
        return new ChartProperties();
    }

    private ChartProperties() {}

    public double getAxisTick() { return this.axisTick; }
    public double getAxisDateStick() { return this.axisDateStick; }
    public double getAxisPriceStick() { return this.axisPriceStick; }

    public RectangleInsets getAxisOffset() { return this.axisOffset; }
    public RectangleInsets getDataOffset() { return this.dataOffset; }

    public Color getAxisColor() { return this.axisColor; }
    public void setAxisColor(Color color) { this.axisColor = color; }

    public Stroke getAxisStroke() { return this.axisStroke; }
    public void setAxisStroke(String stroke) { this.axisStroke = StrokeGenerator.getStroke(stroke); }
    public void setAxisStroke(Stroke stroke) { this.axisStroke = stroke; }

    public void setMarkerVisibility(boolean b) { markerVisibility = b; }
    public boolean getMarkerVisibility() { return markerVisibility; }

    public String[] getMonths() { return this.months; }

    public void setBarWidth(double itemWidth) { barWidth = itemWidth; }
    public double getBarWidth() { return barWidth; }

    public Color getBarColor() { return this.barColor; }
    public void setBarColor(Color color) { this.barColor = color; }

    public Stroke getBarStroke() { return this.barStroke; }
    public void setBarStroke(String stroke) { this.barStroke = StrokeGenerator.getStroke(stroke); }
    public void setBarStroke(Stroke stroke) { this.barStroke = stroke; }

    public boolean getBarVisibility() { return this.barVisibility; }
    public void setBarVisibility(boolean b) { this.barVisibility = b; }

    public Color getBarDownColor() { return this.barDownColor; }
    public void setBarDownColor(Color color) { this.barDownColor = color; }

    public boolean getBarDownVisibility() { return this.barDownVisibility; }
    public void setBarDownVisibility(boolean b) { this.barDownVisibility = b; }

    public Color getBarUpColor() { return this.barUpColor; }
    public void setBarUpColor(Color color) { this.barUpColor = color; }

    public boolean getBarUpVisibility() { return this.barUpVisibility; }
    public void setBarUpVisibility(boolean b) { this.barUpVisibility = b; }

    public Color getGridHorizontalColor() { return this.gridHorizontalColor; }
    public void setGridHorizontalColor(Color color) { this.gridHorizontalColor = color; }

    public Stroke getGridHorizontalStroke() { return this.gridHorizontalStroke; }
    public void setGridHorizontalStroke(String stroke) { this.gridHorizontalStroke = StrokeGenerator.getStroke(stroke); }
    public void setGridHorizontalStroke(Stroke stroke) { this.gridHorizontalStroke = stroke; }

    public boolean getGridHorizontalVisibility() { return this.gridHorizontalVisibility; }
    public void setGridHorizontalVisibility(boolean b) { this.gridHorizontalVisibility = b; }

    public Color getGridVerticalColor() { return this.gridVerticalColor; }
    public void setGridVerticalColor(Color color) { this.gridVerticalColor = color; }

    public Stroke getGridVerticalStroke() { return this.gridVerticalStroke; }
    public void setGridVerticalStroke(String stroke) { this.gridVerticalStroke = StrokeGenerator.getStroke(stroke); }
    public void setGridVerticalStroke(Stroke stroke) { this.gridVerticalStroke = stroke; }

    public boolean getGridVerticalVisibility() { return this.gridVerticalVisibility; }
    public void setGridVerticalVisibility(boolean b) { this.gridVerticalVisibility = b;}

    public Color getBackgroundColor() { return this.backgroundColor; }
    public void setBackgroundColor(Color color) { this.backgroundColor = color; }

    public Font getFont() { return this.font; }
    public void setFont(Font font) { this.font = font; }

    public Color getFontColor() { return this.fontColor; }
    public void setFontColor(Color color) { this.fontColor = color; }

    public Color getAnnotationColor() { return this.annotationColor; }
    public void setAnnotationColor(Color color) { this.annotationColor = color; }

    public Stroke getAnnotationStroke() { return this.annotationStroke; }
    public void setAnnotationStroke(Stroke stroke) { this.annotationStroke = stroke; }

    public void readXMLDocument(Element parent) {
        setAxisColor(XMLUtils.getColorParam(parent, "axisColor"));
        setAxisStroke(XMLUtils.getStrokeParam(parent, "axisStroke"));
        setBarWidth(XMLUtils.getDoubleParam(parent, "barWidth"));
        setBarColor(XMLUtils.getColorParam(parent, "barColor"));
        setBarStroke(XMLUtils.getStrokeParam(parent, "barStroke"));
        setBarVisibility(XMLUtils.getBooleanParam(parent, "barVisibility"));
        setBarDownColor(XMLUtils.getColorParam(parent, "downColor"));
        setBarDownVisibility(XMLUtils.getBooleanParam(parent, "downVisibility"));
        setBarUpColor(XMLUtils.getColorParam(parent, "upColor"));
        setBarUpVisibility(XMLUtils.getBooleanParam(parent, "upVisibility"));
        setGridHorizontalColor(XMLUtils.getColorParam(parent, "horizontalColor"));
        setGridHorizontalStroke(XMLUtils.getStrokeParam(parent, "horizontalStroke"));
        setGridHorizontalVisibility(XMLUtils.getBooleanParam(parent, "horizontalVisibility"));
        setGridVerticalColor(XMLUtils.getColorParam(parent, "verticalColor"));
        setGridVerticalStroke(XMLUtils.getStrokeParam(parent, "verticalStroke"));
        setGridVerticalVisibility(XMLUtils.getBooleanParam(parent, "verticalVisibility"));
        setBackgroundColor(XMLUtils.getColorParam(parent, "background"));
        setFont(XMLUtils.getFontParam(parent, "font"));
        setFontColor(XMLUtils.getColorParam(parent, "fontColor"));
        setAnnotationColor(XMLUtils.getColorParam(parent, "annotationColor"));
        setAnnotationStroke(XMLUtils.getStrokeParam(parent, "annotationStroke"));
    }

    public void writeXMLDocument(Document document, Element parent) {
        Element element;

        element = document.createElement("axisColor");
        parent.appendChild(XMLUtils.setColorParam(element, getAxisColor()));

        element = document.createElement("axisStroke");
        parent.appendChild(XMLUtils.setStrokeParam(element, getAxisStroke()));

        element = document.createElement("barWidth");
        parent.appendChild(XMLUtils.setDoubleParam(element, getBarWidth()));

        element = document.createElement("barColor");
        parent.appendChild(XMLUtils.setColorParam(element, getBarColor()));

        element = document.createElement("barStroke");
        parent.appendChild(XMLUtils.setStrokeParam(element, getBarStroke()));

        element = document.createElement("barVisibility");
        parent.appendChild(XMLUtils.setBooleanParam(element, getBarVisibility()));

        element = document.createElement("downColor");
        parent.appendChild(XMLUtils.setColorParam(element, getBarDownColor()));

        element = document.createElement("downVisibility");
        parent.appendChild(XMLUtils.setBooleanParam(element, getBarDownVisibility()));

        element = document.createElement("upColor");
        parent.appendChild(XMLUtils.setColorParam(element, getBarUpColor()));

        element = document.createElement("upVisibility");
        parent.appendChild(XMLUtils.setBooleanParam(element, getBarUpVisibility()));

        element = document.createElement("horizontalColor");
        parent.appendChild(XMLUtils.setColorParam(element, getGridHorizontalColor()));

        element = document.createElement("horizontalStroke");
        parent.appendChild(XMLUtils.setStrokeParam(element, getGridHorizontalStroke()));

        element = document.createElement("horizontalVisibility");
        parent.appendChild(XMLUtils.setBooleanParam(element, getGridHorizontalVisibility()));

        element = document.createElement("verticalColor");
        parent.appendChild(XMLUtils.setColorParam(element, getGridVerticalColor()));

        element = document.createElement("verticalStroke");
        parent.appendChild(XMLUtils.setStrokeParam(element, getGridVerticalStroke()));

        element = document.createElement("verticalVisibility");
        parent.appendChild(XMLUtils.setBooleanParam(element, getGridVerticalVisibility()));

        element = document.createElement("background");
        parent.appendChild(XMLUtils.setColorParam(element, getBackgroundColor()));

        element = document.createElement("font");
        parent.appendChild(XMLUtils.setFontParam(element, getFont()));

        element = document.createElement("fontColor");
        parent.appendChild(XMLUtils.setColorParam(element, getFontColor()));

        element = document.createElement("annotationColor");
        parent.appendChild(XMLUtils.setColorParam(element, getAnnotationColor()));

        element = document.createElement("annotationStroke");
        parent.appendChild(XMLUtils.setStrokeParam(element, getAnnotationStroke()));
    }

}
