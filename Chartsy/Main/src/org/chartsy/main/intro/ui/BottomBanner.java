package org.chartsy.main.intro.ui;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Random;
//import java.util.Timer;
//import java.util.TimerTask;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.chartsy.main.intro.content.BundleSupport;
import org.chartsy.main.intro.content.Constants;
import org.chartsy.main.intro.content.Utils;
import org.openide.awt.StatusDisplayer;
import sun.awt.image.URLImageSource;

/**
 *
 * @author viorel.gheba
 */
public class BottomBanner extends JPanel implements Constants, MouseListener, ActionListener {

    private String url = "";
    private Random random = new Random();
    private String cookieValue = "";
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
            URL urlLink = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlLink.openConnection();
            connection.setRequestProperty("Cookie", cookieValue);
            connection.setFollowRedirects(true);
            connection.connect();

            Cursor cursor = bottomLink.getCursor();
            bottomLink.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            InputStream receiving = connection.getInputStream();
            receiving.close();
            bottomLink.setCursor(cursor);

            Utils.openURL(connection.getURL().toString());
        } catch (Exception ex) {}
    }

    public Image getImageFromURL(String urlString) {
        try {
            CookieManager cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            CookieHandler.setDefault(cookieManager);

            URL urlLink = new URL(urlString);
            URLConnection connection = urlLink.openConnection();
            connection.setDoOutput(true);
            Object obj = connection.getContent();

            urlLink = new URL(urlString);
            connection = urlLink.openConnection();
            connection.setDoInput(true);
            obj = connection.getContent();

            CookieStore cookieStore = cookieManager.getCookieStore();
            List<HttpCookie> cookies = cookieStore.getCookies();
            for (HttpCookie cookie : cookies)
                cookieValue = cookie.getName() + "=" + cookie.getValue();

            if (obj instanceof URLImageSource) {
                URLImageSource imageSource = (URLImageSource) obj;
                Image image = Toolkit.getDefaultToolkit().createImage(imageSource);
                return image;
            }
            return null;
        } catch (Exception e) {}

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
