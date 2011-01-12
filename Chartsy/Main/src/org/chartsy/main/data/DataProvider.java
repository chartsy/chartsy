package org.chartsy.main.data;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import org.chartsy.main.exceptions.InvalidStockException;
import org.chartsy.main.exceptions.RegistrationException;
import org.chartsy.main.exceptions.StockNotFoundException;
import org.chartsy.main.intervals.DailyInterval;
import org.chartsy.main.intervals.FifteenMinuteInterval;
import org.chartsy.main.intervals.FiveMinuteInterval;
import org.chartsy.main.intervals.Interval;
import org.chartsy.main.intervals.MonthlyInterval;
import org.chartsy.main.intervals.OneMinuteInterval;
import org.chartsy.main.intervals.SixtyMinuteInterval;
import org.chartsy.main.intervals.ThirtyMinuteInterval;
import org.chartsy.main.intervals.WeeklyInterval;
import org.chartsy.main.managers.CacheManager;
import org.chartsy.main.managers.DatasetUsage;
import org.chartsy.main.utils.SerialVersion;
import org.openide.util.NbBundle;

/**
 *
 * @author viorel.gheba
 */
public abstract class DataProvider implements Serializable
{

	private static final long serialVersionUID = SerialVersion.APPVERSION;

    public DataProvider(ResourceBundle bundle)
    {
        this(bundle, false, false);
    }

    public DataProvider(ResourceBundle bundle, boolean supportsIntraDay)
    {
        this(bundle, supportsIntraDay, false);
    }

	public DataProvider(
		ResourceBundle bundle,
		boolean supportsIntraDay,
		boolean supportsCustomIntervals)
	{
		this.name = bundle.getString("DataProvider_NAME");

        String[] exgs = bundle.getString("DataProvider_EXG").split(":");
        exchanges = new Exchange[exgs.length];
        for (int i = 0; i < exgs.length; i++)
        {
            String exchange = exgs[i];
            String resName = exgs[i].replace(" ", "") + "_PRE";
            String sufix = bundle.getString(resName);
            if (sufix.equals("null"))
            {
                sufix = "";
            }
            exchanges[i] = new Exchange(exchange, sufix);
        }
        this.supportsIntraDay = supportsIntraDay;
		this.supportsCustomInterval = supportsCustomIntervals;
	}

    public void initialize()
    {
    }

    public String getName()
    {
        return this.name;
    }

    public Exchange[] getExchanges()
    {
        return this.exchanges;
    }

    public Interval[] getIntraDayIntervals()
    {
        return INTRA_DAY_INTERVALS;
    }

    public Interval[] getIntervals()
    {
        return INTERVALS;
    }

	public Interval[] getSupportedIntervals()
	{
		return new Interval[] {};
	}

	public Interval getIntervalFromKey(String key)
    {
        for (Interval i : INTERVALS)
        {
            if (key.endsWith(i.getTimeParam()))
            {
                return i;
            }
        }
        for (Interval i : INTRA_DAY_INTERVALS)
        {
            if (key.endsWith(i.getTimeParam()))
            {
                return i;
            }
        }
        return null;
    }

	public static Interval getInterval(int intervalHash)
    {
        for (Interval interval : INTERVALS)
        {
            if (interval.hashCode() == intervalHash)
            {
                return interval;
            }
        }
        for (Interval interval : INTRA_DAY_INTERVALS)
        {
            if (interval.hashCode() == intervalHash)
            {
                return interval;
            }
        }
        return null;
    }

    public boolean supportsIntraday()
    {
        return supportsIntraDay;
    }

	public boolean supportsCustomInterval()
	{
		return supportsCustomInterval;
	}

	public boolean supportsAnyInterval()
	{
		return false;
	}

	public void fetchStock(String symbol)
		throws InvalidStockException, StockNotFoundException, RegistrationException, IOException
	{
		String symb = "";
		String exchange = "";
		String company = "";

		symbol.trim();
		String delimiter = ".";

		if (symbol.contains(delimiter))
		{
			int index = symbol.indexOf(delimiter);
			symb = symbol.substring(0, index);
			exchange = symbol.substring(index - 1, symbol.length() - 1);
		} else
		{
			symb = symbol;
		}

		company = fetchCompanyName(symbol);

		Stock stock = new Stock(symb, exchange);
		stock.setCompanyName(company);
		cacheStock(stock);
	}

	protected abstract String fetchCompanyName(String symbol)
            throws InvalidStockException, StockNotFoundException, RegistrationException, IOException;

	protected void cacheStock(Stock stock)
		throws IOException
	{
		String fileName = getStockKey(stock);
		CacheManager.getInstance().cacheStock(stock, fileName);
	}

    public boolean stockExists(String symbol)
    {
		String fileName = getStockKey(symbol);
		return CacheManager.getInstance().stockCacheExists(fileName);
    }

