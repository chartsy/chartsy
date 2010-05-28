package org.chartsy.main.intervals;

import java.io.Serializable;
import java.util.Calendar;

/**
 *
 * @author viorel.gheba
 */
public class SixtyMinuteInterval extends Interval implements Serializable {

    private static final long serialVersionUID = 2L;

    public SixtyMinuteInterval() {
        super("60 Min", true);
    }

    public long startTime() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, -1);
        return c.getTimeInMillis();
    }

    public String getTimeParam() {
        return "60";
    }

    public boolean equals(Object obj)
    {
        if (obj == this)
            return true;

        if (!(obj instanceof SixtyMinuteInterval))
            return false;

        SixtyMinuteInterval that = (SixtyMinuteInterval) obj;

        if (!getName().equals(that.getName()))
            return false;

        if (!getTimeParam().equals(that.getTimeParam()))
            return false;

        if (isIntraDay() != that.isIntraDay())
            return false;

        return true;
    }

}
