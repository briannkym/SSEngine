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

import sprite.Img;

/**
 * The basic colliding object. All collisions happen with a SimpleSolid.
 * 
 * Furthermore, the SimpleSolid automatically sorts its z-index based off of the
 * y-index on the screen.
 * 
 * @author Brian Nakayama
 * @version 1.3 Fuzz movement was created.
 * @since 1.2 Now all code is abstracted as MVC.
 * @since 1.1
 */
public abstract class SimpleSolid extends SimpleObject {

	/**
	 * Create a basic SimpleSolid.
	 */
	public SimpleSolid() {
		super(NO_COLLIDES);
	}

	/**
	 * Create a SimpleSolid optimized for moving.
	 * 
	 * @param NO_UPDATES
	 *            True if you need the object to not move.
	 */
	public SimpleSolid(boolean NO_UPDATES) {
		super(NO_UPDATES_NO_COLLIDES);
		if (!NO_UPDATES) {
			this.updates = NO_COLLIDES;
		}
	}

	/**
	 * Create a SimpleSolid with an image.
	 * 
	 * Note that this is not the good way to create a solid with an image,
	 * though it is the most convenient. Images should be loaded statically. See
	 * the same constructor for SimpleSolid for more notes.
	 * 
	 * @param sprite
	 *            The path to the image.
	 * @see SimpleObject#SimpleObject(String)
	 */
	public SimpleSolid(Img sprite) {
		super(sprite, NO_COLLIDES);
	}

	/**
	 * Create a SimpleSolid with an image, optimized for moving.
	 * 
	 * Note that this is not the good way to create a solid with an image,
	 * though it is the most convenient. Images should be loaded statically. See
	 * the same constructor for SimpleSolid for more notes.
	 * 
	 * @param sprite
	 *            The path to the image.
	 * @param NO_UPDATES
	 *            True if you need the object to move.
	 * @see SimpleObject#SimpleObject(String)
	 */
	public SimpleSolid(Img sprite, boolean NO_UPDATES) {
		super(sprite, NO_UPDATES_NO_COLLIDES);
		if (!NO_UPDATES) {
			this.updates = NO_COLLIDES;
		}
	}

	/**
	 * Undo the most recent move.
	 * 
	 * @return True if the move was successfully reversed.
	 */
	public boolean cancelMove() {
		if (move(pre_cx, pre_cy, false)) {
			pre_cx = coor_x;
			pre_cy = coor_y;
			return true;
		}
		return false;
	}

	/**
	 * The method for moving a SimpleSolid.
	 * 
	 * Similar to {@link SimpleObject#move(int, int, boolean)}, with the
	 * exception that it does not allow movements that overlap with another
	 * SimpleSolid's space. Furthermore, as of version 1.0 it does not attempt
	 * to move at all if encountering an overlap. The solids resort themselves
	 * when the y-axis has changed rows in the map.
	 * 
	 * @param x
	 *            The new x-coordinate.
	 * @param y
	 *            The new y-coordinate.
	 * @param relative
	 *            True to move relative from your position, else move
	 *            absolutely.
	 * @return True iff the object moved successfully.
	 */
	public boolean move(int x, int y, boolean relative) {
		return move(x, y, relative, 0);
	}

	/**
	 * The method for moving a solid, that will attempt to move when colliding
	 * on a corner (fuzz > 0) or will attempt to move as far along the direction
	 * indicated (fuzz = -1).
	 * 
	 * Identical to {@link SimpleSolid#move(int, int, boolean)}, except if
	 * there's a collision with only one object this method will move the object
	 * by the given fuzz amount in a favorable alignment so that this object is
	 * better situated to move when move is called again. Specifically, if we
	 * collide within cellWidth / 2 or cellHeight / 2 of a corner we move the
	 * object fuzz pixels towards the tip of the corner. This method always
	 * moves the object relative from its current position, and is not intended
	 * for large leaps greater than the size of the map cells.
	 * 
	 * 
	 * 
	 * @param x
	 *            The new relative x-coordinate.
	 * @param y
	 *            The new relative y-coordinate.
	 * @param fuzz
	 *            The alternative amount to move either NS or EW if there's a
	 *            collision.
	 * @return True iff the intended move was made or the fuzz move.
	 */
	public boolean fuzzMove(int x, int y, int fuzz) {
		return move(x, y, true, fuzz);
	}

	/**
	 * The method for moving a solid, that will attempt to move as far along the
	 * direction indicated (Same as {@link #fuzzMove(int, int, int)} where fuzz
	 * = -1).
	 * 
	 * Specifically, it looks at the first object it has collided with (if a
	 * collision occurs), and it then moves the maximum amount in both the x and
	 * y directions without colliding.
	 * 
	 * @param x
	 *            The new relative x-coordinate.
	 * @param y
	 *            The new relative y-coordinate.
	 * @return True iff a normal move or the approximation move was made.
	 */
	public boolean approxMove(int x, int y) {
		return move(x, y, true, -1);
	}

