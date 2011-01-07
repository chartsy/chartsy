package org.chartsy.main.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Viorel
 */
public final class DatasetUtilities
{

	public static final int OPEN_PRICE = 0;
    public static final int HIGH_PRICE = 1;
    public static final int LOW_PRICE = 2;
    public static final int CLOSE_PRICE = 3;
    public static final int VOLUME_PRICE = 4;
	
    public static final String OPEN = "Open";
    public static final String HIGH = "High";
    public static final String LOW = "Low";
    public static final String CLOSE = "Close";
    public static final String VOLUME = "Volume";

	public static final String[] LIST = new String[]
    {
        OPEN, HIGH, LOW, CLOSE
    };

	private DatasetUtilities()
	{
	}

	public static int getPrice(String price)
    {
        for (int i = 0; i < LIST.length; i++)
            if (price.equals(LIST[i]))
                return i;
        return 3; // Default Close
    }

	public static Dataset getVisibleDataset(Dataset dataset, int period, int end)
    {
        return new Dataset(getVisibleItems(dataset, period, end));
    }

	public static List<DataItem> getVisibleItems(Dataset dataset, int period, int end)
    {
        List<DataItem> list = new ArrayList<DataItem>();
        for (int i = 0; i < period; i++)
        {
            int j = end - period + i;
            if (j < dataset.getItemsCount() && j >= 0)
            {
                list.add(dataset.getDataItem(j));
            }
        }
        return list;
    }

	public static DataItem[] getVisibleItemsArray(Dataset dataset, int period, int end)
    {
        DataItem[] list = new DataItem[period];
        for (int i = 0; i < period; i++)
        {
            int j = end - period + i;
            if (j < dataset.getItemsCount() && j >= 0)
                list[i] = dataset.getDataItem(j);
        }
        return list;
    }

	public static Dataset EMPTY(int count)
	{
		List<DataItem> items = new ArrayList<DataItem>(count);
		Collections.fill(items, null);
		return new Dataset(items);
	}

	public static Dataset CONST(Dataset dataset, double ct)
	{
		if (dataset == null)
			return null;

		int count = dataset.getItemsCount();
		Dataset result = EMPTY(count);
		for (int i = 0; i < count; i++)
		{
			long time = dataset.getTimeAt(i);
			DataItem item = new DataItem(time, ct);
			result.setDataItem(i, item);
		}

		return result;
	}

	public static Dataset LOG(Dataset dataset)
	{
		if (dataset == null)
			return null;

		int count = dataset.getItemsCount();
		Dataset result = EMPTY(count);

		for (int i = 0; i < count; i++)
		{
			if (dataset.getDataItem(i) != null)
			{
				long time = dataset.getTimeAt(i);
				double open = Math.log10(dataset.getOpenAt(i));
                double high = Math.log10(dataset.getHighAt(i));
                double low = Math.log10(dataset.getLowAt(i));
                double close = Math.log10(dataset.getCloseAt(i));
                double volume = Math.log10(dataset.getVolumeAt(i));
				DataItem item = new DataItem(time, open, high, low, close, volume);
				result.setDataItem(i, item);
			}
		}

		return result;
	}

	public static Dataset SUM(Dataset dataset1, Dataset dataset2)
	{
		if (dataset1 == null)
		{
			return dataset2;
		} else
		{
			if (dataset2 == null)
			{
				return dataset1;
			}
		}

		int count1 = dataset1.getItemsCount();
		int count2 = dataset2.getItemsCount();
		int count = Math.min(count1, count2);
		Dataset result = EMPTY(count);

		for (int i = 0; i < count; i++)
		{
			long time = dataset1.getTimeAt(i);
			double open = dataset1.getOpenAt(i) + dataset2.getOpenAt(i);
			double high = dataset1.getHighAt(i) + dataset2.getHighAt(i);
			double low = dataset1.getLowAt(i) + dataset2.getLowAt(i);
			double close = dataset1.getCloseAt(i) + dataset2.getCloseAt(i);
			double volume = dataset1.getVolumeAt(i) + dataset2.getVolumeAt(i);
			DataItem item = new DataItem(time, open, high, low, close, volume);
			result.setDataItem(i, item);
		}

		return result;
	}

