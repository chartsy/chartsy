package org.chartsy.main.axis;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.text.DecimalFormat;
import org.chartsy.main.ChartFrame;
import org.chartsy.main.data.ChartData;
import org.chartsy.main.utils.RectangleInsets;

/**
 *
 * @author viorel.gheba
 */
public class PriceAxisMarker
{

    private PriceAxisMarker()
    {
    }

    public static void paint(Graphics2D g, ChartFrame cf, double value, Color color, double y)
    {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        if (value < 10f)
        {
            df = new DecimalFormat("#,##0.00000");
        }
        RectangleInsets dataOffset = ChartData.dataOffset;
        FontMetrics fm = g.getFontMetrics();

        g.setPaint(color);
        double x = 1;
        double w = dataOffset.getRight() - 6;
        double h = fm.getHeight() + 4;

        GeneralPath gp = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 5);
        gp.moveTo((float) x, (float) y);
        gp.lineTo((float) (x + 6), (float) (y - h / 2));
        gp.lineTo((float) (x + w + 8), (float) (y - h / 2));
        gp.lineTo((float) (x + w + 8), (float) (y + h / 2));
        gp.lineTo((float) (x + 6), (float) (y + h / 2));
        gp.closePath();
        g.fill(gp);

        g.setPaint(new Color(0xffffff));
        g.drawString(df.format(value), (float) (x + 6 + 1), (float) (y + fm.getDescent()));
    }
}
