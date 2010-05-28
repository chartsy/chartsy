package org.chartsy.main.history;

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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.chartsy.main.data.Stock;
import org.chartsy.main.intervals.Interval;
import org.chartsy.main.utils.FileUtils;

/**
 *
 * @author viorel.gheba
 */
public class History implements Serializable
{

    private static final long serialVersionUID = 2L;

    protected static final Logger LOG = Logger.getLogger(History.class.getPackage().getName());
    private HistoryItem current;
    private String backPath;
    private String fwdPath;
    private boolean initializeFlag = true;

    public History()
    {}

    public void initialize()
    {
        synchronized (this)
        {
            if (initializeFlag)
            {
                initializeFlag = false;
                backPath = FileUtils.cacheFile("BackHistory");
                fwdPath = FileUtils.cacheFile("FwdHistory");
                writeHistory(null, backPath);
                writeHistory(null, fwdPath);
            }
        }
    }

    public void addHistoryItem(HistoryItem hi) 
    {
        ArrayList<HistoryItem> list = readHistory(backPath);
        list.add(hi);
        writeHistory(list, backPath);
    }

    public boolean hasBackHistory()
    { return readHistory(backPath).size() > 0; }

    public boolean hasFwdHistory()
    { return readHistory(fwdPath).size() > 0; }

    public ArrayList<HistoryItem> getBackHistory()
    { return readHistory(backPath); }

    public ArrayList<HistoryItem> getFwdHistory()
    { return readHistory(fwdPath); }

    public HistoryItem getCurrent() 
    { return current; }

    public void setCurrent(HistoryItem hi) 
    { this.current = hi; }

    public void clearBackHistory()
    { writeHistory(null, backPath); }

    public void clearForwardHistory()
    { writeHistory(null, fwdPath); }

    public HistoryItem go(int i)
    {
        ArrayList<HistoryItem> bList = readHistory(backPath);
        ArrayList<HistoryItem> fList = readHistory(fwdPath);
        if (i > 0)
        {
            // forward
            int index = i - 1;
            ArrayList<HistoryItem> aux = new ArrayList<HistoryItem>();
            HistoryItem curr = fList.get(index);
            
            for (int j = index + 1; j < fList.size(); j++)
                aux.add(fList.get(j));

            bList.add(current);
            for (int j = 0; j < index; j++)
                bList.add(fList.get(j));

            fList = aux;

            writeHistory(bList, backPath);
            writeHistory(fList, fwdPath);

            return curr;
        } 
        else if (i < 0)
        {
            // back
            int index = bList.size() + i;
            ArrayList<HistoryItem> aux = new ArrayList<HistoryItem>();
            HistoryItem curr = bList.get(index);

            for (int j = 0; j < index; j++)
                aux.add(bList.get(j));

            fList.add(0, current);
            for (int j = bList.size() - 1; j > index; j--)
                fList.add(0, bList.get(j));

            bList = aux;

            writeHistory(bList, backPath);
            writeHistory(fList, fwdPath);

            return curr;
        }

        writeHistory(bList, backPath);
        writeHistory(fList, fwdPath);
        
        return null;
    }

    public void removeFiles()
    {
        FileUtils.removeFile(backPath);
        FileUtils.removeFile(fwdPath);
    }

    public void writeAllHistory(List<HistoryItem> back, List<HistoryItem> fwd)
    {
        writeHistory(back, backPath);
        writeHistory(fwd, fwdPath);
    }

    public void writeHistory(List<HistoryItem> list, String path)
    {
        try
        {
            OutputStream outFile = new FileOutputStream(path);
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
                        HistoryItem item = list.get(i);
                        OUT.writeObject(item.getStock());
                        OUT.writeObject(item.getInterval());
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

    public ArrayList<HistoryItem> readHistory(String path)
    {
        ArrayList<HistoryItem> list = new ArrayList<HistoryItem>();
        try
        {
            /*if (!FileUtils.fileExists(path))
            {
                FileUtils.createFile(path);
                writeHistory(null, path);
            }*/

            InputStream inFile = new FileInputStream(path);
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
                        Interval interval = (Interval) IN.readObject();
                        HistoryItem item = new HistoryItem(stock, interval);
                        list.add(item);
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

}
