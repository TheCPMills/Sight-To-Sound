package main;

import javax.vecmath.*;

public class MathOps {
    public static final double π = Math.PI;
    public static final double e = Math.E;
    public static final double i = Math.pow(-1, 0.5);

    public static double distance(Point4d p1, Point4d p2) {
        double x = pow((p1.x - p2.x), 2);
        double y = pow((p1.y - p2.y), 2);
        double z = pow((p1.z - p2.z), 2);
        double w = pow((p1.w - p2.w), 2);
        return pow((x + y + z + w), 0.5);
    }

    public static double pow(double base, double exponent) {
        return Math.pow(base, exponent);
    }

    public static double exp(double exponent) {
        return Math.exp(exponent);
    }

    public static double sqrt(double radicand) {
        return root(2, radicand);
    }

    public static double cbrt(double radicand) {
        return root(3, radicand);
    }

    public static double root(double index, double radicand) {
        return Math.pow(radicand, 1.0 / index);
    }

    public static double log(double argument) {
        return Math.log10(argument);
    }

    public static double ln(double argument) {
        return Math.log(argument);
    }

    public static double log(double base, double argument) {
        return ln(argument) / ln(base);
    }

    public static double sin(double value) {
        return Math.sin(value);
    }

    public static double cos(double value) {
        return Math.cos(value);
    }

    public static double tan(double value) {
        return Math.tan(value);
    }

    public static double arcsin(double value) {
        return Math.asin(value);
    }

    public static double arccos(double value) {
        return Math.acos(value);
    }

    public static double arctan(double value) {
        return Math.atan(value);
    }

    public static double sinh(double value) {
        return Math.sinh(value);
    }

    public static double cosh(double value) {
        return Math.cosh(value);
    }

    public static double tanh(double value) {
        return Math.tanh(value);
    }

    public static double arcsinh(double value) {
        return ln(value + pow(pow(value, 2) + 1, 0.5));
    }

    public static double arccosh(double value) {
        return ln(value + pow(pow(value, 2) - 1, 0.5));
    }

    public static double arctanh(double value) {
        return 0.5 * ln((1 + value) / (1 - value));
    }

    public static double abs(double value) {
        return Math.abs(value);
    }

    public static double min(double... values) {
        double min = values[0];
        for (double value : values) {
            min = Math.min(min, value);
        }
        return min;
    }

    public static double max(double... values) {
        double max = values[0];
        for (double value : values) {
            max = Math.max(max, value);
        }
        return max;
    }

    public static double floor(double value) {
        return Math.floor(value);
    }

    public static double ceil(double value) {
        return Math.ceil(value);
    }

    public static double mod(double dividend, double divisor) {
        double mod = dividend % divisor;
        if (dividend * divisor < 0 && mod != 0) {
            mod += divisor;
        }
        return mod;
    }

    public static double rem(double dividend, double divisor) {
        return dividend % divisor;
    }

    public static long integer(double value) {
        return (long) value;
    }

    public static double fraction(double value) {
        return value - integer(value);
    }

    public static double round(double value) {
        return Math.round(value);
    }

    public static double round(double value, double decimalPlace) {
        return Math.round(value / decimalPlace) * decimalPlace;
    }

    public static double map(double value, double minX, double maxX, double minY, double maxY) {
        return ((value - minX) * (maxY - minY) / (maxX - minX)) + minY;
    }

    public static double radiansToDegrees(double angleMeasure) {
        return angleMeasure * (180 / π);
    }

    public static double degreesToRadians(double angleMeasure) {
        return angleMeasure * (π / 180);
    }
}