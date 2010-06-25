package org.chartsy.stockscanpro.ui;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import org.chartsy.main.intro.content.Utils;
import org.chartsy.main.utils.DesktopUtil;
import org.openide.awt.StatusDisplayer;

/**
 *
 * @author Viorel
 */
public class LogoBar extends JPanel
{

    private static final String IMAGE_LOGO = "org/chartsy/stockscanpro/resources/logo.png";
    private static final String STOCK_SCAN_PRO_URL = "http://www.stockscanpro.com";

    public LogoBar()
    {
        super(new GridBagLayout());
        setOpaque(false);
        setPreferredSize(new Dimension(1800, 155));
        ImageIcon imageIcon = Utils.getImageIcon(IMAGE_LOGO);
        LogoLink logoLink = new LogoLink();
        logoLink.setIcon(imageIcon);
        logoLink.setPressedIcon(imageIcon);
        add(logoLink, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(5, 5, 5, 5), 0, 0));
    }

    public class LogoLink extends JButton implements MouseListener, ActionListener
    {

        public LogoLink()
        {
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
            setMargin(new Insets(0, 0, 0, 0));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setHorizontalAlignment(SwingConstants.CENTER);
            setBorderPainted(false);
            setFocusPainted(false);

            addMouseListener(this);
            addActionListener(this);
        }

        public void mouseClicked(MouseEvent e) {}

        public void mousePressed(MouseEvent e) {}

        public void mouseReleased(MouseEvent e) {}

        public void mouseEntered(MouseEvent e) 
        {
            StatusDisplayer.getDefault().setStatusText(STOCK_SCAN_PRO_URL);
        }

        public void mouseExited(MouseEvent e)
        {
            StatusDisplayer.getDefault().setStatusText("");
        }

        public void actionPerformed(ActionEvent e)
        {
            try
            {
                DesktopUtil.browse(STOCK_SCAN_PRO_URL);
            }
            catch (IOException ex)
            {
                Logger.getLogger(LogoBar.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (InterruptedException ex)
            {
                Logger.getLogger(LogoBar.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (Exception ex)
            {
                Logger.getLogger(LogoBar.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
