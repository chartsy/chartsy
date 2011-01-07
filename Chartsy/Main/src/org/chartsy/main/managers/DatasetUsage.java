package org.chartsy.main.managers;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.event.EventListenerList;
import org.chartsy.main.data.DataItem;
import org.chartsy.main.data.DataProvider;
import org.chartsy.main.data.Dataset;
import org.chartsy.main.data.Stock;
import org.chartsy.main.events.DataProviderEvent;
import org.chartsy.main.events.DataProviderListener;
import org.chartsy.main.intervals.Interval;

/**
 *
 * @author Viorel
 */
public class DatasetUsage
{

	private static DatasetUsage instance;
	private final static ScheduledExecutorService service = Executors.newScheduledThreadPool(1);

	private HashMap<String, Dataset> datasets;
	private HashMap<String, AtomicInteger> datasetsUsage;
	private HashMap<String, DatasetUpdaterExecutor> datasetsUpdaters;
	private EventListenerList eventListenerList;

	public static DatasetUsage getInstance()
	{
		if (instance == null)
			instance = new DatasetUsage();
		return instance;
	}

	private DatasetUsage()
	{
		datasets = new HashMap<String, Dataset>();
		datasetsUsage = new HashMap<String, AtomicInteger>();
		datasetsUpdaters = new HashMap<String, DatasetUpdaterExecutor>();
		eventListenerList = new EventListenerList();
	}

	public void addDataProviderListener(DataProviderListener listener)
	{
		eventListenerList.add(DataProviderListener.class, listener);
	}

	public void removeDataProviderListener(DataProviderListener listener)
	{
		eventListenerList.remove(DataProviderListener.class, listener);
	}

	private void fireDataProviderEvent(DataProviderEvent event)
	{
		DataProviderListener[] listeners = eventListenerList.getListeners(DataProviderListener.class);
		for (DataProviderListener listener : listeners)
			listener.triggerDataProviderListener(event);
	}

	public void addDatasetUpdater(String dataProvider, Stock stock, Interval interval)
	{
		DataProvider provider = DataProviderManager.getDefault().getDataProvider(dataProvider);
		String key = provider.getDatasetKey(stock, interval);
		if (!datasetsUpdaters.containsKey(key))
		{
			DatasetUpdaterExecutor updaterExecutor = new DatasetUpdaterExecutor(dataProvider, stock, interval);
			datasetsUpdaters.put(key, updaterExecutor);
		}
	}

	public void addDataset(String key, Dataset dataset)
	{
		datasets.put(key, dataset);
	}

	public void removeDataset(String key)
	{
		datasets.remove(key);
		datasetsUsage.remove(key);
		datasetsUpdaters.get(key).stop();
		datasetsUpdaters.remove(key);
		System.gc();
	}

	public boolean isDatasetInMemory(String key)
	{
		boolean exists = datasets.containsKey(key);
		return exists;
	}

	public Dataset getDatasetFromMemory(String key)
	{
		Dataset dataset = datasets.get(key);
		return dataset;
	}

	public void fetchDataset(String key)
	{
		if (!datasetsUsage.containsKey(key))
		{
			AtomicInteger integer = new AtomicInteger(1);
			datasetsUsage.put(key, integer);
		} else
		{
			AtomicInteger integer = datasetsUsage.get(key);
			integer.incrementAndGet();
			datasetsUsage.put(key, integer);
		}
	}

	public void chartClosed(String key)
	{
		AtomicInteger integer = datasetsUsage.get(key);
		if (integer != null)
		{
			int usage = integer.decrementAndGet();
			if (usage == 0)
				removeDataset(key);
			else
				datasetsUsage.put(key, integer);
		}
	}

	public class DatasetUpdaterExecutor
	{

		private String dataProviderName;
		private Stock stock;
		private Interval interval;
		private ScheduledFuture future;

		public DatasetUpdaterExecutor(String dataProvider, Stock stock, Interval interval)
		{
			this.dataProviderName = dataProvider;
			this.stock = stock;
			this.interval = interval;
			initialize();
		}

		private void initialize()
		{
			final DataProvider dataProvider
				= DataProviderManager.getDefault().getDataProvider(dataProviderName);
			int refreshInterval = dataProvider.getRefreshInterval();

			final Runnable updater = new Runnable()
			{
				@Override
				public void run()
				{
					int itemsAdded = 0;
					boolean fireUpdate = false;
					String key = dataProvider.getDatasetKey(stock, interval);
					if (!interval.isIntraDay())
					{
						DataItem newItem = dataProvider.getLastDataItem(stock, interval);
						if (newItem != null)
						{
							DataItem oldItem = getDatasetFromMemory(key).getLastDataItem();

							long oldTime = oldItem.getTime();
							long newTime = newItem.getTime();

							if ( oldTime != newTime )
							{
								getDatasetFromMemory(key).addDataItem(newItem);
								itemsAdded = 1;
								fireUpdate = true;
							} else
							{
								boolean updateClose = oldItem.updateClose(newItem);
								if ( updateClose )
								{
									int index = getDatasetFromMemory(key).getLastIndex();
									DatasetUsage.getInstance().getDatasetFromMemory(key).setDataItem(index, newItem);
									fireUpdate = true;
								}
							}
						}
					} else
					{
						int count = getDatasetFromMemory(key).getItemsCount();
						List<DataItem> dataItems = dataProvider.getLastDataItems(stock, interval);
						if (dataItems.size() > 0)
						{
							fireUpdate = dataProvider.updateIntraDay(key, dataItems);
							itemsAdded = getDatasetFromMemory(key).getItemsCount() - count;
						}
						dataItems = null;
					}

					if ( fireUpdate )
					{
						DataProviderEvent event = new DataProviderEvent(key, itemsAdded);
						fireDataProviderEvent(event);
					}
				}
			};
			future = service.scheduleAtFixedRate(updater, refreshInterval, refreshInterval, TimeUnit.SECONDS);
		}

		public void stop()
		{
			future.cancel(true);
		}

		public ScheduledFuture getScheduledFuture()
		{
			return future;
		}

	}

}
