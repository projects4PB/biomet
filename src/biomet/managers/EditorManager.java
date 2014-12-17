package biomet.managers;

import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.awt.image.Raster;
import java.awt.image.ShortLookupTable;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;

import biomet.models.FilterMask;
import biomet.models.HSVColor;
import biomet.models.ImageMask;
import biomet.models.InsetMask;
import biomet.models.MinutiaeArea;
import biomet.models.Point;
import biomet.panels.ImagePanel;
import biomet.panels.RootPanel;

/**
 *
 * @author Adrian Olszewski, Dariusz Obuchowski
 */
public class EditorManager
{
    private static volatile EditorManager instance = null;
	
	final private ImagePanel imagePanel = RootPanel.getImagePanel();
	
	private HashMap<Point, MinutiaeType> minutiaes = new HashMap<>();
	
	private ArrayList<MinutiaeArea> minutiaeAreas = new ArrayList<>();
	
	private enum MinutiaeType { ROZWIDLENIE, KONIEC };
	
    int[] thinningDeletionArray ={
			0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 
			1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 
			0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 
			1, 1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 
			1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 0, 1,
			0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 
			1, 1, 0, 1, 1, 1, 1, 1, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 
			0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
			0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1,
			0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 
			1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 
			1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1};

	int[] thinningWeightArray = {128, 1, 2, 64, 0, 4, 32, 16, 8};

    private EditorManager() {}
    
    public static EditorManager getInstance()
    {
        if(instance == null)
        {
            synchronized(EditorManager.class)
            {
                if(instance == null)
                {
                    instance = new EditorManager();
                }
            }
        }
        return instance;
    }

	public Image getImage()
	{
		return this.imagePanel.getImage();
	}

    public void loadImage(File file)
    {
		BufferedImage buffImage = null;
		try {
			buffImage = ImageIO.read(file);
		} catch(IOException ex)
		{
			ex.printStackTrace(System.err);
		}
		Image image = SwingFXUtils.toFXImage(buffImage, null);

		this.imagePanel.setOrginalImage(image);

		this.imagePanel.setWorkingImage(image);
	
		this.imagePanel.setImage(image);
    }
           
    public void updatePreviewImage(final Image image)
    {
    	Platform.runLater(new Runnable()
		{
            @Override
            public void run() {                    
            	imagePanel.updatePreviewImage(image);                                
            }
        });
    }

	public void restoreWorkingImage()
	{
    	Platform.runLater(new Runnable()
		{
            @Override
            public void run() {                    
            	imagePanel.restoreWorkingImage();                                
            }
        });
	}

	public void updateWorkingImage()
	{
        this.imagePanel.updateWorkingImage();                                
	}

	private int getPixelIntensityValue(int pixelValue)
	{
		int red = (pixelValue >> 16) & 0xff;
		int green = (pixelValue >> 8) & 0xff;
		int blue = (pixelValue) & 0xff;

		int value = (int)Math.round(
				.2126 * red + .7152 * green + .0722 * blue
		);
		return value;
	}

	public int[] getImageRegionPixels(int x, int y, int w, int h)
	{
		Image image = this.imagePanel.getWorkingImage();

		PixelReader pxlReader = image.getPixelReader();
		
		int[] pixelsBuffer = new int[w * h];

		pxlReader.getPixels(x, y, w, h,
				PixelFormat.getIntArgbInstance(), pixelsBuffer, 0, w);

		return pixelsBuffer;
	}

	private int[] getPixelsIntesityValues(int[] pixelsBuffer)
	{
		for(int i = 0; i < pixelsBuffer.length; i++)
		{
			pixelsBuffer[i] = this.getPixelIntensityValue(pixelsBuffer[i]);
		}
		return pixelsBuffer;
	}

