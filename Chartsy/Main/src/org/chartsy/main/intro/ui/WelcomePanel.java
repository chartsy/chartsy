package org.chartsy.main.intro.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import org.chartsy.main.intro.content.BulletLink;
import org.chartsy.main.intro.content.BundleSupport;
import org.chartsy.main.intro.content.Constants;
import org.chartsy.main.intro.feed.Feed;
import org.chartsy.main.intro.feed.FeedMessage;
import org.chartsy.main.intro.feed.RSSFeedParser;

/**
 *
 * @author viorel.gheba
 */
public class WelcomePanel extends JPanel implements Constants
{

    public WelcomePanel()
    {
        super(new BorderLayout());
        setOpaque(false);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(300, 130));

        JLabel leftLabel = new JLabel();
        leftLabel.setLayout(new GridBagLayout());
        leftLabel.setOpaque(false);
        leftLabel.setVerticalAlignment(SwingConstants.TOP);

        JPanel panel;
        JLabel label;

        RSSFeedParser parser = new RSSFeedParser(BundleSupport.getURL("LastPlugin"));
        Feed feed = parser.readFeed();
        if (feed != null)
        {
            if (feed.getMessages().size() > 0)
            {
                FeedMessage feedMessage = feed.getMessages().get(0);

                panel = new JPanel(new GridBagLayout());
                panel.setOpaque(false);
                label = new JLabel("Last Uploaded Plugin");
                label.setOpaque(false);
                label.setForeground(COLOR_TEXT);
                label.setFont(GET_STARTED_FONT);
                panel.add(label, new GridBagConstraints(1, 0, 1, 3, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 0, 0), 0, 0));
                panel.add(new JLabel(), new GridBagConstraints(2, 0, 1, 3, 1.0D, 0.0D, 17, 1, new Insets(0, 0, 0, 0), 0, 0));
                leftLabel.add(panel, new GridBagConstraints(0, 0, 1, 1, 1.0D, 0.0D, 18, 2, new Insets(0, 0, 7, 0), 0, 0));

                panel = new JPanel(new GridBagLayout());
                panel.setOpaque(false);
                label = new JLabel(feedMessage.getTitle());
                label.setOpaque(false);
                label.setForeground(COLOR_TEXT);
                label.setFont(GET_STARTED_FONT);
                panel.add(label, new GridBagConstraints(1, 0, 1, 3, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 0, 0), 0, 0));
                panel.add(new JLabel(), new GridBagConstraints(2, 0, 1, 3, 1.0D, 0.0D, 17, 1, new Insets(0, 0, 0, 0), 0, 0));
                leftLabel.add(panel, new GridBagConstraints(0, 1, 1, 1, 1.0D, 0.0D, 18, 2, new Insets(0, 0, 7, 0), 0, 0));

