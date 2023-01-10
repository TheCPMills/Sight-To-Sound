package music;
import java.util.*;

public class PercussiveVoice {
    private static PercussiveVoice instance = new PercussiveVoice();

    private Layer[] layers = new Layer[16];
    private String[] kitPieces = new String[16];

    private PercussiveVoice() {}

    public static PercussiveVoice getInstance() {
        return instance;
    }

    public void addNote(PercussiveNote note) {
        int index = 0;
        Boolean kitPieceHasLayer = null;
        while (index < kitPieces.length && kitPieceHasLayer == null) {
            if (kitPieces[index] == null) {
                kitPieceHasLayer = false;
            } else if (kitPieces[index].equals(note.kitPiece)) {
                kitPieceHasLayer = true;
            } else {
                index++;
            }
        }

        if (kitPieceHasLayer != null) {
            if (kitPieceHasLayer) {
                layers[index].addNote(note);
            } else {
                layers[index] = new Layer(index, note.kitPiece);
                kitPieces[index] = note.kitPiece;
                layers[index].addNote(note);
            }
        } else {
            System.out.println("Error: Percussive Voice has too many layers. Cannot add kit piece " + note.kitPiece + ".");
        }
    }

    public void addNotes(LinkedList<PercussiveNote> notes) {
        for (PercussiveNote note : notes) {
            addNote(note);
        }
    }

    public void reset() {
        layers = new Layer[16];
        kitPieces = new String[16];
    }

    public String toString() {
        String s = "V9 ";

        for (Layer layer : layers) {
            if (layer != null) {
                s += layer.toString() + " ";
            }
        }

        return s;
    }
}
