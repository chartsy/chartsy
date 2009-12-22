package org.chartsy.main.chartsy.chart;

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
import org.chartsy.main.chartsy.ChartFrame;
import org.chartsy.main.chartsy.ChartPanel;
import org.chartsy.main.managers.AnnotationManager;
import org.chartsy.main.utils.Range;
import org.openide.nodes.AbstractNode;

/**
 *
 * @author viorel.gheba
 */
public abstract class Annotation implements MouseListener, MouseMotionListener, Serializable {

    private static final long serialVersionUID = 101L;

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
    protected int inflection;

    protected BitSet inflectionSet;

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

    protected transient ChartFrame cf;
    protected int areaIndex;
    protected Range range;
    protected transient Rectangle2D.Double bounds;

    public Annotation(ChartFrame chartFrame) {
        cf = chartFrame;
        active = false;
        selected = false;
        inflectionSet = new BitSet(9);
    }

    public String getIdentifier() { return getClass().getName(); }

    public int isIntraDay() { return intraDay; }
    public void setIntraDay(int i) { intraDay = i; }

    public ChartFrame getChartFrame() { return cf; }
    public void setChartFrame(ChartFrame c) { cf = c; }

    public int getIndex() { return index; }
    public void setIndex(int i) { index = i; }

    public int getAreaIndex() { return areaIndex; }
    public void setAreaIndex(int i) { areaIndex = i; }

    public Range getRange() { return range; }
    public void setRange(Range r) { range = r; }

    public Rectangle2D.Double getBounds() { return bounds; }
    public void setBounds(Rectangle2D.Double b) { bounds = b; }

    public boolean isActive() { return active; }
    public void setActive(boolean b) { active = b; }

    public boolean isSelected() { return selected; }
    public void setSelected(boolean b) { selected = b; }

    public boolean isNew() { return newAnnotation; }
    public void setNew(boolean b) { newAnnotation = b; }

    public boolean justAdded() { return justAdded; }
    public void setJustAdded(boolean b) { justAdded = b; }

    public long getT1() { return t1; }
    public void setT1(long t) { t1 = t; setX1(getXCoord(t)); }

    public long getT2() { return t2; }
    public void setT2(long t) { t2 = t; setX2(getXCoord(t)); }

    public double getV1() { return v1; }
    public void setV1(double v) { v1 = v; setY1(getYCoord(v)); }
    
    public double getV2() { return v2; }
    public void setV2(double v) { v2 = v; setY2(getYCoord(v)); }

    public double getX1() { return x1; }
    public void setX1(double x) { x1 = x; }

    public double getX2() { return x2; }
    public void setX2(double x) { x2 = x; }

    public double getY1() { return y1; }
    public void setY1(double y) { y1 = y; }
    
    public double getY2() { return y2; }
    public void setY2(double y) { y2 = y; }

    protected double getX(double x) { return cf.getChartRenderer().getX(x); }
    protected double getXCoord(long t) { return cf.getChartRenderer().longToX(t); }
    protected long getXLong(double x) { return cf.getChartRenderer().xToLong(x); }

    protected double getYCoord(double v) { return cf.getChartRenderer().valueToY(v, bounds, range); }
    protected double getYValue(double y) { return cf.getChartRenderer().yToValue(y, bounds, range); }

    protected void setArea(long t1, double v1, long t2, double v2) { setP1(t1, v1); setP2(t2, v2); }
    protected Rectangle getArea() {
        double X1 = getXCoord(getT1()), X2 = getXCoord(getT2());
        double Y1 = getYCoord(getV1()), Y2 = getYCoord(getV2());
        Rectangle r = new Rectangle(); r.setFrameFromDiagonal(X1, Y1, X2, Y2);
        return r;
    }

    protected void setP1(long t, double v) { setT1(t); setV1(v); }
    protected Point2D.Double getP1() {
        double x = getXCoord(getT1()), y = getYCoord(getV1());
        return new Point2D.Double(x, y);
    }

    protected void setP2(long t, double v) { setT2(t); setV2(v); }
    protected Point2D.Double getP2() {
        double x = getXCoord(getT2()), y = getYCoord(getV2());
        return new Point2D.Double(x, y);
    }

    protected Point2D.Double getPoint(double x, double y) { return new Point2D.Double(getX(x), y); }
    protected Point2D.Double getPoint(long t, double v) { return new Point2D.Double(getXCoord(t), getYCoord(v)); };

    public void initialize(double x1, double y1, double x2, double y2) {
        setActive(true);
        setSelected(true);
        setArea(getXLong(x1), getYValue(y1), getXLong(x2), getYValue(y2));
        state = RESIZE;
        inflection = BOTTOM_RIGHT;
    }

