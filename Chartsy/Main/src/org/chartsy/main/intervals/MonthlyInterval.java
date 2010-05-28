package org.chartsy.main.intervals;

import java.io.Serializable;
import java.util.Calendar;

/**
 *
 * @author viorel.gheba
 */
public class MonthlyInterval extends Interval implements Serializable {

    private static final long serialVersionUID = 2L;

    public MonthlyInterval() {
        super("Monthly");
    }

    public long startTime() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, -4);
        return c.getTimeInMillis();
    }

    public String getTimeParam() {
        return "m";
    }

    public boolean equals(Object obj)
    {
        if (obj == this)
            return true;

        if (!(obj instanceof MonthlyInterval))
            return false;

        MonthlyInterval that = (MonthlyInterval) obj;

        if (!getName().equals(that.getName()))
            return false;

        if (!getTimeParam().equals(that.getTimeParam()))
            return false;

        if (isIntraDay() != that.isIntraDay())
            return false;

        return true;
    }

}
