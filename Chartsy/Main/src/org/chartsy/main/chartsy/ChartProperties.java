package org.chartsy.main.chartsy;

import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.io.Serializable;
import org.chartsy.main.utils.DefaultTheme;
import org.chartsy.main.utils.RectangleInsets;
import org.chartsy.main.utils.StrokeGenerator;

/**
 *
 * @author viorel.gheba
 */
public class ChartProperties implements Serializable {

    private static final long serialVersionUID = 101L;

    private double axisTick = DefaultTheme.AXIS_TICK;
    private double axisDateStick = DefaultTheme.AXIS_DATE_STICK;
    private double axisPriceStick = DefaultTheme.AXIS_PRICE_STICK;

    private RectangleInsets axisOffset = DefaultTheme.AXIS_OFFSET;
    private RectangleInsets dataOffset = DefaultTheme.DATA_OFFSET;

    private Color axisColor = DefaultTheme.AXIS_COLOR;
    private int axisStrokeIndex = 0;
    //private transient Stroke axisStroke = DefaultTheme.AXIS_STROKE;
    private boolean markerVisibility = true;
    private String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    private double barWidth = DefaultTheme.BAR_WIDTH;
    private Color barColor = DefaultTheme.BAR_COLOR;
    private int barStrokeIndex = 0;
    //private transient Stroke barStroke = DefaultTheme.BAR_STROKE;
    private boolean barVisibility = DefaultTheme.BAR_VISIBILITY;

    private Color barDownColor = DefaultTheme.BAR_DOWN_COLOR;
    private boolean barDownVisibility = DefaultTheme.BAR_DOWN_VISIBILITY;
    private Color barUpColor = DefaultTheme.BAR_UP_COLOR;
    private boolean barUpVisibility = DefaultTheme.BAR_UP_VISIBILITY;

    private Color gridHorizontalColor = DefaultTheme.HORIZONTAL_GRID_COLOR;
    private int gridHorizontalStrokeIndex = 0;
    //private transient Stroke gridHorizontalStroke = DefaultTheme.HORIZONTAL_GRID_STROKE;
    private boolean gridHorizontalVisibility = DefaultTheme.HORIZONTAL_GRID_VISIBILITY;
    private Color gridVerticalColor = DefaultTheme.VERTICAL_GRID_COLOR;
    private int gridVerticalStrokeIndex = 0;
    //private transient Stroke gridVerticalStroke = DefaultTheme.VERTICAL_GRID_STROKE;
    private boolean gridVerticalVisibility = DefaultTheme.VERTICAL_GRID_VISIBILITY;

    private Color backgroundColor = DefaultTheme.BACKGROUND_COLOR;
    private Font font = DefaultTheme.FONT;
    private Color fontColor = DefaultTheme.FONT_COLOR;

    private Color annotationColor = DefaultTheme.ANNOTATION_COLOR;
    //private transient Stroke annotationStroke = DefaultTheme.ANNOTATION_STROKE;

    public static ChartProperties newInstance() {
        return new ChartProperties();
    }

    private ChartProperties() {}

    public double getAxisTick() {
        return this.axisTick;
    }

    public double getAxisDateStick() {
        return this.axisDateStick;
    }

    public double getAxisPriceStick() { 
        return this.axisPriceStick;
    }

    public RectangleInsets getAxisOffset() {
        return this.axisOffset;
    }

    public RectangleInsets getDataOffset() { 
        return this.dataOffset;
    }

    public Color getAxisColor() {
        return this.axisColor;
    }

    public void setAxisColor(Color color) { 
        this.axisColor = color;
    }

    public int getAxisStrokeIndex() {
        return this.axisStrokeIndex;
    }

    public void setAxisStrokeIndex(int i) {
        this.axisStrokeIndex = i;
    }

    public Stroke getAxisStroke() {
        return StrokeGenerator.getStroke(axisStrokeIndex);
    }

    public void setMarkerVisibility(boolean b) {
        markerVisibility = b;
    }

    public boolean getMarkerVisibility() { 
        return markerVisibility;
    }

    public String[] getMonths() { 
        return this.months;
    }

    public void setBarWidth(double itemWidth) {
        barWidth = itemWidth;
    }

    public double getBarWidth() { 
        return barWidth;
    }

    public Color getBarColor() {
        return this.barColor;
    }

    public void setBarColor(Color color) { 
        this.barColor = color;
    }

    public int getBarStrokeIndex() {
        return this.barStrokeIndex;
    }

    public void setBarStrokeIndex(int i) {
        this.barStrokeIndex = i;
    }

    public Stroke getBarStroke() {
        return StrokeGenerator.getStroke(barStrokeIndex);
    }

    public boolean getBarVisibility() {
        return this.barVisibility;
    }

    public void setBarVisibility(boolean b) { 
        this.barVisibility = b;
    }

    public Color getBarDownColor() {
        return this.barDownColor;
    }

    public void setBarDownColor(Color color) { 
        this.barDownColor = color;
    }

    public boolean getBarDownVisibility() {
        return this.barDownVisibility;
    }

    public void setBarDownVisibility(boolean b) { 
        this.barDownVisibility = b;
    }

    public Color getBarUpColor() {
        return this.barUpColor;
    }

    public void setBarUpColor(Color color) { 
        this.barUpColor = color;
    }

    public boolean getBarUpVisibility() {
        return this.barUpVisibility;
    }

    public void setBarUpVisibility(boolean b) { 
        this.barUpVisibility = b;
    }

    public Color getGridHorizontalColor() {
        return this.gridHorizontalColor;
    }

    public void setGridHorizontalColor(Color color) { 
        this.gridHorizontalColor = color;
    }

    public int getGridHorizontalStrokeIndex() {
        return this.gridHorizontalStrokeIndex;
    }

    public void setGridHorizontalStrokeIndex(int i) {
        this.gridHorizontalStrokeIndex = i;
    }

    public Stroke getGridHorizontalStroke() {
        return StrokeGenerator.getStroke(gridHorizontalStrokeIndex);
    }

    public boolean getGridHorizontalVisibility() {
        return this.gridHorizontalVisibility;
    }

    public void setGridHorizontalVisibility(boolean b) { 
        this.gridHorizontalVisibility = b;
    }

    public Color getGridVerticalColor() {
        return this.gridVerticalColor;
    }

    public void setGridVerticalColor(Color color) { 
        this.gridVerticalColor = color;
    }

    public int getGridVerticalStrokeIndex() {
        return this.gridVerticalStrokeIndex;
    }

    public void setGridVerticalStrokeIndex(int i) {
        this.gridVerticalStrokeIndex = i;
    }

    public Stroke getGridVerticalStroke() {
        return StrokeGenerator.getStroke(gridVerticalStrokeIndex);
    }

    public boolean getGridVerticalVisibility() {
        return this.gridVerticalVisibility;
    }

    public void setGridVerticalVisibility(boolean b) { 
        this.gridVerticalVisibility = b;
    }

    public Color getBackgroundColor() {
        return this.backgroundColor;
    }

    public void setBackgroundColor(Color color) { 
        this.backgroundColor = color;
    }

    public Font getFont() {
        return this.font;
    }

    public void setFont(Font font) { 
        this.font = font;
    }

    public Color getFontColor() {
        return this.fontColor;
    }

    public void setFontColor(Color color) { 
        this.fontColor = color;
    }

    public Color getAnnotationColor() {
        return this.annotationColor;
    }

}
