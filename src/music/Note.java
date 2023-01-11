package music;
public class Note {
    protected final float duration;
    protected final int pitch;
    protected final int velocity;
    protected final int decay;

    public Note(int pitch, float duration) {
        this.pitch = pitch;
        this.duration = duration;
        this.velocity = 64;
        this.decay = 64;
    }

    public Note(int pitch, int velocity, float duration) {
        this.pitch = pitch;
        this.duration = duration;
        this.velocity = velocity;
        this.decay = 64;
    }

    public Note(int pitch, float duration, int decay) {
        this.pitch = pitch;
        this.duration = duration;
        this.velocity = 64;
        this.decay = decay;
    }

    public Note(int pitch, int velocity, float duration, int decay) {
        this.pitch = pitch;
        this.duration = duration;
        this.velocity = velocity;
        this.decay = decay;
    }

    public String getPitch() {
        return (pitch == -1) ? "R" : "" + pitch;
    }

    public float getDuration() {
        return duration;
    }

    public int getVelocity() {
        return velocity;
    }

    public int getDecay() {
        return decay;
    }
}
