package org.chartsy.main.chartsy;

import java.awt.print.PageFormat;
import org.chartsy.main.chartsy.chart.Annotation;
import org.chartsy.main.dialogs.AddIndicators;
import org.chartsy.main.dialogs.AddOverlays;
import org.chartsy.main.dialogs.AnnotationProperties;
import org.chartsy.main.dialogs.ChartSettings;
import org.chartsy.main.managers.AnnotationManager;
import org.chartsy.main.utils.ImageExporter;
import org.chartsy.main.utils.Printer;

/**
 *
 * @author viorel.gheba
 */
public class MainActions {

    private MainActions() {}

    public static void changeTimeAction(ChartFrame chartFrame, String name) {
        if (!chartFrame.getTime().equals(name)) chartFrame.setTime(name);
    }

    public static void changeChartAction(ChartFrame chartFrame, String name) {
        if (!chartFrame.getChart().getName().equals(name)) chartFrame.setChart(name);
    }

    public static void addIndicator(ChartFrame chartFrame) {
        AddIndicators dialog = new AddIndicators(new javax.swing.JFrame(), true);
        dialog.setChartFrame(chartFrame);
        dialog.initForm();
        dialog.setLocationRelativeTo(chartFrame);
        dialog.setVisible(true);
    }

    public static void addOverlay(ChartFrame chartFrame) {
        AddOverlays dialog = new AddOverlays(new javax.swing.JFrame(), true);
        dialog.setChartFrame(chartFrame);
        dialog.initForm();
        dialog.setLocationRelativeTo(chartFrame);
        dialog.setVisible(true);
    }

    public static void addAnnotation(ChartFrame chartFrame, String name) {
        AnnotationManager.getDefault().setNewAnnotationName(name);
        chartFrame.getChartPanel().setState(ChartPanel.NEWANNOTATION);
    }

    public static void annotationSettings(ChartFrame chartFrame, Annotation a) {
        AnnotationProperties dialog = new AnnotationProperties(new javax.swing.JFrame(), true);
        dialog.setListener(a);
        dialog.initializeForm(chartFrame);
        dialog.setLocationRelativeTo(chartFrame);
        dialog.setVisible(true);
    }

    public static void removeAllAnnotations(ChartFrame chartFrame) {
        chartFrame.getChartPanel().removeAllAnnotations();
    }

    public static void exportImage(ChartFrame chartFrame) {
        ImageExporter.getDefault().export(chartFrame.getChartPanel());
    }

    public static void printChart(ChartFrame chartFrame) {
        PageFormat pf = new PageFormat();
        Printer.print(chartFrame.getChartPanel(), pf);
    }

    public static void chartSettings(ChartFrame chartFrame) {
        ChartSettings dialog = new ChartSettings(new javax.swing.JFrame(), true);
        dialog.initializeForm(chartFrame);
        dialog.setLocationRelativeTo(chartFrame);
        dialog.setVisible(true);
    }

}
