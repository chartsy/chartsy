package org.chartsy.main.intro.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import org.chartsy.main.intro.content.BundleSupport;
import org.chartsy.main.intro.content.Constants;
import org.chartsy.main.intro.content.ContentLogo;
import org.chartsy.main.intro.content.ContentPanels;
import org.chartsy.main.intro.content.Utils;
import org.openide.util.ImageUtilities;

/**
 *
 * @author viorel.gheba
 */
public class TabsPanel extends JPanel implements Constants {

    private LinkedList<TabButton> tabs = new LinkedList<TabButton>();
    private JPanel tabHeader;
    private JPanel tabContent;
    private ContentLogo twitterLogo;
    private ContentLogo facebookLogo;

    public TabsPanel(String[] labels) {
        super(new BorderLayout());
        setOpaque(false);
        
        for (int i = 0; i < labels.length; i++) tabs.add(new TabButton(labels[i]));

        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean selected = true;
                int tab = -1;
                Object obj = e.getSource();
                if (obj != null && (obj instanceof TabButton)) {
                    for (int i = 0; i < tabs.size(); i++) {
                        if (tabs.get(i) == obj) { tabs.get(i).setSelected(selected); tab = i; }
                        else { tabs.get(i).setSelected(!selected); }
                    }
                }
                switchTab(tab);
            }
        };
        for (TabButton tabButton : tabs) tabButton.addActionListener(actionListener);

        tabHeader = new TabHeader(tabs);
        add(tabHeader, BorderLayout.NORTH);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new ContentBottomBorder());

        twitterLogo = new ContentLogo(BundleSupport.getURL(URL_TWITTER), Utils.getImage(IMAGE_TWITTER_LOGO));
        twitterLogo.setPreferredSize(new Dimension(50, 250));

        facebookLogo = new ContentLogo(BundleSupport.getURL(URL_FACEBOOK), Utils.getImage(IMAGE_FACEBOOK_LOGO));
        facebookLogo.setPreferredSize(new Dimension(50, 250));

        tabContent = new JPanel(new GridBagLayout());
        tabContent.setOpaque(false);

        panel.add(twitterLogo, BorderLayout.WEST);
        panel.add(tabContent, BorderLayout.CENTER);
        panel.add(facebookLogo, BorderLayout.EAST);
        
        add(panel, BorderLayout.CENTER);

        switchTab(0);
    }

    private void switchTab(int tab) {
        for (int i = 0; i < tabs.size(); i++) 
            if (!tabs.get(i).contentPanelAdded)
                tabs.get(i).addContentPanel(tabContent, i);

        if (tab != -1) {
            TabButton selected = tabs.get(tab);
            selected.setSelected(true); selected.setState(TabButton.SELECTED);
            
            for (int i = 0; i < tabs.size(); i++) 
                if (i != tab) {
                    tabs.get(i).setSelected(false);
                    tabs.get(i).setState(TabButton.UNSELECTED);
                }

            for (Component c : tabContent.getComponents()) c.setVisible(false);
            tabContent.getComponent(tab).setVisible(true);

            repaint();
        }
    }


    private static class TabHeader extends JPanel {

        public TabHeader(LinkedList<TabButton> tabs) {
            super(new GridLayout(1, 0));
            setOpaque(false);
            
            Border border = BorderFactory.createEmptyBorder();
            for (TabButton tabButton : tabs) {
                tabButton.setBorder(border);
                add(tabButton);
            }
        }

        protected void paintBorder(Graphics g) {}

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;

            int width = getWidth();

            Image topBackground = ImageUtilities.loadImage(IMAGE_TAB_CONTENT_TOP, true);
            g2.drawImage(topBackground, 0, 35, width, topBackground.getHeight(this), this);
            
            super.paintComponent(g);
        }

    }

    private static class TabButton extends JLabel {

        public static final int UNSELECTED = 0;
        public static final int SELECTED = 1;
        public static final int HOVER = 2;

        private JPanel contentPanel;
        private boolean contentPanelAdded = false;
        private boolean isSelected = false;
        private ActionListener actionListener;
        private int state = 0;
        static final boolean assertionsDisabled = !TabsPanel.class.desiredAssertionStatus();

        public TabButton(String label) {
            super(BundleSupport.getLabel(label));
            setOpaque(false);
            setPreferredSize(new Dimension(175, 50));

            contentPanel = ContentPanels.getPanel(label);
            contentPanelAdded = false;
            isSelected = false;

            setFont(TAB_FONT);
            setForeground(isSelected ? COLOR_TAB_TEXT : COLOR_TAB_TEXT_HOVER);
            setHorizontalAlignment(0);
            setFocusable(true);
            addMouseListener(new MouseListener() {
                public void mouseClicked(MouseEvent e) { 
                    setSelected(!isSelected);
                    setState(SELECTED);
                    if(null != actionListener) actionListener.actionPerformed(new ActionEvent(TabButton.this, 0, "clicked"));
                }
                public void mousePressed(MouseEvent e) {}
                public void mouseReleased(MouseEvent e) {}
                public void mouseEntered(MouseEvent e) {
                    if(!isSelected) {
                        setCursor(Cursor.getPredefinedCursor(12));
                        setForeground(COLOR_TAB_TEXT_HOVER);
                        setState(HOVER);
                        repaint();
                    } else {
                        setCursor(Cursor.getDefaultCursor());
                        setForeground(COLOR_TAB_TEXT);
                        setState(SELECTED);
                        repaint();
                    }
                }
                public void mouseExited(MouseEvent e) {
                    setCursor(Cursor.getDefaultCursor());
                    setForeground(isSelected ? COLOR_TAB_TEXT : COLOR_TAB_TEXT_HOVER);
                    setState(!isSelected ? UNSELECTED : SELECTED);
                    repaint();
                }
            });
        }

        private void addContentPanel(JComponent component, int location) {
            component.add(contentPanel, new GridBagConstraints(location, 0, 1, 1, 1.0D, 1.0D, 10, 1, new Insets(0, 0, 0, 0), 0, 0));
            contentPanelAdded = true;
        }

        private void setState(int i) { state = i; }

        private void setSelected(boolean b) {
            isSelected = b;
            setForeground(isFocusOwner() ? COLOR_TAB_TEXT_HOVER : (isSelected ? COLOR_TAB_TEXT : COLOR_TAB_TEXT_HOVER));
            setFocusable(!b);
            if (null != getParent()) getParent().repaint();
        }

        public void addActionListener(ActionListener actionlistener) {
            if (assertionsDisabled && null != actionListener) { throw new AssertionError(); }
            else {
                actionListener = actionlistener;
                return;
            }
        }

        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D)g;

            int width = getWidth();
            int height = getHeight();
            Image background;

            switch (state) {
                case UNSELECTED:
                    // do nothing
                    break;
                case SELECTED:
                    background = ImageUtilities.loadImage(IMAGE_TAB_SELECTED, true);
                    g2.drawImage(background, 0, 0, width, height, this);
                    break;
                case HOVER:
                    background = ImageUtilities.loadImage(IMAGE_TAB_HOVER, true);
                    g2.drawImage(background, 0, 10, width, height - 10, this);
                    break;
            }

            super.paint(g);
        }

    }

}
