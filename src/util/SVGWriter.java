package util;

import java.io.*;

public class SVGWriter {
    private final BufferedWriter writer;
    private String strokeWidth = "1px", stroke = "black", fill = "black";

    public SVGWriter(File file, double x, double y, double width, double height) throws IOException {
        writer = new BufferedWriter(new FileWriter(file));
        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n");
        writer.write("<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" width=\"100%\" height=\"100%\" viewBox=\"" + x + " " + y + " " + (x + width) + " " + (y + height) + "\">\n");
    }

    public void setStrokeWidth(String strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public void setStroke(String stroke) {
        this.stroke = stroke;
    }

    public void setFill(String fill) {
        this.fill = fill;
    }

    public void writeRect(double x, double y, double width, double height) throws IOException {
        writer.write(String.format("\t<rect x=\"%f\" y=\"%f\" width=\"%f\" height=\"%f\" stroke-width=\"%s\" stroke=\"%s\" fill=\"%s\" />\n", x, y, width, height, strokeWidth, stroke, fill));
    }

    public void writeRect(double x, double y, double width, double height, double rx, double ry) throws IOException {
        writer.write(String.format("\t<rect x=\"%f\" y=\"%f\" rx=\"%f\" ry=\"%f\" width=\"%f\" height=\"%f\" stroke-width=\"%s\" stroke=\"%s\" fill=\"%s\" />\n", x, y, rx, ry, width, height, strokeWidth, stroke, fill));
    }

    public void writeCircle(double r, double cx, double cy) throws IOException {
        writer.write(String.format("\t<circle r=\"%f\" cx=\"%f\" cy=\"%f\" stroke-width=\"%s\" stroke=\"%s\" fill=\"%s\" />\n", r, cx, cy, strokeWidth, stroke, fill));
    }

    public void writeEllipse(double rx, double ry, double cx, double cy) throws IOException {
        writer.write(String.format("\t<ellipse rx=\"%f\" ry=\"%f\" cx=\"%f\" cy=\"%f\" stroke-width=\"%s\" stroke=\"%s\" fill=\"%s\" />\n", rx, ry, cx, cy, strokeWidth, stroke, fill));
    }

    public void writeLine(double x1, double y1, double x2, double y2) throws IOException {
        writer.write(String.format("\t<line x1=\"%f\" y1=\"%f\" x2=\"%f\" y2=\"%f\" stroke-width=\"%s\" stroke=\"%s\" fill=\"%s\" />\n", x1, y1, x2, y2, strokeWidth, stroke, fill));
    }

    public void writePolyline(double x1, double y1, double x2, double y2, double... points) throws IOException {
        writer.write(String.format("\t<polyline points=\"%f,%f %f,%f", x1, y1, x2, y2));
        for (int i = 0; i < points.length / 2; i++) {
            writer.write(String.format(" %f,%f", points[i * 2], points[i * 2 + 1]));
        }
        writer.write(String.format("\" stroke-width=\"%s\" stroke=\"%s\" fill=\"%s\" />\n", strokeWidth, stroke, fill));
    }

    public void writePolygon(double x1, double y1, double x2, double y2, double x3, double y3, double... points) throws IOException {
        writer.write(String.format("\t<polygon points=\"%f,%f %f,%f %f,%f", x1, y1, x2, y2, x3, y3));
        for (int i = 0; i < points.length / 2; i++) {
            writer.write(String.format(" %f,%f", points[i * 2], points[i * 2 + 1]));
        }
        writer.write(String.format("\" stroke-width=\"%s\" stroke=\"%s\" fill=\"%s\" />\n", strokeWidth, stroke, fill));
    }

    public void writePath(String d) throws IOException {
        writer.write(String.format("\t<path d=\"%s\" stroke-width=\"%s\" stroke=\"%s\" fill=\"%s\" />\n", d, strokeWidth, stroke, fill));
    }

    public void writePath(Path path) throws IOException {
        writePath(path.toString());
    }

    public void writeText(double x, double y, String text) throws IOException {
        writer.write(String.format("\t<text x=\"%f\" y=\"%f\" stroke-width=\"%s\" stroke=\"%s\" fill=\"%s\"> %s </text>\n", x, y, strokeWidth, stroke, fill, text));
    }

    public void close() throws IOException {
        writer.write("</svg>");
        writer.close();
    }
}