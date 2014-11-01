package control;

import sprite.ImgCanvas;
import sprite.ImgUpload;

public interface DeviceControl {
	public boolean setCanvas(ImgCanvas canvas);
	public ImgCanvas getCanvas();
	public ImgUpload getImgUpload();
}
