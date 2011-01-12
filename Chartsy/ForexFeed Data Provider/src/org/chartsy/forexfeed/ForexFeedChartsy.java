package org.chartsy.forexfeed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.prefs.Preferences;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.chartsy.main.data.DataItem;
import org.chartsy.main.data.DataProvider;
import org.chartsy.main.data.Dataset;
import org.chartsy.main.data.Stock;
import org.chartsy.main.data.StockNode;
import org.chartsy.main.data.StockSet;
import org.chartsy.main.exceptions.InvalidStockException;
import org.chartsy.main.exceptions.RegistrationException;
import org.chartsy.main.exceptions.StockNotFoundException;
import org.chartsy.main.intervals.CustomInterval;
import org.chartsy.main.intervals.Interval;
import org.chartsy.main.managers.DatasetUsage;
import org.chartsy.main.managers.ProxyManager;
import org.chartsy.main.utils.NotifyUtil;
import org.chartsy.main.utils.SerialVersion;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;

public class ForexFeedChartsy extends DataProvider
{

    public ForexFeedChartsy()
    {
        super(NbBundle.getBundle(ForexFeedChartsy.class), true, true);
        Preferences pref = NbPreferences.forModule(ForexFeedPanel.class);
        this.ACCESS_KEY = pref.get("access_key", "");
		this.needsRegistration = true;
		this.isRegistered = !ACCESS_KEY.isEmpty();
		this.refreshInterval = pref.getInt("refresh_interval", 5);
    }

	@Override
	public boolean supportsAnyInterval()
	{
		return false;
	}

	@Override
	public Interval[] getSupportedIntervals()
	{
		return supportedIntervals;
	}


    @Override
    public int getRefreshInterval()
    {
        return refreshInterval;
    }

    @Override
    public StockSet fetchAutocomplete(String text)
		throws IOException
    {
        String url = "http://fxf.forexfeed.net/symbols/CHARTSY-" + ACCESS_KEY;
        StockSet result = new StockSet();

        final HttpClient client = ProxyManager.getDefault().getHttpClient();
        final GetMethod method = new GetMethod(url);
		client.executeMethod(method);

		InputStream inputStream = method.getResponseBodyAsStream();
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

		String inputLine;
		int i = 0;
		while ((inputLine = bufferedReader.readLine()) != null)
		{
			if (i > 0)
			{
				StringTokenizer st = new StringTokenizer(inputLine, ",");
				String key = st.nextToken();
				String company = st.nextToken();
				String exchange = "FOREX";
				if (key.toLowerCase().contains(text.toLowerCase()))
				{
					result.add(new StockNode(key, company, exchange));
				}
			}
			i++;
		}

		bufferedReader.close();
		method.releaseConnection();

        return result;
    }

    @Override
    protected DataItem fetchLastDataItem(Stock stock, Interval interval)
		throws IOException, ParseException
    {
        synchronized ((stock.toString() + "-" + interval.getTimeParam()).intern())
        {
			DataItem dataItem = null;
			BufferedReader bufferedReader = null;
			String uri = getLastDataURL(1, stock, interval);
			bufferedReader = ProxyManager.getDefault().bufferReaderGET(uri);

			String inputLine;
			boolean startOnNext = false;
			while ((inputLine = bufferedReader.readLine()) != null)
			{
				if (inputLine.contains("Accesses remaining in this period"))
				{
					String accesses = inputLine.split(",", 2)[1].replace("\"", "");
					accessesRemaining = Integer.parseInt(accesses);
					if (accessesRemaining < 1)
					{
						NotifyUtil.warn("Accesses remaining", "You have no more accesses remaining", false);
						return null;
					}
				}
				if (inputLine.equals("QUOTE END"))
				{
					startOnNext = false;
					break;
				}
				if (startOnNext)
				{
					String[] values = inputLine.split(",");
					if (values.length > 6)
					{
						long time = Long.parseLong(values[2]) * 1000;
						double open = Double.parseDouble(values[3]);
						double high = Double.parseDouble(values[4]);
						double low = Double.parseDouble(values[5]);
						double close = Double.parseDouble(values[6]);
						double volume = 0d;
						dataItem = new DataItem(time, open, high, low, close, volume);
					}
				}
				if (inputLine.equals("QUOTE START"))
				{
					startOnNext = true;
					continue;
				}
			}
			bufferedReader.close();

			return dataItem;
        }
    }

