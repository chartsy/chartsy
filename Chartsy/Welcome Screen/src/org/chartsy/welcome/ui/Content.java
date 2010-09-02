package org.chartsy.welcome.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Stroke;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import org.chartsy.welcome.content.Constants;

/**
 *
 * @author Viorel
 */
public class Content extends JPanel implements Constants
{
	
	private RandomPlugin randomPlugin;
	private FollowUs followUs;
	private Description description;
	private LatestNews latestNews;
	private Tutorials tutorials;
	private Forum forum;

	public Content()
	{
		randomPlugin = new RandomPlugin();
		followUs = new FollowUs();
		description = new Description();
		latestNews = new LatestNews();
		tutorials = new Tutorials();
		forum = new Forum();

		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setOpaque(false);
		setLayout(new LayoutManager()
		{
			public void addLayoutComponent(String name, Component comp)
            {}
            public void removeLayoutComponent(Component comp)
            {}
            public Dimension preferredLayoutSize(Container parent)
            {return new Dimension(0, 0);}
            public Dimension minimumLayoutSize(Container parent)
            {return new Dimension(0, 0);}
            public void layoutContainer(Container parent)
            {
                Insets insets = parent.getInsets();
                int w = parent.getWidth() - insets.left - insets.right;
                int h = parent.getHeight() - insets.top - insets.bottom;

				int w25 = w/4;
				int w75 = 3*w25;
				int w75_2 = w75/2;

				int h5 = h/20;
				int h10 = 2*h5;
				int h45 = 9*h5;
				int h25 = h/4;
				int h75 = 3*h25;

				randomPlugin.setBounds(0, 0, w25, h75);
				followUs.setBounds(0, h75, w25, h25);
				description.setBounds(w25, 0, w75, h10);
				latestNews.setBounds(w25, h10, w75_2, h45);
				tutorials.setBounds(w25 + w75_2, h10, w75_2, h45);
				forum.setBounds(w25, h10 + h45, w75, h45);
            }
		});

		add(randomPlugin);
		add(followUs);
		add(description);
		add(latestNews);
		add(tutorials);
		add(forum);
	}


	@Override public void paint(Graphics g)
	{
		super.paint(g);

		Graphics2D g2 = (Graphics2D)g;

		Stroke old = g2.getStroke();
		g2.setStroke(DASH_STROKE);
		g2.setColor(LINK_COLOR);

		int w = getWidth(), h = getHeight();
		int w25 = w/4;
		int w75 = 3*w25;
		int w75_2 = w75/2;

		int h5 = h/20;
		int h10 = 2*h5;
		int h45 = 9*h5;
		int h25 = h/4;
		int h75 = 3*h25;

		g2.drawLine(5, h75-5, w25-5, h75-5);
		g2.drawLine(w25, 5, w25, h-5);
		g2.drawLine(w25+5, h10, w-5, h10);
		g2.drawLine(w25+5, h10+h45, w-5, h10+h45);
		g2.drawLine(w25+w75_2, h10+5, w25+w75_2, h10+h45-5);
		
		g2.setStroke(old);
	}

}
