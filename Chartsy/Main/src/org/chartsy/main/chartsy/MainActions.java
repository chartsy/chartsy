package org.chartsy.main.chartsy;

import java.awt.event.ActionEvent;
import java.util.Vector;
import javax.swing.AbstractAction;
import org.chartsy.main.chartsy.chart.Annotation;
import org.chartsy.main.dialogs.AddIndicators;
import org.chartsy.main.dialogs.AddOverlays;
import org.chartsy.main.dialogs.AnnotationProperties;
import org.chartsy.main.dialogs.ChartSettings;
import org.chartsy.main.icons.IconUtils;
import org.chartsy.main.managers.AnnotationManager;
import org.chartsy.main.managers.ChartManager;
import org.chartsy.main.utils.ImageExporter;
import org.netbeans.api.print.PrintManager;

/**
 *
 * @author viorel.gheba
 */
public class MainActions {

    private MainActions() {}

    public static AbstractAction zoomIn(final ChartFrame chartFrame) {
        return new AbstractAction(" + ", IconUtils.getDefault().getIcon24("zoomin")) {
            public void actionPerformed(ActionEvent e) {
                chartFrame.getChartRenderer().zoomIn();
            }
        };
    }

    public static AbstractAction zoomOut(final ChartFrame chartFrame) {
        return new AbstractAction(" - ", IconUtils.getDefault().getIcon24("zoomout")) {
            public void actionPerformed(ActionEvent e) {
                chartFrame.getChartRenderer().zoomOut();
            }
        };
    }

