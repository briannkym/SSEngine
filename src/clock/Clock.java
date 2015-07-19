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
package clock;

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
public class Clock implements Runnable {
	// Informs the Thread loop whether to continue running or not.

	private volatile boolean bRun = true;
	// Informs the user if the Projector is running.
	private volatile boolean bIsRunning = false;
	// A Container for the JFrame used in FSM.
	// The desired Frames per Second.
	private volatile float fFps;
	// The object that receives clock updates.
	private Cinterface iC;
	// The JFrame for FSM mode.
	private volatile Thread t;


	/**
	 * Creates the Clock Object
	 * 
	 * @param fFps
	 *            A float representing the desired Frames per second.
	 * @param iC
	 *            The interface object that'll receive updates.
	 */
	public Clock(float fFps, Cinterface iC) {
		this.fFps = fFps;
		this.iC = iC;
	}

	/**
	 * Simply returns if Clock is running.
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
	public void setInterface(Cinterface iI) {
		this.iC = iI;
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
	 * Initiates the clock.
	 */
	public void init() {
		if (!bIsRunning) {
			resume();
		}
	}

	/**
	 * Resumes the Clock thread if stopped.
	 */
	public void resume() {
		if (!bIsRunning) {
			bRun = true;
			t = new Thread(this);
			t.start();
		}
	}

	/*
	 * Overrides the Threads run method to update the Clock, and for
	 * greatest accuracy the iInterface.
	 */
	public void run() {
		do {
			bIsRunning = true;
			long lTime = System.nanoTime();
			iC.update();
			lTime = System.nanoTime() - lTime;
			try {
				/*
				 * The sleep method of Thread accepts milliseconds, while lTime
				 * is currently in nanoseconds. by dividing 1 second(1000f) by
				 * fFps, and subtracting the time it takes to update in
				 * milliseconds, we can make the Clock consistent.
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
}
