package org.chartsy.stockscanpro.completion;

/**
 *
 * @author Viorel
 */
public class IndexedIndicator
{

	private String fixedName;
	private String[] params;
	private boolean hasTradeValue;
	private boolean hasExtraParam;
	private String indicator;

	public IndexedIndicator(String fixedName, String params)
	{
		this(fixedName, params, false, false);
	}

	public IndexedIndicator(String fixedName, String params,
		boolean hasTradeValue, boolean hasExtraParam)
	{
		this.fixedName = fixedName;
		this.params = params.split(",");
		this.hasTradeValue = hasTradeValue;
		this.hasExtraParam = hasExtraParam;
	}
	
	public String getFixedName()
	{
		return fixedName;
	}
	
	public String[] getParams()
	{
		return params;
	}

	public boolean hasTradeValue()
	{
		return this.hasTradeValue;
	}

	public boolean hasExtraParam()
	{
		return hasExtraParam;
	}

	public String toString()
	{
		if (indicator == null)
		{
			StringBuilder sb = new StringBuilder();
			sb.append(fixedName);

			sb.append("(");
			if (params != null && params.length > 0)
			{
				for (int i = 0, n = params.length; i < n; i++)
				{
					if (i > 0)
						sb.append(", ");

					sb.append(params[i]);
				}
			}
			sb.append(")");

			if (hasExtraParam)
				sb.append("[-1]");

			indicator = sb.toString();
		}
		return indicator;
	}

}
