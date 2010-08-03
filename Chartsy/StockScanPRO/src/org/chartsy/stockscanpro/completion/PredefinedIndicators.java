package org.chartsy.stockscanpro.completion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.ResourceBundle;

/**
 *
 * @author Viorel
 */
public final class PredefinedIndicators
{

	public static IndexedIndicator createIndicator(String fixedName, String params, boolean hasTradeValue, boolean hasExtraParam)
	{
		return new IndexedIndicator(fixedName, params, hasTradeValue, hasExtraParam);
	}

	public static Collection<IndexedIndicator> getIndicators()
	{
		Collection<IndexedIndicator> indicators = new ArrayList<IndexedIndicator>();
		ResourceBundle bundle = ResourceBundle.getBundle("org.chartsy.stockscanpro.completion.predefined");
		Enumeration<String> keys = bundle.getKeys();

		while (keys.hasMoreElements())
		{
			String key = keys.nextElement();
			String[] params = bundle.getString(key).split(":");

			indicators.add(createIndicator(key, params[0].equals("null") ? "" : params[0], Boolean.parseBoolean(params[1]), false));
			indicators.add(createIndicator(key, params[0].equals("null") ? "" : params[0], Boolean.parseBoolean(params[1]), true));
		}
		
		return indicators;
	}

}
