package org.chartsy.main.data;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.swing.event.EventListenerList;
import org.chartsy.main.ChartFrame;
import org.chartsy.main.ChartFrameListener;
import org.chartsy.main.chart.Annotation;
import org.chartsy.main.chart.Chart;
import org.chartsy.main.chart.Indicator;
import org.chartsy.main.chart.Overlay;
import org.chartsy.main.events.DatasetEvent;
import org.chartsy.main.events.DatasetListener;
import org.chartsy.main.intervals.Interval;
import org.chartsy.main.intervals.MonthlyInterval;
import org.chartsy.main.intervals.WeeklyInterval;
import org.chartsy.main.managers.DataProviderManager;
import org.chartsy.main.managers.DatasetUsage;
import org.chartsy.main.utils.Bounds;
import org.chartsy.main.utils.Range;
import org.chartsy.main.utils.RectangleInsets;
import org.chartsy.main.utils.SerialVersion;

/**
 *
 * @author viorel.gheba
 */
public class ChartData implements Serializable, ChartFrameListener
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;
	
    public static final int MIN_ITEMS = 40;
    public static final int MAX_ITEMS = 1000;
    public static final RectangleInsets axisOffset = new RectangleInsets(5.0, 5.0, 5.0, 5.0);
    public static final RectangleInsets dataOffset = new RectangleInsets(2.0, 20.0, 40.0, 55.0);
	
    private Stock stock;
    private Interval interval;
    private Chart chart;
    private String dataProviderName;
    private String datasetKey;
    private Dataset visible;
    private Range visibleRange;
    private List<Indicator> savedIndicators;
    private List<Overlay> savedOverlays;
    private List<Integer> annotationsCount;
    private List<Annotation> annotations;
    private int period = -1;
    private int last = -1;
	private int size = -1;

    public ChartData()
    {
    }

    public Stock getStock()
    {
        return stock;
    }

    public void setStock(Stock stock)
    {
        this.stock = stock;
    }

    public boolean isStockNull()
    {
        return stock == null;
    }

    public Interval getInterval()
    {
        return interval;
    }

    public void setInterval(Interval interval)
    {
        this.interval = interval;
    }

    public boolean isIntervalNull()
    {
        return interval == null;
    }

    public boolean updateDataset(Interval newInterval)
    {
        /*Dataset newDataset = dataProvider.getDataset(stock, newInterval);
        if (newDataset != null)
        {
            setInterval(newInterval);
            setDataset(newDataset);
            setLast(-1);
            return fireDatasetEvent(new DatasetEvent(this));
        }*/
        return false;
    }

    public void updateDataset()
    {
        //fireDatasetEvent(new DatasetEvent(this));
    }

    public Chart getChart()
    {
        return chart;
    }

    public void setChart(Chart chart)
    {
        this.chart = chart;
    }

    public boolean isChartNull()
    {
        return chart == null;
    }

    public DataProvider getDataProvider()
    {
        return DataProviderManager.getDefault().getDataProvider(dataProviderName);
    }

    public void setDataProviderName(String dataProviderName)
    {
        this.dataProviderName = dataProviderName;
    }

	public String getDataProviderName()
	{
		return dataProviderName;
	}

    public boolean isDataProviderNull()
    {
        return dataProviderName == null;
    }

    public Dataset getDataset()
    {
		return DatasetUsage.getInstance().getDatasetFromMemory(datasetKey);
    }

    public Dataset getDataset(boolean b)
    {
        return getDataset();
    }

    public void setDatasetKey(String datasetKey)
    {
        this.datasetKey = datasetKey;
		size = getDataset().getItemsCount();
    }

	public String getDatasetKey()
	{
		return datasetKey;
	}

    public boolean isDatasetNull()
    {
        return datasetKey == null;
    }

    public Dataset getVisible()
    {
        return visible;
    }

    private void setVisible(Dataset d)
    {
        visible = d;
    }

    public boolean isVisibleNull()
    {
        return visible == null;
    }

    public int getPeriod()
    {
        return period;
    }

    public void setPeriod(int period)
    {
        this.period = period;
    }

    public int getLast()
    {
        return last;
    }

    public void setLast(int last)
    {
        this.last = last;
    }

    public void setSavedIndicators(List<Indicator> list)
    {
        savedIndicators = list;
    }

    public List<Indicator> getSavedIndicators()
    {
        return savedIndicators;
    }

    public void clearSavedIndicators()
    {
		if (savedIndicators != null)
			savedIndicators.clear();
		savedIndicators = null;
    }

    public void setSavedOverlays(List<Overlay> list)
    {
        savedOverlays = list;
    }

    public List<Overlay> getSavedOverlays()
    {
        return savedOverlays;
    }

    public void clearSavedOverlays()
    {
		if (savedOverlays != null)
			savedOverlays.clear();
		savedOverlays = null;
    }

    public void setAnnotationsCount(List<Integer> list)
    {
        annotationsCount = list;
    }

    public List<Integer> getAnnotationsCount()
    {
        return annotationsCount;
    }

    public void clearAnnotationsCount()
    {
		if (annotationsCount != null)
			annotationsCount.clear();
		annotationsCount = null;
    }

    public void setAnnotations(List<Annotation> list)
    {
        annotations = list;
    }

    public List<Annotation> getAnnotations()
    {
        return annotations;
    }

    public void clearAnnotations()
    {
		if (annotations != null)
			annotations.clear();
		annotations = null;
    }

    public void setVisibleRange(Range r)
    {
        visibleRange = r;
    }

    public Range getVisibleRange()
    {
        return visibleRange;
    }

    public void calculateRange(ChartFrame chartFrame, List<Overlay> overlays)
    {
        Range range = new Range();
        if (!isVisibleNull())
        {
            double min = getVisible().getMinNotZero();
            double max = getVisible().getMaxNotZero();
            range = new Range(min - (max - min) * 0.01, max + (max - min) * 0.01);
			
			for (int i = 0; i < overlays.size(); i++)
			{
				Overlay overlay = overlays.get(i);
				if (overlay.isIncludedInRange())
				{
					Range oRange = overlay.getRange(chartFrame, overlay.getPrice());
					if (oRange != null)
					{
						if (oRange.getLowerBound() > 0)
							range = Range.expandToInclude(range, oRange.getLowerBound());
						if (!Double.isInfinite(oRange.getUpperBound()))
							range = Range.expandToInclude(range, oRange.getUpperBound());
					}
				}
			}
        }
        setVisibleRange(range);
    }

    public void calculate(ChartFrame chartFrame)
    {
		double barWidth = chartFrame.getChartProperties().getBarWidth();
		Rectangle rect = chartFrame.getSplitPanel().getBounds();
		rect.grow(-2, -2);

		last = last == -1 ? size : last;
		period = (int) (rect.getWidth() / (barWidth + 2));
		if (period == 0)
			period = 150;
		if (period > size)
			period = size;

		if (getDataset() != null)
		{
			setVisible(getDataset().getVisibleDataset(period, last));

			int index = chartFrame.getSplitPanel().getIndex();
			chartFrame.getSplitPanel().setIndex(index > period - 1 ? period - 1 : index);
			chartFrame.updateHorizontalScrollBar();
		}
    }

    public double[] getDateValues()
    {
		if (!isVisibleNull())
		{
			int count = getVisible().getItemsCount();
			Interval itrv = getInterval();
			double[] list = new double[getVisible().getItemsCount()];

			if (!itrv.isIntraDay())
			{
				Calendar cal = Calendar.getInstance();
				cal.setFirstDayOfWeek(Calendar.MONDAY);
				cal.setTimeInMillis(getVisible().getTimeAt(0));
				list[0] = 0;
				
				if (itrv instanceof MonthlyInterval)
				{
					int year = cal.get(Calendar.YEAR);
					for (int i = 2; i < count + 1; i++)
					{
						cal.setTimeInMillis(getVisible().getTimeAt(i - 1));
						if (year != cal.get(Calendar.YEAR))
						{
							list[i - 1] = i - 1;
							year = cal.get(Calendar.YEAR);
						} else
						{
							list[i - 1] = -1;
						}
					}
				} else
				{
					int month = cal.get(Calendar.MONTH);
					for (int i = 2; i < count + 1; i++)
					{
						cal.setTimeInMillis(getVisible().getTimeAt(i - 1));
						if (month != cal.get(Calendar.MONTH))
						{
							list[i - 1] = i - 1;
							month = cal.get(Calendar.MONTH);
						} else
						{
							list[i - 1] = -1;
						}
					}
				}
			} else
			{
				for (int i = 0; i < count; i++)
				{
					if (i % 10 == 0)
						list[i] = i;
					else
						list[i] = -1;
				}
			}

			return list;
		}
		
		return new double[0];
    }

	public boolean isFirstWorkingDayOfMonth(long time)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.setTimeInMillis(time);
		if (getInterval() instanceof WeeklyInterval)
		{
			int day = calendar.get(Calendar.DAY_OF_WEEK);
			int week = getFirstWeekMondayOfMonth(
				calendar.get(Calendar.MONTH),
				calendar.get(Calendar.YEAR));
			if (week == calendar.get(Calendar.WEEK_OF_MONTH))
			{
				return day == Calendar.MONDAY;
			}
			return false;
		} else
		{
			int week = getFirstWorkingWeekOfMonth(
				calendar.get(Calendar.MONTH),
				calendar.get(Calendar.YEAR));
			if (calendar.get(Calendar.WEEK_OF_MONTH) == week)
				return calendar.get(Calendar.DAY_OF_WEEK) ==
					getFirstWorkingDayOfMonth(
					calendar.get(Calendar.MONTH),
					calendar.get(Calendar.YEAR));
			else
				return false;
		}
	}

	private int getFirstWeekMondayOfMonth(int month, int year)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONDAY, month);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)
		{
			return calendar.get(Calendar.WEEK_OF_MONTH);
		} else
		{
			while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY)
				calendar.add(Calendar.DAY_OF_MONTH, 1);
			return calendar.get(Calendar.WEEK_OF_MONTH);
		}
	}

	private static int getFirstWorkingWeekOfMonth(int month, int year)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONDAY, month);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		switch (calendar.get(Calendar.DAY_OF_WEEK))
		{
			case Calendar.SATURDAY:
				calendar.add(Calendar.DAY_OF_MONTH, 2);
				return calendar.get(Calendar.WEEK_OF_MONTH);
			case Calendar.SUNDAY:
				calendar.add(Calendar.DAY_OF_MONTH, 1);
				return calendar.get(Calendar.WEEK_OF_MONTH);
			default:
				return calendar.get(Calendar.WEEK_OF_MONTH);
		}
	}

	private int getFirstWorkingDayOfMonth(int month, int year)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONDAY, month);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		switch (calendar.get(Calendar.DAY_OF_WEEK))
		{
			case Calendar.SATURDAY:
				calendar.add(Calendar.DAY_OF_MONTH, 2);
				return calendar.get(Calendar.DAY_OF_WEEK);
			case Calendar.SUNDAY:
				calendar.add(Calendar.DAY_OF_MONTH, 1);
				return calendar.get(Calendar.DAY_OF_WEEK);
			default:
				return calendar.get(Calendar.DAY_OF_WEEK);
		}
	}

	private static DecimalFormat D1 = new DecimalFormat("0.###");
	private static DecimalFormat D2 = new DecimalFormat("0.0");

	public double[] getYValues(Rectangle rectangle, Range range, int fontHeight)
	{
		int count = 15;
		while (((rectangle.height / count) < (fontHeight + 20)) && (count > -2))
			count--;

		double rangeMin = range.getLowerBound();
		double rangeMax = range.getUpperBound();
		
		double vRange = rangeMax - rangeMin;
		double rangeUnit = vRange / count;

		int roundedExponent = (int)Math.round(Math.log10(rangeUnit)) - 1;
		double factor = Math.pow(10, -roundedExponent);
		int adjustedValue = (int)(rangeUnit * factor);
		rangeUnit = (double)adjustedValue / factor;

		if (rangeUnit < 0.001)
		{
			rangeUnit = 0.001d;
		} else if (rangeUnit >= 0.001 && rangeUnit < 0.005)
		{
			String unitStr = D1.format(rangeUnit);
			try
			{
				rangeUnit = D1.parse(unitStr.trim()).doubleValue();
			} catch (ParseException ex)
			{
			}
		} else if (rangeUnit >= 0.005 && rangeUnit < 1)
		{
			String unitStr = D2.format(rangeUnit);
			try
			{
				rangeUnit = D2.parse(unitStr.trim()).doubleValue();
			} catch (ParseException ex)
			{
			}
		}

		rangeMin = (int)(rangeMin / rangeUnit) * rangeUnit;
		count = (int)(vRange / rangeUnit);

		if (count + 2 > 0)
		{
			double[] result = new double[count + 2];
			for (int i = 0; i < count + 2; i++)
				result[i] = rangeMin + rangeUnit * i;
			return result;
		} else
		{
			List<Double> list = getPriceValues(rectangle, range);
			double[] result = new double[list.size()];
			for (int i = 0; i < list.size(); i++)
				result[i] = list.get(i).doubleValue();
			return result;
		}
	}

    public List<Double> getPriceValues(Rectangle rect, Range range)
    {
        List<Double> values = new ArrayList<Double>();

        double diff = range.getUpperBound() - range.getLowerBound();
        if (diff > 10)
        {
            int step = (int) (diff / 10) + 1;
            double low = Math.ceil(range.getUpperBound() - (diff / 10) * 9);

            for (double i = low; i <= range.getUpperBound(); i += step)
            {
                values.add(new Double(i));
            }
        } else
        {
            double step = diff / 10;
            for (double i = range.getLowerBound(); i <= range.getUpperBound(); i += step)
            {
                values.add(new Double(i));
            }
        }

        return values;
    }

    public double calculateWidth(int width)
    {
        int count = getDataset().getItemsCount();
        int items = getPeriod();
        double w = width / items;
        return w * count;
    }

    public Point2D.Double valueToJava2D(final double xvalue, final double yvalue, Rectangle bounds, Range range, boolean isLog)
    {
        double px = getX(xvalue, bounds);
        double py = getY(yvalue, bounds, range, isLog);
        Point2D.Double p = new Point2D.Double(px, py);
        return p;
    }

    public Point2D getPoint(double x, double y, Range range, Rectangle rect, boolean isLog)
    {
        return new Point2D.Double(getX(x, rect), getY(y, rect, range, isLog));
    }

    public double getX(double value, Rectangle rect)
    {
        return rect.getMinX() + (((value + 0.5D) / (double) getPeriod()) * rect.getWidth());
    }

    private double getY(double value, Rectangle rect, Range range)
    {
        return rect.getMinY() + (range.getUpperBound() - value) / (range.getUpperBound() - range.getLowerBound()) * rect.getHeight();
    }

	public double getY(double value, Rectangle rect, Range range, boolean isLog)
    {
		if (isLog)
			return getLogY(value, rect, range);
        return getY(value, rect, range);
    }

    private double getLogY(double value, Rectangle rect, Range range)
    {
        double base = 0;
		if (range.getLowerBound() < 0)
			base = Math.abs(range.getLowerBound()) + 1.0D;
		double scale = (rect.getHeight() / 
			(Math.log(range.getUpperBound() + base) -
			Math.log(range.getLowerBound() + base)));
		return rect.getMinY() +
			Math.round(
			(Math.log(range.getUpperBound() + base) -
			Math.log(value + base)) * scale);
    }

    public int getIndex(int x, Rectangle rect)
    {
        return getIndex(x, 1, rect);
    }

    public int getIndex(int x, int y, Rectangle rect)
    {
        return getIndex(new Point(x, y), rect);
    }

    public int getIndex(Point p, Rectangle rect)
    {
        int index = -1;
        int items = getPeriod();
        double w = rect.getWidth() / items;

        for (int i = 0; i < items; i++)
        {
            Bounds r = new Bounds(rect.getMinX() + (i * w), 0, w, 10);
            if (r.contains(p.x, 1))
            {
                index = i;
                break;
            }
        }

        return index;
    }
	
	private transient EventListenerList datasetListeners;

	private EventListenerList datasetListeners()
	{
		if (datasetListeners == null)
			datasetListeners = new EventListenerList();
		return datasetListeners;
	}

    public void addIndicatorsDatasetListeners(DatasetListener listener)
    {
        datasetListeners().add(DatasetListener.class, listener);
    }

    public void removeIndicatorsDatasetListeners(DatasetListener listener)
    {
        datasetListeners().remove(DatasetListener.class, listener);
    }

    public void removeAllIndicatorsDatasetListeners()
    {
        DatasetListener[] listeners = datasetListeners().getListeners(DatasetListener.class);
        for (int i = 0; i < listeners.length; i++)
        {
			if (listeners[i] instanceof Indicator)
				datasetListeners().remove(DatasetListener.class, listeners[i]);
        }
    }

    public void addOverlaysDatasetListeners(DatasetListener listener)
    {
        datasetListeners().add(DatasetListener.class, listener);
    }

    public void removeOverlaysDatasetListeners(DatasetListener listener)
    {
        datasetListeners().remove(DatasetListener.class, listener);
    }

    public void removeAllOverlaysDatasetListeners()
    {
        DatasetListener[] listeners = datasetListeners().getListeners(DatasetListener.class);
        for (int i = 0; i < listeners.length; i++)
        {
			if (listeners[i] instanceof Overlay)
				datasetListeners().remove(DatasetListener.class, listeners[i]);
        }
    }

    public boolean fireDatasetEvent(DatasetEvent evt)
    {
        DatasetListener[] listeners = datasetListeners().getListeners(DatasetListener.class);
		for (int i = 0; i < listeners.length; i++)
			listeners[i].datasetChanged(evt);
        return true;
    }

	@Override
	public void stockChanged(Stock newStock)
	{
		setStock(newStock);
	}

	@Override
	public void intervalChanged(Interval newInterval)
	{
		setInterval(newInterval);
	}

	@Override
	public void chartChanged(Chart newChart)
	{
		setChart(newChart);
	}

	@Override
	public void indicatorAdded(Indicator indicator)
	{
	}

	@Override
	public void indicatorRemoved(Indicator indicator)
	{
	}

	@Override
	public void overlayAdded(Overlay overlay)
	{
	}

	@Override
	public void overlayRemoved(Overlay overlay)
	{
	}

	@Override
	public double zoomIn(double barWidth)
	{
		double newWidth = barWidth + 1;
		int i = (int) ((period * barWidth) / newWidth);
		newWidth = i < MIN_ITEMS ? barWidth : newWidth;
		return newWidth;
	}

	@Override
	public double zoomOut(double barWidth)
	{
		double newWidth = barWidth - 1;
		int i = (int) ((period * barWidth) / newWidth);
		newWidth = i > getDataset().getItemsCount() ? barWidth : newWidth;
		return newWidth;
	}

	@Override
	public void datasetKeyChanged(String datasetKey)
	{
		setDatasetKey(datasetKey);
	}
	
}
