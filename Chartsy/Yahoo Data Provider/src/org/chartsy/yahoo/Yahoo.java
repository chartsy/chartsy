package org.chartsy.yahoo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import org.chartsy.main.data.DataItem;
import org.chartsy.main.data.DataProvider;
import org.chartsy.main.data.Dataset;
import org.chartsy.main.data.Stock;
import org.chartsy.main.data.StockSet;
import org.chartsy.main.exceptions.InvalidStockException;
import org.chartsy.main.exceptions.RegistrationException;
import org.chartsy.main.exceptions.StockNotFoundException;
import org.chartsy.main.intervals.Interval;
import org.chartsy.main.managers.ProxyManager;
import org.chartsy.main.utils.SerialVersion;
import org.openide.util.NbBundle;

/**
 *
 * @author viorel.gheba
 */
public class Yahoo extends DataProvider
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public Yahoo()
    {
        super(NbBundle.getBundle(Yahoo.class));
    }

    @Override
    public int getRefreshInterval()
    {
        return 5;
    }

	@Override
	protected String fetchCompanyName(String symbol)
		throws InvalidStockException, StockNotFoundException, RegistrationException, IOException
	{
		String company = "";
		String uri = getSymbolURL(symbol);
		BufferedReader bufferedReader = ProxyManager.getDefault().bufferReaderGET(uri);
		if (bufferedReader == null)
			throw new InvalidStockException();

		String inputLine;
        while ((inputLine = bufferedReader.readLine()) != null)
        {
            if (inputLine.contains("<title>"))
            {
                String title = inputLine.split("<title>")[1].split("</title>")[0];
                if (title.equals("Symbol Lookup from Yahoo! Finance"))
                {
                    throw new StockNotFoundException();
                } else if (title.startsWith(symbol))
                {
                    company = title.split("Summary for ")[1].split("-")[0];
                    break;
                } else
                {
                    throw new InvalidStockException();
                }
            }
        }

		bufferedReader.close();

		return company;
	}

	@Override
	protected Dataset fetchDataForFavorites(Stock stock)
		throws IOException, ParseException
	{
		synchronized ((stock.toString() + "-" + DAILY.getTimeParam()).intern())
        {
			Dataset result = null;
            List<DataItem> items = new ArrayList<DataItem>();
			List<DataItem> aux = new ArrayList<DataItem>();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

			BufferedReader bufferedReader = null;
			String uri = getFavoritesDataURL(stock);
			bufferedReader = ProxyManager.getDefault().bufferReaderGET(uri);

			bufferedReader.readLine(); // ignore first line
			String inputLine;
			while ((inputLine = bufferedReader.readLine()) != null)
			{
				String[] values = inputLine.split(",");
				long time = df.parse(values[0]).getTime();
				double open = Double.parseDouble(values[1]);
				double high = Double.parseDouble(values[2]);
				double low = Double.parseDouble(values[3]);
				double close = Double.parseDouble(values[4]);
				double volume = Double.parseDouble(values[5]);
				DataItem item = new DataItem(time, open, high, low, close, volume);
				aux.add(item);
			}
			Collections.sort(aux);
			int count = aux.size();
			items.add(aux.get(count - 2));
			items.add(aux.get(count - 1));
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
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

			BufferedReader bufferedReader = null;
			String uri = getDataURL(stock, interval);
			bufferedReader = ProxyManager.getDefault().bufferReaderGET(uri);
			
			bufferedReader.readLine(); // ignore first line
			String inputLine;
			while ((inputLine = bufferedReader.readLine()) != null)
			{
				String[] values = inputLine.split(",");
				long time = df.parse(values[0]).getTime();
				double open = Double.parseDouble(values[1]);
				double high = Double.parseDouble(values[2]);
				double low = Double.parseDouble(values[3]);
				double close = Double.parseDouble(values[4]);
				double volume = Double.parseDouble(values[5]);
				DataItem item = new DataItem(time, open, high, low, close, volume);
				items.add(item);
			}
			Collections.sort(items);
			result = new Dataset(items);

			bufferedReader.close();

            return result;
        }
    }

    @Override
    protected DataItem fetchLastDataItem(Stock stock, Interval interval)
		throws IOException, ParseException
    {
        synchronized ((stock.toString() + "-" + interval.getTimeParam()).intern())
        {
            //long lastTime = dataset.getLastTime();
			DataItem dataItem = null;
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy");

            BufferedReader bufferedReader = null;
			String uri = getLastDataURL(stock);
			bufferedReader = ProxyManager.getDefault().bufferReaderGET(uri);

			String inputLine;
			while ((inputLine = bufferedReader.readLine()) != null)
			{
				String[] values = inputLine.split(",");
				double close = new Double(values[1]);
				String date = values[2];
				date = date.substring(1, date.length() - 1);
				if (!date.equals("N/A"))
				{
					long time = df.parse(date).getTime();
					if (!values[5].equals("N/A"))
					{
						double open = Double.parseDouble(values[5]);
						double high = Double.parseDouble(values[6]);
						double low = Double.parseDouble(values[7]);
						double volume = Double.parseDouble(values[8]);
						dataItem = new DataItem(time, open, high, low, close, volume);

						/*int index = dataset.getLastIndex();

						if (interval.equals(DAILY))
						{
							if (time == lastTime)
							{
								dataset.setDataItem(index, item);
							} else
							{
								dataset.addDataItem(item);
							}
						} else if (interval.equals(WEEKLY))
						{
							Calendar cal1 = Calendar.getInstance();
							cal1.setTimeInMillis(lastTime);
							cal1.setFirstDayOfWeek(Calendar.MONDAY);
							Calendar cal2 = Calendar.getInstance();
							cal2.setTimeInMillis(lastTime);
							cal2.setFirstDayOfWeek(Calendar.MONDAY);

							if ((cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
									&& (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)))
							{
								dataset.setCloseAt(index, close);
								dataset.setHighAt(index, Math.max(high, dataset.getHighAt(index)));
								dataset.setLowAt(index, Math.min(low, dataset.getLowAt(index)));
								dataset.setVolumeAt(index, volume);
							} else
							{
								dataset.addDataItem(item);
							}
						} else if (interval.equals(MONTHLY))
						{
							Calendar cal1 = Calendar.getInstance();
							cal1.setTimeInMillis(lastTime);
							cal1.setFirstDayOfWeek(Calendar.MONDAY);
							Calendar cal2 = Calendar.getInstance();
							cal2.setTimeInMillis(lastTime);
							cal2.setFirstDayOfWeek(Calendar.MONDAY);

							if ((cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH))
									&& (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)))
							{
								dataset.setCloseAt(index, close);
								dataset.setHighAt(index, Math.max(high, dataset.getHighAt(index)));
								dataset.setLowAt(index, Math.min(low, dataset.getLowAt(index)));
								dataset.setVolumeAt(index, volume);
							} else
							{
								dataset.addDataItem(item);
							}
						}*/
					}
					
					bufferedReader.close();
				}
			}
				
            return dataItem;
        }
    }

	private String getSymbolURL(String symbol)
		throws UnsupportedEncodingException
    {
		return NbBundle.getMessage(Yahoo.class, "Stock_URL", URLEncoder.encode(symbol, "UTF-8"));
    }

	private String getFavoritesDataURL(Stock stock)
		throws UnsupportedEncodingException
    {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c2.add(Calendar.DATE, -7);

		return NbBundle.getMessage(Yahoo.class, "Data_URL", new String[]
		{
			URLEncoder.encode(stock.getKey(), "UTF-8"),
			c2.get(Calendar.MONTH) < 10 ? "0" + Integer.toString(c2.get(Calendar.MONTH)) : Integer.toString(c2.get(Calendar.MONTH)),
			Integer.toString(c2.get(Calendar.DAY_OF_MONTH)),
			Integer.toString(c2.get(Calendar.YEAR)),
			c1.get(Calendar.MONTH) < 10 ? "0" + Integer.toString(c1.get(Calendar.MONTH)) : Integer.toString(c1.get(Calendar.MONTH)),
			Integer.toString(c1.get(Calendar.DAY_OF_MONTH)),
			Integer.toString(c1.get(Calendar.YEAR)),
			URLEncoder.encode(DAILY.getTimeParam(), "UTF-8")
		});
    }

    private String getDataURL(Stock stock, Interval interval)
		throws UnsupportedEncodingException
    {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c2.setTimeInMillis(interval.startTime());

		return NbBundle.getMessage(Yahoo.class, "Data_URL", new String[]
		{
			URLEncoder.encode(stock.getKey(), "UTF-8"),
			c2.get(Calendar.MONTH) < 10 ? "0" + Integer.toString(c2.get(Calendar.MONTH)) : Integer.toString(c2.get(Calendar.MONTH)),
			Integer.toString(c2.get(Calendar.DAY_OF_MONTH)),
			Integer.toString(c2.get(Calendar.YEAR)),
			c1.get(Calendar.MONTH) < 10 ? "0" + Integer.toString(c1.get(Calendar.MONTH)) : Integer.toString(c1.get(Calendar.MONTH)),
			Integer.toString(c1.get(Calendar.DAY_OF_MONTH)),
			Integer.toString(c1.get(Calendar.YEAR)),
			URLEncoder.encode(interval.getTimeParam(), "UTF-8")
		});
    }

    private String getLastDataURL(Stock stock)
		throws UnsupportedEncodingException
    {
		return NbBundle.getMessage(Yahoo.class, "LastData_URL", URLEncoder.encode(stock.getKey(), "UTF-8"));
    }

	@Override
    public StockSet fetchAutocomplete(String text)
		throws IOException
    {
        return new StockSet();
    }
}
