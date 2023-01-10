package music;

public enum NoteDuration {
    SEMIQUAVER, // sixteenth note
    QUAVER, // eighth note
    CROTCHET, // quarter note
    MINIM, // half note
    SEMIBREVE, // whole note
    QUAVER_TRIPLET, // triplet eighth note
    CROCHET_TRIPLET; // triplet quarter note

    public float getDuration() {
        switch (this) {
            case SEMIQUAVER:
                return 1 / 16.0f;
            case QUAVER:
                return 1 / 8.0f;
            case CROTCHET:
                return 1 / 4.0f;
            case MINIM:
                return 1 / 2.0f;
            case SEMIBREVE:
                return 1.0f;
            case QUAVER_TRIPLET:
                return 1 / 12.0f;
            case CROCHET_TRIPLET:
                return 1 / 6.0f;
            default:
                return 0.0f;
        }
    }
}
