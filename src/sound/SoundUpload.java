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

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.nio.file.FileSystemNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

/**
 * Loads all sound files for a directory.
 * 
 * For each directory there exists only one instance of SoundUpload as
 * determined by the getInstance method. For an example of how to use
 * SoundUpload see playSound in SimpleObject
 * 
 * @author Brian Nakayama
 * @version 1.0 Works for MIDI and WAV files.
 * @see world.SimpleObject
 */
public class SoundUpload {

	private File f;
	private static List<SoundUpload> suList = new LinkedList<SoundUpload>();

	private Map<String, Sound> sound = new HashMap<String, Sound>();
	private FileFilter fs = new FileFilter() {
		@Override
		public boolean accept(File f) {
			if (f.getAbsolutePath().endsWith(".mid")) {
				return true;
			}

			if (f.getAbsolutePath().endsWith(".wav")) {
				return true;
			}

			return false;
		}
	};

	/**
	 * Creates an instance of SoundUpload if one doesn't exist for the file f.
	 * 
	 * If an instance already exists, this method will return that instance.
	 * Every time an instance is created all of the sound files in the directory
	 * passed in will be uploaded.
	 * 
	 * @param f
	 *            The directory containing sound files. (*.wav, *.midi)
	 * @return An instance of SoundUpload
	 */
	public static SoundUpload getInstance(File f) {
		Iterator<SoundUpload> Isu = suList.iterator();
		if (Isu.hasNext()) {
			SoundUpload obj;

			do {
				obj = Isu.next();
				if (obj.getFile().getAbsolutePath() == f.getAbsolutePath()) {
					return obj;
				}
			} while (Isu.hasNext());
		}
		SoundUpload su = new SoundUpload(f);
		suList.add(su);
		return su;
	}

	/*
	 * Private constructor. This is a slight modification on the Singleton
	 * pattern.
	 */
	private SoundUpload(File f) {
		this.f = f;
		if (f.isDirectory()) {
			File[] sounds = f.listFiles(fs);

			for (int i = 0; i < sounds.length; i++) {
				Sound bS = null;

				if (sounds[i].getName().endsWith(".mid")) {
					try {
						FileInputStream is = new FileInputStream(sounds[i]);
						Sequence Seq = MidiSystem.getSequence(is);
						bS = new MIDI(Seq);
						is.close();
					} catch (Exception e) {
						System.out.println("Error loading midi: "
								+ sounds[i].getName());
					}
				} else if (sounds[i].getName().endsWith(".wav")) {
					try {
						AudioInputStream as = AudioSystem
								.getAudioInputStream(sounds[i]);
						DataLine.Info info = new DataLine.Info(Clip.class, as.getFormat());
						Clip c = (Clip) AudioSystem.getLine(info);
						c.open(as);
						bS = new WAV(c);
						as.close();
					} catch (Exception e) {
						System.out.println("Error loading wav:"
								+ sounds[i].getName());
						e.printStackTrace();

					}
				}

				if (bS != null) {
					sound.put(sounds[i].getName(), bS);
				}
			}

		} else {
			throw new FileSystemNotFoundException("The directory requested does not exist!");
		}
	}

	/**
	 * Get a sound file
	 * 
	 * @param fileName The file name, not including any directories or the full path.
	 * @return The sound if it exists.
	 */
	public Sound getSound(String fileName) {
		return sound.get(fileName);
	}

	/**
	 * Get the file used to construct this instance.
	 * 
	 * @return The directory
	 */
	public File getFile() {
		return f;
	}
}
