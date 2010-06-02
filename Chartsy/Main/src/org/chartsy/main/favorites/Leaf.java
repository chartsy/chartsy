package org.chartsy.main.favorites;

/**
 *
 * @author Viorel
 */
public class Leaf
{

    private String symbol = "";
    private String exchange = "";
    private Double value = new Double(0.0);
    private Double change = new Double(0.0);
    private Double percent = new Double(0.0);

    public Leaf()
    {}

    public void setSymbol(String s)
    { this.symbol = s; }

    public String getSymbol()
    { return this.symbol; }

    public void setExchange(String s)
    { this.exchange = s; }

    public String getExchange()
    { return this.exchange; }

    public void setValue(Double d)
    { this.value = d; }

    public Double getValues()
    { return this.value; }

    public void setChange(Double d)
    { this.change = d; }

    public Double getChange()
    { return this.change; }

    public void setPercent(Double d)
    { this.percent = d; }

    public Double getPercent()
    { return this.percent; }

    public String getDisplayName()
    { return symbol + exchange; }

}
