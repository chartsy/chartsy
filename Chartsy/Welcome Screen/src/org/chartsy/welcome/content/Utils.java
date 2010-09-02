package org.chartsy.welcome.content;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.util.Map;
import javax.swing.UIManager;

/**
 *
 * @author Viorel
 */
public class Utils {

	/*private static final String[] fonts = { "aller.ttf" };
	private static final Map<String, Font> cache = new ConcurrentHashMap<String, Font>(fonts.length);

	static
	{
		for (String font : fonts)
		{
			try {
				InputStream fin = Utils.class.getClassLoader()
					.getResourceAsStream("org/chartsy/welcome/resources/"+font);
				cache.put(font, Font.createFont(Font.PLAIN, fin));
			} catch (FontFormatException ex) {
				Exceptions.printStackTrace(ex);
			} catch (IOException ex) {
				Exceptions.printStackTrace(ex);
			}
		}
	}*/

	private Utils()
	{}

	public static Graphics2D prepareGraphics(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		Map rhints = (Map)(Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints"));
		if (rhints == null && Boolean.getBoolean("swing.aatext"))
		{
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		}
		else if (rhints != null)
		{
			g2.addRenderingHints(rhints);
		}
		return g2;
	}

	public static void showURL(String href)
	{

	}

	static int getDefaultFontSize()
	{
		Integer customFontSize = (Integer)UIManager.get("customFontSize");
		if (customFontSize != null)
		{
			return customFontSize.intValue();
		}
		else
		{
			Font systemDefaultFont = UIManager.getFont("TextField.font");
			return (systemDefaultFont != null)
                ? systemDefaultFont.getSize()
                : 12;
		}
	}

	/*public static Font getFont(String name)
	{
		return cache.get(name);
	}*/

}
