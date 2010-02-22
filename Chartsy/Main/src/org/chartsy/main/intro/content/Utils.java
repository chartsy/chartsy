package org.chartsy.main.intro.content;

import java.awt.Desktop;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.lang.reflect.Method;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import org.openide.util.ImageUtilities;
import sun.awt.image.URLImageSource;

/**
 *
 * @author viorel.gheba
 */
public class Utils {

    private static final String[] browsers = { "firefox", "opera", "konqueror", "epiphany", "seamonkey", "galeon", "kazehakase", "mozilla", "netscape" };
    private static final String adsCookie = "OAVARS[acaa7ff1]";

    private Utils() {}

    public static void openURL(String url) {
        String osName = System.getProperty("os.name");
        try {
            if (osName.startsWith("Max OS")) {
                Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
                Method openURL = fileMgr.getDeclaredMethod("openURL", new Class[] {String.class});
                openURL.invoke(null, new Object[] {url});
            } else if (osName.startsWith("Windows")) {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
            } else {
                boolean found = false;
                for (String browser : browsers)
                    if (!found) {
                        found = Runtime.getRuntime().exec( new String[] {"which", browser}).waitFor() == 0;
                        if (found) Runtime.getRuntime().exec(new String[] {browser, url});
                    }
                if (!found) throw new Exception(Arrays.toString(browsers));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error attempting to launch web browser\n" + e.toString());
        }
    }

    public static void openURI(URI uri) {
        if (!Desktop.isDesktopSupported()) {
            System.err.println( "Desktop is not supported (fatal)" );
        }
        Desktop desktop = Desktop.getDesktop();
        if (!desktop.isSupported(Desktop.Action.BROWSE)) {
            System.err.println( "Desktop doesn't support the browse action (fatal)" );
        }
        try {
            desktop.browse(uri);
        } catch (Exception e) {}
    }

    public static int getDefaultFontSize() {
        Integer integer = (Integer)UIManager.get("customFontSize");
        if (integer != null) { return integer.intValue(); }
        else {
            Font font = UIManager.getFont("TextField.font");
            return font == null ? 12 : font.getSize();
        }
    }

    public static Image getImage(String s) { return ImageUtilities.loadImage(s, true); }
    public static ImageIcon getImageIcon(String s) { return new ImageIcon(getImage(s)); }

    public static void setCookieValue(String urlString, String cookieValue) {
        try {
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            String cookie = adsCookie + "=" + cookieValue;
            connection.setRequestProperty("Cookie", cookie);
            connection.connect();
            URI uri = connection.getURL().toURI();
            openURI(uri);
        } catch (Exception e) {}
    }

    public static String getCookieValue(String urlString) {
        try {
            CookieManager cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            CookieHandler.setDefault(cookieManager);

            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            connection.setDoOutput(true);
            Object obj = connection.getContent();

            url = new URL(urlString);
            connection = url.openConnection();
            connection.setDoInput(true);
            obj = connection.getContent();

            CookieStore cookieStore = cookieManager.getCookieStore();
            List<HttpCookie> cookies = cookieStore.getCookies();
            for (HttpCookie cookie : cookies)
                if (cookie.getName().equals(adsCookie))
                    return cookie.getValue();
        } catch (Exception e) {}

        return null;
    }

    public static Image getImageFromURL(String urlString) {
        try {
            CookieManager cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            CookieHandler.setDefault(cookieManager);

            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            connection.setDoOutput(true);
            Object obj = connection.getContent();

            url = new URL(urlString);
            connection = url.openConnection();
            connection.setDoInput(true);
            obj = connection.getContent();

            CookieStore cookieStore = cookieManager.getCookieStore();
            List<HttpCookie> cookies = cookieStore.getCookies();
            for (HttpCookie cookie : cookies) System.out.println("CookieHandler retrieved cookie: " + cookie);

            if (obj instanceof URLImageSource) {
                URLImageSource imageSource = (URLImageSource) obj;
                Image image = Toolkit.getDefaultToolkit().createImage(imageSource);
                return image;
            }
            return null;
        } catch (Exception e) {}

        return null;
    }

}
