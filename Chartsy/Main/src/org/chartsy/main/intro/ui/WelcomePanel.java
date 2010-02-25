package org.chartsy.main.intro.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import org.chartsy.main.intro.content.BundleSupport;
import org.chartsy.main.intro.content.Constants;
import org.chartsy.main.intro.content.Utils;
import org.openide.awt.StatusDisplayer;

/**
 *
 * @author viorel.gheba
 */
public class WelcomePanel extends JPanel implements Constants {

    public WelcomePanel() {
        super(new BorderLayout());
        setOpaque(false);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(300, 130));

        JLabel leftTitle = new JLabel("<html>" + BundleSupport.getContent(CONTENT_LICENSE) + "</html>");
        leftTitle.setFont(NORMAL_FONT);
        leftTitle.setForeground(COLOR_LINK);
        leftTitle.setHorizontalTextPosition(SwingConstants.LEFT);
        leftTitle.setVerticalTextPosition(SwingConstants.TOP);
        
        leftPanel.add(leftTitle, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        rightPanel.setPreferredSize(new Dimension(300, 130));

        JLabel label = new JLabel("<html>Watch the Tutorial</html>");
        label.setOpaque(false);
        label.setFont(WELCOME_LABEL_FONT);
        label.setIcon(Utils.getImageIcon(IMAGE_VIDEO_THUMBNAIL));
        label.setVerticalTextPosition(SwingConstants.TOP);
        label.setHorizontalTextPosition(SwingConstants.CENTER);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setCursor(CURSOR_HAND);
        label.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) { Utils.openURL(BundleSupport.getURL(URL_TUTORIAL)); }
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
        rightPanel.add(label, BorderLayout.CENTER);

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
