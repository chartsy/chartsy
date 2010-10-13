/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.chartsy.wclose;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import org.chartsy.main.ChartFrame;
import org.chartsy.main.chart.Overlay;
import org.chartsy.main.data.DataItem;
import org.chartsy.main.data.Dataset;
import org.chartsy.main.utils.DefaultPainter;
import org.chartsy.main.utils.Range;
import org.chartsy.main.utils.SerialVersion;
import org.chartsy.talib.TaLibInit;
import org.chartsy.talib.TaLibUtilities;
import org.openide.nodes.AbstractNode;

/**
 * The weighted close indicator
 * 
 * @author joshua.taylor
 */
public class WClose extends Overlay
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public static final String FULL_NAME = "Weighted Close";
    public static final String HASHKEY = "wclose";

    private OverlayProperties properties;

    //variables for TA-Lib utilization
    private int lookback;
    private double[] output;
    private transient MInteger outBegIdx;
    private transient MInteger outNbElement;
    private transient Core core;

    //variables specific this moving average type
    private double[] allHighs;
    private double[] allLows;

    //the next variable is used for fast calculations
    private Dataset calculatedDataset;

    public WClose()
    {
        super();
        properties = new OverlayProperties();
    }

    @Override
    public String getName(){ return FULL_NAME; }

    @Override
    public String getLabel()
    { return properties.getLabel() + " (" + properties.getPrice() + ")"; }

    @Override
    public Overlay newInstance(){ return new WClose(); }

    @Override
    public Color[] getColors(){ return new Color[] {properties.getColor()}; }

    @Override
    public boolean getMarkerVisibility(){ return properties.getMarker(); }

    @Override
    public AbstractNode getNode(){ return new OverlayNode(properties); }

    @Override
    public String getPrice(){ return properties.getPrice(); }

    @Override
    public LinkedHashMap getHTML(ChartFrame cf, int i) {
        LinkedHashMap ht = new LinkedHashMap();

        DecimalFormat df = new DecimalFormat("#,##0.00");
        double[] values = getValues(cf, i);

        ht.put(getLabel(), " ");
        if (values.length > 0) {
            Color[] colors = getColors();
            for (int j = 0; j < values.length; j++) {
                ht.put(getFontHTML(colors[j], "Weighted Close:"),
                        getFontHTML(colors[j], df.format(values[j])));
            }
        }

        return ht;
    }

    @Override
    public void paint(Graphics2D g, ChartFrame cf, Rectangle bounds)
    {
        Dataset d = visibleDataset(cf, HASHKEY);
        if (d != null)
        {
            Range range = cf.getSplitPanel().getChartPanel().getRange();
            DefaultPainter.line(g, cf, range, bounds, d, properties.getColor(), properties.getStroke(), Dataset.getPrice(properties.getPrice()));
        }
    }

    @Override
    public double[] getValues(ChartFrame cf)
    {
        Dataset d = visibleDataset(cf, HASHKEY);
        if (d != null) {
            int price = Dataset.getPrice(properties.getPrice());
            return new double[] {d.getLastPrice(price)};
        }
        return new double[] {};
    }

    @Override
    public double[] getValues(ChartFrame cf, int i)
    {
        Dataset d = visibleDataset(cf, HASHKEY);
        if (d != null) {
            String price = properties.getPrice();
            return new double[] {d.getPriceAt(i, price)};
        }
        return new double[] {};
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
        allHighs = initial.getHighValues();
        allLows = initial.getLowValues();

        //now do the calculation over the entire dataset
        //[First, perform the lookback call if one exists]
        //[Second, do the calculation call from TA-lib]
        lookback = core.wclPriceLookback();
        core.wclPrice(0, count-1, allHighs, allLows, initial.getCloseValues(), outBegIdx, outNbElement, output);

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