    public void initializeX(double x1, double x2) {
        setActive(true);
        setSelected(true);
        setArea(getXLong(x1), getV1(), getXLong(x2), getV2());
        state = RESIZE;
        inflection = BOTTOM_RIGHT;
    }

    public void initializeY(double y1, double y2) {
        setActive(true);
        setSelected(true);
        setArea(getT1(), getYValue(y1), getT2(), getYValue(y2));
        state = RESIZE;
        inflection = BOTTOM_RIGHT;
    }

    public Rectangle[] getUpdateRectangles() {
        int size = (((updateRects[1] != null) && (updateRects[0] != null)) ? 2 : ((updateRects[0] != null) ? 1 : 0));
        if (size == 0) return null;
        Rectangle[] r = new Rectangle[size];
        for (int i = 0; i < size; i++) r[i] = updateRects[i];
        return r;
    }

    protected void updateRectangles(long oldT1, long newT1, long oldT2, long newT2, double oldV1, double newV1, double oldV2, double newV2) { updateRects[0] = null; }

    protected Rectangle getCurrentRectangle(boolean extraPoints) {
        Point2D.Double p1 = getPoint(getT1(), getV1());
        Point2D.Double p2 = getPoint(getT2(), getV2());
        Rectangle r = new Rectangle(); r.setFrameFromDiagonal(p1, p2);
        if (extraPoints) r.grow(3, 3);
        return r;
    }

    protected boolean hasPrevT(long t) { return cf.getChartRenderer().hasPrevT(t); }
    protected long getPrevT(long t) { return cf.getChartRenderer().getPrevT(t); }
    protected boolean hasNextT(long t) { return cf.getChartRenderer().hasNext(t); }
    protected long getNextT(long t) { return cf.getChartRenderer().getNextT(t); }

    public void moveDown() {
        double dy1 = getYCoord(getV1()) + 10, dy2 = getYCoord(getV2()) + 10;
        setV1(getYValue(dy1)); setV2(getYValue(dy2));
    }

    public void moveUp() {
        double dy1 = getYCoord(getV1()) - 10, dy2 = getYCoord(getV2()) - 10;
        setV1(getYValue(dy1)); setV2(getYValue(dy2));
    }

    public void moveLeft() {
        if (getT1() < getT2() ? hasPrevT(getT1()) : hasPrevT(getT2())) {
            long oldT1 = getT1(), oldT2 = getT2();
            long newT1 = getPrevT(oldT1), newT2 = getPrevT(oldT2);
            setT1(newT1); setT2(newT2);
        }
    }

    public void moveRight() {
        if (getT1() > getT2() ? hasNextT(getT1()) : hasNextT(getT2())) {
            long oldT1 = getT1(), oldT2 = getT2();
            long newT1 = getNextT(oldT1), newT2 = getNextT(oldT2);
            setT1(newT1); setT2(newT2);
        }
    }

    public boolean updatePosition(double x, double y) {
        switch (state) {
            case RESIZE:
                switch (inflection) {
                    case TOP: return updateTop(x, y);
                    case TOP_LEFT: return updateTopLeft(x, y);
                    case TOP_RIGHT: return updateTopRight(x, y);
                    case LEFT: return updateLeft(x, y);
                    case RIGHT: return updateRight(x, y);
                    case BOTTOM: return updateBottom(x, y);
                    case BOTTOM_LEFT: return updateBottomLeft(x, y);
                    case BOTTOM_RIGHT: return updateBottomRight(x, y);
                }
                break;
            case MOVE: return updateMove(x, y);
        }
        return false;
    }

    public boolean updateTop(double x, double y) {
        double v = getYValue(y);
        if (v != getV1()) {
            double oldV1 = getV1();
            setV1(v);
            updateRectangles(getT1(), getT1(), getT2(), getT2(), oldV1, getV1(), getV2(), getV2());
            return true;
        }
        return false;
    }

    public boolean updateTopLeft(double x, double y) {
        long t = getXLong(x);
        double v = getYValue(y);
        if (getT1() != t || getV1() != v) {
            long oldT1 = getT1(); double oldV1 = getV1();
            setT1(t); setV1(v);
            updateRectangles(oldT1, getT1(), getT2(), getT2(), oldV1, getV1(), getV2(), getV2());
            return true;
        }
        return false;
    }

