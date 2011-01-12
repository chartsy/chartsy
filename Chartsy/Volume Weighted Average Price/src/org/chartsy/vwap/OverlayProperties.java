package org.chartsy.vwap;

import java.awt.Color;
import org.chartsy.main.chart.AbstractPropertyListener;
import org.chartsy.main.data.Dataset;
import org.chartsy.main.utils.SerialVersion;

/**
 *
 * @author Viorel
 */
public class OverlayProperties extends AbstractPropertyListener
{

	private static final long serialVersionUID = SerialVersion.APPVERSION;

	// PROPERTIES
	public static final String		PRICE				= Dataset.CLOSE;
	public static final String		LABEL				= "VWAP";
	public static final boolean		MARKER				= true;
	public static final Color		COLOR				= Color.decode("0x000000");
	// BAND 1 PROPERTIES
	public static final boolean		BAND1_VISIBILITY	= true;
	public static final Color		BAND1_COLOR			= Color.decode("0xCC0000");
	public static final int			BAND1_DEV			= 1;
	// BAND 2 PROPERTIES
	public static final boolean		BAND2_VISIBILITY	= true;
	public static final Color		BAND2_COLOR			= Color.decode("0x006E2E");
	public static final int			BAND2_DEV			= 2;
	// BAND 3 PROPERTIES
	public static final boolean		BAND3_VISIBILITY	= true;
	public static final Color		BAND3_COLOR			= Color.decode("0x204A87");
	public static final int			BAND3_DEV			= 3;

	private String price = PRICE;
	private String label = LABEL;
	private boolean marker = MARKER;
	private Color color = COLOR;

	private boolean b1v = BAND1_VISIBILITY;
	private Color b1color = BAND1_COLOR;
	private int b1dev = BAND1_DEV;

	private boolean b2v = BAND2_VISIBILITY;
	private Color b2color = BAND2_COLOR;
	private int b2dev = BAND2_DEV;

	private boolean b3v = BAND3_VISIBILITY;
	private Color b3color = BAND3_COLOR;
	private int b3dev = BAND3_DEV;

	public OverlayProperties()
	{}

	public String getPrice() { return price; }
    public void setPrice(String s) { price = s; }
	
	public String getLabel() { return label; }
    public void setLabel(String s) { label = s; }

	public boolean getMarker() { return marker; }
    public void setMarker(boolean b) { marker = b; }

	public Color getColor() { return color; }
    public void setColor(Color c) { color = c; }

	public boolean getBand1Visibility() { return b1v; }
    public void setBand1Visibility(boolean b) { b1v = b; }

	public Color getBand1Color() { return b1color; }
    public void setBand1Color(Color c) { b1color = c; }

	public int getBand1Dev() { return b1dev; }
    public void setBand1Dev(int i) { b1dev = i; }

	public boolean getBand2Visibility() { return b2v; }
    public void setBand2Visibility(boolean b) { b2v = b; }

	public Color getBand2Color() { return b2color; }
    public void setBand2Color(Color c) { b2color = c; }

	public int getBand2Dev() { return b2dev; }
    public void setBand2Dev(int i) { b2dev = i; }

	public boolean getBand3Visibility() { return b3v; }
    public void setBand3Visibility(boolean b) { b3v = b; }

	public Color getBand3Color() { return b3color; }
    public void setBand3Color(Color c) { b3color = c; }

	public int getBand3Dev() { return b3dev; }
    public void setBand3Dev(int i) { b3dev = i; }

}
