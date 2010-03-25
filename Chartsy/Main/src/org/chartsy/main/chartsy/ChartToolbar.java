package org.chartsy.main.chartsy;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.io.Serializable;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.AbstractBorder;

/**
 *
 * @author viorel.gheba
 */
public class ChartToolbar extends JToolBar implements Serializable {

    private static final long serialVersionUID = 101L;

    
    private ChartFrame chartFrame;
    private Font font;

    public ChartToolbar(ChartFrame chartFrame) {
        super("ChartToolbar", JToolBar.HORIZONTAL);
        this.chartFrame = chartFrame;
        Font f = chartFrame.getChartProperties().getFont();
        font = new Font(f.getName(), f.getStyle(), 10);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setFloatable(false);
        initComponents();
        setFloatable(false);
        setBorder(new BottomBorder());
    }

    private void initComponents() {
        AbstractButton button;

        // Stock Changer Panel
        StockChanger panel = new StockChanger();
        panel.setListener(chartFrame);
        panel.setStock(chartFrame.getStock().getKey());
        add(panel);
        

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

    class BottomBorder extends AbstractBorder implements Serializable {

        private static final long serialVersionUID = 101L;
        
        protected Color color = new Color(0x898c95);
        protected int thickness = 1;
        protected int gap = 1;

        public BottomBorder() {}

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Color old = g.getColor();

            g.setColor(color);
            for (int i = 0; i < thickness; i++) {
                g.drawLine(x, y + height - i - 1, x + width, y + height - i - 1);
            }

            g.setColor(old);
        }

        public Insets getBorderInsets(Component c) { return new Insets(0, 0, gap, 0); }

        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = 0;
            insets.top = 0;
            insets.right = 0;
            insets.bottom = gap;
            return insets;
        }

        public boolean isBorderOpaque() {
            return false;
        }

    }

}
