
import java.io.*;
import java.util.*;
import javax.vecmath.*;
import javafx.scene.paint.*;

public class SVGReader {
    public static LinkedList<Integer> getSong(String fileName) {
        // get the points from the SVG file
        LinkedList<Point2d> points = getPoints(fileName);

        // create the Bezier curves
        int numPoints = (points.size() - 4) / 3 + 1;
        BezierCurve[] curves = new BezierCurve[numPoints];
        for (int i = 0; i < curves.length; i++) {
            curves[i] = new BezierCurve(points.get(3 * i), points.get(3 * i + 1), points.get(3 * i + 2), points.get(3 * i + 3));
        }

        // create the BetterBezierCurve
        BetterBezierCurve betterCurve = new BetterBezierCurve(curves);

        // get the pitches
        return getPitches(betterCurve, numPoints);
    }

    public static String getSVGPath(String fileName) {
        File file = new File("assets/images/" + fileName + ".svg");

        String line = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            br.readLine();
            br.readLine();
            br.readLine();
            line = br.readLine();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int index = line.indexOf("d=");
        String path = line.substring(index + 3);
        path = path.substring(0, path.indexOf("\""));

        return path;
    }

    public static Color getSVGFill(String fileName) {
        File file = new File("assets/images/" + fileName + ".svg");

        String line = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            br.readLine();
            br.readLine();
            br.readLine();
            line = br.readLine();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int index = line.indexOf("fill:hsl(");
        String color = line.substring(index + 9);
        int hue = Integer.parseInt(color.substring(0, color.indexOf(",")));
        color = color.substring(color.indexOf(",") + 2);
        double saturation = Double.parseDouble(color.substring(0, color.indexOf("%"))) / 100.0;
        color = color.substring(color.indexOf(",") + 2);
        double lightness = Double.parseDouble(color.substring(0, color.indexOf("%"))) / 100.0;

        double value = saturation * Math.min(lightness, 1 - lightness) + lightness;
        saturation = (value == 0) ? 0 : 2 * (1 - lightness / value);

        return Color.hsb(hue, saturation, value);
    }

    private static LinkedList<Point2d> getPoints(String fileName) {
        String path = getSVGPath(fileName);

        path = path.replaceAll("[a-zA-Z]", " ");
        path = path.trim();

        // read each pair of numbers
        String[] pairs = path.split(" ");
        LinkedList<Point2d> points = new LinkedList<Point2d>();
        for (String pair : pairs) {
            String point[] = pair.split(",");
            points.add(new Point2d(Double.parseDouble(point[0]), Double.parseDouble(point[1])));
        }

        return points;
    }

    private static LinkedList<Integer> getPitches(BetterBezierCurve curve, int numPoints) {
        LinkedList<Integer> pitches = new LinkedList<Integer>();

        Point2d origin = new Point2d(305, 235);
        Point2d topLeftCorner = new Point2d(0, 0);
        double max = distance(origin, topLeftCorner);
        double factor = numPoints / 16.0;
        
        for (double t = 0.5; t < 16; t++) {
            Point2d point = curve.getPoint(t * factor);
            double dist = distance(origin, point);
            int pitch = (int) Math.round(32 * dist / max);
            pitches.add(pitch);
        }

        return pitches;
    }

    private static double distance(Point2d p1, Point2d p2) {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }

    static record BezierCurve(Point2d p1, Point2d p2, Point2d p3, Point2d p4) {
        public BezierCurve {
            if (p1 == null || p2 == null || p3 == null || p4 == null) {
                throw new IllegalArgumentException("BezierCurve cannot have null points");
            }
        }

        public Point2d getPoint(double t) {
            double x = Math.pow(1 - t, 3) * p1.x + 3 * t * Math.pow(1 - t, 2) * p2.x + 3 * Math.pow(t, 2) * (1 - t) * p3.x + Math.pow(t, 3) * p4.x;
            double y = Math.pow(1 - t, 3) * p1.y + 3 * t * Math.pow(1 - t, 2) * p2.y + 3 * Math.pow(t, 2) * (1 - t) * p3.y + Math.pow(t, 3) * p4.y;
            return new Point2d(x, y);
        }
    }

    static class BetterBezierCurve {
        private BezierCurve[] curves;

        public BetterBezierCurve(BezierCurve... curves) {
            this.curves = curves;
        }

        public Point2d getPoint(double t) {
            int curveIndex = (int) t;
            double curveT = t % 1;
            return curves[curveIndex].getPoint(curveT);
        }
    }
}