	public void findMinutiae()
	{
		Image image = this.imagePanel.getWorkingImage();

        for(int readY = 0; readY < image.getHeight() - 9; readY++) 
        {
            for(int readX = 0; readX < image.getWidth() - 9; readX++) 
            {
            	int pixels3x3[] = this.getPixelsIntesityValues(
						this.getImageRegionPixels(readX + 3, readY + 3, 3, 3));
            	
            	int pixels5x5[] = this.getPixelsIntesityValues(
						this.getImageRegionPixels(readX + 2, readY + 2, 5, 5));
            	
				int pixels9x9[] = this.getPixelsIntesityValues(
						this.getImageRegionPixels(readX, readY, 9, 9));

				ImageMask imgMask3x3 = new ImageMask(3, pixels3x3);
				
				InsetMask insetMask5x5 = new InsetMask(5, pixels5x5);
				InsetMask insetMask9x9 = new InsetMask(9, pixels9x9);

				if(insetMask5x5.countPositivePixels() == 3
						&& insetMask9x9.countPositivePixels() == 3
						&& imgMask3x3.getMaskPixelsAverage() > 0.3
						&& imgMask3x3.getCenterPixelValue() == 1)
				{
					Point minutiaePoint = new Point(readX + 4, readY + 4);
					
					boolean containsMinutiae = false;
					
					for(MinutiaeArea minutiaeArea: this.minutiaeAreas)
					{
						System.out.println(minutiaeArea.containsPoint(minutiaePoint));
						
						if(minutiaeArea.containsPoint(minutiaePoint))
							containsMinutiae = true;
					}
					if(containsMinutiae) continue;
					
					this.minutiaes.put(minutiaePoint, MinutiaeType.ROZWIDLENIE);
					
					minutiaeAreas.add(new MinutiaeArea(minutiaePoint, 9));
				}
				else if(imgMask3x3.countPositivePixels() == 2
						&& imgMask3x3.getCenterPixelValue() == 1)
				{
						this.minutiaes.put(new Point(readX + 4, readY + 4),
								MinutiaeType.KONIEC);

				}
			}
		}
        System.out.println("ilosc: " + this.minutiaes.size());
        
        this.markMinutiaesOnImage();
	}
	
	private void markMinutiaesOnImage()
	{
		Image image = this.imagePanel.getWorkingImage();
		
		WritableImage wImage = new WritableImage(
	                (int)image.getWidth(),
	                (int)image.getHeight());
		
		PixelReader pxlReader = image.getPixelReader();
		
		PixelWriter pxlWriter = wImage.getPixelWriter();
		
		for(int readY = 0; readY < image.getHeight(); readY++) 
        {
            for(int readX = 0; readX < image.getWidth(); readX++) 
            {
            	int pixelValue = pxlReader.getArgb(readX, readY);
            	
            	pxlWriter.setArgb(readX, readY, pixelValue);
            }
        }

		for(Point p : this.minutiaes.keySet())
		{
			for(int i = -2; i <= 2; i++)
			{
				for(int j = -2; j <= 2; j++)
				{
					pxlWriter.setColor(p.getX() + i, p.getY() + j, Color.RED);
				}
			}
		}
		this.updatePreviewImage(wImage);
	}
    
	public HashMap<Integer, Integer> computeHistogramValues()
	{
		Image image = this.imagePanel.getWorkingImage();

		PixelReader pxlReader = image.getPixelReader();

		HashMap<Integer, Integer> results = new HashMap<>();

		int pixelValue = 0, valueCounter;

        for(int readY = 0; readY < image.getHeight(); readY++) 
        {
            for(int readX = 0; readX < image.getWidth(); readX++) 
            {
            	pixelValue = this.getPixelIntensityValue(
						pxlReader.getArgb(readX, readY)
				);
				if(results.containsKey(pixelValue))
				{
					valueCounter = results.get(pixelValue);

					results.put(pixelValue, ++valueCounter);
				}
				else results.put(pixelValue, 1);
			}
		}
		return results;
	}

