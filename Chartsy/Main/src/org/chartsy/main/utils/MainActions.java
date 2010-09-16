package org.chartsy.main.utils;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import org.chartsy.main.ChartFrame;
import org.chartsy.main.ChartToolbar;
import org.chartsy.main.chart.Annotation;
import org.chartsy.main.chart.Chart;
import org.chartsy.main.data.ChartData;
import org.chartsy.main.data.Stock;
import org.chartsy.main.dialogs.AnnotationProperties;
import org.chartsy.main.dialogs.Indicators;
import org.chartsy.main.dialogs.Overlays;
import org.chartsy.main.dialogs.SettingsPanel;
import org.chartsy.main.favorites.nodes.RootAPI;
import org.chartsy.main.favorites.nodes.RootAPINode;
import org.chartsy.main.favorites.nodes.StockAPI;
import org.chartsy.main.favorites.nodes.StockAPINode;
import org.chartsy.main.history.HistoryItem;
import org.chartsy.main.intervals.Interval;
import org.chartsy.main.managers.AnnotationManager;
import org.chartsy.main.managers.ChartManager;
import org.chartsy.main.managers.TemplateManager;
import org.chartsy.main.resources.ResourcesUtils;
import org.netbeans.api.print.PrintManager;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor.Confirmation;
import org.openide.NotifyDescriptor.InputLine;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author viorel.gheba
 */
public final class MainActions
{

    private MainActions()
	{}

	/*
	 * ChartFrame popup actions & ChartToolbar actions
	 */

	public static Action zoomIn(ChartFrame chartFrame)							{ return ZoomIn.getAction(chartFrame); }
	public static Action zoomOut(ChartFrame chartFrame)							{ return ZoomOut.getAction(chartFrame); }
	public static Action intervalPopup(ChartFrame chartFrame)					{ return IntervalPopup.getAction(chartFrame); }
	public static Action chartPopup(ChartFrame chartFrame)						{ return ChartPopup.getAction(chartFrame); }
	public static Action openIndicators(ChartFrame chartFrame)					{ return OpenIndicators.getAction(chartFrame); }
	public static Action openOverlays(ChartFrame chartFrame)					{ return OpenOverlays.getAction(chartFrame); }
	public static Action annotationPopup(ChartFrame chartFrame)					{ return AnnotationPopup.getAction(chartFrame); }
	public static Action toggleMarker(ChartFrame chartFrame)					{ return ToggleMarker.getAction(chartFrame); }
	public static Action exportImage(ChartFrame chartFrame)						{ return ExportImage.getAction(chartFrame); }
	public static Action printChart(ChartFrame chartFrame)						{ return PrintChart.getAction(chartFrame); }
	public static Action chartProperties(ChartFrame chartFrame)					{ return ChartProps.getAction(chartFrame); }
	public static Action toggleToolbarVisibility(ChartFrame chartFrame)			{ return ToggleToolbarVisibility.getAction(chartFrame); }
	public static Action addToFavorites(ChartFrame chartFrame)					{ return AddToFavorites.getAction(chartFrame); }
	public static Action saveToTemplate(ChartFrame chartFrame)					{ return SaveToTemplate.getAction(chartFrame); }

	/*
	 * Submenu actions
	 */

	public static Action changeInterval
		(ChartFrame chartFrame, Interval interval, boolean current)
	{ return ChangeInterval.getAction(chartFrame, interval, current); }

	public static Action changeChart
		(ChartFrame chartFrame, String chartName, boolean current)
	{ return ChangeChart.getAction(chartFrame, chartName, current); }

	public static Action addAnnotation
		(String annotationName)
	{ return AddAnnotation.getAction(annotationName); }

    public static Action removeAllAnnotations
		(ChartFrame chartFrame)
	{ return RemoveAllAnnotations.getAction(chartFrame); }

    public static Action annotationProperties
		(ChartFrame chartFrame, Annotation annotation)
	{ return AnnotationProps.getAction(chartFrame, annotation); }

	/*
	 * ChartToolbar popup actions
	 */

