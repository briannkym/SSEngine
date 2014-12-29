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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;

import sprite.Img;
import sprite.ImgUpload;
import sprite.NullImg;

/**
 * Loads all image files for a directory.
 * 
 * For each directory there exists only one instance of ImgUpload as determined
 * by the getInstance method. For an example of how to use ImgUpload see
 * setImage in SimpleObject
 * 
 * @author Brian Nakayama
 * @version 1.1 Bug fix with animations.
 * @since 1.0 First working version.
 * @see world.SimpleObject
 */

public class DesktopImgUpload implements ImgUpload{
	private File f;
	private static List<DesktopImgUpload> iuList = new LinkedList<DesktopImgUpload>();
	private Map<String, Img> img = new HashMap<String, Img>();

	private FileFilter fs = new FileFilter() {

		@Override
		public boolean accept(File f) {
			if (f.getAbsolutePath().endsWith(".jpg")) {
				return true;
			}

			if (f.getAbsolutePath().endsWith(".png")) {
				return true;
			}

			return false;
		}

	};

	/*
     * Private constructor 
     */
	private DesktopImgUpload(File f) {
		this.f = f;
		if (f.isDirectory()) {
			File[] images = f.listFiles(fs);
			for (int i = 0; i < images.length; i++) {
				BufferedImage bI = null;
				try {
					bI = ImageIO.read(images[i]);
				} catch (IOException ex) {
					System.out.println("Could not read in the following file:"
							+ images[i].getName());
				}

				File temp = new File(images[i].getAbsolutePath() + ".txt");
				if (temp.exists()) {
					try {
						BufferedReader br = new BufferedReader(new FileReader(
								temp));
						String s[] = br.readLine().split(",");
						int width = Integer.parseInt(s[0]);
						int height = Integer.parseInt(s[1]);
						int x_wid = bI.getWidth() / width;
						int y_wid = bI.getHeight() / height;
						BufferedImage[] bIA = new BufferedImage[width * height];

						for (int x = 0; x < width; x++) {
							for (int y = 0; y < height; y++) {
								bIA[y * width + x] = bI.getSubimage(x * x_wid,
										y * y_wid, x_wid, y_wid);
							}
						}
						img.put(images[i].getName(), new Animation(bIA));

						br.close();
					} catch (Exception ex) {
						System.out
								.println("Could not read in this animation txt: "
										+ temp.getName());
					}

				} else {

					img.put(images[i].getName(), new Sprite(bI));
				}
			}

		}
	}

	/**
	 * Creates an instance of ImgUpload if one doesn't exist for the file f.
	 * 
	 * If an instance already exists, this method will return that instance.
	 * Every time an instance is created all of the image files in the directory
	 * passed in will be uploaded. 
	 * 
	 * Animations are also parsed through this method. Each animation should have a
	 * text file containing the number of slides (cells) composed in an image. The name
	 * of the text file should be &lt;filename&gt;.txt.
	 * 
	 * For example:
	 * player.png //Contains image data for an animation of 3 slides.
	 * player.png.txt //Contains "3,1", 3-wide, 1-tall.
	 * 
	 * @param f
	 *            The directory containing image files. (*.png, *.jpg)
	 * @return The instance of ImgUpload
	 */
	public static DesktopImgUpload getInstance(File f) {
		Iterator<DesktopImgUpload> Imu = iuList.iterator();
		if (Imu.hasNext()) {
			DesktopImgUpload obj;

			do {
				obj = Imu.next();
				if (obj.getFile().getAbsolutePath() == f.getAbsolutePath()) {
					return obj;
				}
			} while (Imu.hasNext());
		}
		DesktopImgUpload iu = new DesktopImgUpload(f);
		iuList.add(iu);
		return iu;
	}

	
	@Override
	public Img getImg(String fileName) {
		Img i = img.get(fileName);
		if (i == null) {
			return NullImg.getInstance();
		}
		return i;
	}


	@Override
	public Img getRotatedImg(String fileName, int degree) {
		degree %= 360;
		Img i = img.get(fileName + degree);
		if (i == null) {
			i = img.get(fileName);
			if (i == null){
				return NullImg.getInstance();
			} else {
				Img rotated = i.getRotatedInstance(degree);
				img.put(fileName+degree, rotated);
				return rotated;
			}
		}
		return i;
	}
	
	@Override
	public File getFile() {
		return f;
	}
	

	/**
	 * Gets an image using this factory.
	 * 
	 * @param sprite
	 *            The address of the image.
	 */
	public static Img getImage(String sprite) {
		File f = new File(sprite);
		return getInstance(f.getParentFile()).getImg(f.getName());
	}

}
