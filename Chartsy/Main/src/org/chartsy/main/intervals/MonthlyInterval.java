package org.chartsy.main.intervals;

import java.io.Serializable;
import java.util.Calendar;
import org.chartsy.main.utils.SerialVersion;

/**
 *
 * @author viorel.gheba
 */
public class MonthlyInterval extends Interval implements Serializable
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public MonthlyInterval()
    {
        super("Monthly");
		timeParam = "m";
    }

    public long startTime()
    {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, -4);
        return c.getTimeInMillis();
    }

    public String getTimeParam()
    {
        return timeParam;
    }

    public int getLengthInSeconds()
    {
        return 2628000;
    }

}
