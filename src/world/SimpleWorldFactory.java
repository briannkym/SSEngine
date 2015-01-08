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

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sprite.Img;

/**
 * Holds SimpleObjects that can be added to a map by generated keys.
 * 
 * @author Brian Nakayama
 * @see SimpleObject
 */
public class SimpleWorldFactory {

	// Hold each object along with a key.
	List<SimpleObject> objects = new ArrayList<SimpleObject>();
	Map<Class<?>, Integer> object_map = new HashMap<Class<?>, Integer>();

	public SimpleWorldFactory() {

	}

	/**
	 * Create a SimpleWorldFactory with pre-registered SimpleObjects. The string
	 * should be generated using {@link #toString()}
	 * 
	 * @param s
	 *            A String holding the canonical names of SimpleObjects
	 *            separated by semicolons.
	 */
	public SimpleWorldFactory(String s) {
		String[] classes = s.split(";");
		for (int i = 0; i < classes.length; i++) {
			register(classes[i]);
		}
	}

	/**
	 * Loads all of the compiled SimpleObject *.class files in a package with
	 * the directory URL. This method will not load any inner classes.
	 * 
	 * @param directory
	 *            the URL of the package
	 * @return True if and only if all of the classes were registered as
	 *         SimpleObjects.
	 */
	public boolean loadPackage(String directory) {
		File dir = new File(directory);
		boolean registered = true;
		if (dir.isDirectory()) {
			File[] classes = dir.listFiles(new FilenameFilter() {
				public boolean accept(File directory, String fileName) {
					return fileName.endsWith(".class");
				}
			});
			for (File f : classes) {
				String path = f.getParentFile().getName() + "." + f.getName();
				if (!path.contains("$")) {
					path = path.substring(0, path.length() - 6);
					registered &= register(path);
				}
			}
		} else {
			return false;
		}
		return registered;
	}

	/**
	 * Registers a SimpleObject with a key.
	 * 
	 * @param o
	 *            The SimpleObject to add.
	 * @see SimpleObject
	 */
	public boolean register(SimpleObject o) {
		if (!object_map.containsKey(o.getClass())) {
			objects.add(o);
			object_map.put(o.getClass(), objects.size() - 1);
			return true;
		}
		return false;
	}

	/**
	 * Registers a SimpleObject with a key from a string holding the class name.
	 * 
	 * @param o
	 *            the class name to be registered.
	 * @return True if and only if the object was successfully registered.
	 */
	public boolean register(String o) {
		boolean registered = true;
		try {
			Class<?> Simp = Class.forName(o);
			objects.add((SimpleObject) Simp.newInstance());
			object_map.put(Simp, objects.size() - 1);
		} catch (Exception e) {
			System.out.println("Could not retrieve class: " + o);
			registered = false;
			e.printStackTrace();
		}
		return registered;
	}

	/**
	 * Registers all SimpleObjects in a list in order, replacing any previous
	 * objects registered.
	 * 
	 * @param o
	 *            A List of SimpleObjects in order from key 0 to n.
	 */
	public void registerReplace(List<SimpleObject> o) {
		if (!objects.contains(o)) {
			objects = o;
			object_map = new HashMap<Class<?>, Integer>();

			for (int i = 0; i < objects.size(); i++) {
				object_map.put(objects.get(i).getClass(), i);
			}
		}
	}

	/**
	 * Get the image used by the object corresponding to the key.
	 * 
	 * @param c
	 *            The key.
	 * @return The image.
	 */
	public Img previewKey(int c) {
		return objects.get(c).getImage();
	}

	/**
	 * Add a SimpleObject using either the default constructor or an empty
	 * String using the getClone method.
	 * 
	 * @param c
	 *            The key for the SimpleObject.
	 * @param x
	 *            The x coordinate for the object.
	 * @param y
	 *            The y coordinate for the object.
	 * @param m
	 *            The map the object should be added to.
	 * @return True iff the object was added.
	 */
	public boolean addSimpleObject(int c, int x, int y, SimpleMap m) {
		return addSimpleObject(c, x, y, "", m);
	}

	/**
	 * Gets the list of SimpleObjects currently stored.
	 * 
	 * @return The list of SimpleObjects
	 */
	public List<SimpleObject> getList() {
		return objects;
	}

	/**
	 * Gets the current key being used for the class of a SimpleObject stored in
	 * the SimpleWorldFactory.
	 * 
	 * @param o
	 *            The SimpleObject to search for in the factory.
	 * @return The internal key of the object o. Returns -1 if o is not stored.
	 */
	public int getKey(SimpleObject o) {
		if (object_map.containsKey(o.getClass())) {
			return object_map.get(o.getClass());
		}
		return -1;
	}

	/**
	 * Use this method to get a String containing all of the SimpleObjects
	 * contained in this factory. Can be used to create an instance of
	 * SimpleWorldFactory
	 * 
	 * @see #SimpleWorldFactory(String)
	 */
	@Override
	public String toString() {
		String s = "";
		for (SimpleObject o : objects) {
			s += o.getClass().getCanonicalName() + ";";
		}
		return s;
	}

	/**
	 * Add a SimpleObject using either the default constructor (if getClone
	 * returns null) or the getClone method with a String for customizing the
	 * SimpleObject.
	 * 
	 * @param c
	 *            The key for the SimpleObject.
	 * @param x
	 *            The x coordinate for the object.
	 * @param y
	 *            The y coordinate for the object.
	 * @param m
	 *            The map the object should be added to.
	 * @param s
	 *            The String passed into the getClone method.
	 * @return True iff the object was added.
	 */
	public boolean addSimpleObject(int c, int x, int y, String s, SimpleMap m) {
		boolean successful = false;
		SimpleObject n = objects.get(c);
		if (n == null) {
			return false;
		}
		SimpleObject o = n.getClone(s);
		if (o == null) {
			try {
				SimpleObject newO = n.getClass().newInstance();
				successful = m.addSimpleObject(newO, x, y);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		} else {
			successful = m.addSimpleObject(o, x, y);
		}

		return successful;
	}
}
