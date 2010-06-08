package org.chartsy.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoundedRangeModel;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import org.chartsy.main.chart.Annotation;
import org.chartsy.main.data.ChartData;
import org.chartsy.main.data.DataProvider;
import org.chartsy.main.data.Dataset;
import org.chartsy.main.data.Stock;
import org.chartsy.main.events.StockEvent;
import org.chartsy.main.events.StockListener;
import org.chartsy.main.history.History;
import org.chartsy.main.history.HistoryItem;
import org.chartsy.main.intervals.Interval;
import org.chartsy.main.managers.AnnotationManager;
import org.chartsy.main.managers.ChartManager;
import org.chartsy.main.managers.DataProviderManager;
import org.chartsy.main.resources.ResourcesUtils;
import org.chartsy.main.utils.ChartNode;
import org.chartsy.main.utils.MainActions;
import org.chartsy.main.utils.SerialVersion;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.AbstractNode;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.TaskListener;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author viorel.gheba
 */
public class ChartFrame extends TopComponent implements AdjustmentListener, MouseWheelListener, StockListener
{

    private static ChartFrame instance;
    private static final String PREFERRED_ID = "ChartFrame";
    public static final Logger LOG = Logger.getLogger(ChartFrame.class.getName());
    private static final RequestProcessor RP = new RequestProcessor("interruptible tasks", 1, true);

    private History history = null;
    private ChartProperties chartProperties = null;
    private ChartToolbar chartToolbar = null;
    private ChartData chartData = null;
    private MainPanel mainPanel = null;
    private JScrollBar scrollBar = null;
    private Timer timer = null;

    private boolean restored = false;
    private boolean focus = true;

    public static synchronized ChartFrame getDefault()
    {
        if (instance == null)
        {
            instance = new ChartFrame();
        }
        return instance;
    }

