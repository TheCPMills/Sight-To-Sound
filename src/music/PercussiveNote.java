package music;

public class PercussiveNote extends Note {
    final String kitPiece;

    public PercussiveNote(float time, int pitch, float duration) {
        super(time, pitch, duration);
        this.kitPiece = midiToKitPiece(pitch);
    }

    public PercussiveNote(float time, String kitPiece, float duration) {
        super(time, kitPieceToMidi(kitPiece), duration);
        this.kitPiece = kitPiece;
    }

    private static String midiToKitPiece(int midiNote) {
        switch (midiNote) {
            default:
            case -1:
                return "R";
            case 35:
                return "ACOUSTIC_BASS_DRUM";
            case 36:
                return "BASS_DRUM";
            case 37:
                return "SIDE_STICK";
            case 38:
                return "ACOUSTIC_SNARE";
            case 39:
                return "HAND_CLAP";
            case 40:
                return "ELECTRIC_SNARE";
            case 41:
                return "LOW_FLOOR_TOM";
            case 42:
                return "CLOSED_HI_HAT";
            case 43:
                return "HIGH_FLOOR_TOM";
            case 44:
                return "PEDAL_HI_HAT";
            case 45:
                return "LOW_TOM";
            case 46:
                return "OPEN_HI_HAT";
            case 47:
                return "LOW_MID_TOM";
            case 48:
                return "HI_MID_TOM";
            case 49:
                return "CRASH_CYMBAL_1";
            case 50:
                return "HIGH_TOM";
            case 51:
                return "RIDE_CYMBAL_1";
            case 52:
                return "CHINESE_CYMBAL";
            case 53:
                return "RIDE_BELL";
            case 54:
                return "TAMBOURINE";
            case 55:
                return "SPLASH_CYMBAL";
            case 56:
                return "COWBELL";
            case 57:
                return "CRASH_CYMBAL_2";
            case 58:
                return "VIBRASLAP";
            case 59:
                return "RIDE_CYMBAL_2";
            case 60:
                return "HI_BONGO";
            case 61:
                return "LOW_BONGO";
            case 62:
                return "MUTE_HI_CONGA";
            case 63:
                return "OPEN_HI_CONGA";
            case 64:
                return "LOW_CONGO";
            case 65:
                return "HIGH_TIMBALE";
            case 66:
                return "LOW_TIMBALE";
            case 67:
                return "HIGH_AGOGO";
            case 68:
                return "LOW_AGOGO";
            case 69:
                return "CABASA";
            case 70:
                return "MARACAS";
            case 71:
                return "SHORT_WHISTLE";
            case 72:
                return "LONG_WHISTLE";
            case 73:
                return "SHORT_GUIRO";
            case 74:
                return "LONG_GUIRO";
            case 75:
                return "CLAVES";
            case 76:
                return "HI_WOOD_BLOCK";
            case 77:
                return "LOW_WOOD_BLOCK";
            case 78:
                return "MUTE_CUICA";
            case 79:
                return "OPEN_CUICA";
            case 80:
                return "MUTE_TRIANGLE";
            case 81:
                return "OPEN_TRIANGLE";
        }
    }

    private static int kitPieceToMidi(String kitPiece) {
        switch (kitPiece) {
            default:
            case "R":
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
            case "LOW_FLOOR_TOM":
                return 41;
            case "CLOSED_HI_HAT":
                return 42;
            case "HIGH_FLOOR_TOM":
                return 43;
            case "PEDAL_HI_HAT":
                return 44;
            case "LOW_TOM":
                return 45;
            case "OPEN_HI_HAT":
                return 46;
            case "LOW_MID_TOM":
                return 47;
            case "HI_MID_TOM":
                return 48;
            case "CRASH_CYMBAL_1":
                return 49;
            case "HIGH_TOM":
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
            case "LOW_BONGO":
                return 61;
            case "MUTE_HI_CONGA":
                return 62;
            case "OPEN_HI_CONGA":
                return 63;
            case "LOW_CONGO":
                return 64;
            case "HIGH_TIMBALE":
                return 65;
            case "LOW_TIMBALE":
                return 66;
            case "HIGH_AGOGO":
                return 67;
            case "LOW_AGOGO":
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
            case "LOW_WOOD_BLOCK":
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

    public String toString() {
        if (pitch == -1) {
            return "@" + time + " R/" + duration;
        }
        
        String s = "@" + time + " [" + kitPiece + "]/" + duration;
        return s;
    }
}
