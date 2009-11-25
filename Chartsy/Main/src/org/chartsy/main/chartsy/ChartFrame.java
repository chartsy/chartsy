package org.chartsy.main.chartsy;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.Serializable;
import javax.swing.BoundedRangeModel;
import javax.swing.JScrollBar;
import java.util.Timer;
import java.util.TimerTask;
import org.chartsy.main.chartsy.chart.AbstractChart;
import org.chartsy.main.chartsy.chart.Annotation;
import org.chartsy.main.chartsy.chart.Indicator;
import org.chartsy.main.chartsy.chart.Overlay;
import org.chartsy.main.dataset.Dataset;
import org.chartsy.main.dialogs.LoaderDialog;
import org.chartsy.main.managers.UpdaterManager;
import org.chartsy.main.updater.AbstractUpdater;
import org.chartsy.main.utils.Stock;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author viorel.gheba
 */
public class ChartFrame extends TopComponent implements Cloneable, AdjustmentListener {

    private static ChartFrame instance;
    private static final String PREFERRED_ID = "ChartFrame";

    protected Stock stock;
    protected String updaterName;
    protected String chartName;
    protected String time;

    protected AbstractUpdater updater;
    protected AbstractChart chart;
    
    protected ChartToolbar chartToolbar;
    protected ChartPanel chartPanel;
    protected ChartProperties chartProperties;
    protected ChartRenderer chartRenderer;
    protected JScrollBar jScrollBar;
    protected Marker marker;
    protected Timer timer;

    protected Indicator[] indicators;
    protected Overlay[] overlays;
    protected Annotation[] annotations;
    protected Annotation[] intraDayAnnotations;

    private boolean restored = false;

    public static synchronized ChartFrame getDefault() {
        if (instance == null) { instance = new ChartFrame(); }
        return instance;
    }

