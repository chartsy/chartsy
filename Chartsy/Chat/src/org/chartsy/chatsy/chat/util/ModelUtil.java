package org.chartsy.chatsy.chat.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public final class ModelUtil
{

    private ModelUtil()
	{
    }

    public static boolean areEqual(Object o1, Object o2)
	{
        if (o1 == o2)
		{
            return true;
        }
        else if (o1 == null || o2 == null)
		{
            return false;
        }
        else
		{
            return o1.equals(o2);
        }
    }

    public static boolean areBooleansEqual(Boolean b1, Boolean b2)
	{
        return (b1 == Boolean.TRUE && b2 == Boolean.TRUE) 
			|| (b1 != Boolean.TRUE && b2 != Boolean.TRUE);
    }

    public static boolean areDifferent(Object o1, Object o2)
	{
        return !areEqual(o1, o2);
    }

    public static boolean areBooleansDifferent(Boolean b1, Boolean b2)
	{
        return !areBooleansEqual(b1, b2);
    }

    public static boolean hasNonNullElement(Object[] array)
	{
        if (array != null)
		{
            final int n = array.length;
            for (int i = 0; i < n; i++)
                if (array[i] != null) 
                    return true;
        }
        return false;
    }

    public static String concat(String[] strs)
	{
        return concat(strs, " ");
    }

    public static String concat(String[] strs, String delim)
	{
        if (strs != null)
		{
            final StringBuilder buf = new StringBuilder();
            final int n = strs.length;
            for (int i = 0; i < n; i++)
			{
                final String str = strs[i];
                if (str != null)
                    buf.append(str).append(delim);
            }
            final int length = buf.length();
            if (length > 0)
                buf.setLength(length - 1);
            return buf.toString();
        }
        else
		{
            return "";
        }
    }

    public static boolean hasLength(String s)
	{
        return (s != null && s.length() > 0);
    }

    public static String nullifyIfEmpty(String s)
	{
        return ModelUtil.hasLength(s) ? s : null;
    }

    public static String nullifyingToString(Object o)
	{
        return o != null ? nullifyIfEmpty(o.toString()) : null;
    }

    public static boolean hasStringChanged(String oldString, String newString)
	{
        if (oldString == null && newString == null)
		{
            return false;
        }
        else if (oldString == null || newString == null)
		{
            return true;
        }
        else
		{
            return !oldString.equals(newString);
        }
    }

    public static String getTimeFromLong(long diff)
	{
        final String HOURS = "h";
        final String MINUTES = "min";

        final long MS_IN_A_DAY = 1000 * 60 * 60 * 24;
        final long MS_IN_AN_HOUR = 1000 * 60 * 60;
        final long MS_IN_A_MINUTE = 1000 * 60;
        long numDays = diff / MS_IN_A_DAY;
        diff = diff % MS_IN_A_DAY;
        long numHours = diff / MS_IN_AN_HOUR;
        diff = diff % MS_IN_AN_HOUR;
        long numMinutes = diff / MS_IN_A_MINUTE;

        StringBuilder buf = new StringBuilder();

        if (numDays > 0)
            buf.append(numDays).append(" d, ");

        if (numHours > 0)
            buf.append(numHours).append(" ").append(HOURS).append(", ");

        if (numMinutes > 0)
            buf.append(numMinutes).append(" ").append(MINUTES);

        String result = buf.toString();

        if (numMinutes < 1)
            result = "< 1 min";

        return result;
    }

    public static List<Object> iteratorAsList(Iterator i)
	{
        ArrayList<Object> list = new ArrayList<Object>(10);
        while (i.hasNext())
            list.add(i.next());
        return list;
    }

    public static Iterator reverseListIterator(ListIterator i)
	{
        return new ReverseListIterator(i);
    }
	
}

class ReverseListIterator<T> implements Iterator<T>
{

    private ListIterator<T> _i;

    ReverseListIterator(ListIterator<T> i)
	{
        _i = i;
        while (_i.hasNext()) _i.next();
    }

    @Override public boolean hasNext()
	{
        return _i.hasPrevious();
    }

    @Override public T next()
	{
        return _i.previous();
    }

    @Override public void remove()
	{
        _i.remove();
    }
	
}