	public static Dataset DIFF(Dataset dataset1, Dataset dataset2)
	{
		if (dataset1 == null)
		{
			return dataset2;
		} else
		{
			if (dataset2 == null)
			{
				return dataset1;
			}
		}

        int count1 = dataset1.getItemsCount();
		int count2 = dataset2.getItemsCount();
		int count = Math.min(count1, count2);
        Dataset result = EMPTY(count);

        for (int i = 0; i < count; i++)
        {
			long time = dataset1.getTimeAt(i);
			double open = dataset1.getOpenAt(i) - dataset2.getOpenAt(i);
			double high = dataset1.getHighAt(i) - dataset2.getHighAt(i);
			double low = dataset1.getLowAt(i) - dataset2.getLowAt(i);
			double close = dataset1.getCloseAt(i) - dataset2.getCloseAt(i);
			double volume = dataset1.getVolumeAt(i) - dataset2.getVolumeAt(i);
            DataItem item = new DataItem(time, open, high, low, close, volume);
			result.setDataItem(i, item);
        }

        return result;
	}

	public static Dataset MULTIPLY(Dataset dataset, double value)
    {
        if (dataset == null)
            return null;

        int count = dataset.getItemsCount();
        Dataset result = EMPTY(count);

        for (int i = 0; i < count; i++)
        {
			long time = dataset.getTimeAt(i);
			double open = dataset.getOpenAt(i) * value;
			double high = dataset.getHighAt(i) * value;
			double low = dataset.getLowAt(i) * value;
			double close = dataset.getCloseAt(i) * value;
			double volume = dataset.getVolumeAt(i) * value;
			DataItem item = new DataItem(time, open, high, low, close, volume);
			result.setDataItem(i, item);
        }

        return result;
    }

    public static Dataset DIV(Dataset dataset, double value)
    {
        if (dataset == null)
            return null;

        int count = dataset.getItemsCount();
        Dataset result = EMPTY(count);

        for (int i = 0; i < count; i++)
        {
			long time = dataset.getTimeAt(i);
			double open = dataset.getOpenAt(i) / value;
			double high = dataset.getHighAt(i) / value;
			double low = dataset.getLowAt(i) / value;
			double close = dataset.getCloseAt(i) / value;
			double volume = dataset.getVolumeAt(i) / value;
			DataItem item = new DataItem(time, open, high, low, close, volume);
			result.setDataItem(i, item);
        }

        return result;
    }

	public static Dataset HMA(Dataset dataset, int period)
    {
        if (dataset == null)
            return null;

        Dataset wma_n2 = WMA(dataset, period / 2);
        Dataset wma_n = WMA(dataset, period);
        Dataset two_wma_n2 = MULTIPLY(wma_n2, 2);
        Dataset diff = DIFF(two_wma_n2, wma_n);

        Dataset result = WMA(diff, (int) Math.sqrt(period));

        return result;
    }

	public static Dataset SMA(Dataset dataset, int period)
    {
        if (dataset == null)
            return null;

        int count = dataset.getItemsCount();
        Dataset result = EMPTY(count);

        int j = 0;
        for (j = 0; j < count && (dataset.getDataItem(j) == null); j++)
            result.setDataItem(j, null);

        for (int i = j + period - 1; i < count; i++)
        {
            long time = dataset.getTimeAt(i);
            double open = 0;
            double high = 0;
            double low = 0;
            double close = 0;
            double volume = 0;

            for (int k = 0; k < period; k++)
            {
                open += dataset.getOpenAt(i - k);
                high += dataset.getHighAt(i - k);
                low += dataset.getLowAt(i - k);
                close += dataset.getCloseAt(i - k);
                volume += dataset.getVolumeAt(i - k);
            }

            open /= (double) period;
            high /= (double) period;
            low /= (double) period;
            close /= (double) period;
            volume /= (double) period;

			DataItem item = new DataItem(time, open, high, low, close, volume);
            result.setDataItem(i, item);
        }
		
        return result;
    }

