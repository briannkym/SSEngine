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
package desktopView;

import java.awt.image.BufferedImage;

import control.DesktopControl;

import sprite.Img;
import sprite.ImgListener;
import sprite.NullListener;

/**
 * Stores an animation that updates upon returning a slide.
 * 
 * @author Brian Nakayama
 */
public class Animation implements Img {

	public boolean cycle = true;
	private BufferedImage[] bI;
	private int i = 0;
	private ImgListener iL = NullListener.getInstance();
    private DesktopControl dc = DesktopControl.getInstance();

	/**
	 * Initializes the animation with an array of bufferedImages.
	 * 
	 * @param bI
	 *            The array of images.
	 */
	public Animation(BufferedImage[] bI) {
		this.bI = bI;
	}

	/**
	 * Returns a new animation that uses the same array of images.
	 * 
	 * @return A new animation.
	 */
	public Animation getClone() {
		return new Animation(bI);
	}

	/**
	 * Draws an image and increments the index of the array.
	 */
	@Override
	public void drawSlide(int x, int y) {
		BufferedImage rB = bI[i];
		if(cycle){
			if (i < bI.length - 1) {
				i++;
			} else {
				iL.slideEnd();
				i = 0;
			}
		}
		dc.getCanvas().buffer.drawImage(rB, x, y, null);
	}

	/**
	 * Set the index to be displayed.
	 */
	public void setSlide(int i) {
		this.i = i;
	}

	/**
	 * Set an Image Listener.
	 * 
	 * @see ImgListener
	 */
	public void setListener(ImgListener iL) {
		this.iL = iL;
	}

	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	
}
