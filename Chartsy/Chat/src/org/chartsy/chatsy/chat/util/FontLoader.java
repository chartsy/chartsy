package org.chartsy.chatsy.chat.util;

import java.awt.Font;
import java.io.InputStream;

/**
 *
 * @author Viorel
 */
public final class FontLoader
{

	private static Font droidSans;
	private static Font droidSansBold;

	static
	{
		try
		{
			InputStream inputStream;
			inputStream = FontLoader.class.getResourceAsStream("/org/chartsy/chatsy/resources/DroidSans.ttf");
			droidSans = Font.createFont(Font.TRUETYPE_FONT, inputStream);
			inputStream = FontLoader.class.getResourceAsStream("/org/chartsy/chatsy/resources/DroidSansBold.ttf");
			droidSansBold = Font.createFont(Font.TRUETYPE_FONT, inputStream);
		}
		catch (Exception ex)
		{
		}
	}

	public static Font getDroidFont(int style, float size)
	{
		if (droidSans != null)
			if (style == Font.PLAIN || style == Font.ITALIC)
				return droidSans.deriveFont(style, size);
			else
				return droidSansBold.deriveFont(size);
		else
			return new Font("Dialog", style, (int)size);
	}

}
