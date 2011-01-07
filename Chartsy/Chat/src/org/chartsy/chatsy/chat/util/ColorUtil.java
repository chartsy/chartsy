package org.chartsy.chatsy.chat.util;

import java.awt.Color;

public class ColorUtil
{

    public static Color blend(Color color1, Color color2, double ratio)
	{
        float r = (float)ratio;
        float ir = (float)1.0 - r;

        float rgb1[] = new float[3];
        float rgb2[] = new float[3];

        color1.getColorComponents(rgb1);
        color2.getColorComponents(rgb2);

        return new Color(rgb1[0] * r + rgb2[0] * ir,
			rgb1[1] * r + rgb2[1] * ir,
			rgb1[2] * r + rgb2[2] * ir);
    }

    public static Color blend(Color color1, Color color2)
	{
        return ColorUtil.blend(color1, color2, 0.5);
    }

    public static Color darker(Color color, double fraction)
	{
        int red = (int)Math.round(color.getRed() * (1.0 - fraction));
        int green = (int)Math.round(color.getGreen() * (1.0 - fraction));
        int blue = (int)Math.round(color.getBlue() * (1.0 - fraction));

        if (red < 0) red = 0;
        else if (red > 255) red = 255;
        if (green < 0) green = 0;
        else if (green > 255) green = 255;
        if (blue < 0) blue = 0;
        else if (blue > 255) blue = 255;

        int alpha = color.getAlpha();

        return new Color(red, green, blue, alpha);
    }

    public static Color lighter(Color color, double fraction)
	{
        int red = (int)Math.round(color.getRed() * (1.0 + fraction));
        int green = (int)Math.round(color.getGreen() * (1.0 + fraction));
        int blue = (int)Math.round(color.getBlue() * (1.0 + fraction));

        if (red < 0) red = 0;
        else if (red > 255) red = 255;
        if (green < 0) green = 0;
        else if (green > 255) green = 255;
        if (blue < 0) blue = 0;
        else if (blue > 255) blue = 255;

        int alpha = color.getAlpha();

        return new Color(red, green, blue, alpha);
    }

    public static String getHexName(Color color)
	{
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        String rHex = Integer.toString(r, 16);
        String gHex = Integer.toString(g, 16);
        String bHex = Integer.toString(b, 16);

        return (rHex.length() == 2 ? "" + rHex : "0" + rHex) 
			+ (gHex.length() == 2 ? "" + gHex : "0" + gHex)
			+ (bHex.length() == 2 ? "" + bHex : "0" + bHex);
    }

    public static double colorDistance(double r1, double g1, double b1, double r2, double g2, double b2)
	{
        double a = r2 - r1;
        double b = g2 - g1;
        double c = b2 - b1;

        return Math.sqrt(a * a + b * b + c * c);
    }

    public static double colorDistance(double[] color1, double[] color2)
	{
        return ColorUtil.colorDistance(color1[0], color1[1], color1[2], color2[0], color2[1], color2[2]);
    }

    public static double colorDistance(Color color1, Color color2)
	{
        float rgb1[] = new float[3];
        float rgb2[] = new float[3];

        color1.getColorComponents(rgb1);
        color2.getColorComponents(rgb2);

        return ColorUtil.colorDistance(rgb1[0], rgb1[1], rgb1[2], rgb2[0], rgb2[1], rgb2[2]);
    }

    public static boolean isDark(double r, double g, double b)
	{
        double dWhite = ColorUtil.colorDistance(r, g, b, 1.0, 1.0, 1.0);
        double dBlack = ColorUtil.colorDistance(r, g, b, 0.0, 0.0, 0.0);
        return dBlack < dWhite;
    }

    public static boolean isDark(Color color)
	{
        float r = color.getRed() / 255.0f;
        float g = color.getGreen() / 255.0f;
        float b = color.getBlue() / 255.0f;
        return isDark(r, g, b);
    }
	
}

