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
 * 
 * 
 * SimpleMap holds a 2d map for SimpleSolids, all the methods for adding and
 * removing objects, as well as code for changing the z-index of a SimpleObject.
 * 
 * It works by creating a doubly linked list for the draw order of each
 * SimpleObject. The order in the list represents the order in which objects are
 * drawn. The list for solids is maintained such that each solid is drawn in the
 * order of the y-value of their cells. This automatic z-indexing is useful for
 * games that have many moving objects on an isometric-ish surface.
 * 
 * 
 * @author Brian Nakayama @ * @see SimpleWorld
 * @author Mark Groeneveld
 * @version 1.1
 */
public class SimpleMap {

	/*
	 * The 2d map representing locations of solids. Used for detecting
	 * collisions and adding objects.
	 */
	final SimpleSolid[][] map;
	// The z-indexes. 0-> draw first, 1-> drawn second, etc.
	final SimpleObject[] zArray;
	// The z-indexes per row of the map.
	final SimpleObject[] mapArray;

	// The width of a cell (smallest unit) for collisions.
	public final int cellWidth;
	// The height of a cell (smallest unit) for collisions.
	public final int cellHeight;
	// The width of possible coordinates in a map. Equals (map[0].length - 1) *
	// cellWidth.
	public final int mapWmax;
	// The height of possible coordinates in a map. Equals (map.length - 1) *
	// cellHeight.
	public final int mapHmax;

	// Holds the last object in the list of that index.
	int solidIndex;

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
		this.zArray = new SimpleObject[zWidth + 1];
		this.mapArray = new SimpleObject[height];
		this.mapWmax = cellWidth * (width - 1);
		this.mapHmax = cellHeight * (height - 1);
		if (zWidth < 2) {
			zWidth = 2;
		}
		this.solidIndex = zWidth / 2;
		clearAll();
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
	 * constructor. This method also confirms that the object is not already
	 * part of a map.
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
		if (s != null) {
			return addSimpleSolid(s, x, y);
		} else if (z < zArray.length && o.m == null) {
			o.drawPrevious = zArray[z];
			o.drawNext = zArray[z].drawNext;
			o.drawPrevious.drawNext = o;
			o.drawNext.drawPrevious = o;
		} else {
			return false;
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
	 * Adds a SimpleSolid to the solidIndex.
	 * 
	 * @param s
	 *            The SimpleObject to add to the map.
	 * @param x
	 *            The x-coordinate (in pixels)
	 * @param y
	 *            The y-coordinate (in pixels)
	 * 
	 * @return True iff the object was successfully added.
	 */
	public boolean addSimpleSolid(SimpleSolid s, int x, int y) {
		final int x_n = x / cellWidth;
		final int y_n = y / cellHeight;
		calculateCollisions(x, y, s);
		if (s.collisions[0] == null) {
			map[y_n][x_n] = s;
			s.drawPrevious = mapArray[y_n].drawPrevious;
			s.drawNext = mapArray[y_n];
			s.drawPrevious.drawNext = s;
			s.drawNext.drawPrevious = s;
		} else {
			return false;
		}
		s.m = this;
		s.coor_x = x;
		s.pre_cx = x;
		s.coor_y = y;
		s.pre_cy = y;
		return true;
	}

	/**
	 * Get the beginning of the drawList
	 * 
	 * @return The first element in the drawList
	 */
	SimpleObject getDrawBegin() {
		return zArray[0];
	}

	/**
	 * Get the end of the drawList
	 * 
	 * @return The last element in the drawList
	 */
	SimpleObject getDrawEnd() {
		return zArray[zArray.length - 1];
	}

	/**
	 * Prints all of the objects currently registered to the map.
	 */
	@Override
	public String toString() {
		String s = "";
		int total = 0;
		s += "Next layer...\n";
		for (SimpleObject S = zArray[0]; S != null; S = S.drawNext) {
			s += S.id() + ", " + S.coor_x + ", " + S.coor_y + ", "
					+ S.drawPrevious + ", " + S + "\n";
			total++;
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
	public SimpleObject[] calculateCollisions(int x, int y, SimpleObject s) {
		final int grid_x = x / cellWidth;
		final int grid_y = y / cellHeight;

		s.collisions[0] = null;
		s.collisions[1] = null;
		s.collisions[2] = null;
		s.collisions[3] = null;

		int index = 0;		
			
		for (int y0 = Math.max(grid_y - 1, 0); y0 <= Math.min(grid_y + 1,
				map.length - 1); y0++) {
			for (int x0 = Math.max(grid_x - 1, 0); x0 <= Math.min(grid_x + 1,
					map[y0].length - 1); x0++) {
				if (map[y0][x0] != null) {
					if (Math.abs(map[y0][x0].coor_x - x) < cellWidth
							&& Math.abs(map[y0][x0].coor_y - y) < cellHeight) {
						s.collisions[index] = map[y0][x0];
						index++;
					}
				}
			}
		}
		
		return s.collisions;
	}

	/**
	 * Removes a SimpleObject from the map.
	 * 
	 * <b>If the object is a solid, this method will first try to remove it from
	 * it's z-index and then it'll try to remove it from the map. </b>
	 * 
	 * @param o
	 *            The object to be removed.
	 * @return True if the object was successfully removed.
	 */
	public boolean removeSimpleObject(SimpleObject o) {
		if (o.m == this) {
			return o.removeSelf();
		}
		return false;
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
		zArray[0] = new StaticSimpleObject();
		for (int n = 1; n < zArray.length; n++) {
			zArray[n] = new StaticSimpleObject();
			zArray[n].drawPrevious = zArray[n - 1];
			zArray[n - 1].drawNext = zArray[n];
		}

		mapArray[0] = new StaticSimpleObject();
		for (int y = 1; y < mapArray.length; y++) {
			mapArray[y] = new StaticSimpleObject();
			mapArray[y].drawPrevious = mapArray[y - 1];
			mapArray[y - 1].drawNext = mapArray[y];
		}

		zArray[solidIndex].drawNext = mapArray[0];
		zArray[solidIndex].drawNext.drawPrevious = zArray[solidIndex];
		mapArray[mapArray.length - 1].drawNext = zArray[solidIndex + 1];
		mapArray[mapArray.length - 1].drawNext.drawPrevious = mapArray[mapArray.length - 1];
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
		if (o.m == this) {
			o.removeSelf();
			return addSimpleObject(o, o.coor_x, o.coor_y, z);
		}
		return false;
	}
	
	/**
	 * Returns the max pixel at which an object can exist
	 * on the x-axis.
	 * @return The max pixel
	 */
	public int getMapPixelWidth() {
		return mapWmax;
	}
	
	/**
	 * Returns the max pixel at which an object can exist
	 * on the y-axis.
	 * @return The max pixel
	 */
	public int getMapPixelHeight() {
		return mapHmax;
	}
}
