package org.chartsy.main.welcome.ui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import org.chartsy.main.welcome.content.Constants;
import org.chartsy.main.welcome.content.Logo;
import org.chartsy.main.welcome.content.MenuLink;

/**
 *
 * @author Viorel
 */
public class TopContent extends JPanel implements Constants {

	public TopContent()
	{
		super(new FlowLayout(FlowLayout.CENTER));
		setOpaque(false);
		setBorder(BorderFactory.createEmptyBorder());
		initComponents();
	}

	private void initComponents()
	{
		add(Logo.createChartsyLogo());
		add(MenuLink.createMenuLink("features"));
		add(MenuLink.createMenuLink("plugins"));
		add(MenuLink.createMenuLink("docs"));
		add(MenuLink.createMenuLink("community"));
		add(MenuLink.createMenuLink("partners"));
	}

	@Override protected void paintComponent(Graphics g)
	{
		g.setColor(Color.decode("0xcbda57"));
		g.fillRect(0, 0, getWidth(), 2);

		g.setColor(new Color(0, 0, 0, 200));
		g.fillRect(0, 2, getWidth(), 70);

		super.paintComponent(g);
	}

}
