import util.*;

public class Test {
    public static void main(String[] args) {
        RhythmGenerator rhythmGenerator = new RhythmGenerator("classical");
        String rhythmString = rhythmGenerator.generateRhythm();
        System.out.println(rhythmString);
    }
}
