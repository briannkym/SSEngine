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

import java.awt.Graphics2D;
import java.io.File;

import sound.Sound;
import sound.SoundUpload;
import sound.TrackPlayer;
import sprite.ColorImg;
import sprite.Img;
import sprite.ImgUpload;
import sprite.NullImg;

public abstract class SimpleObject {

	SimpleObject drawNext = null;
	SimpleObject drawPrevious = null;
	SimpleObject updateNext = null;

	public static final int NO_MOVES_NO_COLLIDES = 0, NO_MOVES = 1,
			NO_COLLIDES = 2, NORMAL = 3;
	
	private Img i = NullImg.getInstance();
	int updates = NORMAL;
	int coor_x, coor_y, pre_cx, pre_cy;
	
	SimpleMap m;

	protected final int[] off = { 0, 0 };

	abstract public void collision(SimpleObject s);

	abstract public void update();

	abstract public int id();
	
	public SimpleObject getClone(String s){
		return null;
	}
	
	public String getDescription(){
		return "";
	}

	public SimpleObject() {
		this(NORMAL);
	}

	public SimpleObject(int optimization) {
		this.updates = optimization;
	}

	public SimpleObject(String sprite) {
		this(sprite, NORMAL);
	}

	public SimpleObject(String sprite, int optimization) {
		this(optimization);
		File f = new File(sprite);
		this.i = ImgUpload.getInstance(f.getParentFile()).getImg(f.getName());
	}
	
	public boolean cancelMove(){
		if(coor_x!=pre_cx || coor_y!=pre_cy){
			coor_x = pre_cx;
			coor_y = pre_cy;
			return true;
		} else {
			return false;
		}
	}

	public boolean move(int x, int y, boolean relative) {
		boolean movement = false;
		if (relative) {
			x += coor_x;
			y += coor_y;
		}
		pre_cy = coor_y;
		pre_cx = coor_x;		

		if(x > 0){
			if( x < m.mapWmax) {
				coor_x = x;
				movement = true;
			} else {
				coor_x = m.mapWmax;
			}
		} else {
			coor_x = 0;
		}
		
		if(y > 0){
			if( y < m.mapHmax) {
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

	void newUpdate() {
		switch (updates) {
		case NORMAL:
			for (SimpleSolid S : m.getCollisions(coor_x, coor_y)){
				if (S != null) {
					if(S != this){
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
			//Do nothing on move.
			break;
		}
	}

	public void setOffset(int off_x, int off_y) {
		this.off[0] = off_x;
		this.off[1] = off_y;
	}

	public int[] getOffset() {
		return off;
	}
	
	public int getX(){
		return coor_x;
	}
	
	public int getY(){
		return coor_y;
	}

	void paintImage(Graphics2D g, int[] camera) {
		g.drawImage(i.getSlide(), coor_x + off[0] - camera[0], coor_y + off[1] - camera[1], null);
		updateNext = drawNext;
	}

	public void setImage(String sprite) {
		File f = new File(sprite);
		this.i = ImgUpload.getInstance(f.getParentFile()).getImg(f.getName());
	}

	public void setImage(int rgba, int width, int height) {
		this.i = new ColorImg(rgba, width, height);
	}

	public void setImage(Img i) {
		this.i = i;
	}

	public Img getImage() {
		return i;
	}

	public void playSound(String sound) {
		File f = new File(sound);
		Sound s = SoundUpload.getInstance(f.getParentFile()).getSound(
				f.getName());
		TrackPlayer.getPlayer().play(s);
	}
	
	public void playSound(Sound sound) {
		TrackPlayer.getPlayer().play(sound);
	}

	public SimpleSolid getSolid() {
		return null;
	}
}
