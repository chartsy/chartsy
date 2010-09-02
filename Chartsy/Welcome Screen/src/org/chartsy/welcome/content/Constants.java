package org.chartsy.welcome.content;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Stroke;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Viorel
 */
public interface Constants
{

	public static final Image BG_IMG			= ImageUtilities.loadImage("/org/chartsy/welcome/resources/bg.jpg", true);
	public static final String CHARTSY_ICON		= "/org/chartsy/welcome/resources/logo.png";
	public static final String RANDOM_ICON		= "/org/chartsy/welcome/resources/random.png";
	public static final String FOLLOW_ICON		= "/org/chartsy/welcome/resources/follow.png";
	public static final String NEWS_ICON		= "/org/chartsy/welcome/resources/news.png";
	public static final String TUTS_ICON		= "/org/chartsy/welcome/resources/tutorials.png";
	public static final String TUT_VID_ICON		= "/org/chartsy/welcome/resources/tutorial-video.png";
	public static final String FORUM_ICON		= "/org/chartsy/welcome/resources/forum.png";
	public static final String BULLET_ICON		= "/org/chartsy/welcome/resources/bullet.png";

	public static final Color LINK_COLOR		= Color.decode("0xffffff");
	public static final Color LINK_HOVER_COLOR	= Color.decode("0xcbda57");
	public static final Stroke DASH_STROKE		= new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[] {1.0f,1.0f}, 0.0f);

	public static final String CHARTSY_URL		= "http://www.chartsy.org";
	public static final String TUTS_URL			= "http://www.chartsy.org/support/tutorials";

	static final int FONT_SIZE					= Utils.getDefaultFontSize();
	static final Font BUTTON_FONT				= new Font(null, Font.PLAIN, FONT_SIZE);
	static final Font LINK_FONT					= new Font(null, Font.PLAIN, FONT_SIZE+1);
	static final Font RSS_DESCRIPTION_FONT		= new Font(null, Font.PLAIN, FONT_SIZE-1);
	public static final Font TITLE_FONT			= new Font(null, Font.BOLD, FONT_SIZE+2);

}
