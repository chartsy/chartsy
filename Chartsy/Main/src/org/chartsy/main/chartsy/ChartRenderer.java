package org.chartsy.main.chartsy;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.LineMetrics;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import javax.swing.JFrame;
import org.chartsy.main.chartsy.chart.Indicator;
import org.chartsy.main.chartsy.chart.Overlay;
import org.chartsy.main.dataset.Dataset;
import org.chartsy.main.dialogs.LoaderDialog;
import org.chartsy.main.updater.AbstractUpdater;
import org.chartsy.main.utils.CoordCalc;
import org.chartsy.main.utils.Range;
import org.chartsy.main.utils.RectangleInsets;
import org.chartsy.main.utils.Stock;
import org.openide.windows.WindowManager;

/**
 *
 * @author viorel.gheba
 */
public class ChartRenderer implements Serializable {

    private static final long serialVersionUID = 101L;
    
    private ChartFrame chartFrame;
    private AbstractUpdater abstractUpdater;
    
    private Dataset mainDataset;
    private Dataset visibleDataset;

    private Range chartRange;

    private Indicator[] indicators;
    private Overlay[] overlays;

    private int items = -1;
    private int end = -1;

    private double barWidth;
    private double width;
    private double height;
    private double indicatorHeight = 200;
    
    private RectangleInsets axisOffset;
    private RectangleInsets dataOffset;

    public ChartRenderer(ChartFrame chartFrame) {
        this.chartFrame = chartFrame;
        this.abstractUpdater = chartFrame.getUpdater();
        this.mainDataset = chartFrame.getDataset();
        this.indicators = new Indicator[0];
        this.overlays = new Overlay[0];
        this.barWidth = chartFrame.getChartProperties().getBarWidth();
        this.axisOffset = chartFrame.getChartProperties().getAxisOffset();
        this.dataOffset = chartFrame.getChartProperties().getDataOffset();
        this.visibleDataset = null;
        this.chartRange = null;
    }

    public ChartFrame getChartFrame() { return chartFrame; }

    public Stock getStock() { return chartFrame.getStock(); }
    public String getTime() { return chartFrame.getTime(); }

    public void setUpdater(AbstractUpdater abstractUpdater) { this.abstractUpdater = abstractUpdater; }
    public AbstractUpdater getUpdater() { return abstractUpdater; }

    public boolean datasetExists() { return (mainDataset != null); }
    public boolean visibleDatasetExists() { return (visibleDataset != null); }

    public Dataset getMainDataset() { return mainDataset; }
    public void setMainDataset(Stock stock, String time) {
        if (abstractUpdater.datasetExists(stock, time)) {
            setMainDataset(abstractUpdater.getDataset(stock, time), true);
        } else {
            LoaderDialog loader = new LoaderDialog(new JFrame(), true);
            loader.setLocationRelativeTo(WindowManager.getDefault().getMainWindow());
            loader.updateIntraday(stock, time, abstractUpdater);
            loader.setVisible(true);
            
            if (!loader.isVisible()) {
                setMainDataset(abstractUpdater.getDataset(abstractUpdater.getKey(stock, time)), true);
            }
        }
    }
    public void setMainDataset(Dataset dataset, boolean init) {
        mainDataset = dataset;
        for (int i = 0; i < overlays.length; i++) { overlays[i].setDataset(dataset); overlays[i].calculate(); }
        for (int i = 0; i < indicators.length; i++) { indicators[i].setDataset(dataset); indicators[i].calculate(); }
        if (init) {
            setItems(-1);
            setEnd(-1);
        } else {
            if (getTime().contains("Min")) {
                if (getEnd() + 1 >= dataset.getItemCount()) {
                    setItems(-1);
                    setEnd(-1);
                }
            } else {
                if (getEnd() + 1 >= dataset.getItemCount()) {
                    setItems(-1);
                    setEnd(-1);
                }
            }
        }
        calculate();
    }

    public Dataset getVisibleDataset() { return visibleDataset; }

    public Indicator[] getIndicators() { return indicators; }
    public void setIndicators(Indicator[] list) { indicators = list.length != 0 ? list : new Indicator[0]; }
    public void paintIndicators(Graphics2D g) {
        if (indicators.length > 0) {
            for (int i = 0; i < indicators.length; i++) {
                indicators[i].paintIndicator(g, chartFrame);
            }
        }
    }
    public void setIndicatorBounds() {
        for (int i = 0; i < indicators.length; i++) {
            Indicator ind = indicators[i];
            ind.setBounds(getIndicatorBounds(i));
            ind.setAxisBounds(getIndicatorAxisBounds(i));
        }
    }

