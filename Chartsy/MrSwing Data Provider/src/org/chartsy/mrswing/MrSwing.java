package org.chartsy.mrswing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import org.chartsy.main.intervals.Interval;
import org.chartsy.main.managers.ProxyManager;
import org.chartsy.main.utils.SerialVersion;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;

/**
 *
 * @author viorel.gheba
 */
public class MrSwing extends DataProvider
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public MrSwing()
    {
        super(NbBundle.getBundle(MrSwing.class));
        usedCookies.clear();
        usedCookies.add("PHPSESSID");
        usedCookies.add("amember_nr");
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
		String url = getStockURL(symbol);
        BufferedReader in = ProxyManager.getDefault().bufferReaderGET(url);
        if (in == null)
            throw new StockNotFoundException();

        String firstLine = in.readLine();
        if (!firstLine.equals("OK"))
            throw new RegistrationException(
                    NbBundle.getMessage(MrSwing.class, "MSG_Registration"));

        String inputLine = in.readLine();
        if (inputLine.equals("0"))
            throw new InvalidStockException();

        return inputLine;
    }

	@Override
	protected Dataset fetchDataForFavorites(Stock stock)
		throws IOException, ParseException
	{
		synchronized ((stock.toString() + "-" + DAILY.getTimeParam()).intern())
        {
			Dataset result = null;
            List<DataItem> items = new ArrayList<DataItem>();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

            BufferedReader bufferedReader = null;
			String uri = getDataURL(stock, DAILY) + "&limit=2";
			bufferedReader = ProxyManager.getDefault().bufferReaderGET(uri);

			String inputLine = bufferedReader.readLine();
			if (inputLine.equals("OK")) // check first line
			{
				bufferedReader.readLine(); // ignore first 2 lines
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
			}

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

			String inputLine = bufferedReader.readLine();
			if (inputLine.equals("OK")) // check first line
			{
				bufferedReader.readLine(); // ignore first 2 lines
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
			}

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
			DataItem dataItem = null;

			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			BufferedReader bufferedReader = null;
			String uri = getDataURL(stock, interval) + "&limit=1";
			bufferedReader = ProxyManager.getDefault().bufferReaderGET(uri);

			String inputLine = bufferedReader.readLine();
			if (inputLine.equals("OK")) // check first line
			{
				bufferedReader.readLine(); // ignore first 2 lines
				while ((inputLine = bufferedReader.readLine()) != null)
				{
					String[] values = inputLine.split(",");
					long time = df.parse(values[0]).getTime();
					double open = Double.parseDouble(values[1]);
					double high = Double.parseDouble(values[2]);
					double low = Double.parseDouble(values[3]);
					double close = Double.parseDouble(values[4]);
					double volume = Double.parseDouble(values[5]);
					dataItem = new DataItem(time, open, high, low, close, volume);
				}
			}

			bufferedReader.close();
            /*Dataset result = fetchData(stock, interval);
            if (result != null)
            {
                int last = dataset.getLastIndex();
                DataItem newItem = result.getDataItem(result.getLastIndex());
                DataItem oldItem = dataset.getDataItem(dataset.getLastIndex());

                if (newItem.getTime() != oldItem.getTime())
                {
                    dataset.addDataItem(newItem);
                } else
                {
                    dataset.setDataItem(last, newItem);
                }
            }
            return dataset;*/
			return dataItem;
        }
    }

    private String getUsername()
    {
        Preferences p = NbPreferences.root().node("/org/chartsy/register");
        return p.get("username", null);
    }

    private String getPassword()
    {
        Preferences p = NbPreferences.root().node("/org/chartsy/register");
        return p.get("password", null);
    }

    @Override
	public StockSet fetchAutocomplete(String text)
		throws IOException
    {
        String url = getAutocompleteURL(text);
        StockSet result = new StockSet();

        final HttpClient client = ProxyManager.getDefault().getHttpClient();
        final GetMethod method = new GetMethod(url);
		client.executeMethod(method);

		InputStream inputStream = method.getResponseBodyAsStream();
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

		String inputLine;
		while ((inputLine = bufferedReader.readLine()) != null)
		{
			StringTokenizer st = new StringTokenizer(inputLine, ":");
			String key = st.nextToken();
			String company = st.nextToken();
			String exchange = st.nextToken();
			result.add(new StockNode(key, company, exchange));
		}

		bufferedReader.close();
		method.releaseConnection();

        return result;
    }

    private String getStockURL(String symbol)
		throws UnsupportedEncodingException
    {
		return NbBundle.getMessage(MrSwing.class, "Stock_URL", new String[]
		{
			URLEncoder.encode(symbol, "UTF-8"),
			getUsername(),
			getPassword()
		});
    }

    private String getDataURL(Stock stock, Interval interval)
		throws UnsupportedEncodingException
    {
		return NbBundle.getMessage(MrSwing.class, "Data_URL", new String[]
				{
					URLEncoder.encode(stock.getKey(), "UTF-8"),
					URLEncoder.encode(interval.getTimeParam(), "UTF-8"),
					getUsername(),
					getPassword()
				});
    }

    private String getAutocompleteURL(String word)
		throws UnsupportedEncodingException
    {
		return NbBundle.getMessage(MrSwing.class, "Autocomplete_URL", URLEncoder.encode(word, "UTF-8"));
    }

    private List<String> usedCookies = new ArrayList<String>();
	
}
