/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.chartsy.aroon;

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
import org.chartsy.main.utils.StrokeGenerator;
import org.chartsy.talib.TaLibInit;
import org.chartsy.talib.TaLibUtilities;
import org.openide.nodes.AbstractNode;

/**
 *
 * @author joshua.taylor
 */
public class Aroon extends Indicator
{
    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public static int PERIOD = 10;
    public static final String FULL_NAME = "Aroon";
    //public static final String ABBREV = "aroon";
    public static final String UP_LINE = "upline";
    public static final String DOWN_LINE = "downline";

    private IndicatorProperties properties;

    //variables for TA-Lib utilization
    private int lookback;
    private double[] outputDown;
    private double[] outputUp;
    private transient MInteger outBegIdx;
    private transient MInteger outNbElement;
    private transient Core core;

    //variables specific to this indicator
    private int period = PERIOD;
    private double[] allHighs;
    private double[] allLows;

    //the next variable is used for ultra-fast calculations
    private Dataset calculatedDatasetUp;
    private Dataset calculatedDatasetDown;

    public Aroon()
    {
        super();
        properties = new IndicatorProperties();
    }

    public String getName()
    { return FULL_NAME; }

    public String getLabel()
    { return properties.getLabel(); }

    public String getPaintedLabel(ChartFrame cf)
    { return getLabel(); }

    public Indicator newInstance()
    { return new Aroon(); }

    @Override
    public boolean hasZeroLine(){ return false; }

    @Override
    public boolean getZeroLineVisibility(){ return false; }

    @Override
    public Color getZeroLineColor(){ return null; }

    @Override
    public Stroke getZeroLineStroke() {return null; }

    public boolean hasDelimiters()
    { return true; }

    public boolean getDelimitersVisibility()
    { return true; }

    public double[] getDelimitersValues()
    { return new double[] {30d, 50d, 70d}; }

    public Color getDelimitersColor()
    { return new Color(0xbbbbbb); }

    public Stroke getDelimitersStroke()
    { return StrokeGenerator.getStroke(1); }

    public Color[] getColors()
    { return new Color[] {properties.getUpTrendColor(), properties.getDownTrendColor()}; }

    public boolean getMarkerVisibility(){ return properties.getMarker(); }

    public AbstractNode getNode(){ return new IndicatorNode(properties); }

    @Override
    public Double[] getPriceValues(ChartFrame cf)
    {  return new Double[] {new Double(30), new Double(50), new Double(70)}; }

    public LinkedHashMap getHTML(ChartFrame cf, int i)
    {
        LinkedHashMap ht = new LinkedHashMap();

        DecimalFormat df = new DecimalFormat("#,##0.00");
        double[] values = getValues(cf, i);
        String[] labels = {"Aroon Up:", "Aroon Down:"};

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
    { return new Range(0, 100); }


    public void paint(Graphics2D g, ChartFrame cf, Rectangle bounds)
    {
        Dataset upTrend = visibleDataset(cf, UP_LINE);
        Dataset downTrend = visibleDataset(cf, DOWN_LINE);

        if (upTrend != null && downTrend != null)
        {
            if (maximized)
            {
                Range range = getRange(cf);

                DefaultPainter.line(g, cf, range, bounds, upTrend, properties.getUpTrendColor(), properties.getUpTrendLineStroke()); // paint the signal
                DefaultPainter.line(g, cf, range, bounds, downTrend, properties.getDownTrendColor(), properties.getDownTrendLineStroke()); // paint the MACD
            }
        }
    }

    
    public double[] getValues(ChartFrame cf)
    {
        Dataset upTrend = visibleDataset(cf, UP_LINE);
        Dataset downTrend = visibleDataset(cf, DOWN_LINE);
        
        double[] values = new double[2];

        if(upTrend != null && downTrend != null){
            values[0] = upTrend.getLastClose();
            values[1] = downTrend.getLastClose();
        }
        else
            return new double[] {,};

        return values;
    }

    public double[] getValues(ChartFrame cf, int i)
    {
        Dataset upTrend = visibleDataset(cf, UP_LINE);
        Dataset downTrend = visibleDataset(cf, DOWN_LINE);
        
        double[] values = new double[2];

        if(upTrend != null && downTrend != null){
            values[0] = upTrend.getDataItem(i) != null ? upTrend.getCloseAt(i) : 0;
            values[1] = downTrend.getDataItem(i) != null ? downTrend.getCloseAt(i) : 0;
        }
        else
            return new double[] {,};

        return values;
    }

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
        outputDown = new double[count];
        outputUp = new double[count];
        outBegIdx = new MInteger();
        outNbElement = new MInteger();
        core = TaLibInit.getCore();//needs to be here for serialization issues

        //[your specific indicator variables need to be set first]
        period = properties.getPeriod();
        allHighs = initial.getHighValues();//new double[count];
        allLows = initial.getLowValues();//new double[count];

        //now do the calculation over the entire dataset
        //[First, perform the lookback call if one exists]
        //[Second, do the calculation call from TA-lib]

        lookback = core.aroonLookback(period);
        core.aroon(0, count-1, allHighs, allLows, period, outBegIdx, outNbElement, outputDown, outputUp);

        //Everything between the /***/ lines is what needs to be changed.
        //Everything else remains the same. You are done with your part now.
        /**********************************************************************/

        //fix the output array's structure. TA-Lib does NOT match
        //indicator index and dataset index automatically. That's what
        //this function does for us.
        outputDown = TaLibUtilities.fixOutputArray(outputDown, lookback);
        outputUp = TaLibUtilities.fixOutputArray(outputUp, lookback);

        calculatedDatasetUp = Dataset.EMPTY(initial.getItemsCount());
        for (int i = 0; i < outputUp.length; i++)
            calculatedDatasetUp.setDataItem(i, new DataItem(initial.getTimeAt(i), outputUp[i]));

        calculatedDatasetDown = Dataset.EMPTY(initial.getItemsCount());
        for (int i = 0; i < outputDown.length; i++)
            calculatedDatasetDown.setDataItem(i, new DataItem(initial.getTimeAt(i), outputDown[i]));

        addDataset(UP_LINE, calculatedDatasetUp);
        addDataset(DOWN_LINE, calculatedDatasetDown);
    }


}
