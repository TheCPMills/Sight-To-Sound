package music;

public enum Tempo {
    GRAVE, LARGO, LENTO, ADAGIO, ANDANTE, MODERATO, ALLEGRO, VIVACE, PRESTO;

    public int getBPM() {
        switch (this) {
            default:
            case GRAVE:
                // generate int between 25 and 40
                return 25 + (int) (Math.random() * ((40 - 25) + 1));
            case LARGO:
                // generate int between 40 and 60
                return 40 + (int) (Math.random() * ((60 - 40) + 1));
            case LENTO:
                // generate int between 45 and 60
                return 45 + (int) (Math.random() * ((60 - 45) + 1));
            case ADAGIO:
                // generate int between 66 and 76
                return 66 + (int) (Math.random() * ((76 - 66) + 1));
            case ANDANTE:
                // generate int between 76 and 108
                return 76 + (int) (Math.random() * ((108 - 76) + 1));
            case MODERATO:
                // generate int between 108 and 120
                return 108 + (int) (Math.random() * ((120 - 108) + 1));
            case ALLEGRO:
                // generate int between 120 and 156
                return 120 + (int) (Math.random() * ((156 - 120) + 1));
            case VIVACE:
                // generate int between 156 and 176
                return 156 + (int) (Math.random() * ((176 - 156) + 1));
            case PRESTO:
                // generate int between 168 and 200
                return 168 + (int) (Math.random() * ((200 - 168) + 1));
        }
    }
}