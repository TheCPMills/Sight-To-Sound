package main;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import java.io.*;

public class PlaySequencer {

	/**
	 * To use a specific sequencer, you can run {@link MidiDeviceDisplay} to
	 * discover the names of other sequencers on your system.
	 * 
	 * @see KeyboardToSynth
	 */
	private static final String SEQ_DEV_NAME = "default";
	private static final String SEQ_PROP_KEY = "javax.sound.midi.Sequence";

	public static void main(String[] args) {
		new PlaySequencer().run();
	}

	private void run() {
		Sequencer sequencer = getSequencer(); // Get default sequencer, if it exists

		if (sequencer == null) {
			return;
		}

		try {
			sequencer.open();
		} catch (MidiUnavailableException e1) {
			e1.printStackTrace();
			return;
		}

		sequencer.setTempoInBPM(65.0f);
		Sequence sequence = null;

		try {
			sequence = getMidiInputData(); // Input MIDI data
			sequencer.setSequence(sequence);
		} catch (InvalidMidiDataException e1) {
			e1.printStackTrace();
			return;
		}

		sleep(200); // sleep or first note is too long
		sequencer.start(); // Play sequence

		while (sequencer.isRunning()) {
			sleep(1000);
		}

		// Sleep or last note is clipped
		sleep(200);
		sequencer.close();

		try {
			MidiSystem.write(sequence, 1, new File("Goodbye.mid"));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// Create a sequence and set all MIDI events
	private Sequence getMidiInputData() {
		int ticksPerQuarterNote = 4;
		Sequence seq;
		try {
			seq = new Sequence(Sequence.PPQ, ticksPerQuarterNote);
			setMidiEvents(seq.createTrack());
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
			return null;
		}
		return seq;
	}

	private void setMidiEvents(Track track) {
		track.add(makeEvent(192, 1, 28, 0, 0));
		track.add(makeEvent(192, 2, 33, 0, 0));
		addMelody(track, 0);
		addHarmony(track, 1);
		addBass(track, 2);
	}

	private void addMelody(Track track, int channel) {
		int tick = 0;

		int notes[] = { 0, 62, 67, 62, 69, 71, 0, 71, 71, 69, 67, 69, 71, 69, 0, 67, 66, 64, 66, 67, 0, 64, 63, 67, 0,
				67, 69, 71, 69, 67 };
		int durations[] = { 60, 2, 14, 2, 1, 9, 7, 1, 2, 2, 1, 2, 2, 2, 1, 2, 2, 2, 1, 1, 2, 1, 2, 2, 1, 2, 1, 1, 1,
				15 };
		int velocities[] = { 0, 99, 99, 99, 99, 99, 0, 99, 99, 99, 99, 99, 99, 99, 0, 99, 99, 99, 99, 99, 0, 99, 99, 99,
				0, 99, 99, 99, 99, 99 };

		for (int i = 0; i < notes.length; i++) {
			if (i == 0) {
				addNote(track, channel, notes[i], velocities[i], durations[i], tick);
			} else {
				addNote(track, channel, notes[i], velocities[i], durations[i], tick += durations[i - 1]);
			}
		}
	}

	private void addHarmony(Track track, int channel) {
		int tick = 0;

		int chords[][] = { { 50, 55, 59 }, { 50, 55, 59 }, { 50, 55, 59 }, { 50, 55, 59 }, { 50, 55, 59 },
				{ 50, 55, 59 }, { 50, 55, 59 }, { 50, 55, 59 }, { 50, 55, 59 }, { 50, 55, 59 }, { 50, 55, 59 },
				{ 50, 55, 59 }, { 50, 55, 59 }, { 50, 55, 59 }, { 50, 55, 59 }, { 0 }, { 50, 55, 59 }, { 50, 55, 59 },
				{ 50, 55, 59 }, { 50, 55, 59 }, { 52, 54, 57 }, { 52, 54, 57 }, { 52, 54, 57 }, { 52, 54, 59 },
				{ 52, 55, 59 }, { 52, 55, 59 }, { 50, 55, 59 }, { 50, 55, 59 }, { 52, 55, 60 }, { 52, 55, 62 },
				{ 51, 55, 59 }, { 52, 55, 60 }, { 50, 55, 59 }, { 50, 55, 60 }, { 52, 54, 57 }, { 52, 54, 59 },
				{ 52, 55, 59 } };
		int durations[] = { 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4,
				4, 4, 4, 4, 4, 8 };
		int velocities[] = { 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
				99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99 };

		for (int i = 0; i < chords.length; i++) {
			if (i == 0) {
				addChord(track, channel, chords[i], velocities[i], durations[i], tick);
			} else {
				addChord(track, channel, chords[i], velocities[i], durations[i], tick += durations[i - 1]);
			}
		}
	}

	private void addBass(Track track, int channel) {
		int tick = 0;

		int notes[] = { 31, 38, 31, 38, 31, 38, 31, 0, 31, 38, 39, 40, 40, 38, 40, 40, 40, 43, 43, 45, 42, 40 };
		int durations[] = { 14, 2, 14, 2, 14, 2, 12, 4, 14, 2, 16, 6, 2, 8, 6, 2, 8, 6, 2, 6, 2, 8 };
		int velocities[] = { 99, 99, 99, 99, 99, 99, 99, 0, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99 };

		for (int i = 0; i < notes.length; i++) {
			if (i == 0) {
				addNote(track, channel, notes[i], velocities[i], durations[i], tick);
			} else {
				addNote(track, channel, notes[i], velocities[i], durations[i], tick += durations[i - 1]);
			}
		}
	}

	private void addNote(Track track, int channel, int note, int velocity, int duration, int startTick) {
		addMidiEvent(track, ShortMessage.NOTE_ON, channel, note, velocity, startTick);
		addMidiEvent(track, ShortMessage.NOTE_OFF, channel, note, 0, startTick + duration);
	}

	private void addChord(Track track, int channel, int[] notes, int velocity, int duration, int startTick) {
		for (int i = 0; i < notes.length; i++) {
			addNote(track, channel, notes[i], velocity, duration, startTick);
		}
	}

	// Create a MIDI event and add it to the track
	private void addMidiEvent(Track track, int command, int channel, int data1, int data2, int tick) {
		ShortMessage message = new ShortMessage();
		try {
			message.setMessage(command, channel, data1, data2);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		track.add(new MidiEvent(message, tick));
	}

	/**
	 * @return a specific sequencer object by setting the system property, otherwise
	 *         the default
	 */
	private Sequencer getSequencer() {
		if (!SEQ_DEV_NAME.isEmpty() || !"default".equalsIgnoreCase(SEQ_DEV_NAME)) {
			System.setProperty(SEQ_PROP_KEY, SEQ_DEV_NAME);
		}

		try {
			return MidiSystem.getSequencer();
		} catch (MidiUnavailableException e) {
			System.err.println("Error getting sequencer");
			e.printStackTrace();
			return null;
		}
	}

	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public MidiEvent makeEvent(int command, int channel, int note, int velocity, int tick) {

		MidiEvent event = null;
		try {
			ShortMessage a = new ShortMessage();
			a.setMessage(command, channel, note, velocity);
			event = new MidiEvent(a, tick);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return event;
	}
}