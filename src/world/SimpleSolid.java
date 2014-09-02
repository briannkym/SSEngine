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

public abstract class SimpleSolid extends SimpleObject {

	public SimpleSolid() {
		super(NO_COLLIDES);
	}

	public SimpleSolid(boolean NO_MOVES) {
		super(NO_MOVES_NO_COLLIDES);
		if (!NO_MOVES) {
			this.updates = NO_COLLIDES;
		}
	}

	public SimpleSolid(String sprite) {
		super(sprite, NO_COLLIDES);
	}

	public SimpleSolid(String sprite, boolean NO_MOVES) {
		super(sprite, NO_MOVES_NO_COLLIDES);
		if (!NO_MOVES) {
			this.updates = NO_COLLIDES;
		}
	}

	public boolean cancelMove() {
		if (move(pre_cx, pre_cy, false)) {
			pre_cx = coor_x;
			pre_cy = coor_y;
			return true;
		}
		return false;
	}

	public boolean move(int x, int y, boolean relative) {
		if (relative) {
			x += coor_x;
			y += coor_y;
		}
		if (x >= 0 && x <= m.mapWmax && y >= 0 && y <= m.mapHmax) {
			SimpleSolid[] collisions = m.getCollisions(x, y);
			if (collisions[0] == null
					|| (collisions[0] == this && collisions[1] == null)) {

				int relY = y / m.cellHeight - coor_y / m.cellWidth;

				pre_cx = coor_x;
				pre_cy = coor_y;
				coor_x = x;
				coor_y = y;

				m.map[pre_cy / m.cellHeight][pre_cx / m.cellWidth] = null;
				m.map[coor_y / m.cellHeight][coor_x / m.cellWidth] = this;

				if (relY == 0) {
					return true;
				} else {
					// First remove the solid from the draw list.
					final boolean searchForward = relY > 0;
					
					if (m.zArray[m.solidIndex] == this) {
						// Check head of the list.
						if (!searchForward || drawNext == null || drawNext.coor_y >= coor_y) {
							return true;
						}
						m.zArray[m.solidIndex] = drawNext;
						drawNext.drawPrevious = null;
					} else if (drawNext != null) {
						// Otherwise it is in the middle.
						drawNext.drawPrevious = drawPrevious;
						drawPrevious.drawNext = drawNext;
					} else {
						// Finally check the tail.
						if (searchForward) {
							return true;
						}
						drawPrevious.drawNext = null;
					}
					SimpleObject o;
					
					if (searchForward) {
						for (o = drawNext; o.drawNext != null; o = o.drawNext) {
							if (o.drawNext.coor_y >= coor_y) {
								drawPrevious = o;
								drawNext = o.drawNext;
								drawNext.drawPrevious = this;
								drawPrevious.drawNext = this;
								return true;
							}
						}
						drawPrevious = o;
						drawNext = null;
						drawPrevious.drawNext = this;
						return true;
					} else {
						for (o = drawPrevious; o.drawPrevious != null; o = o.drawPrevious)
						{
							if(o.drawPrevious.coor_y <= coor_y){
								drawPrevious = o.drawPrevious;
								drawNext = o;
								drawNext.drawPrevious = this;
								drawPrevious.drawNext = this;
								return true;
							}
						}
						m.zArray[m.solidIndex] = this;
						drawNext = o;
						drawPrevious = null;
						drawNext.drawPrevious = this;
						return true;
					}
				}
			} else {
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
			}
		}
		return false;
	}

	public SimpleSolid getSolid() {
		return this;
	}

	public SimpleSolid getSolid(int x, int y, boolean relative) {
		if(relative){
			x += coor_x;
			y += coor_y;
		}
		return m.map[y/m.cellHeight][x/m.cellWidth];
	}
}
