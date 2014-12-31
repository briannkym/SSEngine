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

import sprite.Img;

/**
 * The basic image. Holds a buffered image.
 * 
 * @author Brian Nakayama
 */
public class Sprite extends Img {

	BufferedImage bI;
	private DesktopControl dc = DesktopControl.getInstance();

	/**
	 * Creates a sprite with the specified buffered image.
	 * 
	 * @param bI
	 *            The buffered image to store.
	 */
	public Sprite(BufferedImage bI) {
		this.bI = bI;
	}

	/**
	 * Draws the colored image using {@link DesktopCanvas}
	 */
	@Override
	public void drawSlide(int x, int y) {
		dc.getCanvas().drawImage(bI, x, y);
		iL.slideEnd();
	}

	@Override
	public int getWidth() {
		return bI.getWidth();
	}

	@Override
	public int getHeight() {
		return bI.getHeight();
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
		BufferedImage bNew = new BufferedImage(bI.getWidth(), bI.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bNew.createGraphics();
		g.rotate((degree * 2 * Math.PI) / 360.0, bNew.getWidth() / 2,
				bNew.getHeight() / 2);
		g.drawImage(bI, 0, 0, null);
		g.dispose();
		return new Sprite(bNew);
	}

}
