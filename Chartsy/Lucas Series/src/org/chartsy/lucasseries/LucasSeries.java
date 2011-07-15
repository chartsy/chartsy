package org.chartsy.lucasseries;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.util.Calendar;
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
public class LucasSeries extends Overlay {

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public static final String LS = "ls";
    private OverlayProperties properties;

    public LucasSeries()
    {
        super();
        properties = new OverlayProperties();
    }

    public String getName()
    { return "Lucas Series"; }

    public String getLabel() {
        return properties.getLabel();
    }

    public Overlay newInstance() {
        return new LucasSeries();
    }

    public LinkedHashMap getHTML(ChartFrame cf, int i) {
        return new LinkedHashMap();
    }

    public void paint(Graphics2D g, ChartFrame cf, Rectangle bounds) {
        Dataset ls = visibleDataset(cf, LS);
        
        if ( ls != null ) {
            Range range = cf.getSplitPanel().getChartPanel().getRange();

            g.setColor(properties.getColor());
            g.setStroke(properties.getStroke());

            double y = cf.getChartData().getY(range.getLowerBound(), bounds, range, false);
            g.draw(new Line2D.Double(bounds.getMinX(), y, bounds.getMaxX(), y));
            
            for (int i = 0; i < ls.getItemsCount(); i++) {
                if ( ls.getDataItem(i) != null ) {
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
        Calendar c2 = Calendar.getInstance();

        int start = -1;

        for (int i = 0; i < initial.getItemsCount(); i++) {
            long time = initial.getTimeAt(i);
            c2.setTimeInMillis(time);

            if ( c2.get(Calendar.DAY_OF_MONTH) == properties.getStartingDay()
                    && (c2.get(Calendar.MONTH) + 1) == properties.getStartingMonth()
                    && c2.get(Calendar.YEAR) == properties.getStartingYear()
                    && c2.get(Calendar.HOUR_OF_DAY) == properties.getStartingHour()
                    && c2.get(Calendar.MINUTE) == properties.getStartingMinute() ) {
                start = i;
                break;
            }
        }

        if ( start != -1 ) {
            // calculation here
            Dataset ls = Dataset.EMPTY(initial.getItemsCount());

            for (int i = start; i < initial.getItemsCount(); i++) {
                int diff = i - start;                
                if ( diff == 3
                        || diff == 4
                        || diff == 7
                        || diff == 11
                        || diff == 18
                        || diff == 29
                        || diff == 47
                        || diff == 76
                        || diff == 123
                        || diff == 199
                        || diff == 322
                        || diff == 521
                        || diff == 843
                        || diff == 1364
                        || diff == 2207
                        || diff == 3571
                        ) {
                    ls.setDataItem(i, new DataItem(initial.getTimeAt(i), 1));
                }
            }

            addDataset(LS, ls);
        }
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
        return Dataset.CLOSE;
    }

    public boolean isIncludedInRange() {
        return false;
    }

}
