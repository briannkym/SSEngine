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

import java.io.File;

import sound.Sound;
import sound.SoundUpload;
import sound.TrackPlayer;
import sprite.Img;
import sprite.NullImg;

/**
 * A basic square object that holds image, position, and update data to draw a
 * dynamic object on the screen.
 * 
 * @author Brian Nakayama
 * @author Mark Groeneveld
 * @version 1.2 Now all code is abstracted as MVC.
 * @since 1.1
 */
public abstract class SimpleObject {

	// Linked Lists for drawing and updating.
	SimpleObject drawNext = null;
	SimpleObject drawPrevious = null;
	SimpleObject updateNext = null;

	// Optimization options for limited functionality.
	public static final int NO_UPDATES_NO_COLLIDES = 0,
			NO_COLLIDES = 2, NORMAL = 3;
	
	//Int for generating unique IDs for objects.
	private static int ID = 0;

	// By default use the null image.
	Img i = NullImg.getInstance();

	// By default update and check for collisions.
	int updates = NORMAL;

	// The current coordinates and previous coordinates of the object.
	int coor_x, coor_y, pre_cx, pre_cy;

	// Have a copy of the map for methods manipulating objects.
	SimpleMap m;

	// The offset for drawing the image.
	protected final int[] off = { 0, 0 };

	final SimpleSolid[] collisions = new SimpleSolid[4];
	
	/**
	 * Method for generating unique ID's. The ID's will be unique up to 2^32 objects.
	 * @return a unique ID
	 */
	public static int generateID(){
		ID ++;
		return ID;
	}
	
	/**
	 * Override with behavior for collisions. SimpleObjects can only collide
	 * with SimpleSolids, and will overlap when colliding. SimpleSolids can
	 * collide with everything, but cannot overlap with other SimpleSolids. If a
	 * collision occurs, this method will be called for both objects.
	 * 
	 * @param s
	 *            The object for which the collision has occurred.
	 * @see SimpleSolid
	 */
	abstract public void collision(SimpleObject s);

	/**
	 * If the object is set to receive updates each frame, this method will be
	 * called before the object is rendered by SimpleWorld.
	 * 
	 * @see SimpleWorld
	 */
	abstract public void update();

	/**
	 * Override with the id of the object. This method is necessary for
	 * SimpleWorldFactory, but it is also useful for determining what to do upon
	 * a collision.
	 * 
	 * @return The id of the object.
	 * 
	 * @see SimpleWorldFactory
	 */
	abstract public int id();

	/**
	 * Override this method if the child object cannot be created with the
	 * default constructor.
	 * 
	 * The string can be passed in as a parameter to customize the object
	 * creation.
	 * 
	 * @param s
	 *            A string containing information as to how to customize the
	 *            object.
	 * @return The object customized by the String s.
	 */
	public SimpleObject getClone(String s) {
		return null;
	}

	/**
	 * Override this method for objects that need additional information for
	 * their construction. This method will be called for extra information when
	 * saving objects.
	 * 
	 * @return A String containing additional information about the object.
	 * @see SimpleMapIO
	 */
	public String getDescription() {
		return "";
	}

	/**
	 * Default constructor. Checks for collisions and passes updates.
	 */
	public SimpleObject() {
		this(NORMAL);
	}

	/**
	 * Create a SimpleObject optimized to ignore certain methods. By default the
	 * object is NORMAL. If the object doesn't need to update its state before
	 * rendering, use NO_UPDATES. If the object doesn't need to collide use
	 * NO_COLLIDES.
	 * 
	 * @param optimization
	 *            NO_UPDATES_NO_COLLIDES, SOLID, NO_COLLIDES, or NORMAL
	 */
	public SimpleObject(int optimization) {
		this.updates = optimization;
	}

