package org.chartsy.main.data;

import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;
import javax.swing.event.EventListenerList;
import org.chartsy.main.events.DataProviderEvent;
import org.chartsy.main.events.DataProviderListener;
import org.chartsy.main.exceptions.DataNotFoundException;
import org.chartsy.main.exceptions.InvalidDatasetException;
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
import org.chartsy.main.managers.DataProviderManager;
import org.chartsy.main.utils.SerialVersion;
import org.openide.util.NbBundle;

/**
 *
 * @author viorel.gheba
 */
public abstract class DataProvider implements Serializable
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

	transient protected LinkedHashMap<String, LinkedHashMap<String, Timer>> timerMap;
	transient protected int currentTask = 0;

    public static final Interval ONE_MINUTE = new OneMinuteInterval();
    public static final Interval FIVE_MINUTE = new FiveMinuteInterval();
    public static final Interval FIFTEEN_MINUTE = new FifteenMinuteInterval();
    public static final Interval THIRTY_MINUTE = new ThirtyMinuteInterval();
    public static final Interval SIXTY_MINUTE = new SixtyMinuteInterval();
    
    public static final Interval DAILY = new DailyInterval();
    public static final Interval WEEKLY = new WeeklyInterval();
    public static final Interval MONTHLY = new MonthlyInterval();

    public static final Interval[] INTRA_DAY_INTERVALS =
	{ONE_MINUTE, FIVE_MINUTE, FIFTEEN_MINUTE, THIRTY_MINUTE, SIXTY_MINUTE};

    public static final Interval[] INTERVALS =
	{DAILY, WEEKLY, MONTHLY};

    protected String name;
    protected Exchange[] exchanges;
    protected boolean supportsIntraDay = false;

    protected String stocksPath;
    protected String datasetsPath;

	transient protected LinkedHashMap<String, LinkedList<Stock>> allStocks;
	transient protected LinkedHashMap<String, LinkedHashMap<String, Dataset>> allDatasets;
	
    protected boolean initializeFlag = true;

	public DataProvider(ResourceBundle bundle)
	{
		this(bundle, false);
	}

	public DataProvider(ResourceBundle bundle, boolean supportsIntraday)
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
				sufix = "";
			exchanges[i] = new Exchange(exchange, sufix);
		}
		this.supportsIntraDay = supportsIntraday;
		
	}

	private LinkedHashMap<String, LinkedList<Stock>> stocks()
	{
		if (allStocks == null)
			allStocks = new LinkedHashMap<String, LinkedList<Stock>>();
		return allStocks;
	}

	private LinkedHashMap<String, LinkedHashMap<String, Dataset>> datasets()
	{
		if (allDatasets == null)
			allDatasets = new LinkedHashMap<String, LinkedHashMap<String, Dataset>>();
		return allDatasets;
	}

    public void initialize()
    {
		LinkedList<Stock> stocks = new LinkedList<Stock>();
		stocks().put(name, stocks);

		LinkedHashMap<String, Dataset> datasets = new LinkedHashMap<String, Dataset>();
		datasets().put(name, datasets);

		LinkedHashMap<String, Timer> timers = new LinkedHashMap<String, Timer>();
		timers().put(name, timers);
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

    public boolean supportsIntraday() 
    { 
		return supportsIntraDay;
	}

	public void setStockCompanyName(Stock stock)
		throws InvalidStockException, StockNotFoundException,
		RegistrationException, IOException
	{}

	public Dataset getDatas(Stock stock, Interval interval)
		throws InvalidDatasetException, DataNotFoundException,
		RegistrationException, IOException
	{ return null; }
	
    public abstract Stock getStock(String symbol, String exchange);

    public abstract Dataset getData(Stock stock, Interval interval);
    
    public abstract Dataset getLastDataItem(Stock stock, Interval interval, Dataset dataset);

    public abstract StockSet getAutocomplete(String text);

    public String getKey(String symbol, Interval interval)
    {
        return NbBundle.getMessage(DataProvider.class, "Dataset_KEY", symbol, interval.getTimeParam());
    }
	
    public String getKey(Stock stock, Interval interval)
    {
        return NbBundle.getMessage(DataProvider.class, "Dataset_KEY", stock.getKey(), interval.getTimeParam());
    }

    public void addDataset(String key, Dataset value) 
    {
		if (!datasetExists(key))
		{
			datasets().get(name).put(key, value);
			addNewTimer(key);
		}
    }

	protected void setDataset(String key, Dataset value)
	{
		datasets().get(name).put(key, value);
	}

    public void removeDataset(String key) 
    { 
		datasets().get(name).remove(key);
    }

    public void removeAllDatasets() 
    {
		datasets().get(name).clear();
    }

    public Dataset getDataset(Stock stock, Interval interval) 
    {
		return datasets().get(name).get(getKey(stock, interval));
    }

    public Dataset getDataset(String key) 
    {
		return datasets().get(name).get(key);
    }

    public boolean datasetExists(String key) 
    {
		return (datasets().get(name).get(key) != null);
    }
    
    public boolean datasetExists(Stock stock) 
    {
		return (datasets().get(name).get(getKey(stock, DAILY)) != null);
    }
    
    public boolean datasetExists(Stock stock, Interval interval) 
    {
		return (datasets().get(name).get(getKey(stock, interval)) != null);
    }

	public LinkedHashMap<String, Dataset> getDatasetsMap()
	{
		return datasets().get(name);
	}

	public Interval getIntervalFromKey(String key)
	{
		for (Interval i : INTERVALS)
		{
			if (key.endsWith(i.getTimeParam()))
				return i;
		}
		return null;
	}

    public void addStock(Stock stock)
    {
		if (!hasStock(stock))
			stocks().get(name).add(stock);
    }

    public boolean hasStock(Stock stock)
    {
		return stocks().get(name).contains(stock);
    }

	public Stock getStockFromKey(String key)
	{
		LinkedList<Stock> list = stocks().get(name);
		for (Stock s : list)
		{
			for (Interval i : INTERVALS)
			{
				if (key.equals(getKey(s, i)))
					return s;
			}
		}
		return null;
	}

    public Stock getStock(Stock stock)
    {
		int index = stocks().get(name).indexOf(stock);
		if (index != -1)
			return stocks().get(name).get(index);
		return null;
    }

	public List<Stock> getStocks()
	{
		return stocks().get(name);
	}

	public static Interval getInterval(int intervalHash)
	{
		for (Interval interval : INTERVALS)
			if (interval.hashCode() == intervalHash)
				return interval;
		for (Interval interval : INTRA_DAY_INTERVALS)
			if (interval.hashCode() == intervalHash)
				return interval;
		return null;
	}

	public Stock getStockFromHash(int stockHash)
	{
		for (Stock stock : stocks().get(name))
			if (stock.hashCode() == stockHash)
				return stock;
		return null;
	}

	transient private EventListenerList datasetListener;

	private EventListenerList eventListenerList()
	{
		if (datasetListener == null)
			datasetListener = new EventListenerList();
		return datasetListener;
	}

	public void addDatasetListener(DataProviderListener listener)
	{
		eventListenerList().add(DataProviderListener.class, listener);
	}

	public void removeDatasetListener(DataProviderListener listener)
	{
		eventListenerList().remove(DataProviderListener.class, listener);
	}

	private void fireDatasetEvent(DataProviderEvent evt)
	{
		DataProviderListener[] listeners = eventListenerList().getListeners(DataProviderListener.class);
		for (DataProviderListener listener : listeners)
			listener.triggerDataProviderListener(evt);
	}

	public LinkedHashMap<String, LinkedHashMap<String, Timer>> timers()
	{
		if (timerMap == null)
			timerMap = new LinkedHashMap<String, LinkedHashMap<String, Timer>>();
		return timerMap;
	}

	protected void addNewTimer(String key)
	{
		Timer timer = new Timer();
		timer.schedule(new PeriodTimer(key), 5000, 5000);
		if (!timers().get(name).containsKey(key))
			timers().get(name).put(key, timer);
	}

	public class PeriodTimer extends TimerTask
	{

		private String key;

		public PeriodTimer(String key)
		{
			this.key = key;
		}

		public @Override void run()
		{
			Stock stock = getStockFromKey(key);
			Interval interval = getIntervalFromKey(key);

			if (stock == null)
				return;

			if (interval == null)
				return;

			Dataset oldDataset = getDataset(key);
			if (oldDataset == null)
				return;

			Dataset newDataset = null;
			
			if (!oldDataset.isEmpty())
				newDataset = DataProviderManager.getDefault().getDataProvider(name).getLastDataItem(stock, interval, oldDataset);
			else
				newDataset = DataProviderManager.getDefault().getDataProvider(name).getData(stock, interval);

			if (newDataset == null)
				return;
			
			setDataset(key, newDataset);

			fireDatasetEvent(new DataProviderEvent(stock));
		}

	}

}
