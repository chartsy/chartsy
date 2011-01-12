package org.chartsy.main;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import org.chartsy.main.chart.Indicator;
import org.chartsy.main.data.ChartData;
import org.chartsy.main.utils.SerialVersion;

/**
 *
 * @author Administrator
 */
public class IndicatorsPanel extends JPanel implements Serializable
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    private ChartFrame chartFrame;

    public IndicatorsPanel(ChartFrame frame)
    {
        chartFrame = frame;
        setOpaque(false);
		setDoubleBuffered(true);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        setLayout(new LayoutManager() {
			@Override
            public void addLayoutComponent(String name, Component comp) {}
			@Override
            public void removeLayoutComponent(Component comp) {}
			@Override
            public Dimension preferredLayoutSize(Container parent) { return new Dimension(0, 0); }
			@Override
            public Dimension minimumLayoutSize(Container parent) { return new Dimension(0, 0); }
			@Override
            public void layoutContainer(Container parent) {
                Component[] components = parent.getComponents();
                if (components.length == 0) return;
                Insets insets = parent.getInsets();
                int width = parent.getWidth() - insets.left - insets.right;
                int x = insets.left;
                int y = insets.top;
                for (int i = 0; i < components.length; i++)
				{
                    int height = ((IndicatorPanel)components[i]).getPanelHeight();
                    components[i].setBounds(x, y, width, height);
                    y += height;
                }
            }
        });
        Resizeable resizeable = new Resizeable(this);
        addMouseListener(resizeable);
        addMouseMotionListener(resizeable);

		ChartFrameAdapter frameAdapter = new ChartFrameAdapter()
		{
			@Override
			public void indicatorAdded(Indicator indicator)
			{
				int count = getIndicatorsCount();
				IndicatorPanel indicatorPanel = new IndicatorPanel(chartFrame, indicator);
				indicatorPanel.setMaximizedHeight(Indicator.DEFAULT_HEIGHT);
				add(indicatorPanel, count);
				updateIndicatorsToolbar();
				calculateHeight();
				chartFrame.validate();
				chartFrame.repaint();
			}

			@Override
			public void indicatorRemoved(Indicator indicator)
			{
				IndicatorPanel indicatorPanel = getIndicatorPanel(indicator);
				if (indicatorPanel != null)
				{
					indicator.clearDatasets();
					remove(indicatorPanel);
					updateIndicatorsToolbar();
					calculateHeight();
					chartFrame.validate();
					chartFrame.repaint();
				}
			}
		};
		chartFrame.addChartFrameListener(frameAdapter);
    }

    public ChartFrame getChartFrame() 
    { return chartFrame; }

    public void setChartFrame(ChartFrame frame)
    { chartFrame = frame; }

    public int getIndicatorsCount()
	{
        return getComponentCount();
    }

    public IndicatorPanel getIndicatorPanel(Indicator ind)
	{
        IndicatorPanel[] panels = getIndicatorPanels();
        for (int i = 0; i < panels.length; i++)
            if (ind.equals(panels[i].getIndicator()))
				return panels[i];
        return null;
    }

    public IndicatorPanel[] getIndicatorPanels()
	{
		IndicatorPanel[] indicatorPanels = new IndicatorPanel[getComponentCount()];
		for (int i = 0; i < indicatorPanels.length; i++)
		{
			Component component = getComponent(i);
			if (component instanceof IndicatorPanel)
				indicatorPanels[i] = (IndicatorPanel) component;
		}
        return indicatorPanels;
    }

    public int getPanelHeight()
	{
        IndicatorPanel[] panels = getIndicatorPanels();
        if (panels.length > 0)
		{
            int height = 0;
            for (IndicatorPanel panel : panels)
                height += panel.getPanelHeight();
            return height;
        } else
		{
            return 0;
        }
    }

    public int getPanelY(int index)
	{
        IndicatorPanel[] panels = getIndicatorPanels();
        if (panels.length > 0)
		{
            int height = 0;
            for (int i = 0; i < index; i++)
                height += panels[i].getPanelHeight();
            return height;
        } else
		{
            return 0;
        }
    }

    public AnnotationPanel[] getAnnotationPanels()
	{
        IndicatorPanel[] indicatorPanels = getIndicatorPanels();
        AnnotationPanel[] annotationPanels = new AnnotationPanel[indicatorPanels.length];
        for (int i = 0; i < indicatorPanels.length; i++)
            annotationPanels[i] = indicatorPanels[i].getAnnotationPanel();
        return annotationPanels;
    }

    public IndicatorPanel getIndicatorPanel(AnnotationPanel panel)
	{
        IndicatorPanel[] indicatorPanels = getIndicatorPanels();
        for (int i = 0; i < indicatorPanels.length; i++)
            if (indicatorPanels[i].getAnnotationPanel() == panel)
                return indicatorPanels[i];
        return null;
    }

    public boolean allMinimized()
    {
        for (IndicatorPanel ip : getIndicatorPanels())
            if (ip.isMaximized())
                return true;
        return false;
    }

    public int getMaximizedHeight()
    {
        for (IndicatorPanel ip : getIndicatorPanels())
            if (ip.isMaximized())
                return ip.getPanelHeight();
        return 24;
    }

    public void updateIndicatorsToolbar()
    {
        for (IndicatorPanel ip : getIndicatorPanels())
            ip.updateToolbox();
    }

    public void calculateHeight()
    {
		int count = getIndicatorsCount();
		if (count != 0)
		{
			int height = (int) (chartFrame.getSplitPanel().getHeight() - ChartData.dataOffset.bottom);
			height = (int) (0.60 * height);
			height = height / count;
			if (height > Indicator.DEFAULT_HEIGHT || height < 0)
				height = Indicator.DEFAULT_HEIGHT;

			for (IndicatorPanel panel : getIndicatorPanels())
				panel.setMaximizedHeight(height);
		}
    }

    public Indicator[] getIndicators()
	{
        IndicatorPanel[] indicatorPanels = getIndicatorPanels();
        Indicator[] indicators = new Indicator[indicatorPanels.length];
        for (int i = 0; i < indicatorPanels.length; i++)
            indicators[i] = indicatorPanels[i].getIndicator();
        return indicators;
    }

    public List<Indicator> getIndicatorsList()
	{
        Indicator[] inds = getIndicators();
        List<Indicator> list = new ArrayList<Indicator>();
		list.addAll(Arrays.asList(inds));
        return list;
    }

    public void addIndicator(Indicator indicator)
	{
        int count = getIndicatorsCount();
        IndicatorPanel indicatorPanel = new IndicatorPanel(chartFrame, indicator);
        indicatorPanel.updateToolbox();
        int height = Indicator.DEFAULT_HEIGHT != indicator.getMaximizedHeight() 
			? indicator.getMaximizedHeight()
			: Indicator.DEFAULT_HEIGHT;
        indicatorPanel.setMaximizedHeight(height);
        add(indicatorPanel, count);
		calculateHeight();
    }

    public int getIndicatorIndex(IndicatorPanel panel)
    {
        int result = -1;
        IndicatorPanel[] indicatorPanels = getIndicatorPanels();
        for (int i = 0; i < indicatorPanels.length; i++)
		{
            if (indicatorPanels[i] == panel)
			{
                result = i;
                break;
            }
        }
        return result;
    }

    public void moveUp(IndicatorPanel panel)
	{
        int index = getIndicatorIndex(panel);
        int count = getIndicatorsCount();
        int dest = index - 1;
        if (dest < 0) dest = count - 1;
        if (dest != index)
		{
            remove(panel);
            add(panel, dest);
        }
    }

    public void moveDown(IndicatorPanel panel)
	{
        int index = getIndicatorIndex(panel);
        int count = getIndicatorsCount();
        int dest = index + 1;
        if (dest >= count) dest = 0;
        if (dest != index)
		{
            remove(panel);
            add(panel, dest);
        }
    }

    public void removeIndicator(IndicatorPanel panel)
	{
        remove(panel);
        calculateHeight();
        updateIndicatorsToolbar();
    }

    public void removeAllIndicators()
	{
        removeAll();
    }

    public static class Resizeable extends MouseAdapter implements MouseMotionListener
    {

        int fix_pt_x = -1;
        int fix_pt_y = -1;
        IndicatorsPanel cResizeable;
        Cursor oldCursor;

        public Resizeable(IndicatorsPanel comp)
        {
            cResizeable = comp;
        }

        private void setCursorType(Point p)
        {
            boolean n = p.y <= 4;

            if (n)
            {
                cResizeable.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
                return;
            }
            else
            {
                cResizeable.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                return;
            }
        }

        @Override
        public void mouseEntered(MouseEvent e)
        {
            cResizeable.requestFocus();
            if (cResizeable.allMinimized())
                setCursorType(e.getPoint());
        }

        @Override
        public void mouseExited(MouseEvent e)
        {
            if (oldCursor != null)
                ((Component)e.getSource()).setCursor(oldCursor);
            oldCursor = null;
        }

        @Override
        public void mousePressed(MouseEvent e)
        {
            cResizeable.requestFocus();
            Cursor c = cResizeable.getCursor();
            if (c.equals(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR)))
            {
                fix_pt_y = e.getY();
                return;
            }
            else
            {
                fix_pt_y = -1;
                return;
            }
        }

        @Override
        public void mouseReleased(MouseEvent e)
        {
            fix_pt_y = -1;
            cResizeable.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            cResizeable.doLayout();
            cResizeable.getChartFrame().getSplitPanel().doLayout();
            cResizeable.getChartFrame().validate();
            cResizeable.getChartFrame().repaint();
        }

        @Override
        public void mouseMoved(MouseEvent e)
        {
            if (cResizeable.allMinimized())
                setCursorType(e.getPoint());
        }

        @Override
        public void mouseDragged(MouseEvent e)
        {
            if (cResizeable.allMinimized())
            {
                Cursor c = cResizeable.getCursor();
                int y = e.getY();
                int count = cResizeable.getIndicatorsCount();
                int maxHeight = (int) ((cResizeable.getChartFrame().getMainPanel().getHeight() - ChartData.dataOffset.bottom) / (count + 1.5)) * count;
                int height = cResizeable.getMaximizedHeight() * count;
                if (fix_pt_y != -1)
                {
                    int dy = 0;
                    if (c.equals(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR)))
                    {
                        dy = fix_pt_y - y;
                    }
                    height += dy;
                }

                if (height >= maxHeight)
                    height = maxHeight;

                int indicatorH = height / count;
                if (indicatorH < 50)
                    indicatorH = 50;

                for (IndicatorPanel panel : cResizeable.getIndicatorPanels())
                    panel.setMaximizedHeight(indicatorH);

                cResizeable.doLayout();
                cResizeable.getChartFrame().getSplitPanel().doLayout();
                cResizeable.getChartFrame().validate();
                cResizeable.getChartFrame().repaint();
            }
        }

    }

}
