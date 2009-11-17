package org.chartsy.main.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;

/**
 *
 * @author viorel.gheba
 */
public final class DefaultTheme {

    private DefaultTheme() {
        // does nothing
    }

    public static final RectangleInsets AXIS_OFFSET = new RectangleInsets(5.0, 5.0, 5.0, 5.0);
    public static final RectangleInsets DATA_OFFSET = new RectangleInsets(32.0, 20.0, 60.0, 50.0);

    public static final Color AXIS_COLOR = new Color(0x2e3436);
    public static final Stroke AXIS_STROKE = StrokeGenerator.getStroke(StrokeGenerator.NORMAL);
    public static final double AXIS_TICK = 6;
    public static final double AXIS_DATE_STICK = 10;
    public static final double AXIS_PRICE_STICK = 5;

    public static final double BAR_WIDTH = 8;
    public static final Color BAR_COLOR = new Color(0x2e3436);
    public static final Stroke BAR_STROKE = StrokeGenerator.getStroke(StrokeGenerator.NORMAL);
    public static final boolean BAR_VISIBILITY = true;
    public static final Color BAR_DOWN_COLOR = new Color(0xef2929);
    public static final boolean BAR_DOWN_VISIBILITY = true;
    public static final Color BAR_UP_COLOR = new Color(0x73d216);
    public static final boolean BAR_UP_VISIBILITY = true;

    public static final Color HORIZONTAL_GRID_COLOR = new Color(0xeeeeec);
    public static final Stroke HORIZONTAL_GRID_STROKE = StrokeGenerator.getStroke(StrokeGenerator.NORMAL);
    public static final boolean HORIZONTAL_GRID_VISIBILITY = true;
    public static final Color VERTICAL_GRID_COLOR = new Color(0xeeeeec);
    public static final Stroke VERTICAL_GRID_STROKE = StrokeGenerator.getStroke(StrokeGenerator.NORMAL);
    public static final boolean VERTICAL_GRID_VISIBILITY = true;
    
    public static final Color BACKGROUND_COLOR = new Color(0xffffff);
    public static final Font FONT = new Font("Dialog", Font.PLAIN, 12);
    public static final Color FONT_COLOR = new Color(0x2e3436);
    public static final boolean MARKER_VISIBILITY = true;

    public static final Color ANNOTATION_COLOR = Color.RED;
    public static final Stroke ANNOTATION_STROKE = StrokeGenerator.DEFAULT_STROKE;

}
