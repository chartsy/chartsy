package org.chartsy.main.intro.ui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import javax.swing.border.Border;
import org.chartsy.main.intro.content.Constants;
import org.chartsy.main.intro.content.Utils;

/**
 *
 * @author viorel.gheba
 */
public class ContentBottomBorder implements Border, Constants {

    private final Image bottomLeftCorner;
    private final Image bottomRightCorner;
    private static final Insets insets = new Insets(0, 0, 20, 0);
    
    public ContentBottomBorder() {
        bottomLeftCorner = Utils.getImage(IMAGE_TAB_BOTTOM_LEFT);
        bottomRightCorner = Utils.getImage(IMAGE_TAB_BOTTOM_RIGHT);
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D)g;

        g2.drawImage(bottomLeftCorner, 0, height - 20, c);
        g2.drawImage(bottomRightCorner, width - 20, height - 20, c);

        g2.setPaint(COLOR_TAB_CONTENT_BACKGROUND);
        g2.fillRect(bottomLeftCorner.getWidth(c), height - 20, width - bottomLeftCorner.getWidth(c) - bottomRightCorner.getWidth(c), 20);
    }
    
    public Insets getBorderInsets(Component c) { return (Insets)insets.clone(); }
    public boolean isBorderOpaque() { return false; }

}