	public static Dataset EMA(Dataset dataset, int period)
    {
        if (dataset == null)
            return null;

        int count = dataset.getItemsCount();
        Dataset result = EMPTY(count);

        int j = 0;
        for (j = 0; j < count && (dataset.getDataItem(j) == null); j++)
            result.setDataItem(j, null);

        double open = 0;
        double high = 0;
        double low = 0;
        double close = 0;
        double volume = 0;

        for (int i = j; i < period + j && i < count; i++)
        {
			long time = dataset.getTimeAt(i);
            open += dataset.getOpenAt(i);
            high += dataset.getHighAt(i);
            low += dataset.getLowAt(i);
            close += dataset.getCloseAt(i);
            volume += dataset.getVolumeAt(i);

            if (i == period + j - 1)
            {
                open /= (double) period;
                high /= (double) period;
                low /= (double) period;
                close /= (double) period;
                volume /= (double) period;

				DataItem item = new DataItem(time, open, high, low, close, volume);
                result.setDataItem(i, item);
            } else
            {
                result.setDataItem(i, null);
            }
        }

        double k = 2 / ((double) (period + 1));
        for (int i = period + j; i < count; i++)
        {
			long time = dataset.getTimeAt(i);
            open = (dataset.getOpenAt(i) - open) * k + open;
            high = (dataset.getHighAt(i) - high) * k + high;
            low = (dataset.getLowAt(i) - low) * k + low;
            close = (dataset.getCloseAt(i) - close) * k + close;
            volume = (dataset.getVolumeAt(i) - volume) * k + volume;

			DataItem item = new DataItem(time, open, high, low, close, volume);
            result.setDataItem(i, item);
        }

        return result;
    }

	/*
     * Wilder does not use the standard exponential moving average formula.
     *
     * Indicators affected are:

     * Average True Range
     * Directional Movement System
     * Relative Strength Index
     * Twiggs Money Flow developed by Colin Twiggs using Wilder's moving average formula.
     *
     * http://www.incrediblecharts.com/indicators/wilder_moving_average.php
     * http://user42.tuxfamily.org/chart/manual/Exponential-Moving-Average.html
     */
    public static Dataset EMAWilder(Dataset dataset, int period)
    {
        int classic_ema_period = 2 * period - 1;
        return EMA(dataset, classic_ema_period);
    }

	public static Dataset WMA(Dataset dataset, int period)
    {
        if (dataset == null)
            return null;

        int count = dataset.getItemsCount();
        Dataset result = EMPTY(count);
		
        double denominator = ((double) period * ((double) period + 1)) / 2;
        int j = 0;
        for (j = 0; j < count && (dataset.getDataItem(j) == null); j++)
            result.setDataItem(j, null);
        for (int i = j; i < period + j; i++)
            result.setDataItem(i, null);

        for (int i = period + j; i < count; i++)
        {
			long time = dataset.getTimeAt(i);
            double open = 0;
            double high = 0;
            double low = 0;
            double close = 0;
            double volume = 0;

            for (int k = i - period; k < i; k++)
            {
                open += (period - i + k + 1) * dataset.getOpenAt(k);
                high += (period - i + k + 1) * dataset.getHighAt(k);
                low += (period - i + k + 1) * dataset.getLowAt(k);
                close += (period - i + k + 1) * dataset.getCloseAt(k);
                volume += (period - i + k + 1) * dataset.getVolumeAt(k);
            }

            open /= denominator;
            high /= denominator;
            low /= denominator;
            close /= denominator;
            volume /= denominator;

			DataItem item = new DataItem(time, open, high, low, close, volume);
            result.setDataItem(i, item);
        }

        return result;
    }

	public static Dataset TEMA(Dataset dataset, int period)
    {
        if (dataset == null)
            return null;

        int count = dataset.getItemsCount();
        Dataset result = EMPTY(count);

        Dataset ema1 = EMA(dataset, period);
        Dataset ema2 = EMA(ema1, period);
        Dataset ema3 = EMA(ema2, period);

        int j = 0;
        for (j = 0; j < count 
			&& (dataset.getDataItem(j) == null
			|| ema1.getDataItem(j) == null
			|| ema2.getDataItem(j) == null
			|| ema3.getDataItem(j) == null); j++)
            result.setDataItem(j, null);

        for (int i = j; i < count; i++)
        {
			long time = dataset.getTimeAt(i);
            double open = 0;
            double high = 0;
            double low = 0;
            double close = 0;
            double volume = 0;

            if (ema1.getCloseAt(i) != 0 && ema2.getCloseAt(i) != 0 && ema3.getCloseAt(i) != 0)
            {
                open = 3 * ema1.getOpenAt(i) - 3 * ema2.getOpenAt(i) + ema3.getOpenAt(i);
                high = 3 * ema1.getHighAt(i) - 3 * ema2.getHighAt(i) + ema3.getHighAt(i);
                low = 3 * ema1.getLowAt(i) - 3 * ema2.getLowAt(i) + ema3.getLowAt(i);
                close = 3 * ema1.getCloseAt(i) - 3 * ema2.getCloseAt(i) + ema3.getCloseAt(i);
                volume = 3 * ema1.getVolumeAt(i) - 3 * ema2.getVolumeAt(i) + ema3.getVolumeAt(i);
            }

			DataItem item = new DataItem(time, open, high, low, close, volume);
            result.setDataItem(i, item);
        }

        return result;
    }

