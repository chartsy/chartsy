package org.chartsy.main.fonts;

import java.awt.Font;
import java.io.InputStream;

/**
 *
 * @author viorel.gheba
 */
public class FontUtils {

    private FontUtils() {};

    public static Font NadiaSerif() {
        String name = "NadiaSerif.ttf";
        Font font = null;
        try {
            InputStream is = FontUtils.class.getResourceAsStream("/org/chartsy/main/fonts/" + name);
            font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(Font.PLAIN, 12);
            is.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println(name + " not loaded.  Using serif font.");
            font = new Font("serif", Font.PLAIN, 12);
        }
        return font;
    }

}
