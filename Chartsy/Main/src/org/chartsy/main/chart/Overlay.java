package org.chartsy.main.chart;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import org.chartsy.main.ChartFrame;
import org.chartsy.main.ChartProperties;
import org.chartsy.main.data.ChartData;
import org.chartsy.main.data.Dataset;
import org.chartsy.main.events.DatasetEvent;
import org.chartsy.main.events.DatasetListener;
import org.chartsy.main.events.LogEvent;
import org.chartsy.main.events.LogListener;
import org.chartsy.main.utils.Range;
import org.openide.nodes.AbstractNode;

/**
 *
 * @author viorel.gheba
 */
public abstract class Overlay
        implements Serializable, DatasetListener, LogListener
{

    private static final long serialVersionUID = 2L;
    protected Dataset dataset;
    protected LinkedHashMap<String, Dataset> datasets;
    private boolean logarithmic = false;

    public Overlay()
    {
        datasets = new LinkedHashMap<String, Dataset>();
    }

    public boolean isLogarithmic()
    {
        return logarithmic;
    }

    public void setLogarithmic(boolean b)
    {
        logarithmic = b;
    }

    public String getFontHTML(Color color, String text)
    {
        String html = "<font color=\"" + Integer.toHexString(color.getRGB() & 0x00ffffff) + "\">" + text + "</font>";
        return html;
    }

    public Dataset getDataset()
    {
        if (logarithmic)
        {
            return Dataset.LOG(dataset);
        }
        return dataset;
    }

    public void setDataset(Dataset d)
    {
        dataset = d;
    }

    public void addDataset(String key, Dataset value)
    {
        datasets.put(key, value);
    }

    public Dataset getDataset(String key)
    {
        return datasets.get(key);
    }

    public Dataset visibleDataset(ChartFrame cf, String key)
    {
        if (datasets.containsKey(key))
        {
            Dataset d = getDataset(key);
            if (d == null)
            {
                return null;
            }

            Dataset v = d.getVisibleDataset(cf.getChartData().getPeriod(), cf.getChartData().getLast());
            return v;
        }
        return null;
    }

    public void removeDatasets()
    {
        datasets.clear();
    }

    public abstract String getName();

    public abstract String getLabel();

    public abstract Overlay newInstance();

    public abstract LinkedHashMap getHTML(ChartFrame cf, int i);

    public Range getRange(ChartFrame cf, String price)
    {
        if (datasets.isEmpty())
        {
            return new Range();
        }

        Range range = null;
        Iterator<String> it = datasets.keySet().iterator();

        while (it.hasNext())
        {
            Dataset d = visibleDataset(cf, it.next());
            double min = d.getMinNotZero(price);
            double max = d.getMaxNotZero(price);

            if (range == null)
            {
                range = new Range(min - (max - min) * 0.01, max + (max - min) * 0.01);
            } else
            {
                range = Range.combine(range, new Range(min - (max - min) * 0.01, max + (max - min) * 0.01));
            }
        }

        return range;
    }

    public abstract void paint(Graphics2D g, ChartFrame cf, Rectangle bounds);

    public abstract void calculate();

    public abstract Color[] getColors();

    public abstract double[] getValues(ChartFrame cf);

    public abstract double[] getValues(ChartFrame cf, int i);

    public abstract boolean getMarkerVisibility();

    public abstract AbstractNode getNode();

    public abstract String getPrice();

    public void datasetChanged(DatasetEvent evt)
    {
        synchronized (this)
        {
            ChartData cd = (ChartData) evt.getSource();
            setDataset(cd.getDataset(false));
            calculate();
        }
    }

    public void fire(LogEvent evt)
    {
        ChartProperties cp = (ChartProperties) evt.getSource();
        logarithmic = cp.getAxisLogarithmicFlag();
        calculate();
    }

    /**
     * If an override in the overlay class sets this to false
     * that overlay is not included in the range calculation of the chart.
     *
     * @return whether to include this overlay in chart range
     */
    public boolean isIncludedInRange()
    {
        return true;
    }
}
