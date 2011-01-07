package org.chartsy.main.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.chartsy.main.utils.SerialVersion;

/**
 *
 * @author Viorel
 */
public class OIDataset implements Serializable
{

	private static final long serialVersionUID = SerialVersion.APPVERSION;

	private List<OIDataItem> data;

	public OIDataset()
	{
		data = new ArrayList<OIDataItem>();
	}

	public OIDataset(int count)
	{
		data = new ArrayList<OIDataItem>(count);
	}

	public OIDataset(List<OIDataItem> list)
	{
		data = list;
	}

	public boolean isNull()
    {
        return (data == null);
    }

    public boolean isEmpty()
    {
        return data.isEmpty();
    }

    public void sort()
    {
		Collections.sort(data);
    }

	public int size()
	{
		return data.size();
	}

	public int getLastIndex()
    {
		int index = data.size() - 1;
        return index < 0 ? 0 : index;
    }

	public List<OIDataItem> getItems()
	{
		return data;
	}

	public OIDataItem getItem(int index)
	{
		try
		{
			return data.get(index);
		} catch (IndexOutOfBoundsException ex)
		{
			return null;
		}
	}

	public void setItem(int index, OIDataItem item)
	{
		try
		{
			data.add(index, item);
		} catch (Exception ex)
		{
		}
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("OIDataset example: \n");
		for (int i = 0; i < data.size(); i++)
		{
			sb.append(data.get(i).toString()).append("\n");
		}
		return sb.toString();
	}

}
