package org.chartsy.welcome.ui;

import java.awt.Graphics;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
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
public class LatestNews extends JPanel implements Constants
{

	private JLabel loading;
	private boolean initialized = false;

	public LatestNews()
	{
		super(new SpringLayout());
		setOpaque(false);
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		initComponents();
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

	@Override public void paint(Graphics g)
	{
		Feed news = Feeds.getDefault().getLatestNews();
		if (news != null)
		{
			if (!initialized)
			{
				for (int i = 0; i < news.getMessages().size(); i++)
				{
					FeedMessage message = news.getMessages().get(i);
					add(WebLink.createWebLink(message.getTitle(), message.getLink(), true));
					if (i == 0)
					{
						JTextArea textArea = new JTextArea(message.getDescription());
						textArea.setEditable(false);
						textArea.setOpaque(false);
						textArea.setFont(RSS_DESCRIPTION_FONT);
						textArea.setForeground(LINK_COLOR);
						textArea.setLineWrap(true);
						textArea.setWrapStyleWord(true);

						JScrollPane scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
						scrollPane.getViewport().setOpaque(false);
						scrollPane.getViewport().setBorder(null);
						scrollPane.setOpaque(false);
						scrollPane.setBorder(null);
						add(scrollPane);
					}
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
