package main;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class CoolByproduct {
    BufferedImage img;

    public CoolByproduct(int width, int height) {
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    public static void main(String args[]) throws IOException {
        File file = new File("squiddy.jpg");
        BufferedImage image = ImageIO.read(file);

        int height = image.getHeight();
        int width = image.getWidth();

        CoolByproduct iP = new CoolByproduct(width, height);
        iP.magic();

    }

    public void setPixelColor(int x, int y, int hexColor) {
        img.setRGB(x, y, hexColor);
    }

    public void magic() throws IOException {
        File file = new File("squiddy.jpg");
        BufferedImage image = ImageIO.read(file);

        int height = image.getHeight();
        int width = image.getWidth();

        int rows = 10;
        int col = 10;

        int sum = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < col; j++) {
                for (int k = 0; k < width / rows; k++) {
                    for (int l = 0; l < height / col; l++) {
                        sum += image.getRGB(k + ((height / col) * j), l + ((width / rows) * i));
                    }

                    sum /= (height / col) * (width / rows);

                    for (int l = 0; l < height / col; l++) {
                        img.setRGB(k + ((height / col) * j), l + ((width / rows) * i), sum);
                    }

                    sum = 0;
                }
            }
        }
        ImageIO.write(img, "png", new File("final.png"));
    }

    public void printPixelColors(int row, int col) throws IOException {
        File file = new File("squiddy.jpg");
        BufferedImage image = ImageIO.read(file);

        int clr = image.getRGB(row, col);
        int red = (clr & 0x00ff0000) >> 16;
        int green = (clr & 0x0000ff00) >> 8;
        int blue = clr & 0x000000ff;

        System.out.println("Red Color value = " + red);
        System.out.println("Green Color value = " + green);
        System.out.println("Blue Color value = " + blue);
    }
}