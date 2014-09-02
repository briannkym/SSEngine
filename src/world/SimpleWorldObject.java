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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import sound.*;

public abstract class SimpleWorldObject {
	
	public abstract void updateScreen(BufferedImage bi, Graphics2D g);
	
	public void playSound(String sound){
		File f = new File(sound);
		Sound s = SoundUpload.getInstance(f.getParentFile()).getSound(f.getName());
		TrackPlayer.getPlayer().play(s);
	}
	
	public boolean loadImageResources(String spriteFile){
		File f = new File(spriteFile);
		if(f.isDirectory()){
			SoundUpload.getInstance(f.getParentFile());
			return true;
		} 
		return false;
	}
	
	public boolean loadSoundResources(String soundFile){
		File f = new File(soundFile);
		if(f.isDirectory()){
			SoundUpload.getInstance(f.getParentFile());
			return true;
		} 
		return false;
	}
}