package main;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import noise.*;
import noise.fractal.*;
import random.*;
import javax.imageio.ImageIO;
import javax.vecmath.Point2i;
public class Algorithm {
    // for each part, there is an offset for pitches and durations so that the same
    // pixel can have different effects (circular array)
    // maybe a matrix for percussion modes
    private static BufferedImage image = null;
    private static BufferedImage pixelatedImage = null;
    private static List<Color> pixelColors;
    private static int key = -1;
    private static int mode = -1;
    private static int tempo = -1;
    private static int seed = -1;
    private static List<Integer>[] melody = null;
    private static List<Integer>[] harmony = null;
    private static List<Integer>[] bass = null;
    private static List<Integer>[] percussion = null;
    private static MidiFile melodyMidiFile;
    private static MidiFile harmonyMidiFile;
    private static MidiFile bassMidiFile;
    private static MidiFile percussionMidiFile;
    private static final int[][] modes = {{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12},
            { 0, 0, 2, 4, 4, 5, 5, 7, 7, 9, 11, 11, 12 }, { 0, 0, 2, 3, 3, 5, 5, 7, 7, 8, 10, 10, 12 },
            { 0, 0, 2, 3, 3, 5, 5, 7, 7, 9, 11, 11, 12 }, { 0, 0, 2, 3, 3, 5, 5, 7, 7, 8, 11, 11, 12 },
            { 0, 0, 2, 3, 4, 4, 7, 7, 7, 9, 9, 12, 12 }, { 0, 0, 3, 5, 6, 6, 7, 7, 7, 10, 10, 12, 12 },
            { 0, 0, 2, 2, 4, 4, 7, 7, 7, 9, 9, 12, 12 }, { 0, 0, 3, 3, 3, 5, 5, 7, 7, 10, 10, 10, 12 },
            { 0, 0, 2, 2, 5, 5, 5, 7, 7, 10, 10, 10, 12 }, { 0, 0, 2, 3, 3, 5, 5, 7, 7, 9, 10, 10, 12 },
            { 0, 0, 1, 3, 3, 5, 5, 7, 7, 8, 10, 10, 12 }, { 0, 0, 2, 4, 4, 6, 6, 7, 7, 9, 11, 11, 12 },
            { 0, 0, 2, 4, 4, 5, 5, 7, 7, 9, 10, 10, 12 }, { 0, 0, 1, 3, 3, 5, 5, 6, 6, 8, 10, 10, 12 } };

    public static void main(String[] args) {
        startConversion("assets/images/rainbow.jpg", "assets/audio/");
    }

