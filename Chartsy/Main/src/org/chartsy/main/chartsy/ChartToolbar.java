package org.chartsy.main.chartsy;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.util.Vector;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import org.chartsy.main.dialogs.AddIndicators;
import org.chartsy.main.dialogs.AddOverlays;
import org.chartsy.main.dialogs.AnnotationProperties;
import org.chartsy.main.dialogs.ChartSettings;
import org.chartsy.main.icons.IconUtils;
import org.chartsy.main.managers.AnnotationManager;
import org.chartsy.main.managers.ChartManager;
import org.chartsy.main.managers.UpdaterManager;
import org.chartsy.main.utils.ImageExporter;
import org.chartsy.main.utils.Printer;

/**
 *
 * @author viorel.gheba
 */
public class ChartToolbar extends JToolBar implements ActionListener {

    private ChartFrame parent;

    public static ChartToolbar newInstance(ChartFrame parent) { return new ChartToolbar(parent); }

    private ChartToolbar(ChartFrame parent) {
        super("ChartToolbar", JToolBar.HORIZONTAL);
        this.parent = parent;
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setFloatable(false);
        initComponents();
    }

    private void initComponents() {
        AbstractButton button = null;

        // Zoom in button
        button = this.getToolbarButton("zoomin", "ZOOMIN", " + ", "Zoom in");
        this.add(button);

        // Zoom out button
        button = this.getToolbarButton("zoomout", "ZOOMOUT", " - ", "Zoom out");
        this.add(button);

        // Chart time type
        button = this.getToolbarButton("time", "TIMEACTION", "Time", "Time");
        this.add(button);

        // Chart type button
        button = this.getToolbarButton("chart", "CHARTACTION", "Chart", "Chart Type");
        this.add(button);

        // Indicator button
        button = this.getToolbarButton("indicator", "ADDINDICATOR", "Add Indicator", "Add Indicator");
        this.add(button);

        // Overlay button
        button = this.getToolbarButton("overlay", "ADDOVERLAY", "Add Overlay", "Add Overlay");
        this.add(button);

        // Line button
        button = this.getToolbarButton("line", "DRAWINGS", "Draw Line", "Draw Line");
        this.add(button);

        // Value marker visibility button
        button = this.getToolbarToggleButton("marker", "MARKER", "Marker", "Marker", true);
        this.add(button);

        // Export image button
        button = this.getToolbarButton("image", "EXPORTIMAGE", "Export Image", "Export Image");
        this.add(button);

        // Print button
        button = this.getToolbarButton("print", "PRINT", "Print", "Print");
        this.add(button);

        // Settings button
        button = this.getToolbarButton("settings", "SETTINGS", "Settings", "Settings");
        this.add(button);
    }

    private JButton getToolbarButton(final String name, final String action, final String alt, final String tooltip) {
        JButton button = new JButton();

        button.setMargin(new Insets(6,6,6,6));
        button.setBorderPainted(false);
        button.setActionCommand(action);
        button.setToolTipText(tooltip);
        button.addActionListener(this);
        if (IconUtils.getIcon(name) != null) {
            button.setIcon(IconUtils.getIcon(name, tooltip, IconUtils.PNG));
            button.setPreferredSize(new Dimension(24, 24));
        } else {
            button.setText(alt);
        }

        return button;
    }

    private JToggleButton getToolbarToggleButton(final String name, final String action, final String alt, final String tooltip, final boolean def) {
        JToggleButton button = new JToggleButton();

        button.setMargin(new Insets(6,6,6,6));
        button.setBorderPainted(false);
        button.setActionCommand(action);
        button.setToolTipText(tooltip);
        button.addActionListener(this);
        if (IconUtils.getIcon(name) != null) {
            button.setIcon(IconUtils.getIcon(name, tooltip, IconUtils.PNG));
            button.setPreferredSize(new Dimension(24, 24));
        } else {
            button.setText(alt);
        }
        button.setSelected(def);

        return button;
    }

    public void actionPerformed(ActionEvent e) {
        final String actionCommand = e.getActionCommand();
        if (actionCommand.equals("CHARTACTION")) {
            Vector list = ChartManager.getDefault().getCharts();

            JButton button = (JButton)e.getSource();
            JPopupMenu popup = new JPopupMenu();

            for (int i = 0; i < list.size(); i++) {
                final String name = (String) list.get(i);
                JMenuItem item = new JMenuItem(name);
                item.setMargin(new Insets(0,0,0,0));
                item.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        changeChart(name);
                    }
                });

