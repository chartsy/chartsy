/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.chartsy.bop;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
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
import org.chartsy.talib.TaLibInit;
import org.chartsy.talib.TaLibUtilities;
import org.openide.nodes.AbstractNode;

/**
 *
 * @author joshua.taylor
 */
public class BOP extends Indicator
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public static final String FULL_NAME = "Balance of Power";
    public static final String HASHKEY = "bophist";
    
    private IndicatorProperties properties;

    //variables for TA-Lib utilization
    private int lookback;
    private double[] output;
    private transient MInteger outBegIdx;
    private transient MInteger outNbElement;
    private transient Core core;

    //variables specific to Average Directional Index
    private double[] allOpen;
    private double[] allHigh;
    private double[] allLow;
    
    //the next variable is used to hold indicator calculations
    private Dataset calculatedDataset;

    public BOP()
    {
        super();
        properties = new IndicatorProperties();
    }

    @Override
    public String getName(){ return FULL_NAME; }

    @Override
    public String getLabel(){ return properties.getLabel(); }

    @Override
    public String getPaintedLabel(ChartFrame cf){ return getLabel(); }

    @Override
    public Indicator newInstance(){ return new BOP(); }

    @Override
    public boolean hasZeroLine(){ return true; }

    @Override
    public boolean getZeroLineVisibility(){ return true; }

    @Override
    public Color getZeroLineColor(){ return properties.getZeroLineColor(); }

    @Override
    public Stroke getZeroLineStroke(){ return properties.getZeroLineStroke(); }

    @Override
    public boolean hasDelimiters(){ return false; }

    @Override
    public boolean getDelimitersVisibility(){ return false; }

    @Override
    public double[] getDelimitersValues(){ return new double[] {}; }

    @Override
    public Color getDelimitersColor(){ return null; }

    @Override
    public Stroke getDelimitersStroke(){ return null; }

    @Override
    public Color[] getColors(){ return new Color[] {properties.getHistogramPositiveColor(), properties.getHistogramNegativeColor()}; }

    @Override
    public boolean getMarkerVisibility(){ return properties.getMarker(); }

    @Override
    public AbstractNode getNode(){ return new IndicatorNode(properties); }

    @Override
    public LinkedHashMap getHTML(ChartFrame cf, int i)
    {
        LinkedHashMap ht = new LinkedHashMap();

        DecimalFormat df = new DecimalFormat("#,##0.00");
        double[] values = getValues(cf, i);
        String[] labels = {"BOP:"};

        ht.put(getLabel(), " ");
        if (values.length > 0)
        {
            Color[] colors = getColors();
            colors[0] = values[0] > 0 ? properties.getHistogramPositiveColor() : properties.getHistogramNegativeColor();
            for (int j = 0; j < values.length; j++)
            {
                ht.put(getFontHTML(colors[j], labels[j]), getFontHTML(colors[j], df.format(values[j])));
            }
        }

        return ht;
    }

    @Override
    public Range getRange(ChartFrame cf)
    {
        Range range = super.getRange(cf);
        double d = Math.max(Math.abs(range.getLowerBound()), Math.abs(range.getUpperBound()));
        return new Range(-1*d, d);
    }


    @Override
    public void paint(Graphics2D g, ChartFrame cf, Rectangle bounds)
    {
        Dataset histogram = visibleDataset(cf, HASHKEY);

        if (histogram != null)
        {
            if (maximized)
            {
                Range range = getRange(cf);

                DefaultPainter.histogram(g, cf, range, bounds, histogram, properties.getHistogramPositiveColor(), properties.getHistogramNegativeColor()); // paint the histogram
            }
        }
    }

    @Override
    public double[] getValues(ChartFrame cf)
    {
        Dataset histogram = visibleDataset(cf, HASHKEY);

        int i = histogram.getLastIndex();
        double[] values = new double[1];
        values[0] = histogram.getDataItem(i) != null ? histogram.getCloseAt(i) : 0;

        return values;
    }

    @Override
    public double[] getValues(ChartFrame cf, int i)
    {
        Dataset histogram = visibleDataset(cf, HASHKEY);

        double[] values = new double[1];
        values[0] = histogram.getDataItem(i) != null ? histogram.getCloseAt(i) : 0;
        return values;
    }

    @Override
    public Double[] getPriceValues(ChartFrame cf)
    {
        List<Double> list = new ArrayList<Double>();

        Range range = getRange(cf);

        if (range.getUpperBound() >= 1)
        {
            int step = (int) (range.getUpperBound() / 3) + 1;
            for (int i = step; i <= range.getUpperBound(); i+=step)
            {
                list.add(new Double(i));
                list.add(new Double(-1*i));
            }
        }
        else
        {
            double step = (range.getUpperBound() > 0.5) ? 0.25 : 0.15;
            for (double i = step; i <= range.getUpperBound(); i+=step)
            {
                list.add(new Double(i));
                list.add(new Double(-1*i));
            }
        }

        Double[] retval = list.toArray(new Double[list.size()]);
        list = null;
        return retval;
    }

    @Override
    public void calculate()
    {
        Dataset initial = getDataset();
        int count = 0;
        if (initial != null && !initial.isEmpty())
            count = initial.getItemsCount();

        /**********************************************************************/
        //This entire method is basically a copy/paste action into your own
        //code. The only thing you have to change is the next few lines of code.
        //Choose the 'lookback' method and appropriate 'calculation function'
        //from TA-Lib for your needs. You'll also need to ensure you gather
        //everything for your calculation as well. Everything else should stay
        //basically the same

        //prepare ta-lib variables
        output = new double[count];
        outBegIdx = new MInteger();
        outNbElement = new MInteger();
        core = TaLibInit.getCore();//needs to be here for serialization issues

        //[your specific indicator variables need to be set first]
        allOpen = initial.getOpenValues();
        allHigh = initial.getHighValues();
        allLow = initial.getLowValues();
        
        //now do the calculation over the entire dataset
        //[First, perform the lookback call if one exists]
        //[Second, do the calculation call from TA-lib]
        lookback = core.bopLookback();
        core.bop(0, count-1, allOpen, allHigh, allLow, initial.getCloseValues(), outBegIdx, outNbElement, output);

        //Everything between the /***/ lines is what needs to be changed.
        //Everything else remains the same. You are done with your part now.
        /**********************************************************************/

        //fix the output array's structure. TA-Lib does NOT match
        //indicator index and dataset index automatically. That's what
        //this function does for us.
        output = TaLibUtilities.fixOutputArray(output, lookback);

        calculatedDataset = Dataset.EMPTY(initial.getItemsCount());
        for (int i = 0; i < output.length; i++)
            calculatedDataset.setDataItem(i, new DataItem(initial.getTimeAt(i), output[i]));

        addDataset(HASHKEY, calculatedDataset);
    }


}
