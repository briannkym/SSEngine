package test;

import java.io.File;

import sprite.ImgUpload;

import desktopView.DesktopCanvas;
import desktopView.DesktopImgUpload;
import world.SimpleMap;
import world.SimpleWorld;

/**
 * This class tests some of the basic features of the game engine.
 * 
 * @author Brian Nakayama
 */
public class testMain {
	public static void main(String[] args) {

		// Create a new map 160 (width) x 120 (height) cells, and make each cell
		// 20x20 pixels.
		SimpleMap m = new SimpleMap(160, 120, 20, 20);
		ImgUpload iu = DesktopImgUpload.getInstance(new File("src"));
		// Create an object for the camera to follow.
		testObject cam = new testObject();
		// Add the camera object to the map at position (790,590) in pixels.
		m.addSimpleObject(cam, 790, 590);
		for (int x = 0; x < 160; x++) {
			for (int y = 0; y < 120; y++) {
				// For each cell get a random number.
				double d = Math.random();
				// With 40% probability add a solid textObject at the cells
				// coordinates.
				if (d < 0.4) {
					m.addSimpleObject(new testObject(), x * 20, y * 20);
				} else if (d < 0.6) {
					//With 20% probability add a non-solid test Color.
					int z = (int) (Math.random() * 16);
					m.addSimpleObject(new testColor(), x * 20, y * 20, z);
				}
			}
		}

		// This app will be used on a normal desktop computer, so create
		// a desktop view. Let it be 800x600 pixels.
		DesktopCanvas dc = new DesktopCanvas(800, 600, "Test");
		// Hand over the control and the map to a World object.
		SimpleWorld w = new SimpleWorld(m, dc);
		// Tell the world to focus on the camera object.
		w.setCameraStalk(cam);
		w.setBGImage(iu.getImg("Back.png"));
		// Start the world in full screen mode.
		w.start(true);
	}
}
