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

package sprite;

/**
 * Implements many methods that only make sense for an animation.
 * @author Brian Nakayama
 * @version 1.2
 */
public abstract class Anm extends Img{
	
    /**
	 * Sets the internal index of the image.
	 * Only supported in animations.
	 */
    public abstract void setSlide(int i);
    
    /**
     * Get the slide at index i
     * @param i The index of the slide.
     * @return A new image containing the slide.
     */
    public abstract Img getSlide(int i);
    
    /**
     * Get a clone with the specific image slides of this one.
     * @param i The indexes of the slides for the clone
     * @return A new animation clone with only a subset of slides.
     */
    public abstract Anm getClone(int... i);
    
    /**
     * Return a copy object of this one.
     * @return An animation clone.
     */
    public abstract Anm getClone();
    
    /**
     * Set whether or not to treat the image as an animation.
     * @param animate True to animate
     */
    public abstract void animate(boolean animate);

    /**
     * A version of the command pattern. Since animations contain extra code,
     * allow for an accept method to take an animation.
     * @param iC
     */
    @Override
    public <T extends ImgCommand> void accept(T iC){
    	iC.accept(this);
    }
}
