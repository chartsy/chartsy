package org.chartsy.main.intro.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import org.chartsy.main.intro.content.BundleSupport;
import org.chartsy.main.intro.content.Constants;
import org.chartsy.main.intro.content.TopLogo;
import org.chartsy.main.intro.content.Utils;

/**
 *
 * @author viorel.gheba
 */
public class TopBar extends JPanel implements Constants {

    public TopBar() {
        super(new GridBagLayout());
        setPreferredSize(new Dimension(1800, 74));
        ImageIcon imageIcon = Utils.getImageIcon(IMAGE_CHARTSY_LOGO);
        TopLogo topLogo = new TopLogo(BundleSupport.getURL(URL_CHARTSY));
        topLogo.setIcon(imageIcon);
        topLogo.setPressedIcon(imageIcon);
        add(topLogo, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(5, 5, 5, 5), 0, 0));
    }

    protected void paintBorder(Graphics g) {}

    protected void paintComponent(Graphics g) {
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        
        int imageWidth = 1800;
        int imageHeight = 74;

        int panelCenterX = (panelWidth - imageWidth) / 2;
        int panelCenterY = Math.min((panelHeight - imageHeight) / 2, 0);
    }

}
