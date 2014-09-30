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
package world;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JPanel;

import sprite.ColorImg;
import sprite.Img;
import sprite.ImgUpload;
import view.Pinterface;
import view.Projector;

/**
 * Holds the camera and interfaces with the projector which controls the frame
 * rate. Also holds logic for starting the game along with rendering the frame.
 * 
 * @author Brian Nakayama
 * @author Mark Groeneveld
 */
public class SimpleWorld extends JFrame implements Pinterface {

	/*
	 * Though this object is serializable, it is not intended or recommended to
	 * use this feature.
	 */
	private static final long serialVersionUID = 1L;
	private Img<BufferedImage> background;
	private Projector ip;
	private String title;
	private final int[] camera = { 0, 0 };
	private SimpleObject cameraStalk = null;

	private int width, height;
	private SimpleMap m;
	private SimpleWorldObject swo = NullSimpleWorldObject.getInstance();
	private boolean update = true;

	/**
	 * Create a SimpleWorld with the desired width and height.
	 * 
	 * @param m
	 *            The map to be rendered and updated. Switch the map to switch
	 *            "environments" or "rooms".
	 * @param width
	 *            The desired width in pixels.
	 * @param height
	 *            The desired height in pixels.
	 * @param title
	 *            The title of the application.
	 */
	public SimpleWorld(SimpleMap m, int width, int height, String title) {
		this.m = m;
		this.width = width;
		this.height = height;
		BufferedImage bi = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		this.title = title;
		this.ip = new Projector(20.0f, bi, title, this);
	}

	/**
	 * Starts the loop that runs the game and updates the objects. Essentially
	 * starts the game.
	 * 
	 * @param fullscreen
	 *            True for fullscreen, false for windowed screen. The windowed
	 *            screen will be the size defined in the constructor in pixels.
	 */
	public void start(boolean fullscreen) {
		if (fullscreen) {
			ip.init(this);
		} else {
			// TODO Update this code to get the dimensions of the buffered
			// Image.
			Container c = this.getContentPane();
			JPanel jp = ip.init(width, height);
			c.setLayout(new BorderLayout());
			c.add(jp, BorderLayout.NORTH);
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.setTitle(title);
			this.setIgnoreRepaint(true);
			this.pack();
			this.setResizable(false);
			this.setVisible(true);
		}
	}

	/**
	 * Set the current map held and rendered by the world.
	 * 
	 * @param m
	 *            The current map.
	 */
	public void setSimpleSolidMap(SimpleMap m) {
		this.m = m;
	}

	/**
	 * Get the current map held and rendered by the world.
	 * 
	 * @return The current map.
	 */
	public SimpleMap getSimpleSolidMap() {
		return m;
	}

	/**
	 * Set the world object which can draw and render over the rest of the
	 * screen.
	 * 
	 * @param swo
	 *            The SimpleWorldObject for this world.
	 * @see SimpleWorldObject
	 */
	public void setSimpleWorldObject(SimpleWorldObject swo) {
		this.swo = swo;
	}

	/**
	 * Get the world object.
	 * 
	 * @return The SimpleWorldObject for this world.
	 * @see SimpleWorldObject
	 */
	public SimpleWorldObject getSimpleWorldObject() {
		return swo;
	}

	/**
	 * Set an object for the camera to follow (stalk).
	 * 
	 * Notice that the camera will only have an effect if the screen is smaller
	 * than the size of the world.
	 * 
	 * @param cameraStalk
	 *            The object to follow.
	 */
	public void setCameraStalk(SimpleObject cameraStalk) {
		this.cameraStalk = cameraStalk;
	}

	/**
	 * Set the background image to be tiled. Not the suggested way to load the
	 * background. It should be statically loaded. Then use
	 * {@link #setBGImage(Img)}.
	 * 
	 * @param sprite
	 *            The path to the background image to be tiled.
	 */
	public void setBGImage(String sprite) {
		File f = new File(sprite);
		this.background = ImgUpload.getInstance(f.getParentFile()).getImg(
				f.getName());
	}

	/**
	 * Set the background image to be tiled. This method sets a colorImage to be
	 * tiled.
	 * 
	 * @param rgba
	 *            The 32 bit color for the background.
	 * @param width
	 *            The width in pixels of the image.
	 * @param height
	 *            The height in pixels of the image.
	 * @see ColorImg
	 */
	public void setBGImage(int rgba, int width, int height) {
		this.background = new ColorImg(rgba, width, height);
	}

	/**
	 * Set the background image to be tiled. This method sets an arbitrary image
	 * to be tiled. This will work strangely with animations.
	 * 
	 * @param i
	 *            The image to be tiled.
	 */
	public void setBGImage(Img<BufferedImage> i) {
		this.background = i;
	}

	/**
	 * Set the x and y coordinate of the camera for the top left of the screen.
	 * 
	 * @param x
	 *            The new x coordinate.
	 * @param y
	 *            The new y coordinate.
	 */
	public void setCamera(int x, int y) {
		camera[0] = x;
		camera[1] = y;
	}

	/**
	 * Get the array that holds the x and y coordinate of the camera.
	 * 
	 * @return a 2d array with the x and y coordinate of the camera.
	 */
	public int[] getCamera() {
		return camera;
	}

	/**
	 * Get the projector created by the SimpleWorld.
	 * 
	 * The Projector holds more methods for starting and stopping the animation
	 * thread. For example you may want to change the FPS, or render more than
	 * one copy of the screen.
	 * 
	 * @return The projector
	 */
	public Projector getProjector() {
		return ip;
	}

	/**
	 * Inherited method, not for intended for direct use.
	 * 
	 * This method updates the camera, all the objects, then paints them all.
	 * 
	 * @see view.Pinterface#iUpdate(java.awt.image.BufferedImage)
	 */
	@Override
	public void iUpdate(BufferedImage ISlide) {

		// Update camera coordinates based off of the width and height.
		if (cameraStalk != null) {
			camera[0] = cameraStalk.coor_x - (width - m.cellWidth) / 2;
			camera[1] = cameraStalk.coor_y - (height - m.cellHeight) / 2;
			if (camera[0] < 0){
				camera[0] = 0;
			} else {
				int x;
				if (camera[0] > (x = m.mapWmax - width + m.cellWidth)){
					camera[0] = x;
				}
			}
			
			if (camera[1] < 0){
				camera[1] = 0;
			} else {
				int y;
				if (camera[1] > (y = m.mapHmax - height + m.cellHeight)){
					camera[1] = y;
				}
			}
		}

		Graphics2D g = ISlide.createGraphics();
		g.setColor(new Color(0xFFFFFFFF));
		g.fillRect(0, 0, ISlide.getWidth(), ISlide.getHeight());

		if (background != null) {
			BufferedImage bg = background.getSlide();
			int bg_width = bg.getWidth();
			int bg_height = bg.getHeight();

			for (int x = camera[0] % bg_width - bg_width; x < ISlide.getWidth(); x += bg_width) {
				for (int y = camera[1] % bg_height - bg_height; y < ISlide
						.getHeight(); y += bg_height) {
					g.drawImage(bg, x, y, null);
				}
			}
		}

		// Update all objects.
		if (update) {
			for (SimpleObject s : m.zArray) {
				if (s != null) {
					for (; s != null; s = s.updateNext) {
						s.newUpdate();
					}
				}
			}
		}

		// Paint all objects.
		for (SimpleObject s : m.zArray) {
			if (s != null) {
				for (; s != null; s = s.drawNext) {
					s.paintImage(g, camera);
				}
			}
		}

		// Paint the world object over the projection.
		swo.updateScreen(ISlide, g);
		g.dispose();
	}
}
