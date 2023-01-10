package util;
import java.io.*;
import org.javatuples.*;
import org.json.simple.*;
import org.json.simple.parser.*;

public class RhythmGenerator {
    private String genre;

    private Pair<Double, String>[] wholeNoteWeights;
    private Pair<Double, String>[] halfNoteWeights;
    private Pair<Double, String>[] quarterNoteWeights;
    private Pair<Double, String>[] eighthNoteWeights;
    private Pair<Double, String>[] sixteenthNoteWeights;
    private Pair<Double, String>[] quarterNoteTripletWeights;
    private Pair<Double, String>[] eighthNoteTripletWeights;

    public RhythmGenerator(String genre) {
        this.genre = genre;
        initializeWeights();
    }

    @SuppressWarnings("unchecked")
    private void initializeWeights() {
        JSONParser parser = new JSONParser();
        JSONObject genresJSONObject;

        try {
            genresJSONObject = (JSONObject) parser.parse(new FileReader("resources/genres.jsonc"));
        } catch(IOException e) {
            System.out.println("Error: Could not load genres.jsonc file.");
            System.exit(1);
            return;
        } catch(ParseException e) {
            System.out.println("Error: Could not parse genres.jsonc file.");
            System.exit(1);
            return;
        }

        JSONObject genreJSONObject = (JSONObject) genresJSONObject.get(genre);
        JSONObject rhythmWeightsJSONObject = (JSONObject) genreJSONObject.get("rhythmWeights");

        JSONObject wholeNoteJSONObject = (JSONObject) rhythmWeightsJSONObject.get("W");
        JSONObject halfNoteJSONObject = (JSONObject) rhythmWeightsJSONObject.get("H");
        JSONObject quarterNoteJSONObject = (JSONObject) rhythmWeightsJSONObject.get("Q");
        JSONObject eighthNoteJSONObject = (JSONObject) rhythmWeightsJSONObject.get("E");
        JSONObject sixteenthNoteJSONObject = (JSONObject) rhythmWeightsJSONObject.get("S");
        JSONObject quarterNoteTripletJSONObject = (JSONObject) rhythmWeightsJSONObject.get("I");
        JSONObject eighthNoteTripletJSONObject = (JSONObject) rhythmWeightsJSONObject.get("T");

        wholeNoteWeights = new Pair[wholeNoteJSONObject.size()];
        halfNoteWeights = new Pair[halfNoteJSONObject.size()];
        quarterNoteWeights = new Pair[quarterNoteJSONObject.size()];
        eighthNoteWeights = new Pair[eighthNoteJSONObject.size()];
        sixteenthNoteWeights = new Pair[sixteenthNoteJSONObject.size()];
        quarterNoteTripletWeights = new Pair[quarterNoteTripletJSONObject.size()];
        eighthNoteTripletWeights = new Pair[eighthNoteTripletJSONObject.size()];

        int i = 0;
        for (Object key : wholeNoteJSONObject.keySet()) {
            wholeNoteWeights[i] = new Pair<>((Double) wholeNoteJSONObject.get(key), (String) key);
            i++;
        }

        i = 0;
        for (Object key : halfNoteJSONObject.keySet()) {
            halfNoteWeights[i] = new Pair<>((Double) halfNoteJSONObject.get(key), (String) key);
            i++;
        }

        i = 0;
        for (Object key : quarterNoteJSONObject.keySet()) {
            quarterNoteWeights[i] = new Pair<>((Double) quarterNoteJSONObject.get(key), (String) key);
            i++;
        }

        i = 0;
        for (Object key : eighthNoteJSONObject.keySet()) {
            eighthNoteWeights[i] = new Pair<>((Double) eighthNoteJSONObject.get(key), (String) key);
            i++;
        }

        i = 0;
        for (Object key : sixteenthNoteJSONObject.keySet()) {
            sixteenthNoteWeights[i] = new Pair<>((Double) sixteenthNoteJSONObject.get(key), (String) key);
            i++;
        }

        i = 0;
        for (Object key : quarterNoteTripletJSONObject.keySet()) {
            quarterNoteTripletWeights[i] = new Pair<>((Double) quarterNoteTripletJSONObject.get(key), (String) key);
            i++;
        }

        i = 0;
        for (Object key : eighthNoteTripletJSONObject.keySet()) {
            eighthNoteTripletWeights[i] = new Pair<>((Double) eighthNoteTripletJSONObject.get(key), (String) key);
            i++;
        }
    }

    public String generateRhythm() {
        String rhythmStringWithVariables = "W";
        String rhythmString = "";

        while (!rhythmStringWithVariables.isEmpty()) {
            char leftmostVariable = rhythmStringWithVariables.charAt(0);
            rhythmStringWithVariables = rhythmStringWithVariables.substring(1);
            String leftmostVariableReplacement;

            // generate a random number between 0 and 1 inclusive
            double randomNumber = Math.random();

            switch (leftmostVariable) {
                case 'W':
                    leftmostVariableReplacement = getNextNoteDuration(wholeNoteWeights, randomNumber);
                    break;
                case 'H':
                    leftmostVariableReplacement = getNextNoteDuration(halfNoteWeights, randomNumber);
                    break;
                case 'Q':
                    leftmostVariableReplacement = getNextNoteDuration(quarterNoteWeights, randomNumber);
                    break;
                case 'E':
                    leftmostVariableReplacement = getNextNoteDuration(eighthNoteWeights, randomNumber);
                    break;
                case 'S':
                    leftmostVariableReplacement = getNextNoteDuration(sixteenthNoteWeights, randomNumber);
                    break;
                case 'I':
                    leftmostVariableReplacement = getNextNoteDuration(quarterNoteTripletWeights, randomNumber);
                    break;
                case 'T':
                    leftmostVariableReplacement = getNextNoteDuration(eighthNoteTripletWeights, randomNumber);
                    break;
                default:
                    leftmostVariableReplacement = "";
                    break;
            }

            rhythmStringWithVariables = leftmostVariableReplacement + rhythmStringWithVariables;

            char leftmostCharacter = rhythmStringWithVariables.charAt(0);

            if (Character.isLowerCase(leftmostCharacter)) {
                rhythmString += leftmostCharacter;
                rhythmStringWithVariables = rhythmStringWithVariables.substring(1);
            }
        }

        return rhythmString;
    }

    private String getNextNoteDuration(Pair<Double, String>[] weights, double randomNumber) {
        double cumulativeProbability = 0;
        for (Pair<Double, String> weight : weights) {
            cumulativeProbability += weight.getValue0();
            if (randomNumber <= cumulativeProbability) {
                return weight.getValue1();
            }
        }
        return "";
    }
}
