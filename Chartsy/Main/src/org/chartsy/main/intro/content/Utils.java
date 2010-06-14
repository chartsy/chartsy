package org.chartsy.main.intro.content;

import java.awt.Font;
import java.awt.Image;
import java.lang.reflect.Method;
import java.util.Arrays;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import org.openide.util.ImageUtilities;

/**
 *
 * @author viorel.gheba
 */
public class Utils {

    private static final String[] browsers = { "firefox", "opera", "konqueror", "epiphany", "seamonkey", "galeon", "kazehakase", "mozilla", "netscape" };

    private Utils() {}

    public static void openURL(String url) {
        String osName = System.getProperty("os.name");
        try {
            if (osName.startsWith("Max OS")) {
                Class fileMgr = Class.forName("com.apple.eio.FileManager");
                @SuppressWarnings({"unchecked"})
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

    public static int getDefaultFontSize() {
        Integer integer = (Integer)UIManager.get("customFontSize");
        if (integer != null)
        {
            return integer.intValue();
        }
        else {
            Font font = UIManager.getFont("TextField.font");
            return font == null ? 12 : font.getSize();
        }
    }

    public static Image getImage(String s) { return ImageUtilities.loadImage(s, true); }
    public static ImageIcon getImageIcon(String s) { return new ImageIcon(getImage(s)); }

}
