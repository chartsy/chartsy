package org.chartsy.main.intro.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import org.chartsy.main.intro.content.BulletLink;
import org.chartsy.main.intro.content.BundleSupport;
import org.chartsy.main.intro.content.Constants;
import org.chartsy.main.intro.content.Utils;
import org.chartsy.main.intro.feed.Feed;
import org.chartsy.main.intro.feed.FeedMessage;
import org.chartsy.main.intro.feed.RSSFeedParser;
import org.openide.awt.StatusDisplayer;

/**
 *
 * @author viorel.gheba
 */
public class ForumPanel extends JPanel implements Constants {

    public ForumPanel() {
        super(new BorderLayout());
        setOpaque(false);

        JPanel main = new JPanel(new GridBagLayout());
        main.setOpaque(false);
        main.setPreferredSize(new Dimension(600, 130));

        JPanel panel;
        JLabel label;
        int i = 0;

        RSSFeedParser parser = new RSSFeedParser(BundleSupport.getURL("Forum"));
        Feed feed = parser.readFeed();
        for (FeedMessage feedMessage : feed.getMessages()) {
            panel = new JPanel(new BorderLayout());
            panel.setOpaque(false);
            panel.add(new BulletLink(feedMessage.getTitle(), feedMessage.getLink()), BorderLayout.NORTH);
            label = new JLabel("<html>" + feedMessage.getDescription() + "</html>");
            label.setOpaque(false);
            label.setFont(DESCRIPTION_FONT);
            label.setForeground(COLOR_LINK);
            panel.add(label, BorderLayout.CENTER);
            main.add(panel, new GridBagConstraints(0, i, 1, 1, 1.0D, 0.0D, 18, 2, new Insets(0, 0, 7, 0), 0, 0));
            i++;
        }

        panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        label = new JLabel("<html>" + BundleSupport.getLabel("AllTopics") + "</html>");
        label.setOpaque(false);
        label.setCursor(CURSOR_HAND);
        label.setFont(BUTTON_FONT);
        label.setForeground(COLOR_LINK);
        label.setHorizontalTextPosition(SwingConstants.RIGHT);
        label.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { Utils.openURL(BundleSupport.getURL("AllTopics")); }
            public void mouseEntered(MouseEvent e) { 
                JLabel label = (JLabel) e.getSource();
                String s = label.getText();
                label.setText("<html><u>" + s.replace("<html>", "").replace("</html>", "") + "</u></html>");
                StatusDisplayer.getDefault().setStatusText(BundleSupport.getURL("AllTopics"));
            }
            public void mouseExited(MouseEvent e) {
                JLabel label = (JLabel) e.getSource();
                String s = label.getText();
                label.setText(s.replace("<u>", "").replace("</u>", ""));
                StatusDisplayer.getDefault().setStatusText("");
            }
        });
        panel.add(label, BorderLayout.EAST);
        main.add(panel, new GridBagConstraints(0, i, 1, 1, 1.0D, 0.0D, 18, 2, new Insets(0, 0, 7, 0), 0, 0));

        add(main, BorderLayout.CENTER);
    }

     public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;

        g2.setColor(COLOR_TAB_CONTENT_BACKGROUND);
        g2.fillRect(0, 0, getWidth(), getHeight());

        super.paint(g);
    }

}
