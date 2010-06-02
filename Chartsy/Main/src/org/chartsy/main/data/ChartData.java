package org.chartsy.main.data;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.swing.event.EventListenerList;
import org.chartsy.main.ChartFrame;
import org.chartsy.main.chart.Annotation;
import org.chartsy.main.chart.Chart;
import org.chartsy.main.chart.Indicator;
import org.chartsy.main.chart.Overlay;
import org.chartsy.main.events.DatasetEvent;
import org.chartsy.main.events.DatasetListener;
import org.chartsy.main.intervals.Interval;
import org.chartsy.main.intervals.MonthlyInterval;
import org.chartsy.main.utils.Bounds;
import org.chartsy.main.utils.Range;
import org.chartsy.main.utils.RectangleInsets;

/**
 *
 * @author viorel.gheba
 */
public class ChartData implements Serializable
{

    private static final long serialVersionUID = 2L;
    public static final int MIN_ITEMS = 40;
    public static final int MAX_ITEMS = 1000;
    public static RectangleInsets axisOffset = new RectangleInsets(2.0, 2.0, 2.0, 2.0);
    public static RectangleInsets dataOffset = new RectangleInsets(2.0, 20.0, 60.0, 50.0);
    private Stock stock = null;
    private Interval interval = null;
    private Chart chart = null;
    private DataProvider dataProvider = null;
    private Dataset dataset = null;
    private Dataset visible = null;
    private Range visibleRange = null;
    private List<Indicator> savedIndicators = new ArrayList<Indicator>();
    private List<Overlay> savedOverlays = new ArrayList<Overlay>();
    private List<Integer> annotationsCount = new ArrayList<Integer>();
    private List<Annotation> annotations = new ArrayList<Annotation>();
    private int period = -1;
    private int last = -1;
    private boolean logarithmic = false;

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
        Dataset newDataset = dataProvider.getDataset(stock, newInterval);
        if (newDataset != null)
        {
            setInterval(newInterval);
            setDataset(newDataset);
            setLast(-1);
            return fireDatasetEvent(new DatasetEvent(this));
        }

