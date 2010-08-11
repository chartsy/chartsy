package org.chartsy.main.data;

import java.io.Serializable;
import org.chartsy.main.utils.SerialVersion;

/**
 *
 * @author viorel.gheba
 */
final public class Stock implements Serializable {

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    private String symbol = "";
    private String exchange = "";
    private String companyName = "";

    public Stock() {}

    public Stock(String symbol) {
        this(symbol, "");
    }

    public Stock(String symbol, String exchange) {
        setSymbol(symbol);
        setExchange(exchange);
    }

    public void setSymbol(String symbol) {
        if (symbol != null) symbol = symbol.toUpperCase();
        else symbol = "";
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getExchange() {
        return exchange;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyName() {
        return companyName;
    }

	public boolean hasCompanyName()
	{
		return companyName.hashCode() != "".hashCode();
	}

    public boolean isIndex() {
        return symbol.startsWith("$");
    }

    public boolean isFuture() {
        return symbol.endsWith(".F") || symbol.endsWith(".FD");
    }

    public String getSymbolRoot() {
        if (isFuture()) return symbol.substring(0, symbol.length() - 2);
        else return symbol;
    }

    public String getKey() {
        return symbol + exchange;
    }

    public @Override boolean equals(Object obj)
	{
        if (obj == this) 
			return true;

        if (!(obj instanceof Stock)) 
			return false;

        Stock that = (Stock) obj;

        if (!getSymbol().equals(that.getSymbol()))
			return false;

		if (!getExchange().equals(that.getExchange()))
			return false;

		if (!getCompanyName().equals(that.getCompanyName()))
			return false;

        return true;
    }

	public @Override int hashCode()
	{
		int hash = 7;
		hash = 47 * hash + (this.symbol != null ? this.symbol.hashCode() : 0);
		hash = 47 * hash + (this.exchange != null ? this.exchange.hashCode() : 0);
		//hash = 47 * hash + (this.companyName != null ? this.companyName.hashCode() : 0);
		return hash;
	}

    public @Override String toString()
	{
		return symbol + "," + companyName + "," + exchange;
	}

}
