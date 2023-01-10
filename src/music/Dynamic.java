package music;

public enum Dynamic {
    PIANISSISSIMO, PIANISSIMO, PIANO, MEZZO_PIANO, MEZZO_FORTE, FORTE, FORTISSIMO, FORTISSISSIMO;

    public int getVelocityValue() {
        switch (this) {
            default:
            case PIANISSISSIMO:
                return 20;
            case PIANISSIMO:
                return 31;
            case PIANO:
                return 42;
            case MEZZO_PIANO:
                return 53;
            case MEZZO_FORTE:
                return 64;
            case FORTE:
                return 80;
            case FORTISSIMO:
                return 96;
            case FORTISSISSIMO:
                return 112;
        }
    }
}