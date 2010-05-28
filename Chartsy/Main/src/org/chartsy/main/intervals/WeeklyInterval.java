package org.chartsy.main.intervals;

import java.io.Serializable;
import java.util.Calendar;

/**
 *
 * @author viorel.gheba
 */
public class WeeklyInterval extends Interval implements Serializable {

    private static final long serialVersionUID = 2L;

    public WeeklyInterval() {
        super("Weekly");
    }

    public long startTime() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, -4);
        return c.getTimeInMillis();
    }

    public String getTimeParam() {
        return "w";
    }

    public boolean equals(Object obj)
    {
        if (obj == this)
            return true;

        if (!(obj instanceof WeeklyInterval))
            return false;

        WeeklyInterval that = (WeeklyInterval) obj;

        if (!getName().equals(that.getName()))
            return false;

        if (!getTimeParam().equals(that.getTimeParam()))
            return false;

        if (isIntraDay() != that.isIntraDay())
            return false;

        return true;
    }

}
