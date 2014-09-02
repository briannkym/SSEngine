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
package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * The Projector class simply takes an image, and projects it in either full
 * screen mode, or in a JPanel for a given FPS value.
 * 
 * &nbsp;&nbsp;&nbsp;&nbsp; This class sets up the nitty gritty details for a
 * refreshing frame in use for a game or other simulation. Here's a quick
 * example for how to use Projector:
 * 
 * <pre>
 * <code>
 *  public class yourClass implements Pinterface
 *  {
 *  	private Projector ip;
 *  	private BufferedImage bi;
 *  	private JPanel jp;
 *  
 *   	public yourClass()
 *   	{
 *   		bi = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
 *   		ip = new GProjector(10f, bi, "Test", this);
 *   		jp = ip.init(new Dimension(400, 400));
 *   		//or just ip.init() for full screen.
 *   	}
 *  
 *   	public void iUpdate()
 *   	{
 *   	//Code that updates bi, goes here.
 * 	 	}
 * 	}
 * </code>
 * </pre>
 * 
 * @author Brian Nakayama
 * @version 1.7 FSEM implemented for init() method.
 * @since 1.6 Bug fix to the way thread was created.
 * @since 1.5 Several small convenience fixes have been made.
 */
public class Projector implements Runnable {
	// Informs the Thread loop whether to continue running or not.

	private volatile boolean bRun = true;
	// Informs the user if the Projector is running.
	private volatile boolean bIsRunning = false;
	// A Container for the JFrame used in FSM.
	private Container c;
	// The Dimensions of the Projection.
	private int width, height, x, y;
	// The desired Frames per Second.
	private volatile float fFps;
	// The GFeed object that updates the Image/Slide.
	private Pinterface iI;
	// The Image/Slide, to be Projected.
	private volatile BufferedImage ISlide;
	// The JFrame for FSM mode.
	private JFrame jf;
	// The JPanel that holds the Image Projection.
	private JPanel jp;
	// The Thread that loops the Projection.
	private volatile Thread t;
	// A title for FSM mode.
	private String sTitle;

	public static final int TOP_SPACE = 40;

	/**
	 * Creates the GProjector Object
	 * 
	 * @param fFps
	 *            A float representing the desired Frames per second.
	 * @param ISlide
	 *            The Image, or "slide", to be projected.
	 * @param sTitle
	 *            A String representing the title of the projection(FSM only)
	 * @param iI
	 *            The GFeed object that'll receive updates.
	 */
	public Projector(float fFps, BufferedImage ISlide, String sTitle,
			Pinterface iI) {
		this.fFps = fFps;
		this.ISlide = ISlide;
		this.sTitle = sTitle;
		this.iI = iI;
	}

	/**
	 * Simply returns if iProjector is running.
	 * 
	 * @return A boolean defining the running status.
	 */
	public boolean isRunning() {
		return bIsRunning;
	}

	/**
	 * Say that you wanted to switch updates between two different interfaces,
	 * one should use this to set a new one.
	 * 
	 * @param iI
	 *            The new interface to receive updates
	 */
	public void setInterface(Pinterface iI) {
		this.iI = iI;
	}

	/**
	 * Sets the Frames per Second float variable.
	 * 
	 * @param fFps
	 *            The new FPS rate
	 */
	public void setFPS(float fFps) {
		this.fFps = fFps;
	}

	/**
	 * Changes the Image/Slide pointer of the projection. I only recommend this
	 * if you need the original Image replaced, otherwise just update the
	 * original Image.
	 * 
	 * @param ISlide
	 *            The new Image, or Slide, pointer
	 */
	public void setSlide(BufferedImage ISlide) {
		this.ISlide = ISlide;
	}

	/**
	 * Returns if the projection is full screen or not.
	 * 
	 * @return True if it is full screen
	 */
	public boolean isFS() {
		return jf != null;
	}

