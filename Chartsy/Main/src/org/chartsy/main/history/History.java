package org.chartsy.main.history;

import java.io.Serializable;
import java.util.Stack;
import java.util.logging.Logger;
import org.chartsy.main.utils.SerialVersion;

/**
 *
 * @author viorel.gheba
 */
public class History implements Serializable
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;
    protected static final Logger LOG = Logger.getLogger(History.class.getPackage().getName());

    private HistoryItem current;
    transient private Stack<HistoryItem> backStack;
    transient private Stack<HistoryItem> fwdStack;

    public History()
    {
        bk();
        fw();
    }

    private Stack<HistoryItem> bk()
    {
        if (backStack == null)
        {
            backStack = new Stack<HistoryItem>();
        }
        return backStack;
    }

    private Stack<HistoryItem> fw()
    {
        if (fwdStack == null)
        {
            fwdStack = new Stack<HistoryItem>();
        }
        return fwdStack;
    }

    public void initialize()
    {
        bk();
        fw();
    }

    public void addHistoryItem(HistoryItem hi)
    {
        bk().push(hi);
    }

    public boolean hasBackHistory()
    {
        return !bk().empty();
    }

    public boolean hasFwdHistory()
    {
        return !fw().empty();
    }

    public HistoryItem[] getBackHistoryList()
    {
        return bk().toArray(new HistoryItem[bk().size()]);
    }

    public void setBackHistoryList(HistoryItem[] items)
    {
        bk().clear();
        for (HistoryItem item : items)
        {
            bk().push(item);
        }
    }

    public HistoryItem[] getFwdHistoryList()
    {
        return fw().toArray(new HistoryItem[fw().size()]);
    }

    public void setFwdHistoryList(HistoryItem[] items)
    {
        fw().clear();
        for (HistoryItem item : items)
        {
            fw().push(item);
        }
    }

    public HistoryItem getCurrent()
    {
        return current;
    }

    public void setCurrent(HistoryItem hi)
    {
        this.current = hi;
    }

    public void clearBackHistory()
    {
        bk().clear();
    }

    public void clearForwardHistory()
    {
        fw().clear();
    }

    public HistoryItem go(int steps)
    {
        HistoryItem result = null;

        if (steps > 0)
        {
            // forward
            int size = fw().size();
            if (size == 0)
            {
                return null;
            }

            if (size < steps)
            {
                return null;
            }

            if (current != null)
            {
                bk().push(current);
            }

            for (int i = 0; i < steps - 1; i++)
            {
                bk().push(fw().pop());
            }

            result = fw().pop();
        } else if (steps < 0)
        {
            // back
            steps = Math.abs(steps);
            int size = bk().size();
            if (size == 0)
            {
                return null;
            }

            if (size < steps)
            {
                return null;
            }

            if (current != null)
            {
                fw().push(current);
            }

            for (int i = 0; i < steps - 1; i++)
            {
                fw().push(bk().pop());
            }

            result = bk().pop();
        }

        return result;
    }
}
