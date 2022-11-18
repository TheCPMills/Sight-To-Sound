import java.io.*;

import javax.sound.midi.InvalidMidiDataException;

import org.jfugue.player.*;
// import org.jfugue.rhythm.*;
import org.jfugue.pattern.*;
// import org.jfugue.theory.*;
import org.jfugue.midi.*;

public class TestWithAPI {
    public static void main(String[] args) {
        Player player = new Player();
        Pattern song = new Pattern();

        // ====================================================
        // | Test 1: Read a MIDI file and print out the notes |
        // ====================================================
        try {
            File filePath = new File("assets/test1.mid");
            song = MidiFileManager.loadPatternFromMidi(filePath);

            File file = new File("assets/test1.txt");
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(song.toString());
            fileWriter.close();
        } catch (InvalidMidiDataException | IOException e) {
            e.printStackTrace();
        }

        // ========================================
        // | Test 2: Create a pattern and play it |
        // ========================================
        song.clear();
        song.add(args[0]);

        player.play(song);

        // =======================================================
        // | Test 3: Read a pattern and save it as a MIDI file |
        // =======================================================
        song.clear();

        try {
            File file = new File("assets/test3.txt");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                song.add(line);
            }

            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            File filePath = new File("assets/test3.mid");
            MidiFileManager.savePatternToMidi(song, filePath);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
