package org.chartsy.main.resources;

import java.awt.Image;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 *
 * @author viorel.gheba
 */
public final class ResourcesUtils {

    private static String path = "/org/chartsy/main/resources/";
    private static String image = ".png";
    private static String image_16 = "16.png";
    private static String image_24 = "24.png";

    private ResourcesUtils() {}

    public static Image getImage16(final String name) throws IOException {
        return ImageIO.read(ResourcesUtils.class.getResource(path+name+image_16));
    }

    public static Image getImage24(final String name) throws IOException {
        return ImageIO.read(ResourcesUtils.class.getResource(path+name+image_24));
    }

    public static ImageIcon getIcon(final String name) {
        return new ImageIcon(ResourcesUtils.class.getResource(path+name+image));
    }

    public static ImageIcon getIcon16(final String name) {
        return new ImageIcon(ResourcesUtils.class.getResource(path+name+image_16));
    }

    public static ImageIcon getIcon24(final String name) {
        return new ImageIcon(ResourcesUtils.class.getResource(path+name+image_24));
    }

    public static ImageIcon getLogo() {
        return new ImageIcon(ResourcesUtils.class.getResource(path+"logo"+image));
    }

}
