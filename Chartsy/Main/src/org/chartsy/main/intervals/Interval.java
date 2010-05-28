package org.chartsy.main.intervals;

import java.io.Serializable;
import java.util.Calendar;

/**
 *
 * @author viorel.gheba
 */
public abstract class Interval implements Serializable {

    private static final long serialVersionUID = 2L;

    protected String name = "";
    protected boolean intraDay = false;

    public Interval(String name) {
        this.name = name;
        this.intraDay = false;
    }
    
    public Interval(String name, boolean intraDay) {
        this.name = name;
        this.intraDay = intraDay;
    }

    public String getName() {
        return name;
    }

    public boolean isIntraDay() { 
        return intraDay;
    }

    public abstract long startTime();

    public abstract String getTimeParam();

    public String getMarkerString(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);

        StringBuffer sb = new StringBuffer();
        if (!isIntraDay())
        {
            int day = cal.get(Calendar.DAY_OF_MONTH);
            int month = cal.get(Calendar.MONTH) + 1;
            int year = cal.get(Calendar.YEAR);

            if (month < 10) sb.append("0");
            sb.append(Integer.toString(month));
            sb.append("/");
            
            if (day < 10) sb.append("0");
            sb.append(Integer.toString(day));
            sb.append("/");

            sb.append(Integer.toString(year));
            return sb.toString();
        }
        else
        {
            int day = cal.get(Calendar.DAY_OF_MONTH);
            int month = cal.get(Calendar.MONTH) + 1;
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);

            if (month < 10) sb.append("0");
            sb.append(Integer.toString(month));
            sb.append("/");

            if (day < 10) sb.append("0");
            sb.append(Integer.toString(day));
            sb.append(" ");

            if (hour < 10) sb.append("0");
            sb.append(Integer.toString(hour));
            sb.append(":");

            if (minute < 10) sb.append("0");
            sb.append(Integer.toString(minute));

            return sb.toString();
        }
    }

    public String toString() 
    { return name; }

}
