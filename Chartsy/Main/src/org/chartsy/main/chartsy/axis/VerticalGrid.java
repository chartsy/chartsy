package org.chartsy.main.chartsy.axis;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Calendar;
import org.chartsy.main.chartsy.ChartFrame;
import org.chartsy.main.dataset.Dataset;
import org.chartsy.main.utils.RectangleInsets;

/**
 *
 * @author viorel.gheba
 */
public class VerticalGrid {

    private VerticalGrid() {}

    public static void paint(Graphics2D g, ChartFrame cf) {
        if (cf.getChartProperties().getGridVerticalVisibility()) {
            Dataset dataset = cf.getChartRenderer().getVisibleDataset(); // get dataset
            RectangleInsets dataOffset = cf.getChartProperties().getDataOffset(); // get data offset
            g.setPaint(cf.getChartProperties().getGridVerticalColor()); // set vertical grid color
            g.setStroke(cf.getChartProperties().getGridVerticalStroke()); // set vertical grid stroke
            Calendar cal = Calendar.getInstance();
            cal.setTime(dataset.getDate(0));
            if (!cf.getTime().contains("Min")) {
                int month = cal.get(Calendar.MONTH);
                for(int j = 0; j < dataset.getItemCount(); j++) {
                    cal.setTime(dataset.getDate(j));
                    if (month != cal.get(Calendar.MONTH)) {
                        Point2D.Double point = cf.getChartRenderer().valueToJava2DX(j); // get point
                        g.draw(new Line2D.Double(point.getX(), dataOffset.top, point.getX(), cf.getChartRenderer().getHeight() - dataOffset.bottom)); // paint vertical line
                        month = cal.get(Calendar.MONTH);
                    }
                }
            } else {
                for (int j = 0; j < dataset.getItemCount(); j++) {
                    if (j % 5 == 0) {
                        Point2D.Double point = cf.getChartRenderer().valueToJava2DX(j); // get point
                        g.draw(new Line2D.Double(point.getX(), dataOffset.top, point.getX(), cf.getChartRenderer().getHeight() - dataOffset.bottom)); // paint vertical line
                    }
                }
            }
        }
    }

}
