package org.chartsy.main;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;
import org.chartsy.main.chart.Annotation;
import org.chartsy.main.chart.Indicator;
import org.chartsy.main.chart.Overlay;
import org.chartsy.main.data.ChartData;
import org.chartsy.main.data.Dataset;
import org.chartsy.main.dialogs.AnnotationProperties;
import org.chartsy.main.managers.AnnotationManager;
import org.chartsy.main.utils.GraphicsUtils;
import org.chartsy.main.utils.Range;
import org.chartsy.main.utils.SerialVersion;

/**
 *
 * @author viorel.gheba
 */
public class AnnotationPanel extends JPanel
	implements MouseListener, MouseMotionListener, KeyListener, Serializable
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public static final int NONE = 0;
    public static final int NEWANNOTATION = 1;
    public static final int RESIZE = 2;
    public static final int MOVE = 3;
    private int state;

    private ChartFrame chartFrame;
    private List<Annotation> annotations;
    private Annotation current = null;
	private ToolTipManager toolTipManager;

    public AnnotationPanel(ChartFrame frame)
    {
        state = NONE;
        chartFrame = frame;
		toolTipManager = ToolTipManager.sharedInstance();
		toolTipManager.setLightWeightPopupEnabled(true);
		toolTipManager.registerComponent(this);
        annotations = new ArrayList<Annotation>();

        setOpaque(false);
		setDoubleBuffered(true);

        addMouseListener((MouseListener) this);
        addMouseMotionListener((MouseMotionListener) this);
        addKeyListener((KeyListener) this);
    }

    public ChartFrame getChartFrame()
    { return chartFrame; }

    public int getState()
    { return state; }

    public void setState(int i)
    { state = i; }

	@Override
	public void paint(Graphics g)
	{
		Graphics2D g2 = GraphicsUtils.prepareGraphics(g);
        for (Annotation annotation : annotations)
            annotation.paint(g2);
	}

	@Override
	public void update(Graphics g)
	{
		paint(g);
	}

    public Range getRange()
    {
        Container parent = getParent();
        if (parent instanceof ChartPanel)
        {
            return ((ChartPanel)parent).getRange();
        }
        else
        {
            return ((IndicatorPanel)parent).getIndicator().getRange(chartFrame);
        }
    }

    public void setAnnotationsList(List<Annotation> list)
    {
        for (Annotation a : list)
        {
            a.setChartFrame(chartFrame);
            a.setAnnotationPanel(this);
            annotations.add(a);
        }
        repaint();
    }

    public List<Annotation> getAnnotationsList()
    { return annotations; }

    public Annotation[] getAnnotations()
    { return annotations.toArray(new Annotation[annotations.size()]); }

    public void addAnnotation(Annotation a)
    { annotations.add(a); }

    public boolean hasCurrent()
    { return current != null; }

    public boolean isCurrentNull()
    { return current == null; }

    public Annotation getCurrent()
    { return current ; }

    public void setCurrent(Annotation a)
    { current = a; }

    public void deselectAll()
    {
        for (Annotation annotation : annotations)
            annotation.setSelected(false);
        current = null;
    }

    public void removeAllAnnotations()
    {
        try
        {
            current = null;
            annotations.clear();
            validate();
            repaint();
        }
        catch (Exception ex)
        {
            ChartFrame.LOG.log(Level.SEVERE, null, ex);
        }
    }

    public void removeAnnotation()
    {
        if (!isCurrentNull() && getCurrent().isSelected())
        {
            current.setSelected(false);
            annotations.remove(getCurrent());
            current = null;
            repaint();
        }
    }

    private boolean isAnnotation(int x, int y)
    {
        for (Annotation a : annotations)
        {
            boolean b = a.pointIntersects(x, y);
            if (b)
            {
                current = a;
                current.setActive(true);
                current.setSelected(true);
                return b;
            }
        }
        return false;
    }

    public void mouseClicked(MouseEvent e)
    {}

    public void mousePressed(MouseEvent e)
    {
        requestFocus();

        if (e.isConsumed())
            return;

        if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 1)
        {
            if (getCursor().equals(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR)))
                getParent().requestFocus();

            if (AnnotationManager.getDefault().hasNew())
                setState(NEWANNOTATION);
            
            switch (getState())
            {
                case NONE:
                    chartFrame.deselectAll();
                    if (!isAnnotation(e.getX(), e.getY()))
                    {
                        if (!getCursor().equals(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR)))
                        {
                            if (chartFrame.getChartProperties().getMarkerVisibility())
                            {
                                Rectangle rect = getBounds();
                                rect.grow(-2, -2);

                                int i = chartFrame.getChartData().getIndex(e.getPoint(), rect);
                                if (i != -1)
                                {
                                    chartFrame.getSplitPanel().setIndex(i);
                                    chartFrame.getSplitPanel().labelText();
                                    chartFrame.getSplitPanel().repaint();
                                }
                            }
                            else
                            {
                                chartFrame.getSplitPanel().setIndex(-1);
                            }
                        }
                    }
                    else
                    {
                        if (!isCurrentNull())
                            getCurrent().mousePressed(e);
                    }
                    break;
                case RESIZE:
                    if (!isCurrentNull())
                        getCurrent().mousePressed(e);
                    break;
                case MOVE:
                    if (!isCurrentNull())
                        getCurrent().mousePressed(e);
                    break;
                case NEWANNOTATION:
                    chartFrame.deselectAll();
                    Annotation a = AnnotationManager.getDefault().getNewAnnotation(chartFrame);
                    a.setAnnotationPanel(this);
                    setCurrent(a);
                    if (!isCurrentNull())
                    {
                        getCurrent().mousePressed(e);
                    }
                    else
                    {
                        setState(NONE);
                        mousePressed(e);
                    }
                    break;
            }
        }
        if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2)
        {
            if (!isCurrentNull())
            {
                AnnotationProperties dialog = new AnnotationProperties(new JFrame(), true);
                dialog.initializeForm(getCurrent());
                dialog.setLocationRelativeTo(chartFrame);
                dialog.setVisible(true);
            }
        }
        if (e.getButton() == MouseEvent.BUTTON3)
        {
            switch (getState())
            {
                case NONE:
                    chartFrame.getMenu().show(this, e.getX(), e.getY());
                    break;
            }
        }
    }

    public void mouseReleased(MouseEvent e)
    {
        if (e.isConsumed())
            return;

        if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) == MouseEvent.BUTTON1_MASK)
        {
            if (!isCurrentNull())
                getCurrent().mouseReleased(e);
        }
    }

    public void mouseEntered(MouseEvent e)
    {
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		//tooltipHandler(e);
    }

    public void mouseExited(MouseEvent e)
    {}

    public void mouseDragged(MouseEvent e)
    {
        if (e.isConsumed())
            return;
        if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) == MouseEvent.BUTTON1_MASK)
        {
            if (!isCurrentNull())
                getCurrent().mouseDragged(e);
            else
            {
                if (chartFrame.getChartProperties().getMarkerVisibility())
                {
                    Rectangle rect = getBounds();
                    rect.grow(-2, -2);

                    int i = chartFrame.getChartData().getIndex(e.getPoint(), rect);
                    if (i != -1)
                    {
                        chartFrame.getSplitPanel().setIndex(i);
                        chartFrame.getSplitPanel().labelText();
                        chartFrame.getSplitPanel().repaint();
                    }
                }
                else
                {
                    chartFrame.getSplitPanel().setIndex(-1);
                }
            }
        }
    }

    public void mouseMoved(MouseEvent e)
    {
		//tooltipHandler(e);
	}

    public void keyTyped(KeyEvent e)
    {}

    public void keyPressed(KeyEvent e)
    {
        if (e.isConsumed())
            return;
        
        requestFocus();
        switch (e.getKeyCode())
        {
            case KeyEvent.VK_MINUS:
                chartFrame.zoomOut();
                break;
            case KeyEvent.VK_SUBTRACT:
                chartFrame.zoomOut();
                break;
            case KeyEvent.VK_ADD:
                chartFrame.zoomIn();
                break;
        }
        switch (e.getModifiers())
        {
            case KeyEvent.SHIFT_MASK:
                if (e.getKeyCode() == KeyEvent.VK_EQUALS)
                    chartFrame.zoomIn();
                break;
        }

        if (hasCurrent() && getCurrent().isSelected())
        {
            switch (e.getKeyCode())
            {
                case KeyEvent.VK_DELETE:
                    removeAnnotation();
                    break;
                case KeyEvent.VK_UP:
                    getCurrent().moveUp();
                    break;
                case KeyEvent.VK_DOWN:
                    getCurrent().moveDown();
                    break;
                case KeyEvent.VK_LEFT:
                    getCurrent().moveLeft();
                    break;
                case KeyEvent.VK_RIGHT:
                    getCurrent().moveRight();
                    break;
            }
        }
        else
        {
            if (chartFrame.getChartProperties().getMarkerVisibility())
            {
                switch (e.getKeyCode())
                {
                    case KeyEvent.VK_LEFT:
                        chartFrame.getSplitPanel().moveLeft();
                        break;
                    case KeyEvent.VK_RIGHT:
                        chartFrame.getSplitPanel().moveRight();
                        break;
                }
            }
        }
    }

    public void keyReleased(KeyEvent e)
    {}

	public void tooltipHandler(MouseEvent e)
	{
		ChartData chartData = chartFrame.getChartData();
		Dataset dataset = chartData.getVisible();
		DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
		String newLine = "<br>";

		Rectangle rect = getBounds();
		rect.grow(-2, -2);
		int index = chartData.getIndex(e.getPoint(), rect);
		if (index != -1)
		{
			StringBuilder builder = new StringBuilder();
			Container parent = getParent();
			if (parent instanceof ChartPanel)
			{
				ChartPanel chartPanel = (ChartPanel) parent;
				long time = dataset.getTimeAt(index);
				double open = dataset.getOpenAt(index);
				double high = dataset.getHighAt(index);
				double low = dataset.getLowAt(index);
				double close = dataset.getLowAt(index);
				double volume = dataset.getVolumeAt(index);

				builder.append("<html>");
				builder.append("Date: ")
					.append(chartData.getInterval().getMarkerString(time))
					.append(newLine).append(newLine);
				builder.append("Open: ")
					.append(decimalFormat.format(open))
					.append(newLine);
				builder.append("High: ")
					.append(decimalFormat.format(high))
					.append(newLine);
				builder.append("Low: ")
					.append(decimalFormat.format(low))
					.append(newLine);
				builder.append("Close: ")
					.append(decimalFormat.format(close))
					.append(newLine);
				builder.append("Volume: ")
					.append(decimalFormat.format(volume))
					.append(newLine);

				if (chartPanel.hasOverlays())
				{
					for (Overlay overlay : chartPanel.getOverlays())
					{
						LinkedHashMap map = overlay.getHTML(chartFrame, index);
						Iterator it = map.keySet().iterator();
						while (it.hasNext())
						{
							String key = it.next().toString();
							String value = map.get(key).toString();
							if (value.equals(" "))
							{
								builder.append(newLine);
								builder.append(key).append(newLine);
							} else
							{
								builder.append(key).append(" ")
									.append(value).append(newLine);
							}
						}
					}
				}
				
				builder.append("</html>");
			}
			else
			{
				IndicatorPanel indicatorPanel = (IndicatorPanel) parent;
				Indicator indicator = indicatorPanel.getIndicator();
				long time = dataset.getTimeAt(index);

				builder.append("<html>");
				builder.append("Date: ")
					.append(chartData.getInterval().getMarkerString(time))
					.append(newLine).append(newLine);

				LinkedHashMap map = indicator.getHTML(chartFrame, index);
				Iterator it = map.keySet().iterator();
				while (it.hasNext())
				{
					String key = it.next().toString();
					String value = map.get(key).toString();
					if (value.equals(" "))
					{
						builder.append(key).append(newLine);
					} else
					{
						builder.append(key).append(" ")
							.append(value).append(newLine);
					}
				}
				
				builder.append("</html>");
			}

			setToolTipText(builder.toString());
		}
	}

}
