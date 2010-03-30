package org.chartsy.main.intro.ui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import org.chartsy.main.intro.content.BulletLink;
import org.chartsy.main.intro.content.BundleSupport;
import org.chartsy.main.intro.content.Constants;
import org.chartsy.main.intro.content.Utils;
import org.chartsy.main.utils.DesktopUtil;
import org.openide.awt.StatusDisplayer;

/**
 *
 * @author viorel.gheba
 */
public class GetStartedPanel extends JPanel implements Constants {

    public GetStartedPanel() {
        super(new BorderLayout());
        setOpaque(false);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(300, 130));

        JLabel leftLabel = new JLabel("<html>Watch the Tutorial</html>");
        leftLabel.setOpaque(false);
        leftLabel.setFont(WELCOME_LABEL_FONT);
        leftLabel.setIcon(Utils.getImageIcon(IMAGE_VIDEO_THUMBNAIL));
        leftLabel.setVerticalTextPosition(SwingConstants.TOP);
        leftLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        leftLabel.setHorizontalAlignment(SwingConstants.CENTER);
        leftLabel.setVerticalAlignment(SwingConstants.CENTER);
        leftLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        leftLabel.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                try { DesktopUtil.browse(BundleSupport.getURL(URL_TUTORIAL)); }
                catch (Exception ex) {}
            }
            public void mousePressed(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {
                JLabel label = (JLabel) e.getSource();
                label.setText("<html><u>Watch the Tutorial</u></html>");
                StatusDisplayer.getDefault().setStatusText(BundleSupport.getURL(URL_TUTORIAL));
            }
            public void mouseExited(MouseEvent e) {
                JLabel label = (JLabel) e.getSource();
                label.setText("<html>Watch the Tutorial</html>");
                StatusDisplayer.getDefault().setStatusText("");
            }
        });
        leftPanel.add(leftLabel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(false);
        rightPanel.setPreferredSize(new Dimension(300, 130));

        JPanel panel;

        panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.add(new BulletLink("Features", "http://www.chartsy.org/features"), new GridBagConstraints(1, 0, 1, 3, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 0, 0), 0, 0));
        panel.add(new JLabel(), new GridBagConstraints(2, 0, 1, 3, 1.0D, 0.0D, 17, 1, new Insets(0, 0, 0, 0), 0, 0));
        rightPanel.add(panel, new GridBagConstraints(0, 0, 1, 1, 1.0D, 0.0D, 18, 2, new Insets(0, 0, 7, 0), 0, 0));

        panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.add(new BulletLink("Plugins", "http://www.chartsy.org/chartsy-plugins"), new GridBagConstraints(1, 0, 1, 3, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 0, 0), 0, 0));
        panel.add(new JLabel(), new GridBagConstraints(2, 0, 1, 3, 1.0D, 0.0D, 17, 1, new Insets(0, 0, 0, 0), 0, 0));
        rightPanel.add(panel, new GridBagConstraints(0, 1, 1, 1, 1.0D, 0.0D, 18, 2, new Insets(0, 0, 7, 0), 0, 0));

        panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.add(new BulletLink("Doc & Support", "http://www.chartsy.org/docs-and-support"),new GridBagConstraints(1, 0, 1, 3, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 0, 0), 0, 0));
        panel.add(new JLabel(), new GridBagConstraints(2, 0, 1, 3, 1.0D, 0.0D, 17, 1, new Insets(0, 0, 0, 0), 0, 0));
        rightPanel.add(panel, new GridBagConstraints(0, 2, 1, 1, 1.0D, 0.0D, 18, 2, new Insets(0, 0, 7, 0), 0, 0));

        panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.add(new BulletLink("Community", "http://www.chartsy.org/community"), new GridBagConstraints(1, 0, 1, 3, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 0, 0), 0, 0));
        panel.add(new JLabel(), new GridBagConstraints(2, 0, 1, 3, 1.0D, 0.0D, 17, 1, new Insets(0, 0, 0, 0), 0, 0));
        rightPanel.add(panel, new GridBagConstraints(0, 3, 1, 1, 1.0D, 0.0D, 18, 2, new Insets(0, 0, 7, 0), 0, 0));

        panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.add(new BulletLink("Partners", "http://www.chartsy.org/partners"), new GridBagConstraints(1, 0, 1, 3, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 0, 0), 0, 0));
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
