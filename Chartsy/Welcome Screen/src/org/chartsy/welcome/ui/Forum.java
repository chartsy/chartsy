package org.chartsy.welcome.ui;

import java.awt.Graphics;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import org.chartsy.welcome.Feeds;
import org.chartsy.welcome.content.Constants;
import org.chartsy.welcome.content.Feed;
import org.chartsy.welcome.content.FeedMessage;
import org.chartsy.welcome.content.SpringUtilities;
import org.chartsy.welcome.content.WebLink;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Viorel
 */
public class Forum extends JPanel implements Constants
{

	private JLabel loading;
	private boolean initialized = false;

	public Forum()
	{
		super(new SpringLayout());
		setOpaque(false);
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		initComponents();
	}

	private void initComponents()
	{
		JLabel label;

		label = new JLabel(ImageUtilities.loadImageIcon(FORUM_ICON, true));
		label.setHorizontalAlignment(JLabel.LEFT);
		label.setOpaque(false);
		label.setBorder(BorderFactory.createEmptyBorder());
		add(label);

		loading = new JLabel("Loading content ...");
		loading.setHorizontalAlignment(JLabel.LEFT);
		loading.setOpaque(false);
		loading.setBorder(BorderFactory.createEmptyBorder());
		loading.setFont(LINK_FONT);
		loading.setForeground(LINK_COLOR);
		add(loading);

		SpringUtilities.makeCompactGrid(this,
			getComponentCount(), 1,
			5, 5,
			5, 5);
	}

	@Override public void paint(Graphics g)
	{
		Feed forum = Feeds.getDefault().getForum();
		if (forum != null)
		{
			if (!initialized)
			{
				for (int i = 0; i < forum.getMessages().size(); i++)
				{
					FeedMessage message = forum.getMessages().get(i);
					add(WebLink.createWebLink(message.getTitle(), message.getLink(), true));
				}
				initialized = true;
				remove(loading);
				SpringUtilities.makeCompactGrid(this,
					getComponentCount(), 1,
					5, 5,
					5, 5);
			}
		}

		super.paint(g);
	}

}