	/**
	 * Attempts to initiate a Full Screen Exclusive Mode(FSEM) Projection.
	 *  
	 * Pass in the frame that will be fit to the screen. If FSEM is not
	 * available, then this will initiate a Full Screen Mode (FSM) that
	 * stretches the screen to fir using software.
	 * 
	 * @param jf
	 *            Just for reference.
	 * @return a created JPanel, again for reference.
	 */
	public JPanel init(final JFrame jf) {
		this.jf = jf;
		c = jf.getContentPane();
		c.setLayout(new BorderLayout());

		if (jp == null) {
			jp = new JPanel();
		}

		jf.setTitle(sTitle);
		jf.setUndecorated(true);
		jf.setIgnoreRepaint(true);

		Dimension d;

		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		boolean fullScreen = gd.isFullScreenSupported();

		if (fullScreen) {
			d = new Dimension(ISlide.getWidth(), ISlide.getHeight());

			DisplayMode currentDisplay = gd.getDisplayMode();
			int dx = currentDisplay.getWidth() - ISlide.getWidth();
			int dy = currentDisplay.getHeight() - ISlide.getHeight();

			width = ISlide.getWidth();
			height = ISlide.getHeight();
			x = dx;
			y = dy;

			// Find the smallest display mode that fits the projection.
			DisplayMode[] modes = gd.getDisplayModes();
			for (DisplayMode dm : modes) {
				int tdx = dm.getWidth() - ISlide.getWidth();
				int tdy = dm.getHeight() - ISlide.getHeight();

				if (tdy + tdx < dx + dy && tdy >= 0 && tdx >= 0) {
					dx = tdx;
					dy = tdy;
					currentDisplay = dm;
				}
			}

			jp.setPreferredSize(d);
			c.add(jp, BorderLayout.CENTER);
			gd.setFullScreenWindow(jf);
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
			width = (ISlide.getWidth() * height) / ISlide.getHeight();
			y = TOP_SPACE;
			x = (d.width - width) / 2;

			jp.setPreferredSize(d);
			c.add(jp, BorderLayout.NORTH);
			jf.pack();
			jf.setVisible(true);
		}

		jp.setBackground(Color.BLACK);
		jf.setBackground(Color.BLACK);
		jf.setAlwaysOnTop(true);
		jf.setResizable(false);

		if (!bIsRunning) {
			resume();
		}

		return jp;
	}

	// TODO Change to int width, int height. It is more sensible than exposing
	// dimension.
	/**
	 * Initiates a projection on a JPanel of a certain size.
	 * 
	 * @param d
	 *            The size of the JPanel.
	 * @return Returns the JPanel that will receive the Projections.
	 */
	public JPanel init(int width, int height) {
		if (jp == null) {
			jp = new JPanel();
		}
		this.width = width;
		this.height = height;
		this.x = 0;
		this.y = 0;
		jp.setPreferredSize(new Dimension(width, height));

		if (!bIsRunning) {
			resume();
		}

		return jp;
	}

	/*
	 * A graphics update, fits the Image to either the screen(FSM) or the
	 * JPanel(Not FSM).
	 */
	private void paintUpdate() {
		Graphics g;
		try {
			g = jp.getGraphics();
			if ((g != null) && (ISlide != null)) {
				g.drawImage(ISlide, x, y, width, height, null);
				Toolkit.getDefaultToolkit().sync();
				g.dispose();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Resumes the Projection thread if stopped.
	 */
	public void resume() {
		if (!bIsRunning) {
			bRun = true;
			t = new Thread(this);
			t.start();
		}
	}

	/*
	 * Overrides the Threads run method to update the Projection, and for
	 * greatest accuracy the iInterface.
	 */
	public void run() {
		do {
			bIsRunning = true;
			long lTime = System.nanoTime();
			iI.iUpdate((BufferedImage) ISlide);
			paintUpdate();
			lTime = System.nanoTime() - lTime;
			try {
				/*
				 * The sleep method of Thread accepts milliseconds, while lTime
				 * is currently in nanoseconds. by dividing 1 second(1000f) by
				 * fFps, and subtracting the time it takes to update in
				 * milliseconds, we can make the Projection consistent.
				 */
				long lSleep = (long) (1000f / fFps) - (lTime / 1000000l);
				if (lSleep > 0) {
					Thread.sleep(lSleep);
				} else {
					System.out.println("Frame Rate Failure: " + lSleep);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} while (bRun);
		bIsRunning = false;
	}

	/**
	 * Stops the Projection thread.
	 */
	public void stop() {
		bRun = false;
	}

	/**
	 * Closes the full screen; however, it does not stop or end the application.
	 */
	public void closeFS() {
		stop();
		jf.dispose();
		jf = null;
	}
}
