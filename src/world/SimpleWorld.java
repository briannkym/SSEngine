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

public class SimpleWorld extends JFrame implements Pinterface {

	private Img background;
	private Projector ip;
	private String title;
	private final int[] camera = { 0, 0 };
	private SimpleObject cameraStalk = null;

	private int width, height;
	private SimpleMap m;
	private SimpleWorldObject swo = NullSimpleWorldObject.getInstance();
	private boolean update = true;

	public SimpleWorld(SimpleMap m, int width, int height, String title) {
		this.m = m;
		this.width = width;
		this.height = height;
		BufferedImage bi = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		this.title = title;
		this.ip = new Projector(20.0f, bi, title, this);
	}

	public void start(boolean fullscreen) {
		if (fullscreen) {
			ip.init(this);
		} else {
			//TODO Update this code to get the dimensions of the buffered Image.
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

	public void setSimpleSolidMap(SimpleMap m) {
		this.m = m;
	}

	public SimpleMap getSimpleSolidMap() {
		return m;
	}

	public void setSimpleWorldObject(SimpleWorldObject swo) {
		this.swo = swo;
	}

	public SimpleWorldObject getSimpleWorldObject() {
		return swo;
	}
	
	public void setCameraStalk(SimpleObject cameraStalk){
		this.cameraStalk = cameraStalk;
	}

	public void setBGImage(String sprite) {
		File f = new File(sprite);
		this.background = ImgUpload.getInstance(f.getParentFile()).getImg(
				f.getName());
	}

	public void setBGImage(int rgba, int width, int height) {
		this.background = new ColorImg(rgba, width, height);
	}

	public void setBGImage(Img i) {
		this.background = i;
	}

	public void setCamera(int x, int y) {
		camera[0] = x;
		camera[1] = y;
	}

	public int[] getCamera() {
		return camera;
	}

	public Projector getProjector() {
		return ip;
	}

	@Override
	public void iUpdate(BufferedImage ISlide) {

		//Update camera coordinates based off of the width and height.
		if (cameraStalk!=null){
			int camx = cameraStalk.coor_x - (width - m.cellWidth)/2;
			int camy = cameraStalk.coor_y - (height - m.cellHeight)/2;
			camera[0] = (camx >= 0 && camx <= m.mapWmax - width) ? camx : camera[0];
			camera[1] = (camy >= 0 && camy <= m.mapHmax - height) ? camy : camera[1];
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
		
		//Update all objects.
		if (update) {
			for (SimpleObject s : m.zArray) {
				if (s != null) {
					for (; s != null; s = s.updateNext) {
						s.newUpdate();
					}
				}
			}
		}

		//Paint all objects.
		for (SimpleObject s : m.zArray) {
			if (s != null) {
				for (; s != null; s = s.drawNext) {
					s.paintImage(g, camera);
				}
			}
		}

		//Paint the world object over the projection.
		swo.updateScreen(ISlide, g);
		g.dispose();
	}
}
