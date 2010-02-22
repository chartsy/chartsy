package org.chartsy.main.intro.content;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import org.openide.awt.StatusDisplayer;

/**
 *
 * @author viorel.gheba
 */
public class ContentLogo extends JLabel implements Constants, MouseListener {

    private String url;
    private Image image;

    public ContentLogo(String s, Image i) {
        setOpaque(false);

        setBorder(BorderFactory.createEmptyBorder());
        setCursor(Cursor.getPredefinedCursor(12));
        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);

        addMouseListener(this);
        
        url = s;
        image = i;
    }

    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        
        g2.setPaint(COLOR_TAB_CONTENT_BACKGROUND);
        g2.fillRect(0, 0, getWidth(), getHeight());

        int w = image.getWidth(this);
        int h = image.getHeight(this);
        g2.drawImage(image, (getWidth()/2 - w/2), (getHeight()/2 - h/2), w, h, this);

        super.paint(g);
    }


    public void mouseClicked(MouseEvent e) { Utils.openURL(url); }
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) { if (url != null) StatusDisplayer.getDefault().setStatusText(url); }
    public void mouseExited(MouseEvent e) { StatusDisplayer.getDefault().setStatusText(""); }

}