	@Override
	public List<DataItem> getLastDataItems(Stock stock, Interval interval)
	{
		synchronized ((stock.toString() + "-" + interval.getTimeParam()).intern())
        {
			try
			{
				List<DataItem> dataItems = new ArrayList<DataItem>();
				BufferedReader bufferedReader = null;
				String uri = getLastDataURL(getPeriods(interval), stock, interval);
				bufferedReader = ProxyManager.getDefault().bufferReaderGET(uri);

				String inputLine;
				boolean startOnNext = false;
				while ((inputLine = bufferedReader.readLine()) != null)
				{
					if (inputLine.contains("Accesses remaining in this period"))
					{
						String accesses = inputLine.split(",", 2)[1].replace("\"", "");
						accessesRemaining = Integer.parseInt(accesses);
						if (accessesRemaining < 1)
						{
							NotifyUtil.warn("Accesses remaining", "You have no more accesses remaining", false);
							return null;
						}
					}
					if (inputLine.equals("QUOTE END"))
					{
						startOnNext = false;
						break;
					}
					if (startOnNext)
					{
						String[] values = inputLine.split(",");
						if (values.length > 6)
						{
							long time = Long.parseLong(values[2]) * 1000;
							double open = Double.parseDouble(values[3]);
							double high = Double.parseDouble(values[4]);
							double low = Double.parseDouble(values[5]);
							double close = Double.parseDouble(values[6]);
							double volume = 0d;
							DataItem dataItem = new DataItem(time, open, high, low, close, volume);
							dataItems.add(dataItem);
						}
					}
					if (inputLine.equals("QUOTE START"))
					{
						startOnNext = true;
						continue;
					}
				}
				bufferedReader.close();

				Collections.sort(dataItems);

				return dataItems;
			} catch (Exception ex)
			{
				return new ArrayList<DataItem>();
			}
        }
	}

	@Override
	public boolean updateIntraDay(String key, List<DataItem> dataItems)
	{
		DatasetUsage datasetUsage = DatasetUsage.getInstance();

		int lastIndex = datasetUsage.getDatasetFromMemory(key).getLastIndex();
		int last = dataItems.size() - 1;

		DataItem oldItem = datasetUsage.getDatasetFromMemory(key).getLastDataItem();

		if (oldItem.getTime() == dataItems.get(last).getTime())
		{
			datasetUsage.getDatasetFromMemory(key).setDataItem(lastIndex, dataItems.get(last));
			return true;
		} else
		{
			int index = 0;
			for (int i = 0; i <= last; i++)
			{
				if (oldItem.compareTo(dataItems.get(i)) == 0)
				{
					index = i;
					break;
				}
			}
			datasetUsage.getDatasetFromMemory(key).setDataItem(lastIndex, dataItems.get(index));
			for (int i = index + 1; i <= last; i++)
				datasetUsage.getDatasetFromMemory(key).addDataItem(dataItems.get(i));
			return true;
		}
	}

	private int getPeriods(Interval interval)
	{
		int periods = refreshInterval / interval.getLengthInSeconds();
		if (periods > 10) periods += 5;
		else if (periods < 10) periods = 10;
		return periods;
	}

	private String getFavoritesDataURL(Stock stock)
    {
		String url = NbBundle.getMessage(
			getClass(), "favorites.data.url",
			new Object[]
			{
				ACCESS_KEY, stock.getSymbol()
			});
        return url;
    }

    private String getDataURL(Stock stock, Interval interval)
    {
		String url = NbBundle.getMessage(
			getClass(), "data.url",
			new Object[]
			{
				ACCESS_KEY, Integer.toString(interval.getLengthInSeconds()), stock.getSymbol()
			});
        return url;
    }

    private String getLastDataURL(int period, Stock stock, Interval interval)
    {
		String url = NbBundle.getMessage(
			getClass(), "last.data.url",
			new Object[]
			{
				ACCESS_KEY, Integer.toString(period), Integer.toString(interval.getLengthInSeconds()), stock.getSymbol()
			});
        return url;
    }

