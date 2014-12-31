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
package desktopView;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Holds all of the code intended for a desktop view ala Java 1.7. Should be
 * used only when images from the parent package are being used.
 * 
 * @author Brian Nakayama
 * @version 1.2
 */
public class DesktopCanvas extends JFrame implements IDesktopCanvas {

	private static final long serialVersionUID = 1L;

	public static final int TOP_SPACE = 40;
	// The Dimensions of the Projection.
	private int width, height, x, y;
	JPanel jp;
	BufferedImage bi;
	Graphics2D buffer;
	private Graphics g;
	private int rotate = 0;

	/**
	 * This is one of the optional views for the game.
	 * 
	 * @param width
	 * @param height
	 * @param title
	 */
	public DesktopCanvas(int width, int height, String title) {
		super(title);
		this.bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		buffer = bi.createGraphics();
	}

	/**
	 * Using a command pattern, sets up a windowed screen.
	 */
	public void windowScreen() {
		Container c = this.getContentPane();
		jp = new JPanel();
		jp.setPreferredSize(new Dimension(bi.getWidth(), bi.getHeight()));
		this.x = 0;
		this.y = 0;
		this.width = bi.getWidth();
		this.height = bi.getHeight();
		g = jp.getGraphics();
		c.setLayout(new BorderLayout());
		c.add(jp, BorderLayout.NORTH);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setIgnoreRepaint(true);
		this.pack();
		this.setResizable(false);
		this.setVisible(true);
	}
	
	@Override
	public int getWidth(){
		return getContentPane().getWidth();
	}
	
	@Override
	public int getHeight(){
		return getContentPane().getHeight();
	}
	
	/**
	 * Using a command pattern, sets up a full screen.
	 */
	public void fullScreen() {
		Container c = this.getContentPane();
		c.setLayout(new BorderLayout());

		if (jp == null) {
			jp = new JPanel();
		}

		this.setUndecorated(true);
		this.setIgnoreRepaint(true);

		Dimension d;

		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		boolean fullScreen = gd.isFullScreenSupported();

		if (fullScreen) {
			d = new Dimension(bi.getWidth(), bi.getHeight());

			DisplayMode currentDisplay = gd.getDisplayMode();
			int dx = currentDisplay.getWidth() - bi.getWidth();
			int dy = currentDisplay.getHeight() - bi.getHeight();

			width = bi.getWidth();
			height = bi.getHeight();
			x = dx;
			y = dy;

			// Find the smallest display mode that fits the projection.
			DisplayMode[] modes = gd.getDisplayModes();
			for (DisplayMode dm : modes) {
				int tdx = dm.getWidth() - bi.getWidth();
				int tdy = dm.getHeight() - bi.getHeight();

				if (tdy + tdx < dx + dy && tdy >= 0 && tdx >= 0) {
					dx = tdx;
					dy = tdy;
					currentDisplay = dm;
				}
			}

			jp.setPreferredSize(d);
			c.add(jp, BorderLayout.CENTER);
			gd.setFullScreenWindow(this);
			// Attempt to change the display mode, and wait a ms for the screen
			// to change.
			try {
				if (gd.isDisplayChangeSupported()) {
					gd.setDisplayMode(currentDisplay);
					x = dx / 2;
					y = dy / 2;
				}
				Thread.sleep(100);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			// IF FSEM is not supported just stretch the screen to the native
			// resolution.
			d = Toolkit.getDefaultToolkit().getScreenSize();

			height = d.height - TOP_SPACE * 2;
			width = (bi.getWidth() * height) / bi.getHeight();
			y = TOP_SPACE;
			x = (d.width - width) / 2;

			jp.setPreferredSize(d);
			c.add(jp, BorderLayout.NORTH);
			this.pack();
			this.setVisible(true);
		}

		jp.setBackground(Color.BLACK);
		this.setBackground(Color.BLACK);
		this.setAlwaysOnTop(true);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public boolean isFS() {
		return false;
	}

	@Override
	public void paint() {
		try {
			g = jp.getGraphics();
			if ((g != null) && (bi != null)) {
				if(rotate != 0){
					AffineTransform at = new AffineTransform();
					at.rotate(-(rotate * 2 * Math.PI)/360.0, bi.getWidth()/2, bi.getHeight()/2);
					buffer.setTransform(at);
				} else {
					buffer.setTransform(new AffineTransform());
				}
				g.drawImage(bi, x, y, width, height, null);
				Toolkit.getDefaultToolkit().sync();
				g.dispose();

				buffer.setColor(new Color(0xFFFFFFFF));
				buffer.fillRect(0, 0, bi.getWidth(), bi.getHeight());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void register() {
	}

	@Override
	public void drawImage(BufferedImage bi, int x, int y) {
		buffer.drawImage(bi, x, y, null);		
	}

	@Override
	public void setRotation(int degrees) {
		this.rotate = degrees;
	}
}
