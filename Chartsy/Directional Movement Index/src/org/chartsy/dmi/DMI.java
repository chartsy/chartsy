package org.chartsy.dmi;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import org.chartsy.main.ChartFrame;
import org.chartsy.main.chart.Indicator;
import org.chartsy.main.data.Dataset;
import org.chartsy.main.utils.DefaultPainter;
import org.chartsy.main.utils.Range;
import org.chartsy.main.utils.SerialVersion;
import org.openide.nodes.AbstractNode;

/**
 *
 * @author viorel.gheba
 */
public class DMI 
        extends Indicator
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public static final String PDI = "pdi";
    public static final String MDI = "mdi";
    public static final String ADX = "adx";
    
    private IndicatorProperties properties;

    public DMI()
    {
        super();
        properties = new IndicatorProperties();
    }

    public String getName() 
    { return "DMI"; }

    public String getLabel()
    { return properties.getLabel() + "(" + properties.getPeriod() + ")"; }

    public String getPaintedLabel(ChartFrame cf)
    { return getLabel(); }

    public Indicator newInstance()
    { return new DMI(); }

    public LinkedHashMap getHTML(ChartFrame cf, int i)
    {
        LinkedHashMap ht = new LinkedHashMap();

        DecimalFormat df = new DecimalFormat("#,##0.00");
        double[] values = getValues(cf, i);
        String[] labels = {"DI+:", "DI-:", "ADX:"};

        ht.put(getLabel(), " ");
        if (values.length > 0) {
            Color[] colors = getColors();
            for (int j = 0; j < values.length; j++) {
                ht.put(getFontHTML(colors[j], labels[j]), getFontHTML(colors[j], df.format(values[j])));
            }
        }

        return ht;
    }

    public @Override Range getRange(ChartFrame cf)
    {
        Range range = super.getRange(cf);
        range = new Range(0, range.getUpperBound());
        return range;
    }

    public void paint(Graphics2D g, ChartFrame cf, Rectangle bounds)
    {
        Dataset pdi = visibleDataset(cf, PDI);
        Dataset mdi = visibleDataset(cf, MDI);
        Dataset adx = visibleDataset(cf, ADX);

        if (pdi != null && mdi != null && adx != null)
        {
            if (maximized)
            {
                Range range = getRange(cf);

                DefaultPainter.line(g, cf, range, bounds, pdi, properties.getPDIColor(), properties.getPDIStroke()); // paint PDI
                DefaultPainter.line(g, cf, range, bounds, mdi, properties.getMDIColor(), properties.getMDIStroke()); // paint MDI
                DefaultPainter.line(g, cf, range, bounds, adx, properties.getADXColor(), properties.getADXStroke()); // paint ADX
            }
        }
    }

    public void calculate()
    {
        Dataset initial = getDataset();
        if (initial != null && !initial.isEmpty())
        {
            int period = properties.getPeriod();
            Dataset[] ds = Dataset.ADX(initial, period);
            addDataset(PDI, ds[0]); // PDI dataset
            addDataset(MDI, ds[1]); // MDI dataset
            addDataset(ADX, ds[2]); // ADX dataset
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
    { return new Color[] {properties.getPDIColor(), properties.getMDIColor(), properties.getADXColor()}; }

    public double[] getValues(ChartFrame cf)
    {
        Dataset pdi = visibleDataset(cf, PDI);
        Dataset mdi = visibleDataset(cf, MDI);
        Dataset adx = visibleDataset(cf, ADX);

        int i = adx.getLastIndex();
        double[] values = new double[3];
        values[0] = pdi.getDataItem(i) != null ? pdi.getCloseAt(i) : 0;
        values[1] = mdi.getDataItem(i) != null ? mdi.getCloseAt(i) : 0;
        values[2] = adx.getDataItem(i) != null ? adx.getCloseAt(i) : 0;

        return values;
    }

    public double[] getValues(ChartFrame cf, int i)
    {
        Dataset pdi = visibleDataset(cf, PDI);
        Dataset mdi = visibleDataset(cf, MDI);
        Dataset adx = visibleDataset(cf, ADX);

        double[] values = new double[3];
        values[0] = pdi.getDataItem(i) != null ? pdi.getCloseAt(i) : 0;
        values[1] = mdi.getDataItem(i) != null ? mdi.getCloseAt(i) : 0;
        values[2] = adx.getDataItem(i) != null ? adx.getCloseAt(i) : 0;

        return values;
    }

    public boolean getMarkerVisibility()
    { return properties.getMarker(); }

    public AbstractNode getNode()
    { return new IndicatorNode(properties); }

    public @Override Double[] getPriceValues(ChartFrame cf)
    { return new Double[] {new Double(20), new Double(40), new Double(60), new Double(80)}; }

}
