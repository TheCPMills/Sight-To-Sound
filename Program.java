import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public class Program {
    public static void main(String[] args) throws IOException {
        final double sampleRate = 44100.0;
        final double frequency = 523.25;
        final double amplitude = 1.0;
        final double seconds = 5.0;
        final double pi = Math.PI;

        float[] buffer = new float[(int) (seconds * sampleRate)];

        for (int sample = 0; sample < buffer.length / 4; sample++) {
            double time = sample / sampleRate;
            buffer[sample] = (float) (amplitude * Math.sin(frequency * pi * time));
        }

        for (int sample = buffer.length / 4; sample < buffer.length / 2; sample++) {
            double time = sample / sampleRate;
            buffer[sample] = (float) (amplitude * Math.sin(1.25 * frequency * pi * time));
        }

        for (int sample = buffer.length / 2; sample < 3 * buffer.length / 4; sample++) {
            double time = sample / sampleRate;
            buffer[sample] = (float) (amplitude * Math.sin(1.5 * frequency * pi * time));
        }

        for (int sample = 3 * buffer.length / 4; sample < buffer.length; sample++) {
            double time = sample / sampleRate;
            buffer[sample] = (float) (amplitude * (Math.sin(frequency * pi * time) + Math.sin(1.25 * frequency * pi * time) + Math.sin(1.5 * frequency * pi * time)) / 4);
        }

        final byte[] byteBuffer = new byte[buffer.length * 2];

        int bufferIndex = 0;
        for (int i = 0; i < byteBuffer.length; i++) {
            final int x = (int) (buffer[bufferIndex++] * 32767.0);

            byteBuffer[i++] = (byte) x;
            byteBuffer[i] = (byte) (x >>> 8);
        }

        File out = new File("output.wav");

        final boolean bigEndian = false;
        final boolean signed = true;

        final int bits = 16;
        final int channels = 1;

        AudioFormat format = new AudioFormat((float) sampleRate, bits, channels, signed, bigEndian);
        ByteArrayInputStream bais = new ByteArrayInputStream(byteBuffer);
        AudioInputStream audioInputStream = new AudioInputStream(bais, format, buffer.length);
        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, out);
        audioInputStream.close();
    }
}