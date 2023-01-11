package music;

public class PercussiveNote extends Note {
    public PercussiveNote(int pitch, float duration) {
        super(pitch, duration);
    }

    public PercussiveNote(String kitPiece, float duration) {
        super(kitPieceToMidi(kitPiece), duration);
    }

    private static int kitPieceToMidi(String kitPiece) {
        switch (kitPiece) {
            default:
            case "REST":
                return -1;
            case "ACOUSTIC_BASS_DRUM":
                return 35;
            case "BASS_DRUM":
                return 36;
            case "SIDE_STICK":
                return 37;
            case "ACOUSTIC_SNARE":
                return 38;
            case "HAND_CLAP":
                return 39;
            case "ELECTRIC_SNARE":
                return 40;
            case "LO_FLOOR_TOM":
                return 41;
            case "CLOSED_HI_HAT":
                return 42;
            case "HI_FLOOR_TOM":
                return 43;
            case "PEDAL_HI_HAT":
                return 44;
            case "LO_TOM":
                return 45;
            case "OPEN_HI_HAT":
                return 46;
            case "LO_MID_TOM":
                return 47;
            case "HI_MID_TOM":
                return 48;
            case "CRASH_CYMBAL_1":
                return 49;
            case "HI_TOM":
                return 50;
            case "RIDE_CYMBAL_1":
                return 51;
            case "CHINESE_CYMBAL":
                return 52;
            case "RIDE_BELL":
                return 53;
            case "TAMBOURINE":
                return 54;
            case "SPLASH_CYMBAL":
                return 55;
            case "COWBELL":
                return 56;
            case "CRASH_CYMBAL_2":
                return 57;
            case "VIBRASLAP":
                return 58;
            case "RIDE_CYMBAL_2":
                return 59;
            case "HI_BONGO":
                return 60;
            case "LO_BONGO":
                return 61;
            case "MUTE_HI_CONGA":
                return 62;
            case "OPEN_HI_CONGA":
                return 63;
            case "LO_CONGO":
                return 64;
            case "HI_TIMBALE":
                return 65;
            case "LO_TIMBALE":
                return 66;
            case "HI_AGOGO":
                return 67;
            case "LO_AGOGO":
                return 68;
            case "CABASA":
                return 69;
            case "MARACAS":
                return 70;
            case "SHORT_WHISTLE":
                return 71;
            case "LONG_WHISTLE":
                return 72;
            case "SHORT_GUIRO":
                return 73;
            case "LONG_GUIRO":
                return 74;
            case "CLAVES":
                return 75;
            case "HI_WOOD_BLOCK":
                return 76;
            case "LO_WOOD_BLOCK":
                return 77;
            case "MUTE_CUICA":
                return 78;
            case "OPEN_CUICA":
                return 79;
            case "MUTE_TRIANGLE":
                return 80;
            case "OPEN_TRIANGLE":
                return 81;
        }
    }
}
