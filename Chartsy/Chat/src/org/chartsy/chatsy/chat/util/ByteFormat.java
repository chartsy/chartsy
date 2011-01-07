package org.chartsy.chatsy.chat.util;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

public class ByteFormat extends Format
{

	public ByteFormat()
	{
    }

    public String format(long bytes)
	{
        return super.format(bytes);
    }

    public String formatKB(long kilobytes)
	{
        return super.format(kilobytes * 1024);
    }

    public StringBuffer format(Object obj, StringBuffer buf, FieldPosition pos)
	{
        if (obj instanceof Long)
		{
            long numBytes = (Long) obj;
            if (numBytes < 1024)
			{
                DecimalFormat formatter = new DecimalFormat("#,##0");
                buf.append(formatter.format((double)numBytes)).append(" bytes");
            }
            else if (numBytes < 1024 * 1024)
			{
                DecimalFormat formatter = new DecimalFormat("#,##0.0");
                buf.append(formatter.format((double)numBytes / 1024.0)).append(" K");
            }
            else if (numBytes < 1024 * 1024 * 1024)
			{
                DecimalFormat formatter = new DecimalFormat("#,##0.0");
                buf.append(formatter.format((double)numBytes / (1024.0 * 1024.0))).append(" MB");
            }
            else
			{
                DecimalFormat formatter = new DecimalFormat("#,##0.0");
                buf.append(formatter.format((double)numBytes / (1024.0 * 1024.0 * 1024.0))).append(" GB");
            }
        }
        return buf;
    }

    public Object parseObject(String source, ParsePosition pos)
	{
        return null;
    }
	
}
