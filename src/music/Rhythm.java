package music;
import java.util.*;

public class Rhythm {
    public LinkedList<NoteDuration> durations;

    public Rhythm(String rhythmString) {
        durations = new LinkedList<NoteDuration>();
        for (int i = 0; i < rhythmString.length(); i++) {
            switch (rhythmString.charAt(i)) {
                case 's':
                    durations.add(NoteDuration.SEMIQUAVER);
                    break;
                case 'e':
                    durations.add(NoteDuration.QUAVER);
                    break;
                case 'q':
                    durations.add(NoteDuration.CROTCHET);
                    break;
                case 'h':
                    durations.add(NoteDuration.MINIM);
                    break;
                case 'w':
                    durations.add(NoteDuration.SEMIBREVE);
                    break;
                case 't':
                    durations.add(NoteDuration.QUAVER_TRIPLET);
                    break;
                case 'i':
                    durations.add(NoteDuration.CROCHET_TRIPLET);
                    break;
                default:
                    break;
            }
        }
    }

    public Rhythm(LinkedList<NoteDuration> durations) {
        this.durations = durations;
    }

    public LinkedList<NoteDuration> getDurations() {
        return durations;
    }
}