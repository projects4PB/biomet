package biomet.panels;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImagePanel extends ImageView 
{
	private Image orginalImage;

	private Image workingImage;

	private Image previewImage;
	
	public void setOrginalImage(Image img)
	{
		this.orginalImage = img;
	}

	public void setWorkingImage(Image img)
	{
		this.workingImage = img;
	}
	
	public Image getWorkingImage()
	{
		return workingImage;
	}

	public void restoreOrginalImage()
	{
		this.setImage(this.orginalImage);
	}

	public void restoreWorkingImage()
	{
		this.setImage(this.workingImage);
	}

	public void updateWorkingImage()
	{
		this.workingImage = this.previewImage;
	}

	public void updatePreviewImage(Image image)
	{
		this.previewImage = image;

		this.setImage(image);
	}
}
