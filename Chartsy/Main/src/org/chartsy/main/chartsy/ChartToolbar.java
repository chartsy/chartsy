package org.chartsy.main.chartsy;

import java.awt.Font;
import java.awt.Insets;
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
public class ChartToolbar extends JToolBar {

    private ChartFrame parent;
    private Font font;

    public static ChartToolbar newInstance(ChartFrame parent) { return new ChartToolbar(parent); }

    private ChartToolbar(ChartFrame parent) {
        super("ChartToolbar", JToolBar.HORIZONTAL);
        this.parent = parent;
        Font f = this.parent.getChartProperties().getFont();
        font = new Font(f.getName(), f.getStyle(), 10);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setFloatable(false);
        initComponents();
        setFloatable(false);
    }

    private void initComponents() {
        AbstractButton button;

        // Zoom in button
        button = new JButton(MainActions.zoomIn(parent));
        setButtonSettings(button, "Zoom In");
        add(button);

        // Zoom out button
        button = new JButton(MainActions.zoomOut(parent));
        setButtonSettings(button, "Zoom Out");
        add(button);

        // Chart time type
        button = new JButton(MainActions.timeMenu(parent));
        setButtonSettings(button, "Select Time");
        add(button);

        // Chart type button
        button = new JButton(MainActions.chartMenu(parent));
        setButtonSettings(button, "Select Chart");
        add(button);

        // Indicator button
        button = new JButton(MainActions.addIndicators(parent));
        setButtonSettings(button, "Add Indicators");
        add(button);

        // Overlay button
        button = new JButton(MainActions.addOverlays(parent));
        setButtonSettings(button, "Add Overlays");
        add(button);

        // Line button
        button = new JButton(MainActions.annotationMenu(parent));
        setButtonSettings(button, "Annotations");
        add(button);

        // Value marker visibility button
        button = new JToggleButton(MainActions.markerAction(parent));
        setButtonSettings(button, "Marker");
        button.setSelected(true);
        add(button);

        // Export image button
        button = new JButton(MainActions.exportImage(parent));
        setButtonSettings(button, "Export Image");
        add(button);

        // Print button
        button = new JButton(MainActions.printChart(parent));
        setButtonSettings(button, "Print");
        add(button);

        // Settings button
        button = new JButton(MainActions.chartSettings(parent));
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
