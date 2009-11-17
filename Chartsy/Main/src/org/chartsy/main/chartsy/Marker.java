package org.chartsy.main.chartsy;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import org.chartsy.main.chartsy.chart.Indicator;
import org.chartsy.main.chartsy.chart.Overlay;
import org.chartsy.main.dataset.Dataset;
import org.chartsy.main.utils.RectangleInsets;

/**
 *
 * @author viorel.gheba
 */
public class Marker {

    public static final int LEFT = 0;
    public static final int CENTER = 1;
    public static final int RIGHT = 2;
    private ChartFrame cf;
    private static JLabel label = new JLabel();
    private int index = -1;
    private int width = 150;
    private int height = 15;
    private int fontSize = 10;
    private Color lineColor = new Color(0xef2929);
    private Color color = new Color(0x555753);
    private Color backgroundColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 100);
    private Color fontColor = new Color(0xffffff);
    private Font font;

    public static Marker newInstance(ChartFrame cf) { return new Marker(cf); }
    private Marker(ChartFrame c) {
        cf = c;
        Font f = cf.getChartProperties().getFont();
        font = new Font(f.getName(), f.getStyle(), fontSize);
    }

    private String addLine(String left, String right) {
        if (!right.equals(" ")) {
            return "<tr><td width='" + width/2 + "' height='" + fontSize + "' valign='middle' align='left'>&nbsp;" + left + "</td><td width='" + width/2 +"' height='" + fontSize + "' valign='middle' align='right'>" + right + "</td></tr>";
        } else {
            return "<tr><td width='" + width + "' height='" + fontSize + "' valign='middle' align='left' colspan='2'>&nbsp;" + left + "</td></tr>";
        }
    }

    public void paint(Graphics2D g) {
        if (cf.getChartProperties().getMarkerVisibility()) {
            if (index != -1 && index <= cf.getChartRenderer().getItems() && index >= 0) {
                Dataset dataset = cf.getChartRenderer().getVisibleDataset();
                Calendar cal = Calendar.getInstance();
                cal.setTime(dataset.getDate(index));

                String s = (!cf.getTime().contains("Min"))
                ? ((cal.get(Calendar.MONTH) + 1) < 10 ? "0" + String.valueOf(cal.get(Calendar.MONTH) + 1) : String.valueOf(cal.get(Calendar.MONTH) + 1)) + "/" +
                    (cal.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + String.valueOf(cal.get(Calendar.DAY_OF_MONTH)) : String.valueOf(cal.get(Calendar.DAY_OF_MONTH))) + "/" +
                    String.valueOf(cal.get(Calendar.YEAR))
                : ((cal.get(Calendar.MONTH) + 1) < 10 ? "0" + String.valueOf(cal.get(Calendar.MONTH) + 1) : String.valueOf(cal.get(Calendar.MONTH) + 1)) + "/" +
                    (cal.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + String.valueOf(cal.get(Calendar.DAY_OF_MONTH)) : String.valueOf(cal.get(Calendar.DAY_OF_MONTH))) + "/" +
                    String.valueOf(cal.get(Calendar.YEAR) + " " + (cal.get(Calendar.HOUR) < 10 ? "0" + String.valueOf(cal.get(Calendar.HOUR)) : String.valueOf(cal.get(Calendar.HOUR))) + ":" +
                    (cal.get(Calendar.MINUTE) < 10 ? "0" + String.valueOf(cal.get(Calendar.MINUTE)) : String.valueOf(cal.get(Calendar.MINUTE))));

                paintLine(g, s);

                NumberFormat nf = NumberFormat.getNumberInstance();
                DecimalFormat df = (DecimalFormat) nf;
                df.applyPattern("#,###.00");

                String html = "<html><table width='" + width + "' cellpadding='0' cellspacing='0' border='0'>";
                html += addLine(" ", " ");
                html += addLine(" ", " ");
                html += addLine(" ", " ");
                // Date:
                html += addLine("Date:", s);
                // Open:
                html += addLine("Open:", df.format(dataset.getOpenValue(index)));
                // High:
                html += addLine("High:", df.format(dataset.getHighValue(index)));
                // Low:
                html += addLine("Low:", df.format(dataset.getLowValue(index)));
                // Close:
                html += addLine("Close:", df.format(dataset.getCloseValue(index)));

                int hl = 5;

                Overlay[] overlays = cf.getChartRenderer().getOverlays();
                Indicator[] indicators = cf.getChartRenderer().getIndicators();
                if (overlays.length > 0 || indicators.length > 0) {
                    html += addLine(" ", " ");
                    hl++;
                }

                // Overlays
                if (overlays.length > 0) {
                    for (Overlay o : overlays) {
                        LinkedHashMap ht = o.getHTML(cf, index);
                        Iterator it = ht.keySet().iterator();
                        while (it.hasNext()) {
                            Object key = it.next();
                            html += addLine(key.toString(), ht.get(key).toString());
                            hl++;
                        }
                    }
                }

                // Indicators
                if (indicators.length > 0) {
                    if (overlays.length > 0) {
                        html += addLine(" ", " ");
                        hl++;
                    }
                    for (Indicator i : indicators) {
                        LinkedHashMap ht = i.getHTML(cf, index);
                        Iterator it = ht.keySet().iterator();
                        while (it.hasNext()) {
                            Object key = it.next();
                            html += addLine(key.toString(), ht.get(key).toString());
                            hl++;
                        }
                    }
                }

                html += "</table></html>";

                label.setText(html);
                label.setBounds(0, 35, width, height * hl);
                label.setVerticalAlignment(SwingConstants.TOP);
                label.setVerticalTextPosition(SwingConstants.TOP);

                g.setPaint(backgroundColor);
                g.fill(label.getBounds());
                g.setPaint(color);
                g.draw(label.getBounds());

                g.setFont(font);
                g.setColor(fontColor);
                label.setFont(font);
                label.setForeground(fontColor);
                label.paint(g);
            }
        }
    }

    private void paintLine(Graphics2D g, String s) {
        RectangleInsets dataOffset = cf.getChartProperties().getDataOffset();

        Point2D.Double p = cf.getChartRenderer().valueToJava2D(index, 0);
        g.setFont(font);

        FontMetrics fm = g.getFontMetrics(font);
        int w = fm.stringWidth(s) + 2;
        int h = fm.getHeight() + 2;
        float x = (float) p.getX() + 1;
        float y = (float) fm.getAscent() + 1;

        g.setPaint(fontColor);
        g.fill(new Rectangle2D.Double(p.getX(), 0, w, h));

        g.setPaint(lineColor);
        g.draw(new Line2D.Double(p.getX(), 0, p.getX(), cf.getChartRenderer().getHeight() - dataOffset.bottom));
        g.draw(new Rectangle2D.Double(p.getX(), 0, w, h));
        g.drawString(s, x, y);
    }

    public void setIndex(int i) { index = i; }
    public int getIndex() { return index; }

    public int getIndex(double x, double y) {
        int idx = -1;
        int items = cf.getChartRenderer().getItems();
        double w = cf.getChartRenderer().getChartBounds().getWidth() / items;

        for (int i = 0; i < items; i++) {
            Rectangle2D.Double rect = new Rectangle2D.Double(cf.getChartRenderer().getChartBounds().getMinX() + (i * w), 0, w, cf.getChartRenderer().getHeight());
            if (rect.contains(new Point2D.Double(x, y))) { idx = i; break; }
        }

        return idx;
    }

    public void moveRight() {
        int all = cf.getChartRenderer().getMainDataset().getItemCount();
        int end = cf.getChartRenderer().getEnd();
        int items = cf.getChartRenderer().getItems() - 1;
        int idx = index + 1;
        if (idx > items) {
            if (end < all) {
                cf.getChartRenderer().setEnd(end + 1);
            }
        } else {
            setIndex(idx);
        }
    }

    public void moveLeft() {
        int end = cf.getChartRenderer().getEnd();
        int items = cf.getChartRenderer().getItems() - 1;
        int idx = index - 1;
        if (idx < 0) {
            if (end-1 > items) {
                cf.getChartRenderer().setEnd(end - 1);
            }
        } else {
            setIndex(idx);
        }
    }

}
