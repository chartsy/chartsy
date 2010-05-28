package org.chartsy.main.intervals;

import java.io.Serializable;
import java.util.Calendar;

/**
 *
 * @author viorel.gheba
 */
public class OneMinuteInterval extends Interval implements Serializable {

    private static final long serialVersionUID = 2L;

    public OneMinuteInterval() { 
        super("1 Min", true);
    }

    public long startTime() {
        int t;
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 9);
        c.set(Calendar.MINUTE, 30);
        c.set(Calendar.SECOND, 0);

        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            t = -3;
        } else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            t = -2;
        } else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            t = -3;
        } else {
            t = -1;
        }

        c.add(Calendar.DATE, t);
        return c.getTimeInMillis();
    }

    public String getTimeParam() {
        return "1";
    }

    public boolean equals(Object obj)
    {
        if (obj == this)
            return true;

        if (!(obj instanceof OneMinuteInterval))
            return false;

        OneMinuteInterval that = (OneMinuteInterval) obj;

        if (!getName().equals(that.getName()))
            return false;

        if (!getTimeParam().equals(that.getTimeParam()))
            return false;

        if (isIntraDay() != that.isIntraDay())
            return false;

        return true;
    }

}
