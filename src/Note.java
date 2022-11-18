public class Note implements Comparable<Note> {
    private final float time;
    private final float duration;
    private final int pitch;
    private final int velocity;
    private final int decay;

    final boolean percussionMode;
    final String kitPiece;

    public Note(float time, int pitch, float duration) {
        this.time = time;
        this.pitch = pitch;
        this.duration = duration;
        this.velocity = 64;
        this.decay = 64;

        this.percussionMode = false;
        this.kitPiece = null;
    }

    public Note(float time, int pitch, int velocity, float duration) {
        this.time = time;
        this.pitch = pitch;
        this.duration = duration;
        this.velocity = velocity;
        this.decay = 64;

        this.percussionMode = false;
        this.kitPiece = null;
    }

    public Note(float time, int pitch, float duration, int decay) {
        this.time = time;
        this.pitch = pitch;
        this.duration = duration;
        this.velocity = 64;
        this.decay = decay;

        this.percussionMode = false;
        this.kitPiece = null;
    }

    public Note(float time, int pitch, int velocity, float duration, int decay) {
        this.time = time;
        this.pitch = pitch;
        this.duration = duration;
        this.velocity = velocity;
        this.decay = decay;

        this.percussionMode = false;
        this.kitPiece = null;
    }

    public Note(float time, String kitPiece, float duration) {
        this.time = time;
        this.pitch = -1;
        this.duration = duration;
        this.velocity = 64;
        this.decay = 64;

        this.percussionMode = true;
        this.kitPiece = kitPiece;
    }

    public String toString() {
        String s;
        if (percussionMode) {
            s = "@" + time + " [" + kitPiece + "]/" + duration;
        } else {
            if (pitch == -1) {
                return "@" + time + " R/" + duration;
            }

            s = "@" + time + " " + pitch + "/" + duration;
            if (velocity != 64) {
                s += "a" + velocity;
            }
            if (decay != 64) {
                s += "d" + decay;
            }
        }

        return s;
    }

    @Override
    public int compareTo(Note note) {
        return Float.compare(time, note.time);
    }
}
