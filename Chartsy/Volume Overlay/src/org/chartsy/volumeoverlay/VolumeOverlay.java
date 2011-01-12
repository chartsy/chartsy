package org.chartsy.volumeoverlay;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import org.chartsy.main.ChartFrame;
import org.chartsy.main.chart.Overlay;
import org.chartsy.main.data.DataItem;
import org.chartsy.main.data.Dataset;
import org.chartsy.main.utils.ColorGenerator;
import org.chartsy.main.utils.DefaultPainter;
import org.chartsy.main.utils.Range;
import org.chartsy.main.utils.SerialVersion;
import org.openide.nodes.AbstractNode;

public class VolumeOverlay
        extends Overlay
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;
    
    public static final String VOLUME = "volume";
	public static final String SMA = "sma";
    private OverlayProperties properties;

    public VolumeOverlay()
    {
        super();
        properties = new OverlayProperties();
    }

    public String getName()
    {
        return "Volume";
    }

    public String getLabel()
    {
        return properties.getLabel();
    }

    public String getPaintedLabel(ChartFrame cf)
    {
        DecimalFormat df = new DecimalFormat("###,###");
        String factor = df.format((int) getVolumeFactor(cf));
        return getLabel() + " x " + factor;
    }

    public Overlay newInstance()
    {
        return new VolumeOverlay();
    }

    public LinkedHashMap getHTML(ChartFrame cf, int i)
    {
        LinkedHashMap ht = new LinkedHashMap();

        DecimalFormat df = new DecimalFormat();
        df.applyPattern("###,###");
        String factor = df.format((int) getVolumeFactor(cf));
        df.applyPattern("###,##0.00");
		String[] labels = { "Volume:", "VolumeMA:" };
        double[] values = getValues(cf, i);

        ht.put(getLabel() + " x " + factor, " ");
        if (values.length > 0)
        {
            Color[] colors = getColors();
            for (int j = 0; j < values.length; j++)
            {
                ht.put(getFontHTML(colors[j], labels[j]),
                        getFontHTML(colors[j], df.format(values[j])));
            }
        }

        return ht;
    }

    public void paint(Graphics2D g, ChartFrame cf, Rectangle bounds)
    {
        Dataset d = visibleDataset(cf, VOLUME);
		Dataset sma = visibleDataset(cf, SMA);
        if (d != null)
        {
            Rectangle rect = (Rectangle) bounds.clone();
            Range range = getRange(cf, VOLUME);
            int height =  rect.getSize().height/4;
            rect.setLocation(rect.getLocation().x, rect.getLocation().y+height*3);
            rect.setSize(rect.getSize().width, height);
            Color colorVolume = ColorGenerator.getTransparentColor(properties.getColor(), properties.getAlpha());
            DefaultPainter.bar(g, cf, range, rect, d, colorVolume);
			
			if (sma != null)
			{
				Color colorSma = ColorGenerator.getTransparentColor(properties.getSmaColor(), properties.getAlpha());
				DefaultPainter.line(
					g, cf, range, rect, sma,
					colorSma, properties.getSmaStroke()); // paint sma line
			}
        }
    }

    public void calculate()
    {
        Dataset initial = getDataset();
        if (initial != null && !initial.isEmpty())
        {
            Range range = new Range(0, initial.getMax(Dataset.VOLUME_PRICE));
            double factor = Math.pow(10, String.valueOf(Math.round(range.getUpperBound())).length() - 2);
            int count = initial.getItemsCount();
            Dataset d = Dataset.EMPTY(count);
            for (int i = 0; i < count; i++)
            {
                d.setDataItem(i, new DataItem(initial.getTimeAt(i), initial.getVolumeAt(i) / factor));
            }
            addDataset(VOLUME, d);
			addDataset(SMA, Dataset.SMA(d, properties.getSmaPeriod()));
        }
    }

    public Color[] getColors()
    {
        return new Color[]
                {
                    properties.getColor(),
					properties.getSmaColor()
                };
    }

    public double[] getValues(ChartFrame cf)
    {
        Dataset d = visibleDataset(cf, VOLUME);
		Dataset sma = visibleDataset(cf, SMA);
        if (d != null && sma != null)
        {
            return new double[]
                    {
                        d.getLastClose(),
						sma.getLastClose()
                    };
        }
        return new double[]
                {
                };
    }

    public double[] getValues(ChartFrame cf, int i)
    {
        Dataset d = visibleDataset(cf, VOLUME);
		Dataset sma = visibleDataset(cf, SMA);
        if (d != null)
        {
            return new double[]
                    {
                        d.getCloseAt(i),
						sma.getCloseAt(i)
                    };
        }
        return new double[]
                {
                };
    }

    public boolean getMarkerVisibility()
    {
        return false;
    }

    public AbstractNode getNode()
    {
        return new OverlayNode(properties);
    }

    private double getVolumeFactor(ChartFrame cf)
    {
        return Math.pow(10, String.valueOf(Math.round(cf.getChartData().getVisible().getMax(Dataset.VOLUME_PRICE))).length() - 1);
    }

    public String getPrice()
    {
        return VOLUME;
    }

    @Override
    public boolean isIncludedInRange()
    {
        return false;
    }

}
