/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.chartsy.htsine;

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
 * The Hilbert Transform Sine Indicator
 *
 * @author joshua.taylor
 */
public class HTSine extends Indicator{

    private static final long serialVersionUID = SerialVersion.APPVERSION;
    public static final String FULL_NAME = "Hilbert Transform Sine";
    public static final String SINE = "htsine";
    public static final String LEAD_SINE = "htleadsine";


    private IndicatorProperties properties;

    //variables for TA-Lib utilization
    private int lookback;
    private double[] outputSine;
    private double[] outputLeadSine;
    private transient MInteger outBegIdx;
    private transient MInteger outNbElement;
    private transient Core core;

    //variables specific to Average Directional Index
    //NONE...

    //the next variable is used to hold indicator calculations
    private Dataset calculatedDatasetSine;
    private Dataset calculatedDatasetLeadSine;

    public HTSine() {
        super();
        properties = new IndicatorProperties();
    }

    @Override
    public String getName(){ return FULL_NAME;}

    @Override
    public String getLabel() { return properties.getLabel(); }

    @Override
    public String getPaintedLabel(ChartFrame cf){ return ""; }

    @Override
    public Indicator newInstance(){ return new HTSine(); }

    @Override
    public boolean hasZeroLine(){ return true; }

    @Override
    public boolean getZeroLineVisibility(){ return true; }

    @Override
    public Color getZeroLineColor(){ return properties.getZeroLineColor(); }

    @Override
    public Stroke getZeroLineStroke(){ return properties.getZeroLineStroke(); }

    @Override
    public boolean hasDelimiters(){ return true; }

    @Override
    public boolean getDelimitersVisibility(){ return false; }

    @Override
    public double[] getDelimitersValues(){ return new double[] {}; }

    @Override
    public Color getDelimitersColor(){ return null; }

    @Override
    public Stroke getDelimitersStroke(){ return null; }

    @Override
    public Color[] getColors(){ return new Color[] {properties.getSineLineColor(), properties.getLeadSineLineColor()}; }

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
        String[] labels = {"In Phase:", "Quadrature:"};

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
        Dataset inPhase = visibleDataset(cf, SINE);
        Dataset quadrature = visibleDataset(cf, LEAD_SINE);

        if (inPhase != null && quadrature != null)
        {
            if (maximized)
            {
                Range range = getRange(cf);

                DefaultPainter.line(g, cf, range, bounds, inPhase, properties.getSineLineColor(), properties.getSineLineStroke()); // paint the signal
                DefaultPainter.line(g, cf, range, bounds, quadrature, properties.getLeadSineLineColor(), properties.getLeadSineLineStroke()); // paint the MACD
            }
        }
    }

    @Override
    public double[] getValues(ChartFrame cf)
    {
        Dataset inPhase = visibleDataset(cf, SINE);
        Dataset quadrature = visibleDataset(cf, LEAD_SINE);

        double[] values = new double[2];

        if(inPhase != null && quadrature != null){
            values[0] = inPhase.getLastClose();
            values[1] = quadrature.getLastClose();
        }
        else
            return new double[] {,};

        return values;
    }

    @Override
    public double[] getValues(ChartFrame cf, int i)
    {
        Dataset inPhase = visibleDataset(cf, SINE);
        Dataset quadrature = visibleDataset(cf, LEAD_SINE);

        double[] values = new double[2];

        if(inPhase != null && quadrature != null){
            values[0] = inPhase.getDataItem(i) != null ? inPhase.getCloseAt(i) : 0;
            values[1] = quadrature.getDataItem(i) != null ? quadrature.getCloseAt(i) : 0;
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
        //from TA-Lib for your needs. You'll also need to ensure you gather
        //everything for your calculation as well. Everything else should stay
        //basically the same

        //prepare ta-lib variables
        outputSine = new double[count];
        outputLeadSine = new double[count];
        outBegIdx = new MInteger();
        outNbElement = new MInteger();
        core = TaLibInit.getCore();//needs to be here for serialization issues

        //[your specific indicator variables need to be set first]
        //NONE...

        //now do the calculation over the entire dataset
        //[First, perform the lookback call if one exists]
        //[Second, do the calculation call from TA-lib]
        lookback = core.htSineLookback();
        core.htSine(0, count-1, initial.getCloseValues(), outBegIdx, outNbElement, outputSine, outputLeadSine);

        //Everything between the /***/ lines is what needs to be changed.
        //Everything else remains the same. You are done with your part now.
        /**********************************************************************/

        //fix the output array's structure. TA-Lib does NOT match
        //indicator index and dataset index automatically. That's what
        //this function does for us.
        outputSine = TaLibUtilities.fixOutputArray(outputSine, lookback);
        outputLeadSine = TaLibUtilities.fixOutputArray(outputLeadSine, lookback);

        calculatedDatasetSine = Dataset.EMPTY(initial.getItemsCount());
        for (int i = 0; i < outputSine.length; i++)
            calculatedDatasetSine.setDataItem(i, new DataItem(initial.getTimeAt(i), outputSine[i]));

        calculatedDatasetLeadSine = Dataset.EMPTY(initial.getItemsCount());
        for (int i = 0; i < outputLeadSine.length; i++)
            calculatedDatasetLeadSine.setDataItem(i, new DataItem(initial.getTimeAt(i), outputLeadSine[i]));

        addDataset(SINE, calculatedDatasetSine);
        addDataset(LEAD_SINE, calculatedDatasetLeadSine);
    }

}
