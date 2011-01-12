package org.chartsy.atr;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import org.chartsy.main.ChartFrame;
import org.chartsy.main.chart.Indicator;
import org.chartsy.main.data.DataItem;
import org.chartsy.main.data.Dataset;
import org.chartsy.main.utils.DefaultPainter;
import org.chartsy.main.utils.Range;
import org.chartsy.main.utils.SerialVersion;
import org.openide.nodes.AbstractNode;

/**
 *
 * @author Viorel
 */
public class AverageTrueRange
        extends Indicator
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public static final String ATR = "atr";
    private IndicatorProperties properties;

    public AverageTrueRange()
    {
        super();
        properties = new IndicatorProperties();
    }

    public @Override String getName()
    {
        return "ATR";
    }

    public @Override String getLabel()
    {
        return properties.getLabel() + " (" + properties.getPeriod() + ")";
    }

    public @Override String getPaintedLabel(ChartFrame cf)
    {
        return getLabel();
    }

    public @Override Indicator newInstance()
    {
        return new AverageTrueRange();
    }

    public @Override LinkedHashMap getHTML(ChartFrame cf, int i)
    {
        LinkedHashMap ht = new LinkedHashMap();

        DecimalFormat df = new DecimalFormat("#,##0.00");
        double[] values = getValues(cf, i);
        String[] labels = {"ATR:"};

        ht.put(getLabel(), " ");
        if (values.length > 0) {
            Color[] colors = getColors();
            for (int j = 0; j < values.length; j++) {
                ht.put(getFontHTML(colors[j], labels[j]),
                        getFontHTML(colors[j], df.format(values[j])));
            }
        }

        return ht;
    }

    public @Override void paint(Graphics2D g, ChartFrame cf, Rectangle bounds)
    {
        Dataset atrDataset = visibleDataset(cf, ATR);

        if (atrDataset != null)
        {
            if (maximized)
            {
                Range range = getRange(cf);
                DefaultPainter.line(g, cf, range, bounds, atrDataset, properties.getColor(), properties.getStroke());
            }
        }
    }

    public @Override void calculate()
    {
        Dataset mainDataset = getDataset();

        if (mainDataset != null && !mainDataset.isEmpty())
        {
            addDataset(ATR, getATRDataset(mainDataset));
        }
    }

    public @Override boolean hasZeroLine()
    {
        return false;
    }

    public @Override boolean getZeroLineVisibility()
    {
        return false;
    }

    public @Override Color getZeroLineColor()
    {
        return null;
    }

    public @Override Stroke getZeroLineStroke()
    {
        return null;
    }

    public @Override boolean hasDelimiters()
    {
        return false;
    }

    public @Override boolean getDelimitersVisibility()
    {
        return false;
    }

    public @Override double[] getDelimitersValues()
    {
        return new double[] {};
    }

    public @Override Color getDelimitersColor()
    {
        return null;
    }

    public @Override Stroke getDelimitersStroke()
    {
        return null;
    }

    public @Override Color[] getColors()
    {
        return new Color[]
        {
            properties.getColor()
        };
    }

    public @Override double[] getValues(ChartFrame cf)
    {
        Dataset atrDataset = visibleDataset(cf, ATR);

        int i = atrDataset.getLastIndex();

        if (atrDataset.getDataItem(i) != null)
            return new double[]
            {
                atrDataset.getCloseAt(i)
            };
        
        return new double[] {0};
    }

    public @Override double[] getValues(ChartFrame cf, int i)
    {
        Dataset atrDataset = visibleDataset(cf, ATR);

        if (atrDataset.getDataItem(i) != null)
            return new double[]
            {
                atrDataset.getCloseAt(i)
            };
        
        return new double[] {0};
    }

    public @Override boolean getMarkerVisibility()
    {
        return properties.getMarker();
    }

    public @Override AbstractNode getNode()
    {
        return new IndicatorNode(properties);
    }

    private Dataset getATRDataset(Dataset dataset)
    {
        int period = properties.getPeriod();
        int count = dataset.getItemsCount();

        Dataset result = Dataset.EMPTY(count);
        double[] trueRange = new double[count]; // daily TR

        for (int i = 0; i < count; i++)
        {
            double high = dataset.getHighAt(i);
            double low = dataset.getLowAt(i);
            if (i == 0)
            {
                trueRange[i] = high - low; // first TR value = high - low
            }
            else
            {
                double prevClose = dataset.getCloseAt(i - 1);
                trueRange[i] = Math.max( Math.abs( low - prevClose ), Math.max( high - low, Math.abs( high - prevClose ) ) );

                if (i >= period - 1)
                {
                    double atrValue = 0;
                    for (int j = 0; j < period; j++)
                    {
                        atrValue += trueRange[i - j];
                    }
                    atrValue /= (double)period;

                    result.setDataItem(i, new DataItem(dataset.getTimeAt(i), atrValue));
                }
            }
        }

        return result;
    }

    public @Override Double[] getPriceValues(ChartFrame cf)
    {
        Range range = getRange(cf);

        if (range.getUpperBound() < 1)
        {
            double max = Math.ceil(range.getUpperBound() * 10) / 10;
            double min = Math.floor(range.getLowerBound() * 10) / 10;

            double diff = (max - min) * 100;
            int count = (int) (diff / 10);
            Double[] values = new Double[count];
            for (int i = 0; i < count; i++)
                values[i] = new Double(min + ((double) i)/10);
            
            return values;
        }
        else if (range.getUpperBound() > 1 && range.getUpperBound() < 10)
        {
            double max = Math.ceil(range.getUpperBound());
            double step = 0.5;

            int count = (int) (max / step) + 1;
            Double[] values = new Double[count];
            for (int i = 0; i < count; i++)
                values[i] = new Double((double) (i * step));

            return values;
        }
        else
        {
            double max = Math.ceil(range.getUpperBound());
            int step = 5;
            int count = (int) (max / step) + 1;

            Double[] values = new Double[count];
            for (int i = 0; i < count; i++)
                values[i] = new Double((double) (i * step));

            return values;
        }
    }

}
