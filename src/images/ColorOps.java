package images;
import java.awt.Color;
import util.MathOps;

public class ColorOps { // Color functions
    public static float getAlpha(Color color) {
        return color.getAlpha() / 255.0f;
    }

    public static int[] getRGB(Color color) {
        int clr = color.getRGB();
        return new int[] { ((clr & 0x00FF0000) >> 16), ((clr & 0x0000ff00) >> 8), (clr & 0x000000ff) };
    }

    // Hex functions
    public static int[] hexToRGB(int hex) {
        int[] RGB = new int[3];

        RGB[0] = (hex & 0xFF0000) >> 16;
        RGB[1] = (hex & 0x00FF00) >> 8;
        RGB[2] = (hex & 0x0000FF);

        return RGB;
    }

    public static float[] hexToHSV(int hex) {
        return rgbToHSV(hexToRGB(hex));
    }

    public static float[] hexToHSL(int hex) {
        return rgbToHSL(hexToRGB(hex));
    }

    public static int[] hexToCMYK(int hex) {
        return rgbToCMYK(hexToRGB(hex));
    }

    // RGB functions
    public static int rgbToHex(int[] RGB) {
        int HEX = (RGB[0] << 16) + (RGB[1] << 8) + RGB[2];
        return HEX;
    }

    public static float[] rgbToHSV(int[] RGB) {
        float[] HSV = new float[3];

        float sRed = RGB[0] / 255.0f;
        float sGreen = RGB[1] / 255.0f;
        float sBlue = RGB[2] / 255.0f;

        float min = (float) MathOps.min(sRed, sGreen, sBlue);
        float max = (float) MathOps.max(sRed, sGreen, sBlue);
        float delta = max - min;

        if (delta == 0) {
            HSV[0] = 0;
        } else if (max == sRed) {
            HSV[0] = (int) (60 * (float) MathOps.mod(((sGreen - sBlue) / delta), 6));
        } else if (max == sGreen) {
            HSV[0] = (int) (60 * (((sBlue - sRed) / delta) + 2));
        } else {
            HSV[0] = (int) (60 * (((sRed - sGreen) / delta) + 4));
        }

        HSV[1] = (max == 0) ? 0 : (float) MathOps.round(delta / max, 0.001) * 100;
        HSV[2] = (float) MathOps.round(max, 0.001) * 100;

        return HSV;
    }

    public static float[] rgbToHSL(int[] RGB) {
        float[] HSL = new float[3];

        float sRed = RGB[0] / 255.0f;
        float sGreen = RGB[1] / 255.0f;
        float sBlue = RGB[2] / 255.0f;

        float min = (float) MathOps.min(sRed, sGreen, sBlue);
        float max = (float) MathOps.max(sRed, sGreen, sBlue);
        float delta = max - min;

        if (delta == 0) {
            HSL[0] = 0;
        } else if (max == sRed) {
            HSL[0] = 60 * (float) MathOps.mod((int) ((sGreen - sBlue) / delta), 6);
        } else if (max == sGreen) {
            HSL[0] = (int) (60 * (((sBlue - sRed) / delta) + 2));
        } else {
            HSL[0] = (int) (60 * (((sRed - sGreen) / delta) + 4));
        }

        HSL[1] = (max == 0) ? 0 : (float) MathOps.round(delta / (1 - MathOps.abs(max + min - 1)), 0.001) * 100;
        HSL[2] = (float) MathOps.round((max + min) / 2, 0.001) * 100;

        return HSL;
    }

    public static int[] rgbToCMYK(int[] RGB) {
        int[] CMYK = new int[4];

        float sRed = RGB[0] / 255.0f;
        float sGreen = RGB[1] / 255.0f;
        float sBlue = RGB[2] / 255.0f;

        float black = 1 - (float) MathOps.max(sRed, sGreen, sBlue);

        CMYK[0] = (int) MathOps.round((1 - sRed - black) / (1 - black) * 100);
        CMYK[1] = (int) MathOps.round((1 - sGreen - black) / (1 - black) * 100);
        CMYK[2] = (int) MathOps.round((1 - sBlue - black) / (1 - black) * 100);
        CMYK[3] = (int) MathOps.round(black * 100);

        return CMYK;
    }

    // HSV functions
    public static int hsvToHex(float[] hsv) {
        return rgbToHex(hsvToRGB(hsv));
    }

