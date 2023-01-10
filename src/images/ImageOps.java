package images;
import java.io.*;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;

import static images.ColorOps.ColorGroup;

import java.awt.*;

public class ImageOps {
    public static void main(String[] args) throws IOException {
        BufferedImage img = ImageIO.read(new File("assets/images/box.png"));
        System.out.println(redestPixel(img));
        System.out.println(greenestPixel(img));
        System.out.println(bluestPixel(img));
        System.out.println(greatestHuePixel(img));
        System.out.println(mostSaturatedPixel(img));
        System.out.println(brightestPixel(img));
        System.out.println(mostTransparentPixel(img));
    }

    public static Point redestPixel(BufferedImage image) throws IOException {
        int x = -1, y = -1;
        float maxRed = -1;

        int height = image.getHeight();
        int width = image.getWidth();

        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                float red = ColorOps.getRGB(new Color(image.getRGB(column, row)))[0];

                if (red > maxRed) {
                    maxRed = red;
                    x = column;
                    y = row;
                }

            }
        }
        return new Point(x, y);
    }

    public static Point greenestPixel(BufferedImage image) throws IOException {
        int x = -1, y = -1;
        float maxGreen = -1;

        int height = image.getHeight();
        int width = image.getWidth();

        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                float green = ColorOps.getRGB(new Color(image.getRGB(column, row)))[1];

                if (green > maxGreen) {
                    maxGreen = green;
                    x = column;
                    y = row;
                }

            }
        }
        return new Point(x, y);
    }

    public static Point bluestPixel(BufferedImage image) throws IOException {
        int x = -1, y = -1;
        float maxBlue = -1;

        int height = image.getHeight();
        int width = image.getWidth();

        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                float blue = ColorOps.getRGB(new Color(image.getRGB(column, row)))[2];

                if (blue > maxBlue) {
                    maxBlue = blue;
                    x = column;
                    y = row;
                }

            }
        }
        return new Point(x, y);
    }

    public static Point greatestHuePixel(BufferedImage image) throws IOException {
        int x = -1, y = -1;
        float maxHue = -1;

        int height = image.getHeight();
        int width = image.getWidth();

        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                float hue = ColorOps.hexToHSV(image.getRGB(column, row))[0];

                if (hue > maxHue) {
                    maxHue = hue;
                    x = column;
                    y = row;
                }

            }
        }
        return new Point(x, y);
    }

    public static Point mostSaturatedPixel(BufferedImage image) throws IOException {
        int x = -1, y = -1;
        float maxSaturation = -1;

        int height = image.getHeight();
        int width = image.getWidth();

        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                float saturation = ColorOps.hexToHSV(image.getRGB(column, row))[1];

                if (saturation > maxSaturation) {
                    maxSaturation = saturation;
                    x = column;
                    y = row;
                }

            }
        }
        return new Point(x, y);
    }

    public static Point brightestPixel(BufferedImage image) throws IOException {
        int x = -1, y = -1;
        float maxValue = -1;

        int height = image.getHeight();
        int width = image.getWidth();

        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                float value = ColorOps.hexToHSV(image.getRGB(column, row))[2];

                if (value > maxValue) {
                    maxValue = value;
                    x = column;
                    y = row;
                }

            }
        }
        return new Point(x, y);
    }

    public static Point mostTransparentPixel(BufferedImage image) throws IOException {
        int x = -1, y = -1;
        float maxAlpha = -1;

        int height = image.getHeight();
        int width = image.getWidth();

        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                float alpha = ColorOps.getAlpha(new Color(image.getRGB(column, row)));

                if (alpha > maxAlpha) {
                    maxAlpha = alpha;
                    x = column;
                    y = row;
                }

            }
        }
        return new Point(x, y);
    }

    public static ColorGroup mostFrequentColor(BufferedImage image) throws IOException {
        int[] counts = new int[11];

        for (int row = 0; row < image.getHeight(); row++) {
            for (int column = 0; column < image.getWidth(); column++) {

                float[] hsv = ColorOps.hexToHSV(image.getRGB(column, row));

                float hue = hsv[0] % 360;
                float saturation = hsv[1];
                float value = hsv[2];

                if (saturation > 0.1) {
                    if (hue <= 10) {
                        counts[0]++;
                    } else if (hue <= 36) {
                        counts[1]++;
                    } else if (hue <= 64) {
                        counts[2]++;
                    } else if (hue <= 145) {
                        counts[3]++;
                    } else if (hue <= 175) {
                        counts[4]++;
                    } else if (hue <= 256) {
                        counts[5]++;
                    } else if (hue <= 295) {
                        counts[6]++;
                    } else if (hue <= 344) {
                        counts[7]++;
                    } else {
                        counts[0]++;
                    }
                } else {
                    if (value <= 15) {
                        counts[8]++;
                    } else if (value <= 85) {
                        counts[9]++;
                    } else {
                        counts[10]++;
                    }
                }
            }
        }
        
        // get the index of the highest count using streams
        int maxIndex = IntStream.range(0, counts.length)
                    .reduce((i, j) -> counts[i] > counts[j] ? i : j)
                    .getAsInt();

        // get the color of the highest count
        switch(maxIndex) {
            case 0:
                return ColorGroup.RED;
            case 1:
                return ColorGroup.ORANGE;
            case 2:
                return ColorGroup.YELLOW;
            case 3:
                return ColorGroup.GREEN;
            case 4:
                return ColorGroup.CYAN;
            case 5:
                return ColorGroup.BLUE;
            case 6:
                return ColorGroup.PURPLE;
            case 7:
                return ColorGroup.MAGENTA;
            case 8:
                return ColorGroup.WHITE;
            case 9:
                return ColorGroup.GREY;
            case 10:
                return ColorGroup.BLACK;
            default:
                return null;
        }
    }

    public static int averagePixelColor(BufferedImage image) {
        int red = 0, green = 0, blue = 0;
        int height = image.getHeight();
        int width = image.getWidth();

        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                int clr = image.getRGB(column, row);

                red += (clr & 0xFF0000) >> 16;
                green += (clr & 0x00FF00) >> 8;
                blue += clr & 0x0000FF;
            }
        }
        return new Color(red / (height * width), green / (height * width), blue / (height * width)).getRGB();
    }
}