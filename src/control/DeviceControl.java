package control;

import sprite.ImgCanvas;
import sprite.Img;

public interface DeviceControl {
	public boolean setCanvas(ImgCanvas canvas);
	public ImgCanvas getCanvas();
	public Img getImg(String s);
}
