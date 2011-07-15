package org.chartsy.demarkssetup;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.util.LinkedHashMap;
import org.chartsy.main.ChartFrame;
import org.chartsy.main.chart.Overlay;
import org.chartsy.main.data.DataItem;
import org.chartsy.main.data.Dataset;
import org.chartsy.main.utils.Range;
import org.chartsy.main.utils.SerialVersion;
import org.openide.nodes.AbstractNode;

/**
 *
 * @author Viorel
 */
public class DeMarkSetup extends Overlay {

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public static final String BUY = "buy";
    public static final String SELL = "sell";
    private OverlayProperties properties;

    public DeMarkSetup()
    {
        super();
        properties = new OverlayProperties();
    }

    public String getName()
    { return "DeMark's Setup"; }

    public String getLabel() {
        return properties.getLabel();
    }

    public Overlay newInstance() {
        return new DeMarkSetup();
    }

    public LinkedHashMap getHTML(ChartFrame cf, int i) {
        return new LinkedHashMap();
    }

    public void paint(Graphics2D g, ChartFrame cf, Rectangle bounds) {
        Range range = cf.getSplitPanel().getChartPanel().getRange();
        g.setColor(Color.black);
        g.setStroke(properties.getStroke());

        double y = cf.getChartData().getY(range.getLowerBound(), bounds, range, false);
        g.draw(new Line2D.Double(bounds.getMinX(), y, bounds.getMaxX(), y));

        Dataset buy = visibleDataset(cf, BUY);

        if ( buy != null ) {
            g.setColor(Color.green);

            for (int i = 0; i < buy.getItemsCount(); i++) {
                if ( buy.getDataItem(i) != null ) {
                    double x = cf.getChartData().getX(i, bounds);
                    double y1 = cf.getChartData().getY(range.getLowerBound(), bounds, range, false);
                    double y2 = cf.getChartData().getY(range.getUpperBound(), bounds, range, false);
                    double w = cf.getChartProperties().getBarWidth();

                    g.draw(new Line2D.Double(x - w/2, y1, x, y2));
                    g.draw(new Line2D.Double(x + w/2, y1, x, y2));
                }
            }
        }

        Dataset sell = visibleDataset(cf, SELL);

        if ( sell != null ) {
            g.setColor(Color.red);

            for (int i = 0; i < sell.getItemsCount(); i++) {
                if ( sell.getDataItem(i) != null ) {
                    double x = cf.getChartData().getX(i, bounds);
                    double y1 = cf.getChartData().getY(range.getLowerBound(), bounds, range, false);
                    double y2 = cf.getChartData().getY(range.getUpperBound(), bounds, range, false);
                    double w = cf.getChartProperties().getBarWidth();

                    g.draw(new Line2D.Double(x - w/2, y1, x, y2));
                    g.draw(new Line2D.Double(x + w/2, y1, x, y2));
                }
            }
        }
    }

    public void calculate() {
        Dataset initial = getDataset();
        int count = initial.getItemsCount();

        double[] prices = new double[count];
        if ( properties.getPrice().equals(Dataset.OPEN) ) prices = initial.getCloseValues();
        if ( properties.getPrice().equals(Dataset.HIGH) ) prices = initial.getHighValues();
        if ( properties.getPrice().equals(Dataset.LOW) ) prices = initial.getLowValues();
        if ( properties.getPrice().equals(Dataset.CLOSE) ) prices = initial.getCloseValues();
        if ( properties.getPrice().equals(Dataset.VOLUME) ) prices = initial.getVolumeValues();

        // BuySetup
        Dataset buy = Dataset.EMPTY(count);
        for ( int i = 13; i < count; i++ ) {
            if ( prices[i] < prices[i - 4] && prices[i - 9] > prices[i - 13] ) {
                buy.setDataItem(i, new DataItem(initial.getTimeAt(i), 1));
            }
        }
        addDataset(BUY, buy);
        

        // SellSetup
        Dataset sell = Dataset.EMPTY(count);
        for ( int i = 13; i < count; i++ ) {
            if ( prices[i] > prices[i - 4] && prices[i - 9] < prices[i - 13] ) {
                sell.setDataItem(i, new DataItem(initial.getTimeAt(i), 1));
            }
        }
        addDataset(SELL, sell);
    }

    public Color[] getColors() {
        return new Color[] {};
    }

    public double[] getValues(ChartFrame cf) {
        return new double[] {};
    }

    public double[] getValues(ChartFrame cf, int i) {
        return new double[] {};
    }

    public boolean getMarkerVisibility() {
        return false;
    }

    public AbstractNode getNode() {
        return new OverlayNode(properties);
    }

    public String getPrice() {
        return properties.getPrice();
    }

    public boolean isIncludedInRange() {
        return false;
    }

}
