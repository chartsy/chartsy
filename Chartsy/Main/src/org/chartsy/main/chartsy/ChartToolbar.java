package org.chartsy.main.chartsy;

import java.awt.Font;
import java.awt.Insets;
import java.io.Serializable;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

/**
 *
 * @author viorel.gheba
 */
public class ChartToolbar extends JToolBar implements Serializable {

    private static final long serialVersionUID = 101L;

    private ChartFrame chartFrame;
    private Font font;

    public static ChartToolbar newInstance(ChartFrame chartFrame) { return new ChartToolbar(chartFrame); }

    private ChartToolbar(ChartFrame chartFrame) {
        super("ChartToolbar", JToolBar.HORIZONTAL);
        this.chartFrame = chartFrame;
        Font f = chartFrame.getChartProperties().getFont();
        font = new Font(f.getName(), f.getStyle(), 10);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setFloatable(false);
        initComponents();
        setFloatable(false);
    }

    private void initComponents() {
        AbstractButton button;

        // Zoom in button
        button = new JButton(MainActions.zoomIn(chartFrame));
        setButtonSettings(button, "Zoom In");
        add(button);

        // Zoom out button
        button = new JButton(MainActions.zoomOut(chartFrame));
        setButtonSettings(button, "Zoom Out");
        add(button);

        // Chart time type
        button = new JButton(MainActions.timeMenu(chartFrame));
        setButtonSettings(button, "Select Time");
        add(button);

        // Chart type button
        button = new JButton(MainActions.chartMenu(chartFrame));
        setButtonSettings(button, "Select Chart");
        add(button);

        // Indicator button
        button = new JButton(MainActions.addIndicators(chartFrame));
        setButtonSettings(button, "Add Indicators");
        add(button);

        // Overlay button
        button = new JButton(MainActions.addOverlays(chartFrame));
        setButtonSettings(button, "Add Overlays");
        add(button);

        // Line button
        button = new JButton(MainActions.annotationMenu(chartFrame));
        setButtonSettings(button, "Annotations");
        add(button);

        // Value marker visibility button
        button = new JToggleButton(MainActions.markerAction(chartFrame));
        setButtonSettings(button, "Marker");
        button.setSelected(true);
        add(button);

        // Export image button
        button = new JButton(MainActions.exportImage(chartFrame));
        setButtonSettings(button, "Export Image");
        add(button);

        // Print button
        button = new JButton(MainActions.printChart(chartFrame));
        setButtonSettings(button, "Print");
        add(button);

        // Settings button
        button = new JButton(MainActions.chartSettings(chartFrame));
        setButtonSettings(button, "Chart Settings");
        add(button);
    }

    private void setButtonSettings(AbstractButton button, String tooltip) {
        button.setFont(font);
        button.setVerticalAlignment(SwingConstants.TOP);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setMargin(new Insets(6,6,6,6));
        button.setBorderPainted(false);
        button.setToolTipText(tooltip);
    }

}
