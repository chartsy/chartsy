package org.chartsy.main.intro.content;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import org.openide.awt.StatusDisplayer;

/**
 *
 * @author viorel.gheba
 */
public class TopLogo extends JButton implements Constants, MouseListener, ActionListener {

    private String url;

    public TopLogo(String s) {
        setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        setCursor(CURSOR_HAND);
        setHorizontalAlignment(SwingConstants.CENTER);
        addMouseListener(this);
        setMargin(new Insets(0, 0, 0, 0));
        setBorderPainted(false);
        setFocusPainted(false);
        setContentAreaFilled(false);
        addActionListener(this);
        url = s;
    }

    public void mouseClicked(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) { if (url != null) StatusDisplayer.getDefault().setStatusText(url); }
    public void mouseExited(MouseEvent e) { StatusDisplayer.getDefault().setStatusText(""); }
    public void actionPerformed(ActionEvent e) { Utils.openURL(url); }

    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(COLOR_CONTENT_BACKGROUND);

        int width = getWidth();
        int height = getHeight();

        g2.fillRect(0, 0, width, height);

        super.paint(g);
    }

}
