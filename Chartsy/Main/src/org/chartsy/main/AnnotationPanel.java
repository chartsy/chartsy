package org.chartsy.main;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.swing.JPanel;
import org.chartsy.main.chart.Annotation;
import org.chartsy.main.managers.AnnotationManager;
import org.chartsy.main.utils.Range;

/**
 *
 * @author viorel.gheba
 */
public class AnnotationPanel extends JPanel implements MouseListener, MouseMotionListener, KeyListener, Serializable
{

    private static final long serialVersionUID = 2L;

    public static final int NONE = 0;
    public static final int NEWANNOTATION = 1;
    public static final int RESIZE = 2;
    public static final int MOVE = 3;
    private int state;

    private ChartFrame chartFrame;
    private List<Annotation> annotations;
    private Annotation current = null;

    public AnnotationPanel(ChartFrame frame)
    {
        state = NONE;
        chartFrame = frame;
        annotations = new ArrayList<Annotation>();
        setOpaque(false);

        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
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
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        setDoubleBuffered(true);

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

        g2.setPaintMode();

        g2.setPaint(chartFrame.getChartProperties().getAxisColor());

        for (Annotation a : annotations)
            a.paint(g2);
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

        if (e.getButton() == MouseEvent.BUTTON1)
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
                        repaint();
                    }
                    else
                    {
                        if (!isCurrentNull())
                        {
                            getCurrent().mousePressed(e);
                            repaint();
                        }
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
        if (e.isConsumed())
            e.consume();
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
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
    {}

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

}
