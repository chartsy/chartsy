package org.chartsy.main.utils;

/**
 *
 * @author viorel.gheba
 */
public class RectangleInsets {

    public double top;
    public double bottom;
    public double left;
    public double right;

    public RectangleInsets() { this(0.0, 0.0, 0.0, 0.0); }

    public RectangleInsets(double t, double l, double b, double r) {
        this.top = t;
        this.bottom = b;
        this.left = l;
        this.right = r;
    }

    public double getTop() {
        return this.top;
    }

    public double getBottom() {
        return this.bottom;
    }

    public double getLeft() {
        return this.left;
    }

    public double getRight() {
        return this.right;
    }

    public void setTop(double t) {
        this.top = t;
    }

    public void setBottom(double b) {
        this.bottom = b;
    }

    public void setLeft(double l) {
        this.left = l;
    }

    public void setRight(double r) {
        this.right = r;
    }

}
