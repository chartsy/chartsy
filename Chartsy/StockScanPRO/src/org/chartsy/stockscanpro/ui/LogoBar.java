package org.chartsy.stockscanpro.ui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
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
import org.chartsy.main.utils.DesktopUtil;
import org.openide.awt.StatusDisplayer;
import org.openide.util.ImageUtilities;

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
		super(new BorderLayout());
        setOpaque(false);
        ImageIcon imageIcon = ImageUtilities.loadImageIcon(IMAGE_LOGO, true);
        LogoLink logoLink = new LogoLink();
        logoLink.setIcon(imageIcon);
        logoLink.setPressedIcon(imageIcon);
		add(logoLink, BorderLayout.CENTER);
		setPreferredSize(new Dimension(imageIcon.getIconWidth(), imageIcon.getIconHeight()));
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

            addMouseListener((MouseListener) this);
            addActionListener((ActionListener) this);
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
