package org.chartsy.main.data;

import java.io.Serializable;
import java.util.Arrays;
import org.chartsy.main.utils.SerialVersion;

/**
 *
 * @author Viorel
 */
public class OIDataItem implements Serializable, Comparable<OIDataItem>
{

	private static final long serialVersionUID = SerialVersion.APPVERSION;

	private long time;
	private double[] data;

	public OIDataItem(long time, double... values)
	{
		this.time = time;
		this.data = new double[values.length];
		System.arraycopy(values, 0, data, 0, values.length);
	}

	public long getTime()
	{
		return time;
	}

	public void setTime(long time)
	{
		this.time = time;
	}

	public double[] getValues()
	{
		return data;
	}

	public double getValue(int index)
	{
		if (index < 0 || index >= data.length)
			return 0;
		return data[index];
	}

	public void setValue(int index, double value)
	{
		if (index < 0 || index >= data.length)
			return;
		data[index] = value;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
            return true;

        if (!(obj instanceof OIDataItem))
            return false;

        OIDataItem that = (OIDataItem) obj;
        if (getTime() != that.getTime())
            return false;

		if (!Arrays.equals(data, that.getValues()))
			return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		int hash = 7;
		hash = 97 * hash + (int) (this.time ^ (this.time >>> 32));
		hash = 97 * hash + Arrays.hashCode(this.data);
		return hash;
	}

	@Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
		sb.append(Long.toString(time)).append(", ");
        sb.append(Arrays.toString(data));
        return sb.toString();
    }

	@Override
	public int compareTo(OIDataItem item)
	{
		return (new Long(getTime())).compareTo(new Long(item.getTime()));
	}

}
