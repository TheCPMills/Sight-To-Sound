package music;
import java.util.*;

public class Voice implements Comparable<Voice> {
    protected final int channel;
    private final int instrument;
    private final int bpm;
    private final LinkedList<String>[] layers;

    @SuppressWarnings("unchecked")
    public Voice(int channel, int bpm) {
        this.channel = channel;
        this.instrument = (channel == 9) ? -1 : 0;
        this.bpm = bpm;
        this.layers = new LinkedList[16];

        for (int i = 0; i < 16; i++) {
            layers[i] = new LinkedList<String>();
        }

        if (channel == 9) {
            System.out.println("Warning: Percussive channel 9 is not supported.");
        }
    }

    @SuppressWarnings("unchecked")
    public Voice(int channel, int instrument, int bpm) {
        this.channel = channel;
        this.instrument = instrument;
        this.bpm = bpm;
        this.layers = new LinkedList[16];

        for (int i = 0; i < 16; i++) {
            layers[i] = new LinkedList<String>();
        }

        if (channel == 9) {
            System.out.println("Warning: Percussive channel 9 is not supported.");
        }
    }

    public void addNote(Note note) {
        if (channel != 9) {
            float duration = note.getDuration();

            int time; 
            if (layers[0].isEmpty()) {
                time = 0;
            } else {
                String last = layers[0].getLast();
                int lastTime = Integer.parseInt(last.substring(1, last.indexOf(" ")));
                float lastDuration = Float.parseFloat(last.substring(last.indexOf("/") + 1));

                time = lastTime + (int) (240000 * lastDuration / bpm);
            }

            layers[0].add("@" + time + " &V" + channel + ",L0,I" + instrument + "," + note.getPitch() + "/" + duration);
            for (int i = 1; i < 16; i++) {
                layers[i].add("@" + time + " &V" + channel + ",L" + i + ",I" + instrument + ",R/" + duration);
            }
        }
    }

    public void addInterval(Interval interval) {
        if (channel != 9) {
            Note[] notes = interval.getNotes();

            if (notes.length == 1) {
                addNote(notes[0]);
            } else {
                float duration = notes[0].getDuration();

                int time; 
                if (layers[0].isEmpty()) {
                    time = 0;
                } else {
                    String last = layers[0].getLast();
                    int lastTime = Integer.parseInt(last.substring(1, last.indexOf(" ")));
                    float lastDuration = Float.parseFloat(last.substring(last.indexOf("/") + 1));

                    time = lastTime + (int) (240000 * lastDuration / bpm);
                }

                layers[0].add("@" + time + " &V" + channel + ",L0,I" + instrument + "," + notes[0].getPitch() + "/" + duration);
                layers[0].add("@" + time + " &V" + channel + ",L1,I" + instrument + "," + notes[1].getPitch() + "/" + duration);
                for (int i = 2; i < 16; i++) {
                    layers[i].add("@" + time + " &V" + channel + ",L" + i + ",I" + instrument + ",R/" + duration);
                }
            }
        }
    }

    public void addChord(Chord chord) {
        if (channel != 9) {
            Note[] notes = chord.getNotes();
            float duration = notes[0].getDuration();

            int time; 
            if (layers[0].isEmpty()) {
                time = 0;
            } else {
                String last = layers[0].getLast();
                int lastTime = Integer.parseInt(last.substring(1, last.indexOf(" ")));
                float lastDuration = Float.parseFloat(last.substring(last.indexOf("/") + 1));

                time = lastTime + (int) (240000 * lastDuration / bpm);
            }

            int chordSize = Math.min(notes.length, 16);
            for (int i = 0; i < chordSize; i++) {
                layers[i].add("@" + time + " &V" + channel + ",L" + i + ",I" + instrument + "," + notes[i].getPitch() + "/" + duration);
            }
            for (int i = notes.length; i < 16; i++) {
                layers[i].add("@" + time + " &V" + channel + ",L" + i + ",I" + instrument + ",R/" + duration);
            }
        }
    }

    public String toString() {
        String s = "";

        for (LinkedList<String> layer : layers) {
            for (String note : layer) {
                s += note + " ";
            }
            s += " ";
        }

        return s;
    }
    
    @Override
    public int compareTo(Voice voice) {
        return Integer.compare(channel, voice.channel);
    }
}
