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

import javax.sound.midi.Sequence;

/**
 * Holds a MIDI sequence.
 * @author Brian
 * @version 1.0
 */
public class MIDI implements Sound
{
    private Sequence Seq;
    
    /**
     * Initializes a sound with a sequence.
     * @param Seq The sequence to store.
     */
    public MIDI (Sequence Seq){
        this.Seq = Seq;
    }

    /**
     * @return The sequence
     */
    public Sequence getSeq() {
        return Seq;
    }

	@Override
	public WAV getWAV() {
		return null;
	}

	@Override
	public MIDI getMIDI() {
		return this;
	}
    
    
}
