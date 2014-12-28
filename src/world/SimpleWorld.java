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

import control.DeviceControl;
import clock.Cinterface;
import clock.Clock;

import sprite.Img;

/**
 * Holds the camera and interfaces with the projector which controls the frame
 * rate. Also holds logic for starting the game along with rendering the frame.
 * 
 * @author Brian Nakayama
 * @author Mark Groeneveld
 * @version 1.2 Now all code is abstracted as MVC.
 * @since 1.1
 */
public class SimpleWorld implements Cinterface {

	private Img background;
	private Clock clock;
	private final int[] camera = { 0, 0 };
	private SimpleObject cameraStalk = null;

	public final DeviceControl dv;
	private SimpleMap m;
	private SimpleWorldObject swo = NullSimpleWorldObject.getInstance();
	private boolean update = true;

	/**
	 * Create a SimpleWorld with the desired width and height.
	 * 
	 * @param m
	 *            The map to be rendered and updated. Switch the map to switch
	 *            "environments" or "rooms".
	 * @param ImgCanvas 
	 * 			The canvas to be drawn to.
	 * @param title
	 *            The title of the application.
	 */
	public SimpleWorld(SimpleMap m, DeviceControl dv) {
		this.m = m;
		this.dv = dv;
		this.clock = new Clock(20.0f, this);
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
		dv.getCanvas().register();
		if (fullscreen) {
			dv.getCanvas().fullScreen();
		} else {
			dv.getCanvas().windowScreen();
		}
		clock.init();
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
	 * Set the background image to be tiled. This method sets an arbitrary image
	 * to be tiled. This will work strangely with animations.
	 * 
	 * @param i
	 *            The image to be tiled.
	 */
	public void setBGImage(Img i) {
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
	 * Get the clock created by the SimpleWorld.
	 * 
	 * The clock holds more methods for starting and stopping the loop thread.
	 * For example you may want to change the FPS.
	 * 
	 * @return The projector
	 */
	public Clock getClock() {
		return clock;
	}

	/**
	 * Inherited method, not for intended for direct use.
	 * 
	 * This method updates the camera, all the objects, then paints them all.
	 * 
	 * @see clock.Cinterface#iUpdate(java.awt.image.BufferedImage)
	 */
	@Override
	public void update() {

		// Update camera coordinates based off of the width and height.
		if (cameraStalk != null) {
			int width = dv.getCanvas().getWidth();
			int height = dv.getCanvas().getHeight();
			
			camera[0] = cameraStalk.coor_x - (width - m.cellWidth)
					/ 2;
			camera[1] = cameraStalk.coor_y
					- (height - m.cellHeight) / 2;
			if (camera[0] < 0) {
				camera[0] = 0;
			} else {
				int x;
				if (camera[0] > (x = m.mapWmax - width + m.cellWidth)) {
					camera[0] = x;
				}
			}			
			if (camera[1] < 0) {
				camera[1] = 0;
			} else {
				int y;
				if (camera[1] > (y = m.mapHmax - height	+ m.cellHeight)) {
					camera[1] = y;
				}
			}
		}

		//Draw in the background.
		if (background != null) {
			int bg_width = background.getWidth();
			int bg_height = background.getHeight();
			for (int x = camera[0] % bg_width - bg_width; x < dv.getCanvas().getWidth(); x += bg_width) {
				for (int y = camera[1] % bg_height - bg_height; y < dv.getCanvas()
						.getHeight(); y += bg_height) {
					background.drawSlide(x, y);
				}
			}
		}

		// Update all objects.
		if (update) {
			for (SimpleObject s = m.getDrawBegin(); s != null; s = s.updateNext) {
				s.newUpdate();
			}
		}

		// Paint all objects.
		for (SimpleObject s = m.getDrawBegin(); s != null; s = s.drawNext) {
			s.updateNext = s.drawNext;
			s.i.drawSlide(s.coor_x + s.off[0] - camera[0], s.coor_y + s.off[1]
					- camera[1]);
		}

		// Update the world object last.
		swo.update();
		dv.getCanvas().paint();
	}
	
	/**
	 * Disables updating of objects in world.
	 */
	public void disableUpdate() {
		update = false;
	}
	
	/**
	 * Enables updating of objects in world.
	 */
	public void enableUpdate() {
		update = true;
	}
}
