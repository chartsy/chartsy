package org.chartsy.main.chartsy;

import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import javax.swing.BoundedRangeModel;
import javax.swing.JScrollBar;
import java.util.Timer;
import java.util.TimerTask;
import org.chartsy.main.chartsy.chart.AbstractChart;
import org.chartsy.main.dataset.Dataset;
import org.chartsy.main.managers.ChartFrameManager;
import org.chartsy.main.managers.ChartManager;
import org.chartsy.main.managers.DatasetManager;
import org.chartsy.main.managers.UpdaterManager;
import org.chartsy.main.updater.AbstractUpdater;
import org.chartsy.main.utils.XMLUtils;
import org.openide.windows.TopComponent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author viorel.gheba
 */
public class ChartFrame extends TopComponent implements AdjustmentListener, XMLUtils.ToXML {

    private String symbol;
    private String chartName;
    private String time;
    private String id;
    private boolean forced = false;
    
    private AbstractChart chart;
    private AbstractUpdater active;

    private ChartToolbar chartToolbar;
    private ChartPanel chartPanel;
    private ChartProperties chartProperties;
    private ChartRenderer chartRenderer;
    private JScrollBar horizontalBar;
    private Marker marker;
    private Timer timer;

    public ChartFrame() {}

    public ChartFrame(String symbol, String chart) {
        this(symbol, chart, null);
    }

    public ChartFrame(String s, String c, String displayName) {
        symbol = s;
        chartName = c;
        active = UpdaterManager.getDefault().getActiveUpdater();
        setDisplayName(displayName == null ? symbol + " Chart" : displayName);
        initComponents();
    }

    public void initComponents() {
        setLayout(new BorderLayout());
        time = DatasetManager.DAILY;
        chart = ChartManager.getDefault().getChart(chartName);
        chartProperties = ChartProperties.newInstance();
        chartRenderer = ChartRenderer.newInstance(this);
        chartToolbar = ChartToolbar.newInstance(this);
        chartPanel = ChartPanel.newInstance(this);
        horizontalBar = initHorizontalScrollBar();
        marker = Marker.newInstance(this);
        timer = new Timer();
        timer.schedule(new PeriodTimer(), 5000, 5000);

        add(chartToolbar, BorderLayout.NORTH);
        add(chartPanel, BorderLayout.CENTER);
        add(horizontalBar, BorderLayout.SOUTH);
    }

    protected void componentClosed() { timer.cancel(); if (!forced) { ChartFrameManager.getDefault().removeChartFrame(id); } }
    protected void componentOpened() { repaint(); }

    public void setForced(boolean b) { forced = b; }
    public boolean isForced() { return forced; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String s) { symbol = s; }

    public void setChart(String name) { chartName = name; chart = ChartManager.getDefault().getChart(chartName); chartPanel.repaint(); }
    public AbstractChart getChart() { return chart; }
    public void paintChart(Graphics2D g) { if (chart != null) chart.paint(g, this); }

    public String getTime() { return time; }
    public void setTime(String t) { time = t; chartRenderer.setMainDataset(symbol, time); chartPanel.repaint(); }

    public ChartToolbar getChartToolbar() { return chartToolbar; }
    public ChartPanel getChartPanel() { return chartPanel; }
    public ChartProperties getChartProperties() { return chartProperties; }
    public ChartRenderer getChartRenderer() { return chartRenderer; }
    public JScrollBar getHorizontalScrollBar() { return horizontalBar; }
    public Marker getMarker() { return marker; }

    private JScrollBar initHorizontalScrollBar() {
        JScrollBar bar = new JScrollBar(Adjustable.HORIZONTAL);
        BoundedRangeModel model = bar.getModel();
        model.setExtent(chartRenderer.getItems());
        model.setMinimum(0);
        model.setMaximum(chartRenderer.getEnd());
        model.setValue(chartRenderer.getEnd() - chartRenderer.getItems());
        bar.setModel(model);
        bar.addAdjustmentListener(this);
        return bar;
    }

