package org.chartsy.main.intro.content;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.border.Border;

/**
 *
 * @author viorel.gheba
 */
public interface Constants {

    // Images
    public static final String IMAGE_LIGHT_EFFECT = "org/chartsy/main/intro/resources/light_effect.png";
    public static final String IMAGE_CHARTSY_LOGO = "org/chartsy/main/intro/resources/chartsy_logo.png";
    
    public static final String IMAGE_TWITTER_LOGO = "org/chartsy/main/intro/resources/twitter.png";
    public static final String IMAGE_FACEBOOK_LOGO = "org/chartsy/main/intro/resources/facebook.png";

    public static final String IMAGE_TAB_CONTENT_TOP = "org/chartsy/main/intro/resources/tab_content_top.png";
    public static final String IMAGE_TAB_SELECTED = "org/chartsy/main/intro/resources/tab_sel_bg.png";
    public static final String IMAGE_TAB_HOVER = "org/chartsy/main/intro/resources/tab_hover/bg.png";
    public static final String IMAGE_TAB_BOTTOM_LEFT = "org/chartsy/main/intro/resources/content_bl_corner.png";
    public static final String IMAGE_TAB_BOTTOM_RIGHT = "org/chartsy/main/intro/resources/content_br_corner.png";

    public static final String IMAGE_VIDEO_THUMBNAIL = "org/chartsy/main/intro/resources/tutorial_video.png";
    public static final String IMAGE_CHARTSY_SITE = "org/chartsy/main/intro/resources/chartsy_site.png";

    public static final String IMAGE_BULLET = "org/chartsy/main/intro/resources/bullet.png";


    // Colors
    public static final Color COLOR_CONTENT_BACKGROUND = new Color(0x181818);

    public static final Color COLOR_TAB_TEXT = new Color(0xcddc58);
    public static final Color COLOR_TAB_TEXT_HOVER = new Color(0xffffff);
    public static final Color COLOR_TAB_CONTENT_BACKGROUND = new Color(0xffffff);

    public static final Color COLOR_BULLET = new Color(0xcddc58);
    public static final Color COLOR_LINK = new Color(0x000000);

    // Fonts
    public static final int FONT_SIZE = Utils.getDefaultFontSize();
    public static final Font NORMAL_FONT = new Font(null, Font.PLAIN, FONT_SIZE);
    public static final Font BUTTON_FONT = new Font(null, Font.BOLD, FONT_SIZE);
    public static final Font DESCRIPTION_FONT = new Font(null, Font.PLAIN, FONT_SIZE - 1);
    public static final Font TAB_FONT = new Font(null, Font.BOLD, FONT_SIZE + 3);
    public static final Font WELCOME_LABEL_FONT = new Font(null, Font.BOLD, FONT_SIZE + 2);
    public static final Font SECTION_HEADER_FONT = new Font(null, Font.BOLD, FONT_SIZE + 12);
    public static final Font GET_STARTED_FONT = new Font(null, Font.BOLD, (int)((double)FONT_SIZE * 1.3999999999999999D));
    public static final Font RSS_DESCRIPTION_FONT = new Font(null, 0, FONT_SIZE - 1);

    // Ints
    public static final int TEXT_INSETS_LEFT = 10;
    public static final int TEXT_INSETS_RIGHT = 10;
    public static final int START_PAGE_MIN_WIDTH = 600;

    // Borders
    public static final Border HEADER_TEXT_BORDER = BorderFactory.createEmptyBorder(1, 10, 1, 10);
    public static final Border EMPTY_BORDER = BorderFactory.createEmptyBorder();

    // Tabs
    public static final String[] TABS = {"Welcome", "GetStarted", "Plugins", "Forum"};

    // URLs
    public static final String URL_CHARTSY = "Chartsy";
    public static final String URL_TWITTER = "Twitter";
    public static final String URL_FACEBOOK = "Facebook";
    public static final String URL_BANNER_LINK = "BannerLink";
    public static final String URL_BANNER_IMAGE_LINK = "BannerImageLink";

    // Content
    public static final String CONTENT_LICENSE = "License";

    // Cursors
    public static final Cursor CURSOR_DEFAULT = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
    public static final Cursor CURSOR_HAND = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);

}