	public void strechImageHistogram(Set<Integer> histPixels)
	{
		Image image = this.imagePanel.getWorkingImage();

    	BufferedImage srcImage = SwingFXUtils.fromFXImage(image, null);
    	
		BufferedImage dstImage = null;

		double histMin = (double) Collections.min(histPixels);
		double histMax = (double) Collections.max(histPixels);

		short[] values = new short[256];

		for(int pixelValue : histPixels)
		{
			values[pixelValue] = (short)(
					(255.0 / (histMax - histMin)) *
					((double)pixelValue - histMin)
			);
		}

		LookupTable lookupTable = new ShortLookupTable(0, values);
		
		LookupOp op = new LookupOp(lookupTable, null);
		
		dstImage = op.filter(srcImage, null);
    	
    	Image resultImage = SwingFXUtils.toFXImage(dstImage, null);

    	this.updatePreviewImage(resultImage);
	}

	public void equalizeImageHistogram(HashMap<Integer, Integer> histValues)
	{
		Image image = this.imagePanel.getWorkingImage();

    	BufferedImage srcImage = SwingFXUtils.fromFXImage(image, null);
    	
		BufferedImage dstImage = null;

		int pixelsCount = (int)(image.getWidth() * image.getHeight());

		HashMap<Integer, Double> D = new HashMap<>();

		int amount = 0;

		for(Map.Entry<Integer, Integer> entry : histValues.entrySet())
		{
			amount += entry.getValue();

			double dValue = (double)amount / (double)pixelsCount;

			D.put(entry.getKey(), dValue);
		}
		short[] values = new short[256];

		int firstKey = D.keySet().iterator().next();

        for(int pixelValue : histValues.keySet())
		{
			values[pixelValue] = (short)((
					(D.get(pixelValue) - D.get(firstKey)) / (1 - D.get(firstKey)))
				* (histValues.size() - 1));
		}
		LookupTable lookupTable = new ShortLookupTable(0, values);
		
		LookupOp op = new LookupOp(lookupTable, null);
		
		dstImage = op.filter(srcImage, null);
    	
    	Image resultImage = SwingFXUtils.toFXImage(dstImage, null);

    	this.updatePreviewImage(resultImage);
	}

    public void binarizeImage(int sliderValue)
    {
		int pixelCode = 0;
		
		Image image = this.imagePanel.getWorkingImage();
		
		WritableImage wImage = new WritableImage(
	                (int)image.getWidth(),
	                (int)image.getHeight());
		
		PixelReader pxlReader = image.getPixelReader();
		
		PixelWriter pxlWriter = wImage.getPixelWriter();	
		
        for(int readY = 0; readY < image.getHeight(); readY++) 
        {
            for(int readX = 0; readX < image.getWidth(); readX++) 
            {
            	pixelCode = this.getPixelIntensityValue(
						pxlReader.getArgb(readX, readY)
				);
            	if(pixelCode < sliderValue)
            	{
            		pxlWriter.setColor(readX, readY, Color.WHITE);
            	}
            	else
            	{
            		pxlWriter.setColor(readX, readY, Color.BLACK);
            	}
            }
        }
        this.updatePreviewImage(wImage);	
    }
    
    private int getIntFromColor(int red, int green, int blue)
    {
        red = (red << 16) & 0x00FF0000;
        green = (green << 8) & 0x0000FF00;
        blue = blue & 0x000000FF;

        return 0xFF000000 | red | green | blue;
    }
    
    public void filterMedian(FilterMask filterMask) 
    {
    	Image image = this.imagePanel.getWorkingImage();

    	PixelReader pxlReader = image.getPixelReader();
    	
		WritableImage wImage = new WritableImage(
                (int)image.getWidth(),
                (int)image.getHeight());
		
		PixelWriter pxlWriter = wImage.getPixelWriter();

        int halfMask = filterMask.getSizeMask() / 2;

        for (int x = halfMask; x < image.getWidth() - halfMask + 1; x++)
		{
			for (int y = halfMask; y < image.getHeight() - halfMask + 1; y++)
			{
					List<Integer> data = this.getWindowData(x, y, halfMask);
					pxlWriter.setArgb(x, y, getMedianOfRGB(data));
			}
        }
        this.updatePreviewImage(wImage);
    }
    
