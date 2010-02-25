package org.chartsy.main.intro.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import org.chartsy.main.intro.content.BundleSupport;
import org.chartsy.main.intro.content.Constants;
import org.chartsy.main.intro.content.TopLogo;
import org.chartsy.main.intro.content.Utils;
import org.openide.util.ImageUtilities;

/**
 *
 * @author viorel.gheba
 */
public class TopBar extends JPanel implements Constants {

    public TopBar() {
        super(new GridBagLayout());
        setOpaque(false);
        setPreferredSize(new Dimension(1800, 74));
        ImageIcon imageIcon = Utils.getImageIcon(IMAGE_CHARTSY_LOGO);
        TopLogo topLogo = new TopLogo(BundleSupport.getURL(URL_CHARTSY));
        topLogo.setIcon(imageIcon);
        topLogo.setPressedIcon(imageIcon);
        add(topLogo, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(5, 5, 5, 5), 0, 0));
    }

    protected void paintBorder(Graphics g) {}

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D)g;

        Image image = ImageUtilities.loadImage(IMAGE_LIGHT_EFFECT, true);
        g2.drawImage(image, 0, 0, this);
    }

}
