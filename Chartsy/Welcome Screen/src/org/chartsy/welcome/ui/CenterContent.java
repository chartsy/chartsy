package org.chartsy.welcome.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 *
 * @author Viorel
 */
public class CenterContent extends JPanel
{

	public CenterContent()
	{
		super(new BorderLayout());
		setOpaque(false);
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		add(new Content(), BorderLayout.CENTER);
	}

	@Override protected void paintComponent(Graphics g)
	{
		g.setColor(new Color(0, 0, 0, 200));
		g.fillRect(10, 10, getWidth()-20, getHeight()-20);

		super.paintComponent(g);
	}

}
