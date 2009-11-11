package org.chartsy.main.chartsy.axis;

import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Calendar;
import org.chartsy.main.chartsy.ChartFrame;
import org.chartsy.main.dataset.Dataset;
import org.chartsy.main.managers.DatasetManager;
import org.chartsy.main.utils.RectangleInsets;

/**
 *
 * @author viorel.gheba
 */
public class DateAxis {

    private DateAxis() {}

    public static void paint(Graphics2D g, ChartFrame cf) {
        Dataset dataset = cf.getChartRenderer().getVisibleDataset(); // get dataset
        Rectangle2D.Double bounds = cf.getChartRenderer().getDateAxisBounds(); // get axis bounds
        RectangleInsets axisOffset = cf.getChartProperties().getAxisOffset(); // get axis offset
        String[] months = cf.getChartProperties().getMonths(); // get months array
        double axisTick = cf.getChartProperties().getAxisTick(); // get axis tick
        double axisStick = cf.getChartProperties().getAxisDateStick(); // get axis stick

        g.setPaint(cf.getChartProperties().getBackgroundColor()); // set background color
        g.fill(cf.getChartRenderer().getDateAxisBounds()); // paint axis bounds
        g.setPaint(cf.getChartProperties().getAxisColor()); // set axis color
        g.setStroke(cf.getChartProperties().getAxisStroke()); // set axis stroke
        g.draw(new Line2D.Double(bounds.getMinX() - axisOffset.left, bounds.getMinY(), bounds.getMaxX() + axisOffset.right, bounds.getMinY())); // paint axis line
        
        g.setFont(cf.getChartProperties().getFont()); // set font
        FontRenderContext frc = g.getFontRenderContext();
        LineMetrics lm = cf.getChartProperties().getFont().getLineMetrics("0123456789/", g.getFontRenderContext()); // set line metrics

        if (!cf.getTime().contains("Min")) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(dataset.getDate(0));
            int month = cal.get(Calendar.MONTH);
            for(int j = 0; j < dataset.getItemCount(); j++) {
                cal.setTime(dataset.getDate(j)); // set calendar date
                if (month != cal.get(Calendar.MONTH)) {
                    g.setPaint(cf.getChartProperties().getAxisColor()); // set axis color
                    Point2D.Double point = cf.getChartRenderer().valueToJava2D(j, j); // get point
                    g.draw(new Line2D.Double(point.getX(), bounds.getMinY(), point.getX(), bounds.getMinY() + axisTick)); // paint tick
                    g.setPaint(cf.getChartProperties().getFontColor()); // set font color
                    String s = months[cal.get(Calendar.MONTH)];
                    s = s.equals("Jan") ? String.valueOf(cal.get(Calendar.YEAR)).substring(2) : (cf.getTime().equals(DatasetManager.MONTHLY) ? s.substring(0, 1) : s);
                    float w = (float)(cf.getChartProperties().getFont().getStringBounds(s, frc).getWidth());
                    g.drawString(s, (float)(point.getX() - w / 2), (float)(bounds.getMinY() + axisTick + axisStick + lm.getAscent())); // paint label
                    month = cal.get(Calendar.MONTH);
                }
            }
        } else {
            Calendar cal = Calendar.getInstance();
            for(int j = 0; j < dataset.getItemCount(); j++) {
                if (j % 10 == 0) {
                    cal.setTime(dataset.getDate(j)); // set calendar date
                    g.setPaint(cf.getChartProperties().getAxisColor()); // set axis color
                    Point2D.Double point = cf.getChartRenderer().valueToJava2DX(j, bounds); // get point
                    g.draw(new Line2D.Double(point.getX(), point.getY(), point.getX(), point.getY() + axisTick)); // paint tick
                    g.setPaint(cf.getChartProperties().getFontColor()); // set font color
                    String s = (cal.get(Calendar.HOUR) < 10 ? "0" + String.valueOf(cal.get(Calendar.HOUR)) : String.valueOf(cal.get(Calendar.HOUR))) + ":" +
                            (cal.get(Calendar.MINUTE) < 10 ? "0" + String.valueOf(cal.get(Calendar.MINUTE)) : String.valueOf(cal.get(Calendar.MINUTE)));
                    float w = (float)(cf.getChartProperties().getFont().getStringBounds(s, frc).getWidth());
                    g.drawString(s, (float)(point.getX() - w/2), (float)(point.getY() + axisTick + axisStick + lm.getAscent())); // paint label
                }
            }
        }
    }

}
