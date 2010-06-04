package org.chartsy.main.intervals;

import java.io.Serializable;
import java.util.Calendar;
import org.chartsy.main.utils.SerialVersion;

/**
 *
 * @author viorel.gheba
 */
public class FiveMinuteInterval extends Interval implements Serializable {

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public FiveMinuteInterval() { 
        super("5 Min", true);
    }

    public long startTime() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 9);
        c.set(Calendar.MINUTE, 30);
        c.set(Calendar.SECOND, 0);
        c.add(Calendar.MONTH, -1);
        return c.getTimeInMillis();
    }

    public String getTimeParam() {
        return "5";
    }

    public boolean equals(Object obj)
    {
        if (obj == this)
            return true;

        if (!(obj instanceof FiveMinuteInterval))
            return false;

        FiveMinuteInterval that = (FiveMinuteInterval) obj;

        if (!getName().equals(that.getName()))
            return false;

        if (!getTimeParam().equals(that.getTimeParam()))
            return false;

        if (isIntraDay() != that.isIntraDay())
            return false;

        return true;
    }


}
