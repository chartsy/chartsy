/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.chartsy.parabolicsar;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.Serializable;
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
 *
 * @author joshua.taylor
 */
public class ParabolicSAR extends Overlay implements Serializable{

    private static final long serialVersionUID = SerialVersion.APPVERSION;
    public static final String FULL_NAME = "Parabolic SAR";
    public static final String HASHKEY = "sar";

    private OverlayProperties properties;

    //variables for TA-Lib utilization
    private int lookback;
    private double[] output;
    private transient MInteger outBegIdx;
    private transient MInteger outNbElement;
    private transient Core core;

    //variables specific to Parabolic SAR
    private double step;//also called acceleration
    private double maxStep;//also called max acceleration

    //the next variable is used for calculations
    private Dataset calculatedDataset;

    public ParabolicSAR()
    {
        super();
        properties = new OverlayProperties();
    }

    @Override
    public Overlay newInstance() { return new ParabolicSAR(); }

    @Override
    public String getName(){ return FULL_NAME; }

    @Override
    public boolean getMarkerVisibility(){ return properties.getMarker(); }

    @Override
    public AbstractNode getNode(){ return new OverlayNode(properties); }

    @Override
    public String getPrice(){ return properties.getPrice(); }

    @Override
    public Color[] getColors(){ return new Color[] {properties.getColor()}; }

    @Override
    public String getLabel()
    { return properties.getLabel() + " (" + properties.getStep() + ", " + properties.getMaxStep() + ")"; }

     public LinkedHashMap getHTML(ChartFrame cf, int i) {
        LinkedHashMap ht = new LinkedHashMap();

        DecimalFormat df = new DecimalFormat("#,##0.00");
        double[] values = getValues(cf, i);

        ht.put(getLabel(), " ");
        if (values.length > 0) {
            Color[] colors = getColors();
            for (int j = 0; j < values.length; j++) {
                ht.put(getFontHTML(colors[j], "Para.SAR:"),
                        getFontHTML(colors[j], df.format(values[j])));
            }
        }

        return ht;
    }

    @Override
    public void paint(Graphics2D g, ChartFrame cf, Rectangle bounds)
    {
        Dataset dataset = visibleDataset(cf, HASHKEY);
        if (dataset != null)
        {
            Range range = cf.getSplitPanel().getChartPanel().getRange();
            DefaultPainter.dot(g, cf, range, bounds, dataset, properties.getColor(), properties.getStroke(), Dataset.getPrice(properties.getPrice()));
        }
    }

    @Override
    public double[] getValues(ChartFrame cf)
    {
        Dataset dataset = visibleDataset(cf, HASHKEY);
        if (dataset != null) {
            int price = Dataset.getPrice(properties.getPrice());
            return new double[] {dataset.getLastPrice(price)};
        }
        return new double[] {};
    }

    @Override
    public double[] getValues(ChartFrame cf, int i)
    {
        Dataset dataset = visibleDataset(cf, HASHKEY);
        if (dataset != null) {
            String price = properties.getPrice();
            return new double[] {dataset.getPriceAt(i, price)};
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
        //from TA-Lib for your needs. Everything else should stay basically the
        //same

        //prepare ta-lib variables
        output = new double[count];
        outBegIdx = new MInteger();
        outNbElement = new MInteger();
        core = TaLibInit.getCore();//needs to be here for serialization issues      

        //[your specific indicator variables need to be set first]

        //get Parabolic SAR specific variables based on user settings
        //from the properties class. The defaults for Para.SAR are
        //step = .02 and max step = .20 according to Welles Wilder
        step = properties.getStep();//also called acceleration
        maxStep = properties.getMaxStep();//also called max acceleration

        //now do the calculation over the entire dataset
        //[First, perform the lookback call if one exists]
        //[Second, do the calculation call from TA-lib]
        lookback = core.sarLookback(properties.getStep(), properties.getMaxStep());
        core.sar(0, count-1, initial.getHighValues(), initial.getLowValues(), step, maxStep, outBegIdx, outNbElement, output);

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
