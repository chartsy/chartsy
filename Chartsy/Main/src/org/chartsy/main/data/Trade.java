package org.chartsy.main.data;

import java.io.Serializable;
import org.chartsy.main.utils.SerialVersion;

/**
 *
 * @author viorel.gheba
 */
public abstract class Trade implements Serializable
{
    
    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public abstract double getPrice();
    public abstract long getSize();
    public abstract long getCurrentTimestamp();
}
