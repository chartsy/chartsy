package org.chartsy.main.utils;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import org.chartsy.main.ChartFrame;
import org.chartsy.main.chart.Annotation;
import org.chartsy.main.chart.Chart;
import org.chartsy.main.data.Stock;
import org.chartsy.main.dialogs.AnnotationProperties;
import org.chartsy.main.dialogs.ChartSettings;
import org.chartsy.main.dialogs.Indicators;
import org.chartsy.main.dialogs.Overlays;
import org.chartsy.main.history.HistoryItem;
import org.chartsy.main.intervals.Interval;
import org.chartsy.main.managers.AnnotationManager;
import org.chartsy.main.managers.ChartManager;
import org.chartsy.main.resources.ResourcesUtils;
import org.netbeans.api.print.PrintManager;

/**
 *
 * @author viorel.gheba
 */
public final class MainActions {

    private MainActions() {}

    public static AbstractAction zoomIn(final ChartFrame chartFrame) 
    {
        final boolean b = chartFrame.getChartProperties().getToolbarSmallIcons();
        return new AbstractAction(" + ", 
                b ? ResourcesUtils.getIcon16("zoomin") : ResourcesUtils.getIcon24("zoomin"))
        {
            public void actionPerformed(ActionEvent e)
            {
                chartFrame.getChartData().zoomIn(chartFrame);
                chartFrame.componentFocused();
            }
        };
    }

    public static AbstractAction zoomOut(final ChartFrame chartFrame) 
    {
        final boolean b = chartFrame.getChartProperties().getToolbarSmallIcons();
        return new AbstractAction(" - ", 
                b ? ResourcesUtils.getIcon16("zoomout") : ResourcesUtils.getIcon24("zoomout"))
        {
            public void actionPerformed(ActionEvent e)
            {
                chartFrame.getChartData().zoomOut(chartFrame);
                chartFrame.componentFocused();
            }
        };
    }

    public static AbstractAction timeMenu(final ChartFrame chartFrame) 
    {
        final boolean b = chartFrame.getChartProperties().getToolbarSmallIcons();
        return new AbstractAction("Time", 
                b ? ResourcesUtils.getIcon16("time") : ResourcesUtils.getIcon24("time"))
        {
            public void actionPerformed(ActionEvent e)
            {
                JButton button = (JButton) e.getSource();
                JPopupMenu popupMenu = new JPopupMenu();

                Interval interval = chartFrame.getChartData().getInterval();
                Interval[] is = chartFrame.getChartData().getDataProvider().getIntervals();
                
                for (Interval i : is)
                {
                    JMenuItem item = new JMenuItem(changeTime(chartFrame, i, i.equals(interval)));
                    item.setMargin(new java.awt.Insets(0,0,0,0));
                    popupMenu.add(item);
                }

                if (chartFrame.getChartData().getDataProvider().supportsIntraday())
                {
                    popupMenu.addSeparator();

                    is = chartFrame.getChartData().getDataProvider().getIntraDayIntervals();
                    for (Interval i : is)
                    {
                        JMenuItem item = new JMenuItem(changeTime(chartFrame, i, i.equals(interval)));
                        item.setMargin(new java.awt.Insets(0,0,0,0));
                        popupMenu.add(item);
                    }
                }

                if (popupMenu.getComponents().length > 0)
                    popupMenu.show(button, 0, button.getHeight());
            }
        };
    }

