package org.chartsy.main.utils;

import java.awt.BasicStroke;
import java.awt.Stroke;

/**
 *
 * @author viorel.gheba
 */
public final class StrokeGenerator
{

    public static final Stroke DEFAULT_STROKE = new BasicStroke(1.0f);
    private static final Stroke[] strokes =
	{
        DEFAULT_STROKE,
        new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0.0f, new float[] {9.0f,6.0f}, 0.0f),
        new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[] {1.0f,1.0f}, 0.0f),
        new BasicStroke(2.0f),
        new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0.0f, new float[] {9.0f,6.0f}, 0.0f),
        new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 2.0f, new float[] {2.0f,2.0f}, 0.0f),
        new BasicStroke(3.0f),
        new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0.0f, new float[] {9.0f,6.0f}, 0.0f),
        new BasicStroke(3.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 3.0f, new float[] {3.0f,3.0f}, 0.0f)
    };

    private StrokeGenerator() 
	{
	}

    public static Stroke[] getStrokes()
	{
		return strokes;
	}

    public static int getStrokeIndex(Stroke stroke)
	{
        for (int i = 0; i < strokes.length; i++)
		{
            if (strokes[i].equals(stroke)) 
                return i;
        }
        return -1;
    }

    public static Stroke getStroke(int i)
	{
		return i != -1 ? strokes[i] : null;
	}

	public static boolean isStrokeIndex(int index)
	{
		return !(index < 0 || index > strokes.length - 1);
	}

}
