package org.chartsy.hullmovingaverage;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import org.chartsy.main.ChartFrame;
import org.chartsy.main.chart.Overlay;
import org.chartsy.main.data.Dataset;
import org.chartsy.main.utils.DefaultPainter;
import org.chartsy.main.utils.Range;
import org.chartsy.main.utils.SerialVersion;
import org.openide.nodes.AbstractNode;

public class HullMovingAverage
        extends Overlay
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;
    public static final String HMA = "hma";
    private OverlayProperties properties;

    public HullMovingAverage()
    {
        super();
        properties = new OverlayProperties();
    }

    public String getName()
    {
        return "Hull Moving Average";
    }

    public String getLabel()
    {
        return properties.getLabel() + " (" + properties.getPrice() + ", " + properties.getPeriod() + ")";
    }

    public Overlay newInstance()
    {
        return new HullMovingAverage();
    }

    public LinkedHashMap getHTML(ChartFrame cf, int i)
    {
        LinkedHashMap ht = new LinkedHashMap();

        DecimalFormat df = new DecimalFormat("#,##0.00");
        double[] values = getValues(cf, i);

        ht.put(getLabel(), " ");
        if (values.length > 0)
        {
            Color[] colors = getColors();
            for (int j = 0; j < values.length; j++)
            {
                ht.put(getFontHTML(colors[j], "HMA:"),
                        getFontHTML(colors[j], df.format(values[j])));
            }
        }

        return ht;
    }

    public void paint(Graphics2D g, ChartFrame cf, Rectangle bounds)
    {
        Dataset sma = visibleDataset(cf, HMA);
        if (sma != null)
        {
            Range range = cf.getSplitPanel().getChartPanel().getRange();
            DefaultPainter.line(g, cf, range, bounds, sma, properties.getColor(), properties.getStroke(), Dataset.getPrice(properties.getPrice()));
        }
    }

    public void calculate()
    {
        Dataset initial = getDataset();
        if (initial != null && !initial.isEmpty())
        {
            int period = properties.getPeriod();
            Dataset sma = Dataset.HMA(initial, period);
            addDataset(HMA, sma);
        }
    }

    public Color[] getColors()
    {
        return new Color[]
                {
                    properties.getColor()
                };
    }

    public double[] getValues(ChartFrame cf)
    {
        Dataset hma = visibleDataset(cf, HMA);
        if (hma != null)
        {
            int price = Dataset.getPrice(properties.getPrice());
            return new double[]
                    {
                        hma.getLastPrice(price)
                    };
        }
        return new double[]
                {
                };
    }

    public double[] getValues(ChartFrame cf, int i)
    {
        Dataset hma = visibleDataset(cf, HMA);
        if (hma != null)
        {
            String price = properties.getPrice();
            return new double[]
                    {
                        hma.getPriceAt(i, price)
                    };
        }
        return new double[]
                {
                };
    }

    public boolean getMarkerVisibility()
    {
        return properties.getMarker();
    }

    public AbstractNode getNode()
    {
        return new OverlayNode(properties);
    }

    public String getPrice()
    {
        return properties.getPrice();
    }
}
