package org.chartsy.mrswing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

/**
 *
 * @author viorel.gheba
 */
public class MrSwing
        extends DataProvider
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;
    private static final Logger LOG = Logger.getLogger(MrSwing.class.getPackage().getName());

    public MrSwing()
    {
        super(NbBundle.getBundle(MrSwing.class), false);
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
    public String fetchCompanyName(Stock stock)
            throws InvalidStockException, StockNotFoundException, RegistrationException, IOException
    {
        BufferedReader in = ProxyManager.getDefault().bufferReaderGET(getStockURL(stock));

        if (in == null)
        {
            throw new StockNotFoundException();
        }

        String firstLine = in.readLine();
        if (!firstLine.equals("OK"))
        {
            throw new RegistrationException(
                    NbBundle.getMessage(MrSwing.class, "MSG_Registration"));
        }

        String inputLine = in.readLine();
        if (inputLine.equals("0"))
        {
            throw new InvalidStockException();
        }

        return inputLine;
    }

    public Dataset getData(Stock stock, Interval interval)
    {
        synchronized ((stock.toString() + "-" + interval.getTimeParam()).intern())
        {
            List<DataItem> items = new ArrayList<DataItem>();
            DateCompare compare = new DateCompare();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            BufferedReader in = null;
            try
            {
                in = ProxyManager.getDefault().bufferReaderGET(getDataURL(stock, interval));
                if (in != null)
                {
                String inputLine = in.readLine();
                if (inputLine.equals("OK")) // check first line
                {
                in.readLine(); // ignore first 2 lines
                while ((inputLine = in.readLine()) != null)
                {
                String[] values = inputLine.split(",");
                long time = df.parse(values[0]).getTime();
                double open = Double.parseDouble(values[1]);
                double high = Double.parseDouble(values[2]);
                double low = Double.parseDouble(values[3]);
                double close = Double.parseDouble(values[4]);
                double volume = Double.parseDouble(values[5]);
                items.add(new DataItem(time, open, high, low, close, volume));
                }
                Collections.sort(items, compare);
                return new Dataset(items);
                }
                }
            } catch (Exception ex)
            {
                LOG.log(Level.WARNING, null, ex);
            } finally
            {
                try
                {
                    in.close();
                } catch (IOException ex)
                {
                    LOG.log(Level.WARNING, "", ex);
                }
            }

            return null;
        }
    }

    public Dataset getLastDataItem(Stock stock, Interval interval, Dataset dataset)
    {
        synchronized ((stock.toString() + "-" + interval.getTimeParam()).intern())
        {
            Dataset result = getRegisteredDataset(stock, interval);
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
            return dataset;
        }
    }

    private Dataset getRegisteredDataset(Stock stock, Interval interval)
    {
        synchronized ((stock.toString() + "-" + interval.getTimeParam()).intern())
        {
            List<DataItem> items = new ArrayList<DataItem>();
            DateCompare compare = new DateCompare();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            BufferedReader in = null;

            try
            {
                in = ProxyManager.getDefault().bufferReaderGET(getDataURL(stock, interval) + "&limit=1");

                if (in != null)
                {
                    String inputLine = in.readLine();
                    if (inputLine.equals("OK")) // check first line
                    {
                        in.readLine(); // ignore first 2 lines
                        while ((inputLine = in.readLine()) != null)
                        {
                            String[] values = inputLine.split(",");
                            long time = df.parse(values[0]).getTime();
                            double open = Double.parseDouble(values[1]);
                            double high = Double.parseDouble(values[2]);
                            double low = Double.parseDouble(values[3]);
                            double close = Double.parseDouble(values[4]);
                            double volume = Double.parseDouble(values[5]);
                            items.add(new DataItem(time, open, high, low, close, volume));
                        }
                        Collections.sort(items, compare);
                        return new Dataset(items);
                    }
                }
            } catch (Exception ex)
            {
                LOG.log(Level.WARNING, null, ex);
            } finally
            {
                try
                {
                    in.close();
                } catch (IOException ex)
                {
                    LOG.log(Level.WARNING, null, ex);
                }
            }
            return null;
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

    public StockSet getAutocomplete(String text)
    {
        String url = getAutocompleteURL(text);
        StockSet result = new StockSet();

        BufferedReader in = null;
        final HttpClient client = ProxyManager.getDefault().getHttpClient();
        final GetMethod method = new GetMethod(url);

        try
        {
            client.executeMethod(method);
            in = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null)
            {

                StringTokenizer st = new StringTokenizer(inputLine, ":");
                String key = st.nextToken();
                String company = st.nextToken();
                String exchange = st.nextToken();
                result.add(new StockNode(key, company, exchange));
            }

            in.close();
            method.releaseConnection();
        } catch (IOException ex)
        {
            LOG.log(Level.SEVERE, null, ex);
        }

        return result;
    }

    private String getStockURL(Stock stock)
    {
        try
        {
            return NbBundle.getMessage(MrSwing.class, "Stock_URL", new String[]
                    {
                        URLEncoder.encode(stock.getKey(), "UTF-8"),
                        getUsername(),
                        getPassword()
                    });
        } catch (UnsupportedEncodingException ex)
        {
            LOG.log(Level.SEVERE, null, ex);
        }

        return NbBundle.getMessage(MrSwing.class, "Stock_URL", new String[]
                {
                    stock.getKey(),
                    getUsername(),
                    getPassword()
                });
    }

    private String getDataURL(Stock stock, Interval interval)
    {
        try
        {
            return NbBundle.getMessage(MrSwing.class, "Data_URL", new String[]
                    {
                        URLEncoder.encode(stock.getKey(), "UTF-8"),
                        URLEncoder.encode(interval.getTimeParam(), "UTF-8"),
                        getUsername(),
                        getPassword()
                    });
        } catch (UnsupportedEncodingException ex)
        {
            LOG.log(Level.SEVERE, null, ex);
        }

        return NbBundle.getMessage(MrSwing.class, "Data_URL", new String[]
                {
                    stock.getKey(),
                    interval.getTimeParam(),
                    getUsername(),
                    getPassword()
                });
    }

    private String getAutocompleteURL(String word)
    {
        try
        {
            return NbBundle.getMessage(MrSwing.class, "Autocomplete_URL", URLEncoder.encode(word, "UTF-8"));
        } catch (UnsupportedEncodingException ex)
        {
            LOG.log(Level.SEVERE, null, ex);
        }

        return NbBundle.getMessage(MrSwing.class, "Autocomplete_URL", word);
    }
    private List<String> usedCookies = new ArrayList<String>();
}
