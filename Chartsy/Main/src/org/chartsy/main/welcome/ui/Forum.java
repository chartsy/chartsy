package org.chartsy.main.welcome.ui;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import org.chartsy.main.welcome.Feeds;
import org.chartsy.main.welcome.content.Constants;
import org.chartsy.main.welcome.content.Feed;
import org.chartsy.main.welcome.content.FeedEvent;
import org.chartsy.main.welcome.content.FeedListener;
import org.chartsy.main.welcome.content.FeedMessage;
import org.chartsy.main.welcome.content.SpringUtilities;
import org.chartsy.main.welcome.content.WebLink;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Viorel
 */
public class Forum extends JPanel implements Constants, FeedListener
{

	private JLabel loading;

	public Forum()
	{
		super(new SpringLayout());
		setOpaque(false);
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		initComponents();
		Feeds.getDefault().addFeedListener(Feeds.forumFeed, (FeedListener) this);
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

	@Override public void fireFeedParsed(FeedEvent event)
	{
		Feed feed = (Feed) event.getSource();
		if (feed.getFeedName().equals(Feeds.forumFeed))
		{
			remove(loading);

			for (int i = 0; i < feed.getMessages().size(); i++)
			{
				FeedMessage message = feed.getMessages().get(i);
				add(WebLink.createWebLink(message.getTitle(), message.getLink(), true));
			}

			SpringUtilities.makeCompactGrid(this,
				getComponentCount(), 1,
				5, 5,
				5, 5);

			revalidate();
			repaint();
		}
	}

}
