package biomet.models;

public class MinutiaeArea
{
	private Point point;
	
	private int size;
	
	public MinutiaeArea(Point point, int size)
	{
		this.point = point;
		this.size = size;
	}
	
	public boolean containsPoint(Point point)
	{
		int maskDiff = this.size / 2;
		
		if(point.getX() <= this.point.getX() + maskDiff
				&& point.getX() >= this.point.getX() - maskDiff
				&& point.getY() <= this.point.getY() + maskDiff
				&& point.getY() >= this.point.getY() - maskDiff)
		{
			return true;
		}
		return false;
	}
}
