/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.chartsy.trix;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
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
import org.chartsy.talib.TaLibInit;
import org.chartsy.talib.TaLibUtilities;
import org.openide.nodes.AbstractNode;

/**
 * The TRIX Indicator
 *
 * @author joshua.taylor
 */
public class TRIX extends Indicator
{
    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public static final String FULL_NAME = "TRIX";
    public static final String ABBREV = "trix";
    public static final String EMA_ABBREV = "ematrix";

    private IndicatorProperties properties;

    //variables for TA-Lib utilization
    private int lookback;
    private int emaLookback;
    private double[] outputSignal;
    private double[] outputTrix;
    private transient MInteger outBegIdx;
    private transient MInteger outNbElement;
    private transient Core core;

    //variables specific to this indicator
    private int period;
    private int emaPeriod;

    //the next variable is used for holding the new calculated data
    private Dataset calculatedDatasetTrix;
    private Dataset calculatedDatasetSignal;

    public TRIX()
    {
        super();
        properties = new IndicatorProperties();
    }

    @Override
    public String getName(){ return FULL_NAME; }

    @Override
    public String getLabel()
    { return properties.getLabel()+ " (" + properties.getPeriod() + ")"; }

    @Override
    public String getPaintedLabel(ChartFrame cf){ return getLabel(); }

    @Override
    public Indicator newInstance(){ return new TRIX(); }

    @Override
    public boolean hasZeroLine(){ return true; }

    @Override
    public boolean getZeroLineVisibility(){ return true; }

    @Override
    public Color getZeroLineColor(){ return properties.getZeroLineColor(); }

    @Override
    public Stroke getZeroLineStroke() {return properties.getZeroLineStroke(); }

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
    public Color[] getColors()
    { return new Color[] {properties.getTrixColor(), properties.getSignalColor()}; }

    @Override
    public boolean getMarkerVisibility(){ return properties.getMarker(); }

    @Override
    public AbstractNode getNode(){ return new IndicatorNode(properties); }

    @Override
    public Double[] getPriceValues(ChartFrame cf)
    {  return new Double[] {}; }

    @Override
    public LinkedHashMap getHTML(ChartFrame cf, int i)
    {
        LinkedHashMap ht = new LinkedHashMap();

        DecimalFormat df = new DecimalFormat("#,##0.00");
        double[] values = getValues(cf, i);
        String[] labels = {"TRIX:", "Signal:"};

        ht.put(getLabel(), " ");
        if (values.length > 0)
        {
            Color[] colors = getColors();
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
        return range;
    }


    @Override
    public void paint(Graphics2D g, ChartFrame cf, Rectangle bounds)
    {
        Dataset trix = visibleDataset(cf, ABBREV);
        Dataset signal = visibleDataset(cf, EMA_ABBREV);

        if (trix != null && signal != null)
        {
            if (maximized)
            {
                Range range = getRange(cf);

                DefaultPainter.line(g, cf, range, bounds, trix, properties.getTrixColor(), properties.getTrixStroke()); // paint the TRIX
                DefaultPainter.line(g, cf, range, bounds, signal, properties.getSignalColor(), properties.getSignalStroke()); // paint the Signal
            }
        }
    }


    @Override
    public double[] getValues(ChartFrame cf)
    {
        Dataset trix = visibleDataset(cf, ABBREV);
        Dataset signal = visibleDataset(cf, EMA_ABBREV);

        double[] values = new double[2];

        if(trix != null && signal != null){
            values[0] = trix.getLastClose();
            values[1] = signal.getLastClose();
        }
        else
            return new double[] {,};

        return values;
    }

    @Override
    public double[] getValues(ChartFrame cf, int i)
    {
        Dataset trix = visibleDataset(cf, ABBREV);
        Dataset signal = visibleDataset(cf, EMA_ABBREV);

        double[] values = new double[2];

        if(trix != null && signal != null){
            values[0] = trix.getDataItem(i) != null ? trix.getCloseAt(i) : 0;
            values[1] = signal.getDataItem(i) != null ? signal.getCloseAt(i) : 0;
        }
        else
            return new double[] {,};

        return values;
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
        //from TA-Lib for your needs. Everything else should stay basically the
        //same

        //prepare ta-lib variables
        outputSignal = new double[count];
        outputTrix = new double[count];
        outBegIdx = new MInteger();
        outNbElement = new MInteger();
        core = TaLibInit.getCore();//needs to be here for serialization issues

        //[your specific indicator variables need to be set first]
        period = properties.getPeriod();
        emaPeriod = properties.getEmaPeriod();

        //now do the calculation over the entire dataset
        //[First, perform the lookback call if one exists]
        //[Second, do the calculation call from TA-lib]

        lookback = core.trixLookback(period);
        core.trix(0, count-1, initial.getCloseValues(), period, outBegIdx, outNbElement, outputTrix);

        //when calculating a moving average of an indicator, this is the order
        //in which your calculations must occur and the second calculation must
        //use the output from this calculation. Look closely at the pattern.
        outputTrix = TaLibUtilities.fixOutputArray(outputTrix, lookback);

        calculatedDatasetTrix = Dataset.EMPTY(initial.getItemsCount());
        for (int i = 0; i < outputTrix.length; i++)
            calculatedDatasetTrix.setDataItem(i, new DataItem(initial.getTimeAt(i), outputTrix[i]));

        emaLookback = core.emaLookback(emaPeriod);
        core.ema(0, count-1, calculatedDatasetTrix.getCloseValues(), emaPeriod, outBegIdx, outNbElement, outputSignal);

        //Everything between the /***/ lines is what needs to be changed.
        //Everything else remains the same. You are done with your part now.
        /**********************************************************************/

        //fix the output array's structure. TA-Lib does NOT match
        //indicator index and dataset index automatically. That's what
        //this function does for us.
        outputSignal = TaLibUtilities.fixOutputArray(outputSignal, emaLookback);
        
        calculatedDatasetSignal = Dataset.EMPTY(initial.getItemsCount());
        for (int i = 0; i < outputSignal.length; i++)
            calculatedDatasetSignal.setDataItem(i, new DataItem(initial.getTimeAt(i), outputSignal[i]));

        addDataset(ABBREV, calculatedDatasetTrix);
        addDataset(EMA_ABBREV, calculatedDatasetSignal);
    }

}