	public static Action toggleToolbarSmallIcons
		(ChartFrame chartFrame, ChartToolbar chartToolbar)
	{ return ToggleToolbarSmallIcons.getAction(chartFrame, chartToolbar); }

	public static Action toggleToolbarShowLabels
		(ChartFrame chartFrame, ChartToolbar chartToolbar)
	{ return ToggleToolbarShowLabels.getAction(chartFrame, chartToolbar); }

	public static boolean isInFavorites(ChartFrame chartFrame)
	{
		TopComponent component
				= WindowManager.getDefault().findTopComponent("FavoritesComponent");
		if (component instanceof ExplorerManager.Provider)
		{
			ExplorerManager manager
				= ((ExplorerManager.Provider) component).getExplorerManager();
			Node node = manager.getRootContext();
			if (node instanceof RootAPINode)
			{
				RootAPI root = ((RootAPINode) node).getRoot();

				StockAPI stock = new StockAPI();
				stock.setSymbol(chartFrame.getChartData().getStock().getSymbol());
				stock.setExchange(chartFrame.getChartData().getStock().getExchange());
				stock.setCompanyName(chartFrame.getChartData().getStock().getCompanyName());
				stock.setDataProviderName(chartFrame.getChartData().getDataProvider().getName());

				return root.findStock(stock);
			}
		}

		return true;
	}

	public static JMenu generateIntervalsMenu(ChartFrame chartFrame)
	{
		JMenuItem menuItem;
		
		JMenu menu = new JMenu(NbBundle.getMessage(MainActions.class, "ACT_Intervals"));
		menu.setIcon(ResourcesUtils.getIcon16(NbBundle.getMessage(MainActions.class, "ICON_Intervals")));

		ChartData chartData = chartFrame.getChartData();
		Interval current = chartData.getInterval();

		for (Interval interval : chartData.getDataProvider().getIntervals())
		{
			menu.add(menuItem = new JMenuItem(MainActions.changeInterval(chartFrame, interval, interval.equals(current))));
			menuItem.setMargin(new Insets(0, 0, 0, 0));
		}

		if (chartData.getDataProvider().supportsIntraday())
		{
			menu.addSeparator();
			for (Interval interval : chartData.getDataProvider().getIntraDayIntervals())
			{
				menu.add(menuItem  = new JMenuItem(MainActions.changeInterval(chartFrame, interval, interval.equals(current))));
				menuItem.setMargin(new Insets(0, 0, 0, 0));
			}
		}

		return menu;
	}

	public static JMenu generateChartsMenu(ChartFrame chartFrame)
	{
		JMenuItem menuItem;

		JMenu menu = new JMenu(NbBundle.getMessage(MainActions.class, "ACT_Charts"));
		menu.setIcon(ResourcesUtils.getIcon16(NbBundle.getMessage(MainActions.class, "ICON_Charts")));

		ChartData chartData = chartFrame.getChartData();
		String current = chartData.getChart().getName();

		for (String chart : ChartManager.getDefault().getCharts())
		{
			menu.add(menuItem = new JMenuItem(MainActions.changeChart(chartFrame, chart, current.equals(chart))));
			menuItem.setMargin(new Insets(0, 0, 0, 0));
		}

		return menu;
	}

	public static JMenu generateAnnotationsMenu(ChartFrame chartFrame)
	{
		JMenuItem menuItem;

		JMenu menu = new JMenu(NbBundle.getMessage(MainActions.class, "ACT_Annotations"));
		menu.setIcon(ResourcesUtils.getIcon16(NbBundle.getMessage(MainActions.class, "ICON_Annotations")));

		for (String annotation : AnnotationManager.getDefault().getAnnotations())
		{
			menu.add(menuItem = new JMenuItem(
				MainActions.addAnnotation(annotation)));
			menuItem.setMargin(new Insets(0, 0, 0, 0));
		}

		menu.addSeparator();

		menu.add(menuItem = new JMenuItem(
			MainActions.removeAllAnnotations(chartFrame)));
		menuItem.setMargin(new Insets(0, 0, 0, 0));

		if (chartFrame.hasCurrentAnnotation())
		{
			Annotation current = chartFrame.getCurrentAnnotation();
			if (current.isSelected())
			{
				menu.addSeparator();
				menu.add(menuItem = new JMenuItem(
					MainActions.annotationProperties(chartFrame, current)));
				menuItem.setMargin(new Insets(0, 0, 0, 0));
			}
		}

		return menu;
	}

