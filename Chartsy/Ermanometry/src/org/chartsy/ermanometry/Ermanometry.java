package org.chartsy.ermanometry;

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
public class Ermanometry extends Overlay {
    
    private static final long serialVersionUID = SerialVersion.APPVERSION;
    
    public static final String ERM = "erm";
    public static final String COL = "col";
    private OverlayProperties properties;

    public Ermanometry() {
        super();
        properties = new OverlayProperties();
    }

    public String getName() {
        return "Ermanometry";
    }

    public String getLabel() {
        return properties.getLabel();
    }

    public Overlay newInstance() {
        return new Ermanometry();
    }

    public LinkedHashMap getHTML(ChartFrame cf, int i) {
        return new LinkedHashMap();
    }

    public void paint(Graphics2D g, ChartFrame cf, Rectangle bounds) {
        Range range = cf.getSplitPanel().getChartPanel().getRange();

        g.setStroke(properties.getStroke());
        g.setColor(Color.black);

        double y = cf.getChartData().getY(range.getLowerBound(), bounds, range, false);
        g.draw(new Line2D.Double(bounds.getMinX(), y, bounds.getMaxX(), y));

        Dataset erm = visibleDataset(cf, ERM);

        if ( erm != null ) {
            g.setColor(Color.blue);

            for (int i = 0; i < erm.getItemsCount(); i++) {
                if ( erm.getDataItem(i) != null ) {
                    double x = cf.getChartData().getX(i, bounds);
                    double y1 = cf.getChartData().getY(range.getLowerBound(), bounds, range, false);
                    double y2 = cf.getChartData().getY(range.getUpperBound(), bounds, range, false);
                    double w = cf.getChartProperties().getBarWidth();

                    g.draw(new Line2D.Double(x - w/2, y1, x, y2));
                    g.draw(new Line2D.Double(x + w/2, y1, x, y2));
                }
            }
        }

        Dataset col = visibleDataset(cf, COL);

        if ( col != null ) {
            g.setColor(Color.green);

            for (int i = 0; i < col.getItemsCount(); i++) {
                if ( col.getDataItem(i) != null ) {
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
                    && c2.get(Calendar.HOUR_OF_DAY) == properties.getStartingHour()
                    && c2.get(Calendar.MINUTE) == properties.getStartingMinute() ) {
                start = i;
                break;
            }
        }

        if ( start != -1 ) {
            int ef = properties.getSeedSegmentEF();
            int de = properties.getSeedSegmentDE();

            double ratio = ef / de;
            double inverse_ratio = 1 / ratio;

            double cd = de * inverse_ratio;
            double bc = cd * inverse_ratio;
            double ab = bc * inverse_ratio;
            double fg = ef * ratio;
            double gh = fg * ratio;
            double hi = gh * ratio;
            double ij = hi * ratio;

            double fh = Math.sqrt(Math.pow(fg, 2) + Math.pow(gh, 2));

            Dataset erm = Dataset.EMPTY(initial.getItemsCount());
            
            for (int i = start; i < initial.getItemsCount(); i++) {
                int diff = i - start;
                boolean set = false;

                if ( diff == (int) fh 
                        || diff == (int) gh
                        || diff == (int) hi
                        || diff == (int) ij
                        || diff == (int) (de + ef + cd)
                        || diff == (int) (gh + hi + ij)
                        || diff == (int) (cd + de + ef + fg + gh + hi)
                        || diff == (int) (ef + fg + gh)
                        || diff == (int) (cd + de + ef + fg + gh)
                        || diff == (int) (cd + de + ef + fg + gh + hi)
                        || diff == (int) (gh + ij + cd + ab + ef))
                    set = true;

                if (set) erm.setDataItem(i, new DataItem(initial.getTimeAt(i), 1));
            }

            addDataset(ERM, erm);

            fh = Math.sqrt(Math.pow(fg, 2) + Math.pow(gh, 2));

            Dataset col = Dataset.EMPTY(initial.getItemsCount());

            for (int i = start; i < initial.getItemsCount(); i++) {
                int diff = i - start;
                boolean set = false;

                if ( diff == (int) (fh + fg + gh)
                        || diff == (int) (ab + bc + cd + de)
                        || diff == (int) (ab + bc + cd + de + gh)
                        || diff == (int) (fg + gh)
                        || diff == (int) (gh + hi)
                        || diff == (int) (fg + bc + cd)
                        || diff == (int) (fg + bc + cd + de)
                        || diff == (int) (cd + bc)
                        || diff == (int) (de + bc)
                        || diff == (int) (cd + de + ef + fg + gh + hi)
                        || diff == (int) (gh + ij + cd + ab + ef)
                        || diff == (int) (Math.sqrt(Math.pow(cd, 2) + Math.pow(de, 2)) + cd + de)
                        || diff == (int) (Math.sqrt(Math.pow(ef, 2) + Math.pow(fg, 2)) + ef + fg))
                    set = true;

                if (set) col.setDataItem(i, new DataItem(initial.getTimeAt(i), 1));
            }

            addDataset(COL, col);
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
