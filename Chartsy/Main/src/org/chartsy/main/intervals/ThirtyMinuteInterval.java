package org.chartsy.main.intervals;

import java.io.Serializable;
import java.util.Calendar;

/**
 *
 * @author viorel.gheba
 */
public class ThirtyMinuteInterval extends Interval implements Serializable {

    private static final long serialVersionUID = 2L;

    public ThirtyMinuteInterval() {
        super("30 Min", true);
    }

    public long startTime() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -6);
        return c.getTimeInMillis();
    }

    public String getTimeParam() {
        return "30";
    }

    public boolean equals(Object obj)
    {
        if (obj == this)
            return true;

        if (!(obj instanceof ThirtyMinuteInterval))
            return false;

        ThirtyMinuteInterval that = (ThirtyMinuteInterval) obj;

        if (!getName().equals(that.getName()))
            return false;

        if (!getTimeParam().equals(that.getTimeParam()))
            return false;

        if (isIntraDay() != that.isIntraDay())
            return false;

        return true;
    }

}
