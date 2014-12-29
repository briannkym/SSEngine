package sprite;

public interface ImgCommand {
	
	/**
	 * Use this method to perform an arbitrary manipulation on an image.
	 * Override this method to access children of Img.
	 * 
	 * @param i The image to manipulate
	 */
	public void accept(Img i);
	public void accept(Anm a);
	
}