    public static AbstractAction changeTime(final ChartFrame chartFrame, final Interval interval, final boolean current)
    {
        return new AbstractAction(interval.getName(), current ? ResourcesUtils.getIcon16("time") : null)
        {
            public void actionPerformed(ActionEvent e)
            {
                Interval current = chartFrame.getChartData().getInterval();
                if (!current.equals(interval))
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
                                    chartFrame.getHistory().setCurrent(new HistoryItem(stock, interval));
                                }
                            }

                            chartFrame.getChartData().calculate(chartFrame);
                            chartFrame.updateToolbar();
                            chartFrame.validate();
                            chartFrame.repaint();
                        }
                    }
                }
            }
        };
    }

    public static AbstractAction chartMenu(final ChartFrame chartFrame) 
    {
        final boolean b = chartFrame.getChartProperties().getToolbarSmallIcons();
        return new AbstractAction("Chart", 
                b ? ResourcesUtils.getIcon16("chart") : ResourcesUtils.getIcon24("chart"))
        {
            public void actionPerformed(ActionEvent e)
            {
                String current = chartFrame.getChartData().getChart().getName();
                List<String> list = ChartManager.getDefault().getCharts();

                JButton button = (JButton) e.getSource();
                JPopupMenu popupMenu = new JPopupMenu();

                for (String s : list)
                {
                    JMenuItem item = new JMenuItem(changeChart(chartFrame, s, current.equals(s)));
                    item.setMargin(new java.awt.Insets(0,0,0,0));
                    popupMenu.add(item);
                }

                popupMenu.show(button, 0, button.getHeight());
            }
        };
    }

    public static AbstractAction changeChart(final ChartFrame chartFrame, final String name, final boolean current)
    {
        return new AbstractAction(name, current ? ResourcesUtils.getIcon16("chart") : null)
        {
            public void actionPerformed(ActionEvent e)
            {
                Chart chart = ChartManager.getDefault().getChart(name);
                chartFrame.getChartData().setChart(chart);
                chartFrame.validate();
                chartFrame.repaint();
            }
        };
    }

    public static AbstractAction addIndicators(final ChartFrame chartFrame) 
    {
        final boolean b = chartFrame.getChartProperties().getToolbarSmallIcons();
        return new AbstractAction("Indicators", 
                b ? ResourcesUtils.getIcon16("indicator") : ResourcesUtils.getIcon24("indicator"))
        {
            public void actionPerformed(ActionEvent e)
            {
                Indicators dialog = new Indicators(new JFrame(), true);
                dialog.setChartFrame(chartFrame);
                dialog.setLocationRelativeTo(chartFrame);
                dialog.initForm();
                dialog.setVisible(true);
            }
        };
    }

    public static AbstractAction addOverlays(final ChartFrame chartFrame) 
    {
        final boolean b = chartFrame.getChartProperties().getToolbarSmallIcons();
        return new AbstractAction("Overlays", 
                b ? ResourcesUtils.getIcon16("overlay") : ResourcesUtils.getIcon24("overlay"))
        {
            public void actionPerformed(ActionEvent e)
            {
                Overlays dialog = new Overlays(new JFrame(), true);
                dialog.setChartFrame(chartFrame);
                dialog.setLocationRelativeTo(chartFrame);
                dialog.initForm();
                dialog.setVisible(true);
            }
        };
    }

    public static AbstractAction annotationMenu(final ChartFrame chartFrame) 
    {
        final boolean b = chartFrame.getChartProperties().getToolbarSmallIcons();
        return new AbstractAction("Annotations", 
                b ? ResourcesUtils.getIcon16("line") : ResourcesUtils.getIcon24("line"))
        {
            public void actionPerformed(ActionEvent e)
            {
                List<String> list = AnnotationManager.getDefault().getAnnotations();

                JButton button = (JButton) e.getSource();
                JPopupMenu popup = new JPopupMenu();

                JMenuItem item;

                for (String s : list)
                {
                    item = new JMenuItem(addAnnotation(chartFrame, s));
                    item.setMargin(new Insets(0,0,0,0));
                    popup.add(item);
                }

                popup.addSeparator();

                item = new JMenuItem(removeAllAnnotations(chartFrame));
                item.setMargin(new Insets(0,0,0,0));
                popup.add(item);

                if (chartFrame.hasCurrentAnnotation())
                {
                    Annotation current = chartFrame.getCurrentAnnotation();
                    if (current.isSelected())
                    {
                        popup.addSeparator();

                        item = new JMenuItem(annotationSettings(chartFrame, current));
                        item.setMargin(new Insets(0, 0, 0, 0));
                        popup.add(item);
                    }
                }

                popup.show(button, 0, button.getHeight());
            }
        };
    }

    public static AbstractAction addAnnotation(final ChartFrame chartFrame, final String name)
    {
        return new AbstractAction(name, ResourcesUtils.getIcon16("line"))
        {
            public void actionPerformed(ActionEvent e)
            {
                Annotation a = AnnotationManager.getDefault().getAnnotation(name);
                AnnotationManager.getDefault().setNewAnnotation(a);
            }
        };
    }

    public static AbstractAction removeAllAnnotations(final ChartFrame chartFrame)
    {
        return new AbstractAction("Remove All")
        {
            public void actionPerformed(ActionEvent e)
            {
                chartFrame.removeAllAnnotations();
            }
        };
    }

    public static AbstractAction annotationSettings(final ChartFrame chartFrame, final Annotation a)
    {
        return new AbstractAction("Annotation Settings")
        {
            public void actionPerformed(ActionEvent e)
            {
                AnnotationProperties dialog = new AnnotationProperties(new JFrame(), true);
                dialog.initializeForm(a);
                dialog.setLocationRelativeTo(chartFrame);
                dialog.setVisible(true);
            }
        };
    }

    public static AbstractAction markerAction(final ChartFrame chartFrame) 
    {
        final boolean b = chartFrame.getChartProperties().getToolbarSmallIcons();
        return new AbstractAction("Marker", 
                b ? ResourcesUtils.getIcon16("marker") : ResourcesUtils.getIcon24("marker"))
        {
            public void actionPerformed(ActionEvent e)
            {
                if (e.getSource() instanceof JToggleButton)
                {
                    JToggleButton button = (JToggleButton) e.getSource();
                    boolean enable = button.isSelected();
                    chartFrame.getChartProperties().setMarkerVisibility(enable);
                    if (!enable)
                    {
                        chartFrame.getChartProperties().setMarkerVisibility(enable);
                    }
                    chartFrame.validate();
                    chartFrame.repaint();
                    chartFrame.componentFocused();
                }
            }
        };
    }

    public static AbstractAction exportImage(final ChartFrame chartFrame) 
    {
        final boolean b = chartFrame.getChartProperties().getToolbarSmallIcons();
        return new AbstractAction("Export Image", 
                b ? ResourcesUtils.getIcon16("image") : ResourcesUtils.getIcon24("image"))
        {
            public void actionPerformed(ActionEvent e)
            {
                ImageExporter.getDefault().export(chartFrame);
                chartFrame.componentFocused();
            }
        };
    }

    public static AbstractAction printChart(final ChartFrame chartFrame) 
    {
        final boolean b = chartFrame.getChartProperties().getToolbarSmallIcons();
        AbstractAction action = (AbstractAction) PrintManager.printAction(chartFrame.getMainPanel());
        action.putValue(AbstractAction.NAME, "Print");
        action.putValue(AbstractAction.SMALL_ICON, b ? ResourcesUtils.getIcon16("print") : ResourcesUtils.getIcon24("print"));
        return action;
    }

    public static AbstractAction chartSettings(final ChartFrame chartFrame) 
    {
        final boolean b = chartFrame.getChartProperties().getToolbarSmallIcons();
        return new AbstractAction("Settings", 
                b ? ResourcesUtils.getIcon16("settings") : ResourcesUtils.getIcon24("settings"))
        {
            public void actionPerformed(ActionEvent e)
            {
                ChartSettings dialog = new ChartSettings(new JFrame(), true);
                dialog.setLocationRelativeTo(chartFrame);
                dialog.initializeForm(chartFrame);
                dialog.setVisible(true);
            }
        };
    }

    public static AbstractAction toggleToolbarVisibility(final ChartFrame chartFrame)
    {
        final boolean b = chartFrame.getChartProperties().getToolbarVisibility();
        return new AbstractAction(b ? "Hide Toolbar" : "Show Toolbar")
        {
            public void actionPerformed(ActionEvent e)
            {
                chartFrame.getChartProperties().setToolbarVisibility(!b);
                chartFrame.setToolbarVisibility();
            }
        };
    }

}
