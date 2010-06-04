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
import org.chartsy.main.data.Exchange;
import org.chartsy.main.data.Stock;
import org.chartsy.main.data.StockSet;
import org.chartsy.main.intervals.Interval;
import org.chartsy.main.managers.ProxyManager;
import org.chartsy.main.utils.SerialVersion;
import org.chartsy.main.utils.URLChecker;

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
        super(NAME, EXCHANGES, false);
    }

    public Stock getStock(String symbol, String exchange)
    {
        Stock stock = new Stock(symbol, exchange);
        try
        {
            String url = getStockURL(stock);
            if (URLChecker.checkURL(url)) {
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
            if (URLChecker.checkURL(url))
            {
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
                    return dataset;
                }
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
        finally
        {
            try
            {
                in.close();
            }
            catch (IOException e)
            {
                LOG.log(Level.SEVERE, null, e);
            }
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
            if (URLChecker.checkURL(url))
            {
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
                        String d = st.nextToken();
                        d = d.substring(1, d.length()-1);
                        long time = df.parse(d).getTime();
                        if (time == lastTime)
                        {
                            int index = dataset.getLastIndex();
                            dataset.setCloseAt(index, close);
                        }
                        else if (time > lastTime)
                        {
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
                                dataset.addDataItem(item);
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
        String url = STOCK_URL;
        try
        {
            url = url.replace("{0}", URLEncoder.encode(stock.getKey(), "UTF-8"));
        }
        catch (UnsupportedEncodingException ex)
        { Logger.getLogger(Yahoo.class.getName()).log(Level.SEVERE, null, ex); }
        return url;
    }

    private String getDataURL(Stock stock, Interval interval)
    {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c2.setTimeInMillis(interval.startTime());

        String url = DATA_URL;

        try
        {
            url = url.replace("{0}", URLEncoder.encode(stock.getKey(), "UTF-8"));

            if (c2.get(Calendar.MONTH) < 10)
                url = url.replace("{1}", "0"+Integer.toString(c2.get(Calendar.MONTH)));
            else
                url = url.replace("{1}", Integer.toString(c2.get(Calendar.MONTH)));

            url = url.replace("{2}", Integer.toString(c2.get(Calendar.DAY_OF_MONTH)));
            url = url.replace("{3}", Integer.toString(c2.get(Calendar.YEAR)));

            if (c1.get(Calendar.MONTH) < 10)
                url = url.replace("{4}", "0"+Integer.toString(c1.get(Calendar.MONTH)));
            else
                url = url.replace("{4}", Integer.toString(c1.get(Calendar.MONTH)));

            url = url.replace("{5}", Integer.toString(c1.get(Calendar.DAY_OF_MONTH)));
            url = url.replace("{6}", Integer.toString(c1.get(Calendar.YEAR)));

            url = url.replace("{7}", URLEncoder.encode(interval.getTimeParam(), "UTF-8"));
        }
        catch (UnsupportedEncodingException ex)
        { Logger.getLogger(Yahoo.class.getName()).log(Level.SEVERE, null, ex); }

        return url;
    }

    private String getLastDataURL(Stock stock)
    {
        String url = LAST_DATA_URL;
        try
        { url = url.replace("{0}", URLEncoder.encode(stock.getKey(), "UTF-8")); }
        catch (UnsupportedEncodingException ex)
        { Logger.getLogger(Yahoo.class.getName()).log(Level.SEVERE, null, ex); }
        return url;
    }

    public StockSet getAutocomplete(String text)
    { return new StockSet(); }

    private static final String STOCK_URL = "http://finance.yahoo.com/q?s={0}";
    private static final String DATA_URL = "http://ichart.finance.yahoo.com/table.csv?s={0}&a={1}&b={2}&c={3}&d={4}&e={5}&f={6}&g={7}&ignore=.csv";
    private static final String LAST_DATA_URL = "http://download.finance.yahoo.com/d/quotes.csv?s={0}&f=sl1d1t1c1ohgv&e=.csv";
    private static final String NAME = "Yahoo";
    private static final Exchange[] EXCHANGES =
    {
        new Exchange("Default"),
        new Exchange("American Stock Exchange", ""),
        new Exchange("BATS Trading"),
        new Exchange("Chicago Board of Trade", ".CBT"),
        new Exchange("Chicago Mercantile Exchange", ".CME"),
        new Exchange("NASDAQ Stock Exchange"),
        new Exchange("New York Board of Trade", ".NYB"),
        new Exchange("New York Commodities Exchange", ".CMX"),
        new Exchange("New York Mercantile Exchange", ".NYM"),
        new Exchange("New York Stock Exchange"),
        new Exchange("OTC Bulletin Board Market", ".OB"),
        new Exchange("Pink Sheets", ".PK"),
        new Exchange("Buenos Aires Stock Exchange", ".BA"),
        new Exchange("Vienna Stock Exchange", ".VI"),
        new Exchange("Australian Stock Exchange", ".AX"),
        new Exchange("Sao Paolo Stock Exchange", ".SA"),
        new Exchange("Toronto Stock Exchange", ".TO"),
        new Exchange("TSX Venture Exchange", ".V"),
        new Exchange("Shanghai Stock Exchange", ".SS"),
        new Exchange("Shenzhen Stock Exchange", ".SZ"),
        new Exchange("Copenhagen Stock Exchange", ".CO"),
        new Exchange("Paris Stock Exchange", ".PA"),
        new Exchange("Berlin Stock Exchange", ".BE"),
        new Exchange("Bremen Stock Exchange", ".BM"),
        new Exchange("Dusseldorf Stock Exchange", ".DU"),
        new Exchange("Frankfurt Stock Exchange", ".F"),
        new Exchange("Hamburg Stock Exchange", ".HM"),
        new Exchange("Hanover Stock Exchange", ".HA"),
        new Exchange("Munich Stock Exchange", ".MU"),
        new Exchange("Stuttgart Stock Exchange", ".SG"),
        new Exchange("XETRA Stock Exchange", ".DE"),
        new Exchange("Hong Kong Stock Exchange", ".HK"),
        new Exchange("Bombay Stock Exchange", ".BO"),
        new Exchange("National Stock Exchange of India", ".NS"),
        new Exchange("Jakarta Stock Exchange", ".JK"),
        new Exchange("Tel Aviv Stock Exchange", ".TA"),
        new Exchange("Milan Stock Exchange", ".MI"),
        new Exchange("Korea Stock Exchange", ".KS"),
        new Exchange("KOSDAQ", ".KQ"),
        new Exchange("Mexico Stock Exchange", ".MX"),
        new Exchange("Amsterdam Stock Exchange", ".AS"),
        new Exchange("New Zealand Stock Exchange", ".NZ"),
        new Exchange("Oslo Stock Exchange", ".OL"),
        new Exchange("Singapore Stock Exchange", ".SI"),
        new Exchange("Barcelona Stock Exchange", ".BC"),
        new Exchange("Bilbao Stock Exchange", ".BI"),
        new Exchange("Madrid Fixed Income Market", ".MF"),
        new Exchange("Madrid SE C.A.T.S.", ".MC"),
        new Exchange("Madrid Stock Exchange", ".MA"),
        new Exchange("Stockholm Stock Exchange", ".ST"),
        new Exchange("Swiss Exchange", ".SW"),
        new Exchange("Taiwan OTC Exchange", ".TWO"),
        new Exchange("Taiwan Stock Exchange", ".TW"),
        new Exchange("London Stock Exchange", ".L")
    };

}
