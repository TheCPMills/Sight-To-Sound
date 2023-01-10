package music;
public class Note implements Comparable<Note> {
    protected final float time;
    protected final float duration;
    protected final int pitch;
    protected final int velocity;
    protected final int decay;

    public Note(float time, int pitch, float duration) {
        this.time = time;
        this.pitch = pitch;
        this.duration = duration;
        this.velocity = 64;
        this.decay = 64;
    }

    public Note(float time, int pitch, int velocity, float duration) {
        this.time = time;
        this.pitch = pitch;
        this.duration = duration;
        this.velocity = velocity;
        this.decay = 64;
    }

    public Note(float time, int pitch, float duration, int decay) {
        this.time = time;
        this.pitch = pitch;
        this.duration = duration;
        this.velocity = 64;
        this.decay = decay;
    }

    public Note(float time, int pitch, int velocity, float duration, int decay) {
        this.time = time;
        this.pitch = pitch;
        this.duration = duration;
        this.velocity = velocity;
        this.decay = decay;
    }

    public String toString() {
        if (pitch == -1) {
            return "@" + time + " R/" + duration;
        }

        String s = "@" + time + " " + pitch + "/" + duration;
        if (velocity != 64) {
            s += "a" + velocity;
        }
        if (decay != 64) {
            s += "d" + decay;
        }

        return s;
    }

    @Override
    public int compareTo(Note note) {
        return Float.compare(time, note.time);
    }
}
