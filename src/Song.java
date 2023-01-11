import java.util.*;
import java.util.stream.*;
import java.io.*;
import java.awt.image.*;
import javax.imageio.*;

import org.json.simple.*;
import org.json.simple.parser.*;

import music.*;
import images.*;
import util.*;

public class Song {
    // Audio-Visual Resources
    private long seed;
    private BufferedImage image;

    // Musical Parameters
    private char tonic;
    private String mode;
    private Tempo tempo;
    private String genre;

    // Algorithmic Parameters
    public int melodicTimbre;
    public int harmonicTimbre;
    public int[] percussiveTimbres;
    public RhythmGenerator rhythmGenerator;
    public PitchGenerator pitchGenerator;
    public int sections, rows, columns, sliceWidth, sliceHeight;

    public Song(String imageFilePath) {
        // Load image
        try {
            image = ImageIO.read(new File(imageFilePath));
        } catch (IOException e) {
            System.out.println("Error: Could not load image file.");
            System.exit(1);
        }

        // Initialize musical parameters
        initalizeMusicalParameters();

        // Initialize algorithmic parameters
        initializeAlgorithmicParameters();
    }

    public Song(BufferedImage image) {
        this.image = image;

        // Initialize musical parameters
        initalizeMusicalParameters();

        // Initialize algorithmic parameters
        initializeAlgorithmicParameters();
    }

    public void initalizeMusicalParameters() {
        // Calculate seed
        seed = calculateSeed(); // ImageOps.averagePixelColor(image)

        // Calculate musical parameters
        tonic = calculateTonic();
        mode = calculateMode(); // ImageOps.mostFrequentColor(image)
        tempo = calculateTempo();
        genre = calculateGenre();
    }

