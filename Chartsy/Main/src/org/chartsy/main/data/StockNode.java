package org.chartsy.main.data;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

/**
 *
 * @author viorel.gheba
 */
public class StockNode extends AbstractNode
{

    private String symbol = "";
    private String companyName = "";
    private String exchange = "";

    public StockNode(String symbol, String companyName, String exchange)
    {
        super(Children.LEAF);
        this.symbol = symbol;
        this.companyName = companyName;
        this.exchange = exchange;
        setDisplayName(symbol);
    }

    public String getSymbol()
    { return symbol; }

    public String getCompanyName()
    { return companyName; }

    public String getExchange()
    { return exchange; }

	public String getKey()
	{ return symbol + exchange; }

    public String getLeft()
    { return "<font color='#4e9a06'>" + companyName + "</font>"; }

    public String getRight()
    { return "<font color='#000000'><b>" + symbol + "</b></font> <font color='#aaaaaa'><i>(" + exchange + ")</i></font>"; }

    public @Override String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("<html>");
        sb.append(getRight());
        sb.append(" - ");
        sb.append(getLeft());
        sb.append("    </html>");
        return sb.toString();
    }

}
