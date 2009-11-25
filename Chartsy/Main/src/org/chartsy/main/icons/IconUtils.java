package org.chartsy.main.icons;

import java.awt.Image;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import org.openide.windows.WindowManager;

/**
 *
 * @author viorel.gheba
 */
public final class IconUtils {

    public static final String PNG = ".png";
    public static final String JPG = ".jpg";
    public static final String JPEG = ".jpeg";
    public static final String GIF = ".gif";
    public static final String BMP = ".bmp";
    public static final Image mainIcon = WindowManager.getDefault().getMainWindow().getIconImage();
    private static IconUtils instance;

    public static IconUtils getDefault() {
        if (instance == null) instance = new IconUtils();
        return instance;
    }

    private IconUtils() {
        // does nothing
    }

    public Image getMainIcon() { return mainIcon; }

    public Image getImage16(final String name) throws IOException { return ImageIO.read(getClass().getResource("/org/chartsy/main/icons/" + name + "-16x16.png")); }
    public ImageIcon getIcon16(final String name) { return new javax.swing.ImageIcon(getClass().getResource("/org/chartsy/main/icons/" + name + "-16x16.png")); }
    public ImageIcon getIcon24(final String name) { return new javax.swing.ImageIcon(getClass().getResource("/org/chartsy/main/icons/" + name + "-24x24.png")); }

}
