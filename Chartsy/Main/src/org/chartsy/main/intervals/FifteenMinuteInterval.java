package org.chartsy.main.intervals;

import java.io.Serializable;
import java.util.Calendar;

/**
 *
 * @author viorel.gheba
 */
public class FifteenMinuteInterval extends Interval implements Serializable {

    private static final long serialVersionUID = 2L;

    public FifteenMinuteInterval() {
        super("15 Min", true);
    }

    public long startTime() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MONTH, -3);
        return c.getTimeInMillis();
    }

    public String getTimeParam() {
        return "15";
    }

    public boolean equals(Object obj)
    {
        if (obj == this)
            return true;

        if (!(obj instanceof FifteenMinuteInterval))
            return false;

        FifteenMinuteInterval that = (FifteenMinuteInterval) obj;

        if (!getName().equals(that.getName()))
            return false;

        if (!getTimeParam().equals(that.getTimeParam()))
            return false;

        if (isIntraDay() != that.isIntraDay())
            return false;

        return true;
    }

}
