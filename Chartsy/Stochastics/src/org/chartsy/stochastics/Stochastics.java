package org.chartsy.stochastics;

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
 * @author viorel.gheba
 */
public class Stochastics 
        extends Indicator
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public static final String SLOWD = "slow%D";
    public static final String SLOWK = "slow%K";
    public static final String FASTD = "fast%D";
    public static final String FASTK = "fast%K";
    private IndicatorProperties properties;

    public Stochastics()
    {
        super();
        properties = new IndicatorProperties();
    }

    public String getName()
    { return "Stochastic"; }

    public String getLabel()
    { return properties.getLabel() + (properties.getSF() ? " Fast" : "")
		  + " (" + properties.getPeriodK() + ", " + properties.getSmooth()
		  + ", " + properties.getPeriodD() + ")"; }

    public String getPaintedLabel(ChartFrame cf)
    { return getLabel(); }

    public Indicator newInstance() 
    { return new Stochastics(); }

    public LinkedHashMap getHTML(ChartFrame cf, int i)
    {
        LinkedHashMap ht = new LinkedHashMap();

        DecimalFormat df = new DecimalFormat("#,##0.00");
        double[] values = getValues(cf, i);
        String[] labels = {"%D:", "%K:"};

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

    @Override
    public Range getRange(ChartFrame cf) 
    { return new Range(0, 100); }

    public void paint(Graphics2D g, ChartFrame cf, Rectangle bounds)
    {
        Dataset stoD = visibleDataset(cf, properties.getSF() ? FASTD : SLOWD);
        Dataset stoK = visibleDataset(cf, properties.getSF() ? FASTK : SLOWK);

        if (stoD != null && stoK != null)
        {
            if (maximized)
            {
                Range range = getRange(cf);

                DefaultPainter.line(g, cf, range, bounds, stoD, properties.getColorD(), properties.getStrokeD());
                DefaultPainter.line(g, cf, range, bounds, stoK, properties.getColorK(), properties.getStrokeK());
            }
        }
    }

    public void calculate()
    {
        Dataset initial = getDataset();
        if (initial != null && !initial.isEmpty())
        {
            Dataset[] ds = getDataset(initial);
            addDataset(SLOWD, ds[0]);
            addDataset(SLOWK, ds[1]);
            addDataset(FASTD, ds[2]);
            addDataset(FASTK, ds[3]);
        }
    }

    public boolean hasZeroLine()
    { return false; }

    public boolean getZeroLineVisibility()
    { return false; }

    public Color getZeroLineColor()
    { return null; }

    public Stroke getZeroLineStroke()
    { return null; }

    public boolean hasDelimiters() 
    { return true; }

    public boolean getDelimitersVisibility() 
    { return true; }

    public double[] getDelimitersValues() 
    { return new double[] {20d, 50d, 80d}; }

    public Color getDelimitersColor() 
    { return new Color(0xbbbbbb); }

    public Stroke getDelimitersStroke() 
    { return StrokeGenerator.getStroke(1); }

    public Color[] getColors()
    { return new Color[] {properties.getColorD(), properties.getColorK()}; }

    public double[] getValues(ChartFrame cf)
    {
        Dataset stoD = visibleDataset(cf, properties.getSF() ? FASTD : SLOWD);
        Dataset stoK = visibleDataset(cf, properties.getSF() ? FASTK : SLOWK);

        if (stoD != null && stoK != null)
            return new double[] {stoD.getLastClose(), stoK.getLastClose()};
        return new double[] {};
    }
    public double[] getValues(ChartFrame cf, int i)
    {
        Dataset stoD = visibleDataset(cf, properties.getSF() ? FASTD : SLOWD);
        Dataset stoK = visibleDataset(cf, properties.getSF() ? FASTK : SLOWK);

        if (stoD != null && stoK != null)
            return new double[] {stoD.getCloseAt(i), stoK.getCloseAt(i)};
        return new double[] {};
    }

    public boolean getMarkerVisibility() 
    { return properties.getMarker(); }

    public AbstractNode getNode()
    { return new IndicatorNode(properties); }

    @Override
    public Double[] getPriceValues(ChartFrame cf)
    { return new Double[] {new Double(20), new Double(50), new Double(80)}; }

    private Dataset[] getDataset(final Dataset initial)
    {
        int periodK = properties.getPeriodK();
        int smooth = properties.getSmooth();
        int periodD = properties.getPeriodD();
        int count = initial.getItemsCount();

        Dataset F_K = Dataset.EMPTY(count); // fast %K

        for (int i = periodK - 1; i < count; i++)
        {
            double lk = initial.getLowAt(i);
            double hk = initial.getHighAt(i);

            for (int j = 0; j < periodK; j++)
            {
                lk = Math.min(lk, initial.getLowAt(i-j));
                hk = Math.max(hk, initial.getHighAt(i-j));
            }

            double currentFK = hk != lk ? 100 * (initial.getCloseAt(i) - lk) / (hk - lk) : 0;
            currentFK = currentFK < 0 ? 0 : currentFK;

            F_K.setDataItem(i, new DataItem(initial.getTimeAt(i), currentFK));
        }

        Dataset F_D = Dataset.SMA(F_K, periodD); // fast %D
        Dataset S_K = Dataset.SMA(F_K, smooth); // slow %K
        Dataset S_D = Dataset.SMA(S_K, periodD); // slow %D

        Dataset[] result = new Dataset[4];
        result[0] = S_D;
        result[1] = S_K;
        result[2] = F_D;
        result[3] = F_K;
        return result;
    }

}
