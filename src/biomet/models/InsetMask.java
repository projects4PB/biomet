package biomet.models;

import java.util.ArrayList;

public class InsetMask extends ImageMask
{
	private ArrayList<Integer> insetBinaryMask = new ArrayList<>();

	public InsetMask(int size, int[] regionPixels)
	{
		super(size, regionPixels);

		this.buildRidgeBinaryMask();
	}

	private void buildRidgeBinaryMask()
	{
		int binaryMaskSize = this.maskSize * this.maskSize;

		for(int i = 0; i < this.maskSize; i++)
		{
			this.insetBinaryMask.add(this.binaryMask[i]);
		}

		for(int i = (this.maskSize - 1) + this.maskSize; i < binaryMaskSize; i += this.maskSize)
		{
			this.insetBinaryMask.add(this.binaryMask[i]);
		}

		for(int i = binaryMaskSize - 2; i >= binaryMaskSize - this.maskSize; i--)
		{
			this.insetBinaryMask.add(this.binaryMask[i]);
		}

		for(int i = (binaryMaskSize - this.maskSize) - this.maskSize; i > 0; i -= this.maskSize)
		{
			this.insetBinaryMask.add(this.binaryMask[i]);
		}
	}

	@Override
	public double getMaskPixelsAverage()
	{
		double pixelsAmount = 0;

		for(int maskValue : this.insetBinaryMask)
		{
			pixelsAmount += maskValue;
		}
		return pixelsAmount / (double)this.insetBinaryMask.size();
	}
	
	@Override
	public int countPositivePixels()
	{
		int pixelCounter = 0;
		
		for(int maskValue : this.insetBinaryMask)
		{
			if(maskValue == 1) pixelCounter++;
		}
		return pixelCounter;
	}

	public void printInsetMask()
	{
		for(int maskValue : this.insetBinaryMask)
		{
			System.out.println(maskValue);
		}
	}
}