	private boolean move(int x, int y, boolean relative, int fuzz) {
		if (relative) {
			x += coor_x;
			y += coor_y;
		}

		// Check if the object is trying to leave the map.
		if (x > 0) {
			if (x > m.mapWmax) {
				x = m.mapWmax;
			}
		} else {
			x = 0;
		}

		if (y > 0) {
			if (y > m.mapHmax) {
				y = m.mapHmax;
			}
		} else {
			y = 0;
		}

		// Calculate if the move is feasible.
		m.calculateCollisions(x, y, this);
		boolean isMe = (collisions[0] == this);

		if (collisions[0] == null || (isMe && collisions[1] == null)) {

			int pre_y = coor_y / m.cellHeight;
			int new_y = y / m.cellHeight;

			int relY = y / m.cellHeight - coor_y / m.cellWidth;

			pre_cx = coor_x;
			pre_cy = coor_y;
			coor_x = x;
			coor_y = y;

			m.map[pre_y][pre_cx / m.cellWidth] = null;
			m.map[new_y][coor_x / m.cellWidth] = this;

			/*
			 * Only if we've made a significant change in the y direction do we
			 * need to do the complicated sorting part.
			 */
			if (relY == 0) {
				return true;
			} else {
				// Remove from old position
				drawNext.drawPrevious = drawPrevious;
				drawPrevious.drawNext = drawNext;
				// Insert into new position
				drawPrevious = m.mapArray[new_y].drawPrevious;
				drawNext = m.mapArray[new_y];
				drawPrevious.drawNext = this;
				drawNext.drawPrevious = this;
				return true;
			}
		} else {
			switch (fuzz) {
			default:
				// If the fuzz parameter is nonzero, and there is at most one
				// collision.
				if (collisions[2] == null && collisions[1] != null) {
					final int dx;
					final int dy;
					if (isMe) {
						dx = collisions[1].coor_x - collisions[0].coor_x;
						dy = collisions[1].coor_y - collisions[0].coor_y;
					} else {
						dx = collisions[0].coor_x - collisions[1].coor_x;
						dy = collisions[0].coor_y - collisions[1].coor_y;
					}

					if (dx > m.cellWidth / 2 && dx < m.cellWidth) {
						return move(Math.max(dx - m.cellWidth, -fuzz), 0, true,
								0);
					} else if (-dx > m.cellWidth / 2 && -dx < m.cellWidth) {
						return move(Math.min(m.cellWidth + dx, fuzz), 0, true,
								0);
					}

					if (dy > m.cellHeight / 2 && dy < m.cellHeight) {
						return move(0, Math.max(dy - m.cellHeight, -fuzz),
								true, 0);
					} else if (-dy > m.cellHeight / 2 && -dy < m.cellHeight) {
						return move(0, Math.min(m.cellHeight + dy, fuzz), true,
								0);
					}
				}
				/* no break */
			case 0:
				/* Notify all objects in the collision list. */
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
				break;
			case -1:
				/* Approximate move. Move as far along two vectors as possible. */
				if (collisions[1] != null) {
					final int dx1, dy1;
					int dx2, dy2;
					dx2 = x - coor_x;
					dy2 = y - coor_y;

					if (isMe) {
						dx1 = collisions[1].coor_x - coor_x;
						dy1 = collisions[1].coor_y - coor_y;
					} else {
						dx1 = collisions[0].coor_x - coor_x;
						dy1 = collisions[0].coor_y - coor_y;
					}

					if (dx1 >= m.cellWidth || dx1 <= -m.cellWidth) {
						dx2 = (x - coor_x > 0) ? dx1 - m.cellWidth : dx1
								+ m.cellWidth;
					}
					if (dy1 >= m.cellHeight || dy1 <= -m.cellHeight) {
						dy2 = (y - coor_y > 0) ? dy1 - m.cellHeight : dy1
								+ m.cellHeight;
					}
					
					if(dx2 == 0 && dy2== 0){
						return false;
					} else {
						return move(dx2, dy2, true, 0);
					}
				}

			}

		}

		return false;
	}

	/**
	 * Get the solid version of this object.
	 * 
	 * @return The SimpleSolid pointer for this object.
	 */
	public SimpleSolid getSolid() {
		return this;
	}

	/**
	 * Attempt to remove the Object from any map it may be a part of.
	 * 
	 * @return True if the object belongs to a map and is removed.
	 */
	public boolean removeSelf() {
		if (drawNext != null && drawPrevious != null) {
			final int x_n = coor_x / m.cellWidth;
			final int y_n = coor_y / m.cellHeight;
			m.map[y_n][x_n] = null;
			m = null;
			drawNext.drawPrevious = drawPrevious;
			drawPrevious.drawNext = drawNext;
			return true;
		}
		return false;
	}

	/**
	 * Get the solid from the specified coordinates.
	 * 
	 * @param x
	 *            The x coordinate.
	 * @param y
	 *            The y coordinate.
	 * @param relative
	 *            True for relative coordinates from the current object, false
	 *            for absolute.
	 * @return The SimpleSolid. False if the solid does not exist.
	 */
	public SimpleSolid getSolid(int x, int y, boolean relative) {
		if (relative) {
			x += coor_x;
			y += coor_y;
		}

		if (x < 0 || x > m.mapWmax || y < 0 || y > m.mapHmax) {
			return null;
		}

		return m.map[y / m.cellHeight][x / m.cellWidth];
	}
}
