package org.chartsy.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import org.chartsy.main.chart.AbstractPropertyListener;
import org.chartsy.main.utils.SerialVersion;
import org.chartsy.main.utils.StrokeGenerator;
import org.chartsy.main.utils.XMLUtil;
import org.chartsy.main.utils.XMLUtil.XMLTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Administrator
 */
public class ChartProperties
        extends AbstractPropertyListener
		implements XMLTemplate
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public static final double AXIS_TICK = 6;
    public static final double AXIS_DATE_STICK = 10;
    public static final double AXIS_PRICE_STICK = 5;
    public static final Color AXIS_COLOR = new Color(0x2e3436);
    public static final int AXIS_STROKE_INDEX = 0;
    public static final boolean AXIS_LOGARITHMIC_FLAG = false;

    public static final double BAR_WIDTH = 8;
    public static final Color BAR_COLOR = new Color(0x2e3436);
    public static final int BAR_STROKE_INDEX = 0;
    public static final boolean BAR_VISIBILITY = true;
    public static final Color BAR_DOWN_COLOR = new Color(0xef2929);
    public static final boolean BAR_DOWN_VISIBILITY = true;
    public static final Color BAR_UP_COLOR = new Color(0x73d216);
    public static final boolean BAR_UP_VISIBILITY = true;

    public static final Color GRID_HORIZONTAL_COLOR = new Color(0xeeeeec);
    public static final int GRID_HORIZONTAL_STROKE_INDEX = 0;
    public static final boolean GRID_HORIZONTAL_VISIBILITY = true;
    public static final Color GRID_VERTICAL_COLOR = new Color(0xeeeeec);
    public static final int GRID_VERTICAL_STROKE_INDEX = 0;
    public static final boolean GRID_VERTICAL_VISIBILITY = true;

    public static final Color BACKGROUND_COLOR = new Color(0xffffff);
    public static final Font FONT = new Font("Dialog", Font.PLAIN, 12);
    public static final Color FONT_COLOR = new Color(0x2e3436);

    public static final boolean MARKER_VISIBILITY = true;

    public static final boolean TOOLBAR_VISIBILITY = true;
    public static final boolean TOOLBAR_SMALL_ICONS = false;
    public static final boolean TOOLBAR_SHOW_LABELS = true;

    private double axisTick = AXIS_TICK;
    private double axisDateStick = AXIS_DATE_STICK;
    private double axisPriceStick = AXIS_PRICE_STICK;
    private Color axisColor = AXIS_COLOR;
    private int axisStrokeIndex = AXIS_STROKE_INDEX;
    private boolean axisLogarithmicFlag = AXIS_LOGARITHMIC_FLAG;

    private double barWidth = BAR_WIDTH;
    private Color barColor = BAR_COLOR;
    private int barStrokeIndex = BAR_STROKE_INDEX;
    private boolean barVisibility = BAR_VISIBILITY;
    private Color barDownColor = BAR_DOWN_COLOR;
    private boolean barDownVisibility = BAR_DOWN_VISIBILITY;
    private Color barUpColor = BAR_UP_COLOR;
    private boolean barUpVisibility = BAR_UP_VISIBILITY;

    private Color gridHorizontalColor = GRID_HORIZONTAL_COLOR;
    private int gridHorizontalStrokeIndex = GRID_HORIZONTAL_STROKE_INDEX;
    private boolean gridHorizontalVisibility = GRID_HORIZONTAL_VISIBILITY;
    private Color gridVerticalColor = GRID_VERTICAL_COLOR;
    private int gridVerticalStrokeIndex = GRID_VERTICAL_STROKE_INDEX;
    private boolean gridVerticalVisibility = GRID_VERTICAL_VISIBILITY;

    private Color backgroundColor = BACKGROUND_COLOR;
    private Font font = FONT;
    private Color fontColor = FONT_COLOR;

    private boolean markerVisibility = MARKER_VISIBILITY;

    private String[] months = {"", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

    private boolean toolbarVisibility = TOOLBAR_VISIBILITY;
    private boolean toolbarSmallIcons = TOOLBAR_SMALL_ICONS;
    private boolean toolbarShowLabels = TOOLBAR_SHOW_LABELS;

    public ChartProperties() 
	{
	}

    public double getAxisTick() { return this.axisTick; }
    public double getAxisDateStick() { return this.axisDateStick; }
    public double getAxisPriceStick() {  return this.axisPriceStick; }

    public Color getAxisColor()
	{
		return this.axisColor;
	}
    public void setAxisColor(Color color)
	{
		if (color == null)
			return;
		axisColor = color;
	}

    public int getAxisStrokeIndex() 
	{
		return this.axisStrokeIndex;
	}

    public void setAxisStrokeIndex(int i) 
	{
		if (!StrokeGenerator.isStrokeIndex(i))
			return;
		axisStrokeIndex = i;
	}

    public Stroke getAxisStroke() 
	{
		return StrokeGenerator.getStroke(axisStrokeIndex);
	}

    public void setAxisStroke(Stroke s)
	{
		if (s == null)
			return;
		axisStrokeIndex = StrokeGenerator.getStrokeIndex(s);
	}

    public boolean getAxisLogarithmicFlag() 
	{
		return axisLogarithmicFlag;
	}
	
    public void setAxisLogarithmicFlag(boolean b) 
    {
		axisLogarithmicFlag = b;
	}

    public void setBarWidth(double itemWidth)
	{
		if (itemWidth <= 0)
			return;
		barWidth = itemWidth;
	}

    public double getBarWidth()
	{
		return barWidth;
	}

    public Color getBarColor() 
	{
		return this.barColor;
	}

    public void setBarColor(Color color) 
	{
		if (color == null)
			return;
		barColor = color;
	}

    public int getBarStrokeIndex() 
	{
		return this.barStrokeIndex;
	}

    public void setBarStrokeIndex(int i)
	{
		if (!StrokeGenerator.isStrokeIndex(i))
			return;
		barStrokeIndex = i;
	}

    public Stroke getBarStroke() 
	{
		return StrokeGenerator.getStroke(barStrokeIndex);
	}

    public void setBarStroke(Stroke s) 
	{
		if (s == null)
			return;
		barStrokeIndex = StrokeGenerator.getStrokeIndex(s);
	}

    public boolean getBarVisibility() 
	{
		return this.barVisibility;
	}

    public void setBarVisibility(boolean b)
	{
		barVisibility = b;
	}

    public Color getBarDownColor() 
	{
		return this.barDownColor;
	}

    public void setBarDownColor(Color color) 
	{
		if (color == null)
			return;
		barDownColor = color;
	}

    public boolean getBarDownVisibility() 
	{
		return this.barDownVisibility;
	}

    public void setBarDownVisibility(boolean b)
	{
		barDownVisibility = b;
	}

    public Color getBarUpColor() 
	{
		return this.barUpColor;
	}

    public void setBarUpColor(Color color) 
	{
		if (color == null)
			return;
		barUpColor = color;
	}

    public boolean getBarUpVisibility() 
	{
		return this.barUpVisibility;
	}

    public void setBarUpVisibility(boolean b)
	{
		barUpVisibility = b;
	}

    public Color getGridHorizontalColor() 
	{
		return this.gridHorizontalColor;
	}

    public void setGridHorizontalColor(Color color) 
	{
		if (color == null)
			return;
		gridHorizontalColor = color;
	}

    public int getGridHorizontalStrokeIndex() 
	{
		return this.gridHorizontalStrokeIndex;
	}

    public void setGridHorizontalStrokeIndex(int i)
	{
		if (!StrokeGenerator.isStrokeIndex(i))
			return;
		gridHorizontalStrokeIndex = i;
	}

    public Stroke getGridHorizontalStroke() 
	{
		return StrokeGenerator.getStroke(gridHorizontalStrokeIndex);
	}

    public void setGridHorizontalStroke(Stroke s) 
	{
		if (s == null)
			return;
		gridHorizontalStrokeIndex = StrokeGenerator.getStrokeIndex(s);
	}

    public boolean getGridHorizontalVisibility() 
	{
		return this.gridHorizontalVisibility;
	}

    public void setGridHorizontalVisibility(boolean b)
	{
		gridHorizontalVisibility = b;
	}

    public Color getGridVerticalColor() 
	{
		return this.gridVerticalColor;
	}

    public void setGridVerticalColor(Color color) 
	{
		if (color == null)
			return;
		gridVerticalColor = color;
	}

    public int getGridVerticalStrokeIndex() 
	{
		return this.gridVerticalStrokeIndex;
	}

    public void setGridVerticalStrokeIndex(int i)
	{
		if (!StrokeGenerator.isStrokeIndex(i))
			return;
		gridVerticalStrokeIndex = i;
	}

    public Stroke getGridVerticalStroke() {
		return StrokeGenerator.getStroke(gridVerticalStrokeIndex);
	}

    public void setGridVerticalStroke(Stroke s) 
	{
		if (s == null)
			return;
		gridVerticalStrokeIndex = StrokeGenerator.getStrokeIndex(s);
	}

    public boolean getGridVerticalVisibility() 
	{
		return this.gridVerticalVisibility;
	}

    public void setGridVerticalVisibility(boolean b)
	{
		gridVerticalVisibility = b;
	}

    public Color getBackgroundColor() 
	{
		return this.backgroundColor;
	}

    public void setBackgroundColor(Color color) 
	{
		if (color == null)
			return;
		backgroundColor = color;
	}

    public Font getFont() 
	{
		return this.font;
	}

    public void setFont(Font font) 
	{
		if (font == null)
			return;
		this.font = font;
	}

    public Color getFontColor() 
	{
		return this.fontColor;
	}

    public void setFontColor(Color color)
	{
		if (color == null)
			return;
		fontColor = color;
	}

    public void setMarkerVisibility(boolean b) 
	{
		markerVisibility = b;
	}

    public boolean getMarkerVisibility()
	{
		return markerVisibility;
	}

    public String[] getMonths()
	{
		return this.months;
	}

    public boolean getToolbarVisibility() 
	{
		return toolbarVisibility;
	}

    public void setToolbarVisibility(boolean b) 
	{
		toolbarVisibility = b;
	}

	public void toggleToolbarVisibility()
	{
		setToolbarVisibility(!toolbarVisibility);
	}

    public boolean getToolbarSmallIcons() 
	{
		return toolbarSmallIcons;
	}

    public void setToolbarSmallIcons(boolean b) 
	{
		toolbarSmallIcons = b;
	}

	public void toggleToolbarSmallIcons()
	{
		setToolbarSmallIcons(!toolbarSmallIcons);
	}

    public boolean getToolbarShowLabels() 
	{
		return toolbarShowLabels;
	}

    public void setToolbarShowLabels(boolean b) 
	{
		toolbarShowLabels = b;
	}

	public void toggleShowLabels()
	{
		setToolbarShowLabels(!toolbarShowLabels);
	}

	public void copyFrom(ChartProperties chartProperties)
	{
		setAxisColor(chartProperties.getAxisColor());
		setAxisStrokeIndex(chartProperties.getAxisStrokeIndex());
		setBarColor(chartProperties.getBarColor());
		setBarStrokeIndex(chartProperties.getBarStrokeIndex());
		setBarVisibility(chartProperties.getBarVisibility());
		setBarDownColor(chartProperties.getBarDownColor());
		setBarDownVisibility(chartProperties.getBarDownVisibility());
		setBarUpColor(chartProperties.getBarUpColor());
		setBarUpVisibility(chartProperties.getBarUpVisibility());
		setGridHorizontalColor(chartProperties.getGridHorizontalColor());
		setGridHorizontalStrokeIndex(chartProperties.getGridHorizontalStrokeIndex());
		setGridHorizontalVisibility(chartProperties.getGridHorizontalVisibility());
		setGridVerticalColor(chartProperties.getGridVerticalColor());
		setGridVerticalStrokeIndex(chartProperties.getGridVerticalStrokeIndex());
		setGridVerticalVisibility(chartProperties.getGridVerticalVisibility());
		setBackgroundColor(chartProperties.getBackgroundColor());
		setFont(chartProperties.getFont());
		setFontColor(chartProperties.getFontColor());
	}

	@Override
	public void saveToTemplate(Document document, Element element)
	{
		XMLUtil.addColorProperty	(document, element, "axisColor",					axisColor);
		XMLUtil.addIntegerProperty	(document, element, "axisStrokeIndex",				axisStrokeIndex);
		XMLUtil.addDoubleProperty	(document, element, "barWidth",						barWidth);
		XMLUtil.addColorProperty	(document, element, "barColor",						barColor);
		XMLUtil.addIntegerProperty	(document, element, "barStrokeIndex",				barStrokeIndex);
		XMLUtil.addBooleanProperty	(document, element, "barVisibility",				barVisibility);
		XMLUtil.addColorProperty	(document, element, "barDownColor",					barDownColor);
		XMLUtil.addBooleanProperty	(document, element, "barDownVisibility",			barDownVisibility);
		XMLUtil.addColorProperty	(document, element, "barUpColor",					barUpColor);
		XMLUtil.addBooleanProperty	(document, element, "barUpVisibility",				barUpVisibility);
		XMLUtil.addColorProperty	(document, element, "gridHorizontalColor",			gridHorizontalColor);
		XMLUtil.addIntegerProperty	(document, element, "gridHorizontalStrokeIndex",	gridHorizontalStrokeIndex);
		XMLUtil.addBooleanProperty	(document, element, "gridHorizontalVisibility",		gridHorizontalVisibility);
		XMLUtil.addColorProperty	(document, element, "gridVerticalColor",			gridVerticalColor);
		XMLUtil.addIntegerProperty	(document, element, "gridVerticalStrokeIndex",		gridVerticalStrokeIndex);
		XMLUtil.addBooleanProperty	(document, element, "gridVerticalVisibility",		gridVerticalVisibility);
		XMLUtil.addColorProperty	(document, element, "backgroundColor",				backgroundColor);
		XMLUtil.addFontProperty		(document, element, "font",							font);
		XMLUtil.addColorProperty	(document, element, "fontColor",					fontColor);
	}

	@Override
	public void loadFromTemplate(Element element)
	{
		axisColor					= XMLUtil.getColorProperty(element,		"axisColor");
		axisStrokeIndex				= XMLUtil.getIntegerProperty(element,	"axisStrokeIndex");
		barWidth					= XMLUtil.getDoubleProperty(element,	"barWidth");
		barColor					= XMLUtil.getColorProperty(element,		"barColor");
		barStrokeIndex				= XMLUtil.getIntegerProperty(element,	"barStrokeIndex");
		barVisibility				= XMLUtil.getBooleanProperty(element,	"barVisibility");
		barDownColor				= XMLUtil.getColorProperty(element,		"barDownColor");
		barDownVisibility			= XMLUtil.getBooleanProperty(element,	"barDownVisibility");
		barUpColor					= XMLUtil.getColorProperty(element,		"barUpColor");
		barUpVisibility				= XMLUtil.getBooleanProperty(element,	"barUpVisibility");
		gridHorizontalColor			= XMLUtil.getColorProperty(element,		"gridHorizontalColor");
		gridHorizontalStrokeIndex	= XMLUtil.getIntegerProperty(element,	"gridHorizontalStrokeIndex");
		gridHorizontalVisibility	= XMLUtil.getBooleanProperty(element,	"gridHorizontalVisibility");
		gridVerticalColor			= XMLUtil.getColorProperty(element,		"gridVerticalColor");
		gridVerticalStrokeIndex		= XMLUtil.getIntegerProperty(element,	"gridVerticalStrokeIndex");
		gridVerticalVisibility		= XMLUtil.getBooleanProperty(element,	"gridVerticalVisibility");
		backgroundColor				= XMLUtil.getColorProperty(element,		"backgroundColor");
		font						= XMLUtil.getFontProperty(element,		"font");
		fontColor					= XMLUtil.getColorProperty(element,		"fontColor");
	}

}
