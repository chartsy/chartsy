package org.chartsy.main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Transparency;
import java.awt.image.VolatileImage;
import java.io.Serializable;
import javax.swing.JPanel;
import org.chartsy.main.utils.SerialVersion;

/**
 *
 * @author viorel.gheba
 */
public abstract class BufferedPanel extends JPanel implements Serializable
{

	private static final long serialVersionUID = SerialVersion.APPVERSION;

    protected VolatileImage volatileImage;

	public BufferedPanel()
	{
	}

	@Override
	public void paint(Graphics g)
	{
		createBackBuffer();
		do
		{
			GraphicsConfiguration graphicsConfiguration = getGraphicsConfiguration();
			int code = volatileImage.validate(graphicsConfiguration);

			if (code == VolatileImage.IMAGE_INCOMPATIBLE)
				createBackBuffer();

			Graphics graphics = volatileImage.getGraphics();
			Graphics2D graphics2D = (Graphics2D) graphics;
			graphics2D.setColor(new Color(0,0,0,0));
			graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OUT));
			graphics2D.fillRect(0, 0, volatileImage.getWidth(), volatileImage.getHeight());
			paintBufferedComponent(graphics);
			g.drawImage(volatileImage, 0, 0, this);
			graphics2D.dispose();
			graphics.dispose();
		} while (volatileImage.contentsLost());
	}

	protected abstract void paintBufferedComponent(Graphics g);

	protected void createBackBuffer()
	{
		GraphicsConfiguration graphicsConfiguration = getGraphicsConfiguration();
		volatileImage = graphicsConfiguration.createCompatibleVolatileImage(getWidth(), getHeight(), Transparency.TRANSLUCENT);
	}

	@Override
	public void update(Graphics g)
	{
		paint(g);
	}

}
