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
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
            CookieHandler.setDefault(new ListCookieHandler());
            
            URL urlLink = new URL(urlString);
            URLConnection connection = urlLink.openConnection();
            connection.setDoOutput(true);
            Object obj = connection.getContent();

            urlLink = new URL(urlString);
            connection = urlLink.openConnection();
            connection.setDoInput(true);
            obj = connection.getContent();

            Map<String, List<String>> map = CookieHandler.getDefault().get(urlLink.toURI(), new HashMap<String, List<String>>());
            Iterator it = map.keySet().iterator();
            while (it.hasNext()) {
                Object key = it.next();
                @SuppressWarnings("element-type-mismatch")
                List<String> value = map.get(key);
                cookieValue = value.get(0);
            }

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

    private class ListCookieHandler extends CookieHandler {

        private List<Cookie> cookieJar = new LinkedList<Cookie>();

        public Map<String, List<String>> get(URI uri, Map<String, List<String>> requestHeaders) throws IOException {
            StringBuilder cookies = new StringBuilder();
            for (Cookie cookie : cookieJar) {
                // Remove cookies that have expired
                if (cookie.hasExpired()) {
                    cookieJar.remove(cookie);
                } else if (cookie.matches(uri)) {
                    if (cookies.length() > 0) {
                      cookies.append(", ");
                    }
                    cookies.append(cookie.toString());
                }
            }

            Map<String, List<String>> cookieMap = new HashMap<String, List<String>>(requestHeaders);

            if (cookies.length() > 0) {
                List<String> list = Collections.singletonList(cookies.toString());
                cookieMap.put("Cookie", list);
            }
            
            return Collections.unmodifiableMap(cookieMap);
        }

        public void put(URI uri, Map<String, List<String>> responseHeaders) throws IOException {
            List<String> setCookieList = responseHeaders.get("Set-Cookie");
            if (setCookieList != null) {
                for (String item : setCookieList) {
                    Cookie cookie = new Cookie(uri, item);
                    for (Cookie existingCookie : cookieJar) {
                        if ((cookie.getURI().equals(existingCookie.getURI())) && (cookie.getName().equals(existingCookie.getName()))) {
                            cookieJar.remove(existingCookie);
                            break;
                        }
                    }
                    cookieJar.add(cookie);
                }
            }
        }

    }

}

class Cookie {

    private String name;
    private String value;
    private URI uri;
    private String domain;
    private Date expires;
    private String path;

    private static DateFormat expiresFormat1 = new SimpleDateFormat("E, dd MMM yyyy k:m:s 'GMT'", Locale.US);
    private static DateFormat expiresFormat2 = new SimpleDateFormat("E, dd-MMM-yyyy k:m:s 'GMT'", Locale.US);

    public Cookie(URI uri, String header) {
        String attributes[] = header.split(";");
        String nameValue = attributes[0].trim();
        this.uri = uri;
        this.name = nameValue.substring(0, nameValue.indexOf('='));
        this.value = nameValue.substring(nameValue.indexOf('=') + 1);
        this.path = "/";
        this.domain = uri.getHost();

        for (int i = 1; i < attributes.length; i++) {
            nameValue = attributes[i].trim();
            int equals = nameValue.indexOf('=');
            if (equals == -1) { continue; }

            String n = nameValue.substring(0, equals);
            String v = nameValue.substring(equals + 1);

            if (n.equalsIgnoreCase("domain")) {
                String uriDomain = uri.getHost();
                if (uriDomain.equals(v)) {
                    this.domain = v;
                } else {
                    if (!v.startsWith(".")) {
                        v = "." + v;
                    }
                    uriDomain = uriDomain.substring(uriDomain.indexOf('.'));
                    if (!uriDomain.equals(v)) {
                        throw new IllegalArgumentException("Trying to set foreign cookie");
                    }
                    this.domain = v;
                }
            } else if (n.equalsIgnoreCase("path")) {
                this.path = v;
            } else if (n.equalsIgnoreCase("expires")) {
                try {
                    this.expires = expiresFormat1.parse(v);
                } catch (ParseException e) {
                    try {
                        this.expires = expiresFormat2.parse(v);
                    } catch (ParseException e2) {
                        throw new IllegalArgumentException("Bad date format in header: " + v);
                    }
                }
            }
        }
    }

    public boolean hasExpired() {
        if (expires == null) {
            return false;
        }
        Date now = new Date();
        return now.after(expires);
    }

    public String getName() {
        return name;
    }

    public URI getURI() {
        return uri;
    }

    public String getDomain() {
        return domain;
    }

    public boolean matches(URI uri) {
        if (hasExpired()) {
            return false;
        }
        String p = uri.getPath();
        if (p == null) {
            p = "/";
        }
        return p.startsWith(this.path);
    }

    public String toString() {
        StringBuilder result = new StringBuilder(name);
        result.append("=");
        result.append(value);
        return result.toString();
    }

}
