package music;

public class PercussiveVoice {
    private static PercussiveVoice instance = new PercussiveVoice();
    private int bpm = 120;
    private PercussiveLayer[] layers = new PercussiveLayer[16];
    private int layerIndex = 0;

    private PercussiveVoice() {}

    public static PercussiveVoice getInstance() {
        return instance;
    }

    public void setBPM(int bpm) {
        this.bpm = bpm;
    }

    public void addLayer(PercussiveLayer layer) {
        if (layerIndex < layers.length) {
            layers[layerIndex++] = layer;
        }
    }

    public void reset() {
        bpm = 120;
        layers = new PercussiveLayer[16];
        layerIndex = 0;
    }

    public String toString() {
        String s = "";

        for (int i = 0; i < layerIndex; i++) {
            s += layers[i].toString(i, bpm) + " ";
        }

        return s;
    }
}
