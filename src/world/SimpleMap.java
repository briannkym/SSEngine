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

/**
 * SimpleMap holds a 2d map for SimpleSolids, all the methods for adding and
 * removing objects, as well as code for changing the z-index of a SimpleObject.
 * 
 * It works by creating a doubly linked list for the draw order of each
 * SimpleObject. The order in the list represents the order in which objects are
 * drawn. The list for solids is maintained such that each solid is drawn in the
 * order of the y-value of their cells. This automatic z-indexing is useful for
 * games that have many moving objects on an isometric-ish surface.
 * 
 * TODO Keep track of tail of solid list so that the list can be drawn in
 * reverse order.
 * 
 * @author Brian Nakayama @ * @see SimpleWorld
 * @version 1.0
 */
public class SimpleMap {

	/*
	 * The 2d map representing locations of solids. Used for detecting
	 * collisions and adding objects.
	 */
	final SimpleSolid[][] map;
	// The z-indexes. 0-> draw first, 1-> drawn second, etc.
	final SimpleObject[] zArray;
	// The width of a cell (smallest unit) for collisions.
	final int cellWidth;
	// The height of a cell (smallest unit) for collisions.
	final int cellHeight;
	// The width of a map. Equals map[0].length * cellWidth.
	final int mapWmax;
	// The height of a map. Equals map.length * cellHeight.
	final int mapHmax;

	// Holds the last object in the list of that index.
	public final int solidIndex;

	/**
	 * Basic constructor initiates the map with Z-indexes 0-15. Index 8 is saved
	 * for SimpleSolid objects.
	 * 
	 * @param width
	 *            The width of the map in cells.
	 * @param height
	 *            The height of the map in cells.
	 * @param cellWidth
	 *            The width of the cells of the map.
	 * @param cellHeight
	 *            The height of the cells of the map.
	 */
	public SimpleMap(int width, int height, int cellWidth, int cellHeight) {
		this(width, height, cellWidth, cellHeight, 16);
	}

	/**
	 * Full constructor initiates the map with a chosen number of Z-indexes.
	 * Index number/2 will be saved for the SimpleSolidObjects.
	 * 
	 * @param width
	 *            The width of the map in cells.
	 * @param height
	 *            The height of the map in cells.
	 * @param cellWidth
	 *            The width of the cells of the map.
	 * @param cellHeight
	 *            The height of the cells of the map.
	 * @param zWidth
	 *            The number of Z-indexes.
	 */
	public SimpleMap(int width, int height, int cellWidth, int cellHeight,
			int zWidth) {
		this.cellWidth = cellWidth;
		this.cellHeight = cellHeight;
		this.map = new SimpleSolid[height][width];
		this.zArray = new SimpleObject[zWidth];
		this.mapWmax = cellWidth * (width - 1);
		this.mapHmax = cellHeight * (height - 1);
		if (zWidth < 2) {
			zWidth = 2;
		}
		this.solidIndex = zWidth / 2;
	}

	/**
	 * Adds a SimpleObject at the default index. This is either the solidIndex
	 * or solidIndex -1 (for SimpleObjects).
	 * 
	 * @param o
	 *            The SimpleObject to add to the map.
	 * @param x
	 *            The x-coordinate (in pixels)
	 * @param y
	 *            The y-coordinate (in pixels)
	 * @return True iff the object was successfully added.
	 */
	public boolean addSimpleObject(SimpleObject o, int x, int y) {
		return addSimpleObject(o, x, y, solidIndex);
	}

	/**
	 * Adds a SimpleObject at the index given. If the object is a SimpleSolid,
	 * the given index is ignored and the object will be added with the
	 * SolidIndex. The z-index must be within the zWidth given in the
	 * constructor.
	 * 
	 * <b>(0 <= z-index < zWidth)</b>
	 * 
	 * @param o
	 *            The SimpleObject to add to the map.
	 * @param x
	 *            The x-coordinate (in pixels)
	 * @param y
	 *            The y-coordinate (in pixels)
	 * @param z
	 *            The z-index
	 * @return True iff the object was successfully added.
	 */
	public boolean addSimpleObject(SimpleObject o, int x, int y, int z) {
		SimpleSolid s = o.getSolid();
		boolean done = false;
		if (s != null) {
			z = solidIndex;
			final int x_n = x / cellWidth;
			final int y_n = y / cellHeight;
			if (getCollisions(x, y)[0] == null) {
				for (int y0 = y_n; y0 >= 0 && !done; y0--) {
					for (int x0 = map.length - 1; x0 >= 0 && !done; x0--) {
						if (map[y0][x0] != null) {
							s.drawNext = map[y0][x0].drawNext;
							if (s.drawNext != null) {
								s.drawNext.drawPrevious = s;
							}
							s.drawPrevious = map[y0][x0];
							s.drawPrevious.drawNext = s;
							done = true;
						}
					}
				}

				map[y_n][x_n] = s;
			} else {
				return false;
			}
		} else if (z == solidIndex) {
			z--;
		}

		if (!done) {
			if (zArray[z] != null) {
				o.drawNext = zArray[z];
				o.drawPrevious = null;
				o.drawNext.drawPrevious = o;
			}
			zArray[z] = o;
		}
		o.m = this;
		o.coor_x = x;
		o.pre_cx = x;
		o.coor_y = y;
		o.pre_cy = y;
		o.updates = SimpleObject.NORMAL;
		return true;
	}

