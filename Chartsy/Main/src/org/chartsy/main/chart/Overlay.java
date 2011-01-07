package org.chartsy.main.chart;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.chartsy.main.ChartFrame;
import org.chartsy.main.ChartFrameAdapter;
import org.chartsy.main.data.Dataset;
import org.chartsy.main.managers.DatasetUsage;
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
public abstract class Overlay extends ChartFrameAdapter
        implements Serializable, XMLTemplate
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;
    
    protected String datasetKey;
	protected ConcurrentHashMap<String, Dataset> datasets;
	protected boolean active = true;

    public Overlay()
    {
		datasets = new ConcurrentHashMap<String, Dataset>();
    }

    public String getFontHTML(Color color, String text)
    {
        String html = "<font color=\"" + Integer.toHexString(color.getRGB() & 0x00ffffff) + "\">" + text + "</font>";
        return html;
    }

    public Dataset getDataset()
    {
        return DatasetUsage.getInstance().getDatasetFromMemory(datasetKey);
    }

    public void setDatasetKey(String datasetKey)
    {
        this.datasetKey = datasetKey;
    }

	public void clearDatasets()
	{
		datasets.clear();
	}

    public void addDataset(String key, Dataset value)
    {
		datasets.put(key, value);
    }

    public Dataset getDataset(String key)
    {
		return datasets.get(key);
    }

	private boolean datasetExists(String key)
	{
		return datasets.containsKey(key);
	}

    public Dataset visibleDataset(ChartFrame cf, String key)
    {
        if (datasetExists(key))
        {
            Dataset dataset = getDataset(key);
            if (dataset == null)
            {
                return null;
            }

			int period = cf.getChartData().getPeriod();
			int last = cf.getChartData().getLast();
            Dataset visible = dataset.getVisibleDataset(period, last);
            return visible;
        }
        return null;
    }

    public abstract String getName();

    public abstract String getLabel();

    public abstract Overlay newInstance();

    public abstract LinkedHashMap getHTML(ChartFrame cf, int i);

    public Range getRange(ChartFrame cf, String price)
    {
        Range range = null;
		String[] keys = datasets.keySet().toArray(new String[datasets.size()]);
        for (String key : keys)
        {
            Dataset dataset = visibleDataset(cf, key);
            double min = dataset.getMinNotZero(price);
            double max = dataset.getMaxNotZero(price);
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
					if (XMLUtil.elementExists(element, field.getName()))
					{
						Class type = field.getType();
						if (type.equals(String.class))
							field.set(listener, XMLUtil.getStringProperty(element, field.getName()));
						else if (type.equals(int.class))
							field.set(listener, XMLUtil.getIntegerProperty(element, field.getName()));
						else if (type.equals(double.class))
							field.set(listener, XMLUtil.getDoubleProperty(element, field.getName()));
						else if (type.equals(float.class))
							field.set(listener, XMLUtil.getFloatProperty(element, field.getName()));
						else if (type.equals(boolean.class))
							field.set(listener, XMLUtil.getBooleanProperty(element, field.getName()));
						else if (type.equals(Color.class))
							field.set(listener, XMLUtil.getColorProperty(element, field.getName()));
					}
				}
			}
			catch (Exception ex)
			{
				Logger.getLogger(getName()).log(Level.SEVERE, "", ex);
			}
		}
	}

	public void setActive(boolean active)
	{
		this.active = active;
	}

	public boolean isActive()
	{
		return active;
	}

	@Override
	public void datasetKeyChanged(String datasetKey)
	{
		setDatasetKey(datasetKey);
		calculate();
	}
    
}
