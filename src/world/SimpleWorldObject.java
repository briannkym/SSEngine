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
package world;

import java.io.File;

import sound.*;

/**
 * The "global" object for the SimpleWorld that can render ontop of the screen.
 * 
 * This class has one method that the user overrides to paint over the screen.
 * 
 * @author Brian Nakayama
 * @version 1.2 Now all code is abstracted as MVC.
 */
public abstract class SimpleWorldObject {

	/**
	 * Game logic held in SimpleWorldObject should be updated here.
	 * This is also a good place to update any game view there
	 * might be using a controller.
	 * 
	 */
	public abstract void update();

	/**
	 * Play a sound.
	 * 
	 * This is the convenience method not the recommended one. See
	 * SimpleObject's implementation for more details. Also,
	 * {@link TrackPlayer#play(Sound)} can be used to play sounds directly.
	 * 
	 * @param sound
	 *            The path to the sound.
	 * @see SimpleObject#playSound(String)
	 */
	public void playSound(String sound) {
		File f = new File(sound);
		Sound s = SoundUpload.getInstance(f.getParentFile()).getSound(
				f.getName());
		TrackPlayer.getPlayer().play(s);
	}

	/**
	 * Preload all of the images in a folder.
	 * 
	 * @param spriteFile
	 *            The directory with the images to be loaded.
	 * @return True if the images were loaded successfully.
	 */
	public boolean loadImageResources(String spriteFile) {
		File f = new File(spriteFile);
		if (f.isDirectory()) {
			SoundUpload.getInstance(f.getParentFile());
			return true;
		}
		return false;
	}

	/**
	 * Preload all of the sound resources in a folder.
	 * 
	 * @param soundFile
	 *            The directory with the sounds to be loaded.
	 * @return True if the sounds were loaded successfully.
	 */
	public boolean loadSoundResources(String soundFile) {
		File f = new File(soundFile);
		if (f.isDirectory()) {
			SoundUpload.getInstance(f.getParentFile());
			return true;
		}
		return false;
	}
}