package org.chartsy.main;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import org.chartsy.main.chart.Indicator;
import org.chartsy.main.resources.ResourcesUtils;

/**
 *
 * @author Administrator
 */
public class IndicatorPanel extends AbstractComponent implements Serializable {

    private static final long serialVersionUID = 2L;

    private ChartFrame chartFrame;
    private AnnotationPanel annotationPanel;
    private JPanel top;
    private JLabel label;
    private Indicator indicator = null;

    private transient Toolbox toolbox;

    public IndicatorPanel(ChartFrame frame, Indicator i) {
        chartFrame = frame;
        indicator = i;

        annotationPanel = new AnnotationPanel(chartFrame);
        top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BorderLayout());
        top.setLocation(0, 0);

        label = new JLabel(" ");
        label.setOpaque(false);
        label.setHorizontalTextPosition(SwingConstants.LEFT);
        label.setVerticalTextPosition(SwingConstants.CENTER);
        label.setFont(chartFrame.getChartProperties().getFont());
        toolbox = new Toolbox(this);

        top.add(label, BorderLayout.WEST);
        top.add(toolbox, BorderLayout.EAST);

        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
        setLayout(new LayoutManager() {
            public void addLayoutComponent(String name, Component comp)
            {}
            public void removeLayoutComponent(Component comp)
            {}
            public Dimension preferredLayoutSize(Container parent)
            {return new Dimension(0, 0);}
            public Dimension minimumLayoutSize(Container parent)
            {return new Dimension(0, 0);}
            public void layoutContainer(Container parent)
            {
                int width = parent.getWidth();
                int height = parent.getHeight();
                top.setBounds(0, 0, width, 24);
                annotationPanel.setBounds(0, 2, width - 4, height - 4);
            }
        });