	private static JMenu generateTempMenu(ChartFrame chartFrame)
	{
		JMenu menu = new JMenu(NbBundle.getMessage(MainActions.class, "ACT_SelectTemplate"));
		for (Object template : TemplateManager.getDefault().getTemplateNames())
			if (!template.equals(chartFrame.getTemplate().getName()))
				menu.add(new JMenuItem(ChangeTemplate.getAction(chartFrame, (String)template)));
		return menu;
	}

	public static JMenu generateTemplatesMenu(ChartFrame chartFrame)
	{
		JMenu menu = new JMenu(NbBundle.getMessage(MainActions.class, "ACT_Templates"));
		menu.add(generateTempMenu(chartFrame));
		menu.add(new JMenuItem(MainActions.saveToTemplate(chartFrame)));
		return menu;
	}


	/*
	 * Abstract MainAction
	 */

	private static abstract class MainAction extends AbstractAction
	{

		public MainAction(String name, boolean flag)
		{
			putValue(NAME,
				NbBundle.getMessage(MainActions.class, "ACT_"+name));
			putValue(SHORT_DESCRIPTION,
				NbBundle.getMessage(MainActions.class, "TOOL_"+name));
			if (flag)
			{
				putValue(SMALL_ICON,
					ResourcesUtils.getIcon16(
					NbBundle.getMessage(MainActions.class, "ICON_"+name)));
				putValue(LONG_DESCRIPTION, name);
				putValue(LARGE_ICON_KEY,
					ResourcesUtils.getIcon24(
					NbBundle.getMessage(MainActions.class, "ICON_"+name)));
			}
		}

	}

	private static class ZoomIn extends MainAction
	{

		private ChartFrame chartFrame;

		public static Action getAction(ChartFrame chartFrame)
		{
			return new ZoomIn(chartFrame);
		}

		private ZoomIn(ChartFrame chartFrame)
		{
			super("ZoomIn", true);
			this.chartFrame = chartFrame;
		}

		public void actionPerformed(ActionEvent e)
		{
			chartFrame.getChartData().zoomIn(chartFrame);
			chartFrame.componentFocused();
		}

	}

	private static class ZoomOut extends MainAction
	{

		private ChartFrame chartFrame;

		public static Action getAction(ChartFrame chartFrame)
		{
			return new ZoomOut(chartFrame);
		}

		private ZoomOut(ChartFrame chartFrame)
		{
			super("ZoomOut", true);
			this.chartFrame = chartFrame;
		}

		public void actionPerformed(ActionEvent e)
		{
			chartFrame.getChartData().zoomOut(chartFrame);
			chartFrame.componentFocused();
		}
		
	}

	private static class IntervalPopup extends MainAction
	{

		private ChartFrame chartFrame;

		public static Action getAction(ChartFrame chartFrame)
		{
			return new IntervalPopup(chartFrame);
		}

		private IntervalPopup(ChartFrame chartFrame)
		{
			super("Intervals", true);
			this.chartFrame = chartFrame;
		}

		public void actionPerformed(ActionEvent e)
		{
			JButton button = (JButton) e.getSource();
			JPopupMenu popupMenu = new JPopupMenu();

			Interval current = chartFrame.getChartData().getInterval();

			JMenuItem item;
			for (Interval interval : chartFrame.getChartData().getDataProvider().getIntervals())
			{
				popupMenu.add(item = new JMenuItem(
					MainActions.changeInterval(chartFrame, interval, interval.equals(current))));
				item.setMargin(new java.awt.Insets(0,0,0,0));
			}

			if (chartFrame.getChartData().getDataProvider().supportsIntraday())
			{
				popupMenu.addSeparator();

				for (Interval interval : chartFrame.getChartData().getDataProvider().getIntraDayIntervals())
				{
					popupMenu.add(item = new JMenuItem(
						MainActions.changeInterval(chartFrame, interval, interval.equals(current))));
					item.setMargin(new java.awt.Insets(0,0,0,0));
				}
			}

			if (popupMenu.getComponents().length > 0)
				popupMenu.show(button, 0, button.getHeight());
		}

	}

