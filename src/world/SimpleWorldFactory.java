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

import java.util.HashMap;
import java.util.Map;

import sprite.Img;

public class SimpleWorldFactory {
	
	private static SimpleWorldFactory swf = new SimpleWorldFactory();
	Map <Integer, SimpleObject>objects = new HashMap<Integer, SimpleObject>();
	
	public static SimpleWorldFactory getInstance(){
		return swf;
	}
	
	public void register(SimpleObject o){
		if(!objects.containsKey(o.id())){
			objects.put(o.id(), o);
		}
	}
	
	public Integer[] getKeys(){
		return objects.keySet().toArray(new Integer[1]);
	}
	
	public Img previewKey(int c){
		return objects.get(c).getImage();
	}
	
	public boolean addSimpleObject(int c, int x, int y, SimpleMap m){
		return addSimpleObject(c, x, y, "", m);
	}
	
	public boolean addSimpleObject(int c, int x, int y, String s, SimpleMap m){
		boolean successful = false;
		SimpleObject n = objects.get(c);
		if(n == null){
			return false;
		}
		SimpleObject o = n.getClone(s);
		if(o == null){
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
