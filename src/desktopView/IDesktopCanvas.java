package desktopView;

import java.awt.image.BufferedImage;

import sprite.ImgCanvas;

/**
 * The interface for all desktop canvases.
 * @author Brian Nakayama
 *
 */
public interface IDesktopCanvas extends ImgCanvas{
	/**
	 * Draw an image at a specified coordinate
	 * @param bi the image to be drawn.
	 * @param x the x-value in pixels for the coordinate.
	 * @param y the y-value in pixels for the coordinate.
	 */
	public void drawImage(BufferedImage bi, int x, int y);
}
