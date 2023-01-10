import java.util.*;
import java.io.*;

import org.json.simple.*;
import org.json.simple.parser.*;

public class PitchGenerator {
    // Algorithmic Constants
    private final int MELODIC_CENTRAL_OCTAVE = 5;
    private final int MELODIC_RANGE_MIN = -12;
    private final int MELODIC_RANGE_MAX = 19;

    private char tonic;
    private String mode;
    private int[] pitchSet;

    public PitchGenerator(char tonic, String mode) {
        this.tonic = tonic;
        this.mode = mode;

        generatePitchSet();
    }

    private void generatePitchSet() {
        LinkedList<Integer> pitchList = new LinkedList<Integer>();

        int centralPitch = calculateCentralPitch(tonic);

        int pitch = centralPitch + MELODIC_RANGE_MIN;
        int highestPitch = centralPitch + MELODIC_RANGE_MAX;

        // get interval pattern from mode
        JSONParser parser = new JSONParser();
        JSONObject modesJSONObject;

        try {
            modesJSONObject = (JSONObject) parser.parse(new FileReader("resources/modes.jsonc"));
        } catch (IOException e) {
            System.out.println("Error: Could not load modes.jsonc file.");
            System.exit(1);
            return;
        } catch (ParseException e) {
            System.out.println("Error: Could not parse modes.jsonc file.");
            System.exit(1);
            return;
        }

        JSONObject modeJSONObject = (JSONObject) modesJSONObject.get(mode);
        JSONArray intervalPatternJSONArray = (JSONArray) modeJSONObject.get("intervalPattern");

        // convert interval pattern to array of integers
        int[] intervalPattern = new int[intervalPatternJSONArray.size()];
        for (int i = 0; i < intervalPatternJSONArray.size(); i++) {
            intervalPattern[i] = (int) ((long) intervalPatternJSONArray.get(i));
        }

        int index = 0;
        do {
            pitchList.add(pitch);
            pitch += intervalPattern[index];
            index = (index + 1) % intervalPattern.length;
        } while (pitch <= highestPitch);

        pitchSet = pitchList.stream().mapToInt(i -> i).toArray();
    }

    private int calculateCentralPitch(char tonic) {
        int pitch;
        switch (tonic) { // lowercase indicates flat
            case 'C':
                pitch = 0;
                break;
            case 'd':
                pitch = 1;
                break;
            case 'D':
                pitch = 2;
                break;
            case 'e':
                pitch = 3;
                break;
            case 'E':
                pitch = 4;
                break;
            case 'F':
                pitch = 5;
                break;
            case 'g':
                pitch = 6;
                break;
            case 'G':
                pitch = 7;
                break;
            case 'a':
                pitch = 8;
                break;
            case 'A':
                pitch = 9;
                break;
            case 'b':
                pitch = 10;
                break;
            case 'B':
                pitch = 11;
                break;
            default:
                return -1;
        }

        pitch += MELODIC_CENTRAL_OCTAVE * 12;
        return pitch;
    }

    public int generateNextPitch(int color, int lastPitch) {
        return pitchSet[Math.abs(color + lastPitch) % pitchSet.length];
    }
}