    public static void startConversion(String fileName, String destination) {
        reset();
        String imageName = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.lastIndexOf("."));
        File file = new File(fileName);
        melodyMidiFile = new MidiFile();
        harmonyMidiFile = new MidiFile();
        bassMidiFile = new MidiFile();
        percussionMidiFile = new MidiFile();
        try {
            image = ImageIO.read(file);
            algorithm(fileName);
            melodyMidiFile.writeToFile(destination + "/" + imageName + "Melody.mid");
            harmonyMidiFile.writeToFile(destination + "/" + imageName + "Harmony.mid");
            bassMidiFile.writeToFile(destination + "/" + imageName + "Bass.mid");
            percussionMidiFile.writeToFile(destination + "/" + imageName + "Percussion.mid");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void reset() {
        image = null;
        pixelatedImage = null;
        key = -1;
        mode = -1;
        tempo = -1;
        seed = -1;
        melody = null;
        harmony = null;
        bass = null;
        percussion = null;
    }

    private static void algorithm(String fileName) throws IOException {
        int seed = ImageOps.averagePixelColor(fileName); // get average image color and set the seed based off the
                                                         // average color's hex value
        int[] rgb = ColorOps.hexToRGB(seed); // convert average color to HSV color scale

        key = (int) Math.round(11 / 255f * rgb[0]); // set the key based on the saturation of the average color
        mode = (int) Math.round(14 / 255f * rgb[1]); // set the mode/scale based on the hue of the average color
        tempo = (int) (120 / 255f * rgb[2] + 60); // set the tempo based on the value of the average color

        slice(); // slice the image
        pixelatedImage = ImageIO.read(new File("assets/images/pixelatedImage.png")); // get a reference to the sliced
                                                                                     // image
        pixelColors = ImageProcessing.listPixels(pixelatedImage); // get a list of pixel colors in the image

        // TODO: Add top 5 brightest to beginning and top 5 darkest to end
        Randomizer.shuffle(pixelColors, new LCG(seed)); // randomize the order of pixels

        generateMelody();
        generateHarmony();
        generateBass();
        generatePercussion();

        setMelody();
        setHarmony();
        setBass();
        setPercussion();
    }

    private static void slice() throws IOException {
        List<Float> divisions = new ArrayList<>(Arrays.asList(1.0f, 3 / 4.0f, 1 / 2.0f, 3 / 8.0f, 1 / 3.0f, 1 / 4.0f,
                3 / 16.0f, 1 / 6.0f, 1 / 8.0f, 1 / 12.0f, 1 / 16.0f));
        int sliceHeight, sliceWidth;

        float fourBeatsInMs = 240000.0f / tempo;
        float sixteenthNoteInMs = fourBeatsInMs * divisions.get(10);

        int maxNotes = (int) (30000 / sixteenthNoteInMs);
        int area = image.getHeight() * image.getWidth();

        if (area < maxNotes) {
            sliceHeight = 1;
            sliceWidth = 1;
        } else {
            int sliceSize = area / maxNotes;
            Point2i squarestPair = ((LinkedList<Point2i>) (factorPairs(sliceSize))).getLast();
            sliceHeight = squarestPair.x;
            sliceWidth = squarestPair.y;
        }

        float hSlice = image.getHeight() * 1.0f / sliceHeight;
        float wSlice = image.getWidth() * 1.0f / sliceWidth;

        while ((int) hSlice < 10 || (int) wSlice < 10) {
            if ((int) hSlice < 10 && (int) wSlice < 10) {
                sliceHeight = 10;
                sliceWidth = 10;
            }

            if ((int) hSlice < 10) {
                sliceHeight /= 2;
                sliceWidth *= 2;
            }
            if ((int) wSlice < 10) {
                sliceHeight *= 2;
                sliceWidth /= 2;
            }
            hSlice = image.getHeight() * 1.0f / sliceHeight;
            wSlice = image.getWidth() * 1.0f / sliceWidth;
        }

        ImageProcessing.pixelateAndShrink(image, (int) hSlice, (int) wSlice);
        ImageProcessing.pixelate(image, (int) hSlice, (int) wSlice);
    }

    private static List<Point2i> factorPairs(int num) {
        List<Point2i> pairs = new LinkedList<Point2i>();
        for (int i = 1; i < Math.sqrt(num); i++) {
            if (num % i == 0) {
                if (num / i == i) {
                    pairs.add(new Point2i(i, i));
                } else {
                    pairs.add(new Point2i(i, num / i));
                }
            }
        }
        return pairs;
    }

    private static void generateMelody() {
        ArrayList<Integer> notes = new ArrayList<>();
        ArrayList<Integer> durations = new ArrayList<>();
        ArrayList<Integer> velocities = new ArrayList<>();
        melody = new ArrayList[] { notes, durations, velocities };

        Noise melodyNoise = new Perlin(seed);
        NoiseMapGenerator.generate(melodyNoise, pixelatedImage.getWidth(), pixelatedImage.getHeight(), "assets/images/melodyNoise");
        List<Color> noiseSpiral = null;
        long averageNoiseColor = -1;
        try {
            noiseSpiral = ImageProcessing.spiral("assets/images/melodyNoise.png");
            averageNoiseColor = (long) ImageOps.averagePixelColor("assets/images/melodyNoise.png");
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < pixelColors.size(); i++) {
            float[] hsv = ColorOps.rgbToHSV(ColorOps.getRGB(pixelColors.get(i)));
            int pitch = (int) MathOps.round(MathOps.map(hsv[0], 0, 360, 0, 11));
            int duration = (int) MathOps.round(MathOps.map(hsv[1], 0, 100, 0, 4));
            int octave = (int) MathOps.round(MathOps.map(hsv[2], 0, 100, -1, 1));
            int velocity = determineVelocity((long) noiseSpiral.get(i).getRGB(), averageNoiseColor);

            notes.add(modes[mode][pitch % 12] + key + (4 + octave) * 12);
            durations.add(duration);
            velocities.add(velocity);
        }
    }

    private static void generateHarmony() {
        ArrayList<Integer> notes = new ArrayList<>();
        ArrayList<Integer> durations = new ArrayList<>();
        ArrayList<Integer> velocities = new ArrayList<>();
        harmony = new ArrayList[] { notes, durations, velocities };

        Noise harmonyNoise = new Simplex(seed);
        NoiseMapGenerator.generate(harmonyNoise, pixelatedImage.getWidth(), pixelatedImage.getHeight(), "assets/images/harmonyNoise");
        List<Color> noiseSpiral = null;
        long averageNoiseColor = -1;
        try {
            noiseSpiral = ImageProcessing.spiral("assets/images/harmonyNoise.png");
            averageNoiseColor = (long) ImageOps.averagePixelColor("assets/images/harmonyNoise.png");
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < pixelColors.size(); i++) {
            float[] hsv = ColorOps.rgbToHSV(ColorOps.getRGB(pixelColors.get(i)));
            int pitch = (int) MathOps.round(MathOps.map(hsv[0], 0, 360, 0, 11));
            int duration = (int) MathOps.round(MathOps.map(hsv[1], 0, 100, 0, 4));
            int octave = (int) MathOps.round(MathOps.map(hsv[2], 0, 100, -1, 1));
            int velocity = determineVelocity((long) noiseSpiral.get(i).getRGB(), averageNoiseColor);

            notes.add(modes[mode][(pitch + 4) % 12] + key + (4 + octave) * 12);
            durations.add(duration);
            velocities.add(velocity);
        }
    }

    private static void generateBass() {
        ArrayList<Integer> notes = new ArrayList<>();
        ArrayList<Integer> durations = new ArrayList<>();
        ArrayList<Integer> velocities = new ArrayList<>();
        bass = new ArrayList[] { notes, durations, velocities };

        Noise bassNoise = new Value(seed);
        NoiseMapGenerator.generate(bassNoise, pixelatedImage.getWidth(), pixelatedImage.getHeight(), "assets/images/bassNoise");
        List<Color> noiseSpiral = null;
        long averageNoiseColor = -1;
        try {
            noiseSpiral = ImageProcessing.spiral("assets/images/bassNoise.png");
            averageNoiseColor = (long) ImageOps.averagePixelColor("assets/images/bassNoise.png");
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < pixelColors.size(); i++) {
            float[] hsv = ColorOps.rgbToHSV(ColorOps.getRGB(pixelColors.get(i)));
            int pitch = (int) MathOps.round(MathOps.map(hsv[0], 0, 360, 0, 11));
            int duration = (int) MathOps.round(MathOps.map(hsv[1], 0, 100, 0, 4));
            int octave = (int) MathOps.round(MathOps.map(hsv[2], 0, 100, -1, 1));
            int velocity = determineVelocity((long) noiseSpiral.get(i).getRGB(), averageNoiseColor);

            notes.add(modes[mode][(pitch + 7) % 12] + key + (3 + octave) * 12);
            durations.add(duration);
            velocities.add(velocity);
        }
    }

    private static void generatePercussion() {
        ArrayList<Integer> notes = new ArrayList<>();
        ArrayList<Integer> durations = new ArrayList<>();
        ArrayList<Integer> velocities = new ArrayList<>();
        percussion = new ArrayList[] { notes, durations, velocities };

        Noise percussionNoise = new FBM(seed);
        NoiseMapGenerator.generate(percussionNoise, pixelatedImage.getWidth(), pixelatedImage.getHeight(), "assets/images/percussionNoise");
        List<Color> noiseSpiral = null;
        long averageNoiseColor = -1;
        try {
            noiseSpiral = ImageProcessing.spiral("assets/images/percussionNoise.png");
            averageNoiseColor = (long) ImageOps.averagePixelColor("assets/images/percussionNoise.png");
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < pixelColors.size(); i++) {
            float[] hsv = ColorOps.rgbToHSV(ColorOps.getRGB(pixelColors.get(i)));
            int pitch = (int) MathOps.round(MathOps.map(hsv[0], 0, 360, 0, 11));
            int duration = (int) MathOps.round(MathOps.map(hsv[1], 0, 100, 0, 4));
            int octave = (int) MathOps.round(MathOps.map(hsv[2], 0, 100, -1, 1));
            int velocity = determineVelocity((long) noiseSpiral.get(i).getRGB(), averageNoiseColor);

            notes.add(modes[mode][(pitch + 2) % 12] + key + (3 + octave) * 12);
            durations.add(duration);
            velocities.add(velocity);
        }
    }

    private static void setMelody() {
        for (int i = 0; i < pixelColors.size(); i++) {
            melodyMidiFile.noteOnOffNow((int) MathOps.pow(2, melody[1].get(i) + 2), melody[0].get(i), melody[2].get(i));
        }
    }

    private static void setHarmony() {
        for (int i = 0; i < pixelColors.size(); i++) {
            harmonyMidiFile.noteOnOffNow((int) MathOps.pow(2, harmony[1].get(i) + 2), harmony[0].get(i),
                    harmony[2].get(i));
        }
    }

    private static void setBass() {
        for (int i = 0; i < pixelColors.size(); i++) {
            bassMidiFile.noteOnOffNow((int) MathOps.pow(2, bass[1].get(i) + 2), bass[0].get(i), bass[2].get(i));
        }
    }

    private static void setPercussion() {
        for (int i = 0; i < pixelColors.size(); i++) {
            percussionMidiFile.noteOnOffNow((int) MathOps.pow(2, percussion[1].get(i) + 2), percussion[0].get(i),
                    percussion[2].get(i));
        }
    }

    private static int determineVelocity(long noisePixelColor, long averageNoiseColor) {
        return (averageNoiseColor < 0x7FFFFFl)
                ? 127 - (int) MathOps.round(
                        127.0 * MathOps.abs(noisePixelColor - averageNoiseColor) / (0xFFFFFFl - averageNoiseColor))
                : 100 - (int) MathOps
                        .round(100.0 * MathOps.abs(noisePixelColor - averageNoiseColor) / averageNoiseColor);
    }
}