package org.chartsy.mrswing;

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.prefs.Preferences;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.chartsy.main.data.DataItem;
import org.chartsy.main.data.DataProvider;
import org.chartsy.main.data.Dataset;
import org.chartsy.main.data.DateCompare;
import org.chartsy.main.data.Exchange;
import org.chartsy.main.data.Stock;
import org.chartsy.main.data.StockNode;
import org.chartsy.main.data.StockSet;
import org.chartsy.main.intervals.Interval;
import org.chartsy.main.managers.ProxyManager;
import org.chartsy.main.utils.DesktopUtil;
import org.chartsy.main.utils.URLChecker;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.NbPreferences;

/**
 *
 * @author viorel.gheba
 */
public class MrSwing extends DataProvider implements Serializable {

    private static final long serialVersionUID = 2L;

    public MrSwing()
    {
        super(NAME, EXCHANGES, false);
        usedCookies.clear();
        usedCookies.add("PHPSESSID");
        usedCookies.add("amember_nr");
    }

    public Stock getStock(String symbol, String exchange)
    {
        Stock stock = new Stock(symbol, exchange);
        String url = getStockURL(stock);
        if (URLChecker.checkURL(url))
        {
            return getRegisteredStock(symbol, exchange, needsRegistration(url));
        }
        else
        {
            return null;
        }
    }

    public Dataset getData(Stock stock, Interval interval)
    {
        String url = getDataURL(stock, interval);
        if (URLChecker.checkURL(url))
        {
            return getRegisteredDataset(stock, interval, needsRegistration(url));
        }
        else
        {
            return null;
        }
    }

    public Dataset getLastDataItem(Stock stock, Interval interval, Dataset dataset)
    {
        String url = getDataURL(stock, interval) + "&limit=1";
        
        if (URLChecker.checkURL(url))
        {
            Dataset result = getRegisteredDataset(stock, interval, needsRegistration(url));
            if (result != null)
            {
                int last = dataset.getLastIndex();
                DataItem newItem = result.getDataItem(result.getLastIndex());
                DataItem oldItem = dataset.getDataItem(dataset.getLastIndex());

                if (newItem.getTime() != oldItem.getTime())
                {
                    dataset.addDataItem(newItem);
                }
                else
                {
                    dataset.setDataItem(last, newItem);
                }
            }
        }

        return dataset;
    }

    private Stock getRegisteredStock(String symbol, String exchange, boolean setCookie)
    {
        Stock newStock = new Stock();
        Stock stock = new Stock(symbol, exchange);
        final String url = getStockURL(stock);

        BufferedReader in = null;
        final HttpClient client = ProxyManager.getDefault().getHttpClient();
        final GetMethod method = new GetMethod(url);
        
        if (setCookie)
        {
            if (checkIfRegistred(url))
            {
                method.setFollowRedirects(true);
                List<Cookie> list = getCookies(url);
                for (Cookie cookie : list)
                    client.getState().addCookie(cookie);
            }
            else
            {
                SwingUtilities.invokeLater(new Runnable()
                {
                    public void run()
                    {
                        String html = "<html>This symbol is covered by the \"Chartsy.org global EOD daily data\" feed offered by mrswing.com for only 7.79$/month.<br>" +
                                "In order to use that feed please login with your Chartsy username and password at <a href='www.mrswing.com/amember/member.php'>www.mrswing.com/amember/member.php</a>,<br>" +
                                "check \"Chartsy.org global EOD daily data\" from the \"Add/Renew subscription\" section at the right and then click Order.<br>" +
                                "IMPORTANT: Please make sure your Chartsy copy is registered (Help->Register).</html>";
                        final JLabel label = new JLabel(html);
                        label.addMouseListener(new MouseAdapter()
                        {
                            @Override
                            public void mouseClicked(MouseEvent e)
                            {
                                try { DesktopUtil.browse("www.mrswing.com/amember/member.php"); }
                                catch (Exception ex) { LOG.log(Level.SEVERE, null, ex); }
                            }
                            @Override
                            public void mouseEntered(MouseEvent e) { label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); }
                            @Override
                            public void mouseExited(MouseEvent e) { label.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)); }
                        });
                        DialogDescriptor d = new DialogDescriptor(label, "Register");
                        d.setMessageType(DialogDescriptor.INFORMATION_MESSAGE);
                        d.setOptions(new Object[] {DialogDescriptor.OK_OPTION, DialogDescriptor.CANCEL_OPTION});
                        Object ret = DialogDisplayer.getDefault().notify(d);
                        if (ret.equals(DialogDescriptor.OK_OPTION))
                        {
                            try { 
                                DesktopUtil.browse("www.mrswing.com/amember/member.php");
                            }
                            catch (Exception ex)
                            {
                                LOG.log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                });

                return new Stock();
            }
        }

        try
        {
            client.executeMethod(method);
            in = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));