        add(top);
        add(annotationPanel);
    }

    public ChartFrame getChartFrame() 
    { return chartFrame; }

    public void setChartFrame(ChartFrame frame)
    { chartFrame = frame; }

    public AnnotationPanel getAnnotationPanel() 
    { return annotationPanel; }

    public void setAnnotationPanel(AnnotationPanel panel)
    { annotationPanel = panel; }

    public void setLabel(String text)
    { label.setText(text); }

    public Indicator getIndicator()
    { return indicator; }
    
    public void setIndicator(Indicator ind) 
    { indicator = ind; }

    public boolean isMaximized() {
        if (indicator != null)
            return indicator.isMaximized();
        return true;
    }

    public void setMaximized(boolean b) {
        if (indicator.isMaximized() != b) {
            indicator.setMaximized(b);
            if (toolbox != null)
                toolbox.update();
            annotationPanel.setVisible(b);
            chartFrame.validate();
            chartFrame.repaint();
        }
    }

    public void toggleVisible() 
    { setMaximized(!isMaximized()); }

    public Toolbox getToolbox() 
    { return toolbox; }

    public void updateToolbox() {
        toolbox.update();
    }

    public void setMaximizedHeight(int height)
    { this.indicator.setMaximizedHeight(height); }

    public int getPanelHeight() {
        if (isMaximized()) {
            return indicator.getMaximizedHeight();
        } else {
            return top.getHeight();
        }
    }

    private AbstractAction moveUp(final ChartFrame frame, final IndicatorPanel panel) {
        return new AbstractAction("Move Indicator Up", ResourcesUtils.getIcon("up")) {
            public void actionPerformed(ActionEvent e) {
                frame.getSplitPanel().getIndicatorsPanel().moveUp(panel);
            }
        };
    }

    private AbstractAction moveDown(final ChartFrame frame, final IndicatorPanel panel) {
        return new AbstractAction("Move Indicator Down", ResourcesUtils.getIcon("down")) {
            public void actionPerformed(ActionEvent e) {
                frame.getSplitPanel().getIndicatorsPanel().moveDown(panel);
            }
        };
    }

    private AbstractAction minimize(final IndicatorPanel panel) {
        return new AbstractAction("Minimize Indicator", ResourcesUtils.getIcon("minimize")) {
            public void actionPerformed(ActionEvent e) {
                panel.setMaximized(false);
            }
        };
    }

    private AbstractAction maximize(final IndicatorPanel panel) {
        return new AbstractAction("Minimize Indicator", ResourcesUtils.getIcon("maximize")) {
            public void actionPerformed(ActionEvent e) {
                panel.setMaximized(true);
            }
        };
    }

    private AbstractAction removeAction(final ChartFrame frame, final IndicatorPanel panel) {
        return new AbstractAction("Minimize Indicator", ResourcesUtils.getIcon("remove")) {
            public void actionPerformed(ActionEvent e) {
                frame.getSplitPanel().getIndicatorsPanel().remove(panel);
            }
        };
    }

    protected void paintAbstractComponent(Graphics g)
    {
        int width = getWidth();

        Graphics2D g2 = (Graphics2D) g.create();
        setDoubleBuffered(true);

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

        g2.setPaintMode();

        ChartProperties cp = chartFrame.getChartProperties();
        
        g2.setPaint(cp.getAxisColor());
        g2.setStroke(cp.getAxisStroke());
        g2.drawLine(0, 0, width, 0);

        Rectangle rect = getBounds();
        rect.grow(-2, -2);
        Rectangle old = g2.getClipBounds();
        g2.setClip(rect);

        if (indicator != null)
        {
            indicator.paint(g2, chartFrame, rect);
            setLabel(indicator.getPaintedLabel(chartFrame));
        }

        g2.setClip(old);
        g2.dispose();
    }

    @Override
    public Rectangle getBounds()
    { return new Rectangle(0, 0, getWidth(), getPanelHeight()); }

    public class Toolbox extends JComponent implements Serializable
    {

        private static final long serialVersionUID = 2L;
        private transient IndicatorPanel indicatorPanel;
        private transient JPanel container;

        public Toolbox(IndicatorPanel panel)
        {
            indicatorPanel = panel;
            setOpaque(false);
            setLayout(new BorderLayout());
            container = new JPanel();
            container.setOpaque(false);
            container.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 5));
            add(container, BorderLayout.CENTER);
            update();
        }

        public void update()
        {
            setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            container.removeAll();

            int count = chartFrame.getSplitPanel().getIndicatorsPanel().getIndicatorsCount();
            ToolboxButton button;
            if (count > 1)
            {
                // Move Up
                button = new ToolboxButton(moveUp(chartFrame, indicatorPanel));
                button.setText("");
                button.setToolTipText("Move Up");
                container.add(button);
                // Move Down
                button = new ToolboxButton(moveDown(chartFrame, indicatorPanel));
                button.setText("");
                button.setToolTipText("Move Down");
                container.add(button);
            }
            // Toggle Maximize/Minimize
            button = new ToolboxButton(isMaximized() ? minimize(indicatorPanel) : maximize(indicatorPanel));
            button.setText("");
            button.setToolTipText(isMaximized() ? "Minimize" : "Maximize");
            container.add(button);
            // Remove
            button = new ToolboxButton(removeAction(chartFrame, indicatorPanel));
            button.setText("");
            button.setToolTipText("Remove");
            container.add(button);

            validate();
            repaint();
        }

        public class ToolboxButton extends JButton implements Serializable
        {

            private static final long serialVersionUID = 2L;

            public ToolboxButton(Action action)
            {
                super(action);
                setOpaque(false);
                setFocusPainted(false);
                setBorderPainted(false);
                setMargin(new Insets(0, 0, 0, 0));
                setBorder(new Border()
                {
                    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
                    {}
                    public Insets getBorderInsets(Component c)
                    {
                        return new Insets(0, 0, 0, 0);
                    }
                    public boolean isBorderOpaque()
                    {
                        return true;
                    }
                });
                addMouseListener(new MouseAdapter()
                {
                    public @Override void mouseEntered(MouseEvent e)
                    { setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)); }
                });
            }

        }

    }

}