    public Overlay[] getOverlays() { return overlays; }
    public void setOverlays(Overlay[] list) { overlays = list.length != 0 ? list : new Overlay[0]; }
    public void paintOverlays(Graphics2D g) {
        if (overlays.length > 0) {
            for (int i = 0; i < overlays.length; i++) {
                overlays[i].paintOverlay(g, chartFrame);
            }
        }
    }
    public void setOverlaysBounds() {
        for (Overlay o : overlays) {
            o.setBounds(getChartBounds());
            o.setAxisBounds(getPriceAxisBounds());
        }
    }

    public void paintLabels(Graphics2D g) {
        LineMetrics lm = chartFrame.getChartProperties().getFont().getLineMetrics("012345679", g.getFontRenderContext());
        g.setPaint(chartFrame.getChartProperties().getAxisColor());
        g.setFont(chartFrame.getChartProperties().getFont());
        int h = (int) lm.getHeight();
        if (overlays.length > 0) {
            StringBuilder sb = new StringBuilder();
            String d = ", ";
            for (Overlay o : overlays) {
                String label = o.getLabel();
                sb.append(label + d);
            }
            sb.delete(sb.length() - d.length(), sb.length());
            g.drawString(sb.toString(), (float) chartFrame.getChartProperties().getDataOffset().left, lm.getAscent() + h);
        }

        Font font = new Font(chartFrame.getChartProperties().getFont().getName(), Font.BOLD, 12);
        g.setFont(font);
        g.drawString(getStock().getKey() + " - " + (getStock().getCompanyName().equals("") ? "" : getStock().getCompanyName() + " ") + "(" + getTime() + ")", (float) chartFrame.getChartProperties().getDataOffset().left, lm.getAscent());
        g.drawString("Chartsy.org \u00a9 2009-2010 mrswing.com", (float) chartFrame.getChartProperties().getDataOffset().left, (float) (getHeight() - lm.getAscent()));
    }

    public Range getChartRange() { return chartRange; }
    public void setChartRange(Range r) { chartRange = r; }

    public double getWidth() { return width; }
    public void setWidth(double w) { width = w; }

    public double getHeight() { return height; }
    public void setHeight(double h) { height = h; }

    public void calculate() {
        int w = chartFrame.getChartPanel().getWidth();
        if (w != 0) {
            setWidth(w);
            barWidth = chartFrame.getChartProperties().getBarWidth();
            end = end == -1 ? mainDataset.getItemCount() : end;
            items = (int) ((width - dataOffset.left - dataOffset.right - axisOffset.left - axisOffset.right) / (barWidth + 2));
            items = items > mainDataset.getItemCount() ? mainDataset.getItemCount() : items;
            visibleDataset = mainDataset.getDrawableDataset(items, end);
            chartRange = new Range(visibleDataset.getMin(), visibleDataset.getMax());
            for (Overlay o : overlays) chartRange = Range.combine(chartRange, o.getRange(chartFrame));
            indicatorHeight = indicators.length == 0 ? 0 : (((height / 5) * 2) / indicators.length);
            int index = chartFrame.getMarker().getIndex();
            chartFrame.getMarker().setIndex(index > items-1 ? items-1 : index);
            chartFrame.updateHorizontalScrollBar(end);
        }
    }

    public int getItems() { return items; }
    public void setItems(int i) { items = i; }

    public int getEnd() { return end; }
    public void setEnd(int e) { end = e; }

    public int getAreaIndex(double x, double y) {
        if (getChartBounds().contains(x, y)) return 0;
        for (int i = 0; i < indicators.length; i++)
            if (getIndicatorBounds(i).contains(x, y))
                return (i + 1);
        return -1;
    }

    public Range getClickedRange(int areaIndex) {
        switch (areaIndex) {
            case 0:
                return getChartRange();
            default:
                if (indicators[areaIndex-1] != null)
                    return indicators[areaIndex-1].getRange(chartFrame);
        }
        return null;
    }