    private List<Integer> getWindowData(int x, int y, int hm) 
    {
    	Image image = this.imagePanel.getWorkingImage();

    	PixelReader pxlReader = image.getPixelReader();
    	
        List<Integer> result = new LinkedList();
        
        for (int i = x - hm; i < image.getWidth() && i <= x + hm; i++)
		{
                for (int j = y - hm; j < image.getHeight() && j <= y + hm; j++)
				{
                        result.add(pxlReader.getArgb(i, j));
                }
        }
        return result;
    }
    
    private int getMedianOfRGB(List<Integer> data) 
    {
      	Image image = this.imagePanel.getWorkingImage();
      	
    	PixelReader pxlReader = image.getPixelReader();
    	
        List<Integer> r = new ArrayList(data.size());
        List<Integer> g = new ArrayList(data.size());
        List<Integer> b = new ArrayList(data.size());
        
        for (Integer v : data) 
        {
        	java.awt.Color color = new java.awt.Color(v);
            r.add(color.getRed());
            g.add(color.getGreen());
            b.add(color.getBlue());
        }
        
        Collections.sort(r);
        Collections.sort(g);
        Collections.sort(b);

        List<Integer> result = new ArrayList(3);
        int half = data.size() / 2;
        result.add(r.get(half));
        result.add(g.get(half));
        result.add(b.get(half));
        int rgbColorInt = this.getIntFromColor(
				r.get(half), g.get(half), b.get(half)
		);

        return rgbColorInt;
	}

	public void saveHistogram(File file)
	{
        if(file != null)
		{
			HashMap<Integer, Integer> histogramValues =
				this.computeHistogramValues();

			try {
				FileWriter fileWriter = null;
             
				fileWriter = new FileWriter(file);

				for(Map.Entry<Integer, Integer> entry : histogramValues.entrySet())
				{
					int pixelValue = entry.getKey();
					int valueCount = entry.getValue();

					fileWriter.write(pixelValue + ": " + valueCount + "\n");
				}
				fileWriter.close();
			} catch (IOException ex) {
				ex.printStackTrace(System.err);
			}
        }
	}

