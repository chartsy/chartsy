package org.chartsy.main.chart;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.chartsy.main.ChartFrame;
import org.chartsy.main.ChartFrameAdapter;
import org.chartsy.main.data.ChartData;
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
public abstract class Indicator extends ChartFrameAdapter
        implements Serializable, XMLTemplate
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;
    public static final int DEFAULT_HEIGHT = 150;
    protected String datasetKey;
    protected HashMap<String, Dataset> datasets;
    protected boolean maximized = true;
    private int maximizedHeight = DEFAULT_HEIGHT;
	protected boolean active = true;

    public Indicator()
    {
        datasets = new HashMap<String, Dataset>();
    }

    public void setMaximizedHeight(int height)
    {
        this.maximizedHeight = height;
    }

    public int getMaximizedHeight()
    {
        return this.maximizedHeight;
    }

    public String getFontHTML(Color color, String text)
    {
        String html = "<font color=\"" + Integer.toHexString(color.getRGB() & 0x00ffffff) + "\">" + text + "</font>";
        return html;
    }

    public boolean isMaximized()
    {
        return maximized;
    }

    public void setMaximized(boolean b)
    {
        maximized = b;
    }

    protected Dataset getDataset()
    {
        return DatasetUsage.getInstance().getDatasetFromMemory(datasetKey);
    }

    public void setDatasetKey(String datasetKey)
    {
        this.datasetKey = datasetKey;
    }

    protected void addDataset(String key, Dataset value)
    {
		datasets.put(key, value);
    }

    protected Dataset getDataset(String key)
    {
		return datasets.get(key);
    }

	protected boolean datasetExists(String key)
	{
		return datasets.containsKey(key);
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

    public void clearDatasets()
    {
		datasets.clear();
    }

    public abstract String getName();
    public abstract String getLabel();
    public abstract String getPaintedLabel(ChartFrame cf);
    public abstract Indicator newInstance();
    public abstract LinkedHashMap getHTML(ChartFrame cf, int i);

    public Range getRange(ChartFrame cf)
    {
		if (datasets.values().isEmpty())
		{
			return new Range();
		}

		Range range = null;
		Iterator<String> it = datasets.keySet().iterator();

		while (it.hasNext())
		{
			Dataset d = visibleDataset(cf, it.next());

			double min = d.getMin(Dataset.CLOSE_PRICE);
			double max = d.getMax(Dataset.CLOSE_PRICE);

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
    public abstract boolean hasZeroLine();
    public abstract boolean getZeroLineVisibility();
    public abstract Color getZeroLineColor();
    public abstract Stroke getZeroLineStroke();
    public abstract boolean hasDelimiters();
    public abstract boolean getDelimitersVisibility();
    public abstract double[] getDelimitersValues();
    public abstract Color getDelimitersColor();
    public abstract Stroke getDelimitersStroke();
    public abstract Color[] getColors();
    public abstract double[] getValues(ChartFrame cf);
    public abstract double[] getValues(ChartFrame cf, int i);
    public abstract boolean getMarkerVisibility();
    public abstract AbstractNode getNode();

    public boolean paintValues()
    {
        return true;
    }

    public Double[] getPriceValues(ChartFrame cf)
    {
        List<Double> list = new ArrayList<Double>();
        if (!hasZeroLine())
        {
            list.add(new Double(0));
        }

        Range range = getRange(cf);
        int max = (int) range.getUpperBound();
        if (max > 0)
        {
            max = max - max % 10;
        } else
        {
            int i = Math.abs(max % 10);
            max = max - (5 - i);
        }

        int min = (int) Math.ceil(range.getLowerBound());
        if (min > 0)
        {
            int i = min % 10;
            min = min + (i < 5 ? 5 - i : 10 - i);
        } else
        {
            int i = Math.abs(min % 10);
            min = min + (i < 5 ? i : i - 5);
        }

        list.add(new Double((double) min));
        list.add(new Double((double) max));

        if (Math.abs(max) > Math.abs(min))
        {
            list.add(new Double(max / 2));
        } else
        {
            list.add(new Double(min / 2));
        }

        Double[] retval = list.toArray(new Double[list.size()]);
        list = null;
        return retval;
    }

    protected void paintFill(Graphics2D g, ChartFrame cf, Dataset dataset, Rectangle bounds, Range range, Color color, double f, double t, boolean upper)
    {
        double min = Math.min(f, t);
        double max = Math.max(f, t);

        if (upper)
        {
            max = Math.max(max, range.getUpperBound());
        } else
        {
            min = Math.max(f, t);
            max = Math.min(f, t);
        }

        ChartData cd = cf.getChartData();
        int count = dataset.getItemsCount();

        double x, dx, y = cd.getY(min, bounds, range, false);
        Range newRange = new Range(min, max);

        g.setColor(color);
        for (int i = 1; i < count; i++)
        {
            if (dataset.getDataItem(i - 1) != null && dataset.getDataItem(i) != null)
            {
                double value1 = dataset.getCloseAt(i - 1);
                double value2 = dataset.getCloseAt(i);

                Point2D p1 = cd.getPoint(i - 1, value1, range, bounds, false);
                Point2D p2 = cd.getPoint(i, value2, range, bounds, false);

                if (!newRange.contains(value1) && newRange.contains(value2))
                {
                    dx = (y - p1.getY()) / (p2.getY() - p1.getY());
                    x = p1.getX() + dx * (p2.getX() - p1.getX());

                    GeneralPath gp = new GeneralPath();
                    gp.moveTo((float) x, (float) y);
                    gp.lineTo((float) p2.getX(), (float) p2.getY());
                    gp.lineTo((float) p2.getX(), (float) y);
                    gp.closePath();
                    g.fill(gp);
                } else if (newRange.contains(value1) && newRange.contains(value2))
                {
                    GeneralPath gp = new GeneralPath();
                    gp.moveTo((float) p1.getX(), (float) y);
                    gp.lineTo((float) p1.getX(), (float) p1.getY());
                    gp.lineTo((float) p2.getX(), (float) p2.getY());
                    gp.lineTo((float) p2.getX(), (float) y);
                    gp.closePath();
                    g.fill(gp);
                } else if (newRange.contains(value1) && !newRange.contains(value2))
                {
                    dx = (y - p1.getY()) / (p2.getY() - p1.getY());
                    x = p1.getX() + dx * (p2.getX() - p1.getX());

                    GeneralPath gp = new GeneralPath();
                    gp.moveTo((float) p1.getX(), (float) p1.getY());
                    gp.lineTo((float) x, (float) y);
                    gp.lineTo((float) p1.getX(), (float) y);
                    gp.closePath();
                    g.fill(gp);
                }
            }
        }
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
                {
                    XMLUtil.addProperty(document, element, field.getName(), field.get(listener));
                }
            } catch (Exception ex)
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
						{
							field.set(listener, XMLUtil.getStringProperty(element, field.getName()));
						} else if (type.equals(int.class))
						{
							field.set(listener, XMLUtil.getIntegerProperty(element, field.getName()));
						} else if (type.equals(double.class))
						{
							field.set(listener, XMLUtil.getDoubleProperty(element, field.getName()));
						} else if (type.equals(float.class))
						{
							field.set(listener, XMLUtil.getFloatProperty(element, field.getName()));
						} else if (type.equals(boolean.class))
						{
							field.set(listener, XMLUtil.getBooleanProperty(element, field.getName()));
						} else if (type.equals(Color.class))
						{
							field.set(listener, XMLUtil.getColorProperty(element, field.getName()));
						}
					}
                }
            } catch (Exception ex)
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