    public static synchronized ChartFrame findInstance()
    {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null)
        {
            return getDefault();
        }
        if (win instanceof ChartFrame)
        {
            return (ChartFrame) win;
        }
        return getDefault();
    }

    public ChartFrame()
    {}

    public ChartFrame(ChartData data)
    {
        //setIgnoreRepaint(true);
        setLayout(new BorderLayout());
        chartData = data;
        if (!chartData.isStockNull())
        {
            setName(chartData.getStock().getKey() + " Chart");
            setToolTipText(chartData.getStock().getKey() + " Chart");
        }
        else
        {
            setName("Chart");
            setToolTipText("Chart");
        }
        
        addMouseWheelListener(this);
    }

    private void initComponents()
    {
        setLayout(new BorderLayout());
        if (!restored)
        {
            if (chartProperties == null)
                chartProperties = new ChartProperties();
            if (history == null)
            {
                history = new History();
                history.initialize();
            }
        }
        else
        {
            chartProperties.setMarkerVisibility(true);
        }

        chartToolbar = new ChartToolbar(this);
        mainPanel = new MainPanel(this);
        scrollBar = initHorizontalScrollBar();

        add(chartToolbar, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(scrollBar, BorderLayout.SOUTH);

        if (restored)
        {
            synchronized (this)
            {
                getSplitPanel().getIndicatorsPanel().setIndicatorsList(chartData.getSavedIndicators());
                chartData.clearSavedIndicators();
            }

            synchronized (this)
            {
                getSplitPanel().getChartPanel().setOverlays(chartData.getSavedOverlays());
                chartData.clearSavedOverlays();
            }

            synchronized (this)
            {
                restoreAnnotations();
                chartData.clearAnnotations();
                chartData.clearAnnotationsCount();
            }
        }

        history.setCurrent(new HistoryItem(chartData.getStock(), chartData.getInterval()));
        timer = new Timer();
        timer.schedule(new PeriodTimer(), 5000, 5000);
        setRestored(false);

        revalidate();
        componentFocused();
    }
    
    public boolean getRestored()
    { return restored; }

    public void setRestored(boolean b)
    { restored = b; }

    public boolean getFocus()
    { return focus; }

    public void setFocus(boolean b)
    { focus = b; }

    public ChartProperties getChartProperties()
    { return chartProperties; }

    public void setChartProperties(ChartProperties cp)
    { chartProperties = cp; }

    public ChartData getChartData()
    { return chartData; }

    public void setChartData(ChartData data)
    { chartData = data; }

    public History getHistory()
    { return this.history; }

    public void setHistory(History history)
    { this.history = history; }

    public Timer getTimer()
    { return timer; }

    public MainPanel getMainPanel()
    { return mainPanel; }

    public ChartSplitPanel getSplitPanel()
    { 
        if (mainPanel != null)
            return mainPanel.getSplitPanel();
        else
            return null;
    }

    public boolean hasCurrentAnnotation()
    {
        if (getSplitPanel().getChartPanel().getAnnotationPanel().hasCurrent())
            return true;
        for (IndicatorPanel ip : getSplitPanel().getIndicatorsPanel().getIndicatorPanels())
        {
            if (ip.getAnnotationPanel().hasCurrent())
                return true;
        }
        return false;
    }

    public Annotation getCurrentAnnotation()
    {
        if (getSplitPanel().getChartPanel().getAnnotationPanel().hasCurrent())
            return getSplitPanel().getChartPanel().getAnnotationPanel().getCurrent();
        for (IndicatorPanel ip : getSplitPanel().getIndicatorsPanel().getIndicatorPanels())
        {
            if (ip.getAnnotationPanel().hasCurrent())
                return ip.getAnnotationPanel().getCurrent();
        }
        return null;
    }

    public void deselectAll()
    {
        getSplitPanel().getChartPanel().getAnnotationPanel().deselectAll();
        for (IndicatorPanel ip : getSplitPanel().getIndicatorsPanel().getIndicatorPanels())
        {
            ip.getAnnotationPanel().deselectAll();
        }
    }

    public void removeAllAnnotations()
    {
        getSplitPanel().getChartPanel().getAnnotationPanel().removeAllAnnotations();
        for (IndicatorPanel ip : getSplitPanel().getIndicatorsPanel().getIndicatorPanels())
        {
            ip.getAnnotationPanel().removeAllAnnotations();
        }
    }

    public List<Integer> getAnnotationCount()
    {
        List<Integer> list = new ArrayList<Integer>();
        int i = 0;
        i += getSplitPanel().getChartPanel().getAnnotationPanel().getAnnotations().length;
        list.add(new Integer(i));

        for (IndicatorPanel ip : getSplitPanel().getIndicatorsPanel().getIndicatorPanels())
        {
            i += ip.getAnnotationPanel().getAnnotations().length;
            list.add(new Integer(i));
        }
        
        return list;
    }

    public List<Annotation> getAnnotations()
    {
        List<Annotation> list = new ArrayList<Annotation>();
        list.addAll(getSplitPanel().getChartPanel().getAnnotationPanel().getAnnotationsList());
        for (IndicatorPanel ip : getSplitPanel().getIndicatorsPanel().getIndicatorPanels())
            list.addAll(ip.getAnnotationPanel().getAnnotationsList());
        return list;
    }

    public synchronized void restoreAnnotations()
    {
        List<Integer> count = getChartData().getAnnotationsCount();
        List<Annotation> annotations = getChartData().getAnnotations();

        for (int i = 0; i < count.size(); i++)
        {
            if (i == 0) // chart panel annotations
            {
                List<Annotation> newList = annotations.subList(0, count.get(i));
                getSplitPanel().getChartPanel().getAnnotationPanel().setAnnotationsList(newList);
            }
            else // indicator panel annotations
            {
                List<Annotation> newList = annotations.subList(count.get(i-1), count.get(i));
                getSplitPanel().getIndicatorsPanel().getIndicatorPanels()[i-1].getAnnotationPanel().setAnnotationsList(newList);
            }
        }
        
        return;
    }

    public JPopupMenu getMenu()
    {
        JPopupMenu popup = new JPopupMenu();
        JMenu menu;
        JMenuItem menuItem;

        // change interval
        popup.add(menu = new JMenu("Select Interval"));
        menu.setIcon(ResourcesUtils.getIcon16("time"));
        Interval currentInterval = chartData.getInterval();
        for (Interval i : chartData.getDataProvider().getIntervals())
        {
            menuItem = new JMenuItem(MainActions.changeTime(this, i, i.equals(currentInterval)));
            menuItem.setText(i.getName());
            menuItem.setMargin(new Insets(0,0,0,0));
            menu.add(menuItem);
        }
        if (chartData.getDataProvider().supportsIntraday())
        {
            menu.addSeparator();
            for (Interval i : chartData.getDataProvider().getIntraDayIntervals())
            {
                menuItem = new JMenuItem(MainActions.changeTime(this, i, i.equals(currentInterval)));
                menuItem.setText(i.getName());
                menuItem.setMargin(new Insets(0,0,0,0));
                menu.add(menuItem);
            }
        }

        // change chart
        popup.add(menu = new JMenu("Select Chart"));
        menu.setIcon(ResourcesUtils.getIcon16("chart"));
        String currentChart = chartData.getChart().getName();
        for (String s : ChartManager.getDefault().getCharts())
        {
            menuItem = new JMenuItem(MainActions.changeChart(this, s, s.equals(currentChart)));
            menuItem.setText(s);
            menuItem.setMargin(new Insets(0,0,0,0));
            menu.add(menuItem);
        }

        // add indicators
        popup.add(menuItem = new JMenuItem(MainActions.addIndicators(this)));
        menuItem.setText("Indicators");
        menuItem.setIcon(ResourcesUtils.getIcon16("indicator"));

        // add overlays
        popup.add(menuItem = new JMenuItem(MainActions.addOverlays(this)));
        menuItem.setText("Overlays");
        menuItem.setIcon(ResourcesUtils.getIcon16("overlay"));

        // add annotation
        popup.add(menu = new JMenu("Annotations"));
        menu.setIcon(ResourcesUtils.getIcon16("line"));
        for (String s : AnnotationManager.getDefault().getAnnotations())
        {
            menuItem = new JMenuItem(MainActions.addAnnotation(this, s));
            menuItem.setText(s);
            menuItem.setMargin(new Insets(0,0,0,0));
            menuItem.setIcon(ResourcesUtils.getIcon16("line"));
            menu.add(menuItem);
        }
        menu.addSeparator();
        menuItem = new JMenuItem(MainActions.removeAllAnnotations(this));
        menuItem.setText("Remove All");
        menuItem.setMargin(new Insets(0,0,0,0));
        menu.add(menuItem);
        if (hasCurrentAnnotation())
        {
            menuItem = new JMenuItem(MainActions.annotationSettings(this, getCurrentAnnotation()));
            menuItem.setText("Annotation Settings");
            menuItem.setMargin(new Insets(0,0,0,0));
            menu.add(menuItem);
        }

        // export image
        popup.add(menuItem = new JMenuItem(MainActions.exportImage(this)));
        menuItem.setText("Export Image");
        menuItem.setIcon(ResourcesUtils.getIcon16("image"));

        // print
        popup.add(menuItem = new JMenuItem(MainActions.printChart(this)));
        menuItem.setText("Print");
        menuItem.setIcon(ResourcesUtils.getIcon16("print"));

        // chart settings
        popup.add(menuItem = new JMenuItem(MainActions.chartSettings(this)));
        menuItem.setText("Chart Settings");
        menuItem.setIcon(ResourcesUtils.getIcon16("settings"));

        popup.add(menuItem = new JMenuItem(MainActions.toggleToolbarVisibility(this)));

        return popup;
    }

    public void zoomIn()
    {
        if (chartData != null)
            chartData.zoomIn(this);
    }

    public void zoomOut()
    {
        if (chartData != null)
            chartData.zoomOut(this);
    }

    public void setToolbarVisibility()
    { chartToolbar.setVisible(chartProperties.getToolbarVisibility()); }

    public void updateToolbar()
    {
        if (chartToolbar != null)
            chartToolbar.updateToolbar();
    }

    @Override
    public int getPersistenceType() 
    { return TopComponent.PERSISTENCE_ALWAYS; }

    @Override
    protected String preferredID() 
    { return PREFERRED_ID; }

    @Override
    protected Object writeReplace() 
    { return new ResolvableHelper(this); }

    @Override
    protected void componentOpened()
    {
        if (chartData != null)
            loading(chartData.getStock(), true);
    }

    @Override
    protected void componentClosed()
    {
        if (getTimer() != null)
            getTimer().cancel();
        if (getHistory() != null)
            getHistory().removeFiles();

    }

    @Override
    protected void componentActivated()
    {
        super.componentActivated();
        componentFocused();
    }


    public void componentFocused()
    {
        if (getMainPanel() != null)
            if (getSplitPanel() != null)
                getSplitPanel().getChartPanel().getAnnotationPanel().requestFocusInWindow();
    }

    private JScrollBar initHorizontalScrollBar()
    {
        JScrollBar bar = new JScrollBar(JScrollBar.HORIZONTAL);

        int end = chartData.getLast();
        int items = chartData.getPeriod();

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

    public void updateHorizontalScrollBar()
    {
        int last = chartData.getLast();
        int items = chartData.getPeriod();
        int itemsCount = chartData.getDataset().getItemsCount();

        BoundedRangeModel model = scrollBar.getModel();
        model.setExtent(items);
        model.setMinimum(0);
        model.setMaximum(itemsCount);
        model.setValue(last - items);
        scrollBar.setModel(model);

        repaint();
    }

    public void updateHorizontalScrollBar(int end)
    {
        int items = chartData.getPeriod();
        int itemsCount = chartData.getDataset().getItemsCount();
        end = end > itemsCount ? itemsCount : (end < items ? items : end);
        chartData.setLast(end);
        chartData.calculate(this);
    }

    public void adjustmentValueChanged(AdjustmentEvent e)
    {
        BoundedRangeModel model = scrollBar.getModel();
        int items = chartData.getPeriod();
        int itemsCount = chartData.getDataset().getItemsCount();
        int end = model.getValue() + items;
        end = end > itemsCount ? itemsCount : (end < items ? items : end);
        chartData.setLast(end);
        chartData.calculate(this);
    }

    public BufferedImage getBufferedImage(int width, int height)
    {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

        g.setColor(chartProperties.getBackgroundColor());
        g.fillRect(0, 0, width, height);

        mainPanel.paintComponents(g);

        g.dispose();

        return image;
    }

    public AbstractNode getNode() 
    { return new ChartNode(chartProperties); }

    private void reinitialize()
    {
        chartData.setSavedIndicators(getSplitPanel().getIndicatorsPanel().getIndicatorsList());
        chartData.setSavedOverlays(getSplitPanel().getChartPanel().getOverlays());
        chartData.setAnnotationsCount(getAnnotationCount());
        chartData.setAnnotations(getAnnotations());
        chartData.removeAllIndicatorsDatasetListeners();
        chartData.removeAllOverlaysDatasetListeners();
        setRestored(true);
        remove(chartToolbar);
        remove(mainPanel);
        remove(scrollBar);
    }

    private void loading(final Stock newStock, final boolean newChart)
    {
        try
        {
            final DataProvider dataProvider = getChartData().getDataProvider();
            final Interval interval = getChartData().getInterval();

            StringBuffer sb = new StringBuffer();
            sb.append("Aquiring data for ");
            sb.append(newStock.getCompanyName().equals("") ? newStock.getKey() : newStock.getCompanyName());

            final JLabel loading = new JLabel(sb.toString(), ResourcesUtils.getLogo(), SwingConstants.CENTER);
            loading.setOpaque(true);
            loading.setBackground(Color.WHITE);
            loading.setVerticalTextPosition(SwingConstants.BOTTOM);
            loading.setHorizontalTextPosition(SwingConstants.CENTER);

            final RequestProcessor.Task task = RP.create(new Runnable()
            {
                public void run()
                {
                    if (!newChart)
                        reinitialize();
                    add(loading, BorderLayout.CENTER);
                    DataProviderManager.getDefault().update(newStock, dataProvider);
                }
            });

            final ProgressHandle handle = ProgressHandleFactory.createHandle(loading.getText(), task);
            task.addTaskListener(new TaskListener()
            {
                public void taskFinished(Task task)
                {
                    if (DataProviderManager.getDefault().isUpdated())
                    {
                        handle.finish();
                        DataProviderManager.getDefault().setUpdated(false);
                        Dataset dataset = dataProvider.getDataset(newStock, interval);
                        if (dataset != null)
                        {
                            remove(loading);
                            getChartData().setDataset(dataset);
                            getChartData().setInterval(interval);
                            initComponents();
                        }
                        else
                        {
                            StringBuffer sb = new StringBuffer();
                            sb.append("Can't find data for ");
                            sb.append(newStock.getCompanyName().equals("") ? newStock.getKey() : newStock.getCompanyName());
                            loading.setText(sb.toString());
                            loading.repaint();
                            if (!newChart)
                            {
                                sb.append(". Return to the previous chart?");
                                NotifyDescriptor d = new NotifyDescriptor.Confirmation(sb.toString(), "No Data", NotifyDescriptor.YES_NO_OPTION);
                                Object retval = DialogDisplayer.getDefault().notify(d);
                                if (retval.equals(NotifyDescriptor.YES_OPTION))
                                {
                                    remove(loading);
                                    getChartData().setStock(oldStock);
                                    getChartData().updateDataset(oldInterval);
                                    initComponents();
                                    oldStock = null;
                                    oldInterval = null;
                                }
                            }
                        }
                    }
                }
            });

            if (dataProvider.getDataset(newStock, interval) == null)
            {
                handle.start();
                task.schedule(0);
            }
            else
            {
                if (!newChart)
                    reinitialize();
                Dataset dataset = dataProvider.getDataset(newStock, interval);
                getChartData().setDataset(dataset);
                getChartData().setInterval(interval);
                initComponents();
            }
        }
        catch (Exception e)
        {
            LOG.log(Level.WARNING, e.getMessage());
        }
    }

    public void stockChanged(StockEvent evt)
    {
        final HistoryItem historyItem = (HistoryItem) evt.getSource();
        final Stock newStock = historyItem.getStock();
        final Interval interval = historyItem.getInterval();
        oldStock = getChartData().getStock();
        oldInterval = getChartData().getInterval();

        if (newStock != null)
        {
            stockInfo = null;
            final RequestProcessor.Task stockTask = RP.create(new Runnable()
            {
                public void run()
                {
                    stockInfo = chartData.getDataProvider().getStock(newStock.getSymbol(), newStock.getExchange());
                }
            });

            final ProgressHandle handle = ProgressHandleFactory.createHandle("Aquiring stock info", stockTask);
            stockTask.addTaskListener(new TaskListener()
            {
                public void taskFinished(Task task)
                {
                    handle.finish();
                    if (stockInfo == null)
                    {
                        NotifyDescriptor d = new NotifyDescriptor.Exception(new IllegalArgumentException("The symbol is invalid. Please enter a valid symbol."));
                        Object ret = DialogDisplayer.getDefault().notify(d);
                        if (ret.equals(NotifyDescriptor.OK_OPTION))
                        {
                            SwingUtilities.invokeLater(new Runnable()
                            {
                                public void run()
                                {
                                    if (history != null)
                                    {
                                        history.go(-1);
                                        history.clearForwardHistory();
                                    }
                                    updateToolbar();
                                }
                            });
                        }
                    }
                    else
                    {
                        if (!stockInfo.getKey().equals(""))
                        {
                            getChartData().getDataProvider().addStock(stockInfo);
                            SwingUtilities.invokeLater(new Runnable()
                            {
                                public void run()
                                {
                                    getChartData().setStock(stockInfo);
                                    getChartData().setInterval(interval);
                                    setName(stockInfo.getKey() + " Chart");
                                    setToolTipText(stockInfo.getKey() + " Chart");
                                    getSplitPanel().getChartPanel().updateStock();
                                    loading(stockInfo, false);
                                }
                            });
                        }
                        else
                        {
                            SwingUtilities.invokeLater(new Runnable()
                            {
                                public void run()
                                {
                                    updateToolbar();
                                }
                            });
                        }
                    }
                }
            });

            if (!getChartData().getDataProvider().hasStock(newStock))
            {
                handle.start();
                stockTask.schedule(0);
            }
            else
            {
                stockInfo = getChartData().getDataProvider().getStock(newStock);
                getChartData().setStock(stockInfo);
                getChartData().setInterval(interval);
                setName(stockInfo.getKey() + " Chart");
                setToolTipText(stockInfo.getKey() + " Chart");
                getSplitPanel().getChartPanel().updateStock();
                loading(stockInfo, false);
            }
        }
    }

    private Stock stockInfo = null;
    private Stock oldStock = null;
    private Interval oldInterval = null;

    public void mouseWheelMoved(MouseWheelEvent e)
    {
        if (!getChartData().isDatasetNull())
        {
            int items = getChartData().getPeriod();
            int itemsCount = getChartData().getDataset().getItemsCount();
            if (itemsCount > items)
            {
                int last = getChartData().getLast() - e.getWheelRotation();
                last = last > itemsCount ? itemsCount : (last < items ? items : last);

                getChartData().setLast(last);
                getChartData().calculate(this);
            }
        }
    }

    final class PeriodTimer extends TimerTask implements Serializable
    {

        private static final long serialVersionUID = 2L;

        @Override
        public void run()
        {
            if (getChartData() != null)
            {
                Stock stock = getChartData().getStock();
                Interval interval = getChartData().getInterval();
                DataProvider dataProvider = getChartData().getDataProvider();
                Dataset oldDataset = getChartData().getDataset();

                if (!interval.isIntraDay())
                {
                    Dataset newDataset = dataProvider.getLastDataItem(stock, interval, oldDataset);
                    getChartData().setDataset(newDataset);
                    getChartData().updateDataset();

                    int last = getChartData().getLast();
                    int items = newDataset.getItemsCount();
                    if (last+1 == items)
                        getChartData().setLast(items);
                }
            }
            repaint();
        }

    }

    final static class ResolvableHelper implements Serializable
    {

        private static final long serialVersionUID = SerialVersion.APPVERSION;

        private ChartProperties chartProperties;
        private ChartData chartData;
        private String dataProvider;
        private List<HistoryItem> bList;
        private List<HistoryItem> fList;

        private ResolvableHelper(ChartFrame chartFrame)
        {
            chartProperties = chartFrame.getChartProperties();
            chartData = chartFrame.getChartData();
            dataProvider = chartData.getDataProvider().getName();

            chartData.setSavedIndicators(chartFrame.getSplitPanel().getIndicatorsPanel().getIndicatorsList());
            chartData.setSavedOverlays(chartFrame.getSplitPanel().getChartPanel().getOverlays());
            chartData.setAnnotationsCount(chartFrame.getAnnotationCount());
            chartData.setAnnotations(chartFrame.getAnnotations());

            bList = chartFrame.getHistory().getBackHistory();
            fList = chartFrame.getHistory().getFwdHistory();
            chartFrame.getHistory().removeFiles();
        }

        public Object readResolve()
        {
            synchronized (this)
            {
                DataProvider dp = DataProviderManager.getDefault().getDataProvider(dataProvider);
                chartData.setDataProvider(dp);

                ChartFrame chartFrame = new ChartFrame(chartData);
                chartFrame.setChartProperties(chartProperties);

                History history = new History();
                history.initialize();
                history.writeAllHistory(bList, fList);
                chartFrame.setHistory(history);

                chartFrame.setRestored(true);
                return chartFrame;
            }
        }

    }

}
