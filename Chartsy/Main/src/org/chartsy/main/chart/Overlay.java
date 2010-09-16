package org.chartsy.main.chart;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.chartsy.main.ChartFrame;
import org.chartsy.main.ChartProperties;
import org.chartsy.main.data.ChartData;
import org.chartsy.main.data.Dataset;
import org.chartsy.main.events.DatasetEvent;
import org.chartsy.main.events.DatasetListener;
import org.chartsy.main.events.LogEvent;
import org.chartsy.main.events.LogListener;
import org.chartsy.main.utils.Range;
import org.chartsy.main.utils.SerialVersion;
import org.chartsy.main.utils.XMLUtil;
import org.chartsy.main.utils.XMLUtil.XMLTemplate;
import org.openide.nodes.AbstractNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author viorel.gheba
 */
public abstract class Overlay
        implements Serializable, DatasetListener, LogListener, XMLTemplate
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;
    
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

	public void saveToTemplate(Document document, Element element)
	{
		AbstractPropertiesNode node = (AbstractPropertiesNode) getNode();
		AbstractPropertyListener listener = node.getAbstractPropertyListener();
		Field[] fields = listener.getClass().getDeclaredFields();
		for (Field field : fields)
		{
			try
			{
				field.setAccessible(true);
				if (field.getModifiers() == Modifier.PRIVATE)
					XMLUtil.addProperty(document, element, field.getName(), field.get(listener));
			} 
			catch (Exception ex)
			{
				Logger.getLogger(getName()).log(Level.SEVERE, "", ex);
			}
		}
	}

	public void loadFromTemplate(Element element)
	{
		AbstractPropertiesNode node = (AbstractPropertiesNode) getNode();
		AbstractPropertyListener listener = node.getAbstractPropertyListener();
		Field[] fields = listener.getClass().getDeclaredFields();
		for (Field field : fields)
		{
			try
			{
				field.setAccessible(true);
				if (field.getModifiers() == Modifier.PRIVATE) 
				{
					if (field.getType().equals(String.class))
						field.set(listener, XMLUtil.getStringProperty(element, field.getName()));
					else if (field.getType().equals(int.class))
						field.set(listener, XMLUtil.getIntegerProperty(element, field.getName()));
					else if (field.getType().equals(double.class))
						field.set(listener, XMLUtil.getDoubleProperty(element, field.getName()));
					else if (field.getType().equals(float.class))
						field.set(listener, XMLUtil.getFloatProperty(element, field.getName()));
					else if (field.getType().equals(boolean.class))
						field.set(listener, XMLUtil.getBooleanProperty(element, field.getName()));
					else if (field.getType().equals(Color.class))
						field.set(listener, XMLUtil.getColorProperty(element, field.getName()));
				}
			}
			catch (Exception ex)
			{
				Logger.getLogger(getName()).log(Level.SEVERE, "", ex);
			}
		}
	}
    
}
