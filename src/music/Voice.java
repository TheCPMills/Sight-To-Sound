package music;
import java.util.*;

public class Voice implements Comparable<Voice> {
    private final LinkedList<Note> notes;
    protected final int channel;
    private final int instrument;

    public Voice(int channel) {
        this.notes = new LinkedList<Note>();
        this.channel = channel;
        this.instrument = (channel == 9) ? -1 : 0;

        if (channel == 9) {
            System.out.println("Warning: Percussive channel 9 is not supported.");
        }
    }

    public Voice(int channel, int instrument) {
        this.notes = new LinkedList<Note>();
        this.channel = channel;
        this.instrument = instrument;

        if (channel == 9) {
            System.out.println("Warning: Percussive channel 9 is not supported.");
        }
    }

    public Voice(LinkedList<Note> notes, int channel) {
        this.notes = notes;
        this.channel = channel;
        this.instrument = (channel == 9) ? -1 : 0;

        if (channel == 9) {
            System.out.println("Warning: Percussive channel 9 is not supported.");
        }
    }

    public Voice(LinkedList<Note> notes, int channel, int instrument) {
        this.notes = notes;
        this.channel = channel;
        this.instrument = instrument;

        if (channel == 9) {
            System.out.println("Warning: Percussive channel 9 is not supported.");
        }
    }

    public void addNote(Note note) {
        if (channel != 9) {
            notes.add(note);
        }
    }

    public void addNotes(LinkedList<Note> notes) {
        if (channel != 9) {
            this.notes.addAll(notes);
        }
    }

    public String toString() {
        String s = "V" + channel + " I" + instrument + " ";

        for (Note note : notes) {
            s += note.toString() + " ";
        }

        return s;
    }
    
    @Override
    public int compareTo(Voice voice) {
        return Integer.compare(channel, voice.channel);
    }

    
}
