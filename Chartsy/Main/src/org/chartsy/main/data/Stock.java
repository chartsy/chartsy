package org.chartsy.main.data;

import java.io.Serializable;

/**
 *
 * @author viorel.gheba
 */
public class Stock implements Serializable {

    private static final long serialVersionUID = 2L;

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

    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof Stock)) return false;
        Stock that = (Stock) obj;
        if (!getSymbol().equals(that.getSymbol())) return false;
        if (!getExchange().equals(that.getExchange())) return false;
        return true;
    }

    public String toString() { return symbol + "," + companyName + "," + exchange; }

}
