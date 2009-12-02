package org.chartsy.main.icons;

import java.awt.Image;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 *
 * @author viorel.gheba
 */
public final class IconUtils {
    
    private static IconUtils instance;

    public static IconUtils getDefault() {
        if (instance == null) instance = new IconUtils();
        return instance;
    }

    private IconUtils() {}

    public Image getImage16(final String name) throws IOException { return ImageIO.read(getClass().getResource("/org/chartsy/main/icons/" + name + "-16x16.png")); }
    public ImageIcon getIcon16(final String name) { return new ImageIcon(getClass().getResource("/org/chartsy/main/icons/" + name + "-16x16.png")); }
    public ImageIcon getIcon24(final String name) { return new ImageIcon(getClass().getResource("/org/chartsy/main/icons/" + name + "-24x24.png")); }
    public ImageIcon getLogo() { return new ImageIcon(getClass().getResource("/org/chartsy/main/icons/chartsy_component_logo.png")); }

}
