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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import control.DesktopControl;

import sprite.Anm;
import sprite.Img;
import sprite.NullListener;
import sprite.ImgListener;

/**
 * Stores an animation that updates upon returning a slide.
 * 
 * @author Brian Nakayama
 */
public class Animation extends Anm {

	public boolean cycle = true;
	BufferedImage[] bI;
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

		if (cycle) {
			if (i < bI.length - 1) {
				i++;
			} else {
				iL.slideEnd();
				i = 0;
			}
		}

		dc.getCanvas().drawImage(rB, x, y);
	}

	/**
	 * Set the index to be displayed.
	 */
	public void setSlide(int i) {
		this.i = i;
	}

	@Override
	public int getWidth() {
		return bI[0].getWidth();
	}

	@Override
	public int getHeight() {
		return bI[0].getHeight();
	}

	@Override
	public void animate(boolean animate) {
		cycle = animate;
	}

	@Override
	public Img getSlide(int i) {
		return new Sprite(bI[i]);
	}

	@Override
	public Anm getClone(int... i) {
		BufferedImage[] nbI = new BufferedImage[i.length];
		for (int j = 0; j < nbI.length; j++) {
			nbI[j] = bI[i[j]];
		}
		return new Animation(nbI);
	}

	@Override
	public int[] getPixel(int x, int y) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPixel(int x, int y, int[] val) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean checkForCol(int[] val) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Img getRotatedInstance(int degree) {
		BufferedImage[] bNew = new BufferedImage[bI.length];
		for (int i = 0; i < bNew.length; i++) {
			bNew[i] = new BufferedImage(bI[i].getWidth(),bI[i].getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = bNew[i].createGraphics();
			g.rotate((degree * 2 * Math.PI) / 360.0, bNew[i].getWidth() / 2,
					bNew[i].getHeight() / 2);
			g.drawImage(bI[i], 0, 0, null);
			g.dispose();
		}

		return new Animation(bNew);
	}
}
