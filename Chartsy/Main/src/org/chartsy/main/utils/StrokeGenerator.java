package org.chartsy.main.utils;

import java.awt.BasicStroke;
import java.awt.Stroke;

/**
 *
 * @author viorel.gheba
 */
public final class StrokeGenerator {
    
    public static final String NORMAL = "Normal";
    public static final String DASHED = "Dashed";
    public static final String DOTTED = "Dotted";

    public static final String[] LIST = new String[] {NORMAL, DASHED, DOTTED};

    public static final Stroke DEFAULT_STROKE = new BasicStroke(1);
    private static final Stroke[] strokes = {
        DEFAULT_STROKE,
        new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 9, 6 }, 0),
        new BasicStroke(2),
        new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 9, 6 }, 0),
        new BasicStroke(3),
        new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 9, 6 }, 0)
    };

    public static final Stroke NORMAL_STROKE = new BasicStroke(1f);
    public static final Stroke DASHED_STROKE = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 9, 6 }, 0);//new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 3f, new float[] {3f}, 0f);
    public static final Stroke DOTTED_STROKE = new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0f, new float[] {1f, 2f}, 1.5f);

    public static final Stroke[] STROKES = {NORMAL_STROKE, DASHED_STROKE, DOTTED_STROKE};

    private StrokeGenerator() {
        // does nothing
    }

    public static Stroke[] getStrokes() {
        return strokes;
    }

    public static Stroke getStroke(final String name) {
        Stroke stroke = null;
        if (name.equals(NORMAL)) return NORMAL_STROKE;
        else if (name.equals(DASHED)) return DASHED_STROKE;
        else if (name.equals(DOTTED)) return DOTTED_STROKE;
        return stroke;
    }

    public static String getStrokeName(final Stroke stroke) {
        if (stroke.equals(NORMAL_STROKE)) return NORMAL;
        else if (stroke.equals(DASHED_STROKE)) return DASHED;
        else if (stroke.equals(DOTTED_STROKE)) return DOTTED;
        return null;
    }

    public static int getStrokeIndex(Stroke stroke) {
        for (int i = 0; i < strokes.length; i++) {
            if (strokes[i].equals(stroke)) {
                return i;
            }
        }
        return -1;
    }

    public static Stroke getStroke(int i) { return i != -1 ? strokes[i] : null; }

    public static String strokeToString(Stroke stroke) {
        BasicStroke bs = (BasicStroke) stroke;
        float width = bs.getLineWidth();
        int cap = bs.getEndCap();
        int join = bs.getLineJoin();
        float mitterlimit = bs.getMiterLimit();
        float[] dash = bs.getDashArray();
        float dashphase = bs.getDashPhase();
        return "Stroke[" + width + "," + cap + "," + join + "," + mitterlimit + "," + floatListToString(dash) + "," + dashphase + "]";
    }

    public static String floatListToString(float[] list) {
        String s = "(";
        if (list != null) {
            for (int i = 0; i < list.length; i++) s += list[i] + ",";
            s = s.substring(0, s.length() - 1);
        }
        s += ")";
        return s;
    }

}
