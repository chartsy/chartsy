package org.chartsy.main.intro.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import org.chartsy.main.intro.content.Constants;
import org.chartsy.main.intro.content.Utils;
import org.openide.util.ImageUtilities;

/**
 *
 * @author viorel.gheba
 */
public class StartPageContent extends JPanel implements Constants {

    public StartPageContent() {
        super(new BorderLayout());
        setOpaque(false);

        add(new TopBar(), BorderLayout.NORTH);

        TabsPanel tabbedPane = new TabsPanel(TABS);
        tabbedPane.setBorder(EMPTY_BORDER);

        TabPanel panel = new TabPanel(tabbedPane);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(EMPTY_BORDER);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setPreferredSize(new Dimension(700, 100));

        JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
        if (scrollBar != null) {
            scrollBar.setBlockIncrement(30 * Utils.getDefaultFontSize());
            scrollBar.setUnitIncrement(Utils.getDefaultFontSize());
        }
        add(scrollPane, BorderLayout.CENTER);
        add(new BottomBanner(), BorderLayout.SOUTH);

        setOpaque(false);
    }

    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setPaint(COLOR_CONTENT_BACKGROUND);
        g2.fillRect(0, 0, getWidth(), getHeight());

        Image image = ImageUtilities.loadImage(IMAGE_LIGHT_EFFECT, true);
        int w = image.getWidth(this);
        int h = image.getHeight(this);
        g2.drawImage(image, (getWidth()/2 - w/2), getHeight() - h, w, h, this);
    }

    private static class TabPanel extends JPanel {

        public TabPanel(JComponent component) {
            super(new GridBagLayout());
            setOpaque(false);
            add(component, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 10, 1, new Insets(0, 0, 100, 0), 0, 0));
        }
        public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
        public int getScrollableUnitIncrement(Rectangle rectangle, int i, int j) { return Constants.FONT_SIZE; }
        public int getScrollableBlockIncrement(Rectangle rectangle, int i, int j) { return 30 * getScrollableUnitIncrement(rectangle, i, j); }
        public boolean getScrollableTracksViewportWidth() { return false; }
        public boolean getScrollableTracksViewportHeight() { return false; }

    }

}
