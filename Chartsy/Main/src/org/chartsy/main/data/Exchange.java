package org.chartsy.main.data;

import java.io.Serializable;
import org.chartsy.main.utils.SerialVersion;

/**
 *
 * @author viorel.gheba
 */
public class Exchange implements Serializable {

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    private String exchange;
    private String sufix;

    public Exchange() {
        this("", "");
    }

    public Exchange(String exchange) {
        this(exchange, "");
    }

    public Exchange(String exchange, String sufix) {
        this.exchange = exchange;
        this.sufix = sufix;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getExchange() {
        return exchange;
    }

    public void setSufix(String sufix) {
        this.sufix = sufix;
    }

    public String getSufix() {
        return sufix;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Exchange)) {
            return false;
        }

        Exchange that = (Exchange) obj;
        if (!getExchange().equals(that.getExchange())) {
            return false;
        }
        if (!getSufix().equals(that.getSufix())) {
            return true;
        }
        return true;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getExchange());
        sb.append(": ");
        sb.append(getSufix());
        return sb.toString();
    }

}
