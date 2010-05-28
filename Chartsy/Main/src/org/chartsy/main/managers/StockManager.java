package org.chartsy.main.managers;

import java.util.ArrayList;
import org.chartsy.main.data.Stock;

/**
 *
 * @author viorel.gheba
 */
public class StockManager
{

    private static StockManager instance;
    //private static Logger LOG = Logger.getLogger(StockManager.class.getPackage().getName());
    private ArrayList<Stock> stocks;
    //private String path;

    public static StockManager getDefault()
    {
        if (instance == null)
            instance = new StockManager();
        return instance;
    }

    private StockManager()
    {
        stocks = new ArrayList<Stock>();
    }

    public void addStock(Stock stock)
    {
        //readExternal();
        if (!stocks.contains(stock))
            stocks.add(stock);
        //writeExternal();
    }

    public boolean hasStock(Stock stock)
    {
        //readExternal();
        return stocks.contains(stock);
    }

    public Stock getStock(Stock stock)
    {
        //readExternal();
        for (Stock s : stocks)
            if (s.equals(stock))
                return s;
        
        return null;
    }

    /*public void writeExternal()
    {
        try
        {
            OutputStream outFile = new FileOutputStream(path);
            OutputStream outBuffer = new BufferedOutputStream(outFile);
            ObjectOutput OUT = new ObjectOutputStream(outBuffer);

            try
            {
                int size = stocks.size();
                OUT.writeInt(size);
                if (size > 0)
                {
                    for (int i = 0; i < size; i++)
                    {
                        Stock stock = stocks.get(i);
                        if (stock instanceof Serializable)
                        {
                            OUT.writeObject(stock);
                        }
                    }
                }
                stocks.clear();
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

    public void readExternal()
    {
        try
        {
            InputStream inFile = new FileInputStream(path);
            InputStream inBuffer = new BufferedInputStream(inFile);
            ObjectInput IN = new ObjectInputStream(inBuffer);

            try
            {
                int size = IN.readInt();
                if (size > 0)
                {
                    stocks.ensureCapacity(size);
                    for (int i = 0; i < size; i++)
                    {
                        Stock stock = (Stock) IN.readObject();
                        stocks.add(stock);
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
    }*/

}
