package org.chartsy.lucasseries;

import java.awt.Color;
import java.awt.Stroke;
import org.chartsy.main.chart.AbstractPropertyListener;
import org.chartsy.main.utils.ColorGenerator;
import org.chartsy.main.utils.SerialVersion;
import org.chartsy.main.utils.StrokeGenerator;

/**
 *
 * @author Viorel
 */
public class OverlayProperties extends AbstractPropertyListener {

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public static final int STARTING_MONTH = 1;
    public static final int STARTING_DAY = 1;
    public static final int STARTING_YEAR = 2000;
    public static final int STARTING_HOUR = 1;
    public static final int STARTING_MINUTE = 0;

    public static Color COLOR;
    public static final int STROKE_INDEX = 0;
    public static final String LABEL = "Lucas Series";

    private int startingMonth = STARTING_MONTH;
    private int startingDay = STARTING_DAY;
    private int startingYear = STARTING_YEAR;
    private int startingHour = STARTING_HOUR;
    private int startingMinute = STARTING_MINUTE;

    private Color color;
    private int strokeIndex = STROKE_INDEX;
    private String label = LABEL;

    public OverlayProperties() { COLOR = ColorGenerator.getRandomColor(); color = COLOR; }

    public int getStartingMonth() {
        return startingMonth;
    }

    public void setStartingMonth(int i) {
        if (i < 1) i = 1;
        if (i > 12) i = 12;
        startingMonth = i;
    }

    public int getStartingDay() {
        return startingDay;
    }

    public void setStartingDay(int i) {
        if (i < 1) i = 1;
        if (i > 31) i = 31;
        startingDay = i;
    }

    public int getStartingYear() {
        return startingYear;
    }

    public void setStartingYear(int i) {
        if (i < 1980) i = 1980;
        if (i > 2100) i = 2100;
        startingYear = i;
    }

    public int getStartingHour() {
        return startingHour;
    }

    public void setStartingHour(int i) {
        if (i < 1) i = 1;
        if (i > 24) i = 24;
        startingHour = i;
    }

    public int getStartingMinute() {
        return startingMinute;
    }

    public void setStartingMinute(int i) {
        if (i < 0) i = 0;
        if (i > 60) i = 60;
        startingMinute = i;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color c) {
        color = c;
    }

    public int getStrokeIndex() {
        return strokeIndex;
    }

    public void setStrokeIndex(int i) {
        strokeIndex = i;
    }

    public Stroke getStroke() {
        return StrokeGenerator.getStroke(strokeIndex);
    }

    public void setStroke(Stroke s) {
        strokeIndex = StrokeGenerator.getStrokeIndex(s);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String s) {
        label = s;
    }

}
