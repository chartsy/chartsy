package org.chartsy.main.utils;

import java.awt.BasicStroke;
import java.awt.Stroke;

/**
 *
 * @author viorel.gheba
 */
public final class StrokeGenerator {

    public static final Stroke DEFAULT_STROKE = new BasicStroke(1);
    private static final Stroke[] strokes = {
        DEFAULT_STROKE,
        new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[] { 9, 6 }, 0),
        new BasicStroke(2),
        new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[] { 9, 6 }, 0),
        new BasicStroke(3),
        new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[] { 9, 6 }, 0)
    };

    private StrokeGenerator() {}

    public static Stroke[] getStrokes() { return strokes; }

    public static int getStrokeIndex(Stroke stroke) {
        for (int i = 0; i < strokes.length; i++) {
            if (strokes[i].equals(stroke)) {
                return i;
            }
        }
        return -1;
    }

    public static Stroke getStroke(int i) { return i != -1 ? strokes[i] : null; }

}