	private static class ChangeInterval extends MainAction
	{

		private ChartFrame chartFrame;
		private Interval interval;
		private boolean current;

		public static Action getAction(ChartFrame chartFrame, Interval interval, boolean current)
		{
			return new ChangeInterval(chartFrame, interval, current);
		}

		private ChangeInterval(ChartFrame chartFrame, Interval interval, boolean current)
		{
			super("Intervals", current);
			putValue(NAME, interval.getName());
			putValue(SHORT_DESCRIPTION, interval.getName());
			this.chartFrame = chartFrame;
			this.interval = interval;
			this.current = current;
		}

		public void actionPerformed(ActionEvent e)
		{
			if (!current)
			{
				Stock stock = chartFrame.getChartData().getStock();
				if (chartFrame.getChartData().getDataProvider().datasetExists(stock, interval))
				{
					if (chartFrame.getChartData().updateDataset(interval))
					{
						if (chartFrame.getHistory() != null)
						{
							HistoryItem item = chartFrame.getHistory().getCurrent();
							if (item != null)
							{
								chartFrame.getHistory().addHistoryItem(item);
								chartFrame.getHistory().clearForwardHistory();
								chartFrame.getHistory().setCurrent(
									new HistoryItem(stock, interval.hashCode()));
							}
						}

						chartFrame.getChartData().calculate(chartFrame);
						chartFrame.updateToolbar();
						chartFrame.validate();
						chartFrame.repaint();
					}
				}
				else
				{
					HistoryItem item = chartFrame.getHistory().getCurrent();
					if (item != null)
					{
						chartFrame.getHistory().addHistoryItem(item);
						chartFrame.getHistory().clearForwardHistory();
					}
					chartFrame.changeDataset(stock, interval, false);
				}
			}
		}

	}

	private static class ChartPopup extends MainAction
	{

		private ChartFrame chartFrame;

		public static Action getAction(ChartFrame chartFrame)
		{
			return new ChartPopup(chartFrame);
		}

		private ChartPopup(ChartFrame chartFrame)
		{
			super("Charts", true);
			this.chartFrame = chartFrame;
		}

		public void actionPerformed(ActionEvent e)
		{
			String current = chartFrame.getChartData().getChart().getName();

			JButton button = (JButton) e.getSource();
			JPopupMenu popupMenu = new JPopupMenu();

			JMenuItem item;
			for (String chart : ChartManager.getDefault().getCharts())
			{
				popupMenu.add(item = new JMenuItem(
					MainActions.changeChart(chartFrame, chart, chart.equals(current))));
				item.setMargin(new Insets(0, 0, 0, 0));
			}

			popupMenu.show(button, 0, button.getHeight());
		}
		
	}

	private static class ChangeChart extends MainAction
	{

		private ChartFrame chartFrame;
		private String chartName;

		public static Action getAction(ChartFrame chartFrame, String chartName, boolean current)
		{
			return new ChangeChart(chartFrame, chartName, current);
		}

		private ChangeChart(ChartFrame chartFrame, String chartName, boolean current)
		{
			super("Charts", current);
			this.chartFrame = chartFrame;
			this.chartName = chartName;
			putValue(NAME, chartName);
			putValue(SHORT_DESCRIPTION, chartName);
		}

		public void actionPerformed(ActionEvent e)
		{
			Chart chart = ChartManager.getDefault().getChart(chartName);
			chartFrame.getChartData().setChart(chart);
			chartFrame.validate();
			chartFrame.repaint();
		}

	}

	private static class OpenIndicators extends MainAction
	{

		private ChartFrame chartFrame;

		public static Action getAction(ChartFrame chartFrame)
		{
			return new OpenIndicators(chartFrame);
		}

		private OpenIndicators(ChartFrame chartFrame)
		{
			super("Indicators", true);
			this.chartFrame = chartFrame;
		}