	/**
	 * Create a SimpleObject with a sprite. Note, this is not the efficient way
	 * to create an object with a sprite. If the same sprite belongs to all
	 * objects of a class it should be loaded statically. If the same Animation
	 * belongs to all objects of a class similarly it should be loaded
	 * statically and then copied using its getClone method. The following is
	 * preferable:
	 * 
	 * <pre>
	 * <code>
	 *  public class yourClass extends SimpleObject
	 *  {
	 *  	static Animation runS = (Animation) ImgUpload.getInstance(f.getParentFile()).getImg(f.getName());
	 *  	static Animation walkS = (Animation) ImgUpload.getInstance(f.getParentFile()).getImg(f.getName());
	 *  	static Img stand = ImgUpload.getInstance(f.getParentFile()).getImg(f.getName());
	 *  	private Img run = runS.getClone();
	 *  	private Img walk = walkS.getClone();
	 *  
	 *  	public yourClass(){
	 *  		this.setImage(stand);
	 *  	}
	 *  
	 *  	... Overridden methods here...
	 *  }
	 * </code>
	 * </pre>
	 * 
	 * This method exists purely for convenience.
	 * 
	 * 
	 * @param sprite
	 *            The address of the image.
	 * 
	 */
	public SimpleObject(Img sprite) {
		this(sprite, NORMAL);
	}

	/**
	 * Create a SimpleObject optimized to ignore certain methods with a sprite.
	 * By default the object is NORMAL. If the object doesn't need to update its
	 * state before rendering, use NO_UPDATES. If the object doesn't need to
	 * collide use NO_COLLIDES. Note, this is not the efficient way to create an
	 * object with a sprite. If the same sprite belongs to all objects of a
	 * class it should be loaded statically. If the same Animation belongs to
	 * all objects of a class similarly it should be loaded statically and then
	 * copied using its getClone method. The following is preferable:
	 * 
	 * <pre>
	 * <code>
	 *  public class yourClass extends SimpleObject
	 *  {
	 *  	File f = ..., g = ..., h = ...;
	 *  	static Animation runS = (Animation) ImgUpload.getInstance(f.getParentFile()).getImg(f.getName());
	 *  	static Animation walkS = (Animation) ImgUpload.getInstance(g.getParentFile()).getImg(g.getName());
	 *  	static Img stand = ImgUpload.getInstance(h.getParentFile()).getImg(h.getName());
	 *  	private Img run = runS.getClone();
	 *  	private Img walk = walkS.getClone();
	 *  
	 *  	public yourClass(){
	 *  		this.setImage(stand);
	 *  	}
	 *  
	 *  	... Overridden methods here...
	 *  }
	 * </code>
	 * </pre>
	 * 
	 * This method exists purely for convenience.
	 * 
	 * @param sprite
	 *            The address of the image.
	 * @param optimization
	 *            NO_UPDATES_NO_COLLIDES, NO_UPDATES, NO_COLLIDES, or NORMAL
	 */
	public SimpleObject(Img sprite, int optimization) {
		this(optimization);
		this.i = sprite;
	}

