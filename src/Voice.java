import java.util.*;

public class Voice implements Comparable<Voice> {
    private final LinkedList<Note> notes;
    private final int channel;
    private final int instrument;

    public Voice(int channel) {
        this.notes = new LinkedList<Note>();
        this.channel = channel;
        this.instrument = (channel == 9) ? -1 : 0;
    }

    public Voice(int channel, int instrument) {
        this.notes = new LinkedList<Note>();
        this.channel = channel;
        this.instrument = instrument;
    }

    public Voice(LinkedList<Note> notes, int channel) {
        this.notes = notes;
        this.channel = channel;
        this.instrument = (channel == 9) ? -1 : 0;
    }

    public Voice(LinkedList<Note> notes, int channel, int instrument) {
        this.notes = notes;
        this.channel = channel;
        this.instrument = instrument;
    }

    public void add(Note note) {
        notes.add(note);
    }

    public void addNotes(LinkedList<Note> notes) {
        this.notes.addAll(notes);
    }

    public String toString() {
        String s = "V" + channel;
        
        if (instrument == -1) {
            s += " L0 ";
        } else {
            s += " I" + instrument + " ";
        }

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
