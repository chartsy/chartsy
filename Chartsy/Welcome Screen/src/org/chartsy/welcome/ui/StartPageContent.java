package org.chartsy.welcome.ui;

import java.awt.BorderLayout;
import java.awt.Graphics;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import org.chartsy.welcome.content.Constants;

/**
 *
 * @author Viorel
 */
public class StartPageContent extends JPanel implements Constants
{

	public StartPageContent()
	{
		super(new BorderLayout());
		setOpaque(false);
		setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		add(new TopContent(), BorderLayout.NORTH);
		add(new CenterContent(), BorderLayout.CENTER);
		add(new BottomContent(), BorderLayout.SOUTH);
	}

	@Override protected void paintComponent(Graphics g)
	{
		g.drawImage(BG_IMG, 0, 0, this);
		super.paintComponent(g);
	}

}
