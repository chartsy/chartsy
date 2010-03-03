package org.chartsy.main.intro.ui;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Date;
import java.util.Random;
//import java.util.Timer;
//import java.util.TimerTask;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.chartsy.main.intro.content.BundleSupport;
import org.chartsy.main.intro.content.Constants;
import org.chartsy.main.utils.DesktopUtil;
import org.openide.awt.StatusDisplayer;

/**
 *
 * @author viorel.gheba
 */
public class BottomBanner extends JPanel implements Constants, MouseListener, ActionListener {

    private String url = "";
    private Random random = new Random();
    private String cookieName;
    private String cookieValue;
    private String cookieDomain;
    private String cookiePath;
    private Date cookieExpires;
    private boolean cookieSecure;
    //private Timer timer;

    private JButton bottomLink;

    public BottomBanner() {
        super(new GridBagLayout());
        setOpaque(false);
        setPreferredSize(new Dimension(1800, 100));

        bottomLink = new JButton();
        bottomLink.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        bottomLink.setCursor(Cursor.getPredefinedCursor(12));
        bottomLink.setHorizontalAlignment(2);
        bottomLink.addMouseListener(this);
        bottomLink.setMargin(new Insets(0, 0, 0, 0));
        bottomLink.setBorderPainted(false);
        bottomLink.setFocusPainted(false);
        bottomLink.setContentAreaFilled(false);
        bottomLink.addActionListener(this);

        int i = getRandomNumber();
        Image image = getImageFromURL(getImageURL(i));
        if (image != null) {
            url = getURL(i);
            ImageIcon imageIcon = new ImageIcon(image);

            bottomLink.setIcon(imageIcon);
            bottomLink.setPressedIcon(imageIcon);
        }

        add(bottomLink, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(5, 5, 5, 5), 0, 0));

        //timer = new Timer();
        //timer.schedule(new PeriodTimer(), 2000, 2000);
    }

    private int getRandomNumber() {
        int n = 1000;
        int rand = random.nextInt(n);
        return Math.abs(rand);
    }

    private String getURL(int i) {
        String urlString = BundleSupport.getURL(URL_BANNER_LINK);
        return urlString.replace("{0}", "" + i);
    }

    private String getImageURL(int i) {
        String s = BundleSupport.getURL(URL_BANNER_IMAGE_LINK);
        return s.replace("{0}", "" + i);
    }

    public void mouseClicked(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) { StatusDisplayer.getDefault().setStatusText(url); }
    public void mouseExited(MouseEvent e) { StatusDisplayer.getDefault().setStatusText(""); }
    public void actionPerformed(ActionEvent e) {
        try {
            bottomLink.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            HttpClient client = new HttpClient();
            Cookie cookie = new Cookie(cookieDomain, cookieName, cookieValue, cookiePath, cookieExpires, cookieSecure);
            client.getState().addCookie(cookie);
            HttpMethod method = new GetMethod(url.toString());
            method.setFollowRedirects(true);
            client.executeMethod(method);

            try {
                String uri = method.getURI().getHost() + (method.getURI().getPath().equals("/") ? "" : method.getURI().getPath());
                DesktopUtil.browse(uri);
            } catch (Exception exc) { exc.printStackTrace(); }
            
            method.setFollowRedirects(false);
            method.releaseConnection();
            bottomLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Image getImageFromURL(String urlString) {
        try {
            HttpClient client = new HttpClient();
            HttpMethod method = new GetMethod(urlString);
            client.executeMethod(method);

            Cookie[] cookies = client.getState().getCookies();
            for (int i = 0; i < cookies.length; i++) {
                if (cookies[i].getName().equals("OAVARS[acaa7ff1]")) {
                    cookieName = cookies[i].getName();
                    cookieValue = cookies[i].getValue();
                    cookieDomain = cookies[i].getDomain();
                    cookiePath = cookies[i].getPath();
                    cookieExpires = cookies[i].getExpiryDate();
                    cookieSecure = cookies[i].getSecure();
                }
            }

            Image image = ImageIO.read(method.getResponseBodyAsStream());
            method.releaseConnection();
            return image;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /*final class PeriodTimer extends TimerTask {
        public void run() {
            int i = getRandomNumber();
            String urlString = getURL(i);
            String urlImage = getImageURL(i);

            Image image = Utils.getImageFromURL(urlImage);
            if (image != null) {
                ImageIcon imageIcon = new ImageIcon(image);

                bottomLink.setIcon(imageIcon);
                bottomLink.setPressedIcon(imageIcon);
                url = urlString;

                bottomLink.repaint();
            }
        }
    }*/

}
