package org.chartsy.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.SwingConstants;
import javax.swing.event.EventListenerList;
import org.chartsy.main.chart.Annotation;
import org.chartsy.main.chart.Chart;
import org.chartsy.main.chart.Indicator;
import org.chartsy.main.chart.Overlay;
import org.chartsy.main.data.ChartData;
import org.chartsy.main.data.DataProvider;
import org.chartsy.main.data.Dataset;
import org.chartsy.main.data.Stock;
import org.chartsy.main.events.DataProviderEvent;
import org.chartsy.main.events.DataProviderListener;
import org.chartsy.main.history.History;
import org.chartsy.main.history.HistoryItem;
import org.chartsy.main.intervals.Interval;
import org.chartsy.main.managers.CacheManager;
import org.chartsy.main.managers.DatasetUsage;
import org.chartsy.main.resources.ResourcesUtils;
import org.chartsy.main.templates.Template;
import org.chartsy.main.utils.ChartNode;
import org.chartsy.main.utils.GraphicsUtils;
import org.chartsy.main.utils.MainActions;
import org.chartsy.main.utils.SerialVersion;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.AbstractNode;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.openide.util.RequestProcessor;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author viorel.gheba
 */
public class ChartFrame extends TopComponent
	implements AdjustmentListener, MouseWheelListener, DataProviderListener
{

    public ChartFrame()
    {
		int id = ID.incrementAndGet();
		PREFERRED_ID = NbBundle.getMessage(ChartFrame.class, "ID_ChartFrame", Integer.toString(id));
		storeLastID();

		setLayout(new BorderLayout());
		setName(NbBundle.getMessage(ChartFrame.class, "CTL_ChartFrameEmpty"));
		setToolTipText(NbBundle.getMessage(ChartFrame.class, "TOOL_ChartFrameEmpty"));

		chartProperties = new ChartProperties();		
		history = new History();
    }

	public ChartFrame(String id)
	{
		PREFERRED_ID = id;
		setLayout(new BorderLayout());
		setName(NbBundle.getMessage(ChartFrame.class, "CTL_ChartFrameEmpty"));
		setToolTipText(NbBundle.getMessage(ChartFrame.class, "TOOL_ChartFrameEmpty"));
	}

    private void initComponents()
    {
		setOpaque(false);
		setDoubleBuffered(true);

        chartToolbar = new ChartToolbar(this);
        mainPanel = new MainPanel(this);
		scrollBar = new JScrollBar(JScrollBar.HORIZONTAL);
        scrollBar.setAlignmentX(java.awt.Component.RIGHT_ALIGNMENT);

		add(chartToolbar, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(scrollBar, BorderLayout.SOUTH);

		validate();

		if (restored)
		{
			chartProperties.setMarkerVisibility(true);

			for (Overlay overlay : chartData.getSavedOverlays())
				overlayAdded(overlay);
			for (Indicator indicator : chartData.getSavedIndicators())
				indicatorAdded(indicator);
			restoreAnnotations();

			revalidate();
			repaint();
			getSplitPanel().getIndicatorsPanel().calculateHeight();
			revalidate();
			repaint();

            chartData.clearSavedIndicators();
            chartData.clearSavedOverlays();
            chartData.clearAnnotations();
            chartData.clearAnnotationsCount();

			setRestored(false);
		} else
		{
			HistoryItem historyItem = new HistoryItem(
                chartData.getStock(),
                chartData.getInterval());
			history.setCurrent(historyItem);

			if (template != null)
			{
				List<Overlay> overlays = template.getOverlays();
				for (int i = 0; i < overlays.size(); i++)
					overlayAdded(overlays.get(i));
				List<Indicator> indicators = template.getIndicators();
				for (int i = 0; i < indicators.size(); i++)
					indicatorAdded(indicators.get(i));
			}
		}

		DatasetUsage.getInstance().addDataProviderListener(this);
		addMouseWheelListener((MouseWheelListener) this);
		scrollBar.addAdjustmentListener((AdjustmentListener) this);

		initialized = true;
    }

    public Template getTemplate()
    {
        return template;
    }

    public void setTemplate(Template template)
    {
        this.template = template;
        this.chartProperties.copyFrom(template.getChartProperties());

		if (initialized)
		{
			List<Overlay> overlays = getSplitPanel().getChartPanel().getOverlays();
			for (int i = 0; i < overlays.size(); i++)
				overlayRemoved(overlays.get(i));
			overlays = template.getOverlays();
			for (int i = 0; i < overlays.size(); i++)
				overlayAdded(overlays.get(i));

			List<Indicator> indicators = getSplitPanel().getIndicatorsPanel().getIndicatorsList();
			for (int i = 0; i < indicators.size(); i++)
				indicatorRemoved(indicators.get(i));
			indicators = template.getIndicators();
			for (int i = 0; i < indicators.size(); i++)
				indicatorAdded(indicators.get(i));
		}
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
		if (data == null)
			throw new IllegalArgumentException("ChartData shouldn't be null");
        chartData = data;
		addChartFrameListener(data);
		Stock stock = chartData.getStock();
		setName(NbBundle.getMessage(ChartFrame.class, "CTL_ChartFrame", stock.getKey()));
		setToolTipText(NbBundle.getMessage(ChartFrame.class, "TOOL_ChartFrame", stock.getCompanyName()));
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
			return mainPanel.getSplitPanel();
		return null;
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

    public void restoreAnnotations()
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
		if (popupMenu == null)
		{
			popupMenu = new JPopupMenu();
			popupMenu.add(MainActions.generateIntervalsMenu(this)); // change interval
			popupMenu.add(MainActions.generateChartsMenu(this)); // change chart
			popupMenu.add(new JMenuItem(MainActions.openIndicators(this))); // add indicators
			popupMenu.add(new JMenuItem(MainActions.openOverlays(this))); // add overlays
			popupMenu.add(MainActions.generateAnnotationsMenu(this)); // add annotation
			popupMenu.add(new JMenuItem(MainActions.exportImage(this))); // export image
			popupMenu.add(new JMenuItem(MainActions.printChart(this))); // print
			popupMenu.add(new JMenuItem(MainActions.chartProperties(this))); // chart settings
			popupMenu.add(new JMenuItem(MainActions.joinToConference(this))); // join to symbol conference
			popupMenu.add(new JMenuItem(MainActions.addToFavorites(this))); // add to favorites
			popupMenu.add(new JMenuItem(MainActions.toggleToolbarVisibility(this))); // hide/show toolbar
			popupMenu.add(MainActions.generateTemplatesMenu(this)); // save to template

			popupMenu.getComponent(8).setVisible(false);
			popupMenu.getComponent(9).setVisible(false);
		}
		if (NbPreferences.root().node("/org/chartsy/chat").getBoolean("loggedin", false))
			popupMenu.getComponent(8).setVisible(true);
		if (!MainActions.isInFavorites(this))
			popupMenu.getComponent(9).setVisible(true);
        return popupMenu;
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
	public void update(Graphics g)
	{
		paint(g);
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
	protected void componentClosed()
	{
		super.componentClosed();
		DatasetUsage.getInstance().removeDataProviderListener(this);
		String key = chartData.getDatasetKey();
		DatasetUsage.getInstance().chartClosed(key);
	}

    @Override
    protected void componentOpened()
    {
        super.componentOpened();
        if (chartData != null)
            loading(chartData.getStock(), chartData.getInterval(), true);
    }

    @Override
    protected void componentActivated()
    {
		super.componentActivated();
		if (chartToolbar != null)
			chartToolbar.isLoggedInChat();
        componentFocused();
    }

    public void componentFocused()
    {
        if (getMainPanel() != null)
        {
            if (getSplitPanel() != null)
            {
                getSplitPanel().getChartPanel().getAnnotationPanel().requestFocusInWindow();
				//getSplitPanel().getIndicatorsPanel().calculateHeight();
				//revalidate();
				//repaint();
            }
        }
    }

	@Override
	protected void componentShowing() {
		super.componentShowing();
		if (getMainPanel() != null)
        {
            if (getSplitPanel() != null)
            {
				getSplitPanel().getIndicatorsPanel().calculateHeight();
            }
        }
		revalidate();
		repaint();
	}


	public void resetHorizontalScrollBar()
	{
		chartData.setPeriod(-1);
		chartData.setLast(-1);
		chartData.calculate(this);
		int last = getChartData().getLast();
		int items = getChartData().getPeriod();
		scrollBar.getModel().setExtent(items);
		scrollBar.getModel().setMinimum(0);
		scrollBar.getModel().setMaximum(last);
		scrollBar.getModel().setValue(last - items);
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

	@Override
    public void adjustmentValueChanged(AdjustmentEvent e)
    {
        int items = getChartData().getPeriod();
        int itemsCount = getChartData().getDataset().getItemsCount();
        int end = e.getValue() + items;

        end = end > itemsCount ? itemsCount : (end < items ? items : end);

        if (getChartData().getLast() != end)
        {
            getChartData().setLast(end);
            getChartData().calculate(this);
        }
		repaint();
    }

	@Override
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

    public BufferedImage getBufferedImage(int width, int height)
    {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = GraphicsUtils.prepareGraphics(image.createGraphics());
        g.setColor(chartProperties.getBackgroundColor());
        g.fillRect(0, 0, width, height);
        mainPanel.paintComponents(g);
        g.dispose();
        return image;
    }

    public AbstractNode getNode()
    {
		if (node == null)
			node = new ChartNode(chartProperties);
        return node;
    }

    private void loading(final Stock stock, final Interval interval, final boolean newChart)
    {
		if (!newChart)
		{
			oldStock = chartData.getStock();
			chartData.setStock(stock);
			oldInterval = chartData.getInterval();
			chartData.setInterval(interval);
			chartToolbar.setVisible(false);
			mainPanel.setVisible(false);
			scrollBar.setVisible(false);
			revalidate();
			repaint();
		}
		
		final DataProvider dataProvider = getChartData().getDataProvider();
		final String key = dataProvider.getDatasetKey(stock, interval);
		final JLabel loading = getLoadingLabel(stock);
		add(loading, BorderLayout.CENTER);
		revalidate();
		repaint();

		final ProgressHandle handle = ProgressHandleFactory.createHandle(loading.getText());
		handle.start();
		handle.switchToIndeterminate();
		final Runnable runnable = new Runnable()
		{
			@Override
			public void run()
			{
				loadingError = false;
				try
				{
					if (!DatasetUsage.getInstance().isDatasetInMemory(key))
					{
						dataProvider.fetchDataset(stock, interval);
					} else
					{
						Dataset dataset = DatasetUsage.getInstance().getDatasetFromMemory(key);
						if (dataset.getItemsCount() < 3)
							dataProvider.fetchDataset(stock, interval);
					}
				} catch (Exception ex)
				{
					loadingError = true;
				}

				handle.finish();
				if (!loadingError)
				{
					DatasetUsage.getInstance().fetchDataset(key);
					DatasetUsage.getInstance().addDatasetUpdater(dataProvider.getName(), stock, interval);
					datasetKeyChanged(key);
					remove(loading);
					if (!newChart)
					{
						setName(NbBundle.getMessage(ChartFrame.class, "CTL_ChartFrame", stock.getKey()));
						setToolTipText(NbBundle.getMessage(ChartFrame.class, "TOOL_ChartFrame", stock.getCompanyName()));
						HistoryItem item = new HistoryItem(stock, interval);
						history.setCurrent(item);
						DatasetUsage.getInstance().chartClosed(
							dataProvider.getDatasetKey(oldStock, oldInterval));
						resetHorizontalScrollBar();
						chartToolbar.setVisible(true);
						chartToolbar.updateToolbar();
						mainPanel.setVisible(true);
						scrollBar.setVisible(true);
					} else
					{
						initComponents();
					}
				} else
				{
					if (stock.hasCompanyName())
					{
						loading.setText(NbBundle.getMessage(ChartFrame.class, "LBL_LoadingNoDataNew", stock.getCompanyName()));
					} else
					{
						loading.setText(NbBundle.getMessage(ChartFrame.class, "LBL_LoadingNoDataNew", stock.getKey()));
					}
					if (!newChart)
						showConfirmation(stock, loading);
				}
				revalidate();
				repaint();
			}
		};

		WindowManager.getDefault().invokeWhenUIReady(runnable);

		/*task = RP.create(runnable);
		task.addTaskListener(new TaskListener()
		{
			@Override
			public void taskFinished(Task task)
			{
				handle.finish();
				if (!loadingError)
				{
					DatasetUsage.getInstance().fetchDataset(key);
					DatasetUsage.getInstance().addDatasetUpdater(dataProvider.getName(), stock, interval);
					datasetKeyChanged(key);
					remove(loading);
					if (!newChart)
					{
						HistoryItem item = new HistoryItem(stock, interval);
						history.setCurrent(item);
						DatasetUsage.getInstance().chartClosed(
							dataProvider.getDatasetKey(oldStock, oldInterval));
						resetHorizontalScrollBar();
						chartToolbar.setVisible(true);
						chartToolbar.updateToolbar();
						mainPanel.setVisible(true);
						scrollBar.setVisible(true);
					} else
					{
						initComponents();
					}
				} else
				{
					if (stock.hasCompanyName())
					{
						loading.setText(NbBundle.getMessage(ChartFrame.class, "LBL_LoadingNoDataNew", stock.getCompanyName()));
					} else
					{
						loading.setText(NbBundle.getMessage(ChartFrame.class, "LBL_LoadingNoDataNew", stock.getKey()));
					}
					if (!newChart)
						showConfirmation(stock, loading);
				}
				revalidate();
				repaint();
			}
		});
		task.schedule(0);*/
    }

	private JLabel getLoadingLabel(Stock stock)
	{
		String text;
		text = NbBundle.getMessage(ChartFrame.class, "LBL_Loading", stock.getKey());
		ImageIcon logo = ResourcesUtils.getLogo();
		JLabel loading = new JLabel(text, logo, SwingConstants.CENTER);
		loading.setOpaque(true);
		loading.setBackground(Color.WHITE);
		loading.setVerticalTextPosition(SwingConstants.BOTTOM);
		loading.setHorizontalTextPosition(SwingConstants.CENTER);
		return loading;
	}

	private void showConfirmation(final Stock stock, final JLabel loading)
	{
		NotifyDescriptor descriptor = new NotifyDescriptor.Confirmation("");
		descriptor.setTitle("No Data");
		if (stock.hasCompanyName())
		{
			descriptor.setMessage(NbBundle.getMessage(ChartFrame.class, "LBL_LoadingNoData", stock.getCompanyName()));
		} else
		{
			descriptor.setMessage(NbBundle.getMessage(ChartFrame.class, "LBL_LoadingNoData", stock.getKey()));
		}
		descriptor.setOptionType(NotifyDescriptor.YES_NO_OPTION);
		Object retval = DialogDisplayer.getDefault().notify(descriptor);
		if (retval.equals(NotifyDescriptor.YES_OPTION))
		{
			remove(loading);
			DataProvider dataProvider = getChartData().getDataProvider();
			chartData.setDatasetKey(dataProvider.getDatasetKey(oldStock, oldInterval));
			chartData.setStock(oldStock);
			chartData.setInterval(oldInterval);
			resetHorizontalScrollBar();
			chartToolbar.setVisible(true);
			mainPanel.setVisible(true);
			scrollBar.setVisible(true);
			revalidate();
			oldStock = null;
			oldInterval = null;
		}
	}

	@Override
	public void triggerDataProviderListener(DataProviderEvent evt)
	{
		String key = chartData.getDatasetKey();
		if (key.equals((String) evt.getSource()))
		{
			int itemsAdded = evt.getItemsAdded();
			int last = chartData.getLast();
			datasetKeyChanged(key);
			int count = chartData.getDataset().getItemsCount();
			if (last == count - itemsAdded)
				resetHorizontalScrollBar();
			revalidate();
			repaint();
		}
	}

    final static class ResolvableHelper implements Serializable
    {

        private static final long serialVersionUID = SerialVersion.APPVERSION;

		private String id;
        private ChartProperties chartProperties;
        private ChartData chartData;
        private String dataProvider;
        private HistoryItem currentHistoryItem;
        private HistoryItem[] backList;
        private HistoryItem[] fwdList;

        private ResolvableHelper(ChartFrame chartFrame)
        {
			id = chartFrame.preferredID();
			chartProperties = chartFrame.getChartProperties();
			chartProperties.clearPropertyChangeListenerList();
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

        public Object readResolve()
        {
			chartData.setDataProviderName(dataProvider);

			ChartFrame chartFrame = new ChartFrame(id);
			chartFrame.setChartData(chartData);
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

	private transient EventListenerList chartFrameListeners;

	private EventListenerList listenerList()
	{
		if (chartFrameListeners == null)
			chartFrameListeners = new EventListenerList();
		return chartFrameListeners;
	}

	public void addChartFrameListener(ChartFrameListener listener)
	{
		listenerList().add(ChartFrameListener.class, listener);
	}

	public void removeChartFrameListener(ChartFrameListener listener)
	{
		listenerList().remove(ChartFrameListener.class, listener);
	}

	public void historyItemChanged(HistoryItem item)
	{
		Stock newStock = item.getStock();
		Interval newInterval = item.getInterval();
		loading(newStock, newInterval, false);
	}

	public void stockChanged(Stock newStock)
	{
		Interval interval = chartData.getInterval();
		loading(newStock, interval, false);
		
		ChartFrameListener[] listeners = listenerList().getListeners(ChartFrameListener.class);
		for (ChartFrameListener listener : listeners)
			listener.stockChanged(newStock);
	}

	public void intervalChanged(Interval newInterval)
	{
		Stock stock = chartData.getStock();
		loading(stock, newInterval, false);

		ChartFrameListener[] listeners = listenerList().getListeners(ChartFrameListener.class);
		for (ChartFrameListener listener : listeners)
			listener.intervalChanged(newInterval);
	}

	public void chartChanged(Chart newChart)
	{
		ChartFrameListener[] listeners = listenerList().getListeners(ChartFrameListener.class);
		for (ChartFrameListener listener : listeners)
			listener.chartChanged(newChart);
	}

	public void datasetKeyChanged(String datasetKey)
	{
		ChartFrameListener[] listeners = listenerList().getListeners(ChartFrameListener.class);
		for (ChartFrameListener listener : listeners)
			listener.datasetKeyChanged(datasetKey);
	}

	public void overlayAdded(Overlay overlay)
	{
		addChartFrameListener(overlay);
		overlay.setDatasetKey(chartData.getDatasetKey());
		overlay.calculate();

		ChartFrameListener[] listeners = listenerList().getListeners(ChartFrameListener.class);
		for (ChartFrameListener listener : listeners)
			listener.overlayAdded(overlay);
	}

	public void overlayRemoved(Overlay overlay)
	{
		removeChartFrameListener(overlay);
		ChartFrameListener[] listeners = listenerList().getListeners(ChartFrameListener.class);
		for (ChartFrameListener listener : listeners)
			listener.overlayRemoved(overlay);
	}

	public void indicatorAdded(Indicator indicator)
	{
		addChartFrameListener(indicator);
		indicator.setDatasetKey(chartData.getDatasetKey());
		indicator.calculate();

		ChartFrameListener[] listeners = listenerList().getListeners(ChartFrameListener.class);
		for (ChartFrameListener listener : listeners)
			listener.indicatorAdded(indicator);
	}

	public void indicatorRemoved(Indicator indicator)
	{
		removeChartFrameListener(indicator);
		ChartFrameListener[] listeners = listenerList().getListeners(ChartFrameListener.class);
		for (ChartFrameListener listener : listeners)
			listener.indicatorRemoved(indicator);
	}

	public void zoomIn()
    {
        ChartFrameListener[] listeners = listenerList().getListeners(ChartFrameListener.class);
		for (ChartFrameListener listener : listeners)
		{
			double barWidth = chartProperties.getBarWidth();
			double newWidth = listener.zoomIn(barWidth);
			if (barWidth != newWidth)
			{
				chartProperties.setBarWidth(newWidth);
				repaint();
			}
		}
    }

    public void zoomOut()
    {
        ChartFrameListener[] listeners = listenerList().getListeners(ChartFrameListener.class);
		for (ChartFrameListener listener : listeners)
		{
			double barWidth = chartProperties.getBarWidth();
			double newWidth = listener.zoomOut(barWidth);
			if (barWidth != newWidth)
			{
				chartProperties.setBarWidth(newWidth);
				repaint();
			}
		}
    }

	static
	{
		try
		{
			int id = CacheManager.getInstance().getLastChartFrameId();
			ID = new AtomicInteger(id);
		} catch (Exception ex)
		{
			ID = new AtomicInteger();
		}
	}

	private static void storeLastID()
	{
		try { CacheManager.getInstance().cacheLastChartFrameId(ID.get());
		} catch (Exception ex)
		{}
	}

	public static ChartFrame getInstance()
	{
		ChartFrame chartFrame = new ChartFrame();
		return chartFrame;
	}

    public static ChartFrame findInstance(String id)
    {
        TopComponent win = WindowManager.getDefault().findTopComponent(id);
        if (win == null)
            return getInstance();
        if (win instanceof ChartFrame)
            return (ChartFrame) win;
        return getInstance();
    }

	private static AtomicInteger ID;
    private static String PREFERRED_ID;
    public static final Logger LOG = Logger.getLogger(ChartFrame.class.getName());
    //private static final RequestProcessor RP = new RequestProcessor("interruptible tasks", 1, true);

	private ChartProperties chartProperties;
	private ChartData chartData;
	private Template template;
    private History history;
	private transient AbstractNode node;

    private ChartToolbar chartToolbar;
    private MainPanel mainPanel;
    private JScrollBar scrollBar;
	private JPopupMenu popupMenu;

	private boolean initialized = false;
    private boolean restored = false;
    private boolean focus = true;
	boolean loadingError = false;

	private Stock oldStock = null;
    private Interval oldInterval = null;
	private transient RequestProcessor.Task task;

}
