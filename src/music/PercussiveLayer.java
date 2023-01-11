package music;

import java.util.*;

public class PercussiveLayer {
    private final int kitPiece;
    private final LinkedList<PercussiveNote> notes;
    
    public PercussiveLayer(int kitPiece) {
        this.kitPiece = kitPiece;
        this.notes = new LinkedList<>();
    }

    public void addNote(String duration) {
        if (duration.startsWith("R_")) {
            NoteDuration noteDuration = NoteDuration.valueOf(duration.substring(2));
            notes.add(new PercussiveNote(-1, noteDuration.getDuration()));
        } else {
            NoteDuration noteDuration = NoteDuration.valueOf(duration);
            notes.add(new PercussiveNote(kitPiece, noteDuration.getDuration()));
        }
    }

    public String toString(int layer, int bpm) {
        String s = "";

        int time = 0;
        for (PercussiveNote note : notes) {
            float duration = note.getDuration();
            s += "@" + time + " &V9,L" + layer + ",I0," + note.getPitch() + "/" + duration + " ";
            time += (int) (240000 * duration / bpm);
        }

        return s;
    }
}