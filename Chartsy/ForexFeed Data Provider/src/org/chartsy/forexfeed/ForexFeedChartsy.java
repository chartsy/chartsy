package org.chartsy.forexfeed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.chartsy.main.data.DataItem;
import org.chartsy.main.data.DataProvider;
import org.chartsy.main.data.Dataset;
import org.chartsy.main.data.DateCompare;
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

public class ForexFeedChartsy extends DataProvider
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;
    private static final Logger LOG = Logger.getLogger(ForexFeedChartsy.class.getPackage().getName());
    private String ACCESS_KEY = "";

    public ForexFeedChartsy()
    {
        super(NbBundle.getBundle(ForexFeedChartsy.class), true);
        Preferences pref = NbPreferences.forModule(ForexFeedPanel.class);
        this.ACCESS_KEY = pref.get("access_key", "");
    }

    @Override
    public int getRefreshInterval()
    {
        Preferences pref = NbPreferences.forModule(ForexFeedPanel.class);
        return pref.getInt("refresh_interval", 5);
    }

    @Override
    public StockSet getAutocomplete(String text)
    {
        String url = "http://fxf.forexfeed.net/symbols/CHARTSY-" + ACCESS_KEY;
        StockSet result = new StockSet();

        BufferedReader in = null;
        final HttpClient client = ProxyManager.getDefault().getHttpClient();
        final GetMethod method = new GetMethod(url);

        try
        {
            client.executeMethod(method);
            in = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));

            String inputLine;
            int i = 0;
            while ((inputLine = in.readLine()) != null)
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

            in.close();
            method.releaseConnection();
        } catch (IOException ex)
        {
            LOG.log(Level.SEVERE, null, ex);
        }

        return result;
    }

    @Override
    public Dataset getLastDataItem(Stock stock, Interval interval, Dataset dataset)
    {
        synchronized ((stock.toString() + "-" + interval.getTimeParam()).intern())
        {
            long lastTime = dataset.getLastTime();
            BufferedReader in = null;
            try
            {
                in = ProxyManager.getDefault().bufferReaderGET(getLastDataURL(stock, interval));
                if (in != null)
                {
                    String inputLine;
                    int i = 0;
                    while ((inputLine = in.readLine()) != null)
                    {
                        if (i > 16)
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
                                int index = dataset.getLastIndex();
                                if (time == lastTime)
                                {
                                    dataset.setDataItem(index, item);
                                } else if (time > lastTime)
                                {
                                    dataset.addDataItem(item);
                                }
                            }
                        }
                        i++;
                    }
                    in.close();
                    return dataset;
                }
            } catch (Exception ex)
            {
                LOG.log(Level.WARNING, null, ex);
            }
            return null;
        }
    }

    private String getDataURL(Stock stock, Interval interval)
    {
        return "http://fxf.forexfeed.net/data?key=CHARTSY-" + ACCESS_KEY + "&periods=1000&format=csv&interval=" + interval.getLengthInSeconds() + "&symbol=" + stock.getSymbol();
    }

    private String getLastDataURL(Stock stock, Interval interval)
    {
        return "http://fxf.forexfeed.net/data?key=CHARTSY-" + ACCESS_KEY + "&periods=3&format=csv&interval=" + interval.getLengthInSeconds() + "&symbol=" + stock.getSymbol();
    }

    @Override
    public Dataset getData(Stock stock, Interval interval)
    {
        synchronized ((stock.toString() + "-" + interval.getTimeParam()).intern())
        {
            List<DataItem> items = new ArrayList<DataItem>();
            DateCompare compare = new DateCompare();
            BufferedReader in = null;

            try
            {
                in = ProxyManager.getDefault().bufferReaderGET(getDataURL(stock, interval));
                if (in != null)
                {
                    String inputLine;
                    int i = 0;
                    while ((inputLine = in.readLine()) != null)
                    {
                        if (i > 16)
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
                                items.add(new DataItem(time, open, high, low, close, volume));
                            }
                        }
                        i++;
                    }
                    in.close();
                    Collections.sort(items, compare);
                    return new Dataset(items);
                }
            } catch (Exception ex)
            {
                LOG.log(Level.WARNING, null, ex);
            }
            return null;
        }
    }

    @Override
    public String fetchCompanyName(Stock stock)
            throws InvalidStockException, StockNotFoundException, RegistrationException, IOException
    {
        return stock.getSymbol();
    }
}