    public Rectangle2D.Double getClickedBounds(int areaIndex) {
        switch (areaIndex) {
            case 0:
                return getChartBounds();
            default:
                if (indicators.length > (areaIndex-1))
                    return getIndicatorBounds(areaIndex-1);
        }
        return null;
    }

    public Rectangle2D.Double getChartFrameBounds() {
        double x = dataOffset.left;
        double y = dataOffset.top;
        double w = width - dataOffset.left - dataOffset.right;
        double h = height - dataOffset.top - dataOffset.bottom;
        return new Rectangle2D.Double(x, y, w, h);
    }

    public Rectangle2D.Double getChartBounds() {
        double x = dataOffset.left + axisOffset.left;
        double y = dataOffset.top + axisOffset.top;
        double w = width - dataOffset.left - dataOffset.right - axisOffset.left - axisOffset.right;
        double h = height - dataOffset.top - dataOffset.bottom - indicatorHeight * indicators.length - axisOffset.top - axisOffset.bottom;
        return new Rectangle2D.Double(x, y, w, h);
    }
    
    public Rectangle2D.Double getDateAxisBounds() {
        double x = dataOffset.left + axisOffset.left;
        double y = height - dataOffset.bottom;
        double w = width - dataOffset.left - dataOffset.right - axisOffset.left - axisOffset.right;
        double h = dataOffset.bottom;
        return new Rectangle2D.Double(x, y, w, h);
    }

    public Rectangle2D.Double getPriceAxisBounds() {
        double x = width - dataOffset.right;
        double y = dataOffset.top + axisOffset.top;
        double w = dataOffset.right;
        double h = height - dataOffset.top - dataOffset.bottom - indicatorHeight * indicators.length - axisOffset.top - axisOffset.bottom;
        return new Rectangle2D.Double(x, y, w, h);
    }

    public Rectangle2D.Double getIndicatorBounds(int i) {
        double x = dataOffset.left + axisOffset.left;
        double y = dataOffset.top + axisOffset.top + getChartBounds().getHeight() + axisOffset.bottom + i * indicatorHeight + axisOffset.top;
        double w = width - dataOffset.left - dataOffset.right - axisOffset.left - axisOffset.right;
        double h = indicatorHeight - axisOffset.top - axisOffset.bottom;
        return new Rectangle2D.Double(x, y, w, h);
    }

    public Rectangle2D.Double getIndicatorAxisBounds(int i) {
        double x = width - dataOffset.right;
        double y = dataOffset.top + axisOffset.top + getChartBounds().getHeight() + axisOffset.bottom + i * indicatorHeight + axisOffset.top;
        double w = dataOffset.right;
        double h = indicatorHeight - axisOffset.top - axisOffset.bottom;
        return new Rectangle2D.Double(x, y, w, h);
    }

    public Point2D.Double valueToJava2D(final double xvalue, final double yvalue) {
        final Rectangle2D.Double bounds = getChartBounds();
        return valueToJava2D(xvalue, yvalue, bounds);
    }

    public Point2D.Double valueToJava2D(final double xvalue, final double yvalue, Rectangle2D.Double bounds) {
        return valueToJava2D(xvalue, yvalue, bounds, chartRange);
    }

    public Point2D.Double valueToJava2D(final double xvalue, final double yvalue, Rectangle2D.Double bounds, Range range) {
        double x = (bounds.getWidth() / items) * xvalue;
        double c = bounds.getWidth() / (2 * visibleDataset.getItemCount());
        double px = bounds.getMinX() + x + c;

        double dif = range.getUpperBound() - range.getLowerBound();
        double percent = ((range.getUpperBound() - yvalue) / dif) * 100;
        double py = bounds.getMinY() + (bounds.getHeight() * percent) / 100;

        if (range.contains(0)) {
            dif = Math.abs(range.getUpperBound()) + Math.abs(range.getLowerBound());

            double h1 = (Math.abs(range.getUpperBound()) * bounds.getHeight()) / dif;
            double h2 = (Math.abs(range.getLowerBound()) * bounds.getHeight()) / dif;

            if (yvalue >= 0) {
                percent = ((range.getUpperBound() - yvalue) / range.getUpperBound()) * 100;
                py = bounds.getMinY() + (h1 * percent) / 100;
            } else {
                percent = ((Math.abs(range.getLowerBound()) - Math.abs(yvalue)) / Math.abs(range.getLowerBound())) * 100;
                py = bounds.getMinY() + h1 + (h2 - ((h2 * percent) / 100));
            }
        }

        Point2D.Double p = new Point2D.Double(px, py);

        return p;
    }

