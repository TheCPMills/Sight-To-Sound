package main;

import java.io.*;
import javax.imageio.ImageIO;
import java.util.*;
import java.util.List;
import java.awt.image.BufferedImage;
import java.awt.*;

public class ImageProcessing {
    BufferedImage img;

    public ImageProcessing(int width, int height) {
    }

    public static void main(String args[]) throws IOException {
        pixelate("assets/images/squiddy.jpg", 44, 25);
        pixelateAndShrink("assets/images/squiddy.jpg", 44, 25);

    }

    public static void pixelate(String fileName, int rows, int columns) throws IOException {
        File file = new File(fileName);
        BufferedImage image = ImageIO.read(file);

        pixelateAndShrink(image, rows, columns);
    }

    public static void pixelate(BufferedImage image, int rows, int columns) {
        int height = image.getHeight();
        int width = image.getWidth();

        int[] heightsMatrix = Segmenter.segment(height, rows);
        int[] widthsMatrix = Segmenter.segment(width, columns);

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int red = 0;
        int green = 0;
        int blue = 0;

        for (int row = 0; row < rows; row++) {
            int y = (row == 0) ? 0 : Arrays.stream(heightsMatrix).limit(row).sum();
            for (int column = 0; column < columns; column++) {
                int x = (column == 0) ? 0 : Arrays.stream(widthsMatrix).limit(column).sum();
                for (int w = 0; w < widthsMatrix[column]; w++) {
                    for (int h = 0; h < heightsMatrix[row]; h++) {
                        int a = w + x;
                        int b = h + y;

                        int clr = image.getRGB(a, b);
                        red += (clr & 0x00ff0000) >> 16;
                        green += (clr & 0x0000ff00) >> 8;
                        blue += clr & 0x000000ff;
                    }
                }

                red /= heightsMatrix[row] * widthsMatrix[column];
                green /= heightsMatrix[row] * widthsMatrix[column];
                blue /= heightsMatrix[row] * widthsMatrix[column];

                for (int w = 0; w < widthsMatrix[column]; w++) {
                    for (int h = 0; h < heightsMatrix[row]; h++) {
                        int a = w + x;
                        int b = h + y;
                        img.setRGB(a, b, new Color(red, green, blue).getRGB());
                    }
                }

                red = 0;
                green = 0;
                blue = 0;
            }
        }
        try {
            ImageIO.write(img, "png", new File("assets/images/image.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void pixelateAndShrink(String fileName, int rows, int columns) throws IOException {
        File file = new File(fileName);
        BufferedImage image = ImageIO.read(file);

        pixelateAndShrink(image, rows, columns);
    }

    public static void pixelateAndShrink(BufferedImage image, int rows, int columns) {
        int pixHeight = rows;
        int pixWidth = columns;

        int height = image.getHeight();
        int width = image.getWidth();

        int[] heightsMatrix = Segmenter.segment(height, rows);
        int[] widthsMatrix = Segmenter.segment(width, columns);

        BufferedImage img = new BufferedImage(pixWidth, pixHeight, BufferedImage.TYPE_INT_RGB);

        int red = 0;
        int green = 0;
        int blue = 0;

        for (int row = 0; row < rows; row++) {
            int y = (row == 0) ? 0 : Arrays.stream(heightsMatrix).limit(row).sum();
            for (int column = 0; column < columns; column++) {
                int x = (column == 0) ? 0 : Arrays.stream(widthsMatrix).limit(column).sum();
                for (int w = 0; w < widthsMatrix[column]; w++) {
                    for (int h = 0; h < heightsMatrix[row]; h++) {
                        int a = w + x;
                        int b = h + y;

                        int clr = image.getRGB(a, b);
                        red += (clr & 0x00ff0000) >> 16;
                        green += (clr & 0x0000ff00) >> 8;
                        blue += clr & 0x000000ff;
                    }
                }

                red /= heightsMatrix[row] * widthsMatrix[column];
                green /= heightsMatrix[row] * widthsMatrix[column];
                blue /= heightsMatrix[row] * widthsMatrix[column];

                img.setRGB(column, row, new Color(red, green, blue).getRGB());

                red = 0;
                green = 0;
                blue = 0;
            }
        }
        try {
            ImageIO.write(img, "png", new File("assets/images/pixelatedImage.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Color> listPixels(String fileName) throws IOException {
        File file = new File(fileName);
        BufferedImage image = ImageIO.read(file);

        return listPixels(image);
    }

    public static List<Color> listPixels(BufferedImage image) {
        List<Color> pixelColors = new LinkedList<>();
        int height = image.getHeight();
        int width = image.getWidth();

        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                pixelColors.add(new Color(image.getRGB(column, row)));
            }
        }

        return pixelColors;
    }

    public static List<Color> spiral(String fileName) throws IOException {
        File file = new File(fileName);
        BufferedImage image = ImageIO.read(file);

        return spiral(image);
    }

    public static List<Color> spiral(BufferedImage image) {
        List<Color> pixelColors = new LinkedList<>();
        int height = image.getHeight(), width = image.getWidth();
        int i, row = 0, column = 0;

        while (row < height && column < width) {
            for (i = column; i < width; ++i) {
                pixelColors.add(new Color(image.getRGB(i, row)));
            }
            row++;

            for (i = row; i < height; ++i) {
                pixelColors.add(new Color(image.getRGB(width - 1, i)));
            }
            width--;

            if (row < height) {
                for (i = width - 1; i >= column; --i) {
                    pixelColors.add(new Color(image.getRGB(i, height - 1)));
                }
                height--;
            }

            if (column < width) {
                for (i = height - 1; i >= row; --i) {
                    pixelColors.add(new Color(image.getRGB(column, i)));
                }
                column++;
            }
        }

        return pixelColors;
    }
}