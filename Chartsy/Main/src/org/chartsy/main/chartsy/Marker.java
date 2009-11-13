package org.chartsy.main.chartsy;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;
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
    private String text;
    private ChartFrame cf;
    private static JLabel label = new JLabel();
    private int index = -1;
    private int width;
    private int height;
    private Color lineColor = new Color(0xef2929);
    private Color color = new Color(0x555753);
    private Color backgroundColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 100);
    private Color fontColor = new Color(0xffffff);
    private Font font;

    public static Marker newInstance(ChartFrame cf) { return new Marker(cf); }
    private Marker(ChartFrame c) { cf = c; Font f = cf.getChartProperties().getFont(); font = new Font(f.getName(), f.getStyle(), 10); text = ""; }

    public void setText(String t) { text = t; }
    public String getText() { return text; }

    private void setLabelText(Graphics2D g) {
        StringBuffer prefix = new StringBuffer();
        StringBuffer suffix = new StringBuffer();
        StringBuffer htmlText = new StringBuffer();

        prefix.append("&nbsp;");
        suffix.insert(0, "&nbsp;");
        htmlText.append("<html>");

        StringTokenizer tokenizer = new StringTokenizer(text, "\n");
        boolean first = true;

        FontMetrics fm = g.getFontMetrics(font);

        width = 0;
        height = fm.getHeight();

        int i = 0;
        while (tokenizer.hasMoreTokens()) {
            if (first) first = false;
            else htmlText.append("<br>");

            htmlText.append(prefix);
            String string = tokenizer.nextToken();
            htmlText.append("<b>");
            htmlText.append(string);
            htmlText.append("</b>");
            htmlText.append(suffix);

            if (string.contains(">")) {
                string = string.split(">")[1];
                string = string.split("<")[0];
                string = string.replace("&nbsp;", " ");
            }
            width = Math.max(width, fm.stringWidth(string));

            i++;
        }

        htmlText.append("</html>");

        List dontReplace = Arrays.asList(new String[] { "u", "i", "b", "tt", "font", "br" });

        int ltpos = 0;
        while (ltpos != -1) {
            ltpos = htmlText.indexOf("<", ltpos + 1);
            if (ltpos != -1 && !(ltpos + 1 < htmlText.length() && htmlText.charAt(ltpos + 1) == '/')) {
                int end = ltpos + 1;
                while (end < htmlText.length() && Character.isLetter(htmlText.charAt(end))) end++;
                    if (!dontReplace.contains(htmlText.substring(ltpos + 1, end)))
                        htmlText.replace(ltpos, ltpos+1, "&lt;");
            }
        }

        label.setText(htmlText.toString());
        label.setBounds(0, 0, width + 10, height * i);
        label.setHorizontalAlignment(SwingConstants.LEFT);
        label.setVerticalAlignment(SwingConstants.TOP);
        label.setHorizontalTextPosition(SwingConstants.LEFT);
        label.setVerticalTextPosition(SwingConstants.TOP);
    }

    public Rectangle2D getBounds() {
        if (text.length() == 0) return new Rectangle2D.Double();
        Dimension dim = label.getPreferredSize();
        return new Rectangle2D.Double(0, 0, dim.getWidth(), dim.getHeight());
    }

    private String addLine(String left, String right) {
        String html = "";
        html += "<tr>";
        html += "<td width='75' height='15' valign='middle' align='left' style='font-weight:bold;'>";
        html += left;
        html += "</td>";
        html += "<td width='75' height='15' valign='middle' align='right'>";
        html += right;
        html += "</td>";
        html += "</tr>";
        return html;
    }

    public void paint(Graphics2D g) {
        if (cf.getChartProperties().getMarkerVisibility()) {
            if (index != -1 && index <= cf.getChartRenderer().getItems() && index >= 0) {
                Dataset dataset = cf.getChartRenderer().getVisibleDataset();
                paintLine(g);

                FontMetrics fm = g.getFontMetrics(font);
                height = fm.getHeight();


                NumberFormat nf = NumberFormat.getNumberInstance();
                DecimalFormat df = (DecimalFormat) nf;
                df.applyPattern("#,###.00");
                
                Calendar cal = Calendar.getInstance();
                cal.setTime(dataset.getDate(index));

                String html = "<html><table width='150'>";
                // Date:
                String s = (!cf.getTime().contains("Min"))
                ? ((cal.get(Calendar.MONTH) + 1) < 10 ? "0" + String.valueOf(cal.get(Calendar.MONTH) + 1) : String.valueOf(cal.get(Calendar.MONTH) + 1)) + "/" +
                    (cal.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + String.valueOf(cal.get(Calendar.DAY_OF_MONTH)) : String.valueOf(cal.get(Calendar.DAY_OF_MONTH))) + "/" +
                    String.valueOf(cal.get(Calendar.YEAR))
                : ((cal.get(Calendar.MONTH) + 1) < 10 ? "0" + String.valueOf(cal.get(Calendar.MONTH) + 1) : String.valueOf(cal.get(Calendar.MONTH) + 1)) + "/" +
                    (cal.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + String.valueOf(cal.get(Calendar.DAY_OF_MONTH)) : String.valueOf(cal.get(Calendar.DAY_OF_MONTH))) + "/" +
                    String.valueOf(cal.get(Calendar.YEAR) + " " + (cal.get(Calendar.HOUR) < 10 ? "0" + String.valueOf(cal.get(Calendar.HOUR)) : String.valueOf(cal.get(Calendar.HOUR))) + ":" +
                    (cal.get(Calendar.MINUTE) < 10 ? "0" + String.valueOf(cal.get(Calendar.MINUTE)) : String.valueOf(cal.get(Calendar.MINUTE))));
                html += addLine("Date:", s);
                // Open:
                html += addLine("Open:", df.format(dataset.getOpenValue(index)));
                // High:
                html += addLine("High:", df.format(dataset.getHighValue(index)));
                // Low:
                html += addLine("Low:", df.format(dataset.getLowValue(index)));
                // Close:
                html += addLine("Close:", df.format(dataset.getCloseValue(index)));

                html += "</table></html>";

                System.out.println(html);

                /*String s = (!cf.getTime().contains("Min"))
                ? ((cal.get(Calendar.MONTH) + 1) < 10 ? "0" + String.valueOf(cal.get(Calendar.MONTH) + 1) : String.valueOf(cal.get(Calendar.MONTH) + 1)) + "/" +
                    (cal.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + String.valueOf(cal.get(Calendar.DAY_OF_MONTH)) : String.valueOf(cal.get(Calendar.DAY_OF_MONTH))) + "/" +
                    String.valueOf(cal.get(Calendar.YEAR))
                : ((cal.get(Calendar.MONTH) + 1) < 10 ? "0" + String.valueOf(cal.get(Calendar.MONTH) + 1) : String.valueOf(cal.get(Calendar.MONTH) + 1)) + "/" +
                    (cal.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + String.valueOf(cal.get(Calendar.DAY_OF_MONTH)) : String.valueOf(cal.get(Calendar.DAY_OF_MONTH))) + "/" +
                    String.valueOf(cal.get(Calendar.YEAR) + " " + (cal.get(Calendar.HOUR) < 10 ? "0" + String.valueOf(cal.get(Calendar.HOUR)) : String.valueOf(cal.get(Calendar.HOUR))) + ":" +
                    (cal.get(Calendar.MINUTE) < 10 ? "0" + String.valueOf(cal.get(Calendar.MINUTE)) : String.valueOf(cal.get(Calendar.MINUTE))));

                text =  "Date:   " + s + "\n" + "Open:   " + df.format(dataset.getOpenValue(index)) + "\n" + "High:   " + df.format(dataset.getHighValue(index)) + "\n" + "Low:    " + df.format(dataset.getLowValue(index)) + "\n" + "Close:  " + df.format(dataset.getCloseValue(index));
                Indicator[] indicators = cf.getChartRenderer().getIndicators();
                Overlay[] overlays = cf.getChartRenderer().getOverlays();
                if (overlays.length > 0 || indicators.length > 0) text += "&nbsp;\n&nbsp;\n";
                if (overlays.length > 0) for (Overlay o : overlays) text += o.getMarkerLabel(cf, index) + "\n";
                if (indicators.length > 0) for (Indicator i : indicators) text += i.getMarkerLabel(cf, index) + "\n";
                setLabelText(g);*/

                label.setText(html);
                label.setBounds(0, 0, 150, height * 10);
                //label.setHorizontalAlignment(SwingConstants.LEFT);
                label.setVerticalAlignment(SwingConstants.TOP);
                //label.setHorizontalTextPosition(SwingConstants.LEFT);
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

    private void paintLine(Graphics2D g) {
        RectangleInsets dataOffset = cf.getChartProperties().getDataOffset();
        Dataset dataset = cf.getChartRenderer().getVisibleDataset();

        g.setPaint(lineColor);
        g.setFont(font);

        Point2D.Double p = cf.getChartRenderer().valueToJava2D(index, 0);

        g.draw(new Line2D.Double(p.getX(), 0, p.getX(), cf.getChartRenderer().getHeight() - dataOffset.bottom));

        Calendar cal = Calendar.getInstance();
        cal.setTime(dataset.getDate(index));

        String s = (!cf.getTime().contains("Min"))
                ? ((cal.get(Calendar.MONTH) + 1) < 10 ? "0" + String.valueOf(cal.get(Calendar.MONTH) + 1) : String.valueOf(cal.get(Calendar.MONTH) + 1)) + "/" +
                    (cal.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + String.valueOf(cal.get(Calendar.DAY_OF_MONTH)) : String.valueOf(cal.get(Calendar.DAY_OF_MONTH))) + "/" +
                    String.valueOf(cal.get(Calendar.YEAR))
                : (cal.get(Calendar.HOUR) < 10 ? "0" + String.valueOf(cal.get(Calendar.HOUR)) : String.valueOf(cal.get(Calendar.HOUR))) + ":" +
                    (cal.get(Calendar.MINUTE) < 10 ? "0" + String.valueOf(cal.get(Calendar.MINUTE)) : String.valueOf(cal.get(Calendar.MINUTE)));

        FontMetrics fm = g.getFontMetrics(font);
        int w = fm.stringWidth(s) + 2;
        int h = fm.getHeight() + 2;
        float x = (float) p.getX() + 1;
        float y = (float) fm.getAscent() + 1;
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
