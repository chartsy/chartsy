package org.chartsy.yahoo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.chartsy.main.data.DataItem;
import org.chartsy.main.data.DataProvider;
import org.chartsy.main.data.Dataset;
import org.chartsy.main.data.DateCompare;
import org.chartsy.main.data.Stock;
import org.chartsy.main.data.StockSet;
import org.chartsy.main.intervals.Interval;
import org.chartsy.main.managers.ProxyManager;
import org.chartsy.main.utils.SerialVersion;
import org.openide.util.NbBundle;

/**
 *
 * @author viorel.gheba
 */
public class Yahoo
        extends DataProvider
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public Yahoo()
    {
		super(NbBundle.getBundle(Yahoo.class), false);
    }

    public Stock getStock(String symbol, String exchange)
    {
        Stock stock = new Stock(symbol, exchange);
        try
        {
            String url = getStockURL(stock);
			HttpClient client = ProxyManager.getDefault().getHttpClient();
			HttpMethod method = new GetMethod(url);
			client.executeMethod(method);
			BufferedReader in = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null)
			{
				if (inputLine.contains("<title>"))
				{
					String title = inputLine.split("<title>")[1].split("</title>")[0];
					if (title.contains("Summary for "))
					{
						String companyName = title.split("Summary for ")[1].split("-")[0];
						stock.setCompanyName(companyName);
					}
					else
					{
						stock.setCompanyName("");
					}
				}
				if (inputLine.contains("There are no All Markets results"))
					return null;
			}
			in.close();
			method.releaseConnection();
        }
        catch (Exception ex)
        {
            LOG.log(Level.SEVERE, null, ex);
        }
        
        return stock;
    }

    public Dataset getData(Stock stock, Interval interval)
    {
        List<DataItem> items = new ArrayList<DataItem>();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        DateCompare compare = new DateCompare();
        BufferedReader in = null;
        try
        {
            String url = getDataURL(stock, interval);
			HttpClient client = ProxyManager.getDefault().getHttpClient();
			HttpMethod method = new GetMethod(url);
			client.executeMethod(method);
			in = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
			if (in != null)
			{
				String inputLine;
				in.readLine();
				while ((inputLine = in.readLine()) != null)
				{
					StringTokenizer st = new StringTokenizer(inputLine, ",");
					long time = df.parse(st.nextToken()).getTime();
					Double open = Double.parseDouble(st.nextToken());
					Double high = Double.parseDouble(st.nextToken());
					Double low = Double.parseDouble(st.nextToken());
					Double close = Double.parseDouble(st.nextToken());
					Double volume = Double.parseDouble(st.nextToken());
					st.nextToken(); // ignore value
					DataItem item = new DataItem(time, open, high, low, close, volume);
					items.add(item);
				}
				Collections.sort(items, compare);
				Dataset dataset = new Dataset(items);
				method.releaseConnection();
				in.close();
				return dataset;
			}
        }
        catch (IOException ex)
        {
            LOG.log(Level.SEVERE, null, ex);
        }
        catch (ParseException ex)
        {
            LOG.log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Dataset getLastDataItem(Stock stock, Interval interval, Dataset dataset)
    {
		long lastTime = dataset.getLastTime();
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		BufferedReader in = null;

		try
		{
			String url = getLastDataURL(stock);
			HttpClient client = ProxyManager.getDefault().getHttpClient();
			HttpMethod method = new GetMethod(url);
			client.executeMethod(method);
			in = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
			if (in != null)
			{
				String inputLine;
				while ((inputLine = in.readLine()) != null)
				{
					StringTokenizer st = new StringTokenizer(inputLine, ",");
					st.nextToken(); // ignore symbol name
					double close = Double.parseDouble(st.nextToken());
					String date = st.nextToken();
					date = date.substring(1, date.length()-1);
					if (!date.equals("N/A"))
					{
						long time = df.parse(date).getTime();

						String t = st.nextToken();
						st.nextToken(); // ignore value
						t = st.nextToken();
						if (!t.equals("N/A"))
						{
							double open = Double.parseDouble(t);
							double high = Double.parseDouble(st.nextToken());
							double low = Double.parseDouble(st.nextToken());
							double volume = Double.parseDouble(st.nextToken());
							DataItem item = new DataItem(time, open, high, low, close, volume);

							int index = dataset.getLastIndex();

							if (interval.equals(DAILY))
							{
								if (time == lastTime)
									dataset.setDataItem(index, item);
								else
									dataset.addDataItem(item);
							}
							else if (interval.equals(WEEKLY))
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
								}
								else
									dataset.addDataItem(item);
							}
							else if (interval.equals(MONTHLY))
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
								}
								else
									dataset.addDataItem(item);
							}
						}
					}
					method.releaseConnection();
					in.close();
					return dataset;
				}
			}
			else
			{
				method.releaseConnection();
			}
		}
		catch (ParseException ex)
		{
			Logger.getLogger(Yahoo.class.getName()).log(Level.SEVERE, null, ex);
		}
		catch (IOException ex)
		{
			Logger.getLogger(Yahoo.class.getName()).log(Level.SEVERE, null, ex);
		}
        return null;
    }

    private String getStockURL(Stock stock)
    {
        try
        {
			return NbBundle.getMessage(Yahoo.class, "Stock_URL", URLEncoder.encode(stock.getKey(), "UTF-8"));
        }
        catch (UnsupportedEncodingException ex)
        {
			Logger.getLogger(Yahoo.class.getName()).log(Level.SEVERE, null, ex);
		}

        return NbBundle.getMessage(Yahoo.class, "Stock_URL", stock.getKey());
    }

    private String getDataURL(Stock stock, Interval interval)
    {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c2.setTimeInMillis(interval.startTime());

        try
        {
			return NbBundle.getMessage(Yahoo.class, "Data_URL", new String[]
			{
				URLEncoder.encode(stock.getKey(), "UTF-8"),
				c2.get(Calendar.MONTH) < 10 ? "0"+Integer.toString(c2.get(Calendar.MONTH)) : Integer.toString(c2.get(Calendar.MONTH)),
				Integer.toString(c2.get(Calendar.DAY_OF_MONTH)),
				Integer.toString(c2.get(Calendar.YEAR)),
				c1.get(Calendar.MONTH) < 10 ? "0"+Integer.toString(c1.get(Calendar.MONTH)) : Integer.toString(c1.get(Calendar.MONTH)),
				Integer.toString(c1.get(Calendar.DAY_OF_MONTH)),
				Integer.toString(c1.get(Calendar.YEAR)),
				URLEncoder.encode(interval.getTimeParam(), "UTF-8")
			});
        }
        catch (UnsupportedEncodingException ex)
        { 
			Logger.getLogger(Yahoo.class.getName()).log(Level.SEVERE, null, ex);
		}

        return NbBundle.getMessage(Yahoo.class, "Data_URL", new String[]
		{
			stock.getKey(),
			c2.get(Calendar.MONTH) < 10 ? "0"+Integer.toString(c2.get(Calendar.MONTH)) : Integer.toString(c2.get(Calendar.MONTH)),
			Integer.toString(c2.get(Calendar.DAY_OF_MONTH)),
			Integer.toString(c2.get(Calendar.YEAR)),
			c1.get(Calendar.MONTH) < 10 ? "0"+Integer.toString(c1.get(Calendar.MONTH)) : Integer.toString(c1.get(Calendar.MONTH)),
			Integer.toString(c1.get(Calendar.DAY_OF_MONTH)),
			Integer.toString(c1.get(Calendar.YEAR)),
			interval.getTimeParam()
		});
    }

    private String getLastDataURL(Stock stock)
    {
        try
        {
			return NbBundle.getMessage(Yahoo.class, "LastData_URL", URLEncoder.encode(stock.getKey(), "UTF-8"));
		}
        catch (UnsupportedEncodingException ex)
        {
			Logger.getLogger(Yahoo.class.getName()).log(Level.SEVERE, null, ex);
		}

        return NbBundle.getMessage(Yahoo.class, "LastData_URL", stock.getKey());
    }

    public StockSet getAutocomplete(String text)
    { 
		return new StockSet();
	}

}
