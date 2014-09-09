/*The MIT License (MIT)

Copyright (c) 2014 Brian Nakayama

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 */

package sound;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequencer;
import javax.sound.sampled.Clip;

/**
 * Plays the sound files created by SoundUpload.
 * 
 * @author Megan Knez
 * @author Brian Nakayama
 * 
 * @version 1.1
 * @since 1.0 Original version created by Megan and Brian
 * @see SoundUpload
 */
public class TrackPlayer {
	private Sequencer sequencer;
	private static TrackPlayer player = new TrackPlayer();

	/*
	 * Singleton constructor.
	 */
	private TrackPlayer() {
		try {
			sequencer = MidiSystem.getSequencer();
			sequencer.open();
		} catch (Exception e) {
			System.out.println("Midi sequencer was unavailable =(.");
		}
	}

	/**
	 * Get the instance of the track player.
	 * 
	 * @return The track player.
	 */
	public static TrackPlayer getPlayer() {
		return player;
	}

	/**
	 * Play a sound <i>count</i> times.
	 * 
	 * @param s
	 *            The Sound object
	 * @param count
	 *            The number of times to loop.
	 */
	public void play(Sound s, int count) {
		if (s.getWAV() != null) {
			play(s.getWAV(), count);
		} else if (s.getMIDI() != null) {
			play(s.getMIDI(), count);
		}
	}

	/**
	 * Play a sound once.
	 * 
	 * @param s
	 *            The Sound object
	 */
	public void play(Sound s) {
		if (s.getWAV() != null) {
			play(s.getWAV(), 0);
		} else if (s.getMIDI() != null) {
			play(s.getMIDI(), 0);
		}
	}

	/**
	 * Play a MIDI infinitely.
	 * 
	 * @param mi
	 *            The MIDI object
	 */
	public void play(MIDI mi) {
		play(mi, Sequencer.LOOP_CONTINUOUSLY);
	}

	/**
	 * Play a MIDI <i>count</i> times.
	 * 
	 * @param mi
	 *            The MIDI object
	 * @param count
	 *            The number of times to loop
	 */
	public void play(MIDI mi, int count) {
		try {
			sequencer.setSequence(mi.getSeq());
			sequencer.setLoopCount(count);
			sequencer.setLoopStartPoint(0);
			sequencer.start();
		} catch (InvalidMidiDataException ex) {
			System.out.println("Error starting sequence =P.");
		}
	}

	/**
	 * Play a WAV once.
	 * 
	 * @param wav
	 *            The WAV object
	 */
	public void play(WAV wav) {
		play(wav, 0);
	}

	/**
	 * Play a WAV <i>count</i> times.
	 * 
	 * @param wav
	 *            The WAV object
	 * @param count
	 *            The number of times to loop
	 */
	public void play(WAV wav, int count) {
		Clip c = wav.getC();
		c.stop();
		c.loop(count);
		c.setFramePosition(0);
		c.start();
	}
}
