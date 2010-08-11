package org.chartsy.main.data;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.chartsy.main.utils.SerialVersion;

/**
 *
 * @author viorel.gheba
 */
public class StockSet extends AbstractSet<StockNode> implements Serializable, Cloneable
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;
    private ArrayList<StockNode> stocks;

    public StockSet()
    { stocks = new ArrayList<StockNode>(); }

    public StockSet(StockNode[] stocks)
    {
        this();
        for (StockNode stock : stocks)
            this.stocks.add(stock);
    }

    public StockSet(List<StockNode> stocks)
    {
        this();
        for (StockNode stock : stocks)
            this.stocks.add(stock);
    }

    public StockSet(ArrayList<StockNode> stocks)
    {
        this();
        this.stocks = stocks;
    }

	public StockNode[] stocks()
	{
		return stocks.toArray(new StockNode[stocks.size()]);
	}

    public String[] getWords()
    {
        String[] result = new String[stocks.size()];
        for (int i = 0; i < stocks.size(); i++)
            result[i] = stocks.get(i).toString();
        return result;
    }

    public @Override Iterator<StockNode> iterator()
    { return stocks.iterator(); }

    public @Override int size()
    { return stocks.size(); }

    public @Override boolean add(StockNode stock)
    {
        boolean modified;
        if (modified = !stocks.contains(stock))
        {
            stocks.add(stock);
        }
        return modified;
    }

    public @Override boolean remove(Object o)
    { 
        if (o instanceof StockNode)
        {
            StockNode stock = (StockNode) o;
            return stocks.remove(stock); 
        }
        return false;
    }

    public @Override boolean isEmpty()
    { return stocks.isEmpty(); }

    public @Override boolean contains(Object o)
    {
        if (o instanceof StockNode)
        {
            StockNode stock = (StockNode) o;
            return stocks.contains(stock);
        }
        return false;
    }

    public @Override void clear()
    { stocks.clear(); }

    @SuppressWarnings({"unchecked"})
    public @Override Object clone()
    {
        try
        {
            StockSet newSet = (StockSet) super.clone();
            newSet.stocks = (ArrayList<StockNode>) stocks.clone();
            return newSet;
        }
        catch (CloneNotSupportedException ex)
        {
            throw new InternalError();
        }
    }

}
