package music;

public class Interval {
    private final float time;
    private final int tonic;
    private Note[] notes;

    public Interval(float time, int tonic, String interval, float duration) {
        this.time = time;
        this.tonic = tonic;
        parseInterval(interval, duration);
    }

    private void parseInterval(String interval, float duration) {
        switch(interval) {
            default:
            case "P1":
                notes = new Note[1];
                notes[0] = new Note(time, tonic, duration);
                break;
            case "m2":
                notes = new Note[2];
                notes[0] = new Note(time, tonic, duration);
                notes[1] = new Note(time, tonic + 1, duration);
                break;
            case "M2":
                notes = new Note[2];
                notes[0] = new Note(time, tonic, duration);
                notes[1] = new Note(time, tonic + 2, duration);
                break;
            case "m3":
                notes = new Note[2];
                notes[0] = new Note(time, tonic, duration);
                notes[1] = new Note(time, tonic + 3, duration);
                break;
            case "M3":
                notes = new Note[2];
                notes[0] = new Note(time, tonic, duration);
                notes[1] = new Note(time, tonic + 4, duration);
                break;
            case "P4":
                notes = new Note[2];
                notes[0] = new Note(time, tonic, duration);
                notes[1] = new Note(time, tonic + 5, duration);
                break;
            case "TT":
                notes = new Note[2];
                notes[0] = new Note(time, tonic, duration);
                notes[1] = new Note(time, tonic + 6, duration);
                break;
            case "P5":
                notes = new Note[2];
                notes[0] = new Note(time, tonic, duration);
                notes[1] = new Note(time, tonic + 7, duration);
                break;
            case "m6":
                notes = new Note[2];
                notes[0] = new Note(time, tonic, duration);
                notes[1] = new Note(time, tonic + 8, duration);
                break;
            case "M6":
                notes = new Note[2];
                notes[0] = new Note(time, tonic, duration);
                notes[1] = new Note(time, tonic + 9, duration);
                break;
            case "m7":
                notes = new Note[2];
                notes[0] = new Note(time, tonic, duration);
                notes[1] = new Note(time, tonic + 10, duration);
                break;
            case "M7":
                notes = new Note[2];
                notes[0] = new Note(time, tonic, duration);
                notes[1] = new Note(time, tonic + 11, duration);
                break;
            case "P8":
                notes = new Note[2];
                notes[0] = new Note(time, tonic, duration);
                notes[1] = new Note(time, tonic + 12, duration);
                break;
        }
    }
}