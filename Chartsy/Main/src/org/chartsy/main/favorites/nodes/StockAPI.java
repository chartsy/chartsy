package org.chartsy.main.favorites.nodes;

import java.text.DecimalFormat;
import org.chartsy.main.data.DataProvider;
import org.chartsy.main.data.Dataset;
import org.chartsy.main.data.Stock;
import org.chartsy.main.events.DataProviderAdapter;
import org.chartsy.main.events.DataProviderEvent;
import org.chartsy.main.managers.DataProviderManager;
import org.chartsy.main.managers.DatasetUsage;

/**
 *
 * @author Viorel
 */
public class StockAPI extends Object
{

	private String dataProviderName;
	private String symbol;
	private String exchange;
	private String companyName;

	private double newValue = -1;
	private double oldValue = -1;

	public StockAPI()
	{
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
		final DataProvider dp = getDataProvider();
		if (dp != null)
		{
			final Stock stock = getStock();
			String key = dp.getDatasetKey(stock, dp.DAILY);
			if (!DatasetUsage.getInstance().isDatasetInMemory(key))
			{
				try
				{
					dp.fetchDatasetForFavorites(stock);
				} catch (Exception ex)
				{
					// do nothing
				}
			}
			
			DataProviderAdapter adapter = new DataProviderAdapter()
			{
				@Override
				public void triggerDataProviderListener(DataProviderEvent evt)
				{
					String key = dp.getDatasetKey(stock, dp.DAILY);
					if (key.equals((String) evt.getSource()))
					{
						Dataset dataset = DatasetUsage.getInstance().getDatasetFromMemory(key);
						if (dataset != null && !dataset.isEmpty())
						{
							oldValue = dataset.getCloseAt(dataset.getLastIndex() - 1);
							newValue = dataset.getLastClose();
						} else
						{
							oldValue = 0;
							newValue = 0;
						}
					}
				}
			};
			
			DatasetUsage.getInstance().addDataProviderListener(adapter);
			DatasetUsage.getInstance().addDatasetUpdater(dataProviderName, stock, dp.DAILY);
			Dataset dataset = DatasetUsage.getInstance().getDatasetFromMemory(key);
			if (dataset != null && !dataset.isEmpty())
			{
				DatasetUsage.getInstance().fetchDataset(key);
				oldValue = dataset.getCloseAt(dataset.getLastIndex() - 1);
				newValue = dataset.getLastClose();
			} else
			{
				oldValue = 0;
				newValue = 0;
			}
		}
	}

	public Object[][] getData()
	{
		double value = newValue;
		double dif = newValue - oldValue;
		double percent = oldValue == 0 ? 0 : dif / oldValue * 100;
		return new String[][]
		{
			{
				getStock().getKey(),
				String.valueOf(value),
				String.valueOf(dif),
				String.valueOf(percent)
			}
		};
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
