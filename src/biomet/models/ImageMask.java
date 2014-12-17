package biomet.models;

public class ImageMask
{
	protected int maskSize;

	protected int[] maskPixels;

	protected int[] binaryMask;

	public ImageMask(int size, int[] regionPixels)
	{
		this.maskSize = size;
		this.maskPixels = regionPixels;

		this.buildBinaryMask();
	}
	
	public int getMaskSize()
	{
		return this.maskSize;
	}

	private void buildBinaryMask()
	{
		this.binaryMask = new int[this.maskSize * this.maskSize];

		for(int i = 0; i < this.maskPixels.length; i++)
		{
			if(this.maskPixels[i] == 0)
				this.binaryMask[i] = 1;
			else binaryMask[i] = 0;
		}
	}

	public double getMaskPixelsAverage()
	{
		double pixelsAmount = 0;

		for(int i = 0; i < this.binaryMask.length; i++)
		{
			pixelsAmount += this.binaryMask[i];
		}
		return pixelsAmount / (double)this.binaryMask.length;
	}
	
	public int countPositivePixels()
	{
		int pixelCounter = 0;
		
		for(int maskValue : this.binaryMask)
		{
			if(maskValue == 1) pixelCounter++;
		}
		return pixelCounter;
	}
	
	public int getCenterPixelValue()
	{
		if(this.binaryMask.length % 2 == 0)
		{
			System.err.println("Center pixel: Mask size error");
		}
		return this.binaryMask[this.binaryMask.length / 2];
	}
}
