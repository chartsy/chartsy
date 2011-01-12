package org.chartsy.nvi;

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
import org.chartsy.main.utils.StrokeGenerator;
import org.openide.nodes.AbstractNode;

/**
 *
 * @author Viorel
 */
public class NormalizedVolatilityIndicator
        extends Indicator
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public static final String NVI = "nvi";
    public static final String MA65 = "ma65";
    public static final String MA200 = "ma200";

    private IndicatorProperties properties;

    public NormalizedVolatilityIndicator()
    {
        super();
        properties = new IndicatorProperties();
    }

    public String getName()
    {
        return "Normalized Volatility Indicator";
    }

    public String getLabel()
    {
        return properties.getLabel() + " (" + properties.getPeriod() + ")";
    }

    public String getPaintedLabel(ChartFrame cf)
    {
        return getLabel();
    }

    public Indicator newInstance()
    {
        return new NormalizedVolatilityIndicator();
    }

    public LinkedHashMap getHTML(ChartFrame cf, int i)
    {
        LinkedHashMap ht = new LinkedHashMap();

        DecimalFormat df = new DecimalFormat("#,##0.00");
        double[] values = getValues(cf, i);
        String[] labels = {"NVI:","MA (65):","MA (200):"};

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

    public @Override Range getRange(ChartFrame cf)
    {
        Dataset vpiDataset = visibleDataset(cf, NVI);
        Dataset ma65Dataset = visibleDataset(cf, MA65);
        Dataset ma200Dataset = visibleDataset(cf, MA200);

        double min = vpiDataset.getCloseAt(0);
        double max = vpiDataset.getCloseAt(0);

        for (int i = 0; i < vpiDataset.getItemsCount(); i++)
        {
            if (vpiDataset.getDataItem(i) != null)
            {
                if (vpiDataset.getCloseAt(i) > 0)
                {
                    max = Math.max(max, vpiDataset.getCloseAt(i));
                    min = Math.min(min, vpiDataset.getCloseAt(i));
                }
            }
            if (ma65Dataset.getDataItem(i) != null)
            {
                if (ma65Dataset.getCloseAt(i) > 0)
                {
                    max = Math.max(max, ma65Dataset.getCloseAt(i));
                    min = Math.min(min, ma65Dataset.getCloseAt(i));
                }
            }
            if (ma200Dataset.getDataItem(i) != null)
            {
                if (ma200Dataset.getCloseAt(i) > 0)
                {
                    max = Math.max(max, ma200Dataset.getCloseAt(i));
                    min = Math.min(min, ma200Dataset.getCloseAt(i));
                }
            }
        }

        if (min > 1.343)
        {
            min = 1.3;
        }

        if (max < 1.343)
        {
            max = 1.4;
        }

        return new Range(min, max);
    }

    public void paint(Graphics2D g, ChartFrame cf, Rectangle bounds)
    {
        Dataset vpiDataset = visibleDataset(cf, NVI);
        Dataset ma65Dataset = visibleDataset(cf, MA65);
        Dataset ma200Dataset = visibleDataset(cf, MA200);

        if (vpiDataset != null)
        {
            if (maximized)
            {
                Range range = getRange(cf);
                DefaultPainter.line(g, cf, range, bounds, vpiDataset, properties.getColor(), properties.getStroke());

                if (ma65Dataset != null && properties.getMA65Visibility())
                {
                    DefaultPainter.line(g, cf, range, bounds, ma65Dataset, properties.getMA65Color(), properties.getMA65Stroke());
                }

                if (ma200Dataset != null && properties.getMA200Visibility())
                {
                    DefaultPainter.line(g, cf, range, bounds, ma200Dataset, properties.getMA200Color(), properties.getMA200Stroke());
                }
            }
        }
    }

    public void calculate()
    {
        Dataset mainDataset = getDataset();

        if (mainDataset != null && !mainDataset.isEmpty())
        {
            Dataset vpiDataset = getVPIDataset(getDataset());
            addDataset(NVI, vpiDataset);
            addDataset(MA65, Dataset.SMA(vpiDataset, properties.getMA65Period()));
            addDataset(MA200, Dataset.SMA(vpiDataset, properties.getMA200Period()));
        }
    }

    public boolean hasZeroLine()
    {
        return false;
    }

    public boolean getZeroLineVisibility()
    {
        return false;
    }

    public Color getZeroLineColor()
    {
        return null;
    }

    public Stroke getZeroLineStroke()
    {
        return null;
    }

    public boolean hasDelimiters()
    {
        return true;
    }

    public boolean getDelimitersVisibility()
    {
        return true;
    }

    public double[] getDelimitersValues()
    {
        return new double[] {new Double(1.343)};
    }

    public Color getDelimitersColor()
    {
        return new Color(0x204a87);
    }

    public Stroke getDelimitersStroke()
    {
        return StrokeGenerator.getStroke(0);
    }

    public Color[] getColors()
    {
        return new Color[]
        {
            properties.getColor(),
            properties.getMA65Color(),
            properties.getMA200Color()
        };
    }

    public double[] getValues(ChartFrame cf)
    {
        Dataset vpiDataset = visibleDataset(cf, NVI);
        Dataset ma65Dataset = visibleDataset(cf, MA65);
        Dataset ma200Dataset = visibleDataset(cf, MA200);

        int i = vpiDataset.getLastIndex();
        double[] values = new double[3];
        values[0] = vpiDataset.getDataItem(i) != null ? vpiDataset.getCloseAt(i) : 0;
        values[1] = ma65Dataset.getDataItem(i) != null ? ma65Dataset.getCloseAt(i) : 0;
        values[2] = ma200Dataset.getDataItem(i) != null ? ma200Dataset.getCloseAt(i) : 0;

        return values;
    }

    public double[] getValues(ChartFrame cf, int i)
    {
        Dataset vpiDataset = visibleDataset(cf, NVI);
        Dataset ma65Dataset = visibleDataset(cf, MA65);
        Dataset ma200Dataset = visibleDataset(cf, MA200);

        double[] values = new double[3];
        values[0] = vpiDataset.getDataItem(i) != null ? vpiDataset.getCloseAt(i) : 0;
        values[1] = ma65Dataset.getDataItem(i) != null ? ma65Dataset.getCloseAt(i) : 0;
        values[2] = ma200Dataset.getDataItem(i) != null ? ma200Dataset.getCloseAt(i) : 0;

        return values;
    }

    public boolean getMarkerVisibility()
    {
        return properties.getMarker();
    }

    public AbstractNode getNode()
    {
        return new IndicatorNode(properties);
    }


    private Dataset getVPIDataset(Dataset dataset)
    {
        int period = properties.getPeriod();
        int count = dataset.getItemsCount();

        Dataset result = Dataset.EMPTY(count);

        double[] highLow = new double[count]; // high - low
        double[] highPrevClose = new double[count]; // high - prev close
        double[] lowPrevClose = new double[count]; // low - prev close
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

                highLow[i] = high - low;
                highPrevClose[i] = high - prevClose;
                lowPrevClose[i] = low - prevClose;

                trueRange[i] = Math.max(highLow[i], Math.max(highPrevClose[i], lowPrevClose[i]));

                if (i >= period - 1)
                {
                    double close = dataset.getCloseAt(i);
                    double atrValue = 0;
                    for (int j = 0; j < period; j++)
                    {
                        atrValue += trueRange[i - j];
                    }
                    atrValue /= (double)period;

                    double value = (atrValue / close) * 100;
                    result.setDataItem(i, new DataItem(dataset.getTimeAt(i), value));
                }
            }
        }

        return result;
    }

    public @Override Double[] getPriceValues(ChartFrame cf)
    {
        Range range = getRange(cf);
        int max = (int) range.getUpperBound() + 1;

        Double[] values = new Double[max + 1];

        for (int i = 0; i < max; i++)
            values[i] = new Double((double) i);
        
        values[max] = 1.343D;
        
        return values;
    }

}