            String inputLine;
            if (in.readLine().equals("OK")) // ignore first line
            {
                while ((inputLine = in.readLine()) != null)
                {
                    if (inputLine.equals("0")) // null symbol
                    {
                        in.close();
                        method.releaseConnection();
                        return null;
                    }
                    else
                    {
                        newStock.setSymbol(stock.getSymbol());
                        newStock.setExchange(stock.getExchange());
                        newStock.setCompanyName(inputLine);
                        break;
                    }
                }
                in.close();
                method.releaseConnection();
            }
            else
            {
                in.close();
                method.releaseConnection();
                return null;
            }
        }
        catch (IOException ex)
        {
            LOG.log(Level.SEVERE, null, ex);
        }

        return newStock;
    }

    private Dataset getRegisteredDataset(Stock stock, Interval interval, boolean setCookie)
    {
        Dataset dataset = null;
        List<DataItem> items = new ArrayList<DataItem>();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        DateCompare compare = new DateCompare();

        String url = getDataURL(stock, interval);
        BufferedReader in = null;
        HttpClient client = ProxyManager.getDefault().getHttpClient();
        HttpMethod method = new GetMethod(url);

        if (setCookie)
        {
            method.setFollowRedirects(true);
            List<Cookie> list = getCookies(url);
            for (Cookie cookie : list)
                client.getState().addCookie(cookie);
        }

        try
        {
            client.executeMethod(method);
            in = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));

            if (in != null)
            {
                String inputLine;
                if (in.readLine().equals("OK")) // check first line
                {
                    in.readLine(); // ignore first 2 lines

                    while ((inputLine = in.readLine()) != null)
                    {
                        StringTokenizer st = new StringTokenizer(inputLine, ",");
                        long time = df.parse(st.nextToken()).getTime();
                        Double open = Double.parseDouble(st.nextToken());
                        Double high = Double.parseDouble(st.nextToken());
                        Double low = Double.parseDouble(st.nextToken());
                        Double close = Double.parseDouble(st.nextToken());
                        Double volume = Double.parseDouble(st.nextToken());

                        DataItem item = new DataItem(time, open, high, low, close, volume);
                        items.add(item);
                    }

                    in.close();
                    method.releaseConnection();

                    Collections.sort(items, compare);
                    dataset = new Dataset(items);
                }
                else
                {
                    in.close();
                    method.releaseConnection();
                }
            }
            else
            {
                in.close();
                method.releaseConnection();
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

        return dataset;
    }

    private boolean needsRegistration(String url)
    {
        BufferedReader in = null;
        HttpClient client = ProxyManager.getDefault().getHttpClient();
        HttpMethod method = new GetMethod(url);

        try
        {
            client.executeMethod(method);
            in = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null)
            {
                if (inputLine.equals("OK"))
                {
                    method.releaseConnection();
                    in.close();
                    return false;
                }
                else
                {
                    method.releaseConnection();
                    in.close();
                    return true;
                }
            }
        }
        catch (IOException ex)
        {
            LOG.log(Level.SEVERE, null, ex);
        }

        return false;
    }

    private List<Cookie> getCookies(String url)
    {
        List<Cookie> list = new ArrayList<Cookie>();

        String username = getUsername();
        String password = getPassword();

        if (username != null && password != null)
        {
            NameValuePair[] data =
            {
                new NameValuePair("amember_login", username),
                new NameValuePair("amember_pass", password)
            };

            HttpClient client = ProxyManager.getDefault().getHttpClient();
            PostMethod method = new PostMethod(url);
            method.setRequestBody(data);

            try
            {
                int responce = client.executeMethod(method);
                if (responce != HttpStatus.SC_NOT_IMPLEMENTED)
                {
                    for (Cookie cookie : client.getState().getCookies())
                    {
                        if (usedCookies.contains(cookie.getName()))
                            list.add(cookie);
                    }
                }
            }
            catch (IOException ex)
            {
                LOG.log(Level.SEVERE, null, ex);
            }
            finally
            {
                method.releaseConnection();
            }
        }

        return list;
    }

    private boolean checkIfRegistred(String url)
    {
        Preferences p = NbPreferences.root().node("/org/chartsy/register");
        if (Boolean.parseBoolean(p.get("registred", "false")))
        {
            BufferedReader in = null;
            HttpClient client = ProxyManager.getDefault().getHttpClient();
            GetMethod method = new GetMethod(url);

            method.setFollowRedirects(true);
            List<Cookie> list = getCookies(url);
            for (Cookie cookie : list)
                client.getState().addCookie(cookie);

            try
            {
                client.executeMethod(method);
                in = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));

                if (in.readLine().equals("OK"))
                {
                    in.close();
                    method.releaseConnection();
                    return true;
                }
                else
                {
                    in.close();
                    method.releaseConnection();
                    return false;
                }
            }
            catch (IOException ex)
            {
                LOG.log(Level.SEVERE, null, ex);
                method.releaseConnection();
                try { in.close(); }
                catch (IOException io) { LOG.log(Level.SEVERE, null, io); }
                return false;
            }
        }
        else
        {
            return false;
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
        LOG.log(Level.INFO, url);
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
        }
        catch (IOException ex)
        {
            LOG.log(Level.SEVERE, null, ex);
        }

        return result;
    }

    private String getStockURL(Stock stock)
    {
        String url = STOCK_URL;

        try
        {
            url = url.replace("{0}", URLEncoder.encode(stock.getKey(), "UTF-8"));
        }
        catch (UnsupportedEncodingException ex)
        {
            LOG.log(Level.SEVERE, null, ex);
        }

        return url;
    }

    private String getDataURL(Stock stock, Interval interval)
    {
        String url = DATA_URL;

        try
        {
            url = url.replace("{0}", URLEncoder.encode(stock.getKey(), "UTF-8"));
            url = url.replace("{1}", URLEncoder.encode(interval.getTimeParam(), "UTF-8"));
        }
        catch (UnsupportedEncodingException ex)
        {
            LOG.log(Level.SEVERE, null, ex);
        }

        return url;
    }

    private String getAutocompleteURL(String word)
    {
        String url = AUTOCOMPLETE_URL;

        try
        {
            url = url.replace("{0}", URLEncoder.encode(word, "UTF-8"));
        }
        catch (UnsupportedEncodingException ex)
        {
            LOG.log(Level.SEVERE, null, ex);
        }

        return url;
    }

    private List<String> usedCookies = new ArrayList<String>();
    private static final String AUTOCOMPLETE_URL = "http://www.mrswing.com/chartsy/symbolsearch.php?q={0}";
    private static final String STOCK_URL = "http://www.mrswing.com/chartsy/companyname.php?symbol={0}";
    private static final String DATA_URL = "http://www.mrswing.com/chartsy/history.php?symbol={0}&t={1}";
    private static final String NAME = "MrSwing";
    private static final Exchange[] EXCHANGES =
    {
        new Exchange("Default")
    };

}
