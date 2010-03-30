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
import org.chartsy.main.intro.feed.Feed;
import org.chartsy.main.intro.feed.FeedMessage;
import org.chartsy.main.intro.feed.RSSFeedParser;
import org.chartsy.main.utils.DesktopUtil;
import org.openide.awt.StatusDisplayer;

/**
 *
 * @author viorel.gheba
 */
public class PluginsPanel extends JPanel implements Constants {

    public PluginsPanel() {
        super(new BorderLayout());
        setOpaque(false);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(300, 130));

        JLabel l = new JLabel();
        l.setLayout(new GridBagLayout());
        l.setOpaque(false);
        l.setVerticalAlignment(SwingConstants.TOP);
        leftPanel.add(l, BorderLayout.CENTER);

        JPanel panel;
        JLabel label;

        RSSFeedParser parser = new RSSFeedParser(BundleSupport.getURL("LastPlugin"));
        Feed feed = parser.readFeed();
        if (feed != null) {
            if (feed.getMessages().size() > 0) {
                FeedMessage feedMessage = feed.getMessages().get(0);

                panel = new JPanel(new GridBagLayout());
                panel.setOpaque(false);
                label = new JLabel("Last Uploaded Plugin");
                label.setOpaque(false);
                label.setForeground(COLOR_LINK);
                label.setFont(WELCOME_LABEL_FONT);
                panel.add(label, new GridBagConstraints(1, 0, 1, 3, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 0, 0), 0, 0));
                panel.add(new JLabel(), new GridBagConstraints(2, 0, 1, 3, 1.0D, 0.0D, 17, 1, new Insets(0, 0, 0, 0), 0, 0));
                l.add(panel, new GridBagConstraints(0, 0, 1, 1, 1.0D, 0.0D, 18, 2, new Insets(0, 0, 7, 0), 0, 0));

                panel = new JPanel(new GridBagLayout());
                panel.setOpaque(false);
                label = new JLabel(feedMessage.getTitle());
                label.setOpaque(false);
                label.setForeground(COLOR_LINK);
                label.setFont(WELCOME_LABEL_FONT);
                panel.add(label, new GridBagConstraints(1, 0, 1, 3, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 0, 0), 0, 0));
                panel.add(new JLabel(), new GridBagConstraints(2, 0, 1, 3, 1.0D, 0.0D, 17, 1, new Insets(0, 0, 0, 0), 0, 0));
                l.add(panel, new GridBagConstraints(0, 1, 1, 1, 1.0D, 0.0D, 18, 2, new Insets(0, 0, 7, 0), 0, 0));