    public void saveImage(File outFile)
    {
    	Image image = this.imagePanel.getImage();
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", outFile);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public void convolveImage(FilterMask filterMask)
    {
    	Image img = imagePanel.getWorkingImage();
    	
    	BufferedImage srcImg = SwingFXUtils.fromFXImage(img, null);
    	
    	BufferedImage dstImg  = srcImg;
    	
    	float [][] maskData = filterMask.getAllMask();
    	
    	float [] data = new float [9];
    	
    	int k = 0 ;
    	
        for (int i=0; i<3; i++) 
        {
        	for (int j=0; j<3; j++) 
            {
        		data[k] = maskData[i][j];
            	k++;
            }
        }
          
    	Kernel kernel = new Kernel(3, 3, data); 
    	ConvolveOp convolve = new ConvolveOp(kernel); 
    	BufferedImage temp = convolve.filter(srcImg, null);
    	Image dstImage = SwingFXUtils.toFXImage(temp, null);
    	this.updatePreviewImage(dstImage);
    }
            
    public HSVColor getHSVImage(int r, int g, int b)
    {
    	HSVColor hsvColor ;
    	
    	double rRGB = (double)r/255;
    	double gRGB = (double)g/255;
    	double bRGB = (double)b/255;
    	
    	double minRGB = Math.min(rRGB,Math.min(gRGB,bRGB));
    	double maxRGB = Math.max(rRGB,Math.max(gRGB,bRGB));
    	double computedH, computedS, computedV; 
    	
    	if (minRGB==maxRGB) 
    	{
    		hsvColor = new HSVColor(0,0,minRGB);
    		return hsvColor;
    	}
    	
    	else
    	{
    	 	double d = (rRGB==minRGB) ? gRGB-bRGB : ((bRGB==minRGB) ? rRGB-gRGB : bRGB-rRGB);
    	 	
        	double h = (rRGB==minRGB) ? 3 : ((bRGB==minRGB) ? 1 : 5);
        	
        	computedH = 60*(h - d/(maxRGB - minRGB));
        	computedS = (maxRGB - minRGB)/maxRGB;
        	computedV = maxRGB;
        	
        	hsvColor = new HSVColor(computedH, computedS, computedV );	
    	}

    	return 	hsvColor;
    }
    
    public WritableImage decompositionHSVImage(String TypeOperation)
    {
    	Image image = this.imagePanel.getWorkingImage();
    	
    	PixelReader pxlReader = image.getPixelReader();
    	
    	HSVColor hsvColor;
    	
		WritableImage wImage = new WritableImage(
                (int)image.getWidth(),
                (int)image.getHeight());
		
		PixelWriter pxlWriter = wImage.getPixelWriter();
		
        for (int i = 0 ; i < image.getWidth() ; i++)
  		{
        	for (int j = 0; j < image.getHeight() ; j++)
        	{
                Color color = pxlReader.getColor(i, j);
                
                double opacity = color.getOpacity();
                
        		int pixel = pxlReader.getArgb(i, j);
             
                int  red = (pixel & 0x00ff0000) >> 16;
        	
        		int  green = (pixel & 0x0000ff00) >> 8;
        	
        		int  blue = pixel & 0x000000ff;
        		
        		hsvColor = this.getHSVImage(red, green, blue);
        		
        		/*
        		System.out.println(red + " " + green + " " + blue 
        				+ " " + hsvColor.getH() + " " + 
        				hsvColor.getS() + " " + hsvColor.getV());
        		*/
        		        		
                if(TypeOperation.equals("H"))
                {
                	pxlWriter.setColor(i, j, new Color(hsvColor.getH() / 360, 
                			hsvColor.getH() /360, hsvColor.getH() /360, opacity));
                	
                }
                
                else if(TypeOperation.equals("S"))
                {
                	pxlWriter.setColor(i, j, new Color(hsvColor.getS(), 
                			hsvColor.getS(), hsvColor.getS(), opacity));
                }
                
                else
                {
                	pxlWriter.setColor(i, j, new Color(hsvColor.getV(), 
                			hsvColor.getV(), hsvColor.getV(), opacity));
                } 
            }
        }
        
        //this.updateImage(wImage);
        
        return wImage;
    }
    
    
    public void setThinningImage()
    {
    	Image orgImage = this.imagePanel.getWorkingImage();
    	
    	BufferedImage buffOrgImage = SwingFXUtils.fromFXImage(orgImage, null);
    	
    	BufferedImage thinnkingImage = this.thinning(buffOrgImage);
    	
    	Image resultImage = SwingFXUtils.toFXImage(thinnkingImage, null);
    	
    	this.imagePanel.setWorkingImage(resultImage);
    	
        this.updatePreviewImage(resultImage);
    }
    
    private  BufferedImage thinning(BufferedImage img)
    {
	    	final Raster oldimg = img.copyData(null);
	    	final WritableRaster newimg = img.getRaster();
	    	int[][] table = new int[img.getHeight()][img.getWidth()];
	    	int[] blackpix = {0, 0, 0, 0};
	    	int[] whitepix = {255, 255, 255, 255};
	    	int[] pix = new int[4];
	    	for(int i=0;i<oldimg.getHeight();i++)
	    	{
	    		for(int j=0;j<oldimg.getWidth();j++)
	    		{
	    			oldimg.getPixel(j, i,pix);
					if (pix[0] == 0) {
						table[i][j]=1;
					}
				}
	    	}
	    	
	    	thinningAlgorithm(table);
	    	for(int i=0;i<oldimg.getHeight();i++)
	    	{
	    		for(int j=0;j<oldimg.getWidth();j++)
	    		{
	    			try {
	    				if(table[i][j]>0)
	        			{
	    					newimg.setPixel(j, i, blackpix);
	        			}
	    				else
	    				{
	    					newimg.setPixel(j, i, whitepix);
	    				}
					} catch (Exception e) {
						e.printStackTrace();
					}
	    		}
	    	}
	    	
	    	final BufferedImage i = new BufferedImage(img.getColorModel(), newimg, false, null);
	        return i;
	    }
	    
	
		private void thinningAlgorithm(int[][] table) {
			for (int i = 0; i < table.length; i++) {
				table[i][0] = 0;
				table[i][table[0].length - 1] = 0;
			}
			for (int i = 0; i < table[0].length; i++) {
				table[0][i] = 0;
				table[table.length - 1][i] = 0;
			}
			boolean change=true;
			while (change) {
				change=false;
				for (int h = 1; h < table.length - 1; h++) {
					for (int w = 1; w < table[0].length; w++) {
						if (table[h][w] > 0) {
							if (table[h - 1][w] == 0 || table[h + 1][w] == 0
									|| table[h][w - 1] == 0 || table[h][w + 1] == 0)
								table[h][w] = 2;
							else if (table[h - 1][w - 1] == 0
									|| table[h - 1][w + 1] == 0
									|| table[h + 1][w - 1] == 0
									|| table[h + 1][w + 1] == 0)
								table[h][w] = 3;
						}
					}
				}
				for (int h = 1; h < table.length - 1; h++) {
					for (int w = 1; w < table[0].length; w++) {
						if (table[h][w] > 0) {
							thinnerCheckNeighbours(h, w, table);
							if (table[h][w] == 4) {
								thinningCheckPositionDelete(h, w, table);
								if(table[h][w]!=4)
									change=true;
							}
						}
					}
				}
				if(change==false)
				{
					for (int h = 1; h < table.length - 1; h++) {
						for (int w = 1; w < table[0].length; w++) {
							if (table[h][w] > 0) {
								thinnerCheckNeighbours(h, w, table);
								if (table[h][w] == 2) {
									thinningCheckPositionDelete(h, w, table);
									if(table[h][w]!=2)
										change=true;
								}
							}
						}
					}
				}
				if(change==false)
				{
					for (int h = 1; h < table.length - 1; h++) {
						for (int w = 1; w < table[0].length; w++) {
							if (table[h][w] > 0) {
								thinnerCheckNeighbours(h, w, table);
								if (table[h][w] == 3) {
									thinningCheckPositionDelete(h, w, table);
									if(table[h][w]!=3)
										change=true;
								}
							}
						}
					}
				}
			}
		}
		
		private void thinnerCheckNeighbours(int h, int w, int[][] table) {
			int sum=-1;
			for(int i=h-1;i<h+2;i++)
			{
				for(int j=w-1;j<w+2;j++)
				{
					if(table[i][j]>0)
						sum++;
				}
			}
			if(sum>1 && sum<5)
				table[h][w]=4;
		}
	
		private void thinningCheckPositionDelete(int h, int w, int[][] table) {
			int sum=0;
			if(table[h-1][w-1]>0)
				sum+=thinningWeightArray[0];
			if(table[h-1][w]>0)
				sum+=thinningWeightArray[1];
			if(table[h-1][w+1]>0)
				sum+=thinningWeightArray[2];
			if(table[h][w-1]>0)
				sum+=thinningWeightArray[3];
			if(table[h][w+1]>0)
				sum+=thinningWeightArray[5];
			if(table[h+1][w-1]>0)
				sum+=thinningWeightArray[6];
			if (table[h + 1][w] > 0)
				sum += thinningWeightArray[7];
			if (table[h + 1][w + 1] > 0)
				sum += thinningWeightArray[8];
	
			if (thinningDeletionArray[sum] == 1)
				table[h][w]=0;
		}
}