    public boolean updateTopRight(double x, double y) {
        long t = getXLong(x);
        double v = getYValue(y);
        if (t != getT2() || v != getV1()) {
            long oldT2 = getT2(); double oldV1 = getV1();
            setT2(t); setV1(v);
            updateRectangles(getT1(), getT1(), oldT2, getT2(), oldV1, getV1(), getV2(), getV2());
            return true;
        }
        return false;
    }

    public boolean updateLeft(double x, double y) {
        long t = getXLong(x);
        if (getT1() != t) {
            long oldT1 = getT1();
            setT1(t);
            updateRectangles(oldT1, getT1(), getT2(), getT2(), getV1(), getV1(), getV2(), getV2());
            return true;
        }
        return false;
    }

    public boolean updateRight(double x, double y) {
        long t = getXLong(x);
        if (getT2() != t) {
            long oldT2 = getT2();
            setT2(t);
            updateRectangles(getT1(), getT1(), oldT2, getT2(), getV1(), getV1(), getV2(), getV2());
            return true;
        }
        return false;
    }

    public boolean updateBottom(double x, double y) {
        double v = getYValue(y);
        if (v != getV2()) {
            double oldV2 = getV2();
            setV2(v);
            updateRectangles (getT1(), getT1(), getT2(), getT2(), getV1(), getV1(), oldV2, getV2());
            return true;
        }
        return false;
    }

    public boolean updateBottomLeft(double x, double y) {
        long t = getXLong(x);
        double v = getYValue(y);
        if (t != getT1() || v != getV2()) {
            long oldT1 = getT1(); double oldV2 = getV2();
            setT1(t); setV2(v);
            updateRectangles (oldT1, getT1(), getT2(), getT2(), getV1(), getV1(), oldV2, getV2());
            return true;
        }
        return false;
    }

    public boolean updateBottomRight(double x, double y) {
        long t = getXLong(x);
        double v = getYValue(y);
        if (t != getT2() || v != getV2()) {
            long oldT2 = getT2(); double oldV2 = getV2();
            setT2(t); setV2(v);
            updateRectangles (getT1(), getT1(), oldT2, getT2(), getV1(), getV1(), oldV2, getV2());
            return true;
        }
        return false;
    }

    public boolean updateMove(double x, double y) {
        boolean ok = false;
        double X1 = getXCoord(t1o);
        double X2 = getXCoord(t2o);
        double Y1 = getYCoord(v1o);
        double Y2 = getYCoord(v2o);
        double dx = x - x1o;
        double dy = y - y1o;
        long oldT1 = getT1();
        long oldT2 = getT2();
        double oldV1 = getV1();
        double oldV2 = getV2();
        if (dx != 0) {
            if ((dx > 0) ? (getT1() > getT2() ? hasNextT(getT1()) : hasNextT(getT2())) : (getT1() > getT2() ? hasPrevT(getT2()) : hasPrevT(getT1()))) {
                X1 += dx; X2 += dx;
                setT1(getXLong(X1)); setT2(getXLong(X2));
                ok = true;
            }
        }
        if (dy != 0) {
            Y1 += dy; Y2 += dy;
            setV1(getYValue(Y1)); setV2(getYValue(Y2));
            ok = true;
        }
        if (ok) updateRectangles(oldT1, getT1(), oldT2, getT2(), oldV1, getV1(), oldV2, getV2());
        return false;
    }

    public void initializePress(double x1, double y1) {
        setSelected(true);
        x1o = x1;
        y1o = y1;
        t1o = getT1();
        t2o = getT2();
        v1o = getV1();
        v2o = getV2();
        inflection = getInflectionPoint(x1, y1);
        if (inflection == NONE) state = MOVE;
        else state = RESIZE;
    }

    protected boolean inRezise() { return (state == RESIZE); }

    protected int getInflectionPoint(double x, double y) {
        double X1 = getXCoord(getT1()), X2 = getXCoord(getT2()), Xc = (X1 + X2) / 2;
        double Y1 = getYCoord(getV1()), Y2 = getYCoord(getV2()), Yc = (Y1 + Y2) / 2;
        if (inflectionSet.get(TOP) && Math.abs(x - Xc) < 5 && Math.abs(y - Y1) < 5)
            return TOP;
        if (inflectionSet.get(TOP_LEFT) && Math.abs(x - X1) < 5 && Math.abs(y - Y1) < 5)
            return TOP_LEFT;
        if (inflectionSet.get(TOP_RIGHT) && Math.abs(x - X2) < 5 && Math.abs(y - Y1) < 5)
            return TOP_RIGHT;
        if (inflectionSet.get(LEFT) && Math.abs(x - X1) < 5 && Math.abs(y - Yc) < 5)
            return LEFT;
        if (inflectionSet.get(RIGHT) &&  Math.abs(x - X2) < 5 && Math.abs(y - Yc) < 5)
            return RIGHT;
        if (inflectionSet.get(BOTTOM) && Math.abs(x - Xc) < 5 && Math.abs(y - Y2) < 5)
            return BOTTOM;
        if (inflectionSet.get(BOTTOM_LEFT) && Math.abs(x - X1) < 5 && Math.abs(y - Y2) < 5)
            return BOTTOM_LEFT;
        if (inflectionSet.get(BOTTOM_RIGHT) && Math.abs(x - X2) < 5 && Math.abs(y - Y2) < 5)
            return BOTTOM_RIGHT;
        return NONE;
    }