        return false;
    }

    public void updateDataset()
    {
        fireDatasetEvent(new DatasetEvent(this));
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
        return dataProvider;
    }

    public void setDataProvider(DataProvider dataProvider)
    {
        this.dataProvider = dataProvider;
    }

    public boolean isDataProviderNull()
    {
        return dataProvider == null;
    }

    public Dataset getDataset()
    {
        if (logarithmic)
        {
            return Dataset.LOG(dataset);
        }
        return dataset;
    }

    public Dataset getDataset(boolean b)
    {
        if (b)
        {
            return getDataset();
        }
        return dataset;
    }

    public void setDataset(Dataset dataset)
    {
        this.dataset = dataset;
    }

    public boolean isDatasetNull()
    {
        return dataset == null;
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
        savedIndicators.clear();
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
        savedOverlays.clear();
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
        annotationsCount.clear();
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
        annotations.clear();
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
            if (overlays != null && !overlays.isEmpty())
            {
                for (Overlay overlay : overlays)
                {
                    if (overlay.isIncludedInRange())
                    {
                        Range oRange = overlay.getRange(chartFrame, overlay.getPrice());
                        if (oRange != null)
                        {
                            range = Range.combine(range, oRange);
                        }
                    }
                }
            }
        }
        setVisibleRange(range);
    }

    public void calculate(ChartFrame chartFrame)
    {
        if (!isDatasetNull())
        {
            double barWidth = chartFrame.getChartProperties().getBarWidth();
            Rectangle rect = chartFrame.getSplitPanel().getBounds();
            rect.grow(-2, -2);

            last = last == -1 ? dataset.getItemsCount() : last;
            period = (int) (rect.getWidth() / (barWidth + 2));
            if (period == 0)
            {
                period = 150;
            }
            if (period > dataset.getItemsCount())
            {
                period = dataset.getItemsCount();
            }

            setVisible(getDataset().getVisibleDataset(period, last));

            int index = chartFrame.getSplitPanel().getIndex();
            chartFrame.getSplitPanel().setIndex(index > period - 1 ? period - 1 : index);
            chartFrame.updateHorizontalScrollBar();
        }
    }

    public void zoomIn(ChartFrame chartFrame)
    {
        double barWidth = chartFrame.getChartProperties().getBarWidth();
        double newWidth = barWidth + 1;

        int i = (int) ((period * barWidth) / newWidth);
        newWidth = i < MIN_ITEMS ? barWidth : newWidth;

        chartFrame.getChartProperties().setBarWidth(newWidth);
        calculate(chartFrame);
        chartFrame.repaint();
    }

    public void zoomOut(ChartFrame chartFrame)
    {
        double barWidth = chartFrame.getChartProperties().getBarWidth();
        double newWidth = barWidth - 1;

        int i = (int) ((period * barWidth) / newWidth);
        newWidth = i > getDataset().getItemsCount() ? barWidth : newWidth;

        chartFrame.getChartProperties().setBarWidth(newWidth);
        calculate(chartFrame);
        chartFrame.repaint();
    }

    public List<Double> getDateValues()
    {
        List<Double> list = new ArrayList<Double>();
        if (!isVisibleNull())
        {
            Calendar cal = Calendar.getInstance();
            cal.setFirstDayOfWeek(Calendar.MONDAY);
            cal.setTimeInMillis(getVisible().getTimeAt(0));

            if (getInterval() instanceof MonthlyInterval)
            {
                list.add(new Double(0));
            }

            if (!getInterval().isIntraDay())
            {
                int month = cal.get(Calendar.MONTH);
                for (int i = 0; i < getVisible().getItemsCount(); i++)
                {
                    cal.setTimeInMillis(getVisible().getTimeAt(i));
                    if (month != cal.get(Calendar.MONTH))
                    {
                        list.add(new Double(i));
                        month = cal.get(Calendar.MONTH);
                    }
                }
            } else
            {
                for (int i = 0; i < getVisible().getItemsCount(); i++)
                {
                    if (i % 10 == 0)
                    {
                        list.add(new Double(i));
                    }
                }
            }
        }
        return list;
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

    public Point2D.Double valueToJava2D(final double xvalue, final double yvalue, Rectangle bounds, Range range)
    {
        double x = (bounds.getWidth() / getPeriod()) * xvalue;
        double c = bounds.getWidth() / (2 * getVisible().getItemsCount());
        double px = bounds.getMinX() + x + c;

        double dif = range.getUpperBound() - range.getLowerBound();
        double percent = ((range.getUpperBound() - yvalue) / dif) * 100;
        double py = bounds.getMinY() + (bounds.getHeight() * percent) / 100;

        if (range.contains(0))
        {
            dif = Math.abs(range.getUpperBound()) + Math.abs(range.getLowerBound());

            double h1 = (Math.abs(range.getUpperBound()) * bounds.getHeight()) / dif;
            double h2 = (Math.abs(range.getLowerBound()) * bounds.getHeight()) / dif;

            if (yvalue >= 0)
            {
                percent = ((range.getUpperBound() - yvalue) / range.getUpperBound()) * 100;
                py = bounds.getMinY() + (h1 * percent) / 100;
            } else
            {
                percent = ((Math.abs(range.getLowerBound()) - Math.abs(yvalue)) / Math.abs(range.getLowerBound())) * 100;
                py = bounds.getMinY() + h1 + (h2 - ((h2 * percent) / 100));
            }
        }

        Point2D.Double p = new Point2D.Double(px, py);

        return p;
    }

    public Point2D getPoint(double x, double y, Range range, Rectangle rect)
    {
        return new Point2D.Double(getX(x, rect), getY(y, rect, range));
    }

    public double getX(double value, Rectangle rect)
    {
        double x = (rect.getWidth() / getPeriod()) * value;
        double c = rect.getWidth() / (2 * getPeriod());
        return rect.getMinX() + x + c;
    }

    public double getY(double value, Rectangle rect, Range range)
    {
        double dif = range.getUpperBound() - range.getLowerBound();
        double percent = ((range.getUpperBound() - value) / dif) * 100;
        double py = rect.getMinY() + (rect.getHeight() * percent) / 100;

        if (range.contains(0))
        {
            dif = Math.abs(range.getUpperBound()) + Math.abs(range.getLowerBound());
            double h1 = (Math.abs(range.getUpperBound()) * rect.getHeight()) / dif;
            double h2 = (Math.abs(range.getLowerBound()) * rect.getHeight()) / dif;

            if (value >= 0)
            {
                percent = ((range.getUpperBound() - value) / range.getUpperBound()) * 100;
                py = rect.getMinY() + (h1 * percent) / 100;
            } else
            {
                percent = ((Math.abs(range.getLowerBound()) - Math.abs(value)) / Math.abs(range.getLowerBound())) * 100;
                py = rect.getMinY() + h1 + (h2 - ((h2 * percent) / 100));
            }
        }

        return py;
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
    private transient EventListenerList indicatorsDatasetListeners = new EventListenerList();
    private transient EventListenerList overlaysDatasetListeners = new EventListenerList();

    public void addIndicatorsDatasetListeners(DatasetListener listener)
    {
        if (indicatorsDatasetListeners == null)
        {
            indicatorsDatasetListeners = new EventListenerList();
        }
        indicatorsDatasetListeners.add(DatasetListener.class, listener);
    }

    public void removeIndicatorsDatasetListeners(DatasetListener listener)
    {
        if (indicatorsDatasetListeners == null)
        {
            indicatorsDatasetListeners = new EventListenerList();
            return;
        }
        indicatorsDatasetListeners.remove(DatasetListener.class, listener);
    }

    public void removeAllIndicatorsDatasetListeners()
    {
        if (indicatorsDatasetListeners == null)
        {
            indicatorsDatasetListeners = new EventListenerList();
            return;
        }
        DatasetListener[] listeners = indicatorsDatasetListeners.getListeners(DatasetListener.class);
        for (int i = 0; i < listeners.length; i++)
        {
            indicatorsDatasetListeners.remove(DatasetListener.class, listeners[i]);
        }
    }

    public void addOverlaysDatasetListeners(DatasetListener listener)
    {
        if (overlaysDatasetListeners == null)
        {
            overlaysDatasetListeners = new EventListenerList();
        }
        overlaysDatasetListeners.add(DatasetListener.class, listener);
    }

    public void removeOverlaysDatasetListeners(DatasetListener listener)
    {
        if (overlaysDatasetListeners == null)
        {
            overlaysDatasetListeners = new EventListenerList();
            return;
        }
        overlaysDatasetListeners.remove(DatasetListener.class, listener);
    }

    public void removeAllOverlaysDatasetListeners()
    {
        if (overlaysDatasetListeners == null)
        {
            overlaysDatasetListeners = new EventListenerList();
            return;
        }
        DatasetListener[] listeners = overlaysDatasetListeners.getListeners(DatasetListener.class);
        for (int i = 0; i < listeners.length; i++)
        {
            overlaysDatasetListeners.remove(DatasetListener.class, listeners[i]);
        }
    }

    public boolean fireDatasetEvent(DatasetEvent evt)
    {
        DatasetListener[] listeners;

        if (indicatorsDatasetListeners != null)
        {
            listeners = indicatorsDatasetListeners.getListeners(DatasetListener.class);
            for (int i = 0; i < listeners.length; i++)
            {
                listeners[i].datasetChanged(evt);
            }
        }

        if (overlaysDatasetListeners != null)
        {
            listeners = overlaysDatasetListeners.getListeners(DatasetListener.class);
            for (int i = 0; i < listeners.length; i++)
            {
                listeners[i].datasetChanged(evt);
            }
        }

        return true;
    }
}
