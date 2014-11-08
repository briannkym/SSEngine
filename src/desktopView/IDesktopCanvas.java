package desktopView;

import java.awt.image.BufferedImage;

import sprite.ImgCanvas;

public interface IDesktopCanvas extends ImgCanvas{
	public void drawImage(BufferedImage bi, int x, int y);
}