    public static synchronized ChartFrame findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) { return getDefault(); }
        if (win instanceof ChartFrame) { return (ChartFrame) win; }
        return getDefault();
    }

    @Override
    public ChartFrame clone() {
        try { return (ChartFrame) super.clone(); }
        catch (Exception e) { return getDefault(); }
    }

    public ChartFrame() {}

    private void initComponents() {
        setLayout(new BorderLayout());

        if (updater != null && chart != null) {
            if (!restored)
                chartProperties = ChartProperties.newInstance();
            chartRenderer = ChartRenderer.newInstance(this);
            chartToolbar = ChartToolbar.newInstance(this);
            chartPanel = ChartPanel.newInstance(this);
            jScrollBar = initHorizontalScrollBar();
            marker = Marker.newInstance(this);

            if (updater != null) {
                timer = new Timer();
                timer.schedule(new PeriodTimer(), 5000, 5000);
            }

            add(chartToolbar, BorderLayout.NORTH);
            add(chartPanel, BorderLayout.CENTER);
            add(jScrollBar, BorderLayout.SOUTH);
        }

        if (restored) {
            chartRenderer.setIndicators(indicators);
            chartRenderer.setOverlays(overlays);
            chartPanel.setExtraDayAnnotations(annotations);
            chartPanel.setIntraDayAnnotations(intraDayAnnotations);

            indicators = new Indicator[0];
            overlays = new Overlay[0];
            annotations = new Annotation[0];
            intraDayAnnotations = new Annotation[0];
        }

        setRestored(false);
    }

    public void setRestored(boolean b) { 
        restored = b;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
        setName(stock.getKey() + " Chart");
        setToolTipText(stock.getKey() + " Chart");
    }

    public Stock getStock() { 
        return stock;
    }

    public void setUpdaterName(String updaterName) {
        this.updaterName = updaterName;
    }

    public String getUpdaterName() { 
        return updaterName;
    }

    public void setUpdater(AbstractUpdater updater) { 
        this.updater = updater;
        setUpdaterName(updater.getName());
    }

    public AbstractUpdater getUpdater() {
        return updater;
    }

    public Dataset getDataset() { 
        return updater.getDataset(updater.getKey(stock, time));
    }

    public void setChartName(String chartName) {
        this.chartName = chartName;
    }

    public String getChartName() { 
        return chartName;
    }

    public void setChart(AbstractChart chart) { 
        this.chart = chart;
        setChartName(chart.getName());
        if (chartPanel != null)
            chartPanel.repaint();
    }

    public AbstractChart getChart() { 
        return chart;
    }
    
    public void paintChart(Graphics2D g) {
        if (chart != null)
            chart.paint(g, this);
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) { 
        this.time = time;
        if (chartRenderer != null) {
            chartRenderer.setMainDataset(stock, time);
            if (chartPanel != null)
                chartPanel.repaint();
        }
    }

    public void setChartToolbar(ChartToolbar chartToolbar) {
        this.chartToolbar = chartToolbar;
    }

    public ChartToolbar getChartToolbar() { 
        return chartToolbar;
    }

    public void setChartPanel(ChartPanel chartPanel) {
        this.chartPanel = chartPanel;
    }
    
    public ChartPanel getChartPanel() { 
        return chartPanel;
    }

    public void setChartProperties(ChartProperties chartProperties) {
        this.chartProperties = chartProperties;
    }

    public ChartProperties getChartProperties() { 
        return chartProperties;
    }

    public void setChartRenderer(ChartRenderer chartRenderer) {
        this.chartRenderer = chartRenderer;
    }
    
    public ChartRenderer getChartRenderer() { 
        return chartRenderer;
    }

    public JScrollBar getHorizontalScrollBar() { 
        return jScrollBar;
    }
    
    public void setMarker(Marker marker) {
        this.marker = marker;
    }
    
    public Marker getMarker() { 
        return marker;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public Timer getTimer() { 
        return timer;
    }

    public void setIndicators(Indicator[] indicators) {
        this.indicators = indicators;
    }
    
    public Indicator[] getIndicators() { 
        return chartRenderer.getIndicators();
    }

    public void setOverlays(Overlay[] overlays) {
        this.overlays = overlays;
    }

    public Overlay[] getOverlays() { 
        return chartRenderer.getOverlays();
    }

    public void setExtraDayAnnotations(Annotation[] annotations) {
        this.annotations = annotations;
    }

    public Annotation[] getExtraDayAnnotations() { 
        return chartPanel.getExtraDayAnnotations();
    }

    public void setIntraDayAnnotations(Annotation[] annotations) {
        this.intraDayAnnotations = annotations;
    }
    
    public Annotation[] getIntraDayAnnotations() { 
        return this.chartPanel.getIntraDayAnnotations();
    }

    private JScrollBar initHorizontalScrollBar() {
        JScrollBar bar = new JScrollBar(JScrollBar.HORIZONTAL);
        
        int end = chartRenderer.getEnd();
        int items = chartRenderer.getItems();

        BoundedRangeModel model = bar.getModel();
        model.setExtent(items);
        model.setMinimum(0);
        model.setMaximum(end);
        model.setValue(end - items);
        bar.setModel(model);

        bar.setAlignmentX(java.awt.Component.RIGHT_ALIGNMENT);
        bar.addAdjustmentListener(this);
        
        return bar;
    }

    public void updateHorizontalScrollBar() {
        if (chartRenderer.datasetExists()) {
            int end = chartRenderer.getEnd();
            int items = chartRenderer.getItems();
            int itemsCount = chartRenderer.getMainDataset().getItemCount();

            BoundedRangeModel model = jScrollBar.getModel();
            model.setExtent(items);
            model.setMinimum(0);
            model.setMaximum(itemsCount);
            model.setValue(end - items);
            jScrollBar.setModel(model);
        }
    }

    public void updateHorizontalScrollBar(int end) {
        if (chartRenderer.datasetExists()) {
            int items = chartRenderer.getItems();
            int itemsCount = chartRenderer.getMainDataset().getItemCount();
            end = end > itemsCount ? itemsCount : (end < items ? items : end);

            BoundedRangeModel model = jScrollBar.getModel();
            model.setExtent(items);
            model.setMinimum(0);
            model.setMaximum(itemsCount);
            model.setValue(end - items);
            jScrollBar.setModel(model);
        }
    }

    public void adjustmentValueChanged(AdjustmentEvent e) {
        if (chartRenderer.datasetExists()) {
            int items = chartRenderer.getItems();
            int itemsCount = chartRenderer.getMainDataset().getItemCount();

            BoundedRangeModel model = jScrollBar.getModel();
            int end = model.getValue() + items;
            end = end > itemsCount ? itemsCount : (end < items ? items : end);
            chartRenderer.setEnd(end);
            
            chartPanel.repaint();
        }
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }

    @Override
    protected void componentOpened() {
        initComponents();
    }

    @Override
    protected void componentClosed() {
        timer.cancel();
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    @Override
    protected Object writeReplace() {
        return new ResolvableHelper(this);
    }

    final static class ResolvableHelper implements Serializable {

        private static final long serialVersionUID = 101L;
        
        private Stock stock;
        private AbstractUpdater abstractUpdater;
        private AbstractChart abstractChart;
        private String time;

        private ChartProperties chartProperties;

        private Indicator[] indicators;
        private Overlay[] overlays;
        private Annotation[] annotations;
        private Annotation[] intraDay;

        private ResolvableHelper(ChartFrame chartFrame) {
            this.stock = chartFrame.getStock();
            this.abstractUpdater = chartFrame.getUpdater();
            this.abstractChart = chartFrame.getChart();
            this.time = chartFrame.getTime();

            this.chartProperties = chartFrame.getChartProperties();

            this.indicators = chartFrame.getIndicators();
            this.overlays = chartFrame.getOverlays();
            this.annotations = chartFrame.getExtraDayAnnotations();
            this.intraDay = chartFrame.getIntraDayAnnotations();
        }

        public Object readResolve() {
            abstractUpdater.removeAllDatasets();

            LoaderDialog loader = new LoaderDialog(new javax.swing.JFrame(), true);
            loader.setLocationRelativeTo(WindowManager.getDefault().getMainWindow());
            if (time.contains("Min")) {
                loader.updateIntraday(stock, time, abstractUpdater);
                loader.update(stock, abstractUpdater);
            } else {
                loader.update(stock, abstractUpdater);
            }
            loader.setLabelText("Aquiring data for " + (!stock.getCompanyName().equals("") ? stock.getCompanyName() : stock.getKey()));
            loader.setVisible(true);

            if (!loader.isVisible()) {
                try {
                    ChartFrame chartFrame = new ChartFrame();

                    chartFrame.setRestored(true);

                    chartFrame.setStock(stock);
                    chartFrame.setUpdater(abstractUpdater);
                    chartFrame.setChart(abstractChart);
                    chartFrame.setTime(time);

                    chartFrame.setChartProperties(chartProperties);

                    chartFrame.setIndicators(indicators);
                    chartFrame.setOverlays(overlays);

                    for (Annotation a : annotations) {
                        a.setChartFrame(chartFrame);
                    }
                    chartFrame.setExtraDayAnnotations(annotations);

                    for (Annotation a : intraDay) {
                        a.setChartFrame(chartFrame);
                    }
                    chartFrame.setIntraDayAnnotations(intraDay);

                    return chartFrame;
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            }
            
            return null;
        }

    }

    final class PeriodTimer extends TimerTask implements Serializable {

        private static final long serialVersionUID = 101L;

        public void run() {
            if (updater != null) {
                int intraDay = (!time.contains("Min")) ? 0 : 1;
                
                Dataset dataset;
                switch (intraDay) {
                    case 0:
                        dataset = updater.updateLastValues(stock.getKey(), AbstractUpdater.DAILY, updater.getDataset(updater.getKey(stock, AbstractUpdater.DAILY)));
                        if (dataset != null) {
                            updater.addDataset(updater.getKey(stock, AbstractUpdater.DAILY), dataset);
                            if (time.equals(AbstractUpdater.DAILY)) {
                                chartRenderer.setMainDataset(dataset, false);
                            }
                        }
                        break;
                    case 1:
                        dataset = updater.updateIntraDayLastValues(stock.getKey(), time, getChartRenderer().getMainDataset());
                        if (dataset != null) {
                            updater.addDataset(updater.getKey(stock, time), dataset);
                            chartRenderer.setMainDataset(dataset, false);
                        }
                        break;
                }
                chartPanel.repaint();
            }
        }
    }

}
