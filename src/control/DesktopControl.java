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
package control;

import java.io.File;

import sprite.ImgCanvas;
import sprite.Img;
import sprite.ImgUpload;
import desktopView.DesktopCanvas;
import desktopView.DesktopImgUpload;
import desktopView.IDesktopCanvas;

/**
 * Not intended for direct use by user. This is simple a linker class
 * for the DesktopCanvas and it's associated images. It is likely to 
 * change in the future and is here just for good SE.
 * @author Brian Nakayama
 * @version 1.2 Part of MVC update
 */
public class DesktopControl implements DeviceControl{
	private DesktopCanvas canvas;
	
	private static final DesktopControl dc = new DesktopControl();

	private DesktopControl() {
	}

	public static DesktopControl getInstance() {
		return dc;
	}

	public boolean setCanvas(ImgCanvas canvas) {
		if(canvas instanceof IDesktopCanvas){
			this.canvas = (DesktopCanvas)canvas;
			return true;
		}
		return false;
	}

	public DesktopCanvas getCanvas() {
		return canvas;
	}

	@Override
	public ImgUpload getImgUpload(String s) {
		File f = new File(s);
		return DesktopImgUpload.getInstance(f);
	}
	
	
	
}