    protected void paintInflectionPoints(Graphics2D g) {
        double X1 = getXCoord(getT1()), X2 = getXCoord(getT2());
        double Y1 = getYCoord(getV1()), Y2 = getYCoord(getV2());
        g.setPaint(Color.BLACK);
        if (inflectionSet.get(TOP)) {
            if (state == RESIZE && inflection == TOP) g.fill(new Rectangle2D.Double((X1 + X2) / 2 - 2, Y1 - 2, 4, 4));
            else g.draw(new Rectangle2D.Double((X1 + X2) / 2 - 2, Y1 - 2, 4, 4));
        }
        if (inflectionSet.get(TOP_LEFT)) {
            if (state == RESIZE && inflection == TOP_LEFT) g.fill(new Rectangle2D.Double(X1 - 2, Y1 - 2, 4, 4));
            else g.draw(new Rectangle2D.Double(X1 - 2, Y1 - 2, 4, 4));
        }
        if (inflectionSet.get(TOP_RIGHT)) {
            if (state == RESIZE && inflection == TOP_RIGHT) g.fill(new Rectangle2D.Double(X2 - 2, Y1 - 2, 4, 4));
            else g.draw(new Rectangle2D.Double(X2 - 2, Y1 - 2, 4, 4));
        }
        if (inflectionSet.get(LEFT)) {
            if (state == RESIZE && inflection == LEFT) g.fill(new Rectangle2D.Double(X1 - 2, (Y1 + Y2) / 2 - 2, 4, 4));
            else g.draw(new Rectangle2D.Double(X1 - 2, (Y1 + Y2) / 2, 4, 4));
        }
        if (inflectionSet.get(RIGHT)) {
            if (state == RESIZE && inflection == RIGHT) g.fill(new Rectangle2D.Double(X2 - 2, (Y1 + Y2) / 2 - 2, 4, 4));
            else g.draw(new Rectangle2D.Double(X2 - 2, (Y1 + Y2) / 2, 4, 4));
        }
        if (inflectionSet.get(BOTTOM)) {
            if (state == RESIZE && inflection == BOTTOM) g.fill(new Rectangle2D.Double((X1 + X2) / 2 - 2, Y2 - 2, 4, 4));
            else g.draw(new Rectangle2D.Double((X1 + X2) / 2 - 2, Y2 - 2, 4, 4));
        }
        if (inflectionSet.get(BOTTOM_LEFT)) {
            if (state == RESIZE && inflection == BOTTOM_LEFT) g.fill(new Rectangle2D.Double(X1 - 2, Y2 - 2, 4, 4));
            else g.draw(new Rectangle2D.Double(X1 - 2, Y2 - 2, 4, 4));
        }
        if (inflectionSet.get(BOTTOM_RIGHT)) {
            if (state == RESIZE && inflection == BOTTOM_RIGHT) g.fill(new Rectangle2D.Double(X2 - 2, Y2 - 2, 4, 4));
            else g.draw(new Rectangle2D.Double(X2 - 2, Y2 - 2, 4, 4));
        }
    }

    public void addUpdateRectangle(Rectangle rect, boolean b) {
        int i = b ? 1 : 0;
        if (updateRects[i] == null) updateRects[i] = rect;
        else updateRects[index] = updateRects[index].union(rect);
    }

    public void cumulateUpdateArea() {
        if (updateRects[0] != null && updateRects[1] != null) {
            updateRects[0] = updateRects[0].union(updateRects[1]);
            updateRects[1] = null;
        }
    }

    public void updateArea(ChartPanel chartPanel) {
        if (updateRects[1] != null) updateRects[1] = null;
        if (updateRects[0] != null) {
            chartPanel.paintImmediately(updateRects[0]);
            updateRects[0] = null;
        } else {
            chartPanel.repaint();
        }
    }

    public boolean updateAreaContains(double x, double y, boolean second) {
        int idx = second ? 1 : 0;
        if (updateRects[idx] != null) return updateRects[idx].contains(x, y);
        return false;
    }

