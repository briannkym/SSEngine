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

package sprite;

/**
 * The resume command object for resuming an animation.
 * @author Brian Nakayama
 *
 */
public class Resume implements ImgCommand {

	private static Resume c = new Resume();
	
	
	private Resume(){
		
	}
	
	/**
	 * Get the resume command object for resuming animations.
	 * @return the resume object.
	 */
	public static Resume getInstance(){
		return c;
	}
	
	@Override
	public void accept(Anm a) {
		a.animate(true);
	}

	@Override
	public void accept(Img i) {
		// Do nothing.
	}

}
