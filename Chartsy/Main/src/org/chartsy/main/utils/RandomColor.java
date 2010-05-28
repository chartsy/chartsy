package org.chartsy.main.utils;

import java.awt.Color;
import java.util.Random;

/**
 *
 * @author viorel.gheba
 */
public class RandomColor {

    private static Random rand = new Random();

    private RandomColor() {}

    public static Color getRandomColor() { return new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)); }

}
