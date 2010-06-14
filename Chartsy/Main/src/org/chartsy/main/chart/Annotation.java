package org.chartsy.main.chart;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.BitSet;
import java.util.Calendar;
import org.chartsy.main.AnnotationPanel;
import org.chartsy.main.ChartFrame;
import org.chartsy.main.data.ChartData;
import org.chartsy.main.intervals.DailyInterval;
import org.chartsy.main.intervals.MonthlyInterval;
import org.chartsy.main.intervals.WeeklyInterval;
import org.chartsy.main.managers.AnnotationManager;
import org.chartsy.main.utils.CoordCalc;
import org.chartsy.main.utils.Range;
import org.chartsy.main.utils.SerialVersion;
import org.openide.nodes.AbstractNode;

/**
 *
 * @author viorel.gheba
 */
public abstract class Annotation implements Serializable, MouseListener, MouseMotionListener
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public static final int NO = 0;
    public static final int YES = 1;
    protected int intraDay;

    public static final int NONE = 0;
    public static final int TOP = 1;
    public static final int TOP_LEFT = 2;
    public static final int TOP_RIGHT = 3;
    public static final int LEFT = 4;
    public static final int RIGHT = 5;
    public static final int BOTTOM = 6;
    public static final int BOTTOM_LEFT = 7;
    public static final int BOTTOM_RIGHT = 8;
    public static final int CENTER = 9;

    protected static final int RESIZE = 0;
    protected static final int MOVE = 1;

    protected int state;
    protected int action;

    protected BitSet actionSet;

    protected double x1, x2;
    protected double y1, y2;
    protected long t1, t2;
    protected double v1, v2;
    protected double x1o, y1o;
    protected long t1o, t2o;
    protected double v1o, v2o;
    protected int index;

    protected Rectangle[] updateRects = new Rectangle[2];
    protected boolean newAnnotation = true;
    protected boolean justAdded = false;
    protected boolean active;
    protected boolean selected;

    protected transient ChartFrame chartFrame;
    protected transient AnnotationPanel annotationPanel;

    public Annotation()
    {
        active = false;
        selected = false;
        actionSet = new BitSet(9);
    }

    public Annotation(ChartFrame frame)
    {
        chartFrame = frame;
        active = false;
        selected = false;
        actionSet = new BitSet(9);
    }

    public String getName()
    { return getClass().getName(); }

    public abstract Annotation newInstance(ChartFrame frame);

    public ChartFrame getChartFrame()
    { return chartFrame; }

    public void setChartFrame(ChartFrame frame)
    { chartFrame = frame; }

    public AnnotationPanel getAnnotationPanel()
    { return annotationPanel; }

    public void setAnnotationPanel(AnnotationPanel panel)
    { annotationPanel = panel; }

    public int isIntraDay() 
    { return intraDay; }

    public void setIntraDay(int i)
    { intraDay = i; }

    public boolean isActive()
    { return active; }

    public void setActive(boolean b)
    { active = b; }

    public boolean isSelected()
    { return selected; }

    public void setSelected(boolean b)
    { selected = b; }

    public boolean isNew()
    { return newAnnotation; }

    public void setNew(boolean b)
    { newAnnotation = b; }

    public int getIndex()
    { return index; }

    public void setIndex(int i)
    { index = i; }

    public long getT1()
    { return t1; }

    public void setT1(long l)
    { t1 = l; }

    public long getT2()
    { return t2; }

    public void setT2(long l)
    { t2 = l; }

    public double getV1()
    { return v1; }

    public void setV1(double d)
    { v1 = d; }

    public double getV2()
    { return v2; }

    public void setV2(double d)
    { v2 = d; }

    public double getX1()
    { return x1; }

    public void setX1(double d)
    { x1 = d; }

    public double getX2()
    { return x2; }

    public void setX2(double d)
    { x2 = d; }

    public double getY1() 
    { return y1; }

    public void setY1(double d)
    { y1 = d; }

    public double getY2() 
    { return y2; }

    public void setY2(double d)
    { y2 = d; }

    protected double getXFromX(double x)
    { 
        Integer idx = null;
        double xc = 0;

        ChartData cd = chartFrame.getChartData();
        Rectangle rect = chartFrame.getSplitPanel().getChartPanel().getBounds();
        rect.grow(-2, -2);

        int items = cd.getPeriod();
        int count = cd.getDataset().getItemsCount();
        int negCount = count - items - (count - cd.getLast());
        int posCount = count - negCount;

        double w = rect.getWidth() / items;
        double h = rect.getHeight();
        double minX = rect.getMinX();

        boolean negative = (x < minX);
        if (negative)
        {
            for (int i = 0; i < negCount; i++)
            {
                Rectangle2D r = CoordCalc.rectangle(minX + (-1 * (i * w)), 0, w, h);
                if (r.contains(x, 0))
                {
                    idx = -1 * i;
                    xc = r.getX() + (w/2);
                    break;
                }
            }

            if (idx == null)
                xc = CoordCalc.rectangle(minX + (-1 * (negCount * w)), 0, w, h).getX() + (w/2);
        }
        else
        {
            for (int i = 0; i < posCount; i++)
            {
                Rectangle2D r = CoordCalc.rectangle(minX + (i * w), 0, w, h);
                if (r.contains(x, 0))
                {
                    idx = i;
                    xc = r.getX() + (w/2);
                    break;
                }
            }

            if (idx == null)
                xc = CoordCalc.rectangle(minX + ((posCount -1 ) * w), 0, w, h).getX() + (w/2);
        }

        return xc;
    }

    protected int getTimeIndex(long t)
    {
        Integer idx = null;
        ChartData cd = chartFrame.getChartData();
        int count = cd.getDataset().getItemsCount();
        if (cd.getInterval() instanceof DailyInterval)
        {
            for (int i = 0; i < count; i++)
            {
                if (cd.getDataset().getTimeAt(i) == t)
                {
                    idx = new Integer(i);
                    break;
                }
            }
        }
        else if (cd.getInterval() instanceof WeeklyInterval)
        {
            Calendar c1 = Calendar.getInstance();
            Calendar c2 = Calendar.getInstance();
            c1.setTimeInMillis(t);
            for (int i = 0; i < count; i++)
            {
                c2.setTimeInMillis(cd.getDataset().getTimeAt(i));
                if (c1.get(Calendar.WEEK_OF_YEAR) == c2.get(Calendar.WEEK_OF_YEAR) && c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR))
                {
                    idx = new Integer(i);
                    break;
                }
            }
        }
        else if (cd.getInterval() instanceof MonthlyInterval)
        {
            Calendar c1 = Calendar.getInstance();
            Calendar c2 = Calendar.getInstance();
            c1.setTimeInMillis(t);
            for (int i = 0; i < count; i++)
            {
                c2.setTimeInMillis(cd.getDataset().getTimeAt(i));
                if (c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) && c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR))
                {
                    idx = new Integer(i);
                    break;
                }
            }
        }
        else
        {
            for (int i = 0; i < count; i++)
            {
                if (t == cd.getDataset().getTimeAt(i))
                {
                    idx = new Integer(i);
                    break;
                }
            }
        }

        if (idx == null)
        {
            long min = cd.getDataset().getTimeAt(0);
            long max = cd.getDataset().getLastTime();

            if (t < min)
                idx = new Integer(0);
            if (t > max)
                idx = count - 1;
        }

        return idx;
    }

    protected double getXFromTime(long t)
    {
        double xc = 0;

        Integer idx = getTimeIndex(t);
        ChartData cd = chartFrame.getChartData();
        Rectangle rect = chartFrame.getSplitPanel().getChartPanel().getBounds();
        rect.grow(-2, -2);
        if (idx != null) {
            int items = cd.getPeriod();
            int end = cd.getLast();

            int itemsCount = cd.getDataset().getItemsCount();
            int negativeNr = itemsCount - items - (itemsCount - end);

            double w = rect.getWidth() / items;
            double h = rect.getHeight();
            double minX = rect.getMinX();

            boolean negative = (idx < negativeNr);

            if (negative)
                xc = CoordCalc.rectangle(minX + ((idx - negativeNr) * w), 0, w, h).getX() + (w/2);
            else
                xc = CoordCalc.rectangle(minX + ((idx - negativeNr) * w), 0, w, h).getX() + (w/2);
        }

        return xc;
    }

    protected long getTimeFromX(double x)
    {
        Integer idx = null;
        ChartData cd = chartFrame.getChartData();
        Rectangle rect = chartFrame.getSplitPanel().getChartPanel().getBounds();
        rect.grow(-2, -2);
        int items = cd.getPeriod();
        int end = cd.getLast();
        int count = cd.getDataset().getItemsCount();
        int negativeNr = count - items - (count - end);
        int positiveNr = count - negativeNr;
        double w = rect.getWidth() / items;
        double h = rect.getHeight();
        double minX = rect.getMinX();

        boolean negative = (x < minX);
        if (negative)
        {
            for (int i = 0; i < negativeNr; i++)
            {
                if (CoordCalc.rectangle(minX + (-1 * (i * w)), 0, w, h).contains(x, 0))
                {
                    idx = negativeNr - i;
                    break;
                }
            }

            if (idx == null)
                idx = 0;
        }
        else
        {
            for (int i = 0; i < positiveNr; i++)
            {
                if (CoordCalc.rectangle(minX + (i * w), 0, w, h).contains(x, 0))
                {
                    idx = negativeNr + i;
                    break;
                }
            }

            if (idx == null)
                idx = count - 1;
        }

        return chartFrame.getChartData().getDataset().getTimeAt(idx);
    }

    protected double getYFromValue(double v)
    {
        Rectangle rect = chartFrame.getSplitPanel().getChartPanel().getBounds();
        rect.grow(-2, -2);
        Range range = getAnnotationPanel().getRange();
        
        double dif = range.getUpperBound() - range.getLowerBound();
        double percent = ((range.getUpperBound() - v) / dif) * 100;
        double py = rect.getMinY() + (rect.getHeight() * percent) / 100;

        if (range.getLowerBound() <= 0.0)
        {
            dif = Math.abs(range.getUpperBound()) + Math.abs(range.getLowerBound());
            double h1 = (Math.abs(range.getUpperBound()) * rect.getHeight()) / dif;
            double h2 = (Math.abs(range.getLowerBound()) * rect.getHeight()) / dif;

            if (v >= 0)
            {
                percent = ((range.getUpperBound() - v) / range.getUpperBound()) * 100;
                py = rect.getMinY() + (h1 * percent) / 100;
            }
            else
            {
                percent = ((Math.abs(range.getLowerBound()) - Math.abs(v)) / Math.abs(range.getLowerBound())) * 100;
                py = rect.getMinY() + h1 + (h2 - ((h2 * percent) / 100));
            }
        }

        return py;
    }

    protected double getValueFromY(double y)
    {
        Rectangle bounds = chartFrame.getSplitPanel().getChartPanel().getBounds();
        bounds.grow(-2, -2);
        Range range = getAnnotationPanel().getRange();
        
        double dif = range.getUpperBound() - range.getLowerBound();
        double percent = (y - bounds.getMinY()) / bounds.getHeight();
        double value = range.getUpperBound() - (percent * dif);

        if (range.getLowerBound() <= 0.0)
        {
            dif = Math.abs(range.getUpperBound()) + Math.abs(range.getLowerBound());
            double zero = getYFromValue(0);
            double h1 = (Math.abs(range.getUpperBound()) * bounds.getHeight()) / dif;
            double h2 = (Math.abs(range.getLowerBound()) * bounds.getHeight()) / dif;

            if (y < zero)
            { // positiv
                percent = 100 * (y - bounds.getMinY()) / h1;
                value = range.getUpperBound() - ((percent / 100) * range.getUpperBound());
            }
            else
            { // negative
                percent = 100 * (h2 - y + bounds.getMinY() + h1) / h2;
                value = Math.abs(range.getLowerBound()) - (percent * Math.abs(range.getLowerBound()) / 100);
                value = value > 0 ? value * (-1) : value;
            }
        }

        return value;
    }

    protected boolean hasNext(long t)
    { int items = chartFrame.getChartData().getDataset().getItemsCount();
      return (getTimeIndex(t) + 1) < items; }

    protected boolean hasPrev(long t)
    { return (getTimeIndex(t) - 0) > 0; }

    protected long getNext(long t)
    { int i = getTimeIndex(t);
      return chartFrame.getChartData().getDataset().getTimeAt(i + 1); }

    protected long getPrev(long t)
    { int i = getTimeIndex(t);
      return chartFrame.getChartData().getDataset().getTimeAt(i - 1); }

    protected void setArea(long t1, double v1, long t2, double v2)
    { setP1(t2, v1);
      setP2(t2, v2); }

    protected Rectangle getArea()
    {
        Rectangle area = new Rectangle();
        double X1 = getXFromTime(getT1()), X2 = getXFromTime(getT2());
        double Y1 = getYFromValue(getV1()), Y2 = getYFromValue(getV2());
        area.setFrameFromDiagonal(X1, Y1, X2, Y2);
        return area;
    }

    protected void setP1(long l, double d)
    { setT1(l);
      setV1(d); }

    protected Point2D getP1()
    { double x = getXFromTime(getT1()), y = getYFromValue(getV1());
      return new Point2D.Double(x, y); }

    protected void setP2(long l, double d)
    { setT2(l);
      setV2(d); }

    protected Point2D getP2()
    { double x = getXFromTime(getT2()), y = getYFromValue(getV2());
      return new Point2D.Double(x, y); }

    protected Point2D getPoint(long l, double v)
    { return new Point2D.Double(getXFromTime(l), getYFromValue(v)); }

    public void initialize(double x1, double y1, double x2, double y2)
    {
        setActive(true);
        setSelected(true);
        setArea(getTimeFromX((int)x1), getValueFromY(y1), getTimeFromX((int)x2), getValueFromY(y2));
        state = RESIZE;
        action = BOTTOM_RIGHT;
    }

    public void initializeX(double x1, double x2)
    {
        setActive(true);
        setSelected(true);
        setArea(getTimeFromX((int)x1), getV1(), getTimeFromX((int)x2), getV2());
        state = RESIZE;
        action = BOTTOM_RIGHT;
    }

    public void initializeY(double y1, double y2)
    {
        setActive(true);
        setSelected(true);
        setArea(getT1(), getValueFromY(y1), getT2(), getValueFromY(y2));
        state = RESIZE;
        action = BOTTOM_RIGHT;
    }

    public Rectangle[] getUpdateRectangles()
    {
        int size = 0;
        if ((updateRects[1] != null) && (updateRects[0] != null))
        {
            size = 2;
        }
        else
        {
            if (updateRects[0] != null)
            {
                size = 1;
            }
        }

        if (size == 0)
            return null;

        Rectangle[] r = new Rectangle[size];
        for (int i = 0; i < size; i++)
            r[i] = updateRects[i];

        return r;
    }

    protected void updateRectangles(long oldT1, long newT1, long oldT2, long newT2, double oldV1, double newV1, double oldV2, double newV2)
    { updateRects[0] = null; }

    protected Rectangle getCurrentRectangle(boolean extraPoints)
    {
        Rectangle r = new Rectangle();
        r.setFrameFromDiagonal(getP1(), getP2());
        if (extraPoints)
            r.grow(3, 3);
        return r;
    }

    public void moveDown()
    {
        double dy1 = getYFromValue(getV1()) + 10D, dy2 = getYFromValue(getV2()) + 10D;
        setV1(getValueFromY(dy1));
        setV2(getValueFromY(dy2));
    }

    public void moveUp()
    {
        double Y1 = getYFromValue(getV1()) - 10D, Y2 = getYFromValue(getV2()) - 10D;
        setV1(getValueFromY(Y1));
        setV2(getValueFromY(Y2));
    }

    public void moveLeft()
    {
        if (getT1() < getT2() ? hasPrev(getT1()) : hasPrev(getT2()))
        {
            setT1(getPrev(getT1()));
            setT2(getPrev(getT2()));
        }
    }

    public void moveRight()
    {
        if (getT1() > getT2() ? hasNext(getT1()) : hasNext(getT2()))
        {
            setT1(getNext(getT1()));
            setT2(getNext(getT2()));
        }
    }

    public boolean updatePosition(double x, double y)
    {
        switch (state) {
            case RESIZE:
                switch (action) {
                    case TOP:
                        return updateTop(x, y);
                    case TOP_LEFT:
                        return updateTopLeft(x, y);
                    case TOP_RIGHT:
                        return updateTopRight(x, y);
                    case LEFT:
                        return updateLeft(x, y);
                    case RIGHT:
                        return updateRight(x, y);
                    case BOTTOM:
                        return updateBottom(x, y);
                    case BOTTOM_LEFT:
                        return updateBottomLeft(x, y);
                    case BOTTOM_RIGHT:
                        return updateBottomRight(x, y);
                }
                break;
            case MOVE:
                return updateMove(x, y);
        }
        return false;
    }

    public boolean updateTop(double x, double y)
    {
        double v = getValueFromY(y);
        if (v != getV1())
        {
            double oldV1 = getV1();
            setV1(v);
            updateRectangles(getT1(), getT1(), getT2(), getT2(), oldV1, getV1(), getV2(), getV2());
            return true;
        }
        return false;
    }

    public boolean updateTopLeft(double x, double y)
    {
        long t = getTimeFromX((int)x);
        double v = getValueFromY(y);
        if (getT1() != t || getV1() != v)
        {
            long oldT1 = getT1();
            double oldV1 = getV1();
            setT1(t);
            setV1(v);
            updateRectangles(oldT1, getT1(), getT2(), getT2(), oldV1, getV1(), getV2(), getV2());
            return true;
        }
        return false;
    }

    public boolean updateTopRight(double x, double y)
    {
        long t = getTimeFromX((int)x);
        double v = getValueFromY(y);
        if (t != getT2() || v != getV1())
        {
            long oldT2 = getT2();
            double oldV1 = getV1();
            setT2(t);
            setV1(v);
            updateRectangles(getT1(), getT1(), oldT2, getT2(), oldV1, getV1(), getV2(), getV2());
            return true;
        }
        return false;
    }

    public boolean updateLeft(double x, double y)
    {
        long t = getTimeFromX((int)x);
        if (getT1() != t)
        {
            long oldT1 = getT1();
            setT1(t);
            updateRectangles(oldT1, getT1(), getT2(), getT2(), getV1(), getV1(), getV2(), getV2());
            return true;
        }
        return false;
    }

    public boolean updateRight(double x, double y)
    {
        long t = getTimeFromX((int)x);
        if (getT2() != t)
        {
            long oldT2 = getT2();
            setT2(t);
            updateRectangles(getT1(), getT1(), oldT2, getT2(), getV1(), getV1(), getV2(), getV2());
            return true;
        }
        return false;
    }

    public boolean updateBottom(double x, double y)
    {
        double v = getValueFromY(y);
        if (v != getV2())
        {
            double oldV2 = getV2();
            setV2(v);
            updateRectangles (getT1(), getT1(), getT2(), getT2(), getV1(), getV1(), oldV2, getV2());
            return true;
        }
        return false;
    }

    public boolean updateBottomLeft(double x, double y)
    {
        long t = getTimeFromX((int)x);
        double v = getValueFromY(y);
        if (t != getT1() || v != getV2())
        {
            long oldT1 = getT1();
            double oldV2 = getV2();
            setT1(t);
            setV2(v);
            updateRectangles (oldT1, getT1(), getT2(), getT2(), getV1(), getV1(), oldV2, getV2());
            return true;
        }
        return false;
    }

    public boolean updateBottomRight(double x, double y)
    {
        long t = getTimeFromX((int)x);
        double v = getValueFromY(y);
        if (t != getT2() || v != getV2())
        {
            long oldT2 = getT2();
            double oldV2 = getV2();
            setT2(t);
            setV2(v);
            updateRectangles (getT1(), getT1(), oldT2, getT2(), getV1(), getV1(), oldV2, getV2());
            return true;
        }
        return false;
    }

    public boolean updateMove(double x, double y)
    {
        boolean ok = false;
        double X1 = getXFromTime(t1o);
        double X2 = getXFromTime(t2o);
        double Y1 = getYFromValue(v1o);
        double Y2 = getYFromValue(v2o);
        double dx = x - x1o;
        double dy = y - y1o;
        long oldT1 = getT1();
        long oldT2 = getT2();
        double oldV1 = getV1();
        double oldV2 = getV2();
        if (dx != 0)
        {
            if ((dx > 0) ? (getT1() > getT2() ? hasNext(getT1()) : hasNext(getT2())) : (getT1() > getT2() ? hasPrev(getT2()) : hasPrev(getT1())))
            {
                X1 += dx;
                X2 += dx;
                setT1(getTimeFromX((int)X1));
                setT2(getTimeFromX((int)X2));
                ok = true;
            }
        }
        if (dy != 0)
        {
            Y1 += dy;
            Y2 += dy;
            setV1(getValueFromY(Y1));
            setV2(getValueFromY(Y2));
            ok = true;
        }
        if (ok)
            updateRectangles(oldT1, getT1(), oldT2, getT2(), oldV1, getV1(), oldV2, getV2());
        return false;
    }

    public void initializePress(double x1, double y1)
    {
        setSelected(true);
        x1o = x1;
        y1o = y1;
        t1o = getT1();
        t2o = getT2();
        v1o = getV1();
        v2o = getV2();
        action = getActionPoint(x1, y1);

        if (action == NONE)
            state = MOVE;
        else
            state = RESIZE;
    }

    protected boolean inRezise()
    { return (state == RESIZE); }

    protected int getActionPoint(double x, double y)
    {
        double X1 = getXFromTime(getT1()), X2 = getXFromTime(getT2()), Xc = (X1 + X2) / 2;
        double Y1 = getYFromValue(getV1()), Y2 = getYFromValue(getV2()), Yc = (Y1 + Y2) / 2;
        if (actionSet.get(TOP) && Math.abs(x - Xc) < 5 && Math.abs(y - Y1) < 5)
            return TOP;
        if (actionSet.get(TOP_LEFT) && Math.abs(x - X1) < 5 && Math.abs(y - Y1) < 5)
            return TOP_LEFT;
        if (actionSet.get(TOP_RIGHT) && Math.abs(x - X2) < 5 && Math.abs(y - Y1) < 5)
            return TOP_RIGHT;
        if (actionSet.get(LEFT) && Math.abs(x - X1) < 5 && Math.abs(y - Yc) < 5)
            return LEFT;
        if (actionSet.get(RIGHT) &&  Math.abs(x - X2) < 5 && Math.abs(y - Yc) < 5)
            return RIGHT;
        if (actionSet.get(BOTTOM) && Math.abs(x - Xc) < 5 && Math.abs(y - Y2) < 5)
            return BOTTOM;
        if (actionSet.get(BOTTOM_LEFT) && Math.abs(x - X1) < 5 && Math.abs(y - Y2) < 5)
            return BOTTOM_LEFT;
        if (actionSet.get(BOTTOM_RIGHT) && Math.abs(x - X2) < 5 && Math.abs(y - Y2) < 5)
            return BOTTOM_RIGHT;
        return NONE;
    }

    protected void paintActionPoints(Graphics2D g)
    {
        double X1 = getXFromTime(getT1()), X2 = getXFromTime(getT2());
        double Y1 = getYFromValue(getV1()), Y2 = getYFromValue(getV2());
        g.setPaint(Color.BLACK);
        if (actionSet.get(TOP))
        {
            if (state == RESIZE && action == TOP)
                g.fill(CoordCalc.rectangle((X1 + X2) / 2 - 2, Y1 - 2, 4, 4));
            else
                g.draw(CoordCalc.rectangle((X1 + X2) / 2 - 2, Y1 - 2, 4, 4));
        }
        if (actionSet.get(TOP_LEFT))
        {
            if (state == RESIZE && action == TOP_LEFT)
                g.fill(CoordCalc.rectangle(X1 - 2, Y1 - 2, 4, 4));
            else
                g.draw(CoordCalc.rectangle(X1 - 2, Y1 - 2, 4, 4));
        }
        if (actionSet.get(TOP_RIGHT))
        {
            if (state == RESIZE && action == TOP_RIGHT)
                g.fill(CoordCalc.rectangle(X2 - 2, Y1 - 2, 4, 4));
            else
                g.draw(CoordCalc.rectangle(X2 - 2, Y1 - 2, 4, 4));
        }
        if (actionSet.get(LEFT))
        {
            if (state == RESIZE && action == LEFT)
                g.fill(CoordCalc.rectangle(X1 - 2, (Y1 + Y2) / 2 - 2, 4, 4));
            else
                g.draw(CoordCalc.rectangle(X1 - 2, (Y1 + Y2) / 2, 4, 4));
        }
        if (actionSet.get(RIGHT))
        {
            if (state == RESIZE && action == RIGHT)
                g.fill(CoordCalc.rectangle(X2 - 2, (Y1 + Y2) / 2 - 2, 4, 4));
            else 
                g.draw(CoordCalc.rectangle(X2 - 2, (Y1 + Y2) / 2, 4, 4));
        }
        if (actionSet.get(BOTTOM))
        {
            if (state == RESIZE && action == BOTTOM)
                g.fill(CoordCalc.rectangle((X1 + X2) / 2 - 2, Y2 - 2, 4, 4));
            else 
                g.draw(CoordCalc.rectangle((X1 + X2) / 2 - 2, Y2 - 2, 4, 4));
        }
        if (actionSet.get(BOTTOM_LEFT))
        {
            if (state == RESIZE && action == BOTTOM_LEFT)
                g.fill(CoordCalc.rectangle(X1 - 2, Y2 - 2, 4, 4));
            else 
                g.draw(CoordCalc.rectangle(X1 - 2, Y2 - 2, 4, 4));
        }
        if (actionSet.get(BOTTOM_RIGHT))
        {
            if (state == RESIZE && action == BOTTOM_RIGHT)
                g.fill(CoordCalc.rectangle(X2 - 2, Y2 - 2, 4, 4));
            else 
                g.draw(CoordCalc.rectangle(X2 - 2, Y2 - 2, 4, 4));
        }
    }

    public void addUpdateRectangle(Rectangle rect, boolean b)
    {
        int i = b ? 1 : 0;
        if (updateRects[i] == null)
            updateRects[i] = rect;
        else
            updateRects[index] = updateRects[index].union(rect);
    }

    public void cumulateUpdateArea()
    {
        if (updateRects[0] != null && updateRects[1] != null)
        {
            updateRects[0] = updateRects[0].union(updateRects[1]);
            updateRects[1] = null;
        }
    }

    public void updateArea(AnnotationPanel panel)
    {
        if (updateRects[1] != null) 
            updateRects[1] = null;

        if (updateRects[0] != null)
        {
            panel.paintImmediately(updateRects[0]);
            updateRects[0] = null;
        } 
        else
        {
            panel.repaint();
        }
    }

    public boolean updateAreaContains(double x, double y, boolean second)
    {
        int idx = second ? 1 : 0;
        if (updateRects[idx] != null)
            return updateRects[idx].contains(x, y);
        return false;
    }

    public boolean isInBounds(double x, double y)
    { return annotationPanel.getBounds().contains(x, y); }

    protected boolean isFlipped()
    { return (getY1() < getY2()); }

    protected boolean isMirrored()
    { return (getX1() > getX2()); }

    public boolean contains(double x, double y)
    { return getArea().contains(x, y); }

    protected Rectangle getRectangle()
    { return getRectangle(getT1(), getV1(), getT2(), getV2()); }

    protected Rectangle getRectangle(long t1, double v1, long t2, double v2)
    { Rectangle r = new Rectangle();
      r.setFrameFromDiagonal(getPoint(t1, v1), getPoint(t2, v2));
      return r; }

    public boolean lineContains(double x1, double y1, double x2, double y2, double x, double y, int dx, int dy)
    {
        Rectangle r = new Rectangle();
        r.setFrameFromDiagonal(x1, y1, x2, y2);
        r.grow(dx, dy);
        if (r.contains(x, y))
        {
            if ((x1 == x2) || (y1 == y2)) return true;
            double dX = x2 - x1;
            double dY = y2 - y1;
            double ddx = x - x1;
            double ddy = y - y1;
            double D = dX * dX + dY * dY;
            if (D <= 0) return (ddx * ddx + ddy * ddy < dx * dy);
            double d = ddx * dY - dX * ddy;
            d = d * d / D;
            return (d < dx * dy);
        }
        else
            return false;
    }

    public boolean lineContains(double x1, double y1, double x2, double y2, double x, double y, int d)
    { return lineContains(x1, y1, x2, y2, x, y, d, d); }

    public boolean lineContains(Point2D p1, Point2D p2, Point2D p, int d)
    { return lineContains(p1.getX(), p1.getY(), p2.getX(), p2.getY(), p.getX(), p.getY(), d, d); }

    public boolean lineContains(Point2D p1, Point2D p2, Point2D p, int dx, int dy)
    { return lineContains(p1.getX(), p1.getY(), p2.getX(), p2.getY(), p.getX(), p.getY(), dx, dy); }

    public abstract boolean pointIntersects(double x, double y);

    public abstract void paint(Graphics2D g);

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof Annotation)) return false;
        Annotation that = (Annotation) obj;
        if (!getClass().getName().equals(that.getClass().getName())) return false;
        if (getT1() != that.getT1()) return false;
        if (getT2() != that.getT2()) return false;
        if (getV1() != that.getV1()) return false;
        if (getV2() != that.getV2()) return false;
        return true;
    }

    public abstract AbstractNode getNode();

    public void mouseEntered(MouseEvent e)
    {}

    public void mouseExited(MouseEvent e)
    {}

    public void mouseClicked(MouseEvent e)
    {}

    public void mouseMoved(MouseEvent e)
    {}
    
    public void mousePressed(MouseEvent e)
    {
        if (e.getButton() == MouseEvent.BUTTON1)
        {
            Annotation current = getAnnotationPanel().getCurrent();
            getAnnotationPanel().deselectAll();
            int x = e.getX(), y = e.getY();
            if (current != null && current.isInBounds(x, y))
            {
                getAnnotationPanel().setCurrent(current);
                switch (getAnnotationPanel().getState())
                {
                    case AnnotationPanel.NONE:
                        current.setSelected(true);
                        current.initializePress(getXFromX(x), y);
                        if (state == MOVE)
                        {
                            getAnnotationPanel().setState(AnnotationPanel.MOVE);
                            getAnnotationPanel().mousePressed(e);
                        }
                        if (state == RESIZE)
                        {
                            getAnnotationPanel().setState(AnnotationPanel.RESIZE);
                            getAnnotationPanel().mousePressed(e);
                        }
                        break;
                    case AnnotationPanel.NEWANNOTATION:
                        getAnnotationPanel().addAnnotation(current);
                        current.initialize(x, y, x, y);
                        break;
                    case AnnotationPanel.RESIZE:
                        current.setSelected(true);
                        state = RESIZE;
                        break;
                    case AnnotationPanel.MOVE:
                        current.setSelected(true);
                        state = MOVE;
                        break;
                }
            }
        }
    }

    public void mouseDragged(MouseEvent e)
    {
        Annotation current = getAnnotationPanel().getCurrent();
        if (current != null)
        {
            int x = e.getX(), y = e.getY();
            if (current.isInBounds(x, y))
            {
                if (current.updatePosition(x, y))
                    current.updateArea(getAnnotationPanel());
            }
        }
        getAnnotationPanel().repaint();
    }

    public void mouseReleased(MouseEvent e)
    {
        Annotation current = getAnnotationPanel().getCurrent();
        if (current != null && current.isNew())
            current.setNew(false);

        AnnotationManager.getDefault().clearNewAnnotation();
        getAnnotationPanel().setState(AnnotationPanel.NONE);
        getAnnotationPanel().repaint();
    }

}
