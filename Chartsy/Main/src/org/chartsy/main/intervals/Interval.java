package org.chartsy.main.intervals;

import java.io.Serializable;
import java.util.Calendar;
import org.chartsy.main.utils.SerialVersion;

/**
 *
 * @author viorel.gheba
 */
public abstract class Interval implements Serializable
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;
    protected String name = "";
    protected String timeParam = "";
    protected boolean intraDay = false;

    public Interval(String name)
    {
        this.name = name;
        this.intraDay = false;
    }

    public Interval(String name, boolean intraDay)
    {
        this.name = name;
        this.intraDay = intraDay;
    }

    public String getName()
    {
        return name;
    }

    public boolean isIntraDay()
    {
        return intraDay;
    }

    public abstract long startTime();

    public abstract String getTimeParam();

    public abstract int getLengthInSeconds();

    public String getMarkerString(long time)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);

        StringBuilder sb = new StringBuilder();
        if (!isIntraDay())
        {
            int day = cal.get(Calendar.DAY_OF_MONTH);
            int month = cal.get(Calendar.MONTH) + 1;
            int year = cal.get(Calendar.YEAR);

            if (month < 10)
            {
                sb.append("0");
            }
            sb.append(Integer.toString(month));
            sb.append("/");

            if (day < 10)
            {
                sb.append("0");
            }
            sb.append(Integer.toString(day));
            sb.append("/");

            sb.append(Integer.toString(year));
            return sb.toString();
        } else
        {
            int day = cal.get(Calendar.DAY_OF_MONTH);
            int month = cal.get(Calendar.MONTH) + 1;
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);
			int seconds = cal.get(Calendar.SECOND);

            if (month < 10)
            {
                sb.append("0");
            }
            sb.append(Integer.toString(month));
            sb.append("/");

            if (day < 10)
            {
                sb.append("0");
            }
            sb.append(Integer.toString(day));
            sb.append(" ");

            if (hour < 10)
            {
                sb.append("0");
            }
            sb.append(Integer.toString(hour));
            sb.append(":");

            if (minute < 10)
            {
                sb.append("0");
            }
            sb.append(Integer.toString(minute));

			if (getLengthInSeconds() < 60)
			{
				sb.append(":");
				if (seconds < 10)
					sb.append("0");
				sb.append(Integer.toString(seconds));
			}

            return sb.toString();
        }
    }

    public 
    @Override
    String toString()
    {
        return name;
    }

    public
    @Override
    boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }

        if (!(obj instanceof Interval))
        {
            return false;
        }
        Interval that = (Interval) obj;

        if (!that.getName().equals(getName()))
        {
            return false;
        }

        if (!that.getTimeParam().equals(getTimeParam()))
        {
            return false;
        }

        return true;
    }

    public
    @Override
    int hashCode()
    {
        int hash = 5;
        hash = 13 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 13 * hash + (this.timeParam != null ? this.timeParam.hashCode() : 0);
        hash = 13 * hash + (this.intraDay ? 1 : 0);
        return hash;
    }
	
}
