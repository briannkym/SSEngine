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

import java.awt.image.BufferedImage;

/**
 * Stores an animation that updates upon returning a slide.
 * 
 * @author Brian Nakayama
 */
public class Animation implements Img{

    private BufferedImage[] bI;
    private int i=0;
    private ImgListener iL = NullListener.getInstance();

    /**
     * Initializes the animation with an array of bufferedImages.
     * @param bI
     */
    public Animation(BufferedImage[] bI)
    {
        this.bI = bI;
    }

    /**
     * Returns an image and increments the index of the array.
     */
    public BufferedImage getSlide()
    {
        BufferedImage rB=bI[i];

        if(i<bI.length-1)
        {
            i++;
        }
        else
        {
            iL.slideEnd();
            i=0;
        }
        
        return rB;
    }

    /**
     * Set the index to be displayed.
     */
    public void setSlide(int i)
    {
        this.i = i;
    }

    /**
     * Set an Image Listener.
     * @see ImgListener
     */
    public void setListener(ImgListener iL)
    {
        this.iL=iL;
    }

}