                panel = new JPanel(new GridBagLayout());
                panel.setOpaque(false);
                label = new JLabel("<html>" + feedMessage.getDescription() + "</html>");
                label.setOpaque(false);
                label.setForeground(COLOR_TEXT);
                label.setFont(WELCOME_LABEL_FONT);
                label.setPreferredSize(new Dimension(250, 150));
                panel.add(label, new GridBagConstraints(1, 0, 1, 3, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 0, 0), 0, 0));
                panel.add(new JLabel(), new GridBagConstraints(2, 0, 1, 3, 1.0D, 0.0D, 17, 1, new Insets(0, 0, 0, 0), 0, 0));
                leftLabel.add(panel, new GridBagConstraints(0, 2, 1, 1, 1.0D, 0.0D, 18, 2, new Insets(0, 0, 7, 0), 0, 0));
            } else
            {
                panel = new JPanel(new GridBagLayout());
                panel.setOpaque(false);
                label = new JLabel("Last Uploaded Plugin");
                label.setOpaque(false);
                label.setForeground(COLOR_TEXT);
                label.setFont(GET_STARTED_FONT);
                panel.add(label, new GridBagConstraints(1, 0, 1, 3, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 0, 0), 0, 0));
                panel.add(new JLabel(), new GridBagConstraints(2, 0, 1, 3, 1.0D, 0.0D, 17, 1, new Insets(0, 0, 0, 0), 0, 0));
                leftLabel.add(panel, new GridBagConstraints(0, 0, 1, 1, 1.0D, 0.0D, 18, 2, new Insets(0, 0, 7, 0), 0, 0));

                panel = new JPanel(new GridBagLayout());
                panel.setOpaque(false);
                label = new JLabel(" ");
                label.setOpaque(false);
                label.setForeground(COLOR_TEXT);
                label.setFont(GET_STARTED_FONT);
                panel.add(label, new GridBagConstraints(1, 0, 1, 3, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 0, 0), 0, 0));
                panel.add(new JLabel(), new GridBagConstraints(2, 0, 1, 3, 1.0D, 0.0D, 17, 1, new Insets(0, 0, 0, 0), 0, 0));
                leftLabel.add(panel, new GridBagConstraints(0, 1, 1, 1, 1.0D, 0.0D, 18, 2, new Insets(0, 0, 7, 0), 0, 0));

                panel = new JPanel(new GridBagLayout());
                panel.setOpaque(false);
                label = new JLabel("<html>There are no new plugins</html>");
                label.setOpaque(false);
                label.setForeground(COLOR_TEXT);
                label.setFont(WELCOME_LABEL_FONT);
                label.setPreferredSize(new Dimension(250, 150));
                panel.add(label, new GridBagConstraints(1, 0, 1, 3, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 0, 0), 0, 0));
                panel.add(new JLabel(), new GridBagConstraints(2, 0, 1, 3, 1.0D, 0.0D, 17, 1, new Insets(0, 0, 0, 0), 0, 0));
                leftLabel.add(panel, new GridBagConstraints(0, 2, 1, 1, 1.0D, 0.0D, 18, 2, new Insets(0, 0, 7, 0), 0, 0));
            }
        }

        leftPanel.add(leftLabel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        rightPanel.setPreferredSize(new Dimension(300, 130));

        JLabel rightLabel = new JLabel();
        rightLabel.setLayout(new GridBagLayout());
        rightLabel.setOpaque(false);
        rightLabel.setVerticalAlignment(SwingConstants.TOP);

        JPanel rpanel;
        JLabel rlabel;

        parser = new RSSFeedParser(BundleSupport.getURL("Announcements"));
        feed = parser.readFeed();
        if (feed != null)
        {
            if (feed.getMessages().size() > 0)
            {
                /*
                rpanel = new JPanel(new GridBagLayout());
                rpanel.setOpaque(false);
                rlabel = new JLabel("Last Forum Topics");
                rlabel.setOpaque(false);
                rlabel.setForeground(COLOR_TEXT);
                rlabel.setFont(GET_STARTED_FONT);
                rpanel.add(rlabel, new GridBagConstraints(1, 0, 1, 3, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 0, 0), 0, 0));
                rpanel.add(new JLabel(), new GridBagConstraints(2, 0, 1, 3, 1.0D, 0.0D, 17, 1, new Insets(0, 0, 0, 0), 0, 0));
                rightLabel.add(rpanel, new GridBagConstraints(0, 0, 1, 1, 1.0D, 0.0D, 18, 2, new Insets(0, 0, 7, 0), 0, 0));
                 *
                 */

                rpanel = new JPanel(new GridBagLayout());
                rpanel.setOpaque(false);
                rlabel = new JLabel("Chartsy Announcements");
                rlabel.setOpaque(false);
                rlabel.setForeground(COLOR_TEXT);
                rlabel.setFont(GET_STARTED_FONT);
                rpanel.add(rlabel, new GridBagConstraints(1, 0, 1, 3, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 0, 0), 0, 0));
                rpanel.add(new JLabel(), new GridBagConstraints(2, 0, 1, 3, 1.0D, 0.0D, 17, 1, new Insets(0, 0, 0, 0), 0, 0));
                rightLabel.add(rpanel, new GridBagConstraints(0, 1, 1, 1, 1.0D, 0.0D, 18, 2, new Insets(0, 0, 7, 0), 0, 0));

                int i = 0;
                rpanel = new JPanel(new GridBagLayout());
                rpanel.setOpaque(false);
                rlabel = new JLabel();
                rlabel.setLayout(new GridBagLayout());
                rlabel.setOpaque(false);
                rlabel.setForeground(COLOR_LINK);
                rlabel.setFont(WELCOME_LABEL_FONT);
                rlabel.setVerticalAlignment(SwingConstants.TOP);
                rlabel.setPreferredSize(new Dimension(300, 150));
                for (FeedMessage feedMessage : feed.getMessages())
                {
                    BulletLink bl = new BulletLink(feedMessage.getPubDate() + " " + feedMessage.getTitle(), feedMessage.getLink(), false);
                    bl.setLinkFont(WELCOME_LABEL_FONT);
                    bl.setLinkColor(COLOR_LINK);
                    rlabel.add(bl, new GridBagConstraints(0, i, 1, 1, 1.0D, 0.0D, 18, 2, new Insets(0, 0, 7, 0), 0, 0));
                    i++;
                }
                rpanel.add(rlabel, new GridBagConstraints(1, 0, 1, 3, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 0, 0), 0, 0));
                rpanel.add(new JLabel(), new GridBagConstraints(2, 0, 1, 3, 1.0D, 0.0D, 17, 1, new Insets(0, 0, 0, 0), 0, 0));
                rightLabel.add(rpanel, new GridBagConstraints(0, 2, 1, 1, 1.0D, 0.0D, 18, 2, new Insets(0, 0, 7, 0), 0, 0));
            }
        }

        rightPanel.add(rightLabel, BorderLayout.CENTER);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
    }

    public void paint(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(COLOR_TAB_CONTENT_BACKGROUND);
        g2.fillRect(0, 0, getWidth(), getHeight());

        super.paint(g);
    }
}