		public void actionPerformed(ActionEvent e)
		{
			Indicators dialog = new Indicators(new JFrame(), true);
			dialog.setChartFrame(chartFrame);
			dialog.setLocationRelativeTo(chartFrame);
			dialog.initForm();
			dialog.setVisible(true);
		}

	}

	private static class OpenOverlays extends MainAction
	{

		private ChartFrame chartFrame;

		public static Action getAction(ChartFrame chartFrame)
		{
			return new OpenOverlays(chartFrame);
		}

		private OpenOverlays(ChartFrame chartFrame)
		{
			super("Overlays", true);
			this.chartFrame = chartFrame;
		}

		public void actionPerformed(ActionEvent e)
		{
			Overlays dialog = new Overlays(new JFrame(), true);
			dialog.setChartFrame(chartFrame);
			dialog.setLocationRelativeTo(chartFrame);
			dialog.initForm();
			dialog.setVisible(true);
		}

	}

	private static class AnnotationPopup extends MainAction
	{

		private ChartFrame chartFrame;

		public static Action getAction(ChartFrame chartFrame)
		{
			return new AnnotationPopup(chartFrame);
		}

		private AnnotationPopup(ChartFrame chartFrame)
		{
			super("Annotations", true);
			this.chartFrame = chartFrame;
		}

		public void actionPerformed(ActionEvent e)
		{
			JButton button = (JButton) e.getSource();
			JPopupMenu popup = new JPopupMenu();

			JMenuItem item;

			for (String annotation : AnnotationManager.getDefault().getAnnotations())
			{
				popup.add(item = new JMenuItem(
					MainActions.addAnnotation(annotation)));
				item.setMargin(new Insets(0, 0, 0, 0));
			}

			popup.addSeparator();

			popup.add(item = new JMenuItem(
				MainActions.removeAllAnnotations(chartFrame)));
			item.setMargin(new Insets(0, 0, 0, 0));

			if (chartFrame.hasCurrentAnnotation())
			{
				Annotation current = chartFrame.getCurrentAnnotation();
				if (current.isSelected())
				{
					popup.addSeparator();
					popup.add(item = new JMenuItem(
						MainActions.annotationProperties(chartFrame, current)));
					item.setMargin(new Insets(0, 0, 0, 0));
				}
			}

			popup.show(button, 0, button.getHeight());
		}

	}

	private static class AddAnnotation extends MainAction
	{

		private String annotationName;

		public static Action getAction(String annotationName)
		{
			return new AddAnnotation(annotationName);
		}

		private AddAnnotation(String annotationName)
		{
			super("Annotations", true);
			putValue(NAME, annotationName);
			putValue(SHORT_DESCRIPTION, annotationName);
			this.annotationName = annotationName;
		}

		public void actionPerformed(ActionEvent e)
		{
			Annotation annotation = AnnotationManager.getDefault().getAnnotation(annotationName);
			AnnotationManager.getDefault().setNewAnnotation(annotation);
		}

	}

	private static class RemoveAllAnnotations extends MainAction
	{

		private ChartFrame chartFrame;

		public static Action getAction(ChartFrame chartFrame)
		{
			return new RemoveAllAnnotations(chartFrame);
		}

		private RemoveAllAnnotations(ChartFrame chartFrame)
		{
			super("AnnotationsRemoveAll", false);
			this.chartFrame = chartFrame;
		}

		public void actionPerformed(ActionEvent e)
		{
			chartFrame.removeAllAnnotations();
		}

	}

	private static class AnnotationProps extends MainAction
	{

		private ChartFrame chartFrame;
		private Annotation annotation;

		public static Action getAction(ChartFrame chartFrame, Annotation annotation)
		{
			return new AnnotationProps(chartFrame, annotation);
		}

		private AnnotationProps(ChartFrame chartFrame, Annotation annotation)
		{
			super("AnnotationsProperties", false);
			this.chartFrame = chartFrame;
			this.annotation = annotation;
		}

		public void actionPerformed(ActionEvent e)
		{
			AnnotationProperties dialog = new AnnotationProperties(new JFrame(), true);
			dialog.initializeForm(annotation);
			dialog.setLocationRelativeTo(chartFrame);
			dialog.setVisible(true);
		}
		
	}

