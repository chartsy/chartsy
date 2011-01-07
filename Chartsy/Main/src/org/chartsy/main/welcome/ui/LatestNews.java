package org.chartsy.main.welcome.ui;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
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
public class LatestNews extends JPanel implements Constants, FeedListener
{

	private JLabel loading;

	public LatestNews()
	{
		super(new SpringLayout());
		setOpaque(false);
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		initComponents();
		Feeds.getDefault().addFeedListener(Feeds.latestNewsFeed, (FeedListener) this);
	}

	private void initComponents()
	{
		JLabel label;

		label = new JLabel(ImageUtilities.loadImageIcon(NEWS_ICON, true));
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
		if (feed.getFeedName().equals(Feeds.latestNewsFeed))
		{
			remove(loading);

			if (!feed.getMessages().isEmpty())
			{
				int i = 0;
				for (FeedMessage message : feed.getMessages())
				{
					add(WebLink.createWebLink(message.getTitle(), message.getLink(), true));
					if (i == 0)
					{
						JTextArea textArea = new JTextArea();
						textArea.setText(message.getDescription());
						textArea.setEditable(false);
						textArea.setOpaque(false);
						textArea.setFont(RSS_DESCRIPTION_FONT);
						textArea.setForeground(LINK_COLOR);
						textArea.setLineWrap(true);
						textArea.setWrapStyleWord(true);

						JScrollPane scrollPane = new JScrollPane(textArea,
							JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
							JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
						scrollPane.getViewport().setOpaque(false);
						scrollPane.getViewport().setBorder(null);
						scrollPane.setOpaque(false);
						scrollPane.setBorder(null);
						add(scrollPane);
					}
					i++;
				}

				SpringUtilities.makeCompactGrid(this,
					getComponentCount(), 1,
					5, 5,
					5, 5);
			}

			revalidate();
			repaint();
		}
	}

}
