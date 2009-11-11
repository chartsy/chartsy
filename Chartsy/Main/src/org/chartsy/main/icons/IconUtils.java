package org.chartsy.main.icons;

import java.awt.Image;
import java.net.URL;
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

    private IconUtils() {
        // does nothing
    }

    public static Image getMainIcon() {
        return mainIcon;
    }

    public static ImageIcon getIcon(final String name) {
        return getIcon(name, "", PNG);
    }

    public static ImageIcon getIcon(final String name, final String ext) {
        return getIcon(name, "", ext);
    }

    public static ImageIcon getIcon(final String name, final String tooltip, final String ext) {
        final String src = name + ext;
        URL location = IconUtils.class.getResource(src);
        if (location != null) {
            return new ImageIcon(location, tooltip);
        } else {
            return null;
        }
    }

    public static ImageIcon getChartIcon(final String name) {
        return getChartIcon(name, PNG);
    }

    public static ImageIcon getChartIcon(final String name, final String ext) {
        final String src = "charts_" + name + ext;
        URL location = IconUtils.class.getResource(src);
        if (location != null) {
            return new ImageIcon(location);
        } else {
            return null;
        }
    }

}