	private static class ToggleMarker extends MainAction
	{

		private ChartFrame chartFrame;

		public static Action getAction(ChartFrame chartFrame)
		{
			return new ToggleMarker(chartFrame);
		}

		private ToggleMarker(ChartFrame chartFrame)
		{
			super("Marker", true);
			this.chartFrame = chartFrame;
		}

		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() instanceof JToggleButton)
			{
				JToggleButton button = (JToggleButton) e.getSource();
				boolean enable = button.isSelected();
				chartFrame.getChartProperties().setMarkerVisibility(enable);
				chartFrame.validate();
				chartFrame.repaint();
				chartFrame.componentFocused();
			}
		}

	}

	private static class ExportImage extends MainAction
	{

		private ChartFrame chartFrame;

		public static Action getAction(ChartFrame chartFrame)
		{
			return new ExportImage(chartFrame);
		}

		private ExportImage(ChartFrame chartFrame)
		{
			super("ExportImage", true);
			this.chartFrame = chartFrame;
		}

		public void actionPerformed(ActionEvent e)
		{
			ImageExporter.getDefault().export(chartFrame);
			chartFrame.componentFocused();
		}

	}

	private static class PrintChart extends MainAction
	{

		private ChartFrame chartFrame;

		public static Action getAction(ChartFrame chartFrame)
		{
			return new PrintChart(chartFrame);
		}

		private PrintChart(ChartFrame chartFrame)
		{
			super("Print", true);
			this.chartFrame = chartFrame;
		}

		public void actionPerformed(ActionEvent e)
		{
			PrintManager.printAction(chartFrame.getMainPanel()).actionPerformed(e);
		}

	}

	private static class ChartProps extends MainAction
	{

		private ChartFrame chartFrame;

		public static Action getAction(ChartFrame chartFrame)
		{
			return new ChartProps(chartFrame);
		}

		private ChartProps(ChartFrame chartFrame)
		{
			super("ChartProperties", true);
			this.chartFrame = chartFrame;
		}

		public void actionPerformed(ActionEvent e)
		{
			SettingsPanel.getDefault().openSettingsWindow(chartFrame);
		}

	}

	private static class ToggleToolbarVisibility extends MainAction
	{

		private ChartFrame chartFrame;

		public static Action getAction(ChartFrame chartFrame)
		{
			return new ToggleToolbarVisibility(chartFrame);
		}

		private ToggleToolbarVisibility(ChartFrame chartFrame)
		{
			super(
				chartFrame.getChartProperties().getToolbarVisibility()
				? "HideToolbar"
				: "ShowToolbar",
				false);
			this.chartFrame = chartFrame;
		}

		public void actionPerformed(ActionEvent e)
		{
			chartFrame.getChartProperties().toggleToolbarVisibility();
			chartFrame.setToolbarVisibility();
		}
		
	}

	private static class AddToFavorites extends MainAction
	{

		private ChartFrame chartFrame;

		public static Action getAction(ChartFrame chartFrame)
		{
			return new AddToFavorites(chartFrame);
		}

		private AddToFavorites(ChartFrame chartFrame)
		{
			super("AddToFavorites", false);
			putValue(SMALL_ICON, ResourcesUtils.getFavoritesIcon());
			putValue(LARGE_ICON_KEY, ResourcesUtils.getFavoritesBigIcon());
			this.chartFrame = chartFrame;
		}

		public void actionPerformed(ActionEvent e)
		{
			TopComponent component
				= WindowManager.getDefault().findTopComponent("FavoritesComponent");
			if (component instanceof ExplorerManager.Provider)
			{
				ExplorerManager manager
					= ((ExplorerManager.Provider) component).getExplorerManager();
				Node node = manager.getRootContext();
				if (node instanceof RootAPINode)
				{
					RootAPI root = ((RootAPINode) node).getRoot();

					StockAPI stock = new StockAPI();
					stock.setSymbol(chartFrame.getChartData().getStock().getSymbol());
					stock.setExchange(chartFrame.getChartData().getStock().getExchange());
					stock.setCompanyName(chartFrame.getChartData().getStock().getCompanyName());
					stock.setDataProviderName(chartFrame.getChartData().getDataProvider().getName());

					StockAPINode stockNode = new StockAPINode(stock);
					((RootAPINode) node).getChildren().add(new Node[] { stockNode });

					root.addStock(stock);
				}
			}
		}

	}

	private static class ChangeTemplate extends MainAction
	{

		private ChartFrame chartFrame;
		private String template;

		public static Action getAction(ChartFrame chartFrame, String template)
		{
			return new ChangeTemplate(chartFrame, template);
		}

		private ChangeTemplate(ChartFrame chartFrame, String template)
		{
			super("SaveToTemplate", false);
			this.chartFrame = chartFrame;
			this.template = template;
			putValue(NAME, template);
			putValue(SHORT_DESCRIPTION, template);
		}

		public void actionPerformed(ActionEvent e)
		{
			chartFrame.setTemplate(TemplateManager.getDefault().getTemplate(template));
		}

	}

	private static class SaveToTemplate extends MainAction
	{

		private ChartFrame chartFrame;

		public static Action getAction(ChartFrame chartFrame)
		{
			return new SaveToTemplate(chartFrame);
		}

		private SaveToTemplate(ChartFrame chartFrame)
		{
			super("SaveToTemplate", false);
			this.chartFrame = chartFrame;
		}

		public void actionPerformed(ActionEvent e)
		{
			InputLine descriptor = new DialogDescriptor.InputLine(
				"Template Name:", "Save to Template");
			descriptor.setOptions(new Object[]
			{
				DialogDescriptor.OK_OPTION,
				DialogDescriptor.CANCEL_OPTION
			});
			Object ret = DialogDisplayer.getDefault().notify(descriptor);
			if (ret.equals(DialogDescriptor.OK_OPTION))
			{
				String name = descriptor.getInputText();
				if (!TemplateManager.getDefault().templateExists(name))
					TemplateManager.getDefault().saveToTemplate(name, chartFrame);
				else
				{
					Confirmation confirmation = new DialogDescriptor.Confirmation(
						"<html>This template already exists!<br>Do you want to overwrite this template?</html>", "Overwrite");
					Object obj = DialogDisplayer.getDefault().notify(confirmation);
					if (obj.equals(DialogDescriptor.OK_OPTION))
					{
						TemplateManager.getDefault().removeTemplate(name);
						TemplateManager.getDefault().saveToTemplate(name, chartFrame);
					}
				}
			}
		}

	}

	private static class ToggleToolbarSmallIcons extends MainAction
	{

		private ChartFrame chartFrame;
		private ChartToolbar chartToolbar;

		public static Action getAction(ChartFrame chartFrame, ChartToolbar chartToolbar)
		{
			return new ToggleToolbarSmallIcons(chartFrame, chartToolbar);
		}

		private ToggleToolbarSmallIcons(ChartFrame chartFrame, ChartToolbar chartToolbar)
		{
			super("SmallToolbarIcons", false);
			this.chartFrame = chartFrame;
			this.chartToolbar = chartToolbar;
		}

		public void actionPerformed(ActionEvent e)
		{
			chartFrame.getChartProperties().toggleToolbarSmallIcons();
			chartToolbar.toggleIcons();
		}
		
	}

	private static class ToggleToolbarShowLabels extends MainAction
	{

		private ChartFrame chartFrame;
		private ChartToolbar chartToolbar;

		public static Action getAction(ChartFrame chartFrame, ChartToolbar chartToolbar)
		{
			return new ToggleToolbarShowLabels(chartFrame, chartToolbar);
		}

		private ToggleToolbarShowLabels(ChartFrame chartFrame, ChartToolbar chartToolbar)
		{
			super("HideLabels", false);
			this.chartFrame = chartFrame;
			this.chartToolbar = chartToolbar;
		}

		public void actionPerformed(ActionEvent e)
		{
			chartFrame.getChartProperties().toggleShowLabels();
			chartToolbar.toggleLabels();
		}

	}

}
