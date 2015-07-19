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

import java.io.File;

/**
 * The interface for all possible image uploading classes (for different
 * devices).
 * 
 * @author Brian Nakayama
 * 
 */
public interface ImgUpload {

	/**
	 * Get an image file
	 * 
	 * @param fileName
	 *            The file name, not including any directories or the full path.
	 * @return The image if it exists.
	 */
	public Img getImg(String fileName);

	/**
	 * Get a rotated image file. This is the recommended way to rotate an image,
	 * as it ensures that only one copy of the rotated image is created. Use
	 * this method instead of {@link Img#getRotatedInstance(int)}.
	 * 
	 * @param fileName
	 *            The file name, not including any directories or the full path.
	 * @param degree
	 *            The degree [0-360]
	 * @return The image if it exists.
	 */
	public Img getRotatedImg(String fileName, int degree);

	/**
	 * Get the file used to construct this instance.
	 * 
	 * @return The directory
	 */
	public File getFile();
}