                String img = name.replace(" ", "").toLowerCase();
                //if (IconUtils.getChartIcon(img) != null) item.setIcon(IconUtils.getChartIcon(img));
                popup.add(item);
            }

            popup.show(button, 0, button.getHeight());
        } else if (actionCommand.equals("ADDINDICATOR")) {
            AddIndicators dialog = new AddIndicators(new javax.swing.JFrame(), true);
            dialog.setChartFrame(parent);
            dialog.initForm();
            dialog.setLocationRelativeTo(parent);
            dialog.setVisible(true);
        } else if (actionCommand.equals("ADDOVERLAY")) {
            AddOverlays dialog = new AddOverlays(new javax.swing.JFrame(), true);
            dialog.setChartFrame(parent);
            dialog.initForm();
            dialog.setLocationRelativeTo(parent);
            dialog.setVisible(true);
        } else if (actionCommand.equals("DRAWINGS")) {
            Vector list = AnnotationManager.getDefault().getAnnotations();

            JButton button = (JButton)e.getSource();
            JPopupMenu popup = new JPopupMenu();

            for (int i = 0; i < list.size(); i++) {
                final String name = (String) list.get(i);
                JMenuItem item = new JMenuItem(name);
                item.setMargin(new Insets(0,0,0,0));
                item.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        AnnotationManager.getDefault().setNewAnnotationName(name);
                        parent.getChartPanel().setState(ChartPanel.NEWANNOTATION);
                    }
                });

                String img = "annotations_" + name.replace(" ", "").toLowerCase();
                //if (IconUtils.getChartIcon(img) != null) item.setIcon(IconUtils.getChartIcon(img));
                popup.add(item);
            }

            popup.addSeparator();

            JMenuItem item1 = new JMenuItem("Remove All");
            item1.setMargin(new Insets(0, 0, 0, 0));
            item1.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    parent.getChartPanel().removeAllAnnotations();
                }
            });
            popup.add(item1);

            popup.addSeparator();

            JMenuItem item2 = new JMenuItem("Annotation Properties");
            item2.setMargin(new Insets(0, 0, 0, 0));
            item2.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    AnnotationProperties dialog = new AnnotationProperties(new javax.swing.JFrame(), true);
                    dialog.initializeForm(parent);
                    dialog.setLocationRelativeTo(parent);
                    dialog.setVisible(true);
                }
            });
            popup.add(item2);

            popup.show(button, 0, button.getHeight());
        } else if (actionCommand.equals("TIMEACTION")) {
            Vector list = UpdaterManager.getDefault().getActiveUpdater().getTimes();

            JButton button = (JButton)e.getSource();
            JPopupMenu popup = new JPopupMenu();

            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) != null) {
                    final String name = (String) list.get(i);
                    JMenuItem item = new JMenuItem(name);
                    item.setMargin(new Insets(0,0,0,0));
                    item.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            changeTime(name);
                        }
                    });

                    popup.add(item);
                } else {
                    popup.addSeparator();
                }
            }
            popup.show(button, 0, button.getHeight());
        } else if (actionCommand.equals("ZOOMIN")) {
            parent.getChartRenderer().zoomIn();
        } else if (actionCommand.equals("ZOOMOUT")) {
            parent.getChartRenderer().zoomOut();
        } else if (actionCommand.equals("MARKER")) {
            if (e.getSource() instanceof JToggleButton) {
                JToggleButton button = (JToggleButton) e.getSource();
                parent.getChartProperties().setMarkerVisibility(button.isSelected());
                if (!button.isSelected()) parent.getMarker().setIndex(-1);
                parent.getChartPanel().repaint();
            }
        } else if (actionCommand.equals("EXPORTIMAGE")) {
            ImageExporter.getDefault().export(parent.getChartPanel());
        } else if (actionCommand.equals("PRINT")) {
            PageFormat pf = new PageFormat();
            Printer.print(parent.getChartPanel(), pf);
        } else if (actionCommand.equals("SETTINGS")) {
            ChartSettings dialog = new ChartSettings(new javax.swing.JFrame(), true);
            dialog.initializeForm(parent);
            dialog.setLocationRelativeTo(parent);
            dialog.setVisible(true);
        }
    }

    public void changeChart(String chart) { if (!parent.getChart().getName().equals(chart)) parent.setChart(chart); }
    public void changeTime(String time) { if (!parent.getTime().equals(time)) parent.setTime(time); }

}
