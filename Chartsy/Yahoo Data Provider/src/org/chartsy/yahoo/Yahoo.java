package org.chartsy.yahoo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Vector;
import org.chartsy.main.dataset.DataItem;
import org.chartsy.main.dataset.Dataset;
import org.chartsy.main.managers.DatasetManager;
import org.chartsy.main.managers.LoggerManager;
import org.chartsy.main.updater.AbstractUpdater;
import org.chartsy.main.utils.DateCompare;
import org.chartsy.main.utils.FileUtils;
import org.chartsy.main.utils.Stock;
import org.chartsy.main.utils.URLChecker;
import org.chartsy.main.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author viorel.gheba
 */
public class Yahoo extends AbstractUpdater implements Serializable {

    private static final long serialVersionUID = 101L;

    public Yahoo() { super(UpdaterKeys.UPDATER_NAME, UpdaterKeys.EXCHANGES, UpdaterKeys.SUFIXES, UpdaterKeys.TIMES); }

    public Stock getStock(String symbol, String exchange) {
        Stock stock = null;
        try {
            URL yahoo = new URL("http://finance.yahoo.com/q/pr?s=" + symbol);
            if (URLChecker.checkURL(yahoo)) {
                URLConnection yc = yahoo.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.contains("<td width=\"270\" class=\"yfnc_modtitlew2\"><b>")) {
                        String companyName = inputLine.split("<td width=\"270\" class=\"yfnc_modtitlew2\"><b>")[1].split("</b>")[0];
                        stock = new Stock(symbol, exchange, companyName);
                    }
                }
                in.close();
            }
        } catch (Exception ex) {
            LoggerManager.getDefault().log("Bad URL.", ex);
        }
        return stock;
    }

    public Dataset update(String symbol, String time) {
        Vector<DataItem> items = new Vector<DataItem>();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        DateCompare compare = new DateCompare();
        BufferedReader in = null;
        try {
            String strURL = getURL(symbol, time);
            URL url = new URL(strURL);
            if (URLChecker.checkURL(url)) {
                in = new BufferedReader(new InputStreamReader(url.openStream()));
                if (in != null) {
                    String inputLine;
                    in.readLine();
                    while ((inputLine = in.readLine()) != null) {
                        StringTokenizer st = new StringTokenizer(inputLine, ",");
                        Date date = df.parse(st.nextToken());
                        Double open = Double.parseDouble(st.nextToken());
                        Double high = Double.parseDouble(st.nextToken());
                        Double low = Double.parseDouble(st.nextToken());
                        Double close = Double.parseDouble(st.nextToken());
                        Double volume = Double.parseDouble(st.nextToken());
                        Double adj = Double.parseDouble(st.nextToken());
                        DataItem item = new DataItem(date, open, close, high, low, volume, adj);
                        items.add(item);
                    }
                    Collections.sort(items, compare);
                    DataItem[] data = items.toArray(new DataItem[items.size()]);
                    Dataset dataset = new Dataset(data);
                    return dataset;
                }
            }
        } catch (Exception ex) {
            LoggerManager.getDefault().log(ex);
        }
        return null;
    }

    public Dataset updateLastValues(String symbol, String time, Dataset dataset) {
        DataItem[] data = dataset.getData();
        DataItem last = data[data.length-1];
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        BufferedReader in = null;
        try {
            String strURL = "http://download.finance.yahoo.com/d/quotes.csv?s=" + symbol + "&f=sl1d1t1c1ohgv&e=.csv";
            System.out.println(strURL);
            URL url = new URL(strURL);
            if (URLChecker.checkURL(url)) {
                in = new BufferedReader(new InputStreamReader(url.openStream()));
                if (in != null) {
                    String inputLine;
                    //in.readLine();
                    while ((inputLine = in.readLine()) != null) {
                        StringTokenizer st = new StringTokenizer(inputLine, ",");
                        String sym = st.nextToken();
                        System.out.println(sym);
                        Double close = Double.parseDouble(st.nextToken());
                        String d = st.nextToken(); d = d.substring(1, d.length()-1);
                        Date date = df.parse(d);
                        String t = st.nextToken();
                        Double adj = Double.parseDouble(st.nextToken());
                        t = st.nextToken();
                        if (!t.equals("N/A")) {
                            Double open = Double.parseDouble(t);
                            Double high = Double.parseDouble(st.nextToken());
                            Double low = Double.parseDouble(st.nextToken());
                            Double volume = Double.parseDouble(st.nextToken());
                            DataItem item = new DataItem(date, open, close, high, low, volume, adj);
                            int c = compare(last.getDate(), item.getDate());
                            switch (c) {
                                case 0:
                                    data[data.length-1] = item;
                                    break;
                                case 1:
                                    DataItem[] list = new DataItem[data.length+1];
                                    for (int i = 0; i < data.length; i++) list[i] = data[i];
                                    list[data.length] = item;
                                    data = list;
                                    break;
                            }
                            return new Dataset(data);
                        }
                    }
                } else {
                    System.out.println("in is null");
                }
            }
        } catch (Exception ex) {
            LoggerManager.getDefault().log(ex);
        }
        return null;
    }
    public Dataset updateIntraDay(String symbol, String time) { return null; }
    //public DataItem updateLastIntraDayItem(String symbol, String time) { return null; }
    public Dataset updateIntraDayLastValues(String symbol, String time, Dataset dataset) { return null; }

    private String getURL(final String symbol, final String time) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(new Date());

        Calendar c2 = Calendar.getInstance();
        c2.setTime(new Date());
        c2.add(Calendar.YEAR, -4);

        String g = "";
        if (time.equals(DatasetManager.DAILY)) g = "d";
        else if (time.equals(DatasetManager.WEEKLY)) g = "w";
        else if (time.equals(DatasetManager.MONTHLY)) g = "m";

        String strURL = "http://ichart.finance.yahoo.com/table.csv" +
                    "?s=" + symbol +
                    ((c2.get(Calendar.MONTH) < 10) ? "&a=0" + c2.get(Calendar.MONTH) : "&a=" + c2.get(Calendar.MONTH)) +
                    "&b=" + c2.get(Calendar.DAY_OF_MONTH) +
                    "&c=" + c2.get(Calendar.YEAR) +
                    ((c1.get(Calendar.MONTH) < 10) ? "&d=0" + c1.get(Calendar.MONTH) : "&d=" + c1.get(Calendar.MONTH)) +
                    "&e=" + c1.get(Calendar.DAY_OF_MONTH) +
                    "&f=" + c1.get(Calendar.YEAR) +
                    "&g=" + g +
                    "&ignore=.csv";

        return strURL;
    }

    private int compare(Date d1, Date d2) {
        if (d1.getTime() < d2.getTime()) return 1;
        if (d1.getTime() == d2.getTime()) return 0;
        return -1;
    }

    protected void getTimezone() {
        Date date = new Date();
        String TimeZoneIds[] = TimeZone.getAvailableIDs();
        for(int i = 0; i < TimeZoneIds.length; i++) {
            TimeZone tz = TimeZone.getTimeZone(TimeZoneIds[i]);
            String tzName = tz.getDisplayName(tz.inDaylightTime(date), TimeZone.LONG);
            System.out.print(TimeZoneIds[i] + ":   ");
            int rawOffset = tz.getRawOffset();
            int hour = rawOffset / (60*60*1000);
            int minute = Math.abs(rawOffset / (60*1000)) % 60;
            System.out.println(tzName + " " + hour + ":" + minute);
        }
    }

    protected void initializeUserData() {
        String path = FileUtils.UserFile();
        Document document = XMLUtils.loadXMLDocument(path);
        if (document != null) {
            Element root = XMLUtils.getRoot(document);
            if (root != null) {
                NodeList nodeList = root.getElementsByTagName("yahoo");
                if (nodeList.getLength() == 0) {
                    Element parent = document.createElement("yahoo");
                    root.appendChild(parent);
                    XMLUtils.saveXMLDocument(document, path);
                }
            }
        }
    }

}
