package images;
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

    public static BufferedImage imageFromPixelMatrix(Color[][] pixelMatrix) {
        int height = pixelMatrix.length;
        int width = pixelMatrix[0].length;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                image.setRGB(j, i, pixelMatrix[i][j].getRGB());
            }
        }

        return image;
    }

    public static BufferedImage resize(BufferedImage originalImage, int targetWidth, int targetHeight) throws IOException {
        BufferedImage resultingImage = originalImage;

        resultingImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resultingImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(originalImage, 0, 0, targetWidth, targetHeight, 0, 0, originalImage.getWidth(), originalImage.getHeight(), null);
        g.dispose();

        try {
            ImageIO.write(resultingImage, "png", new File("assets/images/resizedImage.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return resultingImage;
    }

    public static BufferedImage pixelate(String fileName, int rows, int columns) throws IOException {
        File file = new File(fileName);
        BufferedImage image = ImageIO.read(file);

        return pixelate(image, rows, columns);
    }

    public static BufferedImage pixelate(BufferedImage image, int rows, int columns) {
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
            ImageIO.write(img, "png", new File("assets/images/pixelatedImage.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return img;
    }

    public static BufferedImage pixelateAndShrink(String fileName, int rows, int columns) throws IOException {
        File file = new File(fileName);
        BufferedImage image = ImageIO.read(file);

        return pixelateAndShrink(image, rows, columns);
    }

    public static BufferedImage pixelateAndShrink(BufferedImage image, int rows, int columns) {
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
            ImageIO.write(img, "png", new File("assets/images/pixelatedShrunkImage.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return img;
    }

    public static Color[][] pixelMatrix(String filename) throws IOException {
        File file = new File(filename);
        BufferedImage image = ImageIO.read(file);

        return pixelMatrix(image);
    }

    public static Color[][] pixelMatrix(BufferedImage image) {
        int height = image.getHeight();
        int width = image.getWidth();

        Color[][] matrix = new Color[height][width];

        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                int clr = image.getRGB(column, row);
                int red = (clr & 0x00ff0000) >> 16;
                int green = (clr & 0x0000ff00) >> 8;
                int blue = clr & 0x000000ff;

                matrix[row][column] = new Color(red, green, blue);
            }
        }

        return matrix;
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

    public static List<Color> listPixelsSpiral(String fileName) throws IOException {
        File file = new File(fileName);
        BufferedImage image = ImageIO.read(file);

        return listPixelsSpiral(image);
    }

    public static List<Color> listPixelsSpiral(BufferedImage image) {
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

    public static List<Color> listPixelsSnake(String fileName) throws IOException {
        File file = new File(fileName);
        BufferedImage image = ImageIO.read(file);

        return listPixelsSnake(image);
    }

    public static List<Color> listPixelsSnake(BufferedImage image) {
        List<Color> pixelColors = new LinkedList<>();
        int height = image.getHeight(), width = image.getWidth();
        
        for (int row = 0; row < height; row++) {
            if (row % 2 == 0) {
                for (int column = 0; column < width; column++) {
                    pixelColors.add(new Color(image.getRGB(column, row)));
                }
            } else {
                for (int column = width - 1; column >= 0; column--) {
                    pixelColors.add(new Color(image.getRGB(column, row)));
                }
            }
        }

        return pixelColors;
    }
}