    public static int[] hsvToRGB(float[] hsv) {
        int[] RGB = new int[3];
        float sRED, sGreen, sBlue;

        float s = hsv[1] / 100.0f;
        float v = hsv[2] / 100.0f;

        float c = s * v;
        float x = c * (1 - (float) MathOps.abs((hsv[0] / 60) % 2 - 1));
        float m = v - c;

        if (hsv[0] > 0 && hsv[0] < 60) {
            sRED = c;
            sGreen = x;
            sBlue = 0;
        } else if (hsv[0] >= 60 && hsv[0] < 120) {
            sRED = x;
            sGreen = c;
            sBlue = 0;
        } else if (hsv[0] >= 120 && hsv[0] < 180) {
            sRED = 0;
            sGreen = c;
            sBlue = x;
        } else if (hsv[0] >= 180 && hsv[0] < 240) {
            sRED = 0;
            sGreen = x;
            sBlue = c;
        } else if (hsv[0] >= 240 && hsv[0] < 300) {
            sRED = x;
            sGreen = 0;
            sBlue = c;
        } else {
            sRED = c;
            sGreen = 0;
            sBlue = x;
        }

        RGB[0] = (int) MathOps.round((sRED + m) * 255);
        RGB[1] = (int) MathOps.round((sGreen + m) * 255);
        RGB[2] = (int) MathOps.round((sBlue + m) * 255);

        return RGB;
    }

    public static float[] hsvToHSL(float[] hsv) {
        return rgbToHSL(hsvToRGB(hsv));
    }

    public static int[] hsvToCMYK(float[] hsv) {
        return rgbToCMYK(hsvToRGB(hsv));
    }

    // HSL functions
    public static int hslToHex(float[] hsl) {
        return rgbToHex(hslToRGB(hsl));
    }

    public static int[] hslToRGB(float[] hsl) {
        int[] RGB = new int[3];
        float sRED, sGreen, sBlue;

        float s = hsl[1] / 100.0f;
        float l = hsl[2] / 100.0f;

        float c = (1 - (float) MathOps.abs(2 * l - 1)) * s;
        float x = c * (1 - (float) MathOps.abs((hsl[0] / 60) % 2 - 1));
        float m = l - c / 2;

        if (hsl[0] > 0 && hsl[0] < 60) {
            sRED = c;
            sGreen = x;
            sBlue = 0;
        } else if (hsl[0] >= 60 && hsl[0] < 120) {
            sRED = x;
            sGreen = c;
            sBlue = 0;
        } else if (hsl[0] >= 120 && hsl[0] < 180) {
            sRED = 0;
            sGreen = c;
            sBlue = x;
        } else if (hsl[0] >= 180 && hsl[0] < 240) {
            sRED = 0;
            sGreen = x;
            sBlue = c;
        } else if (hsl[0] >= 240 && hsl[0] < 300) {
            sRED = x;
            sGreen = 0;
            sBlue = c;
        } else {
            sRED = c;
            sGreen = 0;
            sBlue = x;
        }

        RGB[0] = (int) MathOps.round((sRED + m) * 255);
        RGB[1] = (int) MathOps.round((sGreen + m) * 255);
        RGB[2] = (int) MathOps.round((sBlue + m) * 255);

        return RGB;
    }

    public static float[] hslToHSV(float[] hsl) {
        return rgbToHSV(hslToRGB(hsl));
    }

    public static int[] hslToCMYK(float[] hsl) {
        return rgbToCMYK(hslToRGB(hsl));
    }

    // CMYK functions
    public static int cmykToHex(int[] cmyk) {
        return rgbToHex(cmykToRGB(cmyk));
    }

    public static int[] cmykToRGB(int[] cmyk) {
        int[] RGB = new int[3];

        RGB[0] = (int) MathOps.round((1 - cmyk[0] / 100f) * (1 - cmyk[3] / 100f) * 255);
        RGB[1] = (int) MathOps.round((1 - cmyk[1] / 100f) * (1 - cmyk[3] / 100f) * 255);
        RGB[2] = (int) MathOps.round((1 - cmyk[2] / 100f) * (1 - cmyk[3] / 100f) * 255);

        return RGB;
    }

    public static float[] cmykToHSV(int[] cmyk) {
        return rgbToHSV(cmykToRGB(cmyk));
    }

    public static float[] cmykToHSL(int[] cmyk) {
        return rgbToHSL(cmykToRGB(cmyk));
    }

    // Other Color Formats
    public static int greyscale(int[] rgb) {
        float shade = 0.299f * rgb[0] + 0.587f * rgb[1] + 0.114f * rgb[2];
        return (shade > 255) ? 255 : (int) MathOps.round(shade);
    }

    public static int[] sepia(int[] rgb) {
        int[] sepiaRGB = new int[3];

        float tRed = 0.393f * rgb[0] + 0.769f * rgb[1] + 0.189f * rgb[2];
        float tGreen = 0.349f * rgb[0] + 0.686f * rgb[1] + 0.168f * rgb[2];
        float tBlue = 0.272f * rgb[0] + 0.534f * rgb[1] + 0.131f * rgb[2];

        sepiaRGB[0] = (tRed > 255) ? 255 : (int) MathOps.round(tRed);
        sepiaRGB[0] = (tGreen > 255) ? 255 : (int) MathOps.round(tGreen);
        sepiaRGB[0] = (tBlue > 255) ? 255 : (int) MathOps.round(tBlue);

        return sepiaRGB;
    }

