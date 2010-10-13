package org.chartsy.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoundedRangeModel;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.SwingConstants;
import org.chartsy.main.chart.Annotation;
import org.chartsy.main.data.ChartData;
import org.chartsy.main.data.DataProvider;
import org.chartsy.main.data.Dataset;
import org.chartsy.main.data.Stock;
import org.chartsy.main.events.DataProviderEvent;
import org.chartsy.main.events.DataProviderListener;
import org.chartsy.main.events.StockEvent;
import org.chartsy.main.history.History;
import org.chartsy.main.history.HistoryItem;
import org.chartsy.main.intervals.Interval;
import org.chartsy.main.managers.DataProviderManager;
import org.chartsy.main.resources.ResourcesUtils;
import org.chartsy.main.templates.Template;
import org.chartsy.main.utils.ChartNode;
import org.chartsy.main.utils.MainActions;
import org.chartsy.main.utils.SerialVersion;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.AbstractNode;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.TaskListener;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author viorel.gheba
 */
public class ChartFrame extends TopComponent implements AdjustmentListener, MouseWheelListener, DataProviderListener
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
    private Template template = null;
    private boolean restored = false;
    private boolean focus = true;
    private boolean loadingFlag = false;

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
    {
    }

    public ChartFrame(ChartData data)
    {
        setLayout(new BorderLayout());
        chartData = data;
        if (!chartData.isStockNull())
        {
            setName(
                    NbBundle.getMessage(
                    ChartFrame.class,
                    "CTL_ChartFrame",
                    chartData.getStock().getKey()));
            setToolTipText(
                    NbBundle.getMessage(
                    ChartFrame.class,
                    "TOOL_ChartFrame",
                    chartData.getStock().getCompanyName()));
        } else
        {
            setName(
                    NbBundle.getMessage(ChartFrame.class, "CTL_ChartFrameEmpty"));
            setToolTipText(
                    NbBundle.getMessage(ChartFrame.class, "TOOL_ChartFrameEmpty"));
        }
        chartData.getDataProvider().addDatasetListener((DataProviderListener) this);
        addMouseWheelListener((MouseWheelListener) this);
    }

    public ChartFrame(ChartData chartData, Template template)
    {
        this(chartData);
        this.template = template;
    }

    private synchronized void initComponents()
    {
        setLayout(new BorderLayout());
        if (!restored)
        {
            if (chartProperties == null)
            {
                chartProperties = new ChartProperties();
            }
            if (history == null)
            {
                history = new History();
                history.initialize();
            }
        } else
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
            getSplitPanel().getIndicatorsPanel().setIndicatorsList(chartData.getSavedIndicators());
            chartData.clearSavedIndicators();
            getSplitPanel().getChartPanel().setOverlays(chartData.getSavedOverlays());
            chartData.clearSavedOverlays();
            restoreAnnotations();
            chartData.clearAnnotations();
            chartData.clearAnnotationsCount();
        } else
        {
            if (template != null)
            {
                chartProperties = template.getChartProperties();
                getSplitPanel().getIndicatorsPanel().setIndicatorsList(template.getIndicators());
                getSplitPanel().getChartPanel().setOverlays(template.getOverlays());
            }
        }

        history.setCurrent(new HistoryItem(
                chartData.getStock(),
                chartData.getInterval().hashCode()));

        setRestored(false);

        revalidate();
        componentFocused();
    }

    public Template getTemplate()
    {
        return template;
    }

    public void setTemplate(Template template)
    {
        this.template = template;
        this.chartProperties = template.getChartProperties();

        // indicators
        getSplitPanel().getIndicatorsPanel().removeAllIndicators();
        getChartData().removeAllIndicatorsDatasetListeners();
        getSplitPanel().getIndicatorsPanel().setIndicatorsList(template.getIndicators());

        // overlays
        getSplitPanel().getChartPanel().clearOverlays();
        getChartData().removeAllOverlaysDatasetListeners();
        getSplitPanel().getChartPanel().setOverlays(template.getOverlays());

        revalidate();
        repaint();
    }

    public boolean getRestored()
    {
        return restored;
    }

    public void setRestored(boolean b)
    {
        restored = b;
    }

    public boolean getFocus()
    {
        return focus;
    }

    public void setFocus(boolean b)
    {
        focus = b;
    }

    public ChartProperties getChartProperties()
    {
        return chartProperties;
    }

    public void setChartProperties(ChartProperties cp)
    {
        chartProperties = cp;
    }

    public ChartData getChartData()
    {
        return chartData;
    }

    public void setChartData(ChartData data)
    {
        chartData = data;
    }

    public History getHistory()
    {
        return this.history;
    }

    public void setHistory(History history)
    {
        this.history = history;
    }

    public MainPanel getMainPanel()
    {
        return mainPanel;
    }

    public ChartSplitPanel getSplitPanel()
    {
        if (mainPanel != null)
        {
            return mainPanel.getSplitPanel();
        } else
        {
            return null;
        }
    }

    public boolean hasCurrentAnnotation()
    {
        if (getSplitPanel().getChartPanel().getAnnotationPanel().hasCurrent())
        {
            return true;
        }
        for (IndicatorPanel ip : getSplitPanel().getIndicatorsPanel().getIndicatorPanels())
        {
            if (ip.getAnnotationPanel().hasCurrent())
            {
                return true;
            }
        }
        return false;
    }

    public Annotation getCurrentAnnotation()
    {
        if (getSplitPanel().getChartPanel().getAnnotationPanel().hasCurrent())
        {
            return getSplitPanel().getChartPanel().getAnnotationPanel().getCurrent();
        }
        for (IndicatorPanel ip : getSplitPanel().getIndicatorsPanel().getIndicatorPanels())
        {
            if (ip.getAnnotationPanel().hasCurrent())
            {
                return ip.getAnnotationPanel().getCurrent();
            }
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
        {
            list.addAll(ip.getAnnotationPanel().getAnnotationsList());
        }
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
            } else // indicator panel annotations
            {
                List<Annotation> newList = annotations.subList(count.get(i - 1), count.get(i));
                getSplitPanel().getIndicatorsPanel().getIndicatorPanels()[i - 1].getAnnotationPanel().setAnnotationsList(newList);
            }
        }

        return;
    }

    public JPopupMenu getMenu()
    {
        JPopupMenu popup = new JPopupMenu();
        popup.add(MainActions.generateIntervalsMenu(this)); // change interval
        popup.add(MainActions.generateChartsMenu(this)); // change chart
        popup.add(new JMenuItem(MainActions.openIndicators(this))); // add indicators
        popup.add(new JMenuItem(MainActions.openOverlays(this))); // add overlays
        popup.add(MainActions.generateAnnotationsMenu(this)); // add annotation
        popup.add(new JMenuItem(MainActions.exportImage(this))); // export image
        popup.add(new JMenuItem(MainActions.printChart(this))); // print
        popup.add(new JMenuItem(MainActions.chartProperties(this))); // chart settings
        popup.add(new JMenuItem(MainActions.toggleToolbarVisibility(this))); // hide/show toolbar
        if (!MainActions.isInFavorites(this))
        {
            popup.add(new JMenuItem(MainActions.addToFavorites(this))); // add to favorites
        }
        popup.add(MainActions.generateTemplatesMenu(this)); // save to template
        return popup;
    }

    public void zoomIn()
    {
        if (chartData != null)
        {
            chartData.zoomIn(this);
        }
    }

    public void zoomOut()
    {
        if (chartData != null)
        {
            chartData.zoomOut(this);
        }
    }

    public void setToolbarVisibility()
    {
        chartToolbar.setVisible(chartProperties.getToolbarVisibility());
    }

    public void updateToolbar()
    {
        if (chartToolbar != null)
        {
            chartToolbar.updateToolbar();
        }
    }

    @Override
    public int getPersistenceType()
    {
        return TopComponent.PERSISTENCE_ALWAYS;
    }

    @Override
    protected String preferredID()
    {
        return PREFERRED_ID;
    }

    @Override
    protected Object writeReplace()
    {
        return new ResolvableHelper(this);
    }

    @Override
    protected void componentOpened()
    {
        super.componentOpened();
        if (chartData != null)
        {
            loading(chartData.getStock(), true);
        }
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
        {
            if (getSplitPanel() != null)
            {
                getSplitPanel().getChartPanel().getAnnotationPanel().requestFocusInWindow();
            }
        }
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
        bar.addAdjustmentListener((AdjustmentListener) this);

        return bar;
    }

    public void updateHorizontalScrollBar()
    {
        int last = getChartData().getLast();
        int items = getChartData().getPeriod();
        int itemsCount = getChartData().getDataset().getItemsCount();

        boolean updated = false;

        if (scrollBar.getModel().getExtent() != items)
        {
            scrollBar.getModel().setExtent(items);
            updated = true;
        }
        if (scrollBar.getModel().getMinimum() != 0)
        {
            scrollBar.getModel().setMinimum(0);
            updated = true;
        }
        if (scrollBar.getModel().getMaximum() != itemsCount)
        {
            scrollBar.getModel().setMaximum(itemsCount);
            updated = true;
        }
        if (scrollBar.getModel().getValue() != (last - items))
        {
            scrollBar.getModel().setValue(last - items);
            updated = true;
        }

        if (updated)
        {
            repaint();
        }
    }

    public void adjustmentValueChanged(AdjustmentEvent e)
    {
        int items = getChartData().getPeriod();
        int itemsCount = getChartData().getDataset().getItemsCount();
        int end = scrollBar.getModel().getValue() + items;

        end = end > itemsCount ? itemsCount : (end < items ? items : end);

        if (getChartData().getLast() != end)
        {
            getChartData().setLast(end);
            getChartData().calculate(this);
        }
        repaint();
    }

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

                if (getChartData().getLast() != last)
                {
                    getChartData().setLast(last);
                    getChartData().calculate(this);
                }
            }
        }
    }

    public void triggerDataProviderListener(DataProviderEvent evt)
    {
        if (!loadingFlag)
        {
            Stock source = (Stock) evt.getSource();
            if (source.equals(getChartData().getStock()))
            {
                String provider = getChartData().getDataProvider().getName();
                DataProvider dataProvider = DataProviderManager.getDefault().getDataProvider(provider);
                Dataset newDataset = dataProvider.getDataset(dataProvider.getKey(source, getChartData().getInterval()));

                if (newDataset != null)
                {
                    getChartData().setDataset(newDataset);
                    getChartData().updateDataset();

                    int last = getChartData().getLast();
                    int items = newDataset.getItemsCount();
                    if (last + 1 == items)
                    {
                        getChartData().setLast(items);
                    }
                    repaint();
                }
            }
        }
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
    {
        return new ChartNode(chartProperties);
    }

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

    public void changeDataset(final Stock stock, final Interval interval, final boolean newChart)
    {
        try
        {
            final DataProvider dataProvider = getChartData().getDataProvider();
            final JLabel loading = new JLabel(
                    NbBundle.getMessage(
                    ChartFrame.class,
                    "LBL_Loading",
                    stock.getCompanyName().equals("")
                    ? stock.getKey()
                    : stock.getCompanyName()),
                    ResourcesUtils.getLogo(),
                    SwingConstants.CENTER);
            loading.setOpaque(true);
            loading.setBackground(Color.WHITE);
            loading.setVerticalTextPosition(SwingConstants.BOTTOM);
            loading.setHorizontalTextPosition(SwingConstants.CENTER);

            this.getChartData().removeDataset();

            final RequestProcessor.Task task = RP.create(new Runnable()
            {

                @Override
                public void run()
                {
                    if (!newChart)
                    {
                        reinitialize();
                    }
                    add(loading, BorderLayout.CENTER);
                    DataProviderManager.getDefault().update(stock, interval, dataProvider);
                }
            });

            final ProgressHandle handle = ProgressHandleFactory.createHandle(loading.getText(), task);
            task.addTaskListener(new TaskListener()
            {

                @Override
                public void taskFinished(Task task)
                {
                    if (DataProviderManager.getDefault().isUpdated())
                    {
                        handle.finish();
                        DataProviderManager.getDefault().setUpdated(false);
                        Dataset dataset = dataProvider.getDataset(stock, interval);
                        if (dataset != null)
                        {
                            loadingFlag = false;
                            remove(loading);
                            getChartData().setDataset(dataset);
                            getChartData().setInterval(interval);
                            initComponents();
                        } else
                        {
                            loading.setText(NbBundle.getMessage(
                                    ChartFrame.class,
                                    "LBL_LoadingNoDataNew",
                                    stock.getCompanyName().equals("")
                                    ? stock.getKey()
                                    : stock.getCompanyName()));

                            if (!newChart)
                            {
                                NotifyDescriptor descriptor = new NotifyDescriptor.Confirmation(
                                        NbBundle.getMessage(
                                        ChartFrame.class,
                                        "LBL_LoadingNoData",
                                        stock.getCompanyName().equals("")
                                        ? stock.getKey()
                                        : stock.getCompanyName()),
                                        "No Data",
                                        NotifyDescriptor.YES_NO_OPTION);
                                Object retval = DialogDisplayer.getDefault().notify(descriptor);
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

            Dataset dataset = dataProvider.getDataset(stock, interval);
            if (dataset == null)
            {
                loadingFlag = true;
                handle.start();
                task.schedule(0);
            } else
            {
                if (dataset.getItemsCount() >= 2)
                {
                    if (!newChart)
                    {
                        reinitialize();
                    }
                    getChartData().setDataset(dataset);
                    getChartData().setInterval(interval);
                    initComponents();
                } else
                {
                    handle.start();
                    task.schedule(0);
                }
            }
        } catch (Exception ex)
        {
            LOG.log(Level.WARNING, ex.getMessage());
        }
    }

    private void loading(final Stock newStock, final boolean newChart)
    {
        try
        {
            final DataProvider dataProvider = getChartData().getDataProvider();
            final Interval interval = getChartData().getInterval();

            final JLabel loading = new JLabel(
                    NbBundle.getMessage(
                    ChartFrame.class,
                    "LBL_Loading",
                    newStock.getCompanyName().equals("")
                    ? newStock.getKey()
                    : newStock.getCompanyName()),
                    ResourcesUtils.getLogo(),
                    SwingConstants.CENTER);
            loading.setOpaque(true);
            loading.setBackground(Color.WHITE);
            loading.setVerticalTextPosition(SwingConstants.BOTTOM);
            loading.setHorizontalTextPosition(SwingConstants.CENTER);

            final RequestProcessor.Task task = RP.create(new Runnable()
            {

                @Override
                public void run()
                {
                    if (!newChart)
                    {
                        reinitialize();
                    }
                    add(loading, BorderLayout.CENTER);
                    DataProviderManager.getDefault().update(newStock, interval, dataProvider);
                }
            });

            final ProgressHandle handle = ProgressHandleFactory.createHandle(loading.getText(), task);
            task.addTaskListener(new TaskListener()
            {

                @Override
                public void taskFinished(Task task)
                {
                    if (DataProviderManager.getDefault().isUpdated())
                    {
                        handle.finish();
                        DataProviderManager.getDefault().setUpdated(false);
                        Dataset dataset = dataProvider.getDataset(newStock, interval);
                        if (dataset != null)
                        {
                            loadingFlag = false;
                            remove(loading);
                            getChartData().setDataset(dataset);
                            getChartData().setInterval(interval);
                            initComponents();
                        } else
                        {
                            loading.setText(NbBundle.getMessage(
                                    ChartFrame.class,
                                    "LBL_LoadingNoDataNew",
                                    newStock.getCompanyName().equals("")
                                    ? newStock.getKey()
                                    : newStock.getCompanyName()));

                            if (!newChart)
                            {
                                NotifyDescriptor descriptor = new NotifyDescriptor.Confirmation(
                                        NbBundle.getMessage(
                                        ChartFrame.class,
                                        "LBL_LoadingNoData",
                                        newStock.getCompanyName().equals("")
                                        ? newStock.getKey()
                                        : newStock.getCompanyName()),
                                        "No Data",
                                        NotifyDescriptor.YES_NO_OPTION);
                                Object retval = DialogDisplayer.getDefault().notify(descriptor);
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

            Dataset dataset = dataProvider.getDataset(newStock, interval);
            if (dataset == null)
            {
                loadingFlag = true;
                handle.start();
                task.schedule(0);
            } else
            {
                if (dataset.getItemsCount() >= 2)
                {
                    if (!newChart)
                    {
                        reinitialize();
                    }
                    getChartData().setDataset(dataset);
                    getChartData().setInterval(interval);
                    initComponents();
                } else
                {
                    handle.start();
                    task.schedule(0);
                }
            }
        } catch (Exception e)
        {
            LOG.log(Level.WARNING, e.getMessage());
        }
    }

    public void stockChanged(StockEvent evt)
    {
        final HistoryItem historyItem = (HistoryItem) evt.getSource();
        final Stock newStock = historyItem.getStock();
        final Interval interval = DataProvider.getInterval(historyItem.getIntervalHash());

        oldStock = getChartData().getStock();
        oldInterval = getChartData().getInterval();

        getChartData().setStock(newStock);
        getChartData().setInterval(interval);
        setName(NbBundle.getMessage(
                ChartFrame.class,
                "CTL_ChartFrame",
                newStock.getKey()));
        setToolTipText(NbBundle.getMessage(
                ChartFrame.class,
                "TOOL_ChartFrame",
                newStock.getCompanyName()));
        getSplitPanel().getChartPanel().updateStock();
        loading(newStock, false);
    }
    private Stock oldStock = null;
    private Interval oldInterval = null;

    final static class ResolvableHelper implements Serializable
    {

        private static final long serialVersionUID = SerialVersion.APPVERSION;
        private ChartProperties chartProperties;
        private ChartData chartData;
        private String dataProvider;
        private HistoryItem currentHistoryItem;
        private HistoryItem[] backList;
        private HistoryItem[] fwdList;

        private ResolvableHelper(ChartFrame chartFrame)
        {
            chartProperties = chartFrame.getChartProperties();
            chartData = chartFrame.getChartData();
            dataProvider = chartData.getDataProvider().getName();

            chartData.setSavedIndicators(chartFrame.getSplitPanel().getIndicatorsPanel().getIndicatorsList());
            chartData.setSavedOverlays(chartFrame.getSplitPanel().getChartPanel().getOverlays());
            chartData.setAnnotationsCount(chartFrame.getAnnotationCount());
            chartData.setAnnotations(chartFrame.getAnnotations());

            currentHistoryItem = chartFrame.getHistory().getCurrent();
            backList = chartFrame.getHistory().getBackHistoryList();
            fwdList = chartFrame.getHistory().getFwdHistoryList();
        }

        public synchronized Object readResolve()
        {
                DataProvider dp = DataProviderManager.getDefault().getDataProvider(dataProvider);
                chartData.setDataProvider(dp);

                ChartFrame chartFrame = new ChartFrame(chartData);
                chartFrame.setChartProperties(chartProperties);

                History history = new History();
                history.initialize();
                history.setCurrent(currentHistoryItem);
                history.setBackHistoryList(backList);
                history.setFwdHistoryList(fwdList);
                chartFrame.setHistory(history);

                chartFrame.setRestored(true);
                return chartFrame;
        }
    }
}
