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
 * An empty image that returns nothing for getSlide. 
 * 
 * The purpose of this image is to allow the drawing of images without
 * checking whether an object has an image or not.
 * @author Brian Nakayama
 */
public class NullImg extends Img{

    private static NullImg n = new NullImg();
    
    private NullImg()
    {
    }
    
    /**
     * Gets the Singleton instance of NullImg.
     * @return The instance.
     */
    public static NullImg getInstance()
    {
        return n;
    }
    
    @Override
    public void drawSlide(int x, int y)
    {
    }

	@Override
	public int getWidth() {
		return 0;
	}

	@Override
	public int getHeight() {
		return 0;
	}

	@Override
	public int[] getPixel(int x, int y) {
		return null;
	}

	@Override
	public void setPixel(int x, int y, int[] val) {
	}

	@Override
	public boolean checkForCol(int[] val) {
		return false;
	}

	@Override
	public Img getRotatedInstance(int degree) {
		return this;
	}

}
