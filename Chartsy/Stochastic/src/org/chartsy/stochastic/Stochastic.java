/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.chartsy.stochastic;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MAType;
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
public class Stochastic extends Indicator
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    private static String FULL_NAME = "Stochastic";

    public static final String SLOWD = "slow%D";
    public static final String SLOWK = "slow%K";
    public static final String FASTD = "fast%D";
    public static final String FASTK = "fast%K";

    private IndicatorProperties properties;

    //variables for TA-Lib utilization
    private int lookback;
    private double[] outputFastD;
    private double[] outputFastK;
    private transient MInteger outBegIdx;
    private transient MInteger outNbElement;
    private transient Core core;

    //variables specific to this indicator
    private int periodD;
    private int periodK;
    private int periodSlowD;//the "SMOOTH" variable in IndicatorProperties
    private double[] allHighs;
    private double[] allLows;
    private double[] allClose;

    //the next variable is used for holding the newly calculated data
    private Dataset calculatedDatasetFastD;
    private Dataset calculatedDatasetFastK;

    public Stochastic()
    {
        super();
        properties = new IndicatorProperties();
    }

    @Override
    public String getName(){ return FULL_NAME; }

    @Override
    public String getLabel()
    { return properties.getLabel() + " (" + properties.getPeriodK() + ", " + properties.getSmooth() + ", " + properties.getPeriodD() + ")"; }

    @Override
    public String getPaintedLabel(ChartFrame cf)
    { return (properties.getSF() ? "Fast" : "Slow") + getLabel(); }

    @Override
    public Indicator newInstance(){ return new Stochastic(); }

    @Override
    public LinkedHashMap getHTML(ChartFrame cf, int i)
    {
        LinkedHashMap ht = new LinkedHashMap();

        DecimalFormat df = new DecimalFormat("#,##0.00");
        double[] values = getValues(cf, i);
        String[] labels = {"%D:", "%K:"};

        ht.put((properties.getSF() ? "Fast" : "Slow") + getLabel(), " ");
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

    @Override
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

    @Override
    public boolean hasZeroLine(){ return false; }

    @Override
    public boolean getZeroLineVisibility(){ return false; }

    @Override
    public Color getZeroLineColor(){ return null; }

    @Override
    public Stroke getZeroLineStroke(){ return null; }

    @Override
    public boolean hasDelimiters(){ return true; }

    @Override
    public boolean getDelimitersVisibility(){ return true; }

    @Override
    public double[] getDelimitersValues(){ return new double[] {20d, 50d, 80d}; }

    @Override
    public Color getDelimitersColor(){ return properties.getDelimiterColor(); }

    @Override
    public Stroke getDelimitersStroke(){ return properties.getDelimiterLineStroke(); }

    @Override
    public Color[] getColors()
    { return new Color[] {properties.getColorD(), properties.getColorK()}; }

    @Override
    public double[] getValues(ChartFrame cf)
    {
        Dataset stoD = visibleDataset(cf, properties.getSF() ? FASTD : SLOWD);
        Dataset stoK = visibleDataset(cf, properties.getSF() ? FASTK : SLOWK);

        if (stoD != null && stoK != null)
            return new double[] {stoD.getLastClose(), stoK.getLastClose()};
        return new double[] {};
    }

    @Override
    public double[] getValues(ChartFrame cf, int i)
    {
        Dataset stoD = visibleDataset(cf, properties.getSF() ? FASTD : SLOWD);
        Dataset stoK = visibleDataset(cf, properties.getSF() ? FASTK : SLOWK);

        if (stoD != null && stoK != null)
            return new double[] {stoD.getCloseAt(i), stoK.getCloseAt(i)};
        return new double[] {};
    }

    @Override
    public boolean getMarkerVisibility(){ return properties.getMarker(); }

    @Override
    public AbstractNode getNode(){ return new IndicatorNode(properties); }

    @Override
    public Double[] getPriceValues(ChartFrame cf)
    { return new Double[] {new Double(20), new Double(50), new Double(80)}; }

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
        outputFastD = new double[count];
        outputFastK = new double[count];
        outBegIdx = new MInteger();
        outNbElement = new MInteger();

        core = TaLibInit.getCore();//needs to be here for serialization issues

        //[your specific indicator variables need to be set first]
        periodK = properties.getPeriodK();
        periodD = properties.getPeriodD();
        periodSlowD = properties.getSmooth();//actually this is the Slow%D in the indicator!

        allHighs = initial.getHighValues();
        allLows = initial.getLowValues();
        allClose = initial.getCloseValues();

        //now do the calculation over the entire dataset
        //[First, perform the lookback call if one exists]
        //[Second, do the calculation call from TA-lib]

        lookback = core.stochLookback(periodK, periodD, MAType.Sma, periodSlowD, MAType.Sma);
        core.stoch(0, count-1, allHighs, allLows, allClose, periodK, periodD, MAType.Sma, periodSlowD, MAType.Sma, outBegIdx, outNbElement, outputFastK, outputFastD);

        //Everything between the /***/ lines is what needs to be changed.
        //Everything else remains the same. You are done with your part now.
        /**********************************************************************/

        //fix the output array's structure. TA-Lib does NOT match
        //indicator index and dataset index automatically. That's what
        //this function does for us.
        outputFastD = TaLibUtilities.fixOutputArray(outputFastD, lookback);
        outputFastK = TaLibUtilities.fixOutputArray(outputFastK, lookback);

        calculatedDatasetFastD = Dataset.EMPTY(initial.getItemsCount());
        for (int i = 0; i < outputFastD.length; i++)
            calculatedDatasetFastD.setDataItem(i, new DataItem(initial.getTimeAt(i), outputFastD[i]));

        calculatedDatasetFastK = Dataset.EMPTY(initial.getItemsCount());
        for (int i = 0; i < outputFastK.length; i++)
            calculatedDatasetFastK.setDataItem(i, new DataItem(initial.getTimeAt(i), outputFastK[i]));

        addDataset(FASTD, calculatedDatasetFastD);
        addDataset(FASTK, calculatedDatasetFastK);

    }

}

