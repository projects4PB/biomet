package biomet.models;

public class HSVColor 
{
	private double h,s,v;
	
	public HSVColor(double h, double s, double v)
	{
		this.h = h;
		this.s = s;
		this.v = v;
	}
	
	public double getH()
	{
		return this.h;
	}
	
	public double getS()
	{
		return this.s;
	}
	
	public double getV()
	{
		return this.v;
	}
	
	public void setH(double h)
	{
		this.h = h;
	}
	
	public void setS(double s)
	{
		this.s = s;
	}
	
	public void setV(double v)
	{
		this.v = v;
	}

}