    public void updateHorizontalScrollBar() {
        int value = chartRenderer.getEnd() - chartRenderer.getItems();

        BoundedRangeModel model = horizontalBar.getModel();
        model.setExtent(chartRenderer.getItems());
        model.setMinimum(0);
        model.setMaximum(chartRenderer.getMainDataset().getItemCount());
        model.setValue(value);
        horizontalBar.setModel(model);
    }

    public void updateHorizontalScrollBar(int end) {
        int i = end;
        i = i > chartRenderer.getMainDataset().getItemCount() ? chartRenderer.getMainDataset().getItemCount() : i;
        i = i < chartRenderer.getItems() ? chartRenderer.getItems() : i;
        int value = i - chartRenderer.getItems();

        BoundedRangeModel model = horizontalBar.getModel();
        model.setExtent(chartRenderer.getItems());
        model.setMinimum(0);
        model.setMaximum(chartRenderer.getMainDataset().getItemCount());
        model.setValue(value);
        horizontalBar.setModel(model);
    }

    public void adjustmentValueChanged(AdjustmentEvent e) {
        if (chartRenderer.getMainDataset() != null) {
            BoundedRangeModel model = horizontalBar.getModel();
            int end = model.getValue() + chartRenderer.getItems();
            end = end > chartRenderer.getMainDataset().getItemCount() ? chartRenderer.getMainDataset().getItemCount() : end;
            end = end < chartRenderer.getItems() ? chartRenderer.getItems() : end;
            chartRenderer.setEnd(end);
            chartPanel.repaint();
        }
    }

    public int getPersistenceType() { return PERSISTENCE_NEVER; }
    public String preferredID() { return id == null ? "ChartFrame" : id; }
    public void setID(String s) { id = s; }

    public void readXMLDocument(Element parent) {
        Element element;
        String t = XMLUtils.getStringParam(parent, "time");
        setTime(t);
        element = (Element) parent.getElementsByTagName("properties").item(0);
        getChartProperties().readXMLDocument(element);
        element = (Element) parent.getElementsByTagName("overlays").item(0);
        getChartRenderer().readOverlaysXMLDocument(element);
        element = (Element) parent.getElementsByTagName("indicators").item(0);
        getChartRenderer().readIndicatorsXMLDocument(element);
        element = (Element) parent.getElementsByTagName("annotations").item(0);
        getChartPanel().readAnnotationsXMLDocument(element);
        repaint();
    }

    public void writeXMLDocument(Document document, Element parent) {
        Element element;
        element = document.createElement("symbol");
        parent.appendChild(XMLUtils.setStringParam(element, getSymbol()));
        element = document.createElement("time");
        parent.appendChild(XMLUtils.setStringParam(element, getTime()));
        element = document.createElement("chart");
        parent.appendChild(XMLUtils.setStringParam(element, getChart().getName()));
        element = document.createElement("frame");
        parent.appendChild(element);
        element = document.createElement("properties");
        getChartProperties().writeXMLDocument(document, element);
        parent.appendChild(element);
        element = document.createElement("overlays");
        getChartRenderer().writeOverlaysXMLDocument(document, element);
        parent.appendChild(element);
        element = document.createElement("indicators");
        getChartRenderer().writeIndicatorsXMLDocument(document, element);
        parent.appendChild(element);
        element = document.createElement("annotations");
        getChartPanel().writeAnnotationsXMLDocument(document, element);
        parent.appendChild(element);
    }

    class PeriodTimer extends TimerTask {
        public void run() {
            AbstractUpdater ac = UpdaterManager.getDefault().getActiveUpdater();
            System.out.println(ac.getName());
            if (ac != null) {
                Dataset dataset = (!time.contains("Min")) 
                        ? ac.updateLastValues(symbol, time, getChartRenderer().getMainDataset())
                        : ac.updateIntraDayLastValues(symbol, time, getChartRenderer().getMainDataset());
                if (dataset != null) {
                    DatasetManager.getDefault().addDataset(DatasetManager.getName(symbol, time), dataset);
                    getChartRenderer().setMainDataset(dataset, false);
                    getChartPanel().repaint();
                }
            }
        }
    }

}