	public static Dataset[] ADX(Dataset dataset, int period)
    {
        if (dataset == null)
            return null;

        int count = dataset.getItemsCount();
        Dataset pdi = EMPTY(count);
        Dataset mdi = EMPTY(count);
        Dataset dx = EMPTY(count);

        Dataset tr = EMPTY(count);
        Dataset hmhp = EMPTY(count);
        Dataset lmlp = EMPTY(count);

        for (int i = 1; i < count; i++)
        {
            long time = dataset.getTimeAt(i);

            double tr0 = dataset.getHighAt(i) - dataset.getCloseAt(i - 1);
            double tr1 = Math.abs(dataset.getHighAt(i) - dataset.getCloseAt(i - 1));
            tr0 = Math.max(tr0, tr1);
            tr1 = Math.abs(dataset.getLowAt(i) - dataset.getCloseAt(i - 1));
            tr.setDataItem(i, new DataItem(time, Math.max(tr0, tr1)));

            if (dataset.getHighAt(i) <= dataset.getHighAt(i - 1) && dataset.getLowAt(i) < dataset.getLowAt(i - 1))
            {
                lmlp.setDataItem(i, new DataItem(time, dataset.getLowAt(i - 1) - dataset.getLowAt(i)));
            } else if (dataset.getHighAt(i) > dataset.getHighAt(i - 1) && dataset.getLowAt(i) >= dataset.getLowAt(i - 1))
            {
                hmhp.setDataItem(i, new DataItem(time, dataset.getHighAt(i) - dataset.getHighAt(i - 1)));
            } else
            {
                double tempH = Math.abs(dataset.getHighAt(i) - dataset.getHighAt(i - 1));
                double tempL = Math.abs(dataset.getLowAt(i) - dataset.getLowAt(i - 1));

                if (tempH > tempL)
                {
					DataItem item = new DataItem(time, tempH);
                    hmhp.setDataItem(i, item);
                } else
                {
					DataItem item = new DataItem(time, tempL);
                    lmlp.setDataItem(i, item);
                }
            }
        }

        Dataset strDS = EMA(tr, period);
        Dataset shmhpDS = EMA(hmhp, period);
        Dataset slmlpDS = EMA(lmlp, period);

        for (int i = period; i < count; i++)
        {
			DataItem item = null;
            long time = dataset.getTimeAt(i);
            double curPDI = 0;
            double curMDI = 0;

            if (strDS.getDataItem(i) != null)
            {
                if (strDS.getCloseAt(i) != 0)
                {
                    curPDI = (shmhpDS.getCloseAt(i) / strDS.getCloseAt(i)) * 100;
                    curMDI = (slmlpDS.getCloseAt(i) / strDS.getCloseAt(i)) * 100;
                }
            }

			item = new DataItem(time, curPDI);
            pdi.setDataItem(i, new DataItem(time, curPDI));
			item = new DataItem(time, curMDI);
            mdi.setDataItem(i, new DataItem(time, curMDI));

            if (curPDI + curMDI != 0)
            {
				double value = Math.abs(curPDI - curMDI) / (curPDI + curMDI) * 100;
				item = new DataItem(time, value);
                dx.setDataItem(i, item);
            }
        }

        Dataset adx = EMA(dx, period);

        Dataset[] result = new Dataset[3];
        result[0] = pdi;
        result[1] = mdi;
        result[2] = adx;
        return result;
    }

}
