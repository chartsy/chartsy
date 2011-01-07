package org.chartsy.main;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.LinkedHashMap;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import org.chartsy.main.chart.Indicator;
import org.chartsy.main.chart.Overlay;
import org.chartsy.main.data.ChartData;
import org.chartsy.main.utils.ColorGenerator;
import org.chartsy.main.utils.CoordCalc;
import org.chartsy.main.utils.GraphicsUtils;
import org.chartsy.main.utils.HtmlRendererImpl;
import org.chartsy.main.utils.SerialVersion;
import org.openide.util.NbBundle;

/**
 *
 * @author viorel.gheba
 */
public class ChartSplitPanel extends JLayeredPane implements Serializable
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    private ChartFrame chartFrame;
    private ChartPanel chartPanel;
    private IndicatorsPanel indicatorsPanel;
    private MarkerLabel label;

    private int index = -1;
    private static int width = 200;
    private static int height;
    private int lines = 5;

    private Color lineColor = new Color(0xef2929);
    private Color color = new Color(0x1C2331);
    private Color backgroundColor = ColorGenerator.getTransparentColor(color, 100);
    private Color fontColor = new Color(0xffffff);

    private static Font font;

	static
	{
		font = new Font("Dialog", Font.PLAIN, 10);
        height = 14;
	}

    public ChartSplitPanel(ChartFrame frame)
    {
        chartFrame = frame;
        chartPanel = new ChartPanel(chartFrame);
        indicatorsPanel = new IndicatorsPanel(chartFrame);
        label = new MarkerLabel();

        setOpaque(false);
		setDoubleBuffered(true);

        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        setLayout(new LayoutManager()
        {
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
                Insets insets = parent.getInsets();
				int x = insets.left;
				int y = insets.top;
                int w = parent.getWidth() - insets.left - insets.right;
                int h = parent.getHeight() - insets.top - insets.bottom;
                int indicatorsHeight = indicatorsPanel.getPanelHeight();
                int chartHeight = h - indicatorsHeight;

                indicatorsPanel.setBounds(x, y + chartHeight, w, indicatorsHeight);
                chartPanel.setBounds(x, y, w, chartHeight);

                Point dp = new Point(0, 50);
                Point p = label.getLocation();
                if (!dp.equals(p))
                    label.setBounds(p.x, p.y, width + 2, height * lines + 2);
                else
                    label.setBounds(dp.x, dp.y, width + 2, height * lines + 2);
            }
        });

        add(label);
        add(indicatorsPanel);
        add(chartPanel);
        label.setLocation(0, 50);
    }

    public ChartFrame getChartFrame()
    { return chartFrame; }

    public void setChartFrame(ChartFrame frame)
    { chartFrame = frame; }

    public ChartPanel getChartPanel()
    { return chartPanel; }

    public void setChartPanel(ChartPanel panel)
    { chartPanel = panel; }

    public IndicatorsPanel getIndicatorsPanel()
    { return indicatorsPanel; }

    public void setIndicatorsPanel(IndicatorsPanel panel)
    { indicatorsPanel = panel; }

    public void setIndex(int i)
    { index = i; }

    public int getIndex()
    { return index; }

    @Override
    public void paint(Graphics g)
    {
        super.paint(g);
        Graphics2D g2 = GraphicsUtils.prepareGraphics(g);

        if (chartFrame.getChartProperties().getMarkerVisibility())
        {
            if (index != -1)
            {
                labelText();
                paintMarkerLine(g2);
                label.setVisible(true);
            }
        }
        else
        {
            label.setVisible(false);
        }
        
        g2.dispose();
    }

	@Override
	public void update(Graphics g)
	{
		paint(g);
	}

    private void paintMarkerLine(Graphics2D g)
    {
        Rectangle bounds = chartPanel.getBounds();
        bounds.grow(-2, -2);
        
        long time = chartFrame.getChartData().getVisible().getTimeAt(index);
        String s = chartFrame.getChartData().getInterval().getMarkerString(time);
        double dx = chartFrame.getChartData().getX(index, bounds);
        g.setFont(font);

        FontMetrics fm = g.getFontMetrics(font);
        int w = fm.stringWidth(s) + 2;
        int h = fm.getHeight() + 2;
        boolean inv = (getWidth() - dx < w);

        // paint line
        g.setPaint(lineColor);
        g.draw(CoordCalc.line(dx, 0, dx, getHeight()));
        // paint background
        g.fill(CoordCalc.rectangle(inv ? dx - w : dx, 0, w, h));
        // paint rectangle and string
        g.draw(CoordCalc.rectangle(inv ? dx - w : dx, 0, w, h));
        g.setPaint(fontColor);
        g.drawString(s, inv ? (float) (dx - w + 1) : (float) (dx + 1), (float) (fm.getAscent() + 1));

        bounds.grow(2, 2);
    }

    private String addLine(String left, String right)
    {
        if (!right.equals(" "))
			return NbBundle.getMessage(
				ChartSplitPanel.class,
				"HTML_Line",
				new String[] {String.valueOf(width/2), left, right});
        else
			return NbBundle.getMessage(
				ChartSplitPanel.class,
				"HTML_EmptyLine",
				new String[] {String.valueOf(width), left});
    }

    public void labelText()
    {
        if (index != -1)
        {
            ChartData cd = chartFrame.getChartData();
            DecimalFormat df = new DecimalFormat("#,##0.00");

            long time = cd.getVisible().getTimeAt(index);
            String date = chartFrame.getChartData().getInterval().getMarkerString(time);

            StringBuilder sb = new StringBuilder();
            // Date
            sb.append(addLine("Date:", date));
            // Open
            sb.append(addLine("Open:", df.format(cd.getVisible().getOpenAt(index))));
            // High
            sb.append(addLine("High:", df.format(cd.getVisible().getHighAt(index))));
            // Low
            sb.append(addLine("Low:", df.format(cd.getVisible().getLowAt(index))));
            // Close
            sb.append(addLine("Close:", df.format(cd.getVisible().getCloseAt(index))));

            lines = 5;

            boolean hasOverlays = chartPanel.getOverlaysCount() > 0;
            boolean hasIndicators = indicatorsPanel.getIndicatorsCount() > 0;

            if (hasOverlays || hasIndicators)
            {
                sb.append(addLine(" ", " "));
                lines++;
            }

            if (hasOverlays)
            {
                for (Overlay overlay : chartPanel.getOverlays())
                {
                    LinkedHashMap map = overlay.getHTML(chartFrame, index);
                    Iterator it = map.keySet().iterator();
                    while (it.hasNext()) {
                        Object key = it.next();
                        sb.append(addLine(key.toString(), map.get(key).toString()));
                        lines++;
                    }
                }
            }

            if (hasIndicators)
            {
                if (hasOverlays)
                {
                    sb.append(addLine(" ", " "));
                    lines++;
                }

                for (Indicator indicator : indicatorsPanel.getIndicators())
                {
                    LinkedHashMap map = indicator.getHTML(chartFrame, index);
                    Iterator it = map.keySet().iterator();
                    while (it.hasNext()) {
                        Object key = it.next();
                        sb.append(addLine(key.toString(), map.get(key).toString()));
                        lines++;
                    }
                }
            }

			String labelText = NbBundle.getMessage(
				ChartSplitPanel.class,
				"HTML_Marker",
				new String[] {String.valueOf(width), sb.toString()});
			if (!label.getText().equals(labelText))
				label.setText(labelText);

			Dimension dimension = new Dimension(width, height * lines);
			if (!label.getPreferredSize().equals(dimension))
				label.setPreferredSize(dimension);
        }
        else
        {
            label.setVisible(false);
        }
    }

    public void moveLeft()
    {
        ChartData cd = chartFrame.getChartData();
        int last = cd.getLast();
        int items = cd.getPeriod() - 1;
        int i = index - 1;
        if (i < 0)
        {
            if (last - 1 > items)
            {
                cd.setLast(last - 1);
            }
        }
        else
        {
            index = i;
        }
        labelText();
        cd.calculate(chartFrame);
        chartFrame.repaint();
    }

    public void moveRight()
    {
        ChartData cd = chartFrame.getChartData();
        int all = cd.getDataset().getItemsCount();
        int last = cd.getLast();
        int items = cd.getPeriod() - 1;
        int i = index + 1;
        if (i > items)
        {
            if (last < all)
            {
                cd.setLast(last + 1);
            }
        }
        else
        {
            index = i;
        }
        labelText();
        cd.calculate(chartFrame);
        chartFrame.repaint();
    }

    public static class Draggable extends MouseAdapter implements MouseMotionListener
    {
        Point lastP;
        Component cDraggable;

        public Draggable(Component comp)
        {
            comp.setLocation(0, 0);
            cDraggable = comp;
        }

        private void setCursorType(Point p)
        {
            Point loc = cDraggable.getLocation();
            Dimension size = cDraggable.getSize();
            if ((p.y + 4 < loc.y + size.height) && (p.x + 4 < p.x + size.width))
            {
                cDraggable.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }
        }

        @Override
        public void mousePressed(MouseEvent e)
        {
            if (cDraggable.getCursor().equals(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR)))
            {
                lastP = e.getPoint();
            }
            else
            {
                lastP = null;
            }
        }

        @Override
        public void mouseReleased(MouseEvent e)
        {
            lastP = null;
        }

        @Override
        public void mouseMoved(MouseEvent e)
        {
            setCursorType(e.getPoint());
        }

        @Override
        public void mouseDragged(MouseEvent e)
        {
            int x, y;
            if (lastP != null)
            {
                x = cDraggable.getX() + (e.getX() - (int)lastP.getX());
                y = cDraggable.getY() + (e.getY() - (int)lastP.getY());
                cDraggable.setLocation(x, y);
            }
        }

    }

	private class MarkerLabel extends HtmlRendererImpl
	{

		private static final long serialVersionUID = SerialVersion.APPVERSION;

		private MarkerLabel()
		{
			setOpaque(false);
			setFont(font);
			setForeground(fontColor);
			setVisible(index != -1);
			setPreferredSize(new Dimension(width, height));
			Draggable draggable = new Draggable(this);
			addMouseListener(draggable);
			addMouseMotionListener(draggable);
		}

		@Override protected void paintComponent(Graphics g)
		{
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

			g2.setColor(backgroundColor);
			g2.fillRect(0, 0, width + 2, height * lines + 2);
			g2.setColor(color);
			g2.drawRect(0, 0, width + 1, height * lines + 1);

			super.paintComponent(g);
		}
		
	}

}
