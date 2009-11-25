package org.chartsy.main.utils;

import java.io.Serializable;

/**
 *
 * @author viorel.gheba
 */
public class Stock extends Object implements Serializable {

    private static final long serialVersionUID = 101L;

    private String symbol;
    private String exchange;
    private String companyName;

    public Stock(String symbol) { this(symbol, ""); }
    public Stock(String symbol, String exchange) { this(symbol, exchange, ""); }
    public Stock(String symbol, String exchange, String companyName) {
        this.symbol = symbol;
        this.exchange = exchange;
        this.companyName = companyName;
    }

    public void setSymbol(String symbol) {
        if (symbol != null) {
            symbol = symbol.toUpperCase();
            this.symbol = symbol;
        }
    }
    public String getSymbol() { return symbol; }

    public void setExchange(String exchange) { this.exchange = exchange; }
    public String getExchange() { return exchange; }

    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getCompanyName() { return companyName; }

    public boolean isIndex() { return symbol.startsWith("$"); }
    public boolean isFuture() { return symbol.endsWith(".F") || symbol.endsWith(".FD"); }

    public String getRoot() { return isFuture() ? symbol.substring(0, symbol.length()-2) : symbol; }
    public String getKey() { return symbol + exchange; }

    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof Stock)) return false;
        Stock that = (Stock) obj;
        if (!getSymbol().equals(that.getSymbol())) return false;
        if (!getExchange().equals(that.getExchange())) return false;
        if (!getCompanyName().equals(that.getCompanyName())) return false;
        return true;
    }
    public String toString() { return "[" + symbol + exchange +", " + companyName + "]"; }

}
