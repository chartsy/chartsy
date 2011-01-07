package org.chartsy.chatsy.chat.component.borders;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.border.AbstractBorder;

/**
 *
 * @author Viorel
 */
public class CurvedBorder extends AbstractBorder
{

	private Color wallColor = Color.decode("0xef6500");
	private int sinkLevel = 10;
	private boolean isOpaque = false;

	public CurvedBorder() {}
    public CurvedBorder(int sinkLevel) { this.sinkLevel = sinkLevel; }
    public CurvedBorder(Color wall) { this.wallColor = wall; }
    public CurvedBorder(int sinkLevel, Color wall)
	{
        this.sinkLevel = sinkLevel;
        this.wallColor = wall;
    }
	public CurvedBorder(int sinkLevel, Color wall, boolean opaque)
	{
        this.sinkLevel = sinkLevel;
		if (wall != null)
			this.wallColor = wall;
		this.isOpaque = opaque;
    }

	public void paintBorder(Component c, Graphics g, int x, int y, int w, int h)
	{
		if (isBorderOpaque())
		{
			g.setColor(getWallColor());
			for (int i = 0; i < sinkLevel; i++)
			{
			   g.drawRoundRect(x+i, y+i, w-i-1, h-i-1, sinkLevel-i, sinkLevel);
			   g.drawRoundRect(x+i, y+i, w-i-1, h-i-1, sinkLevel, sinkLevel-i);
			   g.drawRoundRect(x+i, y, w-i-1, h-1, sinkLevel-i, sinkLevel);
			   g.drawRoundRect(x, y+i, w-1, h-i-1, sinkLevel, sinkLevel-i);
			}
		}
	}

	public Insets getBorderInsets(Component c)
	{
        return new Insets(sinkLevel, sinkLevel, sinkLevel, sinkLevel);
    }

    public Insets getBorderInsets(Component c, Insets i)
	{
        i.left = i.right = i.bottom = i.top = sinkLevel;
        return i;
    }

    public boolean isBorderOpaque() { return isOpaque; }
    public int getSinkLevel() { return sinkLevel; }
    public Color getWallColor() { return wallColor; }

}
