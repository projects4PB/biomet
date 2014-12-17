package biomet.models;

public class FilterMask 
{
	private float[][] mask;
	
	public FilterMask(int r )
	{
		mask = new float [r] [r];
	}
	
	public float getMaskValue(int x, int y)
	{
		return this.mask[x][y];
	}
	
	public void setMaskValue(int x, int y, float v)
	{
		this.mask[x][y] = v;
	}
	
	public int getSizeMask()
	{
		return mask.length;
	}
	
	public void setMaskSize(int s)
	{
		mask = new float [s] [s];
	}
	
	public float[][] getAllMask()
	{
		return mask;
	}
	
	public void setNewMask(float [] [] newMask)
	{
		this.mask = newMask;
	}
}
