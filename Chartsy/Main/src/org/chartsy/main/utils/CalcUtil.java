package org.chartsy.main.utils;

import java.util.GregorianCalendar;
import org.chartsy.main.data.Dataset;

/**
 *
 * @author viorel.gheba
 */
public class CalcUtil {

    private CalcUtil() {}

    public static double getMin(double[] list) {
        double result = Double.MAX_VALUE;
        for (int i = 0; i < list.length; i++)
            if (result > list[i])
                result = list[i];
        return result;
    }

    public static double getMinNotZero(double[] list) {
        double result = Double.MAX_VALUE;
        for (int i = 0; i < list.length; i++)
            if (result > list[i] && list[i] != 0)
                result = list[i];
        return result;
    }

    public static double getMax(double[] list) {
        double result = Double.MIN_VALUE;
        for (int i = 0; i < list.length; i++)
            if (result < list[i])
                result = list[i];
        return result;
    }

    public static double getMaxNotZero(double[] list) {
        double result = Double.MIN_VALUE;
        for (int i = 0; i < list.length; i++)
            if (result < list[i] && list[i] != 0)
                result = list[i];
        return result;
    }

    public static double sum(double[] list) {
        double result = 0;
        for (int i = 0; i < list.length; i++)
            result += list[i];
        return result;
    }

    public static double sum(double[] list, int start, int end) {
        double result = 0;
        if (start < 0) start = 0;
        if (end > list.length) end = list.length;
        for (int i = start; i < end; i++)
            result += list[i];
        return result;
    }

    public static double stdDev(double[] list, int curr, int per) {
        double med = 0;
        double sum = 0;

        for (int i = curr; i > curr - per && i > 0; i--) {
            double val = list[i];
            med += val;
            sum += val * val;
        }

        med = med / per;
        sum = sum / per;

        double result = Math.sqrt(sum - med * med);
        return result;
    }

    public static double stdDev(Dataset dataset, String price, int curr, int per)
    {
        return stdDev(dataset, Dataset.getPrice(price), curr, per);
    }

    public static double stdDev(Dataset dataset, int price, int curr, int per)
    {
        double med = 0;
        double sum = 0;

        for (int i = curr; i > curr - per && i > 0; i--) {
            if (dataset.getPriceAt(i, price) != 0) {
                double val = dataset.getPriceAt(i, price);
                med += val;
                sum += val * val;
            }
        }

        med = med / per;
        sum = sum / per;

        double result = Math.sqrt(sum - med * med);
        return result;
    }

	public static boolean isFirstWorkingDayOfMonth(long time)
	{
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setFirstDayOfWeek(GregorianCalendar.MONDAY);
		calendar.setTimeInMillis(time);
		int week = calendar.get(GregorianCalendar.WEEK_OF_MONTH);
		if (week == 1)
		{
			int day = calendar.get(GregorianCalendar.DAY_OF_WEEK);
			calendar.set(GregorianCalendar.DAY_OF_MONTH, 1);
			int first_day = calendar.get(GregorianCalendar.DAY_OF_WEEK);
			while (first_day != GregorianCalendar.SATURDAY && first_day != GregorianCalendar.SUNDAY)
			{
				calendar.add(GregorianCalendar.DAY_OF_MONTH, 1);
				first_day = calendar.get(GregorianCalendar.DAY_OF_WEEK);
			}
			return day == first_day;
		}
		return false;
	}

	public static boolean isFirstWorkingDayOfYear(long time)
	{
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setFirstDayOfWeek(GregorianCalendar.MONDAY);
		calendar.setTimeInMillis(time);
		int week = calendar.get(GregorianCalendar.WEEK_OF_YEAR);
		if (week == 1)
		{
			int day = calendar.get(GregorianCalendar.DAY_OF_WEEK);
			calendar.set(GregorianCalendar.DAY_OF_MONTH, 1);
			int first_day = calendar.get(GregorianCalendar.DAY_OF_WEEK);
			while (first_day != GregorianCalendar.SATURDAY && first_day != GregorianCalendar.SUNDAY)
			{
				calendar.add(GregorianCalendar.DAY_OF_MONTH, 1);
				first_day = calendar.get(GregorianCalendar.DAY_OF_WEEK);
			}
			return day == first_day;
		}
		return false;
	}

}