    public void initializeAlgorithmicParameters() {
        // Set timbres
        setTimbres();

        // Create rhythm generator
        rhythmGenerator = new RhythmGenerator(genre);

        // Create pitch generator
        pitchGenerator = new PitchGenerator(tonic, mode);

        // Subdivide image
        try {
            subdivideImage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private long calculateSeed() {
        return 0;
    }

    private char calculateTonic() {
        return 'C';
    }

    private String calculateMode() {
        return "major";
    }

    private Tempo calculateTempo() {
        return Tempo.ADAGIO;
    }

    private String calculateGenre() {
        return "classical";
    }

    private void setTimbres() {
        // get instruments pattern from genre
        JSONParser parser = new JSONParser();
        JSONObject genresJSONObject;

        try {
            genresJSONObject = (JSONObject) parser.parse(new FileReader("resources/genres.jsonc"));
        } catch (IOException e) {
            System.out.println("Error: Could not load genres.jsonc file.");
            System.exit(1);
            return;
        } catch (ParseException e) {
            System.out.println("Error: Could not parse genres.jsonc file.");
            System.exit(1);
            return;
        }

        JSONObject genreJSONObject = (JSONObject) genresJSONObject.get(genre);
        JSONArray instrumentJSONArray = (JSONArray) genreJSONObject.get("instruments");
        
        // convert instrument array to array of timbre MIDI values
        int[] timbres = new int[instrumentJSONArray.size()];
        for (int i = 0; i < instrumentJSONArray.size(); i++) {
            timbres[i] = (int) ((long) instrumentJSONArray.get(i));
        }

        melodicTimbre = timbres[0];
        harmonicTimbre = timbres[1];
        percussiveTimbres = Arrays.copyOfRange(timbres, 2, timbres.length);
    }

    private void subdivideImage() throws IOException {
        int width = image.getWidth();
        int height = image.getHeight();

        String aspectRatio = determineAspectRatio(width, height);

        // NOTE: This is assuming that the monitor is 1920x1080 and landscape
        // IDEA: Each image will be resized to fit the monitor while maintaining aspect ratio
        switch (aspectRatio) {
            case "1:2":
                // resize image to 540x1080
                image = ImageProcessing.resize(image, 540, 1080);
                sections = 50;
                sliceWidth = 108;
                sliceHeight = 108;
                break;
            case "9:16":
                // resize image to 594x1056
                image = ImageProcessing.resize(image, 594, 1056);
                sections = 72;
                sliceWidth = 99;
                sliceHeight = 88;
                break;
            case "2:3":
                // resize image to 720x1080
                image = ImageProcessing.resize(image, 720, 1080);
                sections = 54;
                sliceWidth = 120;
                sliceHeight = 120;
                break;
            case "5:7":
                // resize image to 770x1078
                image = ImageProcessing.resize(image, 770, 1078);
                sections = 35;
                sliceWidth = 154;
                sliceHeight = 154;
                break;
            case "3:4":
                // resize image to 810x1080
                image = ImageProcessing.resize(image, 810, 1080);
                sections = 48;
                sliceWidth = 135;
                sliceHeight = 135;
                break;
            case "7:9":
                // resize image to 840x1080
                image = ImageProcessing.resize(image, 840, 1080);
                sections = 63;
                sliceWidth = 120;
                sliceHeight = 120;
                break;
            case "11:14":
                // resize image to 847x1078
                image = ImageProcessing.resize(image, 847, 1078);
                sections = 49;
                sliceWidth = 121;
                sliceHeight = 154;
                break;
            case "4:5":
                // resize image to 864x1080
                image = ImageProcessing.resize(image, 864, 1080);
                sections = 60;
                sliceWidth = 144;
                sliceHeight = 108;
                break;
            case "5:6":
                // resize image to 895x1074
                image = ImageProcessing.resize(image, 895, 1074);
                sections = 30;
                sliceWidth = 179;
                sliceHeight = 179;
                break;
            case "1:1":
                // resize image to 1080x1080
                image = ImageProcessing.resize(image, 1080, 1080);
                sections = 64;
                sliceWidth = 135;
                sliceHeight = 135;
                break;
            case "6:5":
                // resize image to 1290x1075
                image = ImageProcessing.resize(image, 1290, 1075);
                sections = 30;
                sliceWidth = 215;
                sliceHeight = 215;
                break;
            case "5:4":
                // resize image to 1350x1080
                image = ImageProcessing.resize(image, 1350, 1080);
                sections = 30;
                sliceWidth = 225;
                sliceHeight = 216;
                break;
            case "14:11":
                // resize image to 1344x1056
                image = ImageProcessing.resize(image, 1344, 1056);
                sections = 48;
                sliceWidth = 168;
                sliceHeight = 176;
                break;
            case "9:7":
                // resize image to 1386x1078
                image = ImageProcessing.resize(image, 1386, 1078);
                sections = 63;
                sliceWidth = 154;
                sliceHeight = 154;
                break;
            case "4:3":
                // resize image to 1440x1080
                image = ImageProcessing.resize(image, 1440, 1080);
                sections = 48;
                sliceWidth = 180;
                sliceHeight = 180;
                break;
            case "7:5":
                // resize image to 1512x1080
                image = ImageProcessing.resize(image, 1512, 1080);
                sections = 35;
                sliceWidth = 216;
                sliceHeight = 216;
                break;
            case "3:2":
                // resize image to 1620x1080
                image = ImageProcessing.resize(image, 1620, 1080);
                sections = 54;
                sliceWidth = 180;
                sliceHeight = 180;
                break;
            case "16:9":
                // resize image to 1920x1080
                image = ImageProcessing.resize(image, 1920, 1080);
                sections = 40;
                sliceWidth = 240;
                sliceHeight = 216;
                break;
            case "2:1":
                // resize image to 2160x1080
                image = ImageProcessing.resize(image, 2160, 1080);
                sections = 50;
                sliceWidth = 216;
                sliceHeight = 216;
                break;
        }

        columns = image.getWidth() / sliceWidth;
        rows = image.getHeight() / sliceHeight;
    }

    private String determineAspectRatio(int width, int height) {
        float aspectRatio = (float) width / height;

        float[] differences = new float[19];

        differences[0] = Math.abs(aspectRatio - (1.0f / 2.0f));
        differences[1] = Math.abs(aspectRatio - (9.0f / 16.0f));
        differences[2] = Math.abs(aspectRatio - (2.0f / 3.0f));
        differences[3] = Math.abs(aspectRatio - (5.0f / 7.0f));
        differences[4] = Math.abs(aspectRatio - (3.0f / 4.0f));
        differences[5] = Math.abs(aspectRatio - (7.0f / 9.0f));
        differences[6] = Math.abs(aspectRatio - (11.0f / 14.0f));
        differences[7] = Math.abs(aspectRatio - (4.0f / 5.0f));
        differences[8] = Math.abs(aspectRatio - (5.0f / 6.0f));
        differences[9] = Math.abs(aspectRatio - (1.0f / 1.0f));
        differences[10] = Math.abs(aspectRatio - (6.0f / 5.0f));
        differences[11] = Math.abs(aspectRatio - (5.0f / 4.0f));
        differences[12] = Math.abs(aspectRatio - (14.0f / 11.0f));
        differences[13] = Math.abs(aspectRatio - (9.0f / 7.0f));
        differences[14] = Math.abs(aspectRatio - (4.0f / 3.0f));
        differences[15] = Math.abs(aspectRatio - (7.0f / 5.0f));
        differences[16] = Math.abs(aspectRatio - (3.0f / 2.0f));
        differences[17] = Math.abs(aspectRatio - (16.0f / 9.0f));
        differences[18] = Math.abs(aspectRatio - (2.0f / 1.0f));
        
        // get index of smallest difference using lambda expression
        int smallestDifferenceIndex = IntStream.range(0, differences.length).reduce((i, j) -> differences[i] < differences[j] ? i : j).getAsInt();

        switch (smallestDifferenceIndex) {
            case 0:
                return "1:2";
            case 1:
                return "9:16";
            case 2:
                return "2:3";
            case 3:
                return "5:7";
            case 4:
                return "3:4";
            case 5:
                return "7:9";
            case 6:
                return "11:14";
            case 7:
                return "4:5";
            case 8:
                return "5:6";
            case 9:
                return "1:1";
            case 10:
                return "6:5";
            case 11:
                return "5:4";
            case 12:
                return "14:11";
            case 13:
                return "9:7";
            case 14:    
                return "4:3";
            case 15:
                return "7:5";
            case 16:
                return "3:2";
            case 17:
                return "16:9";
            case 18:
                return "2:1";
        }
        return null;
    }

    public BufferedImage getSlicedImage() {
        return image;
    }

    public int getBPM() {
        return tempo.getBPM();
    }

    public String getMeasure(int x, int y) {
        // determine row and column
        int currentRow = y / sliceHeight;
        int currentColumn = x / sliceWidth;

        System.out.println("In Column " + currentColumn + ", Row " + currentRow);

        // determine bounds
        int minX = currentColumn * sliceWidth;
        int maxX = (currentColumn + 1) * sliceWidth - 1;
        int minY = currentRow * sliceHeight;
        int maxY = (currentRow + 1) * sliceHeight - 1;

        // generate melody rhythm
        Rhythm melodyRhythm = new Rhythm(rhythmGenerator.generateRhythm());
        LinkedList<NoteDuration> noteDurations = melodyRhythm.getDurations();

        // generate melody pitches
        LinkedList<Integer> melodyPitches = new LinkedList<>();

        int lastPitch = -1;
        for (int i = 0; i < noteDurations.size(); i++) {
            int randomX = (int) (Math.random() * (maxX - minX + 1) + minX);
            int randomY = (int) (Math.random() * (maxY - minY + 1) + minY);

            int color = image.getRGB(randomX, randomY);
            lastPitch = pitchGenerator.generateNextPitch(color, lastPitch); 
            melodyPitches.add(lastPitch);
        }

        // generate melody
        Voice melody = new Voice(0, melodicTimbre, tempo.getBPM());
        for (int i = 0; i < noteDurations.size(); i++) {
            float duration = noteDurations.get(i).getDuration();
            int pitch = melodyPitches.get(i);
            Note note = new Note(pitch, duration);
            melody.addNote(note);
        }
        // generate harmony rhythm
        LinkedList<NoteDuration> harmonyDurations = new LinkedList<>(Arrays.asList(NoteDuration.SEMIBREVE));

        // generate harmony chords and tonic
        LinkedList<Integer> harmonyTonics = new LinkedList<>(Arrays.asList(melodyPitches.get(0)));
        LinkedList<String> harmonyChords = new LinkedList<>(Arrays.asList("maj"));

        // generate harmony
        Voice harmony = new Voice(1, harmonicTimbre, tempo.getBPM());
        for (int i = 0; i < harmonyDurations.size(); i++) {
            float duration = harmonyDurations.get(i).getDuration();
            int tonic = harmonyTonics.get(i);
            String chordType = harmonyChords.get(i);
            Chord chord = new Chord(tonic, chordType, duration);
            harmony.addChord(chord);
        }

        // generate percussion rhythms
        LinkedList<String> percussionDurations0 = new LinkedList<>() {
            {
                add("R_CROTCHET");
                add("R_CROTCHET");
                add("QUAVER");
                add("QUAVER");
                add("CROTCHET");
            }
        };

        LinkedList<String> percussionDurations1 = new LinkedList<>() {
            {
                add("QUAVER_TRIPLET");
                add("QUAVER_TRIPLET");
                add("QUAVER_TRIPLET");
                add("QUAVER");
                add("QUAVER");
            }
        };

        // generate percussion layers
        PercussiveLayer percussiveLayer0 = new PercussiveLayer(percussiveTimbres[0]);
        PercussiveLayer percussiveLayer1 = new PercussiveLayer(percussiveTimbres[1]);

        // generate percussion notes
        for (int i = 0; i < percussionDurations0.size(); i++) {
            percussiveLayer0.addNote(percussionDurations0.get(i));
        }

        for (int i = 0; i < percussionDurations1.size(); i++) {
            percussiveLayer1.addNote(percussionDurations1.get(i));
        }

        // generate percussion
        PercussiveVoice percussion = PercussiveVoice.getInstance();
        percussion.addLayer(percussiveLayer0);
        percussion.addLayer(percussiveLayer1);

        // generate measure
        Measure measure = new Measure(melody, harmony);
        String measureString = measure.toString();

        // reset percussive voice
        percussion.reset();

        return measureString;
    }
}