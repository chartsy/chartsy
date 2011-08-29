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
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import org.chartsy.main.chart.Chart;
import org.chartsy.main.chart.Overlay;
import org.chartsy.main.data.Stock;
import org.chartsy.main.dialogs.SettingsPanel;
import org.chartsy.main.resources.ResourcesUtils;
import org.chartsy.main.utils.ColorGenerator;
import org.chartsy.main.utils.GraphicsUtils;
import org.chartsy.main.utils.Range;
import org.chartsy.main.utils.SerialVersion;
import org.openide.util.NbBundle;

/**
 *
 * @author viorel.gheba
 */
public class ChartPanel extends JLayeredPane implements Serializable
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;
	
    private ChartFrame chartFrame;
    private AnnotationPanel annotationPanel;
    private JLabel stockInfo;
    private JToolBar overlayToolboxes;
    private List<Overlay> overlays;
    //private boolean overlayToolboxesUpdated = false;

    public ChartPanel(ChartFrame frame)
    {
        chartFrame = frame;
        overlays = new ArrayList<Overlay>();
        initializeUIElements();
    }

    private void initializeUIElements()
    {
		setOpaque(false);
		setDoubleBuffered(true);
		
        annotationPanel = new AnnotationPanel(chartFrame);

        overlayToolboxes = new JToolBar(JToolBar.HORIZONTAL);
        overlayToolboxes.setBorder(BorderFactory.createEmptyBorder());
        overlayToolboxes.setOpaque(false);
        overlayToolboxes.setFloatable(false);

        stockInfo = new JLabel();
        stockInfo.setOpaque(false);
        stockInfo.setHorizontalAlignment(SwingConstants.LEFT);
        stockInfo.setVerticalAlignment(SwingConstants.TOP);
        stockInfo.setFont(chartFrame.getChartProperties().getFont());
        stockInfo.setForeground(chartFrame.getChartProperties().getFontColor());

        if (!chartFrame.getChartData().isStockNull())
        {
            Stock stock = chartFrame.getChartData().getStock();
            String stockInfoText = "";
            if (stock.hasCompanyName())
            {
                stockInfoText = NbBundle.getMessage(
                        ChartPanel.class,
                        "LBL_StockInfo",
                        new String[]
                        {
                            stock.getKey(),
                            stock.getCompanyName(),
                            chartFrame.getChartData().getInterval().getName(),
                            chartFrame.getChartData().getDataProvider().getName()
                        });
            } else
            {
                stockInfoText = NbBundle.getMessage(
                        ChartPanel.class,
                        "LBL_StockInfoNoCompany",
                        new String[]
                        {
                            stock.getKey(),
                            chartFrame.getChartData().getInterval().getName(),
                            chartFrame.getChartData().getDataProvider().getName()
                        });
            }

            stockInfo.setText(stockInfoText);
        } else
        {
            String stockInfoText = NbBundle.getMessage(
                    ChartPanel.class,
                    "LBL_StockInfoNoData");
            stockInfo.setText(stockInfoText);
        }

		ChartFrameAdapter frameAdapter = new ChartFrameAdapter()
		{
			@Override
			public void stockChanged(Stock newStock)
			{
				updateStockInfo(newStock);
			}
			@Override
			public void chartChanged(Chart newChart)
			{
				repaint();
			}
			@Override
			public void overlayAdded(Overlay overlay)
			{
				addOverlay(overlay);
				chartFrame.getChartData().calculateRange(chartFrame, overlays);
				chartFrame.revalidate();
				chartFrame.repaint();
			}
			@Override
			public void overlayRemoved(Overlay overlay)
			{
				removeOverlay(overlay);
				chartFrame.getChartData().calculateRange(chartFrame, overlays);
				chartFrame.revalidate();
				chartFrame.repaint();
			}
		};
		chartFrame.addChartFrameListener(frameAdapter);

        setLayout(new LayoutManager()
        {
			@Override
			public void addLayoutComponent(String name, Component comp)
            {}
            @Override
			public void removeLayoutComponent(Component comp)
            {}
            @Override
			public Dimension preferredLayoutSize(Container parent)
            {
                return new Dimension(0, 0);
            }
            @Override
			public Dimension minimumLayoutSize(Container parent)
            {
                return new Dimension(0, 0);
            }
            @Override
			public void layoutContainer(Container parent)
            {
                int width = parent.getWidth();
                int height = parent.getHeight();

                stockInfo.setBounds(0, 0, width, stockInfo.getPreferredSize().height);
                annotationPanel.setBounds(0, 2, width - 4, height - 4);
                overlayToolboxes.setLocation(0, stockInfo.getPreferredSize().height + 1);
            }
        });
		
		setFont(chartFrame.getChartProperties().getFont());
		setForeground(chartFrame.getChartProperties().getFontColor());

        add(overlayToolboxes);
        add(annotationPanel);
        add(stockInfo);

		setComponentZOrder(overlayToolboxes, 0);
		setComponentZOrder(annotationPanel, 1);
		setComponentZOrder(stockInfo, 2);
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

    public Range getRange()
    {
        return chartFrame.getChartData().getVisibleRange();
    }

    public
    @Override
    void paint(Graphics g)
    {
		Graphics2D g2 = GraphicsUtils.prepareGraphics(g);
//		if (!overlayToolboxesUpdated)
//			updateOverlayToolbar();

		chartFrame.getChartData().calculateRange(chartFrame, overlays);
		if (!chartFrame.getChartData().isChartNull())
			chartFrame.getChartData().getChart().paint(g2, chartFrame);

		if (!overlays.isEmpty())
		{
			Rectangle bounds = getBounds();
			bounds.grow(-2, -2);
			for (Overlay overlay : overlays)
				overlay.paint(g2, chartFrame, bounds);
		}

		super.paint(g);
    }

	@Override
	public void update(Graphics g)
	{
		paint(g);
	}

	private void updateStockInfo(Stock stock)
	{
		String stockInfoText = "";
		if (stock.hasCompanyName())
		{
			stockInfoText = NbBundle.getMessage(
				ChartPanel.class,
				"LBL_StockInfo",
				new String[]
				{
					stock.getKey(),
					stock.getCompanyName(),
					chartFrame.getChartData().getInterval().getName(),
					chartFrame.getChartData().getDataProvider().getName()
				});
		} else
		{
			stockInfoText = NbBundle.getMessage(
				ChartPanel.class,
				"LBL_StockInfoNoCompany",
				new String[]
				{
					stock.getKey(),
					chartFrame.getChartData().getInterval().getName(),
					chartFrame.getChartData().getDataProvider().getName()
				});
		}

		if (!stockInfo.getText().equals(stockInfoText))
			stockInfo.setText(stockInfoText);
	}

    public void setOverlays(List<Overlay> list)
    {
        clearOverlays();
        chartFrame.getChartData().removeAllOverlaysDatasetListeners();
        for (Overlay o : list)
        {
            o.setDatasetKey(chartFrame.getChartData().getDatasetKey());
            o.calculate();
            addOverlay(o);
        }
		updateOverlayToolbar();
    }

    public List<Overlay> getOverlays()
    {
        List<Overlay> list = new ArrayList<Overlay>();
        for (Overlay overlay : overlays)
        {
            list.add(overlay);
        }
        return list;
    }

    public Overlay getOverlay(int index)
    {
        if (index < 0 || index > overlays.size())
        {
            return null;
        }
        return overlays.get(index);
    }

	public boolean hasOverlays()
	{
		return !overlays.isEmpty();
	}

    public int getOverlaysCount()
    {
        return overlays.size();
    }

    public void addOverlay(Overlay overlay)
    {
        overlays.add(overlay);
		updateOverlayToolbar();
    }

    public void removeOverlay(Overlay overlay)
    {
        overlays.remove(overlay);
		updateOverlayToolbar();
    }

    public void clearOverlays()
    {
        overlays.clear();
        overlays = new ArrayList<Overlay>();
		updateOverlayToolbar();
    }

    public void updateOverlayToolbar()
    {
        int width = 0;
        int height = 0;

        overlayToolboxes.removeAll();
        for (Overlay overlay : overlays)
        {
            OverlayToolbox overlayToolbox = new OverlayToolbox(overlay);
            overlayToolboxes.add(overlayToolbox);
            overlayToolbox.update();

            width += overlayToolbox.getWidth() + 16;
            height = overlayToolbox.getHeight() + 4;
        }

        overlayToolboxes.setBounds(overlayToolboxes.getX(), overlayToolboxes.getY(), width, height);

        overlayToolboxes.revalidate();
        overlayToolboxes.repaint();
    }

    @Override
    public Rectangle getBounds()
    {
        return new Rectangle(0, 0, getWidth(), getHeight());
    }

    public final class OverlayToolbox extends JToolBar implements Serializable
    {

        private static final long serialVersionUID = SerialVersion.APPVERSION;
        private Overlay overlay;
        private JLabel overlayLabel;
        private JComponent container;
        public boolean mouseOver = false;
        private final Color backColor = ColorGenerator.getTransparentColor(new Color(0x1C2331), 50);

        public OverlayToolbox(Overlay overlay)
        {
            super(JToolBar.HORIZONTAL);
            this.overlay = overlay;
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

            overlayLabel = new JLabel(overlay.getLabel());
            overlayLabel.setHorizontalTextPosition(SwingConstants.LEFT);
            overlayLabel.setVerticalTextPosition(SwingConstants.CENTER);
            overlayLabel.setOpaque(false);
            overlayLabel.setBorder(BorderFactory.createEmptyBorder());
			overlayLabel.setFont(ChartPanel.this.getFont());
			overlayLabel.setForeground(ChartPanel.this.getForeground());
            add(overlayLabel);

            container = new JPanel();
            container.setOpaque(false);
            container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
            add(container);
            update();

            addMouseListener(new MouseAdapter()
            {

                public
                @Override
                void mouseEntered(MouseEvent e)
                {
                    mouseOver = true;
                    validate();
                    repaint();
                }

                public
                @Override
                void mouseExited(MouseEvent e)
                {
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    mouseOver = false;
                    validate();
                    repaint();
                }
            });
        }

        public
        @Override
        int getWidth()
        {
            return getLayout().preferredLayoutSize(this).width;
        }

        public
        @Override
        int getHeight()
        {
            return getLayout().preferredLayoutSize(this).height;
        }

        public void update()
        {
            // remove all buttons
            container.removeAll();

            OverlayToolboxButton button;

            // Settings
            container.add(button = new OverlayToolboxButton(overlaySettings(overlay)));
            button.setText("");
            button.setToolTipText("Settings");

            // Remove
            container.add(button = new OverlayToolboxButton(removeAction(overlay)));
            button.setText("");
            button.setToolTipText("Remove");

            revalidate();
            repaint();
        }

        public
        @Override
        void paint(Graphics g)
        {
			if (!overlayLabel.getFont().equals(chartFrame.getChartProperties().getFont()))
				overlayLabel.setFont(chartFrame.getChartProperties().getFont());
			if (!overlayLabel.getForeground().equals(chartFrame.getChartProperties().getFontColor()))
				overlayLabel.setForeground(chartFrame.getChartProperties().getFontColor());
			if (!overlayLabel.getText().equals(overlay.getLabel()))
				overlayLabel.setText(overlay.getLabel());

            Graphics2D g2 = GraphicsUtils.prepareGraphics(g);
            g2.setPaintMode();

            if (mouseOver)
            {
                g2.setColor(backColor);
                int x = overlayLabel.getLocation().x - getInsets().left;
                int y = overlayLabel.getLocation().y - getInsets().top;
                RoundRectangle2D roundRectangle = new RoundRectangle2D.Double(x, y, getWidth(), getHeight(), 10, 10);
                g2.fill(roundRectangle);
            }

            super.paint(g);
            g2.dispose();
        }

		@Override
		public void update(Graphics g)
		{
			paint(g);
		}

        public class OverlayToolboxButton extends JButton implements Serializable
        {

            private static final long serialVersionUID = SerialVersion.APPVERSION;

            public OverlayToolboxButton(Action action)
            {
                super(action);
                setOpaque(false);
                setFocusPainted(false);
                setBorderPainted(false);
                setMargin(new Insets(0, 0, 0, 0));
                setBorder(new Border()
                {

                    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
                    {
                    }

                    public Insets getBorderInsets(Component c)
                    {
                        return new Insets(0, 2, 0, 2);
                    }

                    public boolean isBorderOpaque()
                    {
                        return true;
                    }
                });
                addMouseListener(new MouseAdapter()
                {

                    public
                    @Override
                    void mouseExited(MouseEvent e)
                    {
                        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                        mouseOver = false;
                    }

                    public
                    @Override
                    void mouseEntered(MouseEvent e)
                    {
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        mouseOver = true;
                    }
                });
            }
        }
    }

    private AbstractAction overlaySettings(final Overlay overlay)
    {
        return new AbstractAction("Overlay Settings", ResourcesUtils.getIcon("settings"))
        {

            public void actionPerformed(ActionEvent e)
            {
                SettingsPanel.getDefault().openSettingsWindow(overlay);
            }
        };
    }

    private AbstractAction removeAction(final Overlay overlay)
    {
        return new AbstractAction("Remove Indicator", ResourcesUtils.getIcon("remove"))
        {

            public void actionPerformed(ActionEvent e)
            {
				chartFrame.overlayRemoved(overlay);
            }
        };
    }
	
}