    public static AbstractAction timeMenu(final ChartFrame chartFrame) {
        return new AbstractAction("Time", IconUtils.getDefault().getIcon24("time")) {
            public void actionPerformed(ActionEvent e) {
                Vector list = chartFrame.getUpdater().getTimes();

                javax.swing.JButton button = (javax.swing.JButton)e.getSource();
                javax.swing.JPopupMenu popup = new javax.swing.JPopupMenu();

                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i) != null) {
                        final String name = (String) list.get(i);
                        javax.swing.JMenuItem item = new javax.swing.JMenuItem(changeTime(chartFrame, name));
                        item.setMargin(new java.awt.Insets(0,0,0,0));
                        item.setIcon(IconUtils.getDefault().getIcon16("time"));

                        popup.add(item);
                    } else {
                        popup.addSeparator();
                    }
                }
                popup.show(button, 0, button.getHeight());
            }
        };
    }

    public static AbstractAction changeTime(final ChartFrame chartFrame, final String name) {
        return new AbstractAction(name) {
            public void actionPerformed(ActionEvent e) {
                if (!chartFrame.getTime().equals(name)) chartFrame.setTime(name);
            }
        };
    }

    public static AbstractAction chartMenu(final ChartFrame chartFrame) {
        return new AbstractAction("Chart", IconUtils.getDefault().getIcon24("chart")) {
            public void actionPerformed(ActionEvent e) {
                Vector list = ChartManager.getDefault().getCharts();

                javax.swing.JButton button = (javax.swing.JButton)e.getSource();
                javax.swing.JPopupMenu popup = new javax.swing.JPopupMenu();

                for (int i = 0; i < list.size(); i++) {
                    final String name = (String) list.get(i);
                    javax.swing.JMenuItem item = new javax.swing.JMenuItem(changeChart(chartFrame, name));
                    item.setMargin(new java.awt.Insets(0,0,0,0));
                    popup.add(item);
                }

                popup.show(button, 0, button.getHeight());
            }
        };
    }

    public static AbstractAction changeChart(final ChartFrame chartFrame, final String name) {
        return new AbstractAction(name) {
            public void actionPerformed(ActionEvent e) {
                if (!chartFrame.getChart().getName().equals(name)) chartFrame.setChart(ChartManager.getDefault().getChart(name));
            }
        };
    }

    public static AbstractAction addIndicators(final ChartFrame chartFrame) {
        return new AbstractAction("Add Indicators", IconUtils.getDefault().getIcon24("indicator")) {
            public void actionPerformed(ActionEvent e) {
                AddIndicators dialog = new AddIndicators(new javax.swing.JFrame(), true);
                dialog.setChartFrame(chartFrame);
                dialog.initForm();
                dialog.setLocationRelativeTo(chartFrame);
                dialog.setVisible(true);
            }
        };
    }

    public static AbstractAction addOverlays(final ChartFrame chartFrame) {
        return new AbstractAction("Add Overlays", IconUtils.getDefault().getIcon24("overlay")) {
            public void actionPerformed(ActionEvent e) {
                AddOverlays dialog = new AddOverlays(new javax.swing.JFrame(), true);
                dialog.setChartFrame(chartFrame);
                dialog.initForm();
                dialog.setLocationRelativeTo(chartFrame);
                dialog.setVisible(true);
            }
        };
    }

    public static AbstractAction annotationMenu(final ChartFrame chartFrame) {
        return new AbstractAction("Annotations", IconUtils.getDefault().getIcon24("line")) {
            public void actionPerformed(ActionEvent e) {
                Vector list = AnnotationManager.getDefault().getAnnotations();

                javax.swing.JButton button = (javax.swing.JButton)e.getSource();
                javax.swing.JPopupMenu popup = new javax.swing.JPopupMenu();

                javax.swing.JMenuItem item;

                for (int i = 0; i < list.size(); i++) {
                    final String name = (String) list.get(i);
                    item = new javax.swing.JMenuItem(addAnnotation(chartFrame, name));
                    item.setMargin(new java.awt.Insets(0,0,0,0));
                    popup.add(item);
                }

                popup.addSeparator();

                item = new javax.swing.JMenuItem(removeAllAnnotations(chartFrame));
                item.setMargin(new java.awt.Insets(0, 0, 0, 0));
                popup.add(item);

                if (chartFrame.getChartPanel().getCurrent() != null && chartFrame.getChartPanel().getCurrent().isSelected()) {
                    popup.addSeparator();

                    item = new javax.swing.JMenuItem(annotationSettings(chartFrame, chartFrame.getChartPanel().getCurrent()));
                    item.setMargin(new java.awt.Insets(0, 0, 0, 0));
                    popup.add(item);
                }

                popup.show(button, 0, button.getHeight());
            }
        };
    }

    public static AbstractAction addAnnotation(final ChartFrame chartFrame, final String name) {
        return new AbstractAction(name) {
            public void actionPerformed(ActionEvent e) {
                AnnotationManager.getDefault().setNewAnnotationName(name);
                chartFrame.getChartPanel().setState(ChartPanel.NEWANNOTATION);
            }
        };
    }

    public static AbstractAction removeAllAnnotations(final ChartFrame chartFrame) {
        return new AbstractAction("Remove All") {
            public void actionPerformed(ActionEvent e) {
                chartFrame.getChartPanel().removeAllAnnotations();
            }
        };
    }

    public static AbstractAction annotationSettings(final ChartFrame chartFrame, final Annotation a) {
        return new AbstractAction("Annotation Settings") {
            public void actionPerformed(ActionEvent e) {
                AnnotationProperties dialog = new AnnotationProperties(new javax.swing.JFrame(), true);
                dialog.setListener(a);
                dialog.initializeForm(chartFrame);
                dialog.setLocationRelativeTo(chartFrame);
                dialog.setVisible(true);
            }
        };
    }

    public static AbstractAction markerAction(final ChartFrame chartFrame) {
        return new AbstractAction("Marker", IconUtils.getDefault().getIcon24("marker")) {
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() instanceof javax.swing.JToggleButton) {
                    javax.swing.JToggleButton button = (javax.swing.JToggleButton) e.getSource();
                    chartFrame.getChartProperties().setMarkerVisibility(button.isSelected());
                    if (!button.isSelected())
                        chartFrame.getMarker().setIndex(-1);
                    chartFrame.getChartPanel().repaint();
                }
            }
        };
    }

    public static AbstractAction exportImage(final ChartFrame chartFrame) {
        return new AbstractAction("Export Image", IconUtils.getDefault().getIcon24("image")) {
            public void actionPerformed(ActionEvent e) {
                ImageExporter.getDefault().export(chartFrame.getChartPanel());
            }
        };
    }

    public static AbstractAction printChart(final ChartFrame chartFrame) {
        AbstractAction action = (AbstractAction) PrintManager.printAction(chartFrame.getChartPanel());
        action.putValue(AbstractAction.NAME, "Print");
        action.putValue(AbstractAction.SMALL_ICON, IconUtils.getDefault().getIcon24("print"));
        return action;
    }

    public static AbstractAction chartSettings(final ChartFrame chartFrame) {
        return new AbstractAction("Settings", IconUtils.getDefault().getIcon24("settings")) {
            public void actionPerformed(ActionEvent e) {
                ChartSettings dialog = new ChartSettings(new javax.swing.JFrame(), true);
                dialog.initializeForm(chartFrame);
                dialog.setLocationRelativeTo(chartFrame);
                dialog.setVisible(true);
            }
        };
    }

}
