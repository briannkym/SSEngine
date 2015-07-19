package sprite;

/**
 * For passing commands to images.
 * @author brian
 *
 */
public interface ImgCommand {
	
	/**
	 * Use this method to perform an arbitrary manipulation on an image.
	 * Override this method to access children of Img.
	 * 
	 * @param i The image to manipulate
	 */
	public void accept(Img i);
	
	/**
	 * Use this method to perform an arbitrary manipulation on an animation.
	 * Override this method to access children of Anm.
	 * 
	 * @param a The animation to manipulate
	 */
	public void accept(Anm a);
	
}
