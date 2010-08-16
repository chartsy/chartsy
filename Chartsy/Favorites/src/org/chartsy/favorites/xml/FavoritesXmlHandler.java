package org.chartsy.favorites.xml;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.chartsy.main.favorites.nodes.FolderAPI;
import org.chartsy.main.favorites.nodes.RootAPI;
import org.chartsy.main.favorites.nodes.StockAPI;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Viorel
 */
public class FavoritesXmlHandler extends DefaultHandler
{

	private static final int FAVORITES		= "favorites".hashCode();

	private static final int FOLDER			= "folder".hashCode();
	private static final int NAME			= "name".hashCode();

	private static final int STOCK			= "stock".hashCode();
	private static final int COMPANY		= "companyName".hashCode();
	private static final int SYMBOL			= "symbol".hashCode();
	private static final int EXCHANGE		= "exchange".hashCode();
	private static final int DATAPROVIDER	= "dataProvider".hashCode();

	private RootAPI root;
	private Stack stack;
	private boolean isStackReadyForText;
	private boolean isInsideFolder;

	public FavoritesXmlHandler()
	{
		stack = new Stack();
		isStackReadyForText = false;
		isInsideFolder = false;
	}

	public RootAPI getRootAPI()
	{
		return root;
	}

	public @Override void startElement
		(String uri, String localName, String qName, Attributes attribs)
	{
		isStackReadyForText = false;
		int identifier = localName.hashCode();

		if (identifier == FAVORITES)
		{
			stack.push(new RootAPI());
			isInsideFolder = false;
		}
		else if(identifier == FOLDER)
		{
			stack.push(new FolderAPI());
			isInsideFolder = true;
		}
		else if(identifier == STOCK)
			stack.push(new StockAPI());
		else if (identifier == NAME
			|| identifier == COMPANY
			|| identifier == SYMBOL
			|| identifier == EXCHANGE
			|| identifier == DATAPROVIDER)
		{
			stack.push(new StringBuilder());
			isStackReadyForText = true;
		}
		else
		{
			// do nothing
		}
	}

	public @Override void endElement
		(String uri, String localName, String qName)
	{
		isStackReadyForText = false;

		Object tmp = stack.pop();
		int identifier = localName.hashCode();

		if (identifier == FAVORITES)
			root = (RootAPI) tmp;
		else if(identifier == FOLDER)
		{
			((RootAPI) stack.peek()).addFolder((FolderAPI) tmp);
			isInsideFolder = false;
		}
		else if(identifier == STOCK)
		{
			if (isInsideFolder)
				((FolderAPI)stack.peek()).addStock((StockAPI) tmp);
			else
				((RootAPI)stack.peek()).addStock((StockAPI) tmp);
		}
		else if (identifier == NAME)
			((FolderAPI)stack.peek()).setDisplayName(decode(tmp.toString()));
		else if (identifier == COMPANY)
			((StockAPI)stack.peek()).setCompanyName(decode(tmp.toString()));
		else if (identifier == SYMBOL)
			((StockAPI)stack.peek()).setSymbol(decode(tmp.toString()));
		else if (identifier == EXCHANGE)
			((StockAPI)stack.peek()).setExchange(decode(tmp.toString()));
		else if (identifier == DATAPROVIDER)
			((StockAPI)stack.peek()).setDataProviderName(decode(tmp.toString()));
		else
			stack.push(tmp);
	}

	public @Override void characters
		(char[] data, int start, int length)
	{
		if (isStackReadyForText == true)
		{
			((StringBuilder)stack.peek()).append(data, start, length);
		}
	}

	private String decode(String text)
	{
		try
		{
			return URLDecoder.decode(text, "UTF-8");
		}
		catch (UnsupportedEncodingException ex)
		{
			Logger.getLogger(FavoritesXmlHandler.class.getName()).log(Level.SEVERE, "", ex);
		}
		return text;
	}

}
