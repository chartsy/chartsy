package org.chartsy.main.utils;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 *
 * @author Viorel
 */
public final class GraphicsUtils
{

	private GraphicsUtils()
	{
	}

	public static Graphics2D prepareGraphics(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(
			RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(
			RenderingHints.KEY_TEXT_ANTIALIASING,
			RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        /*g2.setRenderingHint(
			RenderingHints.KEY_ALPHA_INTERPOLATION,
			RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g2.setPaintMode();*/
		return g2;
	}

}
