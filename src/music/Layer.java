package music;
import java.util.*;

public class Layer implements Comparable<Layer> {
    private final int layerNumber;
    private final LinkedList<PercussiveNote> notes;
    private final String kitPiece;

    public Layer(int layerNumber, String kitPiece) {
        this.layerNumber = layerNumber;
        this.notes = new LinkedList<PercussiveNote>();
        this.kitPiece = kitPiece;
    }

    public Layer(int layerNumber, LinkedList<PercussiveNote> notes, String kitPiece) {
        this.layerNumber = layerNumber;
        this.notes = notes;
        this.kitPiece = kitPiece;
    }

    public void addNote(PercussiveNote note) {
        if (note.kitPiece == kitPiece) {
            this.notes.add(note);
        }
    }

    public void addNotes(LinkedList<PercussiveNote> notes) {
        for (PercussiveNote note : notes) {
            addNote(note);
        }
    }

    public String toString() {
        String s = ""; // "L" + layerNumber + " ";

        for (PercussiveNote note : notes) {
            s += note.toString() + " ";
        }

        return s;
    }

    @Override
    public int compareTo(Layer layer) {
        return Integer.compare(layerNumber, layer.layerNumber);
    }
}
