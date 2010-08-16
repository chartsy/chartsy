package org.chartsy.favorites.xml;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.chartsy.main.favorites.nodes.RootAPI;
import org.chartsy.main.utils.FileUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 *
 * @author Viorel
 */
public final class FavoritesXmlParser
{
	
	private FavoritesXmlParser()
	{}

	public static RootAPI getRoot()
	{
		RootAPI root = null;

		try
		{
			InputSource src
				= new InputSource(
				new FileInputStream(FileUtils.favoritesFile()));

			FavoritesXmlHandler handler = new FavoritesXmlHandler();

			XMLReader reader = XMLReaderFactory.createXMLReader("com.sun.org.apache.xerces.internal.parsers.SAXParser");
			if (reader != null)
			{
				reader.setContentHandler(handler);
				reader.parse(src);
				root = handler.getRootAPI();
			}
		}
		catch (IOException ex)
		{
			Logger.getLogger(FavoritesXmlParser.class.getName()).log(
				Level.SEVERE, "", ex);
		}
		catch (SAXException ex)
		{
			Logger.getLogger(FavoritesXmlParser.class.getName()).log(
				Level.SEVERE, "", ex);
		}

		return root;
	}

}