    public Point2D.Double valueToJava2DX(final double xvalue) {
        final Rectangle2D.Double bounds = getChartBounds();
        return valueToJava2DX(xvalue, bounds);
    }

    public Point2D.Double valueToJava2DX(final double xvalue, final Rectangle2D.Double bounds) {
        double x = (bounds.getWidth() / items) * xvalue;
        double c = bounds.getWidth() / (2 * visibleDataset.getItemCount());
        double px = bounds.getMinX() + x + c;

        double py = bounds.getMinY();

        Point2D.Double p = new Point2D.Double(px, py);

        return p;
    }

    public Point2D.Double valueToJava2DY(double yvalue) {
        Rectangle2D.Double bounds = getChartBounds();
        return valueToJava2DY(yvalue, bounds);
    }

    public Point2D.Double valueToJava2DY(double yvalue, Rectangle2D.Double bounds) {
        Range range = getChartRange();
        return valueToJava2DY(yvalue, bounds, range);
    }

    public Point2D.Double valueToJava2DY(double yvalue, Rectangle2D.Double bounds, Range range) {
        double px = bounds.getMinX();

        double dif = chartRange.getUpperBound() - chartRange.getLowerBound();
        double percent = ((chartRange.getUpperBound() - yvalue) / dif) * 100;
        double py = bounds.getMinY() + (bounds.getHeight() * percent) / 100;

        if (range.contains(0)) {
            dif = Math.abs(range.getUpperBound()) + Math.abs(range.getLowerBound());

            double h1 = (Math.abs(range.getUpperBound()) * bounds.getHeight()) / dif;
            double h2 = (Math.abs(range.getLowerBound()) * bounds.getHeight()) / dif;

            if (yvalue >= 0) {
                percent = ((range.getUpperBound() - yvalue) / range.getUpperBound()) * 100;
                py = bounds.getMinY() + (h1 * percent) / 100;
            } else {
                percent = ((Math.abs(range.getLowerBound()) - Math.abs(yvalue)) / Math.abs(range.getLowerBound())) * 100;
                py = bounds.getMinY() + h1 + (h2 - ((h2 * percent) / 100));
            }
        }

        Point2D.Double p = new Point2D.Double(px, py);

        return p;
    }

    public double getX(double x) { return CoordCalc.getX(this, x); }
    public long xToLong(double x) { return CoordCalc.xToLong(this, x); }
    public int longIndex(long t) { return CoordCalc.longIndex(this, t); }
    public double longToX(long t) { return CoordCalc.longToX(this, t); }

    public boolean hasPrevT(long t) { return CoordCalc.hasPrevT(this, t); }
    public long getPrevT(long t) { return CoordCalc.getPrevT(this, t); }
    public boolean hasNext(long t) { return CoordCalc.hasNextT(this, t); }
    public long getNextT(long t) { return CoordCalc.getNextT(this, t); }

    public double yToValue(double y) { return CoordCalc.yToValue(this, y, getChartBounds(), getChartRange()); }
    public double yToValue(double y, Rectangle2D.Double bounds, Range range) { return CoordCalc.yToValue(this, y, bounds, range); }
    public double valueToY(double value) { return CoordCalc.valueToY(this, value, getChartBounds(), getChartRange()); }
    public double valueToY(double value, Rectangle2D.Double bounds, Range range) { return CoordCalc.valueToY(this, value, bounds, range); }

    public void zoomIn() {
        double newWidth = barWidth + 1;
        int i = (int)((items * barWidth) / newWidth);
        newWidth = i < 20 ? barWidth : newWidth;
        chartFrame.getChartProperties().setBarWidth(newWidth);
        barWidth = newWidth;
        calculate();
        chartFrame.getChartPanel().repaint();
    }

    public void zoomOut() {
        double newWidth = barWidth - 1;
        int i = (int)((items * barWidth) / newWidth);
        newWidth = i > mainDataset.getItemCount() ? barWidth : (newWidth >= 2 ? newWidth : barWidth);
        chartFrame.getChartProperties().setBarWidth(newWidth);
        barWidth = newWidth;
        calculate();
        chartFrame.getChartPanel().repaint();
    }

}