                panel = new JPanel(new GridBagLayout());
                panel.setOpaque(false);
                label = new JLabel("<html>" + feedMessage.getDescription() + "</html>");
                label.setOpaque(false);
                label.setForeground(COLOR_LINK);
                label.setFont(DESCRIPTION_FONT);
                label.setVerticalAlignment(SwingConstants.TOP);
                label.setPreferredSize(new Dimension(300, 150));
                panel.add(label, new GridBagConstraints(1, 0, 1, 3, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 0, 0), 0, 0));
                panel.add(new JLabel(), new GridBagConstraints(2, 0, 1, 3, 1.0D, 0.0D, 17, 1, new Insets(0, 0, 0, 0), 0, 0));
                l.add(panel, new GridBagConstraints(0, 2, 1, 1, 1.0D, 0.0D, 18, 2, new Insets(0, 0, 7, 0), 0, 0));
            } else {
                panel = new JPanel(new GridBagLayout());
                panel.setOpaque(false);
                label = new JLabel("Last Uploaded Plugin");
                label.setOpaque(false);
                label.setForeground(COLOR_LINK);
                label.setFont(WELCOME_LABEL_FONT);
                panel.add(label, new GridBagConstraints(1, 0, 1, 3, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 0, 0), 0, 0));
                panel.add(new JLabel(), new GridBagConstraints(2, 0, 1, 3, 1.0D, 0.0D, 17, 1, new Insets(0, 0, 0, 0), 0, 0));
                l.add(panel, new GridBagConstraints(0, 0, 1, 1, 1.0D, 0.0D, 18, 2, new Insets(0, 0, 7, 0), 0, 0));

                panel = new JPanel(new GridBagLayout());
                panel.setOpaque(false);
                label = new JLabel(" ");
                label.setOpaque(false);
                label.setForeground(COLOR_LINK);
                label.setFont(WELCOME_LABEL_FONT);
                panel.add(label, new GridBagConstraints(1, 0, 1, 3, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 0, 0), 0, 0));
                panel.add(new JLabel(), new GridBagConstraints(2, 0, 1, 3, 1.0D, 0.0D, 17, 1, new Insets(0, 0, 0, 0), 0, 0));
                l.add(panel, new GridBagConstraints(0, 1, 1, 1, 1.0D, 0.0D, 18, 2, new Insets(0, 0, 7, 0), 0, 0));

                panel = new JPanel(new GridBagLayout());
                panel.setOpaque(false);
                label = new JLabel("<html>There are no new plugins</html>");
                label.setOpaque(false);
                label.setForeground(COLOR_LINK);
                label.setFont(DESCRIPTION_FONT);
                label.setVerticalAlignment(SwingConstants.TOP);
                label.setPreferredSize(new Dimension(300, 150));
                panel.add(label, new GridBagConstraints(1, 0, 1, 3, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 0, 0), 0, 0));
                panel.add(new JLabel(), new GridBagConstraints(2, 0, 1, 3, 1.0D, 0.0D, 17, 1, new Insets(0, 0, 0, 0), 0, 0));
                l.add(panel, new GridBagConstraints(0, 2, 1, 1, 1.0D, 0.0D, 18, 2, new Insets(0, 0, 7, 0), 0, 0));
            }
        }

        panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        label = new JLabel("<html>" + BundleSupport.getLabel("AllPlugins") + "</html>");
        label.setOpaque(false);
        label.setCursor(CURSOR_HAND);
        label.setFont(BUTTON_FONT);
        label.setForeground(COLOR_LINK);
        label.setHorizontalTextPosition(SwingConstants.RIGHT);
        label.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                //Utils.openURL(BundleSupport.getURL("AllPlugins"));
                try { DesktopUtil.browse(BundleSupport.getURL("AllPlugins")); }
                catch (Exception ex) {}
            }
            public void mouseEntered(MouseEvent e) {
                JLabel label = (JLabel) e.getSource();
                String s = label.getText();
                label.setText("<html><u>" + s.replace("<html>", "").replace("</html>", "") + "</u></html>");
                StatusDisplayer.getDefault().setStatusText(BundleSupport.getURL("AllPlugins"));
            }
            public void mouseExited(MouseEvent e) {
                JLabel label = (JLabel) e.getSource();
                String s = label.getText();
                label.setText(s.replace("<u>", "").replace("</u>", ""));
                StatusDisplayer.getDefault().setStatusText("");
            }
        });
        panel.add(label, BorderLayout.WEST);
        leftPanel.add(panel, BorderLayout.SOUTH);
        
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(false);
        rightPanel.setPreferredSize(new Dimension(300, 130));
        
        panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.add(new BulletLink("Annotations", "http://www.chartsy.org/chartsy-plugins/annotations"), new GridBagConstraints(1, 0, 1, 3, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 0, 0), 0, 0));
        panel.add(new JLabel(), new GridBagConstraints(2, 0, 1, 3, 1.0D, 0.0D, 17, 1, new Insets(0, 0, 0, 0), 0, 0));
        rightPanel.add(panel, new GridBagConstraints(0, 0, 1, 1, 1.0D, 0.0D, 18, 2, new Insets(0, 0, 7, 0), 0, 0));

        panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.add(new BulletLink("Charts", "http://www.chartsy.org/chartsy-plugins/charts"), new GridBagConstraints(1, 0, 1, 3, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 0, 0), 0, 0));
        panel.add(new JLabel(), new GridBagConstraints(2, 0, 1, 3, 1.0D, 0.0D, 17, 1, new Insets(0, 0, 0, 0), 0, 0));
        rightPanel.add(panel, new GridBagConstraints(0, 1, 1, 1, 1.0D, 0.0D, 18, 2, new Insets(0, 0, 7, 0), 0, 0));

        panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.add(new BulletLink("Data Providers", "http://www.chartsy.org/chartsy-plugins/dataproviders"),new GridBagConstraints(1, 0, 1, 3, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 0, 0), 0, 0));
        panel.add(new JLabel(), new GridBagConstraints(2, 0, 1, 3, 1.0D, 0.0D, 17, 1, new Insets(0, 0, 0, 0), 0, 0));
        rightPanel.add(panel, new GridBagConstraints(0, 2, 1, 1, 1.0D, 0.0D, 18, 2, new Insets(0, 0, 7, 0), 0, 0));

        panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.add(new BulletLink("Indicators", "http://www.chartsy.org/chartsy-plugins/indicators"), new GridBagConstraints(1, 0, 1, 3, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 0, 0), 0, 0));
        panel.add(new JLabel(), new GridBagConstraints(2, 0, 1, 3, 1.0D, 0.0D, 17, 1, new Insets(0, 0, 0, 0), 0, 0));
        rightPanel.add(panel, new GridBagConstraints(0, 3, 1, 1, 1.0D, 0.0D, 18, 2, new Insets(0, 0, 7, 0), 0, 0));

        panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.add(new BulletLink("Overlays", "http://www.chartsy.org/chartsy-plugins/overlays"), new GridBagConstraints(1, 0, 1, 3, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 0, 0), 0, 0));
        panel.add(new JLabel(), new GridBagConstraints(2, 0, 1, 3, 1.0D, 0.0D, 17, 1, new Insets(0, 0, 0, 0), 0, 0));
        rightPanel.add(panel, new GridBagConstraints(0, 4, 1, 1, 1.0D, 0.0D, 18, 2, new Insets(0, 0, 7, 0), 0, 0));

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
    }

    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;

        g2.setColor(COLOR_TAB_CONTENT_BACKGROUND);
        g2.fillRect(0, 0, getWidth(), getHeight());

        super.paint(g);
    }

}
