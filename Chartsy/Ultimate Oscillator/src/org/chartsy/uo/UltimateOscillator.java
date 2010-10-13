package org.chartsy.uo;

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
public class UltimateOscillator
        extends Indicator
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;
    
    public static final String UO = "UO";
    private IndicatorProperties properties;

    public UltimateOscillator() {
        super();
        properties = new IndicatorProperties();
    }

    public String getName() {return "Ultimate Oscillator"; }

    public String getLabel(){ return properties.getLabel(); }

    public String getPaintedLabel(ChartFrame cf){ return getLabel(); }

    public Indicator newInstance() {return new UltimateOscillator(); }

    @Override
    public LinkedHashMap getHTML(ChartFrame cf, int i) {
        LinkedHashMap ht = new LinkedHashMap();

        DecimalFormat df = new DecimalFormat("#,##0.00");
        double[] values = getValues(cf, i);
        String[] labels = {"UO:"};

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
    public Range getRange(ChartFrame cf){ return new Range(0, 100); }

    public void paint(Graphics2D g, ChartFrame cf, Rectangle bounds)
    {
        Dataset d = visibleDataset(cf, UO);
        if (d != null)
        {
            if (maximized)
            {
                Range range = getRange(cf);
                
                DefaultPainter.line(g, cf, range, bounds, d, properties.getColor(), properties.getStroke()); // paint line
            }
        }
    }

    public void calculate() {
        Dataset initial = getDataset();
        if (initial != null && !initial.isEmpty()) {
            Dataset d = getDataset(initial);
            addDataset(UO, d);
        }
    }

    public boolean hasZeroLine(){ return false; }

    public boolean getZeroLineVisibility(){ return false; }

    public Color getZeroLineColor(){ return null; }

    public Stroke getZeroLineStroke(){ return null; }

    public boolean hasDelimiters(){ return true; }

    public boolean getDelimitersVisibility(){ return true; }

    public double[] getDelimitersValues()
    { return new double[] {30.0d, 50.0d, 70.0d}; }

    public Color getDelimitersColor(){ return properties.getDelimiterColor(); }

    public Stroke getDelimitersStroke(){ return properties.getDelimiterLineStroke(); }

    public Color[] getColors() { return new Color[] {properties.getColor()}; }

    public double[] getValues(ChartFrame cf) {
        Dataset d = visibleDataset(cf, UO);
        if (d != null) {
            return new double[] {d.getLastClose()};
        }
        return new double[] {};
    }

    public double[] getValues(ChartFrame cf, int i) {
        Dataset d = visibleDataset(cf, UO);
        if (d != null) {
            return new double[] {d.getCloseAt(i)};
        }
        return new double[] {};
    }

    public boolean getMarkerVisibility() {return properties.getMarker(); }

    public AbstractNode getNode() {return new IndicatorNode(properties); }

    @Override
    public Double[] getPriceValues(ChartFrame cf)
    { return new Double[] {new Double(10), new Double(30), new Double(50), new Double(70), new Double(90)}; }

    private Dataset getDataset(final Dataset initial) {
        int fast = properties.getFast();
        int inter = properties.getIntermediate();
        int slow = properties.getSlow();
        int count = initial.getItemsCount();

        Dataset d = Dataset.EMPTY(count);

        double temp, sbFast, srFast, sbInter, srInter, sbSlow, srSlow, rFast, rInter, rSlow;
        for (int i = slow; i < count; i++) {
            sbFast = 0;
            srFast = 0;
            for (int j = 0; j < fast; j++) {
                if (initial.getCloseAt(i-j-1) < initial.getLowAt(i-j))
                    sbFast += initial.getCloseAt(i-j) - initial.getCloseAt(i-j-1);
                else
                    sbFast += initial.getCloseAt(i-j) - initial.getLowAt(i-j);

                if ((initial.getHighAt(i-j) - initial.getLowAt(i-j)) > (initial.getHighAt(i-j) - initial.getCloseAt(i-j-1)))
                    temp = initial.getHighAt(i-j) - initial.getLowAt(i-j);
                else
                    temp = initial.getHighAt(i-j) - initial.getCloseAt(i-j-1);

                if (temp > (initial.getCloseAt(i-j-1) - initial.getLowAt(i-j)))
                    rFast = temp;
                else
                    rFast = initial.getCloseAt(i-j-1) - initial.getLowAt(i-j);
                
                srFast += rFast;
            }

            sbInter = sbFast;
            srInter = srFast;
            for (int j = fast; j < inter; j++) {
                if (initial.getCloseAt(i-j-1) < initial.getLowAt(i-j))
                    sbInter += initial.getCloseAt(i-j) - initial.getCloseAt(i-j-1);
                else
                    sbInter += initial.getCloseAt(i-j) - initial.getLowAt(i-j);

                if ((initial.getHighAt(i-j) - initial.getLowAt(i-j)) > (initial.getHighAt(i-j) - initial.getCloseAt(i-j-1)))
                    temp = initial.getHighAt(i-j) - initial.getLowAt(i-j);
                else
                    temp = initial.getHighAt(i-j) - initial.getCloseAt(i-j-1);

                if (temp > (initial.getCloseAt(i-j-1) - initial.getLowAt(i-j)))
                    rInter = temp;
                else
                    rInter = initial.getCloseAt(i-j-1) - initial.getLowAt(i-j);

                srInter += rInter;
            }

            sbSlow = sbInter;
            srSlow = srInter;
            for (int j = inter; j < slow; j++) {
                if (initial.getCloseAt(i-j-1) < initial.getLowAt(i-j))
                    sbSlow += initial.getCloseAt(i-j) - initial.getCloseAt(i-j-1);
                else
                    sbSlow += initial.getCloseAt(i-j) - initial.getLowAt(i-j);

                if ((initial.getHighAt(i-j) - initial.getLowAt(i-j)) > (initial.getHighAt(i-j) - initial.getCloseAt(i-j-1)))
                    temp = initial.getHighAt(i-j) - initial.getLowAt(i-j);
                else
                    temp = initial.getHighAt(i-j) - initial.getCloseAt(i-j-1);

                if (temp > (initial.getCloseAt(i-j-1) - initial.getLowAt(i-j)))
                    rSlow = temp;
                else
                    rSlow = initial.getCloseAt(i-j-1) - initial.getLowAt(i-j);
                
                srSlow += rSlow;
            }

            if (srFast != 0 && srInter != 0 && srSlow != 0)
                d.setDataItem(i, new DataItem(initial.getTimeAt(i), (100d * (4*sbFast/srFast + 2*sbInter/srInter + sbSlow/srSlow) / 7d)));
        }

        return d;
    }

}
