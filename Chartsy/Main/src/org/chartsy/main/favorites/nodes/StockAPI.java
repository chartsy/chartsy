package org.chartsy.main.favorites.nodes;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.chartsy.main.data.DataProvider;
import org.chartsy.main.data.Dataset;
import org.chartsy.main.data.Stock;
import org.chartsy.main.events.DataProviderEvent;
import org.chartsy.main.events.DataProviderListener;
import org.chartsy.main.managers.DataProviderManager;

/**
 *
 * @author Viorel
 */
public class StockAPI extends Object implements DataProviderListener
{

	private String dataProviderName;
	private String symbol;
	private String exchange;
	private String companyName;

	private double newValue = -1;
	private double oldValue = -1;

	private long newTime;
	DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

	public StockAPI()
	{
		Calendar cal = Calendar.getInstance();
		String date = formatter.format(cal.getTime());

		try { newTime = formatter.parse(date).getTime(); }
		catch (ParseException ex) {}
	}

	public String getDisplayName()
	{
		return getStock().getKey();
	}

	public void setDataProviderName(String dataProviderName)
	{
		this.dataProviderName = dataProviderName;
	}

	public String getDataProviderName()
	{
		return this.dataProviderName;
	}

	public void setSymbol(String symbol)
	{
		this.symbol = symbol;
	}

	public String getSymbol()
	{
		return this.symbol;
	}

	public void setExchange(String exchange)
	{
		this.exchange = exchange;
	}

	public String getExchange()
	{
		return this.exchange;
	}

	public void setCompanyName(String companyName)
	{
		this.companyName = companyName;
	}

	public String getCompanyName()
	{
		return this.companyName;
	}

	public Stock getStock()
	{
		if (symbol != null
			&& exchange != null
			&& companyName != null)
		{
			Stock stock = new Stock(symbol, exchange);
			stock.setCompanyName(companyName);
			return stock;
		}
		else
			return null;
	}

	public DataProvider getDataProvider()
	{
		return DataProviderManager.getDefault().getDataProvider(dataProviderName);
	}

	public void initializeDataProvider()
	{
		DataProvider dp = DataProviderManager.getDefault().getDataProvider(dataProviderName);
		if (dp != null)
		{
			dp.addStock(getStock());
			dp.addDataset(dp.getKey(getStock(), DataProvider.DAILY), new Dataset());
			dp.addDataset(dp.getKey(getStock(), DataProvider.WEEKLY), new Dataset());
			dp.addDataset(dp.getKey(getStock(), DataProvider.MONTHLY), new Dataset());
			dp.addDatasetListener((DataProviderListener) this);
		}
		else
		{
			System.out.println(dataProviderName + " is null.");
		}
	}

	public Object[][] getData()
	{
		return new String[][]
		{
			{getStock().getKey(),
			String.valueOf(newValue < 0 ? (oldValue < 0 ? 0 : oldValue) : newValue),
			String.valueOf(newValue < 0 ? 0 : (oldValue - newValue)),
			String.valueOf(newValue < 0 ? 0 : ((oldValue - newValue) / oldValue)*100)}
		};
	}

	public void triggerDataProviderListener(DataProviderEvent evt)
	{
		Stock updated = (Stock) evt.getSource();
		Stock stock = getStock();

		if (stock != null
			&& updated.equals(getStock())
			&& dataProviderName != null)
		{
			DataProvider provider = DataProviderManager.getDefault().getDataProvider(dataProviderName);
			Dataset dataset = provider.getDataset(provider.getKey(stock, DataProvider.DAILY));

			if (dataset != null && !dataset.isEmpty())
			{
				if (dataset.getLastTime() == newTime)
				{
					oldValue = dataset.getCloseAt(dataset.getLastIndex() - 1);
					newValue = dataset.getLastClose();
				}
				else
				{
					oldValue = dataset.getLastClose();
					newValue = -1;
				}
			}
		}
	}

	public @Override String toString()
	{
		String newline = System.getProperty("line.separator");

		StringBuilder builder = new StringBuilder();

		builder.append("--- ").append(getStock().getKey()).append(" ---").append(newline);
		builder.append("symbol: ").append(getSymbol()).append(newline);
		builder.append("exchange: ").append(getExchange()).append(newline);
		builder.append("companyName: ").append(getCompanyName()).append(newline);
		builder.append("dataProvider: ").append(getDataProviderName()).append(newline);

		return builder.toString();
	}

	public @Override boolean equals(Object obj)
	{
		if (obj == this)
			return true;

		if (!(obj instanceof StockAPI))
			return false;

		StockAPI that = (StockAPI) obj;

		if (that.hashCode() != hashCode())
			return false;

		return true;
	}

	public @Override int hashCode()
	{
		int hash = 5;
		hash = 89 * hash + (this.dataProviderName != null ? this.dataProviderName.hashCode() : 0);
		hash = 89 * hash + (this.symbol != null ? this.symbol.hashCode() : 0);
		hash = 89 * hash + (this.exchange != null ? this.exchange.hashCode() : 0);
		hash = 89 * hash + (this.companyName != null ? this.companyName.hashCode() : 0);
		return hash;
	}

}
