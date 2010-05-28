package org.chartsy.main.data;

import java.io.Serializable;

/**
 *
 * @author viorel.gheba
 */
public abstract class Trade implements Serializable
{
    
    private static final long serialVersionUID = 2L;

    public abstract double getPrice();
    public abstract long getSize();
    public abstract long getCurrentTimestamp();
}
