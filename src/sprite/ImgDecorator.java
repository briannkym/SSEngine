package sprite;

abstract class ImgDecorator<I extends Img> extends Img
{
	private I img;
	
	
	public ImgDecorator(I image)
	{
		this.img = image;
	}
	
	//Assumes images are the same size, in the same coordinate system.
	public I addImage(I image)
	{
		int x = this.img.getWidth();
		int y = this.img.getHeight();
		int[] col;
		
		for (int i = 0; i < x; i++)
		{
			for (int j = 0; j < y; j++)
			{
				col = image.getPixel(i, j);
				if (checkForCol(col))  this.img.setPixel(i, j, col);
			}
		}
		
		return image;
	}
	
	
	public I getImage()
	{
		return (I) this.img;
	}

}