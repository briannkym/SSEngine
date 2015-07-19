package world;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public class SimpleFactoryIO {
	private File f;
	private DataOutputStream dO;
	private DataInputStream dI;
	private boolean canPrint = false, canRead = false;

	/**
	 * Create a new Factory IO object with the specified path.
	 * 
	 * @param path The path of the SimpleWorldFactory list.
	 */
	public SimpleFactoryIO(String path) {
		this.f = new File(path);
	}
	
	public SimpleFactoryIO(File f) {
		this.f = f;
	}
	
	public static SimpleWorldFactory loadFactory(File f){
		SimpleFactoryIO s = new SimpleFactoryIO(f);
		s.openFactory(true);
		SimpleWorldFactory swf = s.readFactory();
		s.closeFactory();
		return swf;
	}
	
	public static boolean saveFactory(File f, SimpleWorldFactory swf){
		SimpleFactoryIO s = new SimpleFactoryIO(f);
		s.openFactory(false);
		boolean saved = s.writeFactory(swf);
		s.closeFactory();
		return saved;
	}
	
	/**
	 * Open a factory stream.
	 * 
	 * @return True iff the stream was opened successfully.
	 */
	public boolean openFactory(boolean read) {
		if (!f.exists()) {
			try {
				if (!f.createNewFile()) {
					return false;
				}
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}

		return openStream(read);
	}

	/**
	 * Switches a stream to either read or write. Warning: This method will
	 * overwrite a file if read = false.
	 * 
	 * @param read
	 *            True for an input stream, false for an output stream
	 * @return True if the stream was successfully opened.
	 */
	public boolean setWriteRead(boolean read) {
		closeFactory();
		return openStream(read);
	}

	private boolean openStream(boolean read) {
		try {
			if (read) {
				FileInputStream fis = new FileInputStream(f);
				BufferedInputStream bis = new BufferedInputStream(fis);
				GZIPInputStream zis = new GZIPInputStream(bis);
				dI = new DataInputStream(zis);
				
				canRead = true;
			} else {
				FileOutputStream fos = new FileOutputStream(f, false);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				GZIPOutputStream zos = new GZIPOutputStream(bos);
				dO = new DataOutputStream(zos);
				canPrint = true;
			}

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		canPrint = true;
		return true;
	}

	/**
	 * Write a map to a file.
	 * 
	 * This method can save the specific properties of a SimpleObject through
	 * its getDescription() method which the user can override. This method is
	 * not optimized for efficiency, and it does not save any global state
	 * associated with the SimpleWorld such as which SimpleWorldObject should be
	 * used, the background, or which object SimpleWorld should follow with the camera. The user
	 * should implement their own methods for saving game state. This method is
	 * best used to load the initial state of the game.
	 * 
	 * 
	 * Objects with an key of -1 will not be saved.
	 * 
	 * 
	 * The format of the saved file will be:
	 * 
	 * &lt; int: number of objects &gt;<br>
	 * &lt; int: map width &gt;<br>
	 * &lt; int: map height &gt; <br>
	 * &lt; int: map cellWidth &gt; <br>
	 * &lt; int: map cellHeight &gt; <br>
	 * 
	 * # count = |map_objects| <br>
	 * for (s in map_objects) do <br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt; int: key &gt;<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt; int: s.coor_x &gt;<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt; int: s.coor_y &gt;<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt; utf_string: s.description &gt; <br>
	 * done.
	 * 
	 * @param m
	 *            The map to be saved.
	 * @return True if the map was saved successfully.
	 * @see SimpleObject
	 * @see SimpleWorldFactory#getKey(SimpleObject)
	 * @see SimpleMap
	 */
	public boolean writeFactory(SimpleWorldFactory w) {
		try {
			if (canPrint) {
				dO.writeUTF(w.toString());
				dO.flush();
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Read a mp from a file. Reverses the process defined by
	 * SimpleMapIO.writeMap.
	 * 
	 * @return The SimpleMap represented by the file.
	 * @see SimpleWorldFactory
	 */
	public SimpleWorldFactory readFactory() {
		try {
			if (canRead) {
				String s = dI.readUTF();
				return new SimpleWorldFactory(s);
			}
		} catch (Exception e) {
			System.out.println("Error: Couldn't read factory object.");
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Closes any input or output stream to the file.
	 */
	public void closeFactory() {
		try {
			if (dI != null) {
				dI.close();
			}
			if (dO != null) {
				dO.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
