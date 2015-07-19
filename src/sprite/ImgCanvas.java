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
*
* A canvas is what is painted to the screen and generally
* has control over how things are drawn. This is the view,
* and as such is an abstract representation of how a screen
* may be painted.
* 
* @see Img
*
* @author Brian Nakayama
* 
* @version 1.2 Now all code is abstracted as MVC.
*/
public interface ImgCanvas {
	
	/**
	 * Get the width of the canvas.
	 * @return
	 */
	public int getWidth();
	/**
	 * Get the height of the canvas.
	 * @return
	 */
	public int getHeight();
	/**
	 * Paint the screen.
	 */
	public void paint();
	/**
	 * Initiate fullScreen mode.
	 */
	public void fullScreen();
	/**
	 * Initiate a windowScreen mode.
	 */
	public void windowScreen();
	/**
	 * Rotate the screen
	 * @param degrees The degrees the screen should be rotated by.
	 */
	public void setRotation(int degrees);

	/**
	 * Get an image uploader for a directory associated with this Canvas.
	 * @param s The path to the directory.
	 * @return An ImgUploader for that directory.
	 */
	public ImgUpload getImgUpload(String s);
}