    public Stock fetchStockFromCache(String symbol)
    {
		Stock stock = null;
		String fileName = getStockKey(symbol);
		try
		{
			stock = CacheManager.getInstance().fetchStockFromCache(fileName);
		} catch (Exception ex)
		{
			System.err.println(ex.getMessage());
			stock = null;
		}

		return stock;
    }

	public void fetchDatasetForFavorites(Stock stock)
		throws IOException, ParseException
	{
		Dataset dataset = fetchDataForFavorites(stock);
		if (dataset != null)
		{
			String key = getDatasetKey(stock, DAILY);
			DatasetUsage.getInstance().addDataset(key, dataset);
		} else
			throw new IOException();
	}

	protected abstract Dataset fetchDataForFavorites(Stock stock)
		throws IOException, ParseException;

    public void fetchDataset(Stock stock, Interval interval)
		throws IOException, ParseException
	{
		Dataset dataset = fetchData(stock, interval);
		if (dataset != null)
		{
			String key = getDatasetKey(stock, interval);
			DatasetUsage.getInstance().addDataset(key, dataset);
			//cacheDataset(key, dataset);
		} else
			throw new IOException();
	}

	protected abstract Dataset fetchData(Stock stock, Interval interval)
		throws IOException, ParseException;

	public DataItem getLastDataItem(Stock stock, Interval interval)
	{
		try
		{
			return fetchLastDataItem(stock, interval);
		} catch (Exception ex)
		{
			return null;
		}
	}

    protected abstract DataItem fetchLastDataItem(Stock stock, Interval interval)
		throws IOException, ParseException;

	public List<DataItem> getLastDataItems(Stock stock, Interval interval)
	{
		return new ArrayList<DataItem>();
	}

	public boolean updateIntraDay(String key, List<DataItem> dataItems)
	{
		return false;
	}

	protected void cacheDataset(String key, Dataset dataset)
		throws IOException
	{
		CacheManager.getInstance().cacheDataset(dataset, key);
	}

	public boolean datasetExists(Stock stock, Interval interval)
	{
		String fileName = getDatasetKey(stock, interval);
		return CacheManager.getInstance().datasetCacheExists(fileName);
	}

	public void fetchDatasetFromCache(Stock stock, Interval interval)
		throws IOException
	{
		String fileName = getDatasetKey(stock, interval);
		CacheManager.getInstance().fetchDatasetFromCache(fileName);
	}

    public abstract StockSet fetchAutocomplete(String text)
		throws IOException;

    /*
     * Return the refresh interval in seconds
     */
    public abstract int getRefreshInterval();

    public String getStockKey(Stock stock)
    {
        return getStockKey(stock.getKey());
    }

	public String getStockKey(String symbol)
    {
        return NbBundle.getMessage(
			DataProvider.class,
			"Stock_KEY",
			getName(),
			symbol);
    }

    public String getDatasetKey(Stock stock, Interval interval)
    {
        return getDatasetKey(stock.getKey(), interval);
    }

	public String getDatasetKey(String symbol, Interval interval)
    {
        return NbBundle.getMessage(
			DataProvider.class,
			"Dataset_KEY",
			getName(),
			symbol,
			interval.getTimeParam());
    }

	public boolean needsRegistration()
	{
		return needsRegistration;
	}

	public boolean isRegistred()
	{
		return isRegistered;
	}

	public String getRegistrationMessage()
	{
		return "";
	}

	public String getRegistrationURL()
	{
		return "";
	}

    public static final Interval ONE_MINUTE = new OneMinuteInterval();
    public static final Interval FIVE_MINUTE = new FiveMinuteInterval();
    public static final Interval FIFTEEN_MINUTE = new FifteenMinuteInterval();
    public static final Interval THIRTY_MINUTE = new ThirtyMinuteInterval();
    public static final Interval SIXTY_MINUTE = new SixtyMinuteInterval();
    public static final Interval DAILY = new DailyInterval();
    public static final Interval WEEKLY = new WeeklyInterval();
    public static final Interval MONTHLY = new MonthlyInterval();
    public static final Interval[] INTRA_DAY_INTERVALS = { ONE_MINUTE, FIVE_MINUTE, FIFTEEN_MINUTE, THIRTY_MINUTE, SIXTY_MINUTE };
    public static final Interval[] INTERVALS = { DAILY, WEEKLY, MONTHLY };

    protected String name;
    protected Exchange[] exchanges;
    protected boolean supportsIntraDay = false;
	protected boolean supportsCustomInterval = false;
    protected String stocksPath;
    protected String datasetsPath;
    protected boolean initializeFlag = true;
	protected boolean needsRegistration = false;
	protected boolean isRegistered = false;
	
}
