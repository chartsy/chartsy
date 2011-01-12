package org.chartsy.volume;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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
 * @author viorel.gheba
 */
public class Volume 
        extends Indicator
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public static final String VOLUME = "volume";
	public static final String SMA = "sma";
    private IndicatorProperties properties;

    public Volume()
    {
        super();
        properties = new IndicatorProperties();
    }

    public String getName()
    { return "Volume"; }

    public String getLabel()
    { return properties.getLabel(); }

    public String getPaintedLabel(ChartFrame cf)
    {
        DecimalFormat df = new DecimalFormat("###,###");
        String factor = df.format((int) getVolumeFactor(cf));
        return getLabel() + " x " + factor;
    }

    public Indicator newInstance()
    { return new Volume(); }

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

    @Override
    public Range getRange(ChartFrame cf)
    {
        Range range = super.getRange(cf);
        range = new Range(0, range.getUpperBound());
        return range;
    }

    public void paint(Graphics2D g, ChartFrame cf, Rectangle bounds) {
        Dataset d = visibleDataset(cf, VOLUME);
		Dataset sma = visibleDataset(cf, SMA);
        if (d != null) {
            if (maximized)
            {
                Range range = getRange(cf);
                
                DefaultPainter.bar(g, cf, range, bounds, d, properties.getColor());

				if (sma != null)
				{
					DefaultPainter.line(
						g, cf, range, bounds, sma,
						properties.getSmaColor(), properties.getSmaStroke()); // paint sma line
				}
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

    public boolean hasZeroLine()
    { return true; }

    public boolean getZeroLineVisibility()
    { return properties.getZeroLineVisibility(); }

    public Color getZeroLineColor()
    { return properties.getZeroLineColor(); }

    public Stroke getZeroLineStroke()
    { return properties.getZeroLineStroke(); }

    public boolean hasDelimiters()
    { return false; }

    public boolean getDelimitersVisibility()
    { return false; }

    public double[] getDelimitersValues()
    { return new double[] {}; }

    public Color getDelimitersColor()
    { return null; }

    public Stroke getDelimitersStroke()
    { return null; }

    public Color[] getColors()
    { return new Color[] {properties.getColor(),properties.getSmaColor()}; }

    public double[] getValues(ChartFrame cf)
    {
        Dataset d = visibleDataset(cf, VOLUME);
		Dataset sma = visibleDataset(cf, SMA);
        if (d != null && sma != null)
            return new double[] {d.getLastClose(),sma.getLastClose()};
        return new double[] {};
    }

    public double[] getValues(ChartFrame cf, int i)
    {
        Dataset d = visibleDataset(cf, VOLUME);
		Dataset sma = visibleDataset(cf, SMA);
        if (d != null && sma != null)
            return new double[] {d.getCloseAt(i),sma.getCloseAt(i)};
        return new double[] {};
    }

    public boolean getMarkerVisibility()
    { return properties.getMarker(); }

    public AbstractNode getNode()
    { return new IndicatorNode(properties); }

    @Override
    public Double[] getPriceValues(ChartFrame cf)
    {
        List<Double> list = new ArrayList<Double>();

        Range range = getRange(cf);
        int max = (int) range.getUpperBound();

        list.add(new Double((double)max));
        list.add(new Double((double)max/4));
        list.add(new Double((double)max/2));
        list.add(new Double((double)(3*max)/4));

        return list.toArray(new Double[list.size()]);
    }

    private double getVolumeFactor(ChartFrame cf)
    { return Math.pow(10, String.valueOf(Math.round(cf.getChartData().getVisible().getMax(Dataset.VOLUME_PRICE))).length() - 1); }

}