	@Override
	protected Dataset fetchDataForFavorites(Stock stock)
		throws IOException, ParseException
	{
		synchronized ((stock.toString() + "-" + DAILY.getTimeParam()).intern())
		{
			Dataset result = null;
			List<DataItem> items = new ArrayList<DataItem>();

			BufferedReader bufferedReader = null;
			String uri = getFavoritesDataURL(stock);
			bufferedReader = ProxyManager.getDefault().bufferReaderGET(uri);

			String inputLine;
			boolean startOnNext = false;
			while ((inputLine = bufferedReader.readLine()) != null)
			{
				if (inputLine.contains("Accesses remaining in this period"))
				{
					String accesses = inputLine.split(",", 2)[1].replace("\"", "");
					accessesRemaining = Integer.parseInt(accesses);
					if (accessesRemaining < 1)
					{
						NotifyUtil.warn("Accesses remaining", "You have no more accesses remaining", false);
						return null;
					}
				}
				if (inputLine.equals("QUOTE END"))
				{
					startOnNext = false;
					break;
				}
				if (startOnNext)
				{
					String[] values = inputLine.split(",");
					if (values.length > 6)
					{
						long time = Long.parseLong(values[2]) * 1000;
						double open = Double.parseDouble(values[3]);
						double high = Double.parseDouble(values[4]);
						double low = Double.parseDouble(values[5]);
						double close = Double.parseDouble(values[6]);
						double volume = 0d;
						DataItem item = new DataItem(time, open, high, low, close, volume);
						items.add(item);
					}
				}
				if (inputLine.equals("QUOTE START"))
				{
					startOnNext = true;
					continue;
				}
			}

			Collections.sort(items);
			result = new Dataset(items);

			bufferedReader.close();

			return result;
		}
	}

    @Override
    protected Dataset fetchData(Stock stock, Interval interval)
		throws IOException, ParseException
    {
        synchronized ((stock.toString() + "-" + interval.getTimeParam()).intern())
        {
			Dataset result = null;
			List<DataItem> items = new ArrayList<DataItem>();

			BufferedReader bufferedReader = null;
			String uri = getDataURL(stock, interval);
			bufferedReader = ProxyManager.getDefault().bufferReaderGET(uri);

			String inputLine;
			boolean startOnNext = false;
			while ((inputLine = bufferedReader.readLine()) != null)
			{
				if (inputLine.contains("Accesses remaining in this period"))
				{
					String accesses = inputLine.split(",", 2)[1].replace("\"", "");
					accessesRemaining = Integer.parseInt(accesses);
					if (accessesRemaining < 1)
					{
						NotifyUtil.warn("Accesses remaining", "You have no more accesses remaining", false);
						return null;
					}
				}
				if (inputLine.equals("QUOTE END"))
				{
					startOnNext = false;
					break;
				}
				if (startOnNext)
				{
					String[] values = inputLine.split(",");
					if (values.length > 6)
					{
						long time = Long.parseLong(values[2]) * 1000;
						double open = Double.parseDouble(values[3]);
						double high = Double.parseDouble(values[4]);
						double low = Double.parseDouble(values[5]);
						double close = Double.parseDouble(values[6]);
						double volume = 0d;
						DataItem item = new DataItem(time, open, high, low, close, volume);
						items.add(item);
					}
				}
				if (inputLine.equals("QUOTE START"))
				{
					startOnNext = true;
					continue;
				}
			}

			Collections.sort(items);
			result = new Dataset(items);

			bufferedReader.close();

			return result;
        }
    }

    @Override
    protected String fetchCompanyName(String symbol)
            throws InvalidStockException, StockNotFoundException, RegistrationException, IOException
    {
        return symbol;
    }

	@Override
	public String getRegistrationMessage()
	{
		return NbBundle.getMessage(ForexFeedChartsy.class, "registration.msg");
	}

	@Override
	public String getRegistrationURL()
	{
		return NbBundle.getMessage(ForexFeedChartsy.class, "registration.url");
	}

	private static final long serialVersionUID = SerialVersion.APPVERSION;
    private String ACCESS_KEY = "";
	private int refreshInterval;
	private int accessesRemaining = 0;
	private static final Interval[] supportedIntervals =
	{
		new CustomInterval("5 Sec", true, 0, "5S", 5),
		new CustomInterval("10 Sec", true, 0, "10S", 10),
		new CustomInterval("30 Sec", true, 0, "30S", 30),
		new CustomInterval("45 Sec", true, 0, "45S", 45),
		new CustomInterval("1 Min", true, 0, "1M", 60),
		new CustomInterval("5 Min", true, 0, "5M", 300),
		new CustomInterval("10 Min", true, 0, "10M", 600),
		new CustomInterval("15 Min", true, 0, "15M", 900),
		new CustomInterval("20 Min", true, 0, "20M", 1200),
		new CustomInterval("30 Min", true, 0, "30M", 1800),
		new CustomInterval("45 Min", true, 0, "45M", 2700),
		new CustomInterval("1 Hour", true, 0, "1H", 3600),
		new CustomInterval("2 Hour", true, 0, "2H", 7200),
		new CustomInterval("3 Hour", true, 0, "3H", 10800),
		new CustomInterval("4 Hour", true, 0, "4H", 14400),
		new CustomInterval("6 Hour", true, 0, "6H", 21600),
		new CustomInterval("12 Hour", true, 0, "12H", 43200)
	};

}
