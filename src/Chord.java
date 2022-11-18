import java.util.*;
import java.util.stream.*;

public class Chord {
    private final float time;
    private final int tonic;
    private Note[] notes;

    private final HashMap<String, int[]> chords = new HashMap<>() {{
        put("maj", new int[]{0, 4, 7});
        put("maj6", new int[]{0, 4, 7, 9});
        put("maj7", new int[]{0, 4, 7, 11});
        put("maj9", new int[]{0, 4, 7, 11, 14});
        put("maj13", new int[]{0, 7, 11, 14, 16, 21});

        put("min", new int[]{0, 3, 7});
        put("min6", new int[]{0, 3, 7, 9});
        put("min7", new int[]{0, 3, 7, 10});
        put("min9", new int[]{0, 3, 7, 10, 14});
        put("min11", new int[]{0, 7, 10, 14, 15, 17});
        put("min13", new int[]{0, 7, 10, 14, 15, 21});
        put("minMaj7", new int[]{0, 3, 7, 11});
        put("minMaj9", new int[]{0, 3, 7, 11, 14});

        put("dom7", new int[]{0, 4, 7, 10});
        put("dom9", new int[]{0, 4, 7, 10, 14});
        put("dom11", new int[]{0, 7, 10, 14, 17});
        put("dom13", new int[]{0, 7, 10, 14, 16, 21});

        put("aug", new int[]{0, 4, 8});
        put("aug7", new int[]{0, 4, 8, 10});

        put("dim", new int[]{0, 3, 6});
        put("dim7", new int[]{0, 3, 6, 9});

        put("sus2", new int[]{0, 2, 7});
        put("sus4", new int[]{0, 5, 7});

        put("5", new int[]{0, 7});
    }};

    private final int[] intervals = {0, 2, 4, 5, 7, 9, 11, 12, 14, 16, 17, 19, 21};

    /**
     * VITAL NOTE: The order of {@code}notes{@code} is important. It must adhere the following format:
     * <p>
     * "〈chord type〉 add〈added note〉... no〈omitted note〉... b〈flatted note〉... ＃〈sharpened note〉... (^〈inversion number〉or /〈bass note〉)"
     * <p>
     */
    public Chord(float time, int tonic, String chord, float duration) {
        this.time = time;
        this.tonic = tonic;
        parseChord(chord, duration);
    }

    private void parseChord(String chord, float duration) {
        LinkedList<Integer> noteValues = new LinkedList<>();

        String[] chordParts = chord.split(" ");

        for (String chordPart : chordParts) {
            if (chordPart.startsWith("add")) {
                int interval = intervals[Integer.parseInt(chordPart.substring(3)) - 1];
                addNoDuplicates(noteValues, interval);
            } else if (chordPart.startsWith("no")) {
                int interval = intervals[Integer.parseInt(chordPart.substring(2)) - 1];
                noteValues.remove((Integer) interval);
            } else if (chordPart.startsWith("b")) {
                int interval = intervals[Integer.parseInt(chordPart.substring(1)) - 1];
                int index = noteValues.indexOf(interval);

                if (index != -1 && !noteValues.contains((Integer) (interval - 1))) {
                    noteValues.set(index, interval - 1);
                }
            } else if (chordPart.startsWith("#")) {
                try {
                    int interval = intervals[Integer.parseInt(chordPart.substring(1)) - 1];
                    int index = noteValues.indexOf(interval);

                    if (index != -1 && !noteValues.contains((Integer) (interval + 1))) {
                        noteValues.set(index, interval + 1);
                    }
                } catch (IndexOutOfBoundsException e) {}
            } else if (chordPart.startsWith("^")) {
                int inversion = Integer.parseInt(chordPart.substring(1)) % noteValues.size();
                inversion(noteValues, inversion);
            } else if (chordPart.startsWith("/")) {
                int bassNote = Integer.parseInt(chordPart.substring(1));
                bassNote = bassNote - tonic;
                addNoDuplicates(noteValues, bassNote);

                int index = noteValues.indexOf(bassNote);
                inversion(noteValues, index);
            } else {
                int[] intervalNoteValues = chords.get(chordPart);
                for (int intervalNoteValue : intervalNoteValues) {
                    noteValues.add(intervalNoteValue);
                }
            }
        }

        this.notes = new Note[noteValues.size()];
        for (int i = 0; i < noteValues.size(); i++) {
            notes[i] = new Note(time, tonic + noteValues.get(i), duration);
        }
    }

    private void inversion(LinkedList<Integer> list, int n) {
        List<Integer> temp = new LinkedList<>(list.subList(0, n));
        list.removeAll(temp);
        temp = temp.stream().map(i -> i + 12).collect(Collectors.toList());
        addAllNoDuplicates(list, temp);
    }

    private void addNoDuplicates(LinkedList<Integer> noteValues, int noteValue) {
        if (!noteValues.contains(noteValue)) {
            noteValues.add(noteValue);
            Collections.sort(noteValues);
        }
    }

    private void addAllNoDuplicates(LinkedList<Integer> noteValues, List<Integer> newNoteValues) {
        for (int noteValue : newNoteValues) {
            if (!noteValues.contains(noteValue)) {
                noteValues.add(noteValue);
            }
        }
        Collections.sort(noteValues);
    }

    public Note[] getNotes() {
        return notes;
    }
}
