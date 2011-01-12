package org.chartsy.main;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.Serializable;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import org.chartsy.main.chart.Indicator;
import org.chartsy.main.dialogs.SettingsPanel;
import org.chartsy.main.resources.ResourcesUtils;
import org.chartsy.main.utils.ColorGenerator;
import org.chartsy.main.utils.GraphicsUtils;
import org.chartsy.main.utils.SerialVersion;

/**
 *
 * @author Administrator
 */
public class IndicatorPanel extends JPanel implements Serializable
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    private ChartFrame chartFrame;
    private AnnotationPanel annotationPanel;
    private IndicatorToolbox toolbox;
    private Indicator indicator = null;

    public IndicatorPanel(ChartFrame frame, Indicator indicator)
    {
        this.chartFrame = frame;
        this.indicator = indicator;
        initializeUIElements();
    }

    private void initializeUIElements()
    {
        annotationPanel = new AnnotationPanel(chartFrame);
        toolbox = new IndicatorToolbox();
        toolbox.setLocation(0, 0);

        setOpaque(false);
		setDoubleBuffered(true);
        setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
        setLayout(new LayoutManager() {
			@Override
            public void addLayoutComponent(String name, Component comp)
            {}
			@Override
            public void removeLayoutComponent(Component comp)
            {}
			@Override
            public Dimension preferredLayoutSize(Container parent)
            {return new Dimension(0, 0);}
			@Override
            public Dimension minimumLayoutSize(Container parent)
            {return new Dimension(0, 0);}
			@Override
            public void layoutContainer(Container parent)
            {
                int width = parent.getWidth();
                int height = parent.getHeight();
                int toolboxWidth = toolbox.getWidth();
                int toolboxHeight = toolbox.getHeight();
                
                annotationPanel.setBounds(0, 2, width - 4, height - 4);
                toolbox.setBounds(0, 0, toolboxWidth, toolboxHeight);
            }
        });

        add(toolbox);
        add(annotationPanel);
        doLayout();
    }

    public ChartFrame getChartFrame() 
    {
        return chartFrame;
    }

    public void setChartFrame(ChartFrame frame)
    {
        chartFrame = frame;
    }

    public AnnotationPanel getAnnotationPanel() 
    {
        return annotationPanel;
    }

    public void setAnnotationPanel(AnnotationPanel panel)
    {
        annotationPanel = panel;
    }

    public Indicator getIndicator()
    {
        return indicator;
    }
    
    public void setIndicator(Indicator ind) 
    {
        indicator = ind;
    }

    public boolean isMaximized()
    {
        if (indicator != null)
            return indicator.isMaximized();
        return true;
    }

    public void setMaximized(boolean b)
    {
        if (indicator.isMaximized() != b)
        {
            indicator.setMaximized(b);
            if (toolbox != null)
                toolbox.update();
            annotationPanel.setVisible(b);
            chartFrame.validate();
            chartFrame.repaint();
        }
    }

    public void toggleVisible() 
    { 
        setMaximized(!isMaximized());
    }

    public IndicatorToolbox getToolbox()
    { 
        return toolbox;
    }

    public void updateToolbox() 
    { 
        toolbox.update();
    }

    public void setMaximizedHeight(int height)
    {
        this.indicator.setMaximizedHeight(height);
    }

    public int getPanelHeight()
    {
        if (isMaximized())
        {
            return indicator.getMaximizedHeight();
        }
        else
        {
            return toolbox.getHeight();
        }
    }

    private AbstractAction indicatorSettings(final ChartFrame frame, final IndicatorPanel panel)
    {
        return new AbstractAction("Indicator Settings", ResourcesUtils.getIcon("settings"))
        {
			@Override
            public void actionPerformed(ActionEvent e)
            {
				SettingsPanel.getDefault().openSettingsWindow(panel.getIndicator());
            }
        };
    }

    private AbstractAction moveUp(final ChartFrame frame, final IndicatorPanel panel)
    {
        return new AbstractAction("Move Indicator Up", ResourcesUtils.getIcon("up"))
        {
			@Override
            public void actionPerformed(ActionEvent e)
            {
                frame.getSplitPanel().getIndicatorsPanel().moveUp(panel);
            }
        };
    }

    private AbstractAction moveDown(final ChartFrame frame, final IndicatorPanel panel)
    {
        return new AbstractAction("Move Indicator Down", ResourcesUtils.getIcon("down"))
        {
			@Override
            public void actionPerformed(ActionEvent e)
            {
                frame.getSplitPanel().getIndicatorsPanel().moveDown(panel);
            }
        };
    }

    private AbstractAction minimize(final IndicatorPanel panel)
    {
        return new AbstractAction("Minimize Indicator", ResourcesUtils.getIcon("minimize"))
        {
			@Override
            public void actionPerformed(ActionEvent e)
            {
                panel.setMaximized(false);
            }
        };
    }

    private AbstractAction maximize(final IndicatorPanel panel)
    {
        return new AbstractAction("Minimize Indicator", ResourcesUtils.getIcon("maximize"))
        {
			@Override
            public void actionPerformed(ActionEvent e)
            {
                panel.setMaximized(true);
            }
        };
    }

    private AbstractAction removeAction(final ChartFrame frame, final IndicatorPanel panel)
    {
        return new AbstractAction("Remove Indicator", ResourcesUtils.getIcon("remove"))
        {
			@Override
            public void actionPerformed(ActionEvent e)
            {
                frame.indicatorRemoved(indicator);
            }
        };
    }

	@Override
    public void paint(Graphics g)
    {
        int width = getWidth();
        Graphics2D g2 = GraphicsUtils.prepareGraphics(g);
        ChartProperties cp = chartFrame.getChartProperties();
        
        g2.setColor(cp.getAxisColor());
        g2.setStroke(cp.getAxisStroke());
        g2.drawLine(0, 0, width, 0);

        Rectangle rect = this.getBounds();
        rect.grow(-2, -2);

        if (indicator != null)
		{
			boolean isLog = cp.getAxisLogarithmicFlag();
			if ( isLog ) cp.setAxisLogarithmicFlag(false);
            indicator.paint(g2, chartFrame, rect);
			if ( isLog ) cp.setAxisLogarithmicFlag(true);
		}

        super.paint(g);
    }

	@Override
	public void update(Graphics g)
	{
		paint(g);
	}

	@Override
    public Rectangle getBounds()
    {
        return new Rectangle(0, 0, getWidth(), getPanelHeight());
    }

    public final class IndicatorToolbox extends JToolBar implements Serializable
    {

        private static final long serialVersionUID = SerialVersion.APPVERSION;
        private JLabel indicatorLabel;
        private JComponent container;
        public boolean mouseOver = false;
        private final Color backColor = ColorGenerator.getTransparentColor(new Color(0x1C2331), 50);

        public IndicatorToolbox()
        {
            super(JToolBar.HORIZONTAL);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

            indicatorLabel = new JLabel(indicator.getLabel());
            indicatorLabel.setHorizontalTextPosition(SwingConstants.LEFT);
            indicatorLabel.setVerticalTextPosition(SwingConstants.CENTER);
            indicatorLabel.setOpaque(false);
            add(indicatorLabel);

            container = new JPanel();
            container.setOpaque(false);
            container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
            add(container);
            update();
            
            addMouseListener(new MouseAdapter()
            {
                public @Override void mouseEntered(MouseEvent e)
                {
                    mouseOver = true;
                    validate();
                    repaint();
                }
                public @Override void mouseExited(MouseEvent e)
                {
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    mouseOver = false;
                    validate();
                    repaint();
                }
            });
        }

        public @Override int getWidth()
        {
            return this.getLayout().preferredLayoutSize(this).width;
        }

        public @Override int getHeight()
        {
            return this.getLayout().preferredLayoutSize(this).height;
        }

        public void update()
        {
            // remove all buttons
            container.removeAll();

            // number of indicators
            int count = IndicatorPanel.this.chartFrame.getSplitPanel().getIndicatorsPanel().getIndicatorsCount();

            ToolboxButton button;

            // Settings
            container.add(button = new ToolboxButton(indicatorSettings(IndicatorPanel.this.chartFrame, IndicatorPanel.this)));
            button.setText("");
            button.setToolTipText("Settings");

            if (count > 1)
            {
                // Move Up
                container.add(button = new ToolboxButton(moveUp(IndicatorPanel.this.chartFrame, IndicatorPanel.this)));
                button.setText("");
                button.setToolTipText("Move Up");

                // Move Down
                container.add(button = new ToolboxButton(moveDown(IndicatorPanel.this.chartFrame, IndicatorPanel.this)));
                button.setText("");
                button.setToolTipText("Move Down");
            }

            // Toggle Maximize/Minimize
            container.add(button = new ToolboxButton(isMaximized() ? minimize(IndicatorPanel.this) : maximize(IndicatorPanel.this)));
            button.setText("");
            button.setToolTipText(isMaximized() ? "Minimize" : "Maximize");
            
            // Remove
            container.add(button = new ToolboxButton(removeAction(IndicatorPanel.this.chartFrame, IndicatorPanel.this)));
            button.setText("");
            button.setToolTipText("Remove");

            revalidate();
            repaint();
        }

        public @Override void paint(Graphics g)
        {
			if (!indicatorLabel.getFont().equals(chartFrame.getChartProperties().getFont()))
				indicatorLabel.setFont(chartFrame.getChartProperties().getFont());
			if (!indicatorLabel.getForeground().equals(chartFrame.getChartProperties().getFontColor()))
				indicatorLabel.setForeground(chartFrame.getChartProperties().getFontColor());
			if (!indicatorLabel.getText().equals(indicator.getLabel()))
				indicatorLabel.setText(indicator.getLabel());
            
            Rectangle oldClip = g.getClipBounds();
            Rectangle newClip = getBounds();
            g.setClip(newClip);

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

            g2.setPaintMode();

            if (mouseOver)
            {
                g2.setColor(backColor);
                RoundRectangle2D roundRectangle = new RoundRectangle2D.Double(getX(), getY(), getWidth(), getHeight(), 10, 10);
                g2.fill(roundRectangle);
            }

            super.paint(g);

            g2.dispose();
            g.setClip(oldClip);
        }

        public class ToolboxButton extends JButton implements Serializable
        {

            private static final long serialVersionUID = SerialVersion.APPVERSION;

            public ToolboxButton(Action action)
            {
                super(action);
                setOpaque(false);
                setFocusPainted(false);
                setBorderPainted(false);
                setMargin(new Insets(0, 0, 0, 0));
                setBorder(new Border()
                {
					@Override
                    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
                    {}
					@Override
                    public Insets getBorderInsets(Component c)
                    {
                        return new Insets(0, 2, 0, 2);
                    }
					@Override
                    public boolean isBorderOpaque()
                    {
                        return true;
                    }
                });
                addMouseListener(new MouseAdapter()
                {
                    public @Override void mouseExited(MouseEvent e)
                    {
                        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                        IndicatorToolbox.this.mouseOver = false;
                        IndicatorToolbox.this.validate();
                        IndicatorToolbox.this.repaint();
                    }
                    public @Override void mouseEntered(MouseEvent e)
                    {
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        IndicatorToolbox.this.mouseOver = true;
                        IndicatorToolbox.this.validate();
                        IndicatorToolbox.this.repaint();
                    }
                    public @Override void mousePressed(MouseEvent e)
                    {
                        IndicatorToolbox.this.mouseOver = false;
                        IndicatorToolbox.this.validate();
                        IndicatorToolbox.this.repaint();
                    }
                });
            }

        }

    }

}