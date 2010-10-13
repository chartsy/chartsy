package org.chartsy.main.intervals;

import java.io.Serializable;
import java.util.Calendar;
import org.chartsy.main.utils.SerialVersion;

/**
 *
 * @author viorel.gheba
 */
public class OneMinuteInterval extends Interval implements Serializable
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public OneMinuteInterval()
    {
        super("1 Min", true);
        this.timeParam = "1";
    }

    @Override
    public long startTime()
    {
        int t;
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 9);
        c.set(Calendar.MINUTE, 30);
        c.set(Calendar.SECOND, 0);

        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)
        {
            t = -3;
        } else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
        {
            t = -2;
        } else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
        {
            t = -3;
        } else
        {
            t = -1;
        }

        c.add(Calendar.DATE, t);
        return c.getTimeInMillis();
    }

    @Override
    public String getTimeParam()
    {
        return this.timeParam;
    }

    @Override
    public int getLengthInSeconds()
    {
        return 60;
    }
}