	/**
	 * Prints all of the objects currently registered to the map.
	 */
	@Override
	public String toString() {
		String s = "";
		int total = 0;
		for (SimpleObject S : zArray) {
			if (S != null) {
				s += "Next layer...\n";
				for (; S != null; S = S.drawNext) {
					s += S.id() + ", " + S.coor_x + ", " + S.coor_y + ", "
							+ S.drawPrevious + ", " + S + "\n";
					total++;
				}
			}
		}
		s += "There were " + total + " total objects found.\n";
		return s;
	}

	/**
	 * Locates all of the possible collisions for an object of cellWidth and
	 * cellHeight colliding at a position x, y. Note, this will return a
	 * potential collision with itself. To check that there are no other
	 * collisions other than oneself the following code is recommended:
	 * 
	 * <pre>
	 * <code>
	 * SimpleSolid[] collisions = m.getCollisions(x, y);
	 * if (collisions[0] == null || (collisions[0] == this && collisions[1] == null)) {
	 * 		etc...
	 * </code>
	 * </pre>
	 * 
	 * @param x
	 *            The x position of the top left corner of the object.
	 * @param y
	 *            The y position of the top left corner of the object.
	 * @return An array (width = 4) containing up to 4 possible collisions.
	 */
	public SimpleSolid[] getCollisions(int x, int y) {
		int grid_x = x / cellWidth;
		int grid_y = y / cellHeight;

		SimpleSolid[] collisions = new SimpleSolid[4];

		int index = 0;
		for (int y0 = Math.max(grid_y - 1, 0); y0 <= Math.min(grid_y + 1,
				map.length - 1); y0++) {
			for (int x0 = Math.max(grid_x - 1, 0); x0 <= Math.min(grid_x + 1,
					map[y0].length - 1); x0++) {
				if (map[y0][x0] != null) {
					if (Math.abs(map[y0][x0].coor_x - x) < cellWidth
							&& Math.abs(map[y0][x0].coor_y - y) < cellHeight) {
						collisions[index] = map[y0][x0];
						index++;
					}
				}
			}
		}
		return collisions;
	}

	/**
	 * Removes a SimpleObject from the map.
	 * 
	 * <b>Note there is a chance that this method will remove objects associated
	 * with other maps. If the object is a solid, this method will first try to
	 * remove it from it's z-index and then it'll try to remove it from the map.
	 * Using this method for objects not stored in this map may result in
	 * unusual errors.</b>
	 * 
	 * @param o
	 *            The object to be removed.
	 * @return True if the object was successfully removed.
	 */
	public boolean removeSimpleObject(SimpleObject o) {
		boolean removed = false;
		if (o.drawPrevious == null) {
			// Check the heads of the lists.
			for (int i = 0; i < zArray.length; i++) {
				if (zArray[i] == o) {
					// Check if the element to remove is the only element in the
					// list.
					if (o.drawNext != null) {
						zArray[i] = o.drawNext;
						o.drawNext.drawPrevious = null;
					}
					else {
						zArray[i] = null;
					}
					removed = true;
				}
			}
		} else if (o.drawNext != null) {
			// Otherwise it is in the middle.
			o.drawNext.drawPrevious = o.drawPrevious;
			o.drawPrevious.drawNext = o.drawNext;
			removed = true;
		} else {
			// Finally check the tail.
			o.drawPrevious.drawNext = null;
			removed = true;
		}

		o.drawNext = null;
		o.drawPrevious = null;
		o.updates = SimpleObject.NO_UPDATES_NO_COLLIDES;
		SimpleSolid s = o.getSolid();
		if (s != null) {
			if (map[s.coor_y / cellHeight][s.coor_x / cellWidth] == s) {
				map[s.coor_y / cellHeight][s.coor_x / cellWidth] = null;
			} else {
				return false;
			}
		}
		return removed;
	}

	/**
	 * Attempts to remove a SimpleSolid by its x and y coordinate on the grid.
	 * Notice, this is not the same as removing by the x and y pixel locations.
	 * 
	 * @param x
	 *            The x cell of the map.
	 * @param y
	 *            The y cell of the map
	 * @return True iff the object was in the map, and is now successfully
	 *         removed.
	 * @see #removeSimpleObject(SimpleObject)
	 */
	public boolean removeSimpleSolid(int x, int y) {
		if (map[y][x] != null) {
			return removeSimpleObject(map[y][x]);
		}
		return false;
	}

	/**
	 * Removes all objects from the map.
	 */
	public void clearAll() {
		for (int y = 0; y < map.length; y++) {
			for (int x = 0; x < map[y].length; x++) {
				map[y][x] = null;
			}
		}
		for (int z = 0; z < zArray.length; z++) {
			zArray[z] = null;
		}
	}

	/**
	 * Changes the z index of a SimpleObject. This is done by removing the
	 * object, and then adding the object to a different zIndex.
	 * 
	 * @param o
	 * @param z
	 * @return False iff the object is not in the map or the object cannot be
	 *         re-added (in which case it is just removed).
	 * @see #removeSimpleObject(SimpleObject)
	 */
	public boolean changeZIndex(SimpleObject o, int z) {
		int updates = o.updates;
		if (removeSimpleObject(o)) {
			o.updates = updates;
			return addSimpleObject(o, o.coor_x, o.coor_y, z);

		}
		return false;
	}

}