    public boolean isInChartBounds(double x, double y) { return cf.getChartRenderer().getChartFrameBounds().contains(x, y); }

    protected boolean isFlipped() { return (getY1() < getY2()); }
    protected boolean isMirrored() { return (getX1() > getX2()); }

    protected Rectangle2D.Double getRectangle() { return getRectangle(getT1(), getV1(), getT2(), getV2()); }
    protected Rectangle2D.Double getRectangle(long t1, double v1, long t2, double v2) {
        Rectangle2D.Double r = new Rectangle2D.Double();
        r.setFrameFromDiagonal(getPoint(t1, v1), getPoint(t2, v2));
        return r;
    }

    public boolean contains(double x, double y) { return getArea().contains(x, y); }

    public boolean lineContains(double x1, double y1, double x2, double y2, double x, double y, int dx, int dy) {
        Rectangle r = new Rectangle();
        r.setFrameFromDiagonal(x1, y1, x2, y2);
        r.grow(dx, dy);
        if (r.contains(x, y)) {
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
        } else return false;
    }
    public boolean lineContains(Point2D.Double p1, Point2D.Double p2, Point2D.Double p, int dx, int dy) { return lineContains(p1.x, p1.y, p2.x, p2.y, p.x, p.y, dx, dy); }
    public boolean lineContains(double x1, double y1, double x2, double y2, double x, double y, int d) { return lineContains(x1, y1, x2, y2, x, y, d, d); }
    public boolean lineContains(Point2D.Double p1, Point2D.Double p2, Point2D.Double p, int d) { return lineContains(p1.x, p1.y, p2.x, p2.y, p.x, p.y, d, d); }

    public abstract boolean pointIntersects(double x, double y);
    public abstract void paint(Graphics2D g);
    public void paintAnnotation(Graphics2D g) {
        Rectangle2D.Double b = getChartFrame().getChartRenderer().getClickedBounds(getAreaIndex());
        Range r = getChartFrame().getChartRenderer().getClickedRange(getAreaIndex());
        if (b != null && r != null) {
            setBounds(b);
            setRange(r);
            paint(g);
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof Annotation)) return false;
        Annotation that = (Annotation) obj;
        if (!getIdentifier().equals(that.getIdentifier())) return false;
        if (getT1() != that.getT1()) return false;
        if (getT2() != that.getT2()) return false;
        if (getV1() != that.getV1()) return false;
        if (getV2() != that.getV2()) return false;
        return true;
    }

    public abstract AbstractNode getNode();

    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {
        ChartPanel chartPanel = getChartFrame().getChartPanel();
        if (e.getButton() == MouseEvent.BUTTON1) {
            Annotation current = chartPanel.getCurrent();
            chartPanel.deselectAll();
            int x = e.getX(), y = e.getY();
            if (current != null && current.isInChartBounds(x, y)) {
                chartPanel.setCurrent(current);
                switch (chartPanel.getState()) {
                    case ChartPanel.NONE:
                        current.setSelected(true);
                        current.initializePress(getX(x), y);
                        if (state == MOVE) {
                            chartPanel.setState(ChartPanel.MOVE);
                            chartPanel.mousePressed(e);
                        } else if (state == RESIZE) {
                            chartPanel.setState(ChartPanel.RESIZE);
                            chartPanel.mousePressed(e);
                        }
                        break;
                    case ChartPanel.NEWANNOTATION:
                        chartPanel.addAnnotation(current);
                        current.initialize(x, y, x, y);
                        break;
                    case ChartPanel.RESIZE:
                        current.setSelected(true);
                        state = RESIZE;
                        break;
                    case ChartPanel.MOVE:
                        current.setSelected(true);
                        state = MOVE;
                        break;
                }
            }
        }
    }
    public void mouseDragged(MouseEvent e) {
        ChartPanel chartPanel = getChartFrame().getChartPanel();
        Annotation current = chartPanel.getCurrent();
        if (current != null) {
            int x = e.getX(), y = e.getY();
            if (current.isInChartBounds(x, y)) if (current.updatePosition(x, y)) current.updateArea(chartPanel);
        }
        chartPanel.repaint();
    }
    public void mouseReleased(MouseEvent e) {
        ChartPanel chartPanel = getChartFrame().getChartPanel();
        Annotation current = chartPanel.getCurrent();
        if (current != null && current.isNew()) current.setNew(false);
        AnnotationManager.getDefault().setNewAnnotationName("");
        chartPanel.setState(ChartPanel.NONE);
        chartPanel.repaint();
    }

}