    public static enum ColorGroup {
        RED, ORANGE, YELLOW, GREEN, CYAN, BLUE, PURPLE, MAGENTA, GREY, BLACK, WHITE;
    }

    public static void main(String[] args) {
        int HEX;
        int[] RGB;
        float[] HSV;
        float[] HSL;
        int[] CMYK;

        HEX = 0x80FFFF;
        RGB = hexToRGB(HEX);
        HSV = hexToHSV(HEX);
        HSL = hexToHSL(HEX);
        CMYK = hexToCMYK(HEX);
        System.out.println("Hex: 0x80FFFF");
        System.out.println("RGB: " + RGB[0] + ", " + RGB[1] + ", " + RGB[2]);
        System.out.println("HSV: " + (int) HSV[0] + "°, " + HSV[1] + "%, " + HSV[2] + "%");
        System.out.println("HSL: " + (int) HSL[0] + "°, " + HSL[1] + "%, " + HSL[2] + "%");
        System.out.println("CMYK: " + CMYK[0] + "%, " + CMYK[1] + "%, " + CMYK[2] + "%, " + CMYK[3] + "%");

        RGB = new int[] { 64, 32, 128 };
        HEX = rgbToHex(RGB);
        HSV = rgbToHSV(RGB);
        HSL = rgbToHSL(RGB);
        CMYK = rgbToCMYK(RGB);
        System.out.println(
                "==============================================\nHex: 0x" + Integer.toHexString(HEX).toUpperCase());
        System.out.println("RGB: 64, 32, 128");
        System.out.println("HSV: " + (int) HSV[0] + "°, " + HSV[1] + "%, " + HSV[2] + "%");
        System.out.println("HSL: " + (int) HSL[0] + "°, " + HSL[1] + "%, " + HSL[2] + "%");
        System.out.println("CMYK: " + CMYK[0] + "%, " + CMYK[1] + "%, " + CMYK[2] + "%, " + CMYK[3] + "%");

        HSV = new float[] { 72f, 88.5f, 80.625f };
        HEX = hsvToHex(HSV);
        RGB = hsvToRGB(HSV);
        HSL = hsvToHSL(HSV);
        CMYK = hsvToCMYK(HSV);
        System.out.println(
                "==============================================\nHex: 0x" + Integer.toHexString(HEX).toUpperCase());
        System.out.println("RGB: " + RGB[0] + ", " + RGB[1] + ", " + RGB[2]);
        System.out.println("HSV: 72°, 88.5%, 80.625%");
        System.out.println("HSL: " + (int) HSL[0] + "°, " + HSL[1] + "%, " + HSL[2] + "%");
        System.out.println("CMYK: " + CMYK[0] + "%, " + CMYK[1] + "%, " + CMYK[2] + "%, " + CMYK[3] + "%");

        HSL = new float[] { 10f, 90.1f, 56.8f };
        HEX = hslToHex(HSL);
        RGB = hslToRGB(HSL);
        HSV = hslToHSV(HSL);
        CMYK = hslToCMYK(HSL);
        System.out.println(
                "==============================================\nHex: 0x" + Integer.toHexString(HEX).toUpperCase());
        System.out.println("RGB: " + RGB[0] + ", " + RGB[1] + ", " + RGB[2]);
        System.out.println("HSV: " + (int) HSV[0] + "°, " + HSV[1] + "%, " + HSV[2] + "%");
        System.out.println("HSL: 10°, 90.1%, 56.8%");
        System.out.println("CMYK: " + CMYK[0] + "%, " + CMYK[1] + "%, " + CMYK[2] + "%, " + CMYK[3] + "%");

        CMYK = new int[] { 80, 50, 10, 40 };
        HEX = cmykToHex(CMYK);
        RGB = cmykToRGB(CMYK);
        HSV = cmykToHSV(CMYK);
        HSL = cmykToHSL(CMYK);
        System.out.println(
                "==============================================\nHex: 0x" + Integer.toHexString(HEX).toUpperCase());
        System.out.println("RGB: " + RGB[0] + ", " + RGB[1] + ", " + RGB[2]);
        System.out.println("HSV: " + (int) HSV[0] + "°, " + HSV[1] + "%, " + HSV[2] + "%");
        System.out.println("HSL: " + (int) HSL[0] + "°, " + HSL[1] + "%, " + HSL[2] + "%");
        System.out.println("CMYK: 80%, 50%, 10%, 40%");
    }
}