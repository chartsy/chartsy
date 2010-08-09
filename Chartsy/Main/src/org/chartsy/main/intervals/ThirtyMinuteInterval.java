package org.chartsy.main.intervals;

import java.io.Serializable;
import java.util.Calendar;
import org.chartsy.main.utils.SerialVersion;

/**
 *
 * @author viorel.gheba
 */
public class ThirtyMinuteInterval extends Interval implements Serializable
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public ThirtyMinuteInterval()
    {
        super("30 Min", true);
		timeParam = "30";
    }

    public long startTime()
    {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -6);
        return c.getTimeInMillis();
    }

    public String getTimeParam()
    {
        return timeParam;
    }

    public int getLengthInSeconds()
    {
        return 1800;
    }

}