	/**
	 * Cancel the objects movement.
	 * 
	 * This can be used after calling move to undo the move. The following code
	 * demonstrates how it can be used to make a collision with a SimpleSolid
	 * and a SimpleObject seem like a SimpleSolid-SimpleSolid collision:
	 * 
	 * <pre>
	 * <code>
	 * 	public class yourClass extends SimpleObject
	 * 	{
	 * 
	 * 		public void collision(SimpleObject s){
	 * 			switch(s.getID()){ //s must also be a SimpleSolid
	 * 				case 0:
	 * 				//Assuming s was not moving, move back to the place before the collision occurred.
	 * 				cancelMove();
	 * 			 	break;
	 * 			}
	 * 		}
	 * 		
	 * 		... Other methods ...
	 * 	}
	 * </code>
	 * </pre>
	 * 
	 * @return True iff the move was undone.
	 */
	public boolean cancelMove() {
		if (coor_x != pre_cx || coor_y != pre_cy) {
			coor_x = pre_cx;
			coor_y = pre_cy;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Move to a pixel coordinate (x,y) or move relatively from the current
	 * coordinate (coor_x + x, coor_y + y). When requesting to move further than
	 * the boundary of the map, this method will attempt to move the object as
	 * much as possible within the boundary.
	 * 
	 * @param x
	 *            New x -coordinate.
	 * @param y
	 *            New y-coordinate.
	 * @param relative
	 *            True if the object should move relative to its coordinates:
	 *            (coor_x + x, coor_y + y).
	 * @return True if movement (not necessarily the movement asked for)
	 *         happens.
	 */
	public boolean move(int x, int y, boolean relative) {
		boolean movement = false;
		if (relative) {
			x += coor_x;
			y += coor_y;
		}
		pre_cy = coor_y;
		pre_cx = coor_x;

		if (x > 0) {
			if (x < m.mapWmax) {
				coor_x = x;
				movement = true;
			} else {
				coor_x = m.mapWmax;
			}
		} else {
			coor_x = 0;
		}

		if (y > 0) {
			if (y < m.mapHmax) {
				coor_y = y;
				movement = true;
			} else {
				coor_y = m.mapHmax;
			}
		} else {
			coor_y = 0;
		}
		return movement;
	}

	/*
	 * Used by SimpleWorld to ask for updates. This method first checks whether
	 * the object is optimized before checking for collisions, and then
	 * updating.
	 */
	void newUpdate() {
		switch (updates) {
		case NORMAL:
			m.calculateCollisions(coor_x, coor_y, this);
			for (SimpleSolid S : collisions) {
				if (S != null) {
					if (S != this) {
						S.collision(this);
						collision(S);
					}
				} else {
					break;
				}
			}
		case NO_COLLIDES:
			this.update();
		default:
			// Do nothing on move.
			break;
		}
	}

	/**
	 * Change the offset at which the image is drawn from the top left corner
	 * (position) of the object.
	 * 
	 * @param off_x
	 *            The x offset.
	 * @param off_y
	 *            The y offset.
	 */
	public void setOffset(int off_x, int off_y) {
		this.off[0] = off_x;
		this.off[1] = off_y;
	}

	/**
	 * Get the array holding the offset of the image.
	 * 
	 * @return The offset: (off_x, off_y).
	 */
	public int[] getOffset() {
		return off;
	}

	/**
	 * Get the x coordinate of this object from the left of the screen.
	 * 
	 * @return The x coordinate (in pixels).
	 */
	public int getX() {
		return coor_x;
	}

	/**
	 * Get the y coordinate of this object from the top of the screen.
	 * 
	 * @return The y coordinate (in pixels).
	 */
	public int getY() {
		return coor_y;
	}

	/**
	 * Attempt to remove the Object from any map it may be a part of.
	 * @return True if the object belongs to a map and is removed.
	 */
	public boolean removeSelf(){
		if(drawNext!=null && drawPrevious!=null){
			m = null;
			drawNext.drawPrevious = drawPrevious;
			drawPrevious.drawNext = drawNext;
			return true;
		}
		return false;
	}

	/**
	 * Set the image to be rendered.
	 * 
	 * @param i
	 *            The image object to be rendered.
	 */
	public void setImage(Img i) {
		this.i = i;
	}

	/**
	 * Get the image currently being used by the SimpleObject.
	 * 
	 * @return The current image.
	 */
	public Img getImage() {
		return i;
	}

	/**
	 * Play a sound. If a sound is played more than once, the sound object
	 * should by loaded statically, and then played:
	 * 
	 * <pre>
	 * <code>
	 *  public class yourClass extends SimpleObject
	 *  {
	 *  	File f = ...;
	 *  	static Sound beep = SoundUpload.getInstance(f.getParentFile()).getSound(f.getName());
	 *  	
	 *  	... Overridden methods here...
	 *  	
	 *  	public void foo(){
	 *  		playSound(beep);
	 *  	}
	 *  }
	 * </code>
	 * </pre>
	 * 
	 * @param sound
	 *            The address of the sounds to be played.
	 */
	public void playSound(String sound) {
		File f = new File(sound);
		Sound s = SoundUpload.getInstance(f.getParentFile()).getSound(
				f.getName());
		TrackPlayer.getPlayer().play(s);
	}

	/**
	 * Play a sound. For information on how to play a sound statically:
	 * {@link #playSound(String) playSound}.
	 * 
	 * @param sound
	 *            The sound object to be played.
	 */
	public void playSound(Sound sound) {
		TrackPlayer.getPlayer().play(sound);
	}

	/**
	 * If this object is a solid, return itself. Else return null.
	 * 
	 * @return null iff this object is not Solid.
	 */
	public SimpleSolid getSolid() {
		return null;
	}
	
	/**
	 * Get the map that this object is currently a part of.
	 * @return The current map
	 */
	public SimpleMap getMap() {
		return m;
	}
}
