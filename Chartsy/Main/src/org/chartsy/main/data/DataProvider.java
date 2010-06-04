package org.chartsy.main.data;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.chartsy.main.intervals.DailyInterval;
import org.chartsy.main.intervals.FifteenMinuteInterval;
import org.chartsy.main.intervals.FiveMinuteInterval;
import org.chartsy.main.intervals.Interval;
import org.chartsy.main.intervals.MonthlyInterval;
import org.chartsy.main.intervals.OneMinuteInterval;
import org.chartsy.main.intervals.SixtyMinuteInterval;
import org.chartsy.main.intervals.ThirtyMinuteInterval;
import org.chartsy.main.intervals.WeeklyInterval;
import org.chartsy.main.utils.FileUtils;
import org.chartsy.main.utils.SerialVersion;

/**
 *
 * @author viorel.gheba
 */
public abstract class DataProvider implements Serializable
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    protected static final Logger LOG = Logger.getLogger(DataProvider.class.getPackage().getName());

    public static final Interval ONE_MINUTE = new OneMinuteInterval();
    public static final Interval FIVE_MINUTE = new FiveMinuteInterval();
    public static final Interval FIFTEEN_MINUTE = new FifteenMinuteInterval();
    public static final Interval THIRTY_MINUTE = new ThirtyMinuteInterval();
    public static final Interval SIXTY_MINUTE = new SixtyMinuteInterval();
    
    public static final Interval DAILY = new DailyInterval();
    public static final Interval WEEKLY = new WeeklyInterval();
    public static final Interval MONTHLY = new MonthlyInterval();

    public static final Interval[] INTRA_DAY_INTERVALS = {ONE_MINUTE, FIVE_MINUTE, FIFTEEN_MINUTE, THIRTY_MINUTE, SIXTY_MINUTE};
    public static final Interval[] INTERVALS = {DAILY, WEEKLY, MONTHLY};

    protected String name;
    protected Exchange[] exchanges;
    protected boolean supportsIntraDay = false;

    protected String stocksPath;
    protected String datasetsPath;
    protected boolean initializeFlag = true;

    public DataProvider(String name, Exchange[] exchanges)
    {
        this(name, exchanges, false);
    }

    public DataProvider(String name, Exchange[] exchanges, boolean supportsIntraday)
    {
        this.name = name;
        this.supportsIntraDay = supportsIntraday;
        this.exchanges = exchanges;
    }

    public void initialize()
    {
        synchronized (this)
        {
            if (initializeFlag)
            {
                initializeFlag = false;
                stocksPath = FileUtils.cacheFile(name + "Stocks");
                datasetsPath = FileUtils.cacheFile(name + "Datasets");
                writeStocks(null);
                writeDatasets(null);
            }
        }
    }

    public String getName() 
    { return this.name; }

    public Exchange[] getExchanges() 
    { return this.exchanges; }

    public Interval[] getIntraDayIntervals() 
    { return INTRA_DAY_INTERVALS; }

    public Interval[] getIntervals() 
    { return INTERVALS; }

    public boolean supportsIntraday() 
    { return supportsIntraDay; }

    public abstract Stock getStock(String symbol, String exchange);

    public abstract Dataset getData(Stock stock, Interval interval);
    
    public abstract Dataset getLastDataItem(Stock stock, Interval interval, Dataset dataset);

    public abstract StockSet getAutocomplete(String text);

    public String getKey(String symbol, Interval interval)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(symbol);
        sb.append("-");
        sb.append(interval.getTimeParam());
        return sb.toString();
    }
    public String getKey(Stock stock, Interval interval)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(stock.getKey());
        sb.append("-");
        sb.append(interval.getTimeParam());
        return sb.toString();
    }

    public void addDataset(String key, Dataset value) 
    {
        LinkedHashMap<String, Dataset> map = readDatasets();
        map.put(key, value);
        writeDatasets(map);
    }

    public void removeDataset(String key) 
    { 
        LinkedHashMap<String, Dataset> map = readDatasets();
        map.remove(key);
        writeDatasets(map);
    }

    public void removeAllDatasets() 
    {
        writeDatasets(null);
    }

    public Dataset getDataset(Stock stock, Interval interval) 
    {
        LinkedHashMap<String, Dataset> map = readDatasets();
        return map.get(getKey(stock, interval));
    }

    public Dataset getDataset(String key) 
    {
        LinkedHashMap<String, Dataset> map = readDatasets();
        return map.get(key);
    }

    public boolean datasetExists(String key) 
    {
        LinkedHashMap<String, Dataset> map = readDatasets();
        return (map.get(key) != null);
    }
    
    public boolean datasetExists(Stock stock) 
    {
        LinkedHashMap<String, Dataset> map = readDatasets();
        return (map.get(getKey(stock, DAILY)) != null);
    }
    
    public boolean datasetExists(Stock stock, Interval interval) 
    {
        LinkedHashMap<String, Dataset> map = readDatasets();
        return (map.get(getKey(stock, interval)) != null);
    }

    public void addStock(Stock stock)
    {
        ArrayList<Stock> list = readStocks();
        if (!list.contains(stock))
            list.add(stock);
        writeStocks(list);
    }

    public boolean hasStock(Stock stock)
    {
        ArrayList<Stock> list = readStocks();
        return list.contains(stock);
    }

    public Stock getStock(Stock stock)
    {
        ArrayList<Stock> list = readStocks();
        for (Stock s : list)
            if (s.equals(stock))
                return s;

        return null;
    }

    public void removeFiles()
    {
        FileUtils.removeFile(stocksPath);
        FileUtils.removeFile(datasetsPath);
    }

    public void writeStocks(ArrayList<Stock> list)
    {
        try
        {
            OutputStream outFile = new FileOutputStream(stocksPath);
            OutputStream outBuffer = new BufferedOutputStream(outFile);
            ObjectOutput OUT = new ObjectOutputStream(outBuffer);

            try
            {
                if (list == null)
                {
                    OUT.writeInt(0);
                    return;
                }

                int size = list.size();
                OUT.writeInt(size);
                if (size > 0)
                {
                    for (int i = 0; i < size; i++)
                    {
                        Stock stock = list.get(i);
                        OUT.writeObject(stock);
                    }
                }
            }
            finally
            {
                OUT.close();
            }
        }
        catch (IOException ex)
        {
            LOG.log(Level.SEVERE, "Cannot perform output.", ex);
        }
    }

    public ArrayList<Stock> readStocks()
    {
        ArrayList<Stock> list = new ArrayList<Stock>();
        try
        {
            if (!FileUtils.fileExists(stocksPath))
            {
                FileUtils.createFile(stocksPath);
                writeStocks(null);
            }

            InputStream inFile = new FileInputStream(stocksPath);
            InputStream inBuffer = new BufferedInputStream(inFile);
            ObjectInput IN = new ObjectInputStream(inBuffer);

            try
            {
                int size = IN.readInt();
                if (size > 0)
                {
                    for (int i = 0; i < size; i++)
                    {
                        Stock stock = (Stock) IN.readObject();
                        list.add(stock);
                    }
                }
            }
            finally
            {
                IN.close();
            }
        }
        catch(ClassNotFoundException ex)
        {
            LOG.log(Level.SEVERE, "Cannot perform input. Class not found.", ex);
        }
        catch(IOException ex)
        {
            LOG.log(Level.SEVERE, "Cannot perform input.", ex);
        }
        return list;
    }

    public void writeDatasets(LinkedHashMap<String, Dataset> map)
    {
        try
        {
            OutputStream outFile = new FileOutputStream(datasetsPath);
            OutputStream outBuffer = new BufferedOutputStream(outFile);
            ObjectOutput OUT = new ObjectOutputStream(outBuffer);

            try
            {
                if (map == null)
                {
                    OUT.writeInt(0);
                    return;
                }

                int size = map.size();
                OUT.writeInt(size);

                if (size > 0)
                {
                    ArrayList<String> keys = new ArrayList<String>(map.keySet());
                    ArrayList<Dataset> values = new ArrayList<Dataset>(map.values());
                    
                    for (int i = 0; i < size; i++)
                    {
                        OUT.writeObject(keys.get(i));
                        OUT.writeObject(values.get(i));
                    }
                }
            }
            finally
            {
                OUT.close();
            }
        }
        catch (IOException ex)
        {
            LOG.log(Level.SEVERE, "Cannot perform output.", ex);
        }
    }

    public LinkedHashMap<String, Dataset> readDatasets()
    {
        LinkedHashMap<String, Dataset> map = new LinkedHashMap<String, Dataset>();
        try
        {
            if (!FileUtils.fileExists(datasetsPath))
            {
                FileUtils.createFile(datasetsPath);
                writeDatasets(null);
            }

            InputStream inFile = new FileInputStream(datasetsPath);
            InputStream inBuffer = new BufferedInputStream(inFile);
            ObjectInput IN = new ObjectInputStream(inBuffer);

            try
            {
                int size = IN.readInt();
                if (size > 0)
                {
                    for (int i = 0; i < size; i++)
                    {
                        String key = (String) IN.readObject();
                        Dataset value = (Dataset) IN.readObject();
                        map.put(key, value);
                    }
                }
            }
            finally
            {
                IN.close();
            }
        }
        catch(ClassNotFoundException ex)
        {
            LOG.log(Level.SEVERE, "Cannot perform input. Class not found.", ex);
        }
        catch(IOException ex)
        {
            LOG.log(Level.SEVERE, "Cannot perform input.", ex);
        }
        return map;
    }

}
