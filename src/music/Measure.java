package music;

public class Measure {
    private Voice melody;
    private Voice harmony;
    private PercussiveVoice percussion;

    public Measure(Voice melody, Voice harmony) {
        this.melody = melody;
        this.harmony = harmony;
        this.percussion = PercussiveVoice.getInstance();
    }

    public String toString() {
        String measure = "";

        if (melody != null) {
            measure += melody.toString();
        }

        if (harmony != null) {
            measure += harmony.toString();
        }

        if (percussion != null) {
            measure += percussion.toString();
        }
        
        return measure;
    }
}
