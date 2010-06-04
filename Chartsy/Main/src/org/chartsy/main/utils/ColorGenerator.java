package org.chartsy.main.utils;

import java.awt.Color;
import java.util.Random;

/**
 *
 * @author viorel.gheba
 */
public class ColorGenerator
{

    private static Random rand = new Random();

    private ColorGenerator()
    {}

    public static Color getRandomColor()
    {
        return new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
    }

    public static Color getTransparentColor(Color color, int alpha)
    {
        return new Color(
                color.getRed(),
                color.getGreen(),
                color.getBlue(),
                alpha);
    }

}
