package org.chartsy.main.utils;

import java.io.Serializable;

/**
 *
 * @author viorel.gheba
 */
public strictfp class Range implements Serializable {

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    private double lower;
    private double upper;

    public Range() {
        this(0, 1);
    }

    public Range(double lower, double upper) {
        this.lower = Math.min(lower, upper);
        this.upper = Math.max(lower, upper);
    }

    public double getLowerBound() {
        return this.lower;
    }

    public double getUpperBound() {
        return this.upper;
    }

    public double getLength() {
        if (lower >= 0 && upper >= 0) return upper - lower;
        else if (lower < 0 && upper >= 0) return upper + Math.abs(lower);
        else return Math.abs(lower) - Math.abs(upper);
    }

    public boolean contains(double value) {
        return (value >= this.lower && value <= this.upper);
    }

    public boolean intersects(double b0, double b1) {
        if (b0 <= this.lower) {
            return (b1 > this.lower);
        } else {
            return (b0 <= this.upper && b1 >= b0);
        }
    }

    public boolean intersects(Range range) {
        return this.intersects(range.getLowerBound(), range.getUpperBound());
    }

    public double constrain(double value) {
        double result = value;
        if (!contains(value)) {
            if (value > this.upper) {
                result = this.upper;
            } else if (value < this.lower) {
                result = this.lower;
            }
        }
        return result;
    }

    public static Range combine(Range r1, Range r2) {
        if (r1 == null) {
            return r2;
        } else {
            if (r2 == null) {
                return r1;
            } else {
                double l = Math.min(r1.getLowerBound(), r2.getLowerBound());
                double u = Math.max(r1.getUpperBound(), r2.getUpperBound());
                return new Range(l, u);
            }
        }
    }

	public static Range combineNotZero(Range r1, Range r2)
	{
		if (r1 == null) {
			return r2;
		} else {
			if (r2 == null) {
				return r1;
			} else {
				if (r2.getLowerBound() > 0) {
					double l = Math.min(r1.getLowerBound(), r2.getLowerBound());
					double u = Math.max(r1.getUpperBound(), r2.getUpperBound());
					return new Range(
						Math.min(l, u),
						Math.max(l, u));
				} else {
					double l = r1.getLowerBound();
					double u = Math.max(r1.getUpperBound(), r2.getUpperBound());
					return new Range(
						Math.min(l, u),
						Math.max(l, u));
				}
			}
		}
	}

    public static Range expandToInclude(Range range, double value) {
        if (range == null) {
            return new Range(value, value);
        }
        if (value < range.getLowerBound()) {
            return new Range(value, range.getUpperBound());
        } else if (value > range.getUpperBound()) {
            return new Range(range.getLowerBound(), value);
        } else {
            return range;
        }
    }

    public static Range expand(Range range, double lowerMargin, double upperMargin) {
        if (range == null) {
            throw new IllegalArgumentException("Null 'range' argument.");
        }
        double length = range.getLength();
        double lower = range.getLowerBound() - length * lowerMargin;
        double upper = range.getUpperBound() + length * upperMargin;
        if (lower > upper) {
            lower = lower / 2.0 + upper / 2.0;
            upper = lower;
        }
        return new Range(lower, upper);
    }

    public static Range shift(Range base, double delta) {
        return shift(base, delta, false);
    }

    public static Range shift(Range base, double delta, boolean allowZeroCrossing) {
        if (base == null) {
            throw new IllegalArgumentException("Null 'base' argument.");
        }
        if (allowZeroCrossing) {
            return new Range(base.getLowerBound() + delta, base.getUpperBound() + delta);
        } else {
            return new Range(shiftWithNoZeroCrossing(base.getLowerBound(), delta), shiftWithNoZeroCrossing(base.getUpperBound(), delta));
        }
    }

    private static double shiftWithNoZeroCrossing(double value, double delta) {
        if (value > 0.0) {
            return Math.max(value + delta, 0.0);
        } else if (value < 0.0) {
            return Math.min(value + delta, 0.0);
        } else {
            return value + delta;
        }
    }

    public static Range scale(Range base, double factor) {
        if (base == null) {
            throw new IllegalArgumentException("Null 'base' argument.");
        }
        if (factor < 0) {
            throw new IllegalArgumentException("Negative 'factor' argument.");
        }
        return new Range(base.getLowerBound() * factor, base.getUpperBound() * factor);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Range)) {
            return false;
        }
        Range range = (Range) obj;
        if (!(this.lower == range.lower)) {
            return false;
        }
        if (!(this.upper == range.upper)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(this.lower);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.upper);
        result = 29 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public String toString() {
        return ("Range[" + this.lower + "," + this.upper + "]");
    }

}
