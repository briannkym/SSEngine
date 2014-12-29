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
 * This class along with {@link ImgCanvas} abstract the view away
 * from the rest of the objects while allowing images to each have
 * an image. The current solution uses the Strategy pattern to
 * have images essentially draw themselves.
 *
 * @author Brian Nakayama
 * 
 * @version 1.2 Now all code is abstracted as MVC.
 * @since 1.0
 */
public abstract class Img
{

	protected ImgListener iL = NullListener.getInstance();
	/**
	 * Returns the current slide of the image.
	 * @param x The x position of the upper left corner
	 * @param y The y position of the upper left corner
	 */
    public abstract void drawSlide(int x, int y);
	
    /**
     * Return the width of the image
     * @return The width
     */
    public abstract int getWidth();
    
    /**
     * Return the height of the image
     * @return The height
     */
    public abstract int getHeight();
    
    /**
	 * Sets a listener for the end of an animation.
	 * Only supported in animations.
	 */
    public void setListener(ImgListener iL){
    	if(iL==null){
    		this.iL = NullListener.getInstance();
    	} else {
        	this.iL = iL;
    	}
    }
    
    /**
     * A version of the command pattern. 
     * @param iC
     */
    public <T extends ImgCommand> void accept(T iC){
    	iC.accept(this);
    }

    //TODO work with this with Chris.
    //Gets the pixel at x, y
    public abstract int[] getPixel(int x, int y);
    
    //Sets the pixel at x, y
    public abstract void setPixel(int x, int y, int[] val);
    
    //Checks for transparency
    public abstract boolean checkForCol(int[] val);
    
    /**
     * Creates a copy of the image rotated by the specified number of degrees.
     * Instead of rotating an image on the fly this method can be used to pre-rotate
     * images.
     * @param degree The amount to rotate out of 360 degrees
     * @return A rotated copy.
     */
    public abstract Img getRotatedInstance(int degree);
    